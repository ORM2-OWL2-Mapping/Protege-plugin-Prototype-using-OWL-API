import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.io.File;
import java.util.*;


public class OwlAPI {

    public static OWLDataFactory df = OWLManager.getOWLDataFactory();
    public static OWLOntologyManager manager;
    public static OWLOntology ontology;
    public static IRI ontology_iri;
    public static OWLEntityRemover entityRemover;
    public static OWLEntityRenamer owlEntityRenamer;

    public OwlAPI (OWLOntologyManager manager) {
        this.manager = manager;
        this.ontology = manager.getOntologies().iterator().next();
        this.ontology_iri = IRI.create(ontology.getOntologyID().getOntologyIRI().get().toString() + "#");
        this.entityRemover = new OWLEntityRemover(Collections.singleton(this.ontology));
        this.owlEntityRenamer = new OWLEntityRenamer(this.manager, this.manager.getOntologies());
    }

    public OwlAPI (String iri) throws Exception {
        this.manager = OWLManager.createOWLOntologyManager();
        this.ontology = manager.createOntology(IRI.create(iri));
        this.ontology_iri = IRI.create(iri + "#");
        this.entityRemover = new OWLEntityRemover(Collections.singleton(this.ontology));
        this.owlEntityRenamer = new OWLEntityRenamer(this.manager, this.manager.getOntologies());
    }

    private static void debugSave() throws Exception {
        File fileformated = new File("example.owl");
        manager.saveOntology(ontology, new OWLXMLDocumentFormat(), IRI.create(fileformated.toURI()));
    }

    private void removeOWLEntity(OWLEntity owlEntity) {
        owlEntity.accept(entityRemover);
        manager.applyChanges(entityRemover.getChanges());
        entityRemover.reset();
    }

    private Set<OWLClass> getOWLThingSubClasses() {

        Set<OWLClass> subClasses = new HashSet<OWLClass>();
        Set<OWLClass> ontology_classes = ontology.getClassesInSignature();
        ontology_classes.remove(df.getOWLThing());
        //ontology_classes.remove(df.getOWLClass(IRI.create(ontology_iri + "Universe")));
        for (OWLClass owlClass : ontology_classes) {
            if (ontology.getSubClassAxiomsForSubClass(owlClass).size() == 0) {
                subClasses.add(owlClass);
            }
        }
        return subClasses;

    }

    private Set<OWLClass> getSubClasses(OWLClass classParent) {

        Set<OWLClass> subClasses = new HashSet<OWLClass>();
        for (OWLSubClassOfAxiom subClassAxiom : ontology.getAxioms(AxiomType.SUBCLASS_OF)) {
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
    private void updateCloseWorld() throws Exception {

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

//        // Найти OWL:Thing и прописать в equivalentTo blank_node_класс, который собирает все классы через ИЛИ
//        String or_class_name = class_name;
//        //System.out.println(or_class_name);
//        for (OWLClass owlClass : classes) {
//            if (!owlClass.getIRI().getShortForm().equals(class_name)) {
//                or_class_name = or_class_name.concat("_or_" + owlClass.getIRI().getShortForm());
//            }
//        }

//        // Если класс-universe есть в онтологии, то удаляем его
//        if (ontology.containsClassInSignature(IRI.create(ontology_iri + "Universe"))) {
//
//            // Удаляем текущий класс-universe
//            removeOWLEntity(df.getOWLClass(IRI.create(ontology_iri + "Universe")));
//        }
//        // Иначе удаляем эквивалентность между OWLThing и единственным классом
//        else {
//            for (OWLClassAxiom axiom : ontology.getAxioms(df.getOWLThing())) {
//                if (axiom.getAxiomType().toString().equals("EquivalentClasses") &&
//                        ontology_classes.iterator().next().equals(axiom.getClassesInSignature().iterator().next())) {
//                    manager.removeAxiom(ontology, axiom);
//                    break;
//                }
//            }
//        }

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

//        // Создаём и объявляем новый класс-universe
//        if (ontology_classes.size() > 1) {
//
//            // Создаём и объявляем новый класс-universe
//            OWLClass universe_class = df.getOWLClass(IRI.create(ontology_iri + "Universe"));
//            OWLDeclarationAxiom classDeclarationAx = df.getOWLDeclarationAxiom(universe_class);
//            manager.addAxiom(ontology, classDeclarationAx);
//
//            // Объявляем, что класс-universe эквивалентен дизъюнкции (логическому ИЛИ) существующих классов
//            OWLClassExpression universeClassExp = df.getOWLObjectUnionOf(ontology_classes);
//            OWLEquivalentClassesAxiom universeClassAxiom = df.getOWLEquivalentClassesAxiom(universe_class, universeClassExp);
//            manager.addAxiom(ontology, universeClassAxiom);
//            OWLEquivalentClassesAxiom owlThingClassAxiom = df.getOWLEquivalentClassesAxiom(df.getOWLThing(), universe_class);
//            manager.addAxiom(ontology, owlThingClassAxiom);
//        }
//        // Иначе OWLThing и единственный класс эквивалентны друг другу
//        else if (ontology_classes.size() == 1) {
//            OWLEquivalentClassesAxiom owlThingClassAxiom = df.getOWLEquivalentClassesAxiom(df.getOWLThing(), ontology_classes.iterator().next());
//            manager.addAxiom(ontology, owlThingClassAxiom);
//        }

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

    // МЕТОДЫ, КОТОРЫЕ НУЖНО ДОБАВИТЬ!!!
    /*
        +++declareEntityType
        updateEntityType
        ++removeEntityType
        DisjointClasses(Set<OWLClass>)
        Удаление Disjoint
        declareSubtype(Класс-родитель, классы-дети)
        GetSubtypeParent(класс)
        GetSubtypeChild(класс)
        removeSubtype(Класс-родитель, классы-дети)
        ++declareValueType
        updateValueType
        ++removeValueType
        ++declareUnaryRole
        updateUnaryRole
        ++removeUnaryRole
        ++declareBinaryRole
        updateBinaryRole
        ++removeBinaryRole
    */
    // Наверное тоже надо создавать universe-property и делать между Object(Data)Property disjoint


    public void test() throws Exception {

        getSubClasses(df.getOWLClass(IRI.create(ontology_iri + "Person")));
        getSubClasses(df.getOWLClass(IRI.create(ontology_iri + "Male")));

    }

    /**
    * Объявление Entity Type (OWL-класса)
    * @param class_name - имя объявляемого класса
    */
    public void declareEntityType(String class_name) throws Exception {
        // Объявление нового OWL-класса
        OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + class_name));
        OWLDeclarationAxiom classDeclarationAx = df.getOWLDeclarationAxiom(owl_class);
        manager.addAxiom(ontology, classDeclarationAx);

        updateCloseWorld();
        //debugSave();
    }
    public void declareEntityType2(String class_name) throws Exception {

        // Удаляем прошлый disjoint у классов
        Set<OWLClass> other_classes = ontology.getClassesInSignature();
        other_classes.remove(df.getOWLThing());

        if (other_classes.size() > 1) {
            for (OWLClassAxiom axiom : ontology.getAxioms(other_classes.iterator().next())) {
                //System.out.println(axiom.getAxiomType().toString());
                if (axiom.getAxiomType().toString().equals("DisjointClasses")) {
                    manager.removeAxiom(ontology, axiom);
                    break;
                }
            }
        }

        // Удаляем предыдущий класс-universe
        for (OWLClassAxiom axiom : ontology.getAxioms(df.getOWLThing())) {
            if (axiom.getAxiomType().toString().equals("EquivalentClasses")) {
                OWLClass equiv_class = axiom.getClassesInSignature().iterator().next();
                //System.out.println(equiv_class.getIRI().getShortForm());
                if (other_classes.size() > 1) {
                    removeOWLEntity(equiv_class);
                } else {
                    manager.removeAxiom(ontology, axiom);
                }
                break;
            }
        }



        // Объявление нового OWL-класса
        OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + class_name));
        OWLDeclarationAxiom classDeclarationAx = df.getOWLDeclarationAxiom(owl_class);
        manager.addAxiom(ontology, classDeclarationAx);

        // Добавить класс в disjoint с другими
        // Нужно сначала определить родителя, а потом сделать disjoint с другими, кроме родителя
        Set<OWLClass> classes = ontology.getClassesInSignature();
        classes.remove(df.getOWLThing());
        if (classes.size() > 1) {
            OWLDisjointClassesAxiom disjointClassesAxiom = df.getOWLDisjointClassesAxiom(classes);
            manager.addAxiom(ontology, disjointClassesAxiom);

            // Найти OWL:Thing и прописать в equivalentTo blank_node_класс, который собирает все классы через ИЛИ
            String or_class_name = class_name;
            //System.out.println(or_class_name);
            for (OWLClass owlClass : classes) {
                if (!owlClass.getIRI().getShortForm().equals(class_name)) {
                    or_class_name = or_class_name.concat("_or_" + owlClass.getIRI().getShortForm());
                }
            }



            //System.out.println(or_class_name);
            OWLClass owl_or_class = df.getOWLClass(IRI.create(ontology_iri + or_class_name));
            OWLDeclarationAxiom classDeclarationAx2 = df.getOWLDeclarationAxiom(owl_or_class);
            manager.addAxiom(ontology, classDeclarationAx2);

            OWLClassExpression owlOrClassExp = df.getOWLObjectUnionOf(classes);
            OWLEquivalentClassesAxiom owlOrClassAxiom = df.getOWLEquivalentClassesAxiom(owl_or_class, owlOrClassExp);
            manager.addAxiom(ontology, owlOrClassAxiom);
            OWLEquivalentClassesAxiom owlThingClassAxiom = df.getOWLEquivalentClassesAxiom(df.getOWLThing(), owl_or_class);
            manager.addAxiom(ontology, owlThingClassAxiom);
        } else {
            OWLEquivalentClassesAxiom owlThingClassAxiom = df.getOWLEquivalentClassesAxiom(df.getOWLThing(), owl_class);
            manager.addAxiom(ontology, owlThingClassAxiom);
        }


    }

    public void updateEntityType(String current_class_name, String new_class_name) throws Exception {

        List<OWLOntologyChange> changes = owlEntityRenamer.changeIRI(IRI.create(ontology_iri + current_class_name), IRI.create(ontology_iri + new_class_name));
        manager.applyChanges(changes);
    }

    /**
    * Удаление Entity Type (OWL-класса)
    * @param class_name - имя объявляемого класса
    */
    public void removeEntityType(String class_name) throws Exception {

        removeOWLEntity(df.getOWLClass(IRI.create(ontology_iri + class_name)));
        updateCloseWorld();
    }
    public void removeEntityType2(String class_name) throws Exception {

        // Удаляем сам EntityType
        for (OWLClass owlClass : ontology.getClassesInSignature()) {
            if (owlClass.getIRI().getShortForm().equals(class_name)) {
                removeOWLEntity(owlClass);
                break;
            }
        }

        // Удаляем предыдущий класс-universe
        for (OWLClassAxiom axiom : ontology.getAxioms(df.getOWLThing())) {
            if (axiom.getAxiomType().toString().equals("EquivalentClasses")) {
                OWLClass equiv_class = axiom.getClassesInSignature().iterator().next();
                removeOWLEntity(equiv_class);
                //manager.removeAxiom(ontology, axiom);
                break;
            }
        }



        // Обновляем disjoint'ы
        // Нужно сначала определить родителя, а потом сделать disjoint с другими, кроме родителя
        Set<OWLClass> classes = ontology.getClassesInSignature();
        classes.remove(df.getOWLThing());
        if (classes.size() > 1) {
            OWLDisjointClassesAxiom disjointClassesAxiom = df.getOWLDisjointClassesAxiom(classes);
            manager.addAxiom(ontology, disjointClassesAxiom);

            // Создаём новый класс-universe
            // Формируем имя класса-universe
            String or_class_name = "";
            for (OWLClass owlClass : classes) {
                if (or_class_name.equals("")) {
                    or_class_name = owlClass.getIRI().getShortForm();
                } else {
                    or_class_name = or_class_name.concat("_or_" + owlClass.getIRI().getShortForm());
                }
            }

            // Объявляем класс-universe
            OWLClass owl_or_class = df.getOWLClass(IRI.create(ontology_iri + or_class_name));
            OWLDeclarationAxiom classDeclarationAx2 = df.getOWLDeclarationAxiom(owl_or_class);
            manager.addAxiom(ontology, classDeclarationAx2);

            // Пишем equivalentTo для класса-universe
            OWLClassExpression owlOrClassExp = df.getOWLObjectUnionOf(classes);
            OWLEquivalentClassesAxiom owlOrClassAxiom = df.getOWLEquivalentClassesAxiom(owl_or_class, owlOrClassExp);
            manager.addAxiom(ontology, owlOrClassAxiom);

            // Делаем OWLThing эквивалентым классу-universe
            OWLEquivalentClassesAxiom owlThingClassAxiom = df.getOWLEquivalentClassesAxiom(df.getOWLThing(), owl_or_class);
            manager.addAxiom(ontology, owlThingClassAxiom);
        } else if (classes.size() == 1) {
            // Делаем OWLThing эквивалентым единственному EntityType
            OWLEquivalentClassesAxiom owlThingClassAxiom = df.getOWLEquivalentClassesAxiom(df.getOWLThing(), classes.iterator().next());
            manager.addAxiom(ontology, owlThingClassAxiom);
        }

    }

    /**
     * Объявление Subtype
     * @param child_class_name - имя EntityType, который является дочерним
     * @param parent_class_name - имя EntityType, который является родителем
     */
    public void declareSubtype(String child_class_name, String parent_class_name) throws Exception {

        OWLClass child_owl_class = df.getOWLClass(IRI.create(ontology_iri + child_class_name));
        OWLClass parent_owl_class = df.getOWLClass(IRI.create(ontology_iri + parent_class_name));
        OWLSubClassOfAxiom subClassAxiom = df.getOWLSubClassOfAxiom(child_owl_class, parent_owl_class);
        manager.addAxiom(ontology, subClassAxiom);
        updateCloseWorld();

    }

    // Нужно ли обновление subtype? Легче удалить и создать новый
//    public void updateSubtype() throws Exception {
//
//    }

    /**
     * Удаление Subtype
     * @param child_class_name - имя EntityType, который является дочерним
     * @param parent_class_name - имя EntityType, который является родителем
     */
    public void removeSubtype(String child_class_name, String parent_class_name) throws Exception {

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
    public void declareValueType(String value_name, String datatype, String class_name) throws Exception {

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
    public void updateValueType(String value_name, String new_value_name, String new_datatype, String new_class_name) throws Exception {

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
    public void removeValueType(String value_name) throws Exception {

        OWLDataProperty valueType = df.getOWLDataProperty(IRI.create(ontology_iri + value_name));
        removeOWLEntity(valueType);

    }

    /**
    * Объявление Unary Role
    * @param role_name - имя унарной роли
    * @param class_name - имя класса, который играет роль
    */
    public void declareUnaryRole(String role_name, String class_name) throws Exception {

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
    public void updateUnaryRole(String role_name, String new_role_name, String new_class_name) throws Exception {

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
    public void removeUnaryRole(String role_name) throws Exception {

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
    public void declareBinaryRole(String role_name, String inverse_role_name, String class_name, String inverse_class_name) throws Exception {

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
    public void updateBinaryRole(String role_name, String new_role_name, String new_inverse_role_name, String new_class_name, String new_inverse_class_name) throws Exception {
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
    public void removeBinaryRole(String role_name) throws Exception {

        OWLObjectProperty binaryRole = df.getOWLObjectProperty(IRI.create(ontology_iri + role_name));
        OWLObjectProperty inverseBinaryRole = null;
        for (OWLAxiom axiom : ontology.getAxioms(binaryRole)) {
            if (axiom.getAxiomType() == AxiomType.INVERSE_OBJECT_PROPERTIES) {
                Set<OWLObjectProperty> props = axiom.getObjectPropertiesInSignature();
                props.remove(binaryRole);
                inverseBinaryRole = props.iterator().next();
                break;
            }
        }
        removeOWLEntity(binaryRole);
        if (inverseBinaryRole != null) {
            removeOWLEntity(inverseBinaryRole);
        }
    }







    //public void updateORM(List<? extends OWLOntologyChange> changes) {
    public HashMap<String, String> updateORM() {

        HashMap<String, String> orm_elem_dict = new HashMap<String, String>();
        Set<OWLAxiom>unused_axioms = new HashSet<OWLAxiom>();

        checkEntityType(orm_elem_dict, unused_axioms);
        checkValueType(orm_elem_dict, unused_axioms);
        checkUnaryRole(orm_elem_dict, unused_axioms);
        checkBinaryRole(orm_elem_dict, unused_axioms);

        System.out.println("======================");
        System.out.println("Неиспользуемые аксиомы");
        for (OWLAxiom unused_axiom : unused_axioms) {
            System.out.println(unused_axiom.toString());
        }
        System.out.println("======================");

        return orm_elem_dict;
    }

    private void checkBinaryRole(HashMap<String, String> orm_elem_dict, Set<OWLAxiom> unused_axioms) {

        Set<OWLObjectProperty>owl_object_properties = ontology.getObjectPropertiesInSignature();
        owl_object_properties.remove(df.getOWLTopObjectProperty());

        for (OWLObjectProperty owl_object_prop : owl_object_properties) {

            boolean domain_is_valid = false;
            boolean range_is_valid = false;
            boolean inverse_role_is_valid = false;
            boolean valid_name = false;

            OWLClass owl_class_domain = null;
            OWLClass owl_class_range = null;

            // Как проверять inverseRole
            for (OWLAxiom owl_object_prop_axiom : ontology.getAxioms(owl_object_prop)) {

                if (owl_object_prop_axiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_DOMAIN) {
                    Set<OWLClass> owl_object_prop_domain = owl_object_prop_axiom.getClassesInSignature();
                    if (owl_object_prop_domain.size() == 1) {
                        owl_class_domain = owl_object_prop_domain.iterator().next();
                        Object obj = orm_elem_dict.get(owl_class_domain.getIRI().getShortForm());
                        if (obj != null && obj.equals("EntityType")) {
                            domain_is_valid = true;
                        }
                    }
                }
                else if (owl_object_prop_axiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_RANGE) {
                    Set<OWLClass> owl_object_prop_range = owl_object_prop_axiom.getClassesInSignature();
                    if (owl_object_prop_range.size() == 1) {
                        owl_class_range = owl_object_prop_range.iterator().next();
                        Object obj = orm_elem_dict.get(owl_class_range.getIRI().getShortForm());
                        if (obj != null && obj.equals("EntityType")) {
                            range_is_valid = true;
                        }
                    }
                }
            }

            String[] split_name = owl_object_prop.getIRI().getShortForm().split("\\.");
            if (split_name.length == 3
                    && domain_is_valid && split_name[1].equals(owl_class_domain.getIRI().getShortForm())
                    && range_is_valid && split_name[2].equals(owl_class_range.getIRI().getShortForm())) {
                valid_name = true;
            }

            // Если в Domain и в Range прописаны по одному EntityType
            if (domain_is_valid && range_is_valid && valid_name) {
                orm_elem_dict.put(owl_object_prop.getIRI().getShortForm(), "BinaryRole");
            } else {
                unused_axioms.addAll(ontology.getAxioms(owl_object_prop));
                unused_axioms.add(df.getOWLDeclarationAxiom(owl_object_prop));
            }
        }
    }

    private void checkUnaryRole(HashMap<String, String> orm_elem_dict, Set<OWLAxiom> unused_axioms) {

        Set<OWLDataProperty>owl_data_properties = ontology.getDataPropertiesInSignature();
        owl_data_properties.remove(df.getOWLTopDataProperty());

        for (OWLDataProperty owl_data_prop : owl_data_properties) {

            boolean domain_is_valid = false;
            boolean range_is_valid = false;
            boolean valid_name = false;

            for (OWLAxiom owl_data_prop_axiom : ontology.getAxioms(owl_data_prop)) {

                if (owl_data_prop_axiom.getAxiomType() == AxiomType.DATA_PROPERTY_DOMAIN) {
                    Set<OWLClass> owl_data_prop_domain = owl_data_prop_axiom.getClassesInSignature();
                    if (owl_data_prop_domain.size() == 1) {
                        OWLClass owl_class = owl_data_prop_domain.iterator().next();
                        Object obj = orm_elem_dict.get(owl_class.getIRI().getShortForm());
                        if (obj != null && obj.equals("EntityType")) {
                            domain_is_valid = true;
                        }
                        String[] split_name = owl_data_prop.getIRI().getShortForm().split("\\.");
                        if (split_name.length == 2 && split_name[1].equals(owl_class.getIRI().getShortForm())) {
                            valid_name = true;
                        }
                    }
                }
                else if (owl_data_prop_axiom.getAxiomType() == AxiomType.DATA_PROPERTY_RANGE) {
                    Set<OWLDatatype> owl_data_prop_range = owl_data_prop_axiom.getDatatypesInSignature();
                    if (owl_data_prop_range.size() == 1) {
                        OWLDatatype owl_datatype = owl_data_prop_range.iterator().next();
                        if (owl_datatype.isBoolean()) {
                            range_is_valid = true;
                        }
                    }
                }
            }

            // Если в Domain прописан единственный EntityType и в Range прописан тип "string" или "integer"
            if (domain_is_valid && range_is_valid && valid_name) {
                orm_elem_dict.put(owl_data_prop.getIRI().getShortForm(), "UnaryRole");
            } else {
                unused_axioms.addAll(ontology.getAxioms(owl_data_prop));
                unused_axioms.add(df.getOWLDeclarationAxiom(owl_data_prop));
            }
        }
    }

    private void checkValueType(HashMap<String, String> orm_elem_dict, Set<OWLAxiom> unused_axioms) {

        Set<OWLDataProperty>owl_data_properties = ontology.getDataPropertiesInSignature();
        owl_data_properties.remove(df.getOWLTopDataProperty());

        for (OWLDataProperty owl_data_prop : owl_data_properties) {

            boolean domain_is_valid = false;
            boolean range_is_valid = false;
            boolean valid_name = false;

            for (OWLAxiom owl_data_prop_axiom : ontology.getAxioms(owl_data_prop)) {

                if (owl_data_prop_axiom.getAxiomType() == AxiomType.DATA_PROPERTY_DOMAIN) {
                    Set<OWLClass> owl_data_prop_domain = owl_data_prop_axiom.getClassesInSignature();
                    if (owl_data_prop_domain.size() == 1) {
                        OWLClass owl_class = owl_data_prop_domain.iterator().next();
                        Object obj = orm_elem_dict.get(owl_class.getIRI().getShortForm());
                        if (obj != null && obj.equals("EntityType")) {
                            domain_is_valid = true;
                        }
                        String[] split_name = owl_data_prop.getIRI().getShortForm().split("\\.");
                        if (split_name.length == 2 && split_name[1].equals(owl_class.getIRI().getShortForm())) {
                            valid_name = true;
                        }
                    }
                }
                else if (owl_data_prop_axiom.getAxiomType() == AxiomType.DATA_PROPERTY_RANGE) {
                    Set<OWLDatatype> owl_data_prop_range = owl_data_prop_axiom.getDatatypesInSignature();
                    if (owl_data_prop_range.size() == 1) {
                        OWLDatatype owl_datatype = owl_data_prop_range.iterator().next();
                        if (owl_datatype.isString() || owl_datatype.isInteger()) {
                            range_is_valid = true;
                        }
                    }
                }
            }

            // Если в Domain прописан единственный EntityType и в Range прописан тип "string" или "integer"
            if (domain_is_valid && range_is_valid && valid_name) {
                orm_elem_dict.put(owl_data_prop.getIRI().getShortForm(), "ValueType");
            } else {
                unused_axioms.addAll(ontology.getAxioms(owl_data_prop));
                unused_axioms.add(df.getOWLDeclarationAxiom(owl_data_prop));
            }
        }
    }

    private void checkEntityType(HashMap<String, String> orm_elem_dict, Set<OWLAxiom> unused_axioms) {

        Set<OWLClass>owl_classes = ontology.getClassesInSignature();
        owl_classes.remove(df.getOWLThing());

        Set<OWLClass>owl_classes_in_universe = new HashSet<OWLClass>();

        for (OWLAxiom axiom : ontology.getAxioms(df.getOWLThing())) {

            // Ищем EquivalentTo аксиому у OWL-Thing
            if (axiom.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {

//                System.out.println("axiom " + axiom.toString());
//                System.out.println(axiom.getClassesInSignature().toString());
                // Ищем EquivalentTo аксиому у OWL-Thing
                for (OWLClassExpression class_expr : axiom.getNestedClassExpressions()) {

                    // Запоминаем классы, формирующие Universe
//                    System.out.println("Class_expr " + class_expr.getClassExpressionType());
                    if (class_expr.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF ||
                            class_expr.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
                        Set<OWLClass> expr_classes = class_expr.getClassesInSignature();
                        expr_classes.remove(df.getOWLThing());
                        owl_classes_in_universe.addAll(expr_classes);
//                        for (OWLClass expr_class : expr_classes) {
//                            owl_classes_in_universe.add(expr_class);
//                        }
                    }

                }

                //break;
            }
        }

        // Смотрим все классы в онтологии
        for (OWLClass owl_class : owl_classes) {
            boolean isEntityType = false;

            // Если класс входит в Universe
            if (owl_classes_in_universe.contains(owl_class)) {

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
//                System.out.println("Объявлен Entity Type - " + owl_class.getIRI().getShortForm());
                orm_elem_dict.put(owl_class.getIRI().getShortForm(), "EntityType");
            } else {
//                for (OWLAxiom class_axiom : ontology.getAxioms(owl_class)) {
//                    unused_axioms.add(class_axiom);
//                }
                unused_axioms.addAll(ontology.getAxioms(owl_class));
                unused_axioms.add(df.getOWLDeclarationAxiom(owl_class));
            }
        }


    }
}
