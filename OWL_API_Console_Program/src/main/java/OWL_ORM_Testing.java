import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.semanticweb.owlapi.model.OWLOntology;
import ORMModel.*;

import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.util.Set;

public class OWL_ORM_Testing extends TestingClass {

    public OWL_ORM_Testing() {
        TEST_DIR = System.getProperty("user.dir") + "/tests/OWL_to_ORM/";
    }

    private boolean assertEntityTypes(ORMEntityType expected, ORMEntityType actual) {
        return expected.getName().equals(actual.getName());
    }
    private boolean assertEntityTypesLists(ORMModel expectedModel, ORMModel actualModel) {

        Set<ORMElement> expectedList = expectedModel.getElements("EntityType");
        Set<ORMElement> actualList = actualModel.getElements("EntityType");

        boolean isEqualLists = true;

        for (ORMElement expElement : expectedList) {
            boolean findEquivalent = false;
            ORMEntityType expEntityType = (ORMEntityType) expElement;
            for (ORMElement actElement : actualList) {
                ORMEntityType actEntityType = (ORMEntityType) actElement;
                if (assertEntityTypes(expEntityType, actEntityType)) {
                    findEquivalent = true;
                    break;
                }
            }
            if (!findEquivalent) {
                System.out.println("В полученной модели отсутствует EntityType \"" + expEntityType.getName() + "\"");
                isEqualLists = false;
            }
        }

        for (ORMElement actElement : actualList) {
            boolean findEquivalent = false;
            ORMEntityType actEntityType = (ORMEntityType) actElement;
            for (ORMElement expElement : expectedList) {
                ORMEntityType expEntityType = (ORMEntityType) expElement;
                if (assertEntityTypes(expEntityType, actEntityType)) {
                    findEquivalent = true;
                    break;
                }
            }
            if (!findEquivalent) {
                System.out.println("В полученной модели присутствует лишний EntityType \"" + actEntityType.getName() + "\"");
                isEqualLists = false;
            }
        }
        return isEqualLists;
    }

    private boolean assertSubtypes(ORMSubtyping expected, ORMSubtyping actual) {
        return
                expected.getSource().getName().equals(actual.getSource().getName())
            &&  expected.getTarget().getName().equals(actual.getTarget().getName());
    }
    private boolean assertSubtypesLists(ORMModel expectedModel, ORMModel actualModel) {

        Set<ORMElement> expectedList = expectedModel.getElements("Subtyping");
        Set<ORMElement> actualList = actualModel.getElements("Subtyping");

        boolean isEqualLists = true;

        for (ORMElement expElement : expectedList) {
            boolean findEquivalent = false;
            ORMSubtyping expSubtyping = (ORMSubtyping) expElement;
            for (ORMElement actElement : actualList) {
                ORMSubtyping actSubtyping = (ORMSubtyping) actElement;
                if (assertSubtypes(expSubtyping, actSubtyping)) {
                    findEquivalent = true;
                    break;
                }
            }
            if (!findEquivalent) {
                System.out.println("В полученной модели отсутствует Subtype \""
                        + expSubtyping.getSource().getName() + "\" -> \""
                        + expSubtyping.getTarget().getName() + "\"");
                isEqualLists = false;
            }
        }

        for (ORMElement actElement : actualList) {
            boolean findEquivalent = false;
            ORMSubtyping actSubtyping = (ORMSubtyping) actElement;
            for (ORMElement expElement : expectedList) {
                ORMSubtyping expSubtyping = (ORMSubtyping) expElement;
                if (assertSubtypes(expSubtyping, actSubtyping)) {
                    findEquivalent = true;
                    break;
                }
            }
            if (!findEquivalent) {
                System.out.println("В полученной модели присутствует лишний Subtype \""
                        + actSubtyping.getSource().getName() + "\" -> \""
                        + actSubtyping.getTarget().getName() + "\"");
                isEqualLists = false;
            }
        }
        return isEqualLists;
    }

    private boolean assertValueTypes(ORMValueType expected, ORMValueType actual) {
        return
                expected.getRoleName().equals(actual.getRoleName())
            &&  expected.getName().equals(actual.getName())
            &&  assertEntityTypes(expected.getEntityType(), actual.getEntityType());
    }
    private boolean assertValueTypesLists(ORMModel expectedModel, ORMModel actualModel) {

        Set<ORMElement> expectedList = expectedModel.getElements("ValueType");
        Set<ORMElement> actualList = actualModel.getElements("ValueType");

        boolean isEqualLists = true;

        for (ORMElement expElement : expectedList) {
            boolean findEquivalent = false;
            ORMValueType expValueType = (ORMValueType) expElement;
            for (ORMElement actElement : actualList) {
                ORMValueType actValueType = (ORMValueType) actElement;
                if (assertValueTypes(expValueType, actValueType)) {
                    findEquivalent = true;
                    break;
                }
            }
            if (!findEquivalent) {
                System.out.println("В полученной модели отсутствует ValueType \""
                        + expValueType.getName() + "\"");
                isEqualLists = false;
            }
        }

        for (ORMElement actElement : actualList) {
            boolean findEquivalent = false;
            ORMValueType actValueType = (ORMValueType) actElement;
            for (ORMElement expElement : expectedList) {
                ORMValueType expValueType = (ORMValueType) expElement;
                if (assertValueTypes(expValueType, actValueType)) {
                    findEquivalent = true;
                    break;
                }
            }
            if (!findEquivalent) {
                System.out.println("В полученной модели присутствует лишний ValueType \""
                        + actValueType.getName() + "\"");
                isEqualLists = false;
            }
        }
        return isEqualLists;
    }

    private boolean assertUnaryRoles(ORMUnaryRole expected, ORMUnaryRole actual) {
        return
                expected.getName().equals(actual.getName())
            &&  assertEntityTypes(expected.getEntityType(), actual.getEntityType());
    }
    private boolean assertUnaryRolesLists(ORMModel expectedModel, ORMModel actualModel) {

        Set<ORMElement> expectedList = expectedModel.getElements("UnaryRole");
        Set<ORMElement> actualList = actualModel.getElements("UnaryRole");

        boolean isEqualLists = true;

        for (ORMElement expElement : expectedList) {
            boolean findEquivalent = false;
            ORMUnaryRole expUnaryRole = (ORMUnaryRole) expElement;
            for (ORMElement actElement : actualList) {
                ORMUnaryRole actUnaryRole = (ORMUnaryRole) actElement;
                if (assertUnaryRoles(expUnaryRole, actUnaryRole)) {
                    findEquivalent = true;
                    break;
                }
            }
            if (!findEquivalent) {
                System.out.println("В полученной модели отсутствует UnaryRole \""
                        + expUnaryRole.getName() + "\"");
                isEqualLists = false;
            }
        }

        for (ORMElement actElement : actualList) {
            boolean findEquivalent = false;
            ORMUnaryRole actUnaryRole = (ORMUnaryRole) actElement;
            for (ORMElement expElement : expectedList) {
                ORMUnaryRole expUnaryRole = (ORMUnaryRole) expElement;
                if (assertUnaryRoles(expUnaryRole, actUnaryRole)) {
                    findEquivalent = true;
                    break;
                }
            }
            if (!findEquivalent) {
                System.out.println("В полученной модели присутствует лишний ValueType \""
                        + actUnaryRole.getName() + "\"");
                isEqualLists = false;
            }
        }
        return isEqualLists;
    }

    private boolean assertBinaryRoles(ORMBinaryRole expected, ORMBinaryRole actual) {
        return
                expected.getName().equals(actual.getName())
            &&  expected.getInverseRoleName().equals(actual.getInverseRoleName())
            &&  assertEntityTypes(expected.getSource(), actual.getSource())
            &&  assertEntityTypes(expected.getTarget(), actual.getTarget());
    }
    private boolean assertBinaryRolesLists(ORMModel expectedModel, ORMModel actualModel) {

        Set<ORMElement> expectedList = expectedModel.getElements("BinaryRole");
        Set<ORMElement> actualList = actualModel.getElements("BinaryRole");

        boolean isEqualLists = true;

        for (ORMElement expElement : expectedList) {
            boolean findEquivalent = false;
            ORMBinaryRole expBinaryRole = (ORMBinaryRole) expElement;
            for (ORMElement actElement : actualList) {
                ORMBinaryRole actBinaryRole = (ORMBinaryRole) actElement;
                if (assertBinaryRoles(expBinaryRole, actBinaryRole)) {
                    findEquivalent = true;
                    break;
                }
            }
            if (!findEquivalent) {
                System.out.println("В полученной модели отсутствует BinaryRole \""
                        + expBinaryRole.getName() + "\"");
                isEqualLists = false;
            }
        }

        for (ORMElement actElement : actualList) {
            boolean findEquivalent = false;
            ORMBinaryRole actBinaryRole = (ORMBinaryRole) actElement;
            for (ORMElement expElement : expectedList) {
                ORMBinaryRole expBinaryRole = (ORMBinaryRole) expElement;
                if (assertBinaryRoles(expBinaryRole, actBinaryRole)) {
                    findEquivalent = true;
                    break;
                }
            }
            if (!findEquivalent) {
                System.out.println("В полученной модели присутствует лишний BinaryRole \""
                        + actBinaryRole.getName() + "\"");
                isEqualLists = false;
            }
        }
        return isEqualLists;
    }

    private boolean assertModels(ORMModel expectedModel, ORMModel actualModel) {

        return
                assertEntityTypesLists(expectedModel, actualModel)
            &&  assertSubtypesLists(expectedModel, actualModel)
            &&  assertValueTypesLists(expectedModel, actualModel)
            &&  assertUnaryRolesLists(expectedModel, actualModel)
            &&  assertBinaryRolesLists(expectedModel, actualModel);
    }

    @Before
    public void beforeTest() throws Exception {
        model = new ORMModel();
    }

    @Test
    public void test01() throws Exception {

        String testName = "test01";
        setLogPrintStream(testName);
        ORMModel newModel = new ORMModel();
        try {
            newModel = ORM_OWL_Mapper.convertOWLtoORM(makePreparedOntologyFilename(testName));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception();
        }


        ORMEntityType person = new ORMEntityType("Person");
        person.setUpdateStatus("Created");
        model.addElement(person, "EntityType");

        ORMValueType personName = new ORMValueType("has", "PersonName", person);
        personName.setUpdateStatus("Created");
        model.addElement(personName, "ValueType");

        ORMUnaryRole smokes = new ORMUnaryRole("smokes", person);
        smokes.setUpdateStatus("Created");
        model.addElement(smokes, "UnaryRole");

        ORMEntityType committee = new ORMEntityType("Committee");
        committee.setUpdateStatus("Created");
        model.addElement(committee, "EntityType");

        ORMBinaryRole is_a_member_of = new ORMBinaryRole("is_a_member_of", person, committee, "includes");
        is_a_member_of.setUpdateStatus("Created");
        model.addElement(is_a_member_of, "BinaryRole");

        ORMBinaryRole includes = new ORMBinaryRole("includes", committee, person,"is_a_member_of");
        includes.setUpdateStatus("Created");
        model.addElement(includes, "BinaryRole");

        ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee, "is_chaired_by");
        chairs.setUpdateStatus("Created");
        model.addElement(chairs, "BinaryRole");

        ORMBinaryRole is_chaired_by = new ORMBinaryRole("is_chaired_by", committee, person, "chairs");
        is_chaired_by.setUpdateStatus("Created");
        model.addElement(is_chaired_by, "BinaryRole");

        ORMEntityType budget = new ORMEntityType("Budget");
        budget.setUpdateStatus("Created");
        model.addElement(budget, "EntityType");

        ORMBinaryRole has_budget = new ORMBinaryRole("has", committee, budget, "inverse__has");
        has_budget.setUpdateStatus("Created");
        model.addElement(has_budget, "BinaryRole");

        ORMBinaryRole inverse__has = new ORMBinaryRole("inverse__has", budget, committee, "has");
        inverse__has.setUpdateStatus("Created");
        model.addElement(inverse__has, "BinaryRole");

        boolean isEqualsModels = assertModels(model, newModel);

        System.out.println("-----------------------");
        if (isEqualsModels) {
            System.out.println(testName + " пройден успешно");
        } else {
            System.out.println(testName + " провален");
            throw new Exception();
        }
    }

    @Test
    public void test02() throws Exception {

        String testName = "test02";
        setLogPrintStream(testName);
        ORMModel newModel = new ORMModel();
        try {
            newModel = ORM_OWL_Mapper.convertOWLtoORM(makePreparedOntologyFilename(testName));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception();
        }



        ORMEntityType person = new ORMEntityType("Person");
        person.setUpdateStatus("Created");
        model.addElement(person, "EntityType");

        ORMValueType personAge = new ORMValueType("has", "PersonAge", person);
        personAge.setUpdateStatus("Created");
        model.addElement(personAge, "ValueType");

        ORMUnaryRole is_sportsman = new ORMUnaryRole("is_sportsman", person);
        is_sportsman.setUpdateStatus("Created");
        model.addElement(is_sportsman, "UnaryRole");

        ORMEntityType club = new ORMEntityType("Club");
        club.setUpdateStatus("Created");
        model.addElement(club, "EntityType");

        ORMBinaryRole is_a_member_of = new ORMBinaryRole("is_a_member_of", person, club, "includes");
        is_a_member_of.setUpdateStatus("Created");
        model.addElement(is_a_member_of, "BinaryRole");

        ORMBinaryRole includes = new ORMBinaryRole("includes", club, person,"is_a_member_of");
        includes.setUpdateStatus("Created");
        model.addElement(includes, "BinaryRole");

        ORMBinaryRole chairs = new ORMBinaryRole("heads", person, club, "is_headed_by");
        chairs.setUpdateStatus("Created");
        model.addElement(chairs, "BinaryRole");

        ORMBinaryRole is_chaired_by = new ORMBinaryRole("is_headed_by", club, person, "heads");
        is_chaired_by.setUpdateStatus("Created");
        model.addElement(is_chaired_by, "BinaryRole");

        ORMEntityType budget = new ORMEntityType("Budget");
        budget.setUpdateStatus("Created");
        model.addElement(budget, "EntityType");

        ORMBinaryRole has_budget = new ORMBinaryRole("has", person, budget, "inverse__has");
        has_budget.setUpdateStatus("Created");
        model.addElement(has_budget, "BinaryRole");

        ORMBinaryRole inverse__has = new ORMBinaryRole("inverse__has", budget, person, "has");
        inverse__has.setUpdateStatus("Created");
        model.addElement(inverse__has, "BinaryRole");

        boolean isEqualsModels = assertModels(model, newModel);

        System.out.println("-----------------------");
        if (isEqualsModels) {
            System.out.println(testName + " пройден успешно");
        } else {
            System.out.println(testName + " провален");
            throw new Exception();
        }
    }
}
