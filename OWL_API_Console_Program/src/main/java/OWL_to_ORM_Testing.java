import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;
import org.vstu.nodelinkdiagram.MainDiagramModel;
import org.vstu.orm2diagram.model.ORM_DiagramFactory;
import org.vstu.orm2diagram.model.ORM_EntityType;
import org.vstu.orm2diagram.model.ORM_Subtyping;

public class OWL_to_ORM_Testing extends TestingClass {

    public OWL_to_ORM_Testing() {
        TEST_DIR = System.getProperty("user.dir") + "/tests/OWL_to_ORM/";
    }

    @Test
    public void runTest03() throws Exception {
        String testName = "test03";
        mainModel = new MainDiagramModel(new ORM_DiagramFactory());
        sourceClientModel = mainModel.registerClient(new Test_DiagramClient());
        myModel = mainModel.registerClient(new Test_DiagramClient());

        ElementsPresenter elementsPresenter = new ElementsPresenter();
        myModel.addListener(elementsPresenter);

        // Изменяю модель клиента
        sourceClientModel.beginUpdate();
        ORM_EntityType person_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        person_EntityType.setName("Person");
        ORM_EntityType female_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        female_EntityType.setName("Female");
        ORM_Subtyping subtyping_Person_Female = sourceClientModel.connectBy(female_EntityType, person_EntityType, ORM_Subtyping.class);
        sourceClientModel.commit();

        myModel.removeListener(elementsPresenter);


        // Изменяю модель клиента
        sourceClientModel.beginUpdate();
        ORM_EntityType male_EntityType = sourceClientModel.createNode(ORM_EntityType.class);
        male_EntityType.setName("Male");
        ORM_Subtyping subtyping_Person_Male = sourceClientModel.connectBy(male_EntityType, person_EntityType, ORM_Subtyping.class);
        sourceClientModel.commit();


        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(myModel, TEST_DIR + testName + "/prepared_" + testName + ".owl");
        saveOntologyInFile(ontology, testName + "/actual_" + testName + ".owl");

        compareOntologies(testName + "/expected_" + testName + ".owl", testName + "/actual_" + testName + ".owl");
    }
}
