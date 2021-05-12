package edu.stanford.bmir.protege.examples.view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import javax.swing.*;


import javafx.scene.layout.HBox;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFResource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс, отвечающий за правую панель вкладки
 */
public class Metrics extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(Metrics.class);

    // Элементы панели
    private JButton addClassButton = new JButton("Добавить класс");
    private JButton removeClassButton = new JButton("Удалить класс");
    private JButton createOntButton = new JButton("Заполнить онтологию");
    private JTextField textField = new JTextField();

    private JButton btn = new JButton("Получить аксиомы подклассы");

    private ActionListener addClassAction = e -> addEntityType();
    private ActionListener removeClassAction = e -> removeEntityType();
    private ActionListener createExampleOntologyAction = e -> createExampleOntology();

    private OWLModelManager modelManager; // Встроенный менеджер онтологии
    private OwlAPI ontology_manager; // Класс API
    private OWLModelManagerListener modelListener;
    private OWLOntologyChangeListener changeListener;

    private HashMap<String, String> orm_elem_dict = new HashMap<>();

//    private OWLModelManagerListener modelListener = event -> {
//        if (event.getType() == EventType.ACTIVE_ONTOLOGY_CHANGED) {
//            getSubAxioms();
//        }
//    };

    public Metrics(OWLModelManager modelManager) {
    	this.modelManager = modelManager;

        //modelManager.addListener(modelListener);

//        modelListener = new OWLModelManagerListener() {
//            public void handleChange(OWLModelManagerChangeEvent event) {
//                log.info("modelListener handleChange");
//                if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED) || event.isType(EventType.ONTOLOGY_RELOADED)) {
//                    // Clear
//                    //fireModelChangedEvent();
//                    log.info("Ontology changed");
//                }
//            }
//        };
//        this.modelManager.addListener(modelListener);


        ontology_manager = new OwlAPI(modelManager.getOWLOntologyManager());

        addClassButton.addActionListener(addClassAction);
        removeClassButton.addActionListener(removeClassAction);
        createOntButton.addActionListener(createExampleOntologyAction);
        btn.addActionListener(e -> getSubAxioms());

        // Формирование панели
        JPanel jp = new JPanel();
        jp.setSize(1000, 1000);
        textField.setMinimumSize(new Dimension(300, 30));
        textField.setText("NewClass");
        jp.add(textField);
        jp.add(addClassButton);
        jp.add(removeClassButton);
        jp.add(createOntButton);
        jp.add(btn);
        add(jp);

        changeListener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
                log.info("");
                HashMap<String, String> update_orm_elem_dict = ontology_manager.updateORM(changes);
                HashMap<String, String> copy_orm_elem_dict = new HashMap<>(orm_elem_dict);

                for (Map.Entry<String, String> orm_elem : copy_orm_elem_dict.entrySet()) {
                    if (!update_orm_elem_dict.containsKey(orm_elem.getKey())) {
                        orm_elem_dict.remove(orm_elem.getKey());
                    }
                }
                for (Map.Entry<String, String> orm_elem : update_orm_elem_dict.entrySet()) {
                    orm_elem_dict.put(orm_elem.getKey(), orm_elem.getValue());
                }

                log.info("=======================");
                log.info("Список элементов ORM:");
                for (Map.Entry<String, String> orm_elem : orm_elem_dict.entrySet()) {
                    log.info("Элемент: " + orm_elem.getKey() + ", тип: " + orm_elem.getValue());
                }
                log.info("=======================");
//                for (OWLOntologyChange changeItem : changes) {
//                    if (changeItem.isAddAxiom()) {
//                        log.info("Add Axiom");
//                    }
////                    if (changeItem.isAxiomChange()) {
////                        log.info("Change Axiom");
////                    }
//                    if (changeItem.isRemoveAxiom()) {
//                        log.info("Remove Axiom");
//                    }
//                    log.info(changeItem.getAxiom().getAxiomType().toString());
//                    log.info(changeItem.getAxiom().toString());
//                    log.info("----------------------------------------");
//                }
//                log.info("----------------------------------------");
//                log.info("----------------------------------------");
            }
        };
        this.modelManager.addOntologyChangeListener(changeListener);
    }
    
    public void dispose() {
        modelManager.removeListener(modelListener);
    }

    /**
     * Объявление EntityType
     */
    private void addEntityType() {

        String className = textField.getText();
        ontology_manager.declareEntityType(className);

    }

    /**
     * Удаление EntityType
     */
    private void removeEntityType() {

        String className = textField.getText();
        ontology_manager.removeEntityType(className);

    }

    /**
     * Создание онтологии с помощью OWLAPI
     */
    private void createExampleOntology() {

        ontology_manager.declareEntityType("Person");
        ontology_manager.declareEntityType("Male");
        ontology_manager.declareEntityType("Female");

        ontology_manager.declareUnaryRole("is_sportsman", "Person");

        ontology_manager.declareValueType("has_gender", "", "Person");

        ontology_manager.declareBinaryRole("uses", "used_by", "Male", "Female");

        //ontology_manager.declareSubtype("Male", "Person");
        //ontology_manager.declareSubtype("Female", "Male");

        //ontology_manager.removeEntityType("Male");
        //ontology_manager.removeEntityType("Person");
        //ontology_manager.removeEntityType("Female");

        //ontology_manager.removeValueType("has_gender.Person");

        //ontology_manager.removeUnaryRole("is_sportsman.Person");

        //ontology_manager.removeBinaryRole("uses.Male.Female");

        //ontology_manager.updateValueType("has_gender.Person", "has_gender_new", "","Male");

        //ontology_manager.updateUnaryRole("is_sportsman.Person", "is_sportsman_new", "Person1");

        //ontology_manager.updateBinaryRole("uses.Male.Female", "uses_new", "used_by_new", "Female", "Male");
    }

    // Проверка OWLRenamer
    private void renameOWLEntity() {
        ontology_manager.updateUnaryRole("is_sportsman.Person", "123", "");
    }
    
    // Проверка работы с reasoner
    private void getSubAxioms() {

        OWLReasoner reasoner = modelManager.getOWLReasonerManager().getCurrentReasoner();
//        log.info(reasoner.getReasonerName());
//        reasoner.getRootOntology().getAxioms(AxiomType.ANNOTATION_ASSERTION);

//        for (OWLSubClassOfAxiom subClassAxiom : reasoner.getRootOntology().ge) {
//            log.info("Hello");
//            log.info(subClassAxiom.toString());
//        }

//        ontology_manager.test();

        OWLOntology inferredOntology = modelManager.getOntologies().iterator().next();
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
        // we require all inferred stuff except for disjoints...
        gens.add(new InferredClassAssertionAxiomGenerator());
        gens.add(new InferredDataPropertyCharacteristicAxiomGenerator());
        gens.add(new InferredEquivalentClassAxiomGenerator());
        gens.add(new InferredEquivalentDataPropertiesAxiomGenerator());
        gens.add(new InferredEquivalentObjectPropertyAxiomGenerator());
        gens.add(new InferredInverseObjectPropertiesAxiomGenerator());
        gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
        gens.add(new InferredPropertyAssertionGenerator());
        gens.add(new InferredSubClassAxiomGenerator());
        gens.add(new InferredSubDataPropertyAxiomGenerator());
        gens.add(new InferredSubObjectPropertyAxiomGenerator());
        // now create the target ontology and save
        OWLOntologyManager inferredManager = inferredOntology.getOWLOntologyManager();
        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
        //iog.fillOntology(OWLManager.getOWLDataFactory(), inferredOntology);

//        for (OWLSubClassOfAxiom subClassAxiom : inferredOntology.getAxioms(AxiomType.SUBCLASS_OF)) {
//            log.info("Hello");
//            log.info(subClassAxiom.toString());
//        }

//        for (OWLSubClassOfAxiom subClassAxiom : new HermitReasoningService().run(modelManager.getOntologies().iterator().next(),gens)) {
//            log.info("Hello");
//            log.info(subClassAxiom.toString());
//        }

        for (OWLClass c : modelManager.getOntologies().iterator().next().getClassesInSignature()) {
            // the boolean argument specifies direct subclasses
            NodeSet<OWLClass> subClasses = reasoner.getSubClasses(c, true);
            for (OWLClass subClass : subClasses.getFlattened()) {
                log.info(subClass.getIRI().getShortForm() + " subclass of " + c.getIRI().getShortForm());
            }
        }

    }

    // Проверка reasoner и вывод ошибок, если есть
    private void checkOntology() {

        OWLOntology o = modelManager.getOntologies().iterator().next();
        // Create a reasoner; it will include the imports closure
        OWLReasoner reasoner =  modelManager.getOWLReasonerManager().getCurrentReasoner();
        // Ask the reasoner to precompute some inferences
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        // We can determine if the ontology is actually consistent
        log.info(""+reasoner.isConsistent());
        // get a list of unsatisfiable classes
        Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
        System.out.println("Unsatisfiable classes:");
        // leave owl:Nothing out
        for (OWLClass cls : bottomNode.getEntitiesMinusBottom()) {
            log.info(cls.getIRI().getShortForm());
        }


    }
}
