import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.semanticweb.owlapi.model.OWLOntology;
import org.vstu.nodelinkdiagram.MainDiagramModel;
import org.vstu.orm2diagram.model.ORM_DiagramFactory;
import org.vstu.orm2diagram.model.ORM_EntityType;
import org.vstu.orm2diagram.model.ORM_Subtyping;

public class ORM_to_OWL_Base_Methods_Testing extends TestingClass {

    public ORM_to_OWL_Base_Methods_Testing() {
        TEST_DIR = System.getProperty("user.dir") + "/tests/ORM_to_OWL_tests/";
    }


    @Test
    @DisplayName("Добавление EntityType в пустую онтологию")
    public void test01() throws Exception {

        String testName = "test01";
        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
        myModel = mainModel.registerClient(new Test_DiagramClient());

        sourceClientModel.beginUpdate();
        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        person_EntityType.setName("Person");
        sourceClientModel.commit();

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, "");
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    @DisplayName("Добавление EntityType в онтологию с единственным EntityType")
    public void test02() throws Exception {

        String testName = "test02";
        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
        myModel = mainModel.registerClient(new Test_DiagramClient());

        ElementsPresenter elementsPresenter = new TestingClass.ElementsPresenter();
        myModel.addListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        person_EntityType.setName("Person");
        sourceClientModel.commit();

        myModel.removeListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_EntityType female_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        female_EntityType.setName("Female");
        sourceClientModel.commit();

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    @DisplayName("Добавление EntityType в онтологию с двумя EntityType")
    public void test03() throws Exception {

        String testName = "test03";
        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
        myModel = mainModel.registerClient(new Test_DiagramClient());

        ElementsPresenter elementsPresenter = new TestingClass.ElementsPresenter();
        myModel.addListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        person_EntityType.setName("Person");
        ORM_EntityType female_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        female_EntityType.setName("Female");
        sourceClientModel.commit();

        myModel.removeListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_EntityType male_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        male_EntityType.setName("Male");
        sourceClientModel.commit();

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    @DisplayName("Обновление одного из трёх EntityType в онтологии")
    public void test04() throws Exception {

        String testName = "test04";
        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
        myModel = mainModel.registerClient(new Test_DiagramClient());

        ElementsPresenter elementsPresenter = new TestingClass.ElementsPresenter();
        myModel.addListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        person_EntityType.setName("Person");
        ORM_EntityType female_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        female_EntityType.setName("Female");
        ORM_EntityType male_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        male_EntityType.setName("Male");
        sourceClientModel.commit();

        myModel.removeListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        person_EntityType.setName("NewPerson");
        sourceClientModel.commit();

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    @DisplayName("Удаление одного из трёх EntityType в онтологии")
    public void test05() throws Exception {

        String testName = "test05";
        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
        myModel = mainModel.registerClient(new Test_DiagramClient());

        ElementsPresenter elementsPresenter = new TestingClass.ElementsPresenter();
        myModel.addListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        person_EntityType.setName("Person");
        ORM_EntityType female_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        female_EntityType.setName("Female");
        ORM_EntityType male_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        male_EntityType.setName("Male");
        sourceClientModel.commit();

        myModel.removeListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        sourceClientModel.removeElement(male_EntityType);
        sourceClientModel.commit();

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    @DisplayName("Удаление двух из трёх EntityType в онтологии")
    public void test06() throws Exception {

        String testName = "test06";
        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
        myModel = mainModel.registerClient(new Test_DiagramClient());

        ElementsPresenter elementsPresenter = new TestingClass.ElementsPresenter();
        myModel.addListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        person_EntityType.setName("Person");
        ORM_EntityType female_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        female_EntityType.setName("Female");
        ORM_EntityType male_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        male_EntityType.setName("Male");
        sourceClientModel.commit();

        myModel.removeListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        sourceClientModel.removeElement(male_EntityType);
        sourceClientModel.removeElement(female_EntityType);
        sourceClientModel.commit();

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    @DisplayName("Удаление всех трёх EntityType в онтологии")
    public void test07() throws Exception {

        String testName = "test07";
        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
        myModel = mainModel.registerClient(new Test_DiagramClient());

        ElementsPresenter elementsPresenter = new TestingClass.ElementsPresenter();
        myModel.addListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        person_EntityType.setName("Person");
        ORM_EntityType female_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        female_EntityType.setName("Female");
        ORM_EntityType male_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        male_EntityType.setName("Male");
        sourceClientModel.commit();

        myModel.removeListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        sourceClientModel.removeElement(male_EntityType);
        sourceClientModel.removeElement(female_EntityType);
        sourceClientModel.removeElement(person_EntityType);
        sourceClientModel.commit();

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    @DisplayName("Добавление Subtype между двумя EntityType")
    public void test08() throws Exception {

        String testName = "test08";
        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
        myModel = mainModel.registerClient(new Test_DiagramClient());

        ElementsPresenter elementsPresenter = new TestingClass.ElementsPresenter();
        myModel.addListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        person_EntityType.setName("Person");
        ORM_EntityType female_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        female_EntityType.setName("Female");
        sourceClientModel.commit();

        myModel.removeListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_Subtyping subtyping_Person_Female = sourceClientModel.connectBy(female_EntityType, person_EntityType, ORM_Subtyping.class);
        sourceClientModel.commit();

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    @DisplayName("Добавление subtype между EntityType, где родитель уже является родителем в subtype с другим EntityType")
    public void test09() throws Exception {

        String testName = "test09";
        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
        myModel = mainModel.registerClient(new Test_DiagramClient());

        ElementsPresenter elementsPresenter = new TestingClass.ElementsPresenter();
        myModel.addListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        person_EntityType.setName("Person");
        ORM_EntityType female_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        female_EntityType.setName("Female");
        ORM_Subtyping subtyping_Person_Female = sourceClientModel.connectBy(female_EntityType, person_EntityType, ORM_Subtyping.class);
        ORM_EntityType male_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        male_EntityType.setName("Male");
        sourceClientModel.commit();

        myModel.removeListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_Subtyping subtyping_Person_Male = sourceClientModel.connectBy(male_EntityType, person_EntityType, ORM_Subtyping.class);
        sourceClientModel.commit();

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    @DisplayName("Добавление subtype между EntityType, где родитель уже является подклассом в subtype с другим EntityType")
    public void test10() throws Exception {

        String testName = "test10";
        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
        myModel = mainModel.registerClient(new Test_DiagramClient());

        ElementsPresenter elementsPresenter = new TestingClass.ElementsPresenter();
        myModel.addListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        person_EntityType.setName("Person");
        ORM_EntityType female_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        female_EntityType.setName("Female");
        ORM_Subtyping subtyping_Person_Female = sourceClientModel.connectBy(female_EntityType, person_EntityType, ORM_Subtyping.class);
        ORM_EntityType mother_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        mother_EntityType.setName("Mother");
        sourceClientModel.commit();

        myModel.removeListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_Subtyping subtyping_Female_Mother = sourceClientModel.connectBy(mother_EntityType, female_EntityType, ORM_Subtyping.class);
        sourceClientModel.commit();

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    @DisplayName("Добавление subtype к одному из двух EntityType, которые уже в subtype с другим EntityType")
    public void test11() throws Exception {

        String testName = "test11";
        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
        myModel = mainModel.registerClient(new Test_DiagramClient());

        ElementsPresenter elementsPresenter = new TestingClass.ElementsPresenter();
        myModel.addListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        person_EntityType.setName("Person");
        ORM_EntityType female_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        female_EntityType.setName("Female");
        ORM_Subtyping subtyping_Person_Female = sourceClientModel.connectBy(female_EntityType, person_EntityType, ORM_Subtyping.class);
        ORM_EntityType male_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        male_EntityType.setName("Male");
        ORM_Subtyping subtyping_Person_Male = sourceClientModel.connectBy(male_EntityType, person_EntityType, ORM_Subtyping.class);
        ORM_EntityType mother_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        mother_EntityType.setName("Mother");
        sourceClientModel.commit();

        myModel.removeListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_Subtyping subtyping_Female_Mother = sourceClientModel.connectBy(mother_EntityType, female_EntityType, ORM_Subtyping.class);
        sourceClientModel.commit();

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    @DisplayName("Добавление subtype между Grandmother и Female => Person -> Male, Person -> Female -> (Mother, Grandmother)")
    public void test12() throws Exception {

        String testName = "test12";
        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
        myModel = mainModel.registerClient(new Test_DiagramClient());

        ElementsPresenter elementsPresenter = new TestingClass.ElementsPresenter();
        myModel.addListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        person_EntityType.setName("Person");
        ORM_EntityType female_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        female_EntityType.setName("Female");
        ORM_Subtyping subtyping_Person_Female = sourceClientModel.connectBy(female_EntityType, person_EntityType, ORM_Subtyping.class);
        ORM_EntityType male_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        male_EntityType.setName("Male");
        ORM_Subtyping subtyping_Person_Male = sourceClientModel.connectBy(male_EntityType, person_EntityType, ORM_Subtyping.class);
        ORM_EntityType mother_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        mother_EntityType.setName("Mother");
        ORM_Subtyping subtyping_Female_Mother = sourceClientModel.connectBy(mother_EntityType, female_EntityType, ORM_Subtyping.class);
        ORM_EntityType grandmother_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        grandmother_EntityType.setName("Grandmother");
        sourceClientModel.commit();

        myModel.removeListener(elementsPresenter);

        sourceClientModel.beginUpdate();
        ORM_Subtyping subtyping_Female_Grandmother = sourceClientModel.connectBy(grandmother_EntityType, female_EntityType, ORM_Subtyping.class);
        sourceClientModel.commit();

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }



//    @Test
//    public void runTest01() throws Exception {
//        String testName = "test01";
//        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
//        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
//        myModel = mainModel.registerClient(new Test_DiagramClient());
//
//        TestingClass.ElementsPresenter elementsPresenter = new TestingClass.ElementsPresenter();
//        myModel.addListener(elementsPresenter);
//
//
//        sourceClientModel.beginUpdate();
//        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
//        person_EntityType.setName("Person");
//        ORM_EntityType female_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
//        female_EntityType.setName("Female");
//        sourceClientModel.commit();
//
//        myModel.removeListener(elementsPresenter);
//
//        sourceClientModel.beginUpdate();
//        person_EntityType.setName("Person2");
//        ORM_EntityType male_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
//        male_EntityType.setName("Male");
//        sourceClientModel.removeElement(female_EntityType);
//        sourceClientModel.commit();
//
//
//
//        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, TEST_DIR + testName + "/prepared_" + testName + ".owl");
//        saveOntologyInFile(ontology, testName + "/actual_" + testName + ".owl");
//
//        compareOntologies(testName + "/expected_" + testName + ".owl", testName + "/actual_" + testName + ".owl");
//    }
//
//
//    @Test
//    public void runTest02() throws Exception {
//        String testName = "test02";
//        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
//        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
//        myModel = mainModel.registerClient(new Test_DiagramClient());
//
//        sourceClientModel.beginUpdate();
//        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
//        person_EntityType.setName("Person");
//        ORM_EntityType male_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
//        male_EntityType.setName("Male");
//        ORM_Subtyping subtyping_Person_Male = sourceClientModel.connectBy(male_EntityType, person_EntityType, ORM_Subtyping.class);
//        sourceClientModel.commit();
//
//        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, "");
//        saveOntologyInFile(ontology, testName + "/actual_" + testName + ".owl");
//
//        compareOntologies(testName + "/expected_" + testName + ".owl", testName + "/actual_" + testName + ".owl");
//    }
//
//    @Test
//    public void runTest03() throws Exception {
//        String testName = "test03";
//        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
//        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
//        myModel = mainModel.registerClient(new Test_DiagramClient());
//
//        TestingClass.ElementsPresenter elementsPresenter = new TestingClass.ElementsPresenter();
//        myModel.addListener(elementsPresenter);
//
//        // Изменяю модель клиента
//        sourceClientModel.beginUpdate();
//        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
//        person_EntityType.setName("Person");
//        ORM_EntityType female_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
//        female_EntityType.setName("Female");
//        ORM_Subtyping subtyping_Person_Female = sourceClientModel.connectBy(female_EntityType, person_EntityType, ORM_Subtyping.class);
//        sourceClientModel.commit();
//
//        myModel.removeListener(elementsPresenter);
//
//
//        // Изменяю модель клиента
//        sourceClientModel.beginUpdate();
//        ORM_EntityType male_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
//        male_EntityType.setName("Male");
//        ORM_Subtyping subtyping_Person_Male = sourceClientModel.connectBy(male_EntityType, person_EntityType, ORM_Subtyping.class);
//        sourceClientModel.commit();
//
//
//        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, TEST_DIR + testName + "/prepared_" + testName + ".owl");
//        saveOntologyInFile(ontology, testName + "/actual_" + testName + ".owl");
//
//        compareOntologies(testName + "/expected_" + testName + ".owl", testName + "/actual_" + testName + ".owl");
//    }
//
//    @Test
//    public void runTest04() throws Exception {
//
//
//    }
}
