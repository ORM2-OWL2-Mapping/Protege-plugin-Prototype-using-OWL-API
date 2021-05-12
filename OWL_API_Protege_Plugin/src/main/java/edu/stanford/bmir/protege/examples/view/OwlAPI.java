package edu.stanford.bmir.protege.examples.view;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Отвечает за правую панель вкладки
 */
public class OwlAPI {

    private static final Logger log = LoggerFactory.getLogger(OwlAPI.class);

    public static OWLDataFactory df = OWLManager.getOWLDataFactory();
    public static OWLOntologyManager manager;
    public static OWLOntology ontology;
    public static String ontology_iri;
    public static OWLEntityRemover entityRemover;
    public static OWLEntityRenamer owlEntityRenamer;


    public OwlAPI (OWLOntologyManager manager) {
        this.manager = manager;
        this.ontology = this.manager.getOntologies().iterator().next(); // Получаем текущую онтологию
        this.ontology_iri = this.ontology.getOntologyID().getOntologyIRI().get().toString() + '/';
        this.entityRemover = new OWLEntityRemover(Collections.singleton(this.ontology)); // Создаём специальный "удалитель"
        this.owlEntityRenamer = new OWLEntityRenamer(this.manager, this.manager.getOntologies()); // Создаём сменщика имён
    }

    public OwlAPI (String iri) throws Exception {
        this.manager = OWLManager.createOWLOntologyManager();
        this.ontology_iri = iri + '/';
        this.ontology = this.manager.createOntology(IRI.create(this.ontology_iri));
        this.entityRemover = new OWLEntityRemover(Collections.singleton(this.ontology));
        this.owlEntityRenamer = new OWLEntityRenamer(this.manager, this.manager.getOntologies()); // Создаём сменщика имён
    }

    /**
     * Для дебага
     */
    private static void debugSave() throws Exception {
        File fileformated = new File("example.owl");
        manager.saveOntology(ontology, new OWLXMLDocumentFormat(), IRI.create(fileformated.toURI()));
    }

    /**
     * Удаляет OWL-элемент из онтологии
     * @param owlEntity - удаляемый элемент
     */
    private void removeOWLEntity(OWLEntity owlEntity) {
        owlEntity.accept(entityRemover);
        manager.applyChanges(entityRemover.getChanges());
        entityRemover.reset();
    }

    /**
     * Возвращает множество OWL-классов, которые являются прямыми детьми
     * @return множество OWL-классов
     */
    private Set<OWLClass> getOWLThingSubClasses() {

        Set<OWLClass> subClasses = new HashSet<OWLClass>();
        Set<OWLClass> ontology_classes = ontology.getClassesInSignature(); // Получаем все OWL-классы в онтологии

        // Удаляем из множества сам OWLThing и Universe-класс
        ontology_classes.remove(df.getOWLThing());
        ontology_classes.remove(df.getOWLClass(IRI.create(ontology_iri + "Universe")));

        // Каждый класс, который НЕ имеет родителей, является прямым ребёнком OWLThing
        for (OWLClass owlClass : ontology_classes) {
            if (ontology.getSubClassAxiomsForSubClass(owlClass).size() == 0) {
                subClasses.add(owlClass);
            }
        }

        return subClasses;

    }

    /**
     * Возвращает множество OWL-классов, которые являются прямыми детьми указанного предка
     * @param classParent - предок
     * @return множество OWL-классов
     */
    private Set<OWLClass> getSubClasses(OWLClass classParent) {

        Set<OWLClass> subClasses = new HashSet<OWLClass>();

        // Для каждой subclass-аксиомы
        for (OWLSubClassOfAxiom subClassAxiom : ontology.getAxioms(AxiomType.SUBCLASS_OF)) {
            // Если аксиома включает наш класс-предок, то добавляем класс-ребёнок из данной аксиомы в множество
            if (subClassAxiom.getSuperClass().asOWLClass().equals(classParent)) {
                subClasses.add(subClassAxiom.getSubClass().asOWLClass());
            }
        }
        //System.out.println(subClasses.toString());
        return subClasses;

    }

    /**
     * Обновление universe-класса и disjoint'ов между классами
     */
    private void updateCloseWorld() {

        // Получаем все 1-го уровня (прямые дети OWLThing) классы онтологии
        Set<OWLClass> ontology_classes = getOWLThingSubClasses();

        // Если в онтологии есть больше 1 класса, то удаляем текущий disjoint у классов
        if (ontology_classes.size() > 1) {
            boolean disjointIsRemove = false;
            for (OWLClass owlClass : ontology_classes) {
                for (OWLClassAxiom axiom : ontology.getAxioms(owlClass)) {
                    if (axiom.getAxiomType().toString().equals("DisjointClasses")) {
                        manager.removeAxiom(ontology, axiom);
                        disjointIsRemove = true;
                        break;
                    }
                }
                if (disjointIsRemove) {
                    break;
                }
            }
        }

        // Удаляем эквивалентность между OWLThing и классами 1-го уровня
        for (OWLClassAxiom axiom : ontology.getAxioms(df.getOWLThing())) {
            if (axiom.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                manager.removeAxiom(ontology, axiom);
                break;
            }
        }

        // Объявляем новые disjoint'ы между классами 1-уровня
        if (ontology_classes.size() > 1) {
            OWLDisjointClassesAxiom disjointClassesAxiom = df.getOWLDisjointClassesAxiom(ontology_classes);
            manager.addAxiom(ontology, disjointClassesAxiom);
        }

        // Объявляем, что OWL-Thing эквивалентен дизъюнкции (логическому ИЛИ) существующих классов
        OWLClassExpression universeClassExp = df.getOWLObjectUnionOf(ontology_classes);
        OWLEquivalentClassesAxiom universeClassAxiom = df.getOWLEquivalentClassesAxiom(df.getOWLThing(), universeClassExp);
        manager.addAxiom(ontology, universeClassAxiom);

    }



    /**
     * Сохранение онтологии в файл
     * @param filename - название файла, в который будем сохранять онтологию
     */
    public void saveOntologyInFile(String filename) throws Exception {

        File fileformated = new File(filename);
        manager.saveOntology(ontology, new OWLXMLDocumentFormat(), IRI.create(fileformated.toURI()));

    }

    /**
    * Объявление Entity Type (OWL-класса)
    * @param class_name - имя объявляемого класса
    */
    public void declareEntityType(String class_name)  {

        // Объявление нового OWL-класса
        OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + class_name));
        OWLDeclarationAxiom classDeclarationAx = df.getOWLDeclarationAxiom(owl_class);
        manager.addAxiom(ontology, classDeclarationAx);

        updateCloseWorld();
    }

    /**
    * Удаление Entity Type (OWL-класса)
    * @param class_name - имя объявляемого класса
    */
    public void removeEntityType(String class_name) {

        removeOWLEntity(df.getOWLClass(IRI.create(ontology_iri + class_name))); // Удаление OWL-класса

        updateCloseWorld();

    }

    /**
     * Объявление Subtype
     * @param child_class_name - имя EntityType, который является дочерним
     * @param parent_class_name - имя EntityType, который является родителем
     */
    public void declareSubtype(String child_class_name, String parent_class_name) {

        // Объявляем subtype-аксиому между классами
        OWLClass child_owl_class = df.getOWLClass(IRI.create(ontology_iri + child_class_name));
        OWLClass parent_owl_class = df.getOWLClass(IRI.create(ontology_iri + parent_class_name));
        OWLSubClassOfAxiom subClassAxiom = df.getOWLSubClassOfAxiom(child_owl_class, parent_owl_class);
        manager.addAxiom(ontology, subClassAxiom);

        updateCloseWorld();

    }

    /**
     * Удаление Subtype
     * @param child_class_name - имя EntityType, который является дочерним
     * @param parent_class_name - имя EntityType, который является родителем
     */
    public void removeSubtype(String child_class_name, String parent_class_name)  {

        OWLClass child_owl_class = df.getOWLClass(IRI.create(ontology_iri + child_class_name));
        OWLClass parent_owl_class = df.getOWLClass(IRI.create(ontology_iri + parent_class_name));
        for (OWLAxiom axiom : ontology.getAxioms(child_owl_class)) {
            if (axiom.getAxiomType().toString().equals("SubClassOf")) {
                for (OWLClass owlClass : axiom.getClassesInSignature()) {
                    if (owlClass.equals(parent_owl_class)) {
                        manager.removeAxiom(ontology, axiom);
                        updateCloseWorld();
                        return;
                    }
                }
            }
        }

    }

    /**
     * Объявление ValueType
     * @param value_name - имя DataProperty
     * @param datatype - тип значения
     * @param class_name - имя класса, который связан с dataProperty
     */
    public void declareValueType(String value_name, String datatype, String class_name)  {

        // Объявляем DataProperty
        OWLDataProperty valueType = df.getOWLDataProperty(IRI.create(ontology_iri + value_name + '.' + class_name));
        OWLDeclarationAxiom valueTypeDecl = df.getOWLDeclarationAxiom(valueType);
        manager.addAxiom(ontology, valueTypeDecl);

        // Добавляем класс в domains
        OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + class_name));
        OWLDataPropertyDomainAxiom domainAxiom = df.getOWLDataPropertyDomainAxiom(valueType, owl_class);
        manager.addAxiom(ontology, domainAxiom);

        // Добавляем тип в ranges
        OWLDatatype stringDatatype = df.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI());
        OWLDataPropertyRangeAxiom rangeAxiom = df.getOWLDataPropertyRangeAxiom(valueType, stringDatatype);
        manager.addAxiom(ontology, rangeAxiom);

    }

    /**
     * Обновление существующего ValueType
     * @param value_name - имя существующего DataProperty
     * @param new_value_name - новое имя
     * @param new_datatype - новый тип значения
     * @param new_class_name - имя класса, с которым теперь будет связан DataProperty
     */
    public void updateValueType(String value_name, String new_value_name, String new_datatype, String new_class_name) {

        OWLDataProperty valueType = df.getOWLDataProperty(IRI.create(ontology_iri + value_name));

        // Если текущее название ValueType не совпадает с новым
        if (!value_name.split("\\.")[0].equals(new_value_name)) {

            // Меняем название
            List<OWLOntologyChange> changes = owlEntityRenamer.changeIRI(IRI.create(ontology_iri + value_name), IRI.create(ontology_iri + new_value_name + '.' + new_class_name));
            manager.applyChanges(changes);
            valueType = df.getOWLDataProperty(IRI.create(ontology_iri + new_value_name + '.' + new_class_name));
        }

        for (OWLAxiom axiom : ontology.getAxioms(valueType)) {

            // Если текущая аксиома описывает domain и текущий класс не совпадает с новый классом
            if (axiom.getAxiomType() == AxiomType.DATA_PROPERTY_DOMAIN &&
                    !axiom.getClassesInSignature().iterator().next().getIRI().getShortForm().equals(new_class_name)) {

                // Удаляем текущий domain
                manager.removeAxiom(ontology, axiom);

                // Добавляем новый класс в domains
                OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + new_class_name));
                OWLDataPropertyDomainAxiom domainAxiom = df.getOWLDataPropertyDomainAxiom(valueType, owl_class);
                manager.addAxiom(ontology, domainAxiom);
            }
            else if (axiom.getAxiomType() == AxiomType.DATA_PROPERTY_RANGE) {

                // Удаляем текущий range
                manager.removeAxiom(ontology, axiom);

                // Добавляем новый тип в ranges
                OWLDatatype stringDatatype = df.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI());
                OWLDataPropertyRangeAxiom rangeAxiom = df.getOWLDataPropertyRangeAxiom(valueType, stringDatatype);
                manager.addAxiom(ontology, rangeAxiom);
            }
        }

    }

    /**
     * Удаление ValueType
     * @param value_name - имя DataProperty
     */
    public void removeValueType(String value_name)  {

        OWLDataProperty valueType = df.getOWLDataProperty(IRI.create(ontology_iri + value_name));
        removeOWLEntity(valueType);

    }

    /**
    * Объявление Unary Role
    * @param role_name - имя унарной роли
    * @param class_name - имя класса, который играет роль
    */
    public void declareUnaryRole(String role_name, String class_name)  {

        // Объявляем DataProperty
        OWLDataProperty unaryRole = df.getOWLDataProperty(IRI.create(ontology_iri + role_name + '.' + class_name));
        OWLDeclarationAxiom unaryRoleDecl = df.getOWLDeclarationAxiom(unaryRole);
        manager.addAxiom(ontology, unaryRoleDecl);

        // Добавляем domains
        OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + class_name));
        OWLDataPropertyDomainAxiom domainAxiom = df.getOWLDataPropertyDomainAxiom(unaryRole, owl_class);
        manager.addAxiom(ontology, domainAxiom);

        // Добавляем ranges
        OWLDatatype booleanDatatype = df.getBooleanOWLDatatype();
        OWLDataPropertyRangeAxiom rangeAxiom = df.getOWLDataPropertyRangeAxiom(unaryRole, booleanDatatype);
        manager.addAxiom(ontology, rangeAxiom);

    }

    /**
     * Обновление существующего UnaryRole
     * @param role_name - имя существующей DataProperty
     * @param new_role_name - новое имя DataProperty
     * @param new_class_name - имя класса, с которым теперь будет связан DataProperty
     */
    public void updateUnaryRole(String role_name, String new_role_name, String new_class_name) {

        OWLDataProperty unaryRole = df.getOWLDataProperty(IRI.create(ontology_iri + role_name));

        // Если текущее название UnaryRole не совпадает с новым
        if (!role_name.split("\\.")[0].equals(new_role_name)) {

            // Меняем название
            List<OWLOntologyChange> changes = owlEntityRenamer.changeIRI(IRI.create(ontology_iri + role_name), IRI.create(ontology_iri + new_role_name + '.' + new_class_name));
            manager.applyChanges(changes);
            unaryRole = df.getOWLDataProperty(IRI.create(ontology_iri + new_role_name + '.' + new_class_name));
        }

        for (OWLAxiom axiom : ontology.getAxioms(unaryRole)) {

            // Если текущая аксиома описывает domain и текущий класс не совпадает с новый классом
            if (axiom.getAxiomType() == AxiomType.DATA_PROPERTY_DOMAIN &&
                    !axiom.getClassesInSignature().iterator().next().getIRI().getShortForm().equals(new_class_name)) {

                // Удаляем текущий domain
                manager.removeAxiom(ontology, axiom);

                // Добавляем новый класс в domains
                OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + new_class_name));
                OWLDataPropertyDomainAxiom domainAxiom = df.getOWLDataPropertyDomainAxiom(unaryRole, owl_class);
                manager.addAxiom(ontology, domainAxiom);
            }
        }
    }

    /**
     * Удаление Unary Role
     * @param role_name - имя унарной роли
     */
    public void removeUnaryRole(String role_name)  {

        OWLDataProperty unaryRole = df.getOWLDataProperty(IRI.create(ontology_iri + role_name));
        removeOWLEntity(unaryRole);

    }

    /**
     * Объявление Binary Role
     * @param role_name - имя бинарной роли
     * @param inverse_role_name - имя инверсной бинарной роли
     * @param class_name - имя класса, который играет бинарную роль
     * @param inverse_class_name - имя класса, который играет инверсную бинарную роль
     */
    public void declareBinaryRole(String role_name, String inverse_role_name, String class_name, String inverse_class_name)  {

        // Создаём первую роль
        // Объявляем ObjectProperty
        OWLObjectProperty binaryRole = df.getOWLObjectProperty(IRI.create(ontology_iri + role_name + '.' + class_name + '.' + inverse_class_name));
        OWLDeclarationAxiom binaryRoleDecl = df.getOWLDeclarationAxiom(binaryRole);
        manager.addAxiom(ontology, binaryRoleDecl);

        // Добавляем domains
        OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + class_name));
        OWLObjectPropertyDomainAxiom domainAxiom = df.getOWLObjectPropertyDomainAxiom(binaryRole, owl_class);
        manager.addAxiom(ontology, domainAxiom);

        // Добавляем ranges
        owl_class = df.getOWLClass(IRI.create(ontology_iri + inverse_class_name));
        OWLObjectPropertyRangeAxiom rangeAxiom = df.getOWLObjectPropertyRangeAxiom(binaryRole, owl_class);
        manager.addAxiom(ontology, rangeAxiom);


        // Создаём вторую (инверсную) роль
        // Объявляем ObjectProperty
        if (inverse_role_name.equals("")) {
            inverse_role_name = "inverse_" + role_name;
        }
        OWLObjectProperty inverseBinaryRole = df.getOWLObjectProperty(IRI.create(ontology_iri + inverse_role_name + '.' + inverse_class_name + '.' + class_name));
        OWLDeclarationAxiom inverseBinaryRoleDecl = df.getOWLDeclarationAxiom(inverseBinaryRole);
        manager.addAxiom(ontology, inverseBinaryRoleDecl);

        // Добавляем domains
        owl_class = df.getOWLClass(IRI.create(ontology_iri + inverse_class_name));
        domainAxiom = df.getOWLObjectPropertyDomainAxiom(inverseBinaryRole, owl_class);
        manager.addAxiom(ontology, domainAxiom);

        // Добавляем ranges
        owl_class = df.getOWLClass(IRI.create(ontology_iri + class_name));
        rangeAxiom = df.getOWLObjectPropertyRangeAxiom(inverseBinaryRole, owl_class);
        manager.addAxiom(ontology, rangeAxiom);


        // Объявляем, что роли инверсны друг другу
        OWLInverseObjectPropertiesAxiom inverseAxiom = df.getOWLInverseObjectPropertiesAxiom(binaryRole, inverseBinaryRole);
        manager.addAxiom(ontology, inverseAxiom);

    }

    /**
     * Обновление существующего BinaryRole
     * @param role_name - имя существующей ObjectProperty
     * @param new_role_name - новое имя ObjectProperty
     * @param new_inverse_role_name - новое имя инверсной ObjectProperty
     * @param new_class_name - имя класса, с которым теперь будет связан ObjectProperty
     * @param new_inverse_class_name - имя класса, с которым теперь будет связан инверсный ObjectProperty
     */
    public void updateBinaryRole(String role_name, String new_role_name, String new_inverse_role_name, String new_class_name, String new_inverse_class_name) {
        OWLObjectProperty binaryRole = df.getOWLObjectProperty(IRI.create(ontology_iri + role_name));

        // Если текущее название BinaryRole не совпадает с новым
        if (!role_name.split("\\.")[0].equals(new_role_name)) {

            // Меняем название
            List<OWLOntologyChange> changes = owlEntityRenamer.changeIRI(IRI.create(ontology_iri + role_name), IRI.create(ontology_iri + new_role_name + '.' + new_class_name + '.' + new_inverse_class_name));
            manager.applyChanges(changes);
            binaryRole = df.getOWLObjectProperty(IRI.create(ontology_iri + new_role_name + '.' + new_class_name + '.' + new_inverse_class_name));
        }

        for (OWLAxiom axiom : ontology.getAxioms(binaryRole)) {

            // Если текущая аксиома описывает domain и текущий класс не совпадает с новый классом
            if (axiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_DOMAIN &&
                    !axiom.getClassesInSignature().iterator().next().getIRI().getShortForm().equals(new_class_name)) {

                // Удаляем текущий domain
                manager.removeAxiom(ontology, axiom);

                // Добавляем новый класс в domains
                OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + new_class_name));
                OWLObjectPropertyDomainAxiom domainAxiom = df.getOWLObjectPropertyDomainAxiom(binaryRole, owl_class);
                manager.addAxiom(ontology, domainAxiom);
            }
            else if (axiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_RANGE &&
                    !axiom.getClassesInSignature().iterator().next().getIRI().getShortForm().equals(new_inverse_class_name)) {

                // Удаляем текущий range
                manager.removeAxiom(ontology, axiom);

                // Добавляем новый класс в ranges
                OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + new_inverse_class_name));
                OWLObjectPropertyRangeAxiom rangeAxiom = df.getOWLObjectPropertyRangeAxiom(binaryRole, owl_class);
                manager.addAxiom(ontology, rangeAxiom);
            }
        }


        // Получаем инверсую роль из онтологии
        OWLObjectProperty inverseBinaryRole = null;
        for (OWLAxiom axiom : ontology.getAxioms(binaryRole)) {
            if (axiom.getAxiomType() == AxiomType.INVERSE_OBJECT_PROPERTIES) {
                Set<OWLObjectProperty> props = axiom.getObjectPropertiesInSignature();
                props.remove(binaryRole);
                inverseBinaryRole = props.iterator().next();
                break;
            }
        }
        if (inverseBinaryRole != null) {

            // Если текущее название инверсной BinaryRole не совпадает с новым
            if (!inverseBinaryRole.getIRI().getShortForm().split("\\.")[0].equals(new_inverse_role_name)) {

                // Меняем название
                IRI iri_inverse_role = inverseBinaryRole.getIRI();
                IRI iri_new_inverse_role = IRI.create(ontology_iri + new_inverse_role_name + '.' + new_inverse_class_name + '.' + new_class_name);
                List<OWLOntologyChange> changes = owlEntityRenamer.changeIRI(iri_inverse_role, iri_new_inverse_role);
                manager.applyChanges(changes);
                inverseBinaryRole = df.getOWLObjectProperty(iri_new_inverse_role);
            }
            for (OWLAxiom axiom : ontology.getAxioms(inverseBinaryRole)) {

                // Если текущая аксиома описывает domain и текущий класс не совпадает с новый классом
                if (axiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_DOMAIN &&
                        !axiom.getClassesInSignature().iterator().next().getIRI().getShortForm().equals(new_inverse_class_name)) {

                    // Удаляем текущий domain
                    manager.removeAxiom(ontology, axiom);

                    // Добавляем новый класс в domains
                    OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + new_inverse_class_name));
                    OWLObjectPropertyDomainAxiom domainAxiom = df.getOWLObjectPropertyDomainAxiom(inverseBinaryRole, owl_class);
                    manager.addAxiom(ontology, domainAxiom);
                }
                else if (axiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_RANGE &&
                        !axiom.getClassesInSignature().iterator().next().getIRI().getShortForm().equals(new_class_name)) {

                    // Удаляем текущий range
                    manager.removeAxiom(ontology, axiom);

                    // Добавляем новый класс в ranges
                    OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + new_class_name));
                    OWLObjectPropertyRangeAxiom rangeAxiom = df.getOWLObjectPropertyRangeAxiom(inverseBinaryRole, owl_class);
                    manager.addAxiom(ontology, rangeAxiom);
                }
            }
        }
    }

    /**
     * Удаление Binary Role
     * @param role_name - имя бинарной роли
     */
    public void removeBinaryRole(String role_name)  {

        OWLObjectProperty binaryRole = df.getOWLObjectProperty(IRI.create(ontology_iri + role_name));
        OWLObjectProperty inverseBinaryRole = null;
        for (OWLAxiom axiom : ontology.getAxioms(binaryRole)) {
            if (axiom.getAxiomType().toString().equals("InverseObjectProperties")) {
                inverseBinaryRole = axiom.getObjectPropertiesInSignature().iterator().next();
                break;
            }
        }
        removeOWLEntity(binaryRole);
        removeOWLEntity(inverseBinaryRole);
    }

    public void test() {

//        log.info("Hello");
//        for (OWLSubClassOfAxiom subClassAxiom : ontology.getAxioms(AxiomType.SUBCLASS_OF)) {
//            log.info("Hello in for");
//            log.info(subClassAxiom.toString());
//        }



    }


    // Блок кода, связанный с обновлением ORM по событию изменения OWL

    public HashMap<String, String> updateORM(List<? extends OWLOntologyChange> changes) {

        HashMap<String, String> orm_elem_dict = new HashMap<>();
        Set<OWLAxiom>unused_axioms = new HashSet<OWLAxiom>();

        checkEntityType(orm_elem_dict, unused_axioms);

        log.info("======================");
        log.info("Неиспользуемые аксиомы");
        for (OWLAxiom unused_axiom : unused_axioms) {
            log.info(unused_axiom.toString());
        }
        log.info("======================");

        return orm_elem_dict;
    }

    private void checkEntityType(HashMap<String, String> orm_elem_dict, Set<OWLAxiom> unused_axioms) {

        Set<OWLClass>owl_classes = ontology.getClassesInSignature();
        owl_classes.remove(df.getOWLThing());

        Set<OWLClass>owl_classes_in_universe = new HashSet<OWLClass>();

        for (OWLAxiom axiom : ontology.getAxioms(df.getOWLThing())) {

            // Ищем EquivalentTo аксиому у OWL-Thing
            if (axiom.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {

                // Ищем EquivalentTo аксиому у OWL-Thing
                for (OWLClassExpression class_expr : axiom.getNestedClassExpressions()) {

                    // Запоминаем классы, формирующие Universe
                    if (class_expr.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF ||
                            class_expr.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {

                        Set<OWLClass> expr_classes = class_expr.getClassesInSignature();
                        expr_classes.remove(df.getOWLThing());
                        owl_classes_in_universe.addAll(expr_classes);
                    }
                }
                //break;
            }
        }

        // Смотрим все классы в онтологии
        for (OWLClass owl_class : owl_classes) {

            boolean isEntityType = false; // Флаг сообщающий, что найден EntityType

            // Если класс входит в Universe
            if (owl_classes_in_universe.contains(owl_class)) {

                // Если Universe состоит только из единственного класса, то EntityType найден
                if (owl_classes_in_universe.size() == 1) {

                    isEntityType = true;

                } else if (owl_classes_in_universe.size() > 1) {

                    // Смотрим disjoint'ы у класса
                    for (OWLDisjointClassesAxiom disjoint_axiom : ontology.getDisjointClassesAxioms(owl_class)) {
                        Set<OWLClass>disjoint_classes = disjoint_axiom.getClassesInSignature();
                        disjoint_classes.remove(owl_class);
                        Set<OWLClass>owl_classes_in_universe_without_current = new HashSet<OWLClass>(owl_classes_in_universe);
                        owl_classes_in_universe_without_current.remove(owl_class);

                        // Смотрим, совпадают ли множества классов в Universe и disjoint текущего класса
                        if (disjoint_classes.size() >= owl_classes_in_universe_without_current.size()) {
                            isEntityType = true;
//                            for (OWLClass disjoint_class : disjoint_classes) {
//                                if (!owl_classes_in_universe_without_current.contains(disjoint_class)) {
//                                    isEntityType = false;
//                                    break;
//                                }
//                            }
                            for (OWLClass owl_class_in_universe : owl_classes_in_universe_without_current) {
                                if (!disjoint_classes.contains(owl_class_in_universe)) {
                                    isEntityType = false;
                                    break;
                                }
                            }
                        }

                    }
                }
            }

            //Если множества классов в Universe и disjoint совпадают, то значит класс является EntityType
            if (isEntityType) {
                //log.info("Объявлен Entity Type - " + owl_class.getIRI().getShortForm());
                orm_elem_dict.put(owl_class.getIRI().getShortForm(), "EntityType");
            } else {
                unused_axioms.addAll(ontology.getAxioms(owl_class));
                unused_axioms.add(df.getOWLDeclarationAxiom(owl_class));
            }
        }


    }

}
