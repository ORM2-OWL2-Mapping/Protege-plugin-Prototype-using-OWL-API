import ForOntologyTesting.TestBase;
import ORMModel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.vstu.nodelinkdiagram.*;
import org.vstu.nodelinkdiagram.statuses.CommitStatus;
import org.vstu.orm2diagram.model.ORM_DiagramFactory;
import org.vstu.orm2diagram.model.ORM_EntityType;
import org.vstu.orm2diagram.model.ORM_Subtyping;
import org.vstu.orm2diagram.model.ORM_ValueType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

class Test_DiagramClient implements DiagramClient {
}

public class TestingClass extends TestBase {

    protected OwlAPI ontologyManager;
    protected String TEST_DIR;

    protected Map<OWLEntity, IRI> getRenameOWLEntities(OWLOntology ontology, IRI new_ontology_IRI) {

        Map<OWLEntity, IRI> changeMap = new HashMap<>();
        new_ontology_IRI = IRI.create(new_ontology_IRI.toString() + "#");
//        Set<OWLClass> ontology_classes = ontology.getClassesInSignature();
//        ontology_classes.remove();
        for (OWLClass ontology_class : ontology.getClassesInSignature()) {
            if (!ontology_class.getIRI().getShortForm().equals("Thing")) {
                changeMap.put(ontology_class, IRI.create(new_ontology_IRI + ontology_class.getIRI().getShortForm()));
            }
        }

        for (OWLObjectProperty ontology_obj_prop : ontology.getObjectPropertiesInSignature()) {
            if (!ontology_obj_prop.getIRI().getShortForm().equals("topObjectProperty")) {
                changeMap.put(ontology_obj_prop, IRI.create(new_ontology_IRI  + ontology_obj_prop.getIRI().getShortForm()));
            }
        }

        for (OWLDataProperty ontology_data_prop : ontology.getDataPropertiesInSignature()) {
            if (!ontology_data_prop.getIRI().getShortForm().equals("topDataProperty")) {
                changeMap.put(ontology_data_prop, IRI.create(new_ontology_IRI + ontology_data_prop.getIRI().getShortForm()));
            }
        }

        return changeMap;
    }

    protected void compareOntologies(String ontology_file_1, String ontology_file_2) throws Exception {

        OWLOntologyManager manager_1 = OWLManager.createOWLOntologyManager();
        File file = new File(TEST_DIR + ontology_file_1);
        OWLOntology ontology_1 = manager_1.loadOntologyFromOntologyDocument(file);
        IRI ontology_1_iri = ontology_1.getOntologyID().getOntologyIRI().get();

        OWLOntologyManager manager_2 = OWLManager.createOWLOntologyManager();
        File file_2 = new File(TEST_DIR + ontology_file_2);
        OWLOntology ontology_2 = manager_2.loadOntologyFromOntologyDocument(file_2);

        if (!ontology_2.getOntologyID().getOntologyIRI().get().equals(ontology_1_iri)) {

            OWLEntityRenamer owlEntityRenamer = new OWLEntityRenamer(manager_2, manager_2.getOntologies());
            Map<OWLEntity, IRI> changeMap = getRenameOWLEntities(ontology_2, ontology_1_iri);
            List<OWLOntologyChange> changes = owlEntityRenamer.changeIRI(changeMap);
            manager_2.applyChanges(changes);
            ontology_2 = manager_2.createOntology(ontology_2.getAxioms(), ontology_1_iri);
        }

        boolean res_1_2 = equal(ontology_1, ontology_2);
        assertTrue(res_1_2);
        boolean res_2_1 = equal(ontology_2, ontology_1);
        assertTrue(res_2_1);
    }

    protected void saveOntologyInFile(OWLOntology ontology, String filename) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File fileformated = new File(TEST_DIR + filename);
        manager.saveOntology(ontology, new OWLXMLDocumentFormat(), IRI.create(fileformated.toURI()));
    }

    protected String makeActualOntologyFilename(String testName) {
        return testName + "/actual_" + testName + ".owl";
    }

    protected String makeExpectedOntologyFilename(String testName) {
        return testName + "/expected_" + testName + ".owl";
    }

    protected String makePreparedOntologyFilename(String testName) {
        return TEST_DIR + testName + "/prepared_" + testName + ".owl";
    }

    protected MainDiagramModel mainModel;
    protected ClientDiagramModel sourceClientModel, myModel;

//    @BeforeEach
//    public void beforeTest() throws Exception {
//        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
//        clientModel = mainModel.registerClient(new Test_DiagramClient());
//    }

    protected class ElementsPresenter implements ClientDiagramModelListener {

        @Override
        public void isUpdated(ModelUpdateEvent e) {
            if (e.getSuccessfulUpdateStatus() == ModelUpdateEvent.SuccessfulUpdateStatus.SuccessCommit &&
                    e.getUpdateInitiator() == ModelUpdateEvent.UpdateInitiator.AnotherOne) {

                ClientDiagramModel clientModel = (ClientDiagramModel) e.getSource();

                List<DiagramElement> notPresentedElements =
                        clientModel.getElements(CommitStatus.NotPresented, DiagramElement.class).collect(Collectors.toList());
                notPresentedElements.forEach(clientModel::markElementAsPresented);
            }
        }
    }

//    public void runTestX() throws Exception {
//
//        ORMModel model = new ORMModel();
//
//        ORMEntityType person = new ORMEntityType("Person");
//        person.setUpdateStatus("Created");
//        model.addElement(person, "EntityType");
//
//        ORMEntityType male = new ORMEntityType("Male");
//        male.setUpdateStatus("Created");
//        model.addElement(male, "EntityType");
//
//        ORMEntityType female = new ORMEntityType("Female");
//        female.setUpdateStatus("Created");
//        model.addElement(female, "EntityType");
//
//        try {
//            ORM_OWL_Mapper.convertORMtoOWL(model, "");
//        } catch (Exception e) {
//
//        }
//
//        compareOntologies("ORM_test2_expect.owl", "result.owl");
//
////            try {
////                compareOntologies("ORM_test2_expect.owl", "result.owl");
////                return true;
////            } catch (Exception e) {
////                e.printStackTrace();
////                return false;
////            }
//    }
}
