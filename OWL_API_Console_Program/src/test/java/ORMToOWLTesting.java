import ForOntologyTesting.TestBase;


import org.junit.jupiter.api.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class ORMToOWLTesting extends TestBase {

    private OwlAPI ontologyManager;
    private final String EXPECTED_ONTOLOGIES_DIR = System.getProperty("user.dir") + "/src/test/java/ExpectedOntologies/";
    private final String ACTUAL_ONTOLOGIES_DIR = System.getProperty("user.dir") + "/src/test/java/ActualOntologies/";
    private final String FOR_TESTING_ONTOLOGIES_DIR = System.getProperty("user.dir") + "/src/test/java/UseForTestingOntologies/";

    private Map<OWLEntity, IRI> getRenameOWLEntities(OWLOntology ontology, IRI new_ontology_IRI) {

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

    private void compareOntologies(String ontology_file_1, String ontology_file_2) throws Exception {

        OWLOntologyManager manager_1 = OWLManager.createOWLOntologyManager();
        File file = new File(EXPECTED_ONTOLOGIES_DIR + ontology_file_1);
        OWLOntology ontology_1 = manager_1.loadOntologyFromOntologyDocument(file);
        IRI ontology_1_iri = ontology_1.getOntologyID().getOntologyIRI().get();

        OWLOntologyManager manager_2 = OWLManager.createOWLOntologyManager();
        File file_2 = new File(ACTUAL_ONTOLOGIES_DIR + ontology_file_2);
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



    @BeforeEach
    public void beforeTest() throws Exception {
        ontologyManager = new OwlAPI("http://www.semanticweb.org/example");
    }


    @Nested
    @DisplayName("01 - Тестирование EntityType")
    class EntityTypeTesting {

        @Test
        @DisplayName("01 - Добавление единственного EntityType в пустую онтологию")
        public void declareOneEntityTypeInEmptyOntology(TestInfo testInfo) throws Exception {
            System.out.println(testInfo.getDisplayName());
            ontologyManager.declareEntityType("Class_A");

            String filename = "test01.owl";
            ontologyManager.saveOntologyInFile(ACTUAL_ONTOLOGIES_DIR + filename);

            compareOntologies(filename, filename);
        }

        @Test
        @DisplayName("02 - Добавление двух EntityType в пустую онтологию")
        public void declareTwoEntityTypeInEmptyOntology() throws Exception {

            System.out.println(System.getProperty("user.dir"));

            ontologyManager.declareEntityType("Class_A");
            ontologyManager.declareEntityType("Class_B");

            String filename = "test02.owl";
            ontologyManager.saveOntologyInFile(ACTUAL_ONTOLOGIES_DIR + filename);

            compareOntologies(filename, filename);
        }

        @Test
        @DisplayName("03 - Добавление одного EntityType в существующую онтологию, cодержащую один EntityType")
        public void declareOneEntityTypeInExistOntologyWithOneEntityType() throws Exception {

            String filename = "test03.owl";
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(FOR_TESTING_ONTOLOGIES_DIR + filename);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            ontologyManager = new OwlAPI(manager);

            ontologyManager.declareEntityType("Class_B");

            ontologyManager.saveOntologyInFile(ACTUAL_ONTOLOGIES_DIR + filename);

            compareOntologies(filename, filename);
        }

        @Test
        @DisplayName("04 - Добавление одного EntityType в существующую онтологию, cодержащую два EntityType")
        public void declareOneEntityTypeInExistOntologyWithTwoEntityType() throws Exception {

            String filename = "test04.owl";
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(FOR_TESTING_ONTOLOGIES_DIR + filename);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            ontologyManager = new OwlAPI(manager);

            ontologyManager.declareEntityType("Class_C");

            ontologyManager.saveOntologyInFile(ACTUAL_ONTOLOGIES_DIR + filename);

            compareOntologies(filename, filename);
        }

        @Test
        @DisplayName("05 - Удаление одного EntityType, онтология становится пустой")
        public void removeOneEntityTypeInExistOntologyWithOneEntityType() throws Exception {

            String filename = "test05.owl";
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(FOR_TESTING_ONTOLOGIES_DIR + filename);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            ontologyManager = new OwlAPI(manager);

            ontologyManager.removeEntityType("Class_A");

            ontologyManager.saveOntologyInFile(ACTUAL_ONTOLOGIES_DIR + filename);

            compareOntologies(filename, filename);
        }

        @Test
        @DisplayName("06 - Удаление одного EntityType, в онтологии остаётся один EntityType")
        public void removeOneEntityTypeInExistOntologyWithTwoEntityType() throws Exception {

            String filename = "test06.owl";
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(FOR_TESTING_ONTOLOGIES_DIR + filename);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            ontologyManager = new OwlAPI(manager);

            ontologyManager.removeEntityType("Class_B");

            ontologyManager.saveOntologyInFile(ACTUAL_ONTOLOGIES_DIR + filename);

            compareOntologies(filename, filename);
        }

        @Test
        @DisplayName("07 - Удаление одного EntityType, в онтологии остаётся два EntityType")
        public void removeOneEntityTypeInExistOntologyWithThreeEntityType() throws Exception {

            String filename = "test07.owl";
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(FOR_TESTING_ONTOLOGIES_DIR + filename);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            ontologyManager = new OwlAPI(manager);

            ontologyManager.removeEntityType("Class_C");

            ontologyManager.saveOntologyInFile(ACTUAL_ONTOLOGIES_DIR + filename);

            compareOntologies(filename, filename);
        }

        @Test
        @DisplayName("08 - Обновление одного EntityType, в онтологии один EntityType")
        public void updateOneEntityTypeInExistOntologyWithOneEntityType() throws Exception {

            String filename = "test08.owl";
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(FOR_TESTING_ONTOLOGIES_DIR + filename);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            ontologyManager = new OwlAPI(manager);

            ontologyManager.updateEntityType("Class_A", "Class_D");

            ontologyManager.saveOntologyInFile(ACTUAL_ONTOLOGIES_DIR + filename);

            compareOntologies(filename, filename);
        }

        @Test
        @DisplayName("09 - Обновление одного EntityType, в онтологии два EntityType")
        public void updateOneEntityTypeInExistOntologyWithTwoEntityType() throws Exception {

            String filename = "test09.owl";
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(FOR_TESTING_ONTOLOGIES_DIR + filename);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            ontologyManager = new OwlAPI(manager);

            ontologyManager.updateEntityType("Class_A", "Class_D");

            ontologyManager.saveOntologyInFile(ACTUAL_ONTOLOGIES_DIR + filename);

            compareOntologies(filename, filename);
        }

        @Test
        @DisplayName("10 - Обновление одного EntityType, в онтологии три EntityType")
        public void updateOneEntityTypeInExistOntologyWithThreeEntityType() throws Exception {

            String filename = "test10.owl";
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(FOR_TESTING_ONTOLOGIES_DIR + filename);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            ontologyManager = new OwlAPI(manager);

            ontologyManager.updateEntityType("Class_A", "Class_D");

            ontologyManager.saveOntologyInFile(ACTUAL_ONTOLOGIES_DIR + filename);

            compareOntologies(filename, filename);
        }
    }

    @Nested
    @DisplayName("02 - Тестирование ValueType")
    class ValueTypeTesting {

        @Test
        @DisplayName("11 - Добавление одного ValueType в существующую онтологию, cодержащую один EntityType")
        public void declareOneValueTypeInExistOntologyWithOneEntityType() throws Exception {

            String filename = "test11.owl";
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(FOR_TESTING_ONTOLOGIES_DIR + filename);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            ontologyManager = new OwlAPI(manager);

            ontologyManager.declareValueType("ValueType_A", "", "Class_A");

            ontologyManager.saveOntologyInFile(ACTUAL_ONTOLOGIES_DIR + filename);

            compareOntologies(filename, filename);
        }

        @Test
        @DisplayName("12 - Добавление одного ValueType в существующую онтологию, cодержащую два EntityType")
        public void declareOneValueTypeInExistOntologyWithTwoEntityType() throws Exception {

            String filename = "test12.owl";
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(FOR_TESTING_ONTOLOGIES_DIR + filename);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            ontologyManager = new OwlAPI(manager);

            ontologyManager.declareValueType("ValueType_A", "", "Class_A");

            ontologyManager.saveOntologyInFile(ACTUAL_ONTOLOGIES_DIR + filename);

            compareOntologies(filename, filename);
        }

        @Test
        @DisplayName("13 - Добавление второго ValueType к единственному EntityType")
        public void declareSecondValueTypeInExistOntologyWithOneEntityType() throws Exception {

            String filename = "test13.owl";
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(FOR_TESTING_ONTOLOGIES_DIR + filename);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            ontologyManager = new OwlAPI(manager);

            ontologyManager.declareValueType("ValueType_B", "", "Class_A");

            ontologyManager.saveOntologyInFile(ACTUAL_ONTOLOGIES_DIR + filename);

            compareOntologies(filename, filename);
        }

        @Test
        @DisplayName("14 - Добавление второго ValueType ко второму EntityType")
        public void declareSecondValueTypeInExistOntologyWithTwoEntityType() throws Exception {

            String filename = "test14.owl";
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(FOR_TESTING_ONTOLOGIES_DIR + filename);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            ontologyManager = new OwlAPI(manager);

            ontologyManager.declareValueType("ValueType_B", "", "Class_B");

            ontologyManager.saveOntologyInFile(ACTUAL_ONTOLOGIES_DIR + filename);

            compareOntologies(filename, filename);
        }
    }
}
