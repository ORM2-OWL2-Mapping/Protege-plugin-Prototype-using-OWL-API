import ForOntologyTesting.TestBase;
import ORMModel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRenamer;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ORM_OWL_Testing extends TestBase {

    private ORMModel model;
    private String TEST_DIR = System.getProperty("user.dir") + "/tests/ORM_to_OWL_test/";

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

    private void saveOntologyInFile(OWLOntology ontology, String filename) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File fileformated = new File(TEST_DIR + filename);
        manager.saveOntology(ontology, new OWLXMLDocumentFormat(), IRI.create(fileformated.toURI()));
    }

    private String makeActualOntologyFilename(String testName) {
        return testName + "/actual_" + testName + ".owl";
    }

    private String makeExpectedOntologyFilename(String testName) {
        return testName + "/expected_" + testName + ".owl";
    }

    private String makePreparedOntologyFilename(String testName) {
        return TEST_DIR + testName + "/prepared_" + testName + ".owl";
    }


    @BeforeEach
    public void beforeTest() throws Exception {
        model = new ORMModel();
    }


    @Nested
    @DisplayName("01 - Тестирование EntityType")
    class EntityTypeTesting {

        @Test
        @DisplayName("01 - Добавление EntityType в пустую онтологию")
        public void test01() throws Exception {

            String testName = "test01";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Created");
            model.addElement(person, "EntityType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, "");
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("02 - Добавление EntityType в онтологию с единственным EntityType")
        public void test02() throws Exception {

            String testName = "test02";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Created");
            model.addElement(female, "EntityType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("03 - Добавление EntityType в онтологию с двумя EntityType")
        public void test03() throws Exception {

            String testName = "test03";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Stable");
            model.addElement(female, "EntityType");

            ORMEntityType male = new ORMEntityType("Male");
            male.setUpdateStatus("Created");
            model.addElement(male, "EntityType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("04 - Обновление одного из трёх EntityType в онтологии")
        public void test04() throws Exception {

            String testName = "test04";

            ORMEntityType personLastState = new ORMEntityType("Person");
            personLastState.setUpdateStatus("Stable");
            //model.addElement(personLastState, "EntityType");

            ORMEntityType person = new ORMEntityType("NewPerson");
            person.setUpdateStatus("Modified");
            person.setLastState(personLastState);
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Stable");
            model.addElement(female, "EntityType");

            ORMEntityType male = new ORMEntityType("Male");
            male.setUpdateStatus("Created");
            model.addElement(male, "EntityType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("05 - Удаление одного из трёх EntityType в онтологии")
        public void test05() throws Exception {

            String testName = "test05";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Stable");
            model.addElement(female, "EntityType");

            ORMEntityType male = new ORMEntityType("Male");
            male.setUpdateStatus("Deleted");
            model.addElement(male, "EntityType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("06 - Удаление двух из трёх EntityType в онтологии")
        public void test06() throws Exception {

            String testName = "test06";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Deleted");
            model.addElement(female, "EntityType");

            ORMEntityType male = new ORMEntityType("Male");
            male.setUpdateStatus("Deleted");
            model.addElement(male, "EntityType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("07 - Удаление всех трёх EntityType в онтологии")
        public void test07() throws Exception {

            String testName = "test07";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Deleted");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Deleted");
            model.addElement(female, "EntityType");

            ORMEntityType male = new ORMEntityType("Male");
            male.setUpdateStatus("Deleted");
            model.addElement(male, "EntityType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

    }

    @Nested
    @DisplayName("02 - Тестирование Subtyping")
    class SubtypingTesting {

        @Test
        @DisplayName("08 - Добавление Subtype между двумя EntityType")
        public void test08() throws Exception {

            String testName = "test08";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Stable");
            model.addElement(female, "EntityType");

            ORMSubtyping female_person = new ORMSubtyping(female, person);
            female_person.setUpdateStatus("Created");
            model.addElement(female_person, "Subtyping");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("09 - Добавление subtype между EntityType, где родитель уже является родителем в subtype с другим EntityType")
        public void test09() throws Exception {

            String testName = "test09";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Stable");
            model.addElement(female, "EntityType");

            ORMSubtyping female_person = new ORMSubtyping(female, person);
            female_person.setUpdateStatus("Stable");
            model.addElement(female_person, "Subtyping");

            ORMEntityType male = new ORMEntityType("Male");
            male.setUpdateStatus("Stable");
            model.addElement(male, "EntityType");

            ORMSubtyping male_person = new ORMSubtyping(male, person);
            male_person.setUpdateStatus("Created");
            model.addElement(male_person, "Subtyping");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("10 - Добавление subtype между EntityType, где родитель уже является подклассом в subtype с другим EntityType")
        public void test10() throws Exception {

            String testName = "test10";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Stable");
            model.addElement(female, "EntityType");

            ORMSubtyping female_person = new ORMSubtyping(female, person);
            female_person.setUpdateStatus("Stable");
            model.addElement(female_person, "Subtyping");

            ORMEntityType mother = new ORMEntityType("Mother");
            mother.setUpdateStatus("Stable");
            model.addElement(mother, "EntityType");

            ORMSubtyping mother_female = new ORMSubtyping(mother, female);
            mother_female.setUpdateStatus("Created");
            model.addElement(mother_female, "Subtyping");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("11 - Добавление subtype к одному из двух EntityType, которые уже в subtype с другим EntityType")
        public void test11() throws Exception {

            String testName = "test11";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Stable");
            model.addElement(female, "EntityType");

            ORMSubtyping female_person = new ORMSubtyping(female, person);
            female_person.setUpdateStatus("Stable");
            model.addElement(female_person, "Subtyping");

            ORMEntityType male = new ORMEntityType("Male");
            male.setUpdateStatus("Stable");
            model.addElement(male, "EntityType");

            ORMSubtyping male_person = new ORMSubtyping(male, person);
            male_person.setUpdateStatus("Stable");
            model.addElement(male_person, "Subtyping");

            ORMEntityType mother = new ORMEntityType("Mother");
            mother.setUpdateStatus("Stable");
            model.addElement(mother, "EntityType");

            ORMSubtyping mother_female = new ORMSubtyping(mother, female);
            mother_female.setUpdateStatus("Created");
            model.addElement(mother_female, "Subtyping");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("12 - Добавление subtype между Grandmother и Female => Person -> Male, Person -> Female -> (Mother, Grandmother)")
        public void test12() throws Exception {

            String testName = "test12";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Stable");
            model.addElement(female, "EntityType");

            ORMSubtyping female_person = new ORMSubtyping(female, person);
            female_person.setUpdateStatus("Stable");
            model.addElement(female_person, "Subtyping");

            ORMEntityType male = new ORMEntityType("Male");
            male.setUpdateStatus("Stable");
            model.addElement(male, "EntityType");

            ORMSubtyping male_person = new ORMSubtyping(male, person);
            male_person.setUpdateStatus("Stable");
            model.addElement(male_person, "Subtyping");

            ORMEntityType mother = new ORMEntityType("Mother");
            mother.setUpdateStatus("Stable");
            model.addElement(mother, "EntityType");

            ORMSubtyping mother_female = new ORMSubtyping(mother, female);
            mother_female.setUpdateStatus("Stable");
            model.addElement(mother_female, "Subtyping");

            ORMEntityType grandmother = new ORMEntityType("Grandmother");
            grandmother.setUpdateStatus("Stable");
            model.addElement(grandmother, "EntityType");

            ORMSubtyping grandmother_female = new ORMSubtyping(grandmother, female);
            grandmother_female.setUpdateStatus("Created");
            model.addElement(grandmother_female, "Subtyping");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("13 - Обновление subtype, Mother -> Female => Mother -> Person")
        public void test13() throws Exception {

            String testName = "test13";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Stable");
            model.addElement(female, "EntityType");

            ORMSubtyping female_person = new ORMSubtyping(female, person);
            female_person.setUpdateStatus("Stable");
            model.addElement(female_person, "Subtyping");

            ORMEntityType male = new ORMEntityType("Male");
            male.setUpdateStatus("Stable");
            model.addElement(male, "EntityType");

            ORMSubtyping male_person = new ORMSubtyping(male, person);
            male_person.setUpdateStatus("Stable");
            model.addElement(male_person, "Subtyping");

            ORMEntityType mother = new ORMEntityType("Mother");
            mother.setUpdateStatus("Stable");
            model.addElement(mother, "EntityType");

            ORMEntityType grandmother = new ORMEntityType("Grandmother");
            grandmother.setUpdateStatus("Stable");
            model.addElement(grandmother, "EntityType");

            ORMSubtyping grandmother_female = new ORMSubtyping(grandmother, female);
            grandmother_female.setUpdateStatus("Stable");
            model.addElement(grandmother_female, "Subtyping");

            ORMSubtyping mother_female = new ORMSubtyping(mother, female);
            mother_female.setUpdateStatus("Stable");
            //model.addElement(mother_female, "Subtyping");

            ORMSubtyping mother_person = new ORMSubtyping(mother, person);
            mother_person.setUpdateStatus("Modified");
            mother_person.setLastState(mother_female);
            model.addElement(mother_person, "Subtyping");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("14 - Обновление subtype, Male -> Person => Male -> Female")
        public void test14() throws Exception {

            String testName = "test14";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Stable");
            model.addElement(female, "EntityType");

            ORMSubtyping female_person = new ORMSubtyping(female, person);
            female_person.setUpdateStatus("Stable");
            model.addElement(female_person, "Subtyping");

            ORMEntityType male = new ORMEntityType("Male");
            male.setUpdateStatus("Stable");
            model.addElement(male, "EntityType");

            ORMSubtyping male_person = new ORMSubtyping(male, person);
            male_person.setUpdateStatus("Stable");
            //model.addElement(male_person, "Subtyping");

            ORMEntityType mother = new ORMEntityType("Mother");
            mother.setUpdateStatus("Stable");
            model.addElement(mother, "EntityType");

            ORMSubtyping mother_female = new ORMSubtyping(mother, female);
            mother_female.setUpdateStatus("Stable");
            model.addElement(mother_female, "Subtyping");

            ORMEntityType grandmother = new ORMEntityType("Grandmother");
            grandmother.setUpdateStatus("Stable");
            model.addElement(grandmother, "EntityType");

            ORMSubtyping grandmother_female = new ORMSubtyping(grandmother, female);
            grandmother_female.setUpdateStatus("Stable");
            model.addElement(grandmother_female, "Subtyping");

            ORMSubtyping male_female = new ORMSubtyping(male, female);
            male_female.setUpdateStatus("Modified");
            male_female.setLastState(male_person);
            model.addElement(male_female, "Subtyping");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("15 - Обновление subtype, Mother -> Female => Mother -> Person, Grandmother -> Female => Grandmother -> Person")
        public void test15() throws Exception {

            String testName = "test15";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Stable");
            model.addElement(female, "EntityType");

            ORMSubtyping female_person = new ORMSubtyping(female, person);
            female_person.setUpdateStatus("Stable");
            model.addElement(female_person, "Subtyping");

            ORMEntityType male = new ORMEntityType("Male");
            male.setUpdateStatus("Stable");
            model.addElement(male, "EntityType");

            ORMSubtyping male_person = new ORMSubtyping(male, person);
            male_person.setUpdateStatus("Stable");
            model.addElement(male_person, "Subtyping");

            ORMEntityType mother = new ORMEntityType("Mother");
            mother.setUpdateStatus("Stable");
            model.addElement(mother, "EntityType");

            ORMSubtyping mother_female = new ORMSubtyping(mother, female);
            mother_female.setUpdateStatus("Stable");
            //model.addElement(mother_female, "Subtyping");

            ORMSubtyping mother_person = new ORMSubtyping(mother, person);
            mother_person.setUpdateStatus("Modified");
            mother_person.setLastState(mother_female);
            model.addElement(mother_person, "Subtyping");

            ORMEntityType grandmother = new ORMEntityType("Grandmother");
            grandmother.setUpdateStatus("Stable");
            model.addElement(grandmother, "EntityType");

            ORMSubtyping grandmother_female = new ORMSubtyping(grandmother, female);
            grandmother_female.setUpdateStatus("Stable");
            //model.addElement(grandmother_female, "Subtyping");

            ORMSubtyping grandmother_person = new ORMSubtyping(grandmother, person);
            grandmother_person.setUpdateStatus("Modified");
            grandmother_person.setLastState(grandmother_female);
            model.addElement(grandmother_person, "Subtyping");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("16 - Удаление subtype Mother -> Female")
        public void test16() throws Exception {

            String testName = "test16";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Stable");
            model.addElement(female, "EntityType");

            ORMSubtyping female_person = new ORMSubtyping(female, person);
            female_person.setUpdateStatus("Stable");
            model.addElement(female_person, "Subtyping");

            ORMEntityType male = new ORMEntityType("Male");
            male.setUpdateStatus("Stable");
            model.addElement(male, "EntityType");

            ORMSubtyping male_person = new ORMSubtyping(male, person);
            male_person.setUpdateStatus("Stable");
            model.addElement(male_person, "Subtyping");

            ORMEntityType mother = new ORMEntityType("Mother");
            mother.setUpdateStatus("Stable");
            model.addElement(mother, "EntityType");

            ORMSubtyping mother_female = new ORMSubtyping(mother, female);
            mother_female.setUpdateStatus("Deleted");
            model.addElement(mother_female, "Subtyping");

            ORMEntityType grandmother = new ORMEntityType("Grandmother");
            grandmother.setUpdateStatus("Stable");
            model.addElement(grandmother, "EntityType");

            ORMSubtyping grandmother_female = new ORMSubtyping(grandmother, female);
            grandmother_female.setUpdateStatus("Stable");
            model.addElement(grandmother_female, "Subtyping");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("17 - Удаление subtype Female -> Person")
        public void test17() throws Exception {

            String testName = "test17";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Stable");
            model.addElement(female, "EntityType");

            ORMSubtyping female_person = new ORMSubtyping(female, person);
            female_person.setUpdateStatus("Deleted");
            model.addElement(female_person, "Subtyping");

            ORMEntityType male = new ORMEntityType("Male");
            male.setUpdateStatus("Stable");
            model.addElement(male, "EntityType");

            ORMSubtyping male_person = new ORMSubtyping(male, person);
            male_person.setUpdateStatus("Stable");
            model.addElement(male_person, "Subtyping");

            ORMEntityType mother = new ORMEntityType("Mother");
            mother.setUpdateStatus("Stable");
            model.addElement(mother, "EntityType");

            ORMSubtyping mother_female = new ORMSubtyping(mother, female);
            mother_female.setUpdateStatus("Stable");
            model.addElement(mother_female, "Subtyping");

            ORMEntityType grandmother = new ORMEntityType("Grandmother");
            grandmother.setUpdateStatus("Stable");
            model.addElement(grandmother, "EntityType");

            ORMSubtyping grandmother_female = new ORMSubtyping(grandmother, female);
            grandmother_female.setUpdateStatus("Stable");
            model.addElement(grandmother_female, "Subtyping");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("18 - Удаление subtype Mother -> Female, Grandmother -> Female, Female -> Person, Male -> Person")
        public void test18() throws Exception {

            String testName = "test18";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType female = new ORMEntityType("Female");
            female.setUpdateStatus("Stable");
            model.addElement(female, "EntityType");

            ORMSubtyping female_person = new ORMSubtyping(female, person);
            female_person.setUpdateStatus("Deleted");
            model.addElement(female_person, "Subtyping");

            ORMEntityType male = new ORMEntityType("Male");
            male.setUpdateStatus("Stable");
            model.addElement(male, "EntityType");

            ORMSubtyping male_person = new ORMSubtyping(male, person);
            male_person.setUpdateStatus("Deleted");
            model.addElement(male_person, "Subtyping");

            ORMEntityType mother = new ORMEntityType("Mother");
            mother.setUpdateStatus("Stable");
            model.addElement(mother, "EntityType");

            ORMSubtyping mother_female = new ORMSubtyping(mother, female);
            mother_female.setUpdateStatus("Deleted");
            model.addElement(mother_female, "Subtyping");

            ORMEntityType grandmother = new ORMEntityType("Grandmother");
            grandmother.setUpdateStatus("Stable");
            model.addElement(grandmother, "EntityType");

            ORMSubtyping grandmother_female = new ORMSubtyping(grandmother, female);
            grandmother_female.setUpdateStatus("Deleted");
            model.addElement(grandmother_female, "Subtyping");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }
    }

    @Nested
    @DisplayName("03 - Тестирование ValueType")
    class ValueTypeTesting {

        @Test
        @DisplayName("19 - Добавление ValueType к единственному EntityType")
        public void test19() throws Exception {

            String testName = "test19";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMValueType personName = new ORMValueType("has", "PersonName", person);
            personName.setUpdateStatus("Created");
            model.addElement(personName, "ValueType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("20 - Добавление второго ValueType к единственному EntityType")
        public void test20() throws Exception {

            String testName = "test20";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMValueType personName = new ORMValueType("has", "PersonName", person);
            personName.setUpdateStatus("Stable");
            model.addElement(personName, "ValueType");

            ORMValueType personAge = new ORMValueType("has", "PersonAge", person);
            personAge.setUpdateStatus("Created");
            model.addElement(personAge, "ValueType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("21 - Добавление ValueType каждому из двух EntityType")
        public void test21() throws Exception {

            String testName = "test21";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMValueType personName = new ORMValueType("has", "PersonName", person);
            personName.setUpdateStatus("Created");
            model.addElement(personName, "ValueType");

            ORMValueType personAge = new ORMValueType("has", "Budget", committee);
            personAge.setUpdateStatus("Created");
            model.addElement(personAge, "ValueType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("22 - Изменение только названия у ValueType")
        public void test22() throws Exception {

            String testName = "test22";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMValueType personName = new ORMValueType("has", "PersonName", person);
            personName.setUpdateStatus("Stable");
            //model.addElement(personName, "ValueType");

            ORMValueType newPersonName = new ORMValueType("has", "NewPersonName", person);
            newPersonName.setUpdateStatus("Modified");
            newPersonName.setLastState(personName);
            model.addElement(newPersonName, "ValueType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("23 - Изменение только названия связи между ValueType и EntityType")
        public void test23() throws Exception {

            String testName = "test23";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMValueType personName = new ORMValueType("has", "PersonName", person);
            personName.setUpdateStatus("Stable");
            //model.addElement(personName, "ValueType");

            ORMValueType newPersonName = new ORMValueType("new_has", "PersonName", person);
            newPersonName.setUpdateStatus("Modified");
            newPersonName.setLastState(personName);
            model.addElement(newPersonName, "ValueType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("24 - Перенос ValueType к другому EntityType")
        public void test24() throws Exception {

            String testName = "test24";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMValueType personName = new ORMValueType("has", "Name", person);
            personName.setUpdateStatus("Stable");
            //model.addElement(personName, "ValueType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMValueType committeeName = new ORMValueType("has", "Name", committee);
            committeeName.setUpdateStatus("Modified");
            committeeName.setLastState(personName);
            model.addElement(committeeName, "ValueType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("25 - Удаление ValueType")
        public void test25() throws Exception {

            String testName = "test25";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMValueType committeeName = new ORMValueType("has", "Name", committee);
            committeeName.setUpdateStatus("Deleted");
            model.addElement(committeeName, "ValueType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }
    }

    @Nested
    @DisplayName("04 - Тестирование UnaryRole")
    class UnaryRoleTesting {

        @Test
        @DisplayName("26 - Добавление UnaryRole к единственному EntityType")
        public void test26() throws Exception {

            String testName = "test26";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMUnaryRole smokes = new ORMUnaryRole("smokes", person);
            smokes.setUpdateStatus("Created");
            model.addElement(smokes, "UnaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("27 - Добавление второй UnaryRole к единственному EntityType")
        public void test27() throws Exception {

            String testName = "test27";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMUnaryRole smokes = new ORMUnaryRole("smokes", person);
            smokes.setUpdateStatus("Stable");
            model.addElement(smokes, "UnaryRole");

            ORMUnaryRole is_sportsman = new ORMUnaryRole("is_sportsman",  person);
            is_sportsman.setUpdateStatus("Created");
            model.addElement(is_sportsman, "UnaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("28 - Добавление UnaryRole каждому из двух EntityType")
        public void test28() throws Exception {

            String testName = "test28";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMUnaryRole smokes = new ORMUnaryRole("smokes", person);
            smokes.setUpdateStatus("Created");
            model.addElement(smokes, "UnaryRole");

            ORMUnaryRole is_big = new ORMUnaryRole("is_big",  committee);
            is_big.setUpdateStatus("Created");
            model.addElement(is_big, "UnaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("29 - Изменение только названия у UnaryRole")
        public void test29() throws Exception {

            String testName = "test29";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMUnaryRole smokes = new ORMUnaryRole("smokes", person);
            smokes.setUpdateStatus("Stable");
            //model.addElement(smokes, "UnaryRole");

            ORMUnaryRole new_smokes = new ORMUnaryRole("new_smokes", person);
            new_smokes.setUpdateStatus("Modified");
            new_smokes.setLastState(smokes);
            model.addElement(new_smokes, "UnaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("30 - Перенос UnaryRole к другому EntityType")
        public void test30() throws Exception {

            String testName = "test30";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMUnaryRole is_old = new ORMUnaryRole("is_old", person);
            is_old.setUpdateStatus("Stable");
            //model.addElement(smokes, "UnaryRole");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMUnaryRole new_is_old = new ORMUnaryRole("is_old", committee);
            new_is_old.setUpdateStatus("Modified");
            new_is_old.setLastState(is_old);
            model.addElement(new_is_old, "UnaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("31 - Удаление UnaryRole")
        public void test31() throws Exception {

            String testName = "test31";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMUnaryRole smokes = new ORMUnaryRole("smokes", person);
            smokes.setUpdateStatus("Deleted");
            model.addElement(smokes, "UnaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }
    }

    @Nested
    @DisplayName("05 - Тестирование BinaryRole")
    class BinaryRoleTesting {

        @Test
        @DisplayName("32 - Добавление BinaryRole между двумя разными EntityType")
        public void test32() throws Exception {

            String testName = "test32";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee, "is_chaired_by");
            chairs.setUpdateStatus("Created");
            model.addElement(chairs, "BinaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("33 - Добавление BinaryRole между двумя разными EntityType без названия inverseRole")
        public void test33() throws Exception {

            String testName = "test33";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee);
            chairs.setUpdateStatus("Created");
            model.addElement(chairs, "BinaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("34 - Добавление BinaryRole к одному EntityType")
        public void test34() throws Exception {

            String testName = "test34";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, person, "is_chaired_by");
            chairs.setUpdateStatus("Created");
            model.addElement(chairs, "BinaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("35 - Добавление второй BinaryRole между двумя разными EntityType")
        public void test35() throws Exception {

            String testName = "test35";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee, "is_chaired_by");
            chairs.setUpdateStatus("Stable");
            model.addElement(chairs, "BinaryRole");

            ORMBinaryRole is_a_member_of = new ORMBinaryRole("is_a_member_of", person, committee, "includes");
            is_a_member_of.setUpdateStatus("Created");
            model.addElement(is_a_member_of, "BinaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("36 - Добавление второй BinaryRole, у которой совпадает source с предыдущей")
        public void test36() throws Exception {

            String testName = "test36";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee, "is_chaired_by");
            chairs.setUpdateStatus("Stable");
            model.addElement(chairs, "BinaryRole");

            ORMEntityType budget = new ORMEntityType("Budget");
            budget.setUpdateStatus("Stable");
            model.addElement(budget, "EntityType");

            ORMBinaryRole has_budget = new ORMBinaryRole("has", person, budget);
            has_budget.setUpdateStatus("Created");
            model.addElement(has_budget, "BinaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("37 - Добавление второй BinaryRole, у которой совпадает target с предыдущей")
        public void test37() throws Exception {

            String testName = "test37";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee, "is_chaired_by");
            chairs.setUpdateStatus("Stable");
            model.addElement(chairs, "BinaryRole");

            ORMEntityType budget = new ORMEntityType("Budget");
            budget.setUpdateStatus("Stable");
            model.addElement(budget, "EntityType");

            ORMBinaryRole has_budget = new ORMBinaryRole("has", budget, committee);
            has_budget.setUpdateStatus("Created");
            model.addElement(has_budget, "BinaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("38 - Добавление второй BinaryRole к другим EntityType")
        public void test38() throws Exception {

            String testName = "test38";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee, "is_chaired_by");
            chairs.setUpdateStatus("Stable");
            model.addElement(chairs, "BinaryRole");

            ORMEntityType budget = new ORMEntityType("Budget");
            budget.setUpdateStatus("Stable");
            model.addElement(budget, "EntityType");

            ORMEntityType city = new ORMEntityType("City");
            city.setUpdateStatus("Stable");
            model.addElement(city, "EntityType");

            ORMBinaryRole has_budget = new ORMBinaryRole("has", city, budget);
            has_budget.setUpdateStatus("Created");
            model.addElement(has_budget, "BinaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("39 - Изменение названия BinaryRole и названия её inverseRole")
        public void test39() throws Exception {

            String testName = "test39";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee, "is_chaired_by");
            chairs.setUpdateStatus("Stable");
            //model.addElement(chairs, "BinaryRole");

            ORMBinaryRole new_chairs = new ORMBinaryRole("new_chairs", person, committee, "new_is_chaired_by");
            new_chairs.setUpdateStatus("Modified");
            new_chairs.setLastState(chairs);
            model.addElement(new_chairs, "BinaryRole");

            ORMEntityType budget = new ORMEntityType("Budget");
            budget.setUpdateStatus("Stable");
            model.addElement(budget, "EntityType");

            ORMEntityType city = new ORMEntityType("City");
            city.setUpdateStatus("Stable");
            model.addElement(city, "EntityType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("40 - Изменение названия только BinaryRole")
        public void test40() throws Exception {

            String testName = "test40";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee, "is_chaired_by");
            chairs.setUpdateStatus("Stable");
            //model.addElement(chairs, "BinaryRole");

            ORMBinaryRole new_chairs = new ORMBinaryRole("new_chairs", person, committee, "is_chaired_by");
            new_chairs.setUpdateStatus("Modified");
            new_chairs.setLastState(chairs);
            model.addElement(new_chairs, "BinaryRole");

            ORMEntityType budget = new ORMEntityType("Budget");
            budget.setUpdateStatus("Stable");
            model.addElement(budget, "EntityType");

            ORMEntityType city = new ORMEntityType("City");
            city.setUpdateStatus("Stable");
            model.addElement(city, "EntityType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("41 - Изменение названия только inverseBinaryRole")
        public void test41() throws Exception {

            String testName = "test41";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee, "is_chaired_by");
            chairs.setUpdateStatus("Stable");
            //model.addElement(chairs, "BinaryRole");

            ORMBinaryRole new_chairs = new ORMBinaryRole("chairs", person, committee, "new_is_chaired_by");
            new_chairs.setUpdateStatus("Modified");
            new_chairs.setLastState(chairs);
            model.addElement(new_chairs, "BinaryRole");

            ORMEntityType budget = new ORMEntityType("Budget");
            budget.setUpdateStatus("Stable");
            model.addElement(budget, "EntityType");

            ORMEntityType city = new ORMEntityType("City");
            city.setUpdateStatus("Stable");
            model.addElement(city, "EntityType");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("42 - Изменение source BinaryRole")
        public void test42() throws Exception {

            String testName = "test42";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee, "is_chaired_by");
            chairs.setUpdateStatus("Stable");
            //model.addElement(chairs, "BinaryRole");

            ORMEntityType budget = new ORMEntityType("Budget");
            budget.setUpdateStatus("Stable");
            model.addElement(budget, "EntityType");

            ORMEntityType city = new ORMEntityType("City");
            city.setUpdateStatus("Stable");
            model.addElement(city, "EntityType");

            ORMBinaryRole new_chairs = new ORMBinaryRole("chairs", city, committee, "is_chaired_by");
            new_chairs.setUpdateStatus("Modified");
            new_chairs.setLastState(chairs);
            model.addElement(new_chairs, "BinaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("43 - Изменение target BinaryRole")
        public void test43() throws Exception {

            String testName = "test43";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee, "is_chaired_by");
            chairs.setUpdateStatus("Stable");
            //model.addElement(chairs, "BinaryRole");

            ORMEntityType budget = new ORMEntityType("Budget");
            budget.setUpdateStatus("Stable");
            model.addElement(budget, "EntityType");

            ORMEntityType city = new ORMEntityType("City");
            city.setUpdateStatus("Stable");
            model.addElement(city, "EntityType");

            ORMBinaryRole new_chairs = new ORMBinaryRole("chairs", person, city, "is_chaired_by");
            new_chairs.setUpdateStatus("Modified");
            new_chairs.setLastState(chairs);
            model.addElement(new_chairs, "BinaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("44 - Изменение source и target BinaryRole")
        public void test44() throws Exception {

            String testName = "test44";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee, "is_chaired_by");
            chairs.setUpdateStatus("Stable");
            //model.addElement(chairs, "BinaryRole");

            ORMEntityType budget = new ORMEntityType("Budget");
            budget.setUpdateStatus("Stable");
            model.addElement(budget, "EntityType");

            ORMEntityType city = new ORMEntityType("City");
            city.setUpdateStatus("Stable");
            model.addElement(city, "EntityType");

            ORMBinaryRole new_chairs = new ORMBinaryRole("chairs", city, budget, "is_chaired_by");
            new_chairs.setUpdateStatus("Modified");
            new_chairs.setLastState(chairs);
            model.addElement(new_chairs, "BinaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }

        @Test
        @DisplayName("45 - Удаление BinaryRole")
        public void test45() throws Exception {

            String testName = "test45";

            ORMEntityType person = new ORMEntityType("Person");
            person.setUpdateStatus("Stable");
            model.addElement(person, "EntityType");

            ORMEntityType committee = new ORMEntityType("Committee");
            committee.setUpdateStatus("Stable");
            model.addElement(committee, "EntityType");

            ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee, "is_chaired_by");
            chairs.setUpdateStatus("Deleted");
            model.addElement(chairs, "BinaryRole");

            OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
            saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

            compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
        }
    }
}
