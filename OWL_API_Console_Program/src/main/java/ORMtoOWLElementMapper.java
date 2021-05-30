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

    protected ORMModel model;

    public ORMtoOWLElementMapper (OWLOntologyManager manager) {
        this.manager = manager;
        this.ontology = manager.getOntologies().iterator().next();
        this.ontology_iri = IRI.create(ontology.getOntologyID().getOntologyIRI().get().toString() + "#");
        this.entityRemover = new OWLEntityRemover(Collections.singleton(this.ontology));
        this.owlEntityRenamer = new OWLEntityRenamer(this.manager, this.manager.getOntologies());
    }

    public ORMtoOWLElementMapper (OWLOntologyManager manager, ORMModel model) {
        this.model = model;
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

    protected Set<OWLClass> getOWLClassesWhichEntityTypes() {
        Set<OWLClass> owlClassesWhichEntityTypes = new HashSet<>();
        for (ORMElement entityType : model.getElements("EntityType")) {
            OWLClass owlClass = df.getOWLClass(IRI.create(ontology_iri + entityType.getName()));
            owlClassesWhichEntityTypes.add(owlClass);
        }
        return owlClassesWhichEntityTypes;
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

    public ORMtoOWLEntityTypeMapper(OWLOntologyManager manager, ORMModel model) {
        super(manager, model);
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

    public Set<ORMEntityType> getElementsFromOntology() {

        Set<OWLClass> owlClassesWhichEntityTypes = new HashSet<>();
        Set<OWLClass> mainClasses = new HashSet<>();

        // Ищем утверждение EquivalentClasses у owl:Thing
        for (OWLAxiom axiom : ontology.getAxioms(df.getOWLThing())) {
            if (axiom.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
                mainClasses = axiom.getClassesInSignature();
                mainClasses.remove(df.getOWLThing());
                break;
            }
        }

        // Если в утверждении EquivalentClasses только один класс, то добавляем только его
        if (mainClasses.size() == 1) {
            owlClassesWhichEntityTypes.add(mainClasses.iterator().next());
        }
        // Если больше 1, проверяем, имеет ли текущий класс Disjoint с другими mainClasses
        else if (mainClasses.size() > 1) {
            OWLDisjointClassesAxiom disjointClassesAxiom = df.getOWLDisjointClassesAxiom(mainClasses);
            for (OWLClass mainClass : mainClasses) {
                boolean isEntityType = ontology.getDisjointClassesAxioms(mainClass).contains(disjointClassesAxiom);
                if (isEntityType) {
                    owlClassesWhichEntityTypes.add(mainClass);
                }
            }
        }

        mainClasses = getOWLThingSubClasses();

        // Если в онтологии есть не корневые классы и их название не начинается с "not__" и "union__", то добавляем
        for (OWLClass owlClass : ontology.getClassesInSignature()) {
            if (owlClass.equals(df.getOWLThing()) || mainClasses.contains(owlClass)) { continue; }
            String className = owlClass.getIRI().getShortForm();
            if (!owlClassesWhichEntityTypes.contains(owlClass) && !className.startsWith("not__") && !className.startsWith("union__")) {
                owlClassesWhichEntityTypes.add(owlClass);
            }
        }

        // Создаём EntityType на основе найденных классов
        Set<ORMEntityType> entityTypes = new HashSet<>();
        for (OWLClass owlClass : owlClassesWhichEntityTypes) {
            String className = owlClass.getIRI().getShortForm();
            entityTypes.add(new ORMEntityType(className));
        }

        return entityTypes;
    }
}

class ORMtoOWLSubtypingMapper extends ORMtoOWLElementMapper {


    public ORMtoOWLSubtypingMapper(OWLOntologyManager manager) {
        super(manager);
    }

    public ORMtoOWLSubtypingMapper(OWLOntologyManager manager, ORMModel model) {
        super(manager, model);
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

    public Set<ORMSubtyping> getElementsFromOntology() {

        Set<ORMSubtyping> subtypings = new HashSet<>();
        Set<OWLClass> owlClassesWhichEntityTypes = getOWLClassesWhichEntityTypes();

        for (OWLClass owlClass : owlClassesWhichEntityTypes) {

            Set<OWLClass> subclasses = getSubClasses(owlClass);

            if (subclasses.size() != 2) { continue; }

            OWLDisjointClassesAxiom disjointAxiom = df.getOWLDisjointClassesAxiom(subclasses);
            boolean disjointIsFind = ontology.getDisjointClassesAxioms(subclasses.stream().findFirst().get()).contains(disjointAxiom);

            OWLEquivalentClassesAxiom equivalentAxiom = df.getOWLEquivalentClassesAxiom(owlClass, df.getOWLObjectUnionOf(subclasses));
            boolean equivalentIsFind = ontology.getEquivalentClassesAxioms(owlClass).contains(equivalentAxiom);

            if (!(equivalentIsFind && disjointIsFind)) { continue; }

            boolean not__isFind = false;
            boolean oneChild__isFind = true;
            boolean union__isFind = false;

            Set<OWLClass> unionSubclasses = new HashSet<>();

            for (OWLClass subclass : subclasses) {
                if (subclass.getIRI().getShortForm().startsWith("not__")) {
                    not__isFind = true;
                }
                else if (subclass.getIRI().getShortForm().startsWith("union__")) {
                    oneChild__isFind = false;
                    unionSubclasses = getSubClasses(subclass);
                    equivalentAxiom = df.getOWLEquivalentClassesAxiom(subclass, df.getOWLObjectUnionOf(unionSubclasses));
                    boolean unionEquivalentIsFind = ontology.getEquivalentClassesAxioms(subclass).contains(equivalentAxiom);
                    disjointAxiom = df.getOWLDisjointClassesAxiom(unionSubclasses);
                    boolean unionDisjointIsFind = ontology.getDisjointUnionAxioms(unionSubclasses.stream().findFirst().get()).contains(disjointAxiom);
                    if (unionEquivalentIsFind && !unionDisjointIsFind) {
                        union__isFind = true;
                    }
                } else {
                    unionSubclasses.add(subclass);
                }
            }

            // Если класс-родитель имеет два подкласса: not__ и (union__ или единственный класс), то добавляем Subtyping
            if (not__isFind && (oneChild__isFind || union__isFind)) {
                ORMEntityType targetSubtyping = new ORMEntityType(owlClass.getIRI().getShortForm());
                try {
                    targetSubtyping = (ORMEntityType)model.getElements("EntityType")
                            .stream().filter(
                                    e -> e.getName().equals(owlClass.getIRI().getShortForm())
                            ).findFirst().get();
                } catch (Exception e) {

                }
                for (OWLClass unionSubclass : unionSubclasses) {
                    ORMEntityType sourceSubtyping = new ORMEntityType(unionSubclass.getIRI().getShortForm());
                    try {
                        sourceSubtyping = (ORMEntityType)model.getElements("EntityType")
                                .stream().filter(
                                        e -> e.getName().equals(unionSubclass.getIRI().getShortForm())
                                ).findFirst().get();
                    } catch (Exception e) {

                    }
                    ORMSubtyping subtyping = new ORMSubtyping(sourceSubtyping, targetSubtyping);
                    subtypings.add(subtyping);
                }
            }
        }

        return subtypings;
    }
}

class ORMtoOWLValueTypeMapper extends ORMtoOWLElementMapper {

    public ORMtoOWLValueTypeMapper(OWLOntologyManager manager) {
        super(manager);
    }

    public ORMtoOWLValueTypeMapper(OWLOntologyManager manager, ORMModel model) {
        super(manager, model);
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

    public Set<ORMValueType> getElementsFromOntology() {

        Set<ORMValueType> valueTypes = new HashSet<>();
        Set<OWLClass> owlClassesWhichEntityTypes = getOWLClassesWhichEntityTypes();

        OWLDatatype stringDatatype = df.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI());

        for (OWLDataProperty dataProp : ontology.getDataPropertiesInSignature()) {
            OWLClass domainClass = null;
            boolean domainIsEntityType = false;
            boolean rangeIsString = false;
            boolean nameIsCorrect = false;

            for (OWLAxiom axiom : ontology.getAxioms(dataProp)) {
                if (axiom.getAxiomType() == AxiomType.DATA_PROPERTY_DOMAIN) {
                    domainClass = axiom.getClassesInSignature().stream().findFirst().get();
                    if (owlClassesWhichEntityTypes.contains(domainClass)) {
                        domainIsEntityType = true;
                    }
                } else if (axiom.getAxiomType() == AxiomType.DATA_PROPERTY_RANGE) {
                    if (axiom.getDatatypesInSignature().size() == 1
                            && axiom.getDatatypesInSignature().contains(stringDatatype)) {
                        rangeIsString = true;
                    }
                }
            }

            String dataPropName = dataProp.getIRI().getShortForm();
            // splitDataPropName: 0 - название связи, 1 - название OWLClass, 2 - название ValueType
            String[] splitDataPropName = dataPropName.split("\\.");
            if (domainIsEntityType && splitDataPropName[1].equals(domainClass.getIRI().getShortForm())) {
                nameIsCorrect = true;
            }

            if (domainIsEntityType && rangeIsString && nameIsCorrect) {
                String domainClassName = domainClass.getIRI().getShortForm();
                ORMEntityType sourceValueType = new ORMEntityType(domainClassName);
                try {
                    sourceValueType = (ORMEntityType)model.getElements("EntityType")
                            .stream().filter(
                                    e -> e.getName().equals(domainClassName)
                            ).findFirst().get();
                } catch (Exception e) {

                }
                ORMValueType valueType = new ORMValueType(splitDataPropName[0], splitDataPropName[2], sourceValueType);
                valueTypes.add(valueType);
            }
        }

        return valueTypes;
    }
}

class ORMtoOWLUnaryRoleMapper extends ORMtoOWLElementMapper {

    public ORMtoOWLUnaryRoleMapper(OWLOntologyManager manager) {
        super(manager);
    }

    public ORMtoOWLUnaryRoleMapper(OWLOntologyManager manager, ORMModel model) {
        super(manager, model);
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

    public Set<ORMUnaryRole> getElementsFromOntology() {

        Set<ORMUnaryRole> unaryRoles = new HashSet<>();

        Set<OWLClass> owlClassesWhichEntityTypes = getOWLClassesWhichEntityTypes();

        OWLDatatype booleanDatatype = df.getOWLDatatype(OWL2Datatype.XSD_BOOLEAN.getIRI());

        for (OWLDataProperty dataProp : ontology.getDataPropertiesInSignature()) {
            OWLClass domainClass = null;
            boolean domainIsEntityType = false;
            boolean rangeIsBoolean = false;
            boolean nameIsCorrect = false;

            for (OWLAxiom axiom : ontology.getAxioms(dataProp)) {
                if (axiom.getAxiomType() == AxiomType.DATA_PROPERTY_DOMAIN) {
                    domainClass = axiom.getClassesInSignature().stream().findFirst().get();
                    if (owlClassesWhichEntityTypes.contains(domainClass)) {
                        domainIsEntityType = true;
                    }
                } else if (axiom.getAxiomType() == AxiomType.DATA_PROPERTY_RANGE) {
                    if (axiom.getDatatypesInSignature().size() == 1
                            && axiom.getDatatypesInSignature().contains(booleanDatatype)) {
                        rangeIsBoolean = true;
                    }
                }
            }

            String dataPropName = dataProp.getIRI().getShortForm();
            // splitDataPropName: 0 - название связи, 1 - название OWLClass
            String[] splitDataPropName = dataPropName.split("\\.");
            if (domainIsEntityType && splitDataPropName[1].equals(domainClass.getIRI().getShortForm())) {
                nameIsCorrect = true;
            }

            if (domainIsEntityType && rangeIsBoolean && nameIsCorrect) {
                String domainClassName = domainClass.getIRI().getShortForm();
                ORMEntityType sourceUnaryRole = new ORMEntityType(domainClassName);
                try {
                    sourceUnaryRole = (ORMEntityType)model.getElements("EntityType")
                            .stream().filter(
                                    e -> e.getName().equals(domainClassName)
                            ).findFirst().get();
                } catch (Exception e) {

                }
                ORMUnaryRole unaryRole = new ORMUnaryRole(splitDataPropName[0], sourceUnaryRole);
                unaryRoles.add(unaryRole);
            }
        }

        return unaryRoles;
    }
}

class ORMtoOWLBinaryRoleMapper extends ORMtoOWLElementMapper {

    public ORMtoOWLBinaryRoleMapper(OWLOntologyManager manager) {
        super(manager);
    }

    public ORMtoOWLBinaryRoleMapper(OWLOntologyManager manager, ORMModel model) {
        super(manager, model);
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
        if (!editedBinaryRole.getInverseRoleName().isEmpty()) {
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

    public Set<ORMBinaryRole> getElementsFromOntology() {

        Set<ORMBinaryRole> binaryRoles = new HashSet<>();

        Set<OWLClass> owlClassesWhichEntityTypes = getOWLClassesWhichEntityTypes();
        Set<OWLObjectProperty>findObjProps = new HashSet<>();

        for (OWLObjectProperty objProp : ontology.getObjectPropertiesInSignature()) {

            if (findObjProps.contains(objProp)) { continue; }

            OWLClass domainClass = null;
            OWLClass rangeClass = null;
            OWLObjectProperty inverseObjProp = null;
            boolean domainIsEntityType = false;
            boolean rangeIsEntityType = false;
            boolean inverseObjPropIsFind = false;
            boolean nameIsCorrect = false;

            for (OWLAxiom axiom : ontology.getAxioms(objProp)) {
                if (axiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_DOMAIN) {
                    domainClass = axiom.getClassesInSignature().stream().findFirst().get();
                    if (owlClassesWhichEntityTypes.contains(domainClass)) {
                        domainIsEntityType = true;
                    }
                } else if (axiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_RANGE) {
                    rangeClass = axiom.getClassesInSignature().stream().findFirst().get();
                    if (owlClassesWhichEntityTypes.contains(rangeClass)) {
                        rangeIsEntityType = true;
                    }
                } else if (axiom.getAxiomType() == AxiomType.INVERSE_OBJECT_PROPERTIES) {
                    inverseObjProp = axiom.getObjectPropertiesInSignature()
                            .stream().filter(e -> !e.equals(objProp)).findFirst().get();
                    inverseObjPropIsFind = true;
                }
            }

            String objPropName = objProp.getIRI().getShortForm();
            // splitDataPropName: 0 - название связи, 1 - название domainOWLClass, 2 - название rangeOWLClass
            String[] splitObjPropName = objPropName.split("\\.");
            if (splitObjPropName[0].startsWith("inverse__")) {
                String tmp = splitObjPropName[1];
                splitObjPropName[1] = splitObjPropName[2];
                splitObjPropName[2] = tmp;
            }
            if (domainIsEntityType && rangeIsEntityType
                    && splitObjPropName[1].equals(domainClass.getIRI().getShortForm())
                    && splitObjPropName[2].equals(rangeClass.getIRI().getShortForm())) {
                nameIsCorrect = true;
            }

            if (!(domainIsEntityType && rangeIsEntityType && inverseObjPropIsFind && nameIsCorrect)) { continue; }

            boolean inverseDomainIsEntityType = false;
            boolean inverseRangeIsEntityType = false;
            boolean inverseNameIsCorrect = false;
            OWLClass inverseDomainClass = null;
            OWLClass inverseRangeClass = null;
            for (OWLAxiom axiom : ontology.getAxioms(inverseObjProp)) {
                if (axiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_DOMAIN) {
                    inverseDomainClass = axiom.getClassesInSignature().stream().findFirst().get();
                    if (inverseDomainClass.equals(rangeClass)) {
                        inverseDomainIsEntityType = true;
                    }
                } else if (axiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_RANGE) {
                    inverseRangeClass = axiom.getClassesInSignature().stream().findFirst().get();
                    if (inverseRangeClass.equals(domainClass)) {
                        inverseRangeIsEntityType = true;
                    }
                }
            }

            String inverseObjPropName = inverseObjProp.getIRI().getShortForm();
            // splitDataPropName: 0 - название связи, 1 - название domainOWLClass, 2 - название rangeOWLClass
            String[] splitInverseObjPropName = inverseObjPropName.split("\\.");
            if (splitInverseObjPropName[1].equals(rangeClass.getIRI().getShortForm())
                    && splitInverseObjPropName[2].equals(domainClass.getIRI().getShortForm())) {
                inverseNameIsCorrect = true;
            }

            if (inverseDomainIsEntityType && inverseRangeIsEntityType && inverseNameIsCorrect) {

                String domainClassName = domainClass.getIRI().getShortForm();
                ORMEntityType sourceBinaryRole = new ORMEntityType(domainClassName);
                try {
                    sourceBinaryRole = (ORMEntityType)model.getElements("EntityType")
                            .stream().filter(
                                    e -> e.getName().equals(domainClassName)
                            ).findFirst().get();
                } catch (Exception ignored) {

                }
                String rangeClassName = rangeClass.getIRI().getShortForm();
                ORMEntityType targetBinaryRole = new ORMEntityType(rangeClassName);
                try {
                    targetBinaryRole = (ORMEntityType)model.getElements("EntityType")
                            .stream().filter(
                                    e -> e.getName().equals(rangeClassName)
                            ).findFirst().get();
                } catch (Exception ignored) {

                }
//                ORMBinaryRole binaryRole = new ORMBinaryRole(splitObjPropName[0], sourceBinaryRole, targetBinaryRole);
//                if (!splitInverseObjPropName[0].startsWith("inverse__")) {
//                    binaryRole = new ORMBinaryRole(splitObjPropName[0], sourceBinaryRole, targetBinaryRole, splitInverseObjPropName[0]);
//                }

                findObjProps.add(objProp);
                findObjProps.add(inverseObjProp);
                ORMBinaryRole binaryRole = new ORMBinaryRole(splitObjPropName[0], sourceBinaryRole, targetBinaryRole, splitInverseObjPropName[0]);
                binaryRoles.add(binaryRole);
                ORMBinaryRole inverseBinaryRole = new ORMBinaryRole(splitInverseObjPropName[0], targetBinaryRole, sourceBinaryRole, splitObjPropName[0]);
                binaryRoles.add(inverseBinaryRole);
            }
        }

        return binaryRoles;
    }
}