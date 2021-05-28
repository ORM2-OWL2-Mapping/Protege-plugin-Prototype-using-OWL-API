import ORMModel.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.semanticweb.owlapi.vocab.OWL2Datatype;


import java.io.File;
import java.util.*;

public abstract class ORMtoOWLElementMapper {

    protected OWLDataFactory df = OWLManager.getOWLDataFactory();
    protected OWLOntologyManager manager;
    protected OWLOntology ontology;
    protected IRI ontology_iri;
    protected OWLEntityRemover entityRemover;
    protected OWLEntityRenamer owlEntityRenamer;


    public ORMtoOWLElementMapper (OWLOntologyManager manager) {
        this.manager = manager;
        this.ontology = manager.getOntologies().iterator().next();
        this.ontology_iri = IRI.create(ontology.getOntologyID().getOntologyIRI().get().toString() + "#");
        this.entityRemover = new OWLEntityRemover(Collections.singleton(this.ontology));
        this.owlEntityRenamer = new OWLEntityRenamer(this.manager, this.manager.getOntologies());
    }

    protected void debugSave() throws Exception {
        File fileformated = new File("example.owl");
        manager.saveOntology(ontology, new OWLXMLDocumentFormat(), IRI.create(fileformated.toURI()));
    }

    protected void removeOWLEntity(OWLEntity owlEntity) {
        owlEntity.accept(entityRemover);
        manager.applyChanges(entityRemover.getChanges());
        entityRemover.reset();
    }

    protected Set<OWLClass> getOWLThingSubClasses() {

        Set<OWLClass> subClasses = new HashSet<OWLClass>();
        Set<OWLClass> ontology_classes = ontology.getClassesInSignature();
        ontology_classes.remove(df.getOWLThing());
        for (OWLClass owlClass : ontology_classes) {
            if (ontology.getSubClassAxiomsForSubClass(owlClass).size() == 0) {
                subClasses.add(owlClass);
            }
        }
        return subClasses;
    }

    protected Set<OWLClass> getSubClasses(OWLClass classParent) {

        Set<OWLClass> subClasses = new HashSet<OWLClass>();
        for (OWLSubClassOfAxiom subClassAxiom : ontology.getAxioms(AxiomType.SUBCLASS_OF)) {
            if (subClassAxiom.getSuperClass().asOWLClass().equals(classParent)) {
                subClasses.add(subClassAxiom.getSubClass().asOWLClass());
            }
        }

        return subClasses;
    }

    /**
     * Удаление текущих утверждений о закрытом мире
     */
    protected void clearCloseWorld() {

        // Получаем корневые классы (MainClasses)
        Set<OWLClass> mainClasses = getOWLThingSubClasses();

        // Если всего один MainClass
        if (mainClasses.size() == 1) {

            // Удаляем equivalent между owl:Thing и MainClass
            OWLEquivalentClassesAxiom universeClassAxiom = df.getOWLEquivalentClassesAxiom(df.getOWLThing(), mainClasses.iterator().next());
            manager.removeAxiom(ontology, universeClassAxiom);
        }
        // Если в онтологии больше одного MainClass
        else if (mainClasses.size() > 1) {

            // Удаляем disjoint между MainClasses
            OWLDisjointClassesAxiom disjointClassesAxiom = df.getOWLDisjointClassesAxiom(mainClasses);
            manager.removeAxiom(ontology, disjointClassesAxiom);

            // Удаляем equivalent между owl:Thing и union(MainClasses)
            OWLClassExpression universeClassExp = df.getOWLObjectUnionOf(mainClasses);
            OWLEquivalentClassesAxiom universeClassAxiom = df.getOWLEquivalentClassesAxiom(df.getOWLThing(), universeClassExp);
            manager.removeAxiom(ontology, universeClassAxiom);
        }
    }

    /**
     * Создание новых утверждений о закрытом мире
     */
    protected void createCloseWorld() {

        // Получаем корневые классы (MainClasses)
        Set<OWLClass> mainClasses = getOWLThingSubClasses();

        // Если всего один MainClass
        if (mainClasses.size() == 1) {

            // Добавляем equivalent между owl:Thing и MainClass
            OWLEquivalentClassesAxiom universeClassAxiom = df.getOWLEquivalentClassesAxiom(df.getOWLThing(), mainClasses.iterator().next());
            manager.addAxiom(ontology, universeClassAxiom);
        }
        // Если в онтологии больше одного MainClass
        else if (mainClasses.size() > 1) {

            // Добавляем disjoint между MainClasses
            OWLDisjointClassesAxiom disjointClassesAxiom = df.getOWLDisjointClassesAxiom(mainClasses);
            manager.addAxiom(ontology, disjointClassesAxiom);

            // Добавляем equivalent между owl:Thing и union(MainClasses)
            OWLClassExpression universeClassExp = df.getOWLObjectUnionOf(mainClasses);
            OWLEquivalentClassesAxiom universeClassAxiom = df.getOWLEquivalentClassesAxiom(df.getOWLThing(), universeClassExp);
            manager.addAxiom(ontology, universeClassAxiom);
        }
    }

    protected void updateClassSubclasses(OWLClass classParent) {

        Set<OWLClass> subclasses = getSubClasses(classParent);
        OWLClass not__class = null;
        OWLClass union__class = null;

        // Ищем классы not__ и union__
        for (OWLClass subclass : subclasses) {
            if (subclass.getIRI().getShortForm().startsWith("not__") && getSubClasses(subclass).size() == 0) {
                not__class = subclass;
            } else if (subclass.getIRI().getShortForm().startsWith("union__")) {
                union__class = subclass;
            }
        }

        // Если класс union__ был найден, то его подклассы делаем подклассами classParent и сам класс удаляем
        if (union__class != null) {
            for (OWLClass subclass : getSubClasses(union__class)) {
                OWLSubClassOfAxiom subClassAxiom = df.getOWLSubClassOfAxiom(subclass, classParent);
                manager.addAxiom(ontology, subClassAxiom);
            }
            removeOWLEntity(union__class);
        }

        // Если класс not__ был найден, то его удаляем
        if (not__class != null) {
            removeOWLEntity(not__class);
        }

        // Получаем текущие подклассы класса classParent
        subclasses = getSubClasses(classParent);
        Set<OWLClass> entityTypes = new HashSet<>();
        for (OWLClass subclass : subclasses) {
            if (!subclass.getIRI().getShortForm().startsWith("not__") && !subclass.getIRI().getShortForm().startsWith("union__")) {
                entityTypes.add(subclass);
            }
        }

        // Если есть подклассы, то создаём новые классы not__ и union__
        if (entityTypes.size() > 0) {

            String unionClassName = "union__";

            // Если больше одного подкласса, то создаём класс union__
            if (entityTypes.size() > 1) {

                // Формируем имя union__ класса
                String suffixName = "_or_";

                List<OWLClass> sortedList = new ArrayList<>(entityTypes);
                Collections.sort(sortedList);
                for (OWLClass entityType : sortedList) {
                    unionClassName = unionClassName.concat(entityType.getIRI().getShortForm() + suffixName);
                }
                unionClassName = unionClassName.substring(0, unionClassName.length() - suffixName.length());

                // Объявляем класс union__
                union__class = df.getOWLClass(IRI.create(ontology_iri + unionClassName));
                OWLDeclarationAxiom classDeclarationAx = df.getOWLDeclarationAxiom(union__class);
                manager.addAxiom(ontology, classDeclarationAx);

                // Добавляем подклассы к union__
                for (OWLClass entityType : entityTypes) {
                    OWLSubClassOfAxiom subClassAxiom = df.getOWLSubClassOfAxiom(entityType, union__class);
                    manager.addAxiom(ontology, subClassAxiom);
                    subClassAxiom = df.getOWLSubClassOfAxiom(entityType, classParent);
                    manager.removeAxiom(ontology, subClassAxiom);
                }

                // Cоздаём утверждение union__ == union(EntityTypes)
                OWLClassExpression universeClassExp = df.getOWLObjectUnionOf(entityTypes);
                OWLEquivalentClassesAxiom universeClassAxiom = df.getOWLEquivalentClassesAxiom(union__class, universeClassExp);
                manager.addAxiom(ontology, universeClassAxiom);
            }
            // Иначе классом union__ является единственный подкласс
            else {
                union__class = entityTypes.iterator().next();
                unionClassName = union__class.getIRI().getShortForm();
            }

            // Делаем класс union__ подклассом classParent
            OWLSubClassOfAxiom subClassAxiom = df.getOWLSubClassOfAxiom(union__class, classParent);
            manager.addAxiom(ontology, subClassAxiom);

            // Создаём и объявляем класс not__
            String notClassName = "not__" + unionClassName;
            not__class = df.getOWLClass(IRI.create(ontology_iri + notClassName));
            OWLDeclarationAxiom classDeclarationAx = df.getOWLDeclarationAxiom(not__class);
            manager.addAxiom(ontology, classDeclarationAx);

            // Делаем класс not__ подклассом classParent
            subClassAxiom = df.getOWLSubClassOfAxiom(not__class, classParent);
            manager.addAxiom(ontology, subClassAxiom);


            Set<OWLClass> unionEquivalentClassParent = new HashSet<>();
            unionEquivalentClassParent.add(not__class);
            unionEquivalentClassParent.add(union__class);

            // Cоздаём утверждение classParent == union(not__, union__)
            OWLClassExpression universeClassExp = df.getOWLObjectUnionOf(unionEquivalentClassParent);
            OWLEquivalentClassesAxiom universeClassAxiom = df.getOWLEquivalentClassesAxiom(classParent, universeClassExp);
            manager.addAxiom(ontology, universeClassAxiom);

            // Cоздаём disjoint между union__ и not__
            OWLDisjointClassesAxiom disjointClassesAxiom = df.getOWLDisjointClassesAxiom(unionEquivalentClassParent);
            manager.addAxiom(ontology, disjointClassesAxiom);
        }
    }



    public abstract void addElement(ORMElement element);

    public abstract void updateElement(ORMElement editedElement, ORMElement existElement);

    public abstract void removeElement(ORMElement element);
}

//    try { debugSave(); } catch (Exception ignored) { }

class ORMtoOWLEntityTypeMapper extends ORMtoOWLElementMapper {


    public ORMtoOWLEntityTypeMapper(OWLOntologyManager manager) {
        super(manager);
    }

    private void renameAllRelatedElements(String className, String newClassName) {

        Map<OWLEntity, IRI> changeMap = new HashMap<>();

        for (OWLClass ontologyClass : ontology.getClassesInSignature()) {

            String ontologyClassName = ontologyClass.getIRI().getShortForm();
            if ((ontologyClassName.startsWith("union__") || ontologyClassName.startsWith("not__")) && ontologyClassName.contains(className)) {

                String newOntologyClassName = ontologyClassName.replaceFirst("_" + className + "_", "_" + newClassName + "_");
                newOntologyClassName = newOntologyClassName.replaceFirst("_" + className + "$", "_" + newClassName);
                changeMap.put(ontologyClass, IRI.create(ontology_iri + newOntologyClassName));
            }
        }

        for (OWLObjectProperty ontologyObjProp : ontology.getObjectPropertiesInSignature()) {

            String ontologyObjPropName = ontologyObjProp.getIRI().getShortForm();
            if (ontologyObjPropName.contains(className)) {

                String newOntologyObjPropName = ontologyObjPropName.replaceFirst("\\." + className + "\\.", "." + newClassName + ".");
                newOntologyObjPropName = newOntologyObjPropName.replaceFirst("\\." + className + "$", "." + newClassName);
                changeMap.put(ontologyObjProp, IRI.create(ontology_iri + newOntologyObjPropName));
            }
        }

        for (OWLDataProperty ontologyDataProp : ontology.getDataPropertiesInSignature()) {

            String ontologyDataPropName = ontologyDataProp.getIRI().getShortForm();
            if (ontologyDataPropName.contains(className)) {

                String newOntologyDataPropName = ontologyDataPropName.replaceFirst("\\." + className + "\\.", "." + newClassName + ".");
                newOntologyDataPropName = newOntologyDataPropName.replaceFirst("\\." + className + "$", "." + newClassName);
                changeMap.put(ontologyDataProp, IRI.create(ontology_iri + newOntologyDataPropName));
            }
        }

        List<OWLOntologyChange> changes = owlEntityRenamer.changeIRI(changeMap);
        manager.applyChanges(changes);
    }

    @Override
    public void addElement(ORMElement element) {

        clearCloseWorld();

        ORMEntityType entityType = (ORMEntityType)element;

        // Объявление нового OWL-класса
        OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + entityType.getName()));
        OWLDeclarationAxiom classDeclarationAx = df.getOWLDeclarationAxiom(owl_class);
        manager.addAxiom(ontology, classDeclarationAx);

        createCloseWorld();
    }

    @Override
    public void updateElement(ORMElement editedElement, ORMElement existElement) {

        ORMEntityType existEntityType = (ORMEntityType)existElement;
        ORMEntityType editedEntityType = (ORMEntityType)editedElement;

        renameAllRelatedElements(existEntityType.getName(), editedEntityType.getName());

        List<OWLOntologyChange> changes = owlEntityRenamer.changeIRI(IRI.create(ontology_iri + existEntityType.getName()), IRI.create(ontology_iri + editedEntityType.getName()));
        manager.applyChanges(changes);
    }

    @Override
    public void removeElement(ORMElement element) {

        clearCloseWorld();

        ORMEntityType entityType = (ORMEntityType)element;

        // Удаление OWL-класса
        OWLClass owlClass = df.getOWLClass(IRI.create(ontology_iri + entityType.getName()));
        removeOWLEntity(owlClass);

        createCloseWorld();
    }
}

class ORMtoOWLSubtypingMapper extends ORMtoOWLElementMapper {


    public ORMtoOWLSubtypingMapper(OWLOntologyManager manager) {
        super(manager);
    }

    @Override
    public void addElement(ORMElement element) {

        clearCloseWorld();

        ORMSubtyping subtype = (ORMSubtyping)element;
        ORMEntityType sourceEntityType = subtype.getSource();
        ORMEntityType targetEntityType = subtype.getTarget();

        OWLClass child_owl_class = df.getOWLClass(IRI.create(ontology_iri + sourceEntityType.getName()));
        OWLClass parent_owl_class = df.getOWLClass(IRI.create(ontology_iri + targetEntityType.getName()));
        OWLSubClassOfAxiom subClassAxiom = df.getOWLSubClassOfAxiom(child_owl_class, parent_owl_class);
        manager.addAxiom(ontology, subClassAxiom);

        updateClassSubclasses(parent_owl_class);
        createCloseWorld();
    }

    @Override
    public void updateElement(ORMElement editedElement, ORMElement existElement) {

        ORMSubtyping existSubtyping = (ORMSubtyping)existElement;
        ORMSubtyping editedSubtyping = (ORMSubtyping)editedElement;

        removeElement(existSubtyping);
        addElement(editedSubtyping);
    }

    @Override
    public void removeElement(ORMElement element) {

        clearCloseWorld();

        ORMSubtyping subtype = (ORMSubtyping)element;
        ORMEntityType sourceEntityType = subtype.getSource();
        ORMEntityType targetEntityType = subtype.getTarget();

        OWLClass child_owl_class = df.getOWLClass(IRI.create(ontology_iri + sourceEntityType.getName()));
        OWLClass parent_owl_class = df.getOWLClass(IRI.create(ontology_iri + targetEntityType.getName()));
        OWLSubClassOfAxiom subClassAxiom = df.getOWLSubClassOfAxiom(child_owl_class, parent_owl_class);

        for (OWLClass subclass : getSubClasses(parent_owl_class)) {
            if (subclass.getIRI().getShortForm().startsWith("union__") && getSubClasses(subclass).size() > 1) {
                for (OWLClass owlClass : getSubClasses(subclass)) {
                    if (owlClass.equals(child_owl_class)) {
                        subClassAxiom = df.getOWLSubClassOfAxiom(child_owl_class, subclass);
                        break;
                    }
                }
                break;
            }
        }

        manager.removeAxiom(ontology, subClassAxiom);

        updateClassSubclasses(parent_owl_class);
        createCloseWorld();
    }
}

class ORMtoOWLValueTypeMapper extends ORMtoOWLElementMapper {

    public ORMtoOWLValueTypeMapper(OWLOntologyManager manager) {
        super(manager);
    }

    @Override
    public void addElement(ORMElement element) {

        ORMValueType valueType = (ORMValueType)element;
        String valueTypeName = valueType.getRoleName() + '.' + valueType.getEntityType().getName() + '.' + valueType.getName();

        // Объявляем DataProperty
        OWLDataProperty dataProp = df.getOWLDataProperty(IRI.create(ontology_iri + valueTypeName));
        OWLDeclarationAxiom dataPropDeclAx = df.getOWLDeclarationAxiom(dataProp);
        manager.addAxiom(ontology, dataPropDeclAx);

        // Добавляем класс в domains
        OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + valueType.getEntityType().getName()));
        OWLDataPropertyDomainAxiom domainAxiom = df.getOWLDataPropertyDomainAxiom(dataProp, owl_class);
        manager.addAxiom(ontology, domainAxiom);

        // Добавляем тип в ranges
        OWLDatatype stringDatatype = df.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI());
        OWLDataPropertyRangeAxiom rangeAxiom = df.getOWLDataPropertyRangeAxiom(dataProp, stringDatatype);
        manager.addAxiom(ontology, rangeAxiom);
    }

    @Override
    public void updateElement(ORMElement editedElement, ORMElement existElement) {

        ORMValueType existValueType = (ORMValueType)existElement;
        ORMValueType editedValueType = (ORMValueType)editedElement;

        String existValueTypeName = existValueType.getRoleName() + '.' + existValueType.getEntityType().getName() + '.' + existValueType.getName();
        OWLDataProperty dataProp = df.getOWLDataProperty(IRI.create(ontology_iri + existValueTypeName));

        for (OWLAxiom axiom : ontology.getAxioms(dataProp)) {

            // Если текущая аксиома описывает domain и текущий класс не совпадает с новый классом
            if (axiom.getAxiomType() == AxiomType.DATA_PROPERTY_DOMAIN && !existValueType.getEntityType().getName().equals(editedValueType.getEntityType().getName()) ) {

                // Удаляем текущий domain
                manager.removeAxiom(ontology, axiom);

                // Добавляем новый класс в domains
                OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + editedValueType.getEntityType().getName()));
                OWLDataPropertyDomainAxiom domainAxiom = df.getOWLDataPropertyDomainAxiom(dataProp, owl_class);
                manager.addAxiom(ontology, domainAxiom);
            }
            else if (axiom.getAxiomType() == AxiomType.DATA_PROPERTY_RANGE) {

                // Удаляем текущий range
                manager.removeAxiom(ontology, axiom);

                // Добавляем новый тип в ranges
                OWLDatatype stringDatatype = df.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI());
                OWLDataPropertyRangeAxiom rangeAxiom = df.getOWLDataPropertyRangeAxiom(dataProp, stringDatatype);
                manager.addAxiom(ontology, rangeAxiom);
            }
        }

        String editedValueTypeName = editedValueType.getRoleName() + '.' + editedValueType.getEntityType().getName() + '.' + editedValueType.getName();

        // Переименовываем dataProperty, если изменилось название
        if (!existValueTypeName.equals(editedValueTypeName)) {
            List<OWLOntologyChange> changes = owlEntityRenamer.changeIRI(IRI.create(ontology_iri + existValueTypeName), IRI.create(ontology_iri + editedValueTypeName));
            manager.applyChanges(changes);
        }
    }

    @Override
    public void removeElement(ORMElement element) {

        ORMValueType valueType = (ORMValueType)element;
        String valueTypeName = valueType.getRoleName() + '.' + valueType.getEntityType().getName() + '.' + valueType.getName();

        // Находим и удаляем DataProperty
        OWLDataProperty dataProp = df.getOWLDataProperty(IRI.create(ontology_iri + valueTypeName));
        removeOWLEntity(dataProp);
    }
}

class ORMtoOWLUnaryRoleMapper extends ORMtoOWLElementMapper {

    public ORMtoOWLUnaryRoleMapper(OWLOntologyManager manager) {
        super(manager);
    }

    @Override
    public void addElement(ORMElement element) {

        ORMUnaryRole unaryRole = (ORMUnaryRole)element;
        String unaryRoleName = unaryRole.getName() + '.' + unaryRole.getEntityType().getName();

        // Объявляем DataProperty
        OWLDataProperty dataProp = df.getOWLDataProperty(IRI.create(ontology_iri + unaryRoleName));
        OWLDeclarationAxiom dataPropDeclAx = df.getOWLDeclarationAxiom(dataProp);
        manager.addAxiom(ontology, dataPropDeclAx);

        // Добавляем класс в domains
        OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + unaryRole.getEntityType().getName()));
        OWLDataPropertyDomainAxiom domainAxiom = df.getOWLDataPropertyDomainAxiom(dataProp, owl_class);
        manager.addAxiom(ontology, domainAxiom);

        // Добавляем тип в ranges
        OWLDatatype stringDatatype = df.getOWLDatatype(OWL2Datatype.XSD_BOOLEAN.getIRI());
        OWLDataPropertyRangeAxiom rangeAxiom = df.getOWLDataPropertyRangeAxiom(dataProp, stringDatatype);
        manager.addAxiom(ontology, rangeAxiom);
    }

    @Override
    public void updateElement(ORMElement editedElement, ORMElement existElement) {

        ORMUnaryRole existUnaryRole = (ORMUnaryRole)existElement;
        ORMUnaryRole editedUnaryRole = (ORMUnaryRole)editedElement;

        String existUnaryRoleName = existUnaryRole.getName() + '.' + existUnaryRole.getEntityType().getName();
        OWLDataProperty dataProp = df.getOWLDataProperty(IRI.create(ontology_iri + existUnaryRoleName));

        for (OWLAxiom axiom : ontology.getAxioms(dataProp)) {

            // Если текущая аксиома описывает domain и текущий класс не совпадает с новый классом
            if (axiom.getAxiomType() == AxiomType.DATA_PROPERTY_DOMAIN && !existUnaryRole.getEntityType().getName().equals(editedUnaryRole.getEntityType().getName()) ) {

                // Удаляем текущий domain
                manager.removeAxiom(ontology, axiom);

                // Добавляем новый класс в domains
                OWLClass owl_class = df.getOWLClass(IRI.create(ontology_iri + editedUnaryRole.getEntityType().getName()));
                OWLDataPropertyDomainAxiom domainAxiom = df.getOWLDataPropertyDomainAxiom(dataProp, owl_class);
                manager.addAxiom(ontology, domainAxiom);
            }
            else if (axiom.getAxiomType() == AxiomType.DATA_PROPERTY_RANGE) {

                // Удаляем текущий range
                manager.removeAxiom(ontology, axiom);

                // Добавляем новый тип в ranges
                OWLDatatype stringDatatype = df.getOWLDatatype(OWL2Datatype.XSD_BOOLEAN.getIRI());
                OWLDataPropertyRangeAxiom rangeAxiom = df.getOWLDataPropertyRangeAxiom(dataProp, stringDatatype);
                manager.addAxiom(ontology, rangeAxiom);
            }
        }

        String editedUnaryRoleName = editedUnaryRole.getName() + '.' + editedUnaryRole.getEntityType().getName();

        // Переименовываем dataProperty, если изменилось название
        if (!existUnaryRoleName.equals(editedUnaryRoleName)) {
            List<OWLOntologyChange> changes = owlEntityRenamer.changeIRI(IRI.create(ontology_iri + existUnaryRoleName), IRI.create(ontology_iri + editedUnaryRoleName));
            manager.applyChanges(changes);
        }
    }

    @Override
    public void removeElement(ORMElement element) {

        ORMUnaryRole unaryRole = (ORMUnaryRole)element;
        String unaryRoleName = unaryRole.getName() + '.' + unaryRole.getEntityType().getName();

        // Находим и удаляем DataProperty
        OWLDataProperty dataProp = df.getOWLDataProperty(IRI.create(ontology_iri + unaryRoleName));
        removeOWLEntity(dataProp);
    }
}

class ORMtoOWLBinaryRoleMapper extends ORMtoOWLElementMapper {

    public ORMtoOWLBinaryRoleMapper(OWLOntologyManager manager) {
        super(manager);
    }

    @Override
    public void addElement(ORMElement element) {

        ORMBinaryRole binaryRole = (ORMBinaryRole)element;
        String binaryRoleName = binaryRole.getName() + '.' + binaryRole.getSource().getName() + '.' + binaryRole.getTarget().getName();

        // Объявляем ObjectProperty
        OWLObjectProperty objProp = df.getOWLObjectProperty(IRI.create(ontology_iri + binaryRoleName));
        OWLDeclarationAxiom objPropDeclAx = df.getOWLDeclarationAxiom(objProp);
        manager.addAxiom(ontology, objPropDeclAx);

        // Добавляем класс в domains
        OWLClass domain_owl_class = df.getOWLClass(IRI.create(ontology_iri + binaryRole.getSource().getName()));
        OWLObjectPropertyDomainAxiom domainAxiom = df.getOWLObjectPropertyDomainAxiom(objProp, domain_owl_class);
        manager.addAxiom(ontology, domainAxiom);

        // Добавляем класс в ranges
        OWLClass range_owl_class = df.getOWLClass(IRI.create(ontology_iri + binaryRole.getTarget().getName()));
        OWLObjectPropertyRangeAxiom rangeAxiom = df.getOWLObjectPropertyRangeAxiom(objProp, range_owl_class);
        manager.addAxiom(ontology, rangeAxiom);

        // Формируем название inverseRole
        String inverseBinaryRoleName = "inverse__" + binaryRoleName;
        if (!binaryRole.getInverseRoleName().isEmpty()) {
            inverseBinaryRoleName = binaryRole.getInverseRoleName() + '.' + binaryRole.getTarget().getName() + '.' + binaryRole.getSource().getName();;
        }

        // Объявляем ObjectProperty
        OWLObjectProperty inverse_objProp = df.getOWLObjectProperty(IRI.create(ontology_iri + inverseBinaryRoleName));
        objPropDeclAx = df.getOWLDeclarationAxiom(inverse_objProp);
        manager.addAxiom(ontology, objPropDeclAx);

        // Добавляем класс в domains
        domainAxiom = df.getOWLObjectPropertyDomainAxiom(inverse_objProp, range_owl_class);
        manager.addAxiom(ontology, domainAxiom);

        // Добавляем класс в ranges
        rangeAxiom = df.getOWLObjectPropertyRangeAxiom(inverse_objProp, domain_owl_class);
        manager.addAxiom(ontology, rangeAxiom);

        // Объявляем, что роли инверсны друг другу
        OWLInverseObjectPropertiesAxiom inverseAxiom = df.getOWLInverseObjectPropertiesAxiom(objProp, inverse_objProp);
        manager.addAxiom(ontology, inverseAxiom);
    }

    @Override
    public void updateElement(ORMElement editedElement, ORMElement existElement) {

        ORMBinaryRole existBinaryRole = (ORMBinaryRole)existElement;
        ORMBinaryRole editedBinaryRole = (ORMBinaryRole)editedElement;

        String existBinaryRoleName = existBinaryRole.getName() + '.' + existBinaryRole.getSource().getName() + '.' + existBinaryRole.getTarget().getName();
        OWLObjectProperty objProp = df.getOWLObjectProperty(IRI.create(ontology_iri + existBinaryRoleName));

        // Формируем название inverseRole
        String inverseExistBinaryRoleName = "inverse__" + existBinaryRoleName;
        if (!existBinaryRole.getInverseRoleName().isEmpty()) {
            inverseExistBinaryRoleName = existBinaryRole.getInverseRoleName() + '.' + existBinaryRole.getTarget().getName() + '.' + existBinaryRole.getSource().getName();;
        }
        OWLObjectProperty inverse_objProp = df.getOWLObjectProperty(IRI.create(ontology_iri + inverseExistBinaryRoleName));

        // Если domain отличается
        if (!existBinaryRole.getSource().getName().equals(editedBinaryRole.getSource().getName())) {

            // Удаляем текущий domain у роли и range у инверсной роли
            OWLClass domain_owl_class = df.getOWLClass(IRI.create(ontology_iri + existBinaryRole.getSource().getName()));
            OWLObjectPropertyDomainAxiom domainAxiom = df.getOWLObjectPropertyDomainAxiom(objProp, domain_owl_class);
            manager.removeAxiom(ontology, domainAxiom);
            OWLObjectPropertyRangeAxiom rangeAxiom = df.getOWLObjectPropertyRangeAxiom(inverse_objProp, domain_owl_class);
            manager.removeAxiom(ontology, rangeAxiom);

            // Добавляем новый класс в domains у роли и range у инверсной
            OWLClass new_domain_owl_class = df.getOWLClass(IRI.create(ontology_iri + editedBinaryRole.getSource().getName()));
            domainAxiom = df.getOWLObjectPropertyDomainAxiom(objProp, new_domain_owl_class);
            manager.addAxiom(ontology, domainAxiom);
            rangeAxiom = df.getOWLObjectPropertyRangeAxiom(inverse_objProp, new_domain_owl_class);
            manager.addAxiom(ontology, rangeAxiom);
        }

        // Если range отличается
        if (!existBinaryRole.getTarget().getName().equals(editedBinaryRole.getTarget().getName())) {

            // Удаляем текущий range у роли и domain у инверсной роли
            OWLClass range_owl_class = df.getOWLClass(IRI.create(ontology_iri + existBinaryRole.getTarget().getName()));
            OWLObjectPropertyRangeAxiom rangeAxiom = df.getOWLObjectPropertyRangeAxiom(objProp, range_owl_class);
            manager.removeAxiom(ontology, rangeAxiom);
            OWLObjectPropertyDomainAxiom domainAxiom = df.getOWLObjectPropertyDomainAxiom(inverse_objProp, range_owl_class);
            manager.removeAxiom(ontology, domainAxiom);

            // Добавляем новый класс в range у роли и domain у инверсной роли
            OWLClass new_range_owl_class = df.getOWLClass(IRI.create(ontology_iri + editedBinaryRole.getTarget().getName()));
            rangeAxiom = df.getOWLObjectPropertyRangeAxiom(objProp, new_range_owl_class);
            manager.addAxiom(ontology, rangeAxiom);
            domainAxiom = df.getOWLObjectPropertyDomainAxiom(inverse_objProp, new_range_owl_class);
            manager.addAxiom(ontology, domainAxiom);
        }

        String editedBinaryRoleName = editedBinaryRole.getName() + '.' + editedBinaryRole.getSource().getName() + '.' + editedBinaryRole.getTarget().getName();

        // Если названия различаются, то переименовываем
        if (!existBinaryRoleName.equals(editedBinaryRoleName)) {
            List<OWLOntologyChange> changes = owlEntityRenamer.changeIRI(IRI.create(ontology_iri + existBinaryRoleName), IRI.create(ontology_iri + editedBinaryRoleName));
            manager.applyChanges(changes);
        }

        String inverseEditedBinaryRoleName = "inverse__" + editedBinaryRoleName;
        if (!existBinaryRole.getInverseRoleName().isEmpty()) {
            inverseEditedBinaryRoleName = editedBinaryRole.getInverseRoleName() + '.' + editedBinaryRole.getTarget().getName() + '.' + editedBinaryRole.getSource().getName();;
        }

        // Если названия inverseRole различаются, то переименовываем
        if (!inverseExistBinaryRoleName.equals(inverseEditedBinaryRoleName)) {
            List<OWLOntologyChange> changes = owlEntityRenamer.changeIRI(IRI.create(ontology_iri + inverseExistBinaryRoleName), IRI.create(ontology_iri + inverseEditedBinaryRoleName));
            manager.applyChanges(changes);
        }
    }

    @Override
    public void removeElement(ORMElement element) {

        ORMBinaryRole binaryRole = (ORMBinaryRole)element;
        String binaryRoleName = binaryRole.getName() + '.' + binaryRole.getSource().getName() + '.' + binaryRole.getTarget().getName();

        // Формируем название inverseRole
        String inverseBinaryRoleName = "inverse__" + binaryRoleName;
        if (!binaryRole.getInverseRoleName().isEmpty()) {
            inverseBinaryRoleName = binaryRole.getInverseRoleName() + '.' + binaryRole.getTarget().getName() + '.' + binaryRole.getSource().getName();;
        }

        // Находим и удаляем ObjectProperty
        OWLObjectProperty objProp = df.getOWLObjectProperty(IRI.create(ontology_iri + binaryRoleName));
        removeOWLEntity(objProp);
        OWLObjectProperty inverse_objProp = df.getOWLObjectProperty(IRI.create(ontology_iri + inverseBinaryRoleName));
        removeOWLEntity(inverse_objProp);
    }
}