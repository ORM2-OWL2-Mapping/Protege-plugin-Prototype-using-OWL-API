import org.junit.After;
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

        boolean entityTypesListsIsEqual = assertEntityTypesLists(expectedModel, actualModel);
        boolean subtypesListsIsEqual = assertSubtypesLists(expectedModel, actualModel);
        boolean valueTypesListsIsEqual = assertValueTypesLists(expectedModel, actualModel);
        boolean unaryRolesListsIsEqual = assertUnaryRolesLists(expectedModel, actualModel);
        boolean binaryRolesListsIsEqual = assertBinaryRolesLists(expectedModel, actualModel);

        return
                entityTypesListsIsEqual
            &&  subtypesListsIsEqual
            &&  valueTypesListsIsEqual
            &&  unaryRolesListsIsEqual
            &&  binaryRolesListsIsEqual;
    }

    public String currentTestName;

    @Before
    public void beforeTest() throws Exception {
        model = new ORMModel();
    }

    @After
    public void afterTest() throws Exception {

        setLogPrintStream(currentTestName);
        ORMModel newModel = new ORMModel();
        try {
            newModel = ORM_OWL_Mapper.convertOWLtoORM(makePreparedOntologyFilename(currentTestName));
        } catch (TestOntologyException e) {
            System.out.println(e.getErrorMessage());
            throw new TestFailedException(currentTestName);
        }

        boolean isEqualsModels = assertModels(model, newModel);

        System.out.println("-----------------------");
        if (isEqualsModels) {
            System.out.println(currentTestName + " пройден успешно");
        } else {
            System.out.println(currentTestName + " провален");
            throw new TestFailedException(currentTestName);
        }
    }



    @Test
    public void test01() throws Exception {

        currentTestName = "test01";



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
    }

    @Test
    public void test02() throws Exception {

        currentTestName = "test02";



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
    }

    @Test
    public void test03() throws Exception {

        currentTestName = "test03";



        ORMEntityType person = new ORMEntityType("Person");
        person.setUpdateStatus("Created");
        model.addElement(person, "EntityType");

        ORMEntityType budget = new ORMEntityType("Budget");
        budget.setUpdateStatus("Created");
        model.addElement(budget, "EntityType");

        ORMBinaryRole has_budget = new ORMBinaryRole("has", person, budget, "inverse__has");
        has_budget.setUpdateStatus("Created");
        model.addElement(has_budget, "BinaryRole");

        ORMBinaryRole inverse__has_budget = new ORMBinaryRole("inverse__has", budget, person, "has");
        inverse__has_budget.setUpdateStatus("Created");
        model.addElement(inverse__has_budget, "BinaryRole");
    }

    @Test
    public void test04() throws Exception {

        currentTestName = "test04";



        ORMEntityType book = new ORMEntityType("Book");
        book.setUpdateStatus("Created");
        model.addElement(book, "EntityType");

        ORMValueType bookTitle = new ORMValueType("has", "BookTitle", book);
        bookTitle.setUpdateStatus("Created");
        model.addElement(bookTitle, "ValueType");

        ORMBinaryRole is_translated_from = new ORMBinaryRole("is_translated_from", book, book, "inverse__is_translated_from");
        is_translated_from.setUpdateStatus("Created");
        model.addElement(is_translated_from, "BinaryRole");

        ORMBinaryRole inverse__is_translated_from = new ORMBinaryRole("inverse__is_translated_from", book, book, "is_translated_from");
        inverse__is_translated_from.setUpdateStatus("Created");
        model.addElement(inverse__is_translated_from, "BinaryRole");

        ORMEntityType year = new ORMEntityType("Year");
        year.setUpdateStatus("Created");
        model.addElement(year, "EntityType");

        ORMBinaryRole was_published_in = new ORMBinaryRole("was_published_in", book, year, "inverse__was_published_in");
        was_published_in.setUpdateStatus("Created");
        model.addElement(was_published_in, "BinaryRole");

        ORMBinaryRole inverse__was_published_in = new ORMBinaryRole("inverse__was_published_in", year, book, "was_published_in");
        inverse__was_published_in.setUpdateStatus("Created");
        model.addElement(inverse__was_published_in, "BinaryRole");

        ORMEntityType publishedBook = new ORMEntityType("PublishedBook");
        publishedBook.setUpdateStatus("Created");
        model.addElement(publishedBook, "EntityType");

        ORMSubtyping publishedBook_Book = new ORMSubtyping(publishedBook, book);
        publishedBook_Book.setUpdateStatus("Created");
        model.addElement(publishedBook_Book, "Subtyping");

        ORMUnaryRole is_a_best_seller = new ORMUnaryRole("is_a_best_seller", publishedBook);
        is_a_best_seller.setUpdateStatus("Created");
        model.addElement(is_a_best_seller, "UnaryRole");

        ORMValueType nrCopies = new ORMValueType("sold_total", "NrCopies", publishedBook);
        nrCopies.setUpdateStatus("Created");
        model.addElement(nrCopies, "ValueType");

        ORMEntityType language = new ORMEntityType("Language");
        language.setUpdateStatus("Created");
        model.addElement(language, "EntityType");

        ORMBinaryRole is_written_in = new ORMBinaryRole("is_written_in", book, language, "inverse__is_written_in");
        is_written_in.setUpdateStatus("Created");
        model.addElement(is_written_in, "BinaryRole");

        ORMBinaryRole inverse__is_written_in = new ORMBinaryRole("inverse__is_written_in", language, book, "is_written_in");
        inverse__is_written_in.setUpdateStatus("Created");
        model.addElement(inverse__is_written_in, "BinaryRole");

        ORMEntityType person = new ORMEntityType("Person");
        person.setUpdateStatus("Created");
        model.addElement(person, "EntityType");

        ORMBinaryRole is_assigned_for_review_by = new ORMBinaryRole("is_assigned_for_review_by", book, person, "inverse__is_assigned_for_review_by");
        is_assigned_for_review_by.setUpdateStatus("Created");
        model.addElement(is_assigned_for_review_by, "BinaryRole");

        ORMBinaryRole inverse__is_assigned_for_review_by = new ORMBinaryRole("inverse__is_assigned_for_review_by", person, book, "is_assigned_for_review_by");
        inverse__is_assigned_for_review_by.setUpdateStatus("Created");
        model.addElement(inverse__is_assigned_for_review_by, "BinaryRole");

        ORMBinaryRole is_authored_by = new ORMBinaryRole("is_authored_by", book, person, "authored");
        is_authored_by.setUpdateStatus("Created");
        model.addElement(is_authored_by, "BinaryRole");

        ORMBinaryRole inverse__is_authored_by = new ORMBinaryRole("authored", person, book, "is_authored_by");
        inverse__is_authored_by.setUpdateStatus("Created");
        model.addElement(inverse__is_authored_by, "BinaryRole");

        ORMEntityType editor = new ORMEntityType("Editor");
        editor.setUpdateStatus("Created");
        model.addElement(editor, "EntityType");

        ORMSubtyping editor_Person = new ORMSubtyping(editor, person);
        editor_Person.setUpdateStatus("Created");
        model.addElement(editor_Person, "Subtyping");

        ORMBinaryRole is_assigned = new ORMBinaryRole("is_assigned", book, editor, "inverse__is_assigned");
        is_assigned.setUpdateStatus("Created");
        model.addElement(is_assigned, "BinaryRole");

        ORMBinaryRole inverse__is_assigned = new ORMBinaryRole("inverse__is_assigned", editor, book, "is_assigned");
        inverse__is_assigned.setUpdateStatus("Created");
        model.addElement(inverse__is_assigned, "BinaryRole");
    }

    @Test
    public void test05() throws Exception {

        currentTestName = "test05";



        ORMEntityType book = new ORMEntityType("Book");
        book.setUpdateStatus("Created");
        model.addElement(book, "EntityType");

        ORMValueType bookTitle = new ORMValueType("has", "BookTitle", book);
        bookTitle.setUpdateStatus("Created");
        model.addElement(bookTitle, "ValueType");

        ORMBinaryRole is_translated_from = new ORMBinaryRole("is_translated_from", book, book, "inverse__is_translated_from");
        is_translated_from.setUpdateStatus("Created");
        model.addElement(is_translated_from, "BinaryRole");

        ORMBinaryRole inverse__is_translated_from = new ORMBinaryRole("inverse__is_translated_from", book, book, "is_translated_from");
        inverse__is_translated_from.setUpdateStatus("Created");
        model.addElement(inverse__is_translated_from, "BinaryRole");

        ORMEntityType publishedDate = new ORMEntityType("PublishedDate");
        publishedDate.setUpdateStatus("Created");
        model.addElement(publishedDate, "EntityType");

        ORMBinaryRole has_PublishedDate = new ORMBinaryRole("has", book, publishedDate, "inverse__has");
        has_PublishedDate.setUpdateStatus("Created");
        model.addElement(has_PublishedDate, "BinaryRole");

        ORMBinaryRole inverse__has_PublishedDate = new ORMBinaryRole("inverse__has", publishedDate, book, "has");
        inverse__has_PublishedDate.setUpdateStatus("Created");
        model.addElement(inverse__has_PublishedDate, "BinaryRole");

        ORMEntityType publishedBook = new ORMEntityType("PublishedBook");
        publishedBook.setUpdateStatus("Created");
        model.addElement(publishedBook, "EntityType");

        ORMSubtyping publishedBook_Book = new ORMSubtyping(publishedBook, book);
        publishedBook_Book.setUpdateStatus("Created");
        model.addElement(publishedBook_Book, "Subtyping");

        ORMUnaryRole is_a_popular = new ORMUnaryRole("is_a_popular", book);
        is_a_popular.setUpdateStatus("Created");
        model.addElement(is_a_popular, "UnaryRole");

        ORMValueType nrCopies = new ORMValueType("sold_total", "NrCopies", publishedBook);
        nrCopies.setUpdateStatus("Created");
        model.addElement(nrCopies, "ValueType");

        ORMEntityType language = new ORMEntityType("Language");
        language.setUpdateStatus("Created");
        model.addElement(language, "EntityType");

        ORMBinaryRole is_written_in = new ORMBinaryRole("is_written_in", book, language, "inverse__is_written_in");
        is_written_in.setUpdateStatus("Created");
        model.addElement(is_written_in, "BinaryRole");

        ORMBinaryRole inverse__is_written_in = new ORMBinaryRole("inverse__is_written_in", language, book, "is_written_in");
        inverse__is_written_in.setUpdateStatus("Created");
        model.addElement(inverse__is_written_in, "BinaryRole");

        ORMEntityType person = new ORMEntityType("Person");
        person.setUpdateStatus("Created");
        model.addElement(person, "EntityType");

        ORMBinaryRole is_assigned_for_review_by = new ORMBinaryRole("is_assigned_for_review_by", book, person, "inverse__is_assigned_for_review_by");
        is_assigned_for_review_by.setUpdateStatus("Created");
        model.addElement(is_assigned_for_review_by, "BinaryRole");

        ORMBinaryRole inverse__is_assigned_for_review_by = new ORMBinaryRole("inverse__is_assigned_for_review_by", person, book, "is_assigned_for_review_by");
        inverse__is_assigned_for_review_by.setUpdateStatus("Created");
        model.addElement(inverse__is_assigned_for_review_by, "BinaryRole");

        ORMBinaryRole is_authored_by = new ORMBinaryRole("is_authored_by", book, person, "authored");
        is_authored_by.setUpdateStatus("Created");
        model.addElement(is_authored_by, "BinaryRole");

        ORMBinaryRole inverse__is_authored_by = new ORMBinaryRole("authored", person, book, "is_authored_by");
        inverse__is_authored_by.setUpdateStatus("Created");
        model.addElement(inverse__is_authored_by, "BinaryRole");

        ORMEntityType editor = new ORMEntityType("Editor");
        editor.setUpdateStatus("Created");
        model.addElement(editor, "EntityType");

        ORMSubtyping editor_Person = new ORMSubtyping(editor, person);
        editor_Person.setUpdateStatus("Created");
        model.addElement(editor_Person, "Subtyping");

        ORMBinaryRole is_assigned = new ORMBinaryRole("is_assigned", book, editor, "inverse__is_assigned");
        is_assigned.setUpdateStatus("Created");
        model.addElement(is_assigned, "BinaryRole");

        ORMBinaryRole inverse__is_assigned = new ORMBinaryRole("inverse__is_assigned", editor, book, "is_assigned");
        inverse__is_assigned.setUpdateStatus("Created");
        model.addElement(inverse__is_assigned, "BinaryRole");
    }

    @Test
    public void test06() throws Exception {

        currentTestName = "test06";



        ORMEntityType book = new ORMEntityType("Book");
        book.setUpdateStatus("Created");
        model.addElement(book, "EntityType");

        ORMValueType bookTitle = new ORMValueType("has", "BookTitle", book);
        bookTitle.setUpdateStatus("Created");
        model.addElement(bookTitle, "ValueType");

        ORMEntityType publishedDate = new ORMEntityType("PublishedDate");
        publishedDate.setUpdateStatus("Created");
        model.addElement(publishedDate, "EntityType");

        ORMBinaryRole has_PublishedDate = new ORMBinaryRole("has", book, publishedDate, "inverse__has");
        has_PublishedDate.setUpdateStatus("Created");
        model.addElement(has_PublishedDate, "BinaryRole");

        ORMBinaryRole inverse__has_PublishedDate = new ORMBinaryRole("inverse__has", publishedDate, book, "has");
        inverse__has_PublishedDate.setUpdateStatus("Created");
        model.addElement(inverse__has_PublishedDate, "BinaryRole");

        ORMEntityType language = new ORMEntityType("Language");
        language.setUpdateStatus("Created");
        model.addElement(language, "EntityType");

        ORMBinaryRole is_written_in = new ORMBinaryRole("is_written_in", book, language, "inverse__is_written_in");
        is_written_in.setUpdateStatus("Created");
        model.addElement(is_written_in, "BinaryRole");

        ORMBinaryRole inverse__is_written_in = new ORMBinaryRole("inverse__is_written_in", language, book, "is_written_in");
        inverse__is_written_in.setUpdateStatus("Created");
        model.addElement(inverse__is_written_in, "BinaryRole");

        ORMEntityType person = new ORMEntityType("Person");
        person.setUpdateStatus("Created");
        model.addElement(person, "EntityType");

        ORMBinaryRole is_assigned_for_review_by = new ORMBinaryRole("is_assigned_for_review_by", book, person, "inverse__is_assigned_for_review_by");
        is_assigned_for_review_by.setUpdateStatus("Created");
        model.addElement(is_assigned_for_review_by, "BinaryRole");

        ORMBinaryRole inverse__is_assigned_for_review_by = new ORMBinaryRole("inverse__is_assigned_for_review_by", person, book, "is_assigned_for_review_by");
        inverse__is_assigned_for_review_by.setUpdateStatus("Created");
        model.addElement(inverse__is_assigned_for_review_by, "BinaryRole");

        ORMBinaryRole is_authored_by = new ORMBinaryRole("is_authored_by", book, person, "authored");
        is_authored_by.setUpdateStatus("Created");
        model.addElement(is_authored_by, "BinaryRole");

        ORMBinaryRole inverse__is_authored_by = new ORMBinaryRole("authored", person, book, "is_authored_by");
        inverse__is_authored_by.setUpdateStatus("Created");
        model.addElement(inverse__is_authored_by, "BinaryRole");

        ORMEntityType editor = new ORMEntityType("Editor");
        editor.setUpdateStatus("Created");
        model.addElement(editor, "EntityType");

        ORMSubtyping editor_Person = new ORMSubtyping(editor, person);
        editor_Person.setUpdateStatus("Created");
        model.addElement(editor_Person, "Subtyping");

        ORMBinaryRole is_assigned = new ORMBinaryRole("is_assigned", book, editor, "inverse__is_assigned");
        is_assigned.setUpdateStatus("Created");
        model.addElement(is_assigned, "BinaryRole");

        ORMBinaryRole inverse__is_assigned = new ORMBinaryRole("inverse__is_assigned", editor, book, "is_assigned");
        inverse__is_assigned.setUpdateStatus("Created");
        model.addElement(inverse__is_assigned, "BinaryRole");
    }

    @Test
    public void test07() throws Exception {

        currentTestName = "test07";



        ORMEntityType book = new ORMEntityType("Book");
        book.setUpdateStatus("Created");
        model.addElement(book, "EntityType");

        ORMValueType bookTitle = new ORMValueType("has", "BookTitle", book);
        bookTitle.setUpdateStatus("Created");
        model.addElement(bookTitle, "ValueType");

        ORMEntityType publishedDate = new ORMEntityType("PublishedDate");
        publishedDate.setUpdateStatus("Created");
        model.addElement(publishedDate, "EntityType");

        ORMBinaryRole has_PublishedDate = new ORMBinaryRole("has", book, publishedDate, "inverse__has");
        has_PublishedDate.setUpdateStatus("Created");
        model.addElement(has_PublishedDate, "BinaryRole");

        ORMBinaryRole inverse__has_PublishedDate = new ORMBinaryRole("inverse__has", publishedDate, book, "has");
        inverse__has_PublishedDate.setUpdateStatus("Created");
        model.addElement(inverse__has_PublishedDate, "BinaryRole");

        ORMEntityType language = new ORMEntityType("Language");
        language.setUpdateStatus("Created");
        model.addElement(language, "EntityType");

        ORMBinaryRole is_written_in = new ORMBinaryRole("is_written_in", book, language, "inverse__is_written_in");
        is_written_in.setUpdateStatus("Created");
        model.addElement(is_written_in, "BinaryRole");

        ORMBinaryRole inverse__is_written_in = new ORMBinaryRole("inverse__is_written_in", language, book, "is_written_in");
        inverse__is_written_in.setUpdateStatus("Created");
        model.addElement(inverse__is_written_in, "BinaryRole");

        ORMEntityType editor = new ORMEntityType("Editor");
        editor.setUpdateStatus("Created");
        model.addElement(editor, "EntityType");

        ORMBinaryRole is_assigned = new ORMBinaryRole("is_assigned", book, editor, "inverse__is_assigned");
        is_assigned.setUpdateStatus("Created");
        model.addElement(is_assigned, "BinaryRole");

        ORMBinaryRole inverse__is_assigned = new ORMBinaryRole("inverse__is_assigned", editor, book, "is_assigned");
        inverse__is_assigned.setUpdateStatus("Created");
        model.addElement(inverse__is_assigned, "BinaryRole");

        ORMEntityType artist = new ORMEntityType("Artist");
        artist.setUpdateStatus("Created");
        model.addElement(artist, "EntityType");

        ORMBinaryRole is_assigned_Artist = new ORMBinaryRole("is_assigned", book, artist, "inverse__is_assigned");
        is_assigned_Artist.setUpdateStatus("Created");
        model.addElement(is_assigned_Artist, "BinaryRole");

        ORMBinaryRole inverse__is_assigned_Artist = new ORMBinaryRole("inverse__is_assigned", artist, book, "is_assigned");
        inverse__is_assigned_Artist.setUpdateStatus("Created");
        model.addElement(inverse__is_assigned_Artist, "BinaryRole");
    }

    @Test
    public void test08() throws Exception {

        currentTestName = "test08";



        ORMEntityType food = new ORMEntityType("Food");
        food.setUpdateStatus("Created");
        model.addElement(food, "EntityType");

        ORMEntityType serveSize = new ORMEntityType("ServeSize");
        serveSize.setUpdateStatus("Created");
        model.addElement(serveSize, "EntityType");

        ORMBinaryRole has_standard = new ORMBinaryRole("has_standard", food, serveSize, "inverse__has_standard");
        has_standard.setUpdateStatus("Created");
        model.addElement(has_standard, "BinaryRole");

        ORMBinaryRole inverse__has_standard = new ORMBinaryRole("inverse__has_standard", serveSize, food, "has_standard");
        inverse__has_standard.setUpdateStatus("Created");
        model.addElement(inverse__has_standard, "BinaryRole");

        ORMEntityType form = new ORMEntityType("Form");
        form.setUpdateStatus("Created");
        model.addElement(form, "EntityType");

        ORMBinaryRole has_Form = new ORMBinaryRole("has", food, form, "inverse__has");
        has_Form.setUpdateStatus("Created");
        model.addElement(has_Form, "BinaryRole");

        ORMBinaryRole inverse__has_Form = new ORMBinaryRole("inverse__has", form, food, "has");
        inverse__has_Form.setUpdateStatus("Created");
        model.addElement(inverse__has_Form, "BinaryRole");

        ORMEntityType drink = new ORMEntityType("Drink");
        drink.setUpdateStatus("Created");
        model.addElement(drink, "EntityType");

        ORMSubtyping drink_Food = new ORMSubtyping(drink, food);
        drink_Food.setUpdateStatus("Created");
        model.addElement(drink_Food, "Subtyping");

        ORMEntityType drinkType = new ORMEntityType("DrinkType");
        drinkType.setUpdateStatus("Created");
        model.addElement(drinkType, "EntityType");

        ORMBinaryRole is_of = new ORMBinaryRole("is_of", drink, drinkType, "is_of");
        is_of.setUpdateStatus("Created");
        model.addElement(is_of, "BinaryRole");

        ORMBinaryRole inverse__is_of = new ORMBinaryRole("is_of", drinkType, drink, "is_of");
        inverse__is_of.setUpdateStatus("Created");
        model.addElement(inverse__is_of, "BinaryRole");

        ORMEntityType energy = new ORMEntityType("Energy");
        energy.setUpdateStatus("Created");
        model.addElement(energy, "EntityType");

        ORMBinaryRole provides_per_serve = new ORMBinaryRole("provides...per_serve", drink, energy, "inverse__provides...per_serve");
        provides_per_serve.setUpdateStatus("Created");
        model.addElement(provides_per_serve, "BinaryRole");

        ORMBinaryRole inverse__provides_per_serve = new ORMBinaryRole("inverse__provides...per_serve", energy, drink, "provides...per_serve");
        inverse__provides_per_serve.setUpdateStatus("Created");
        model.addElement(inverse__provides_per_serve, "BinaryRole");

        ORMEntityType alcoholicDrink = new ORMEntityType("Alcoholic_Drink");
        alcoholicDrink.setUpdateStatus("Created");
        model.addElement(alcoholicDrink, "EntityType");

        ORMSubtyping alcoholicDrink_Drink = new ORMSubtyping(alcoholicDrink, drink);
        alcoholicDrink_Drink.setUpdateStatus("Created");
        model.addElement(alcoholicDrink_Drink, "Subtyping");

        ORMValueType hasAlcoholPercent = new ORMValueType("has", "AlcoholPercent", alcoholicDrink);
        hasAlcoholPercent.setUpdateStatus("Created");
        model.addElement(hasAlcoholPercent, "ValueType");

        ORMEntityType nonAlcoholicDrink = new ORMEntityType("NonAlcoholic_Drink");
        nonAlcoholicDrink.setUpdateStatus("Created");
        model.addElement(nonAlcoholicDrink, "EntityType");

        ORMSubtyping nonAlcoholicDrink_Drink = new ORMSubtyping(nonAlcoholicDrink, drink);
        nonAlcoholicDrink_Drink.setUpdateStatus("Created");
        model.addElement(nonAlcoholicDrink_Drink, "Subtyping");

        ORMValueType hasWaterPercent = new ORMValueType("has", "WaterPercent", nonAlcoholicDrink);
        hasWaterPercent.setUpdateStatus("Created");
        model.addElement(hasWaterPercent, "ValueType");

        ORMEntityType milkBasedDrink = new ORMEntityType("MilkBased_Drink");
        milkBasedDrink.setUpdateStatus("Created");
        model.addElement(milkBasedDrink, "EntityType");

        ORMSubtyping milkBasedDrink_nonAlcoholicDrink = new ORMSubtyping(milkBasedDrink, nonAlcoholicDrink);
        milkBasedDrink_nonAlcoholicDrink.setUpdateStatus("Created");
        model.addElement(milkBasedDrink_nonAlcoholicDrink, "Subtyping");

        ORMEntityType fattyAcidType = new ORMEntityType("FattyAcidType");
        fattyAcidType.setUpdateStatus("Created");
        model.addElement(fattyAcidType, "EntityType");

        ORMBinaryRole contains = new ORMBinaryRole("contains", milkBasedDrink, fattyAcidType, "inverse__contains");
        contains.setUpdateStatus("Created");
        model.addElement(contains, "BinaryRole");

        ORMBinaryRole inverse__contains = new ORMBinaryRole("inverse__contains", fattyAcidType, milkBasedDrink, "contains");
        inverse__contains.setUpdateStatus("Created");
        model.addElement(inverse__contains, "BinaryRole");

        ORMEntityType mass = new ORMEntityType("Mass");
        mass.setUpdateStatus("Created");
        model.addElement(mass, "EntityType");

        ORMBinaryRole has_per_serve = new ORMBinaryRole("has...per_serve", drink, mass, "inverse__has...per_serve");
        has_per_serve.setUpdateStatus("Created");
        model.addElement(has_per_serve, "BinaryRole");

        ORMBinaryRole inverse__has_per_serve = new ORMBinaryRole("inverse__has...per_serve", mass, drink, "has...per_serve");
        inverse__has_per_serve.setUpdateStatus("Created");
        model.addElement(inverse__has_per_serve, "BinaryRole");

        ORMBinaryRole has_of_water_per_serve = new ORMBinaryRole("has...of_water_per_serve", nonAlcoholicDrink, mass, "inverse__has...of_water_per_serve");
        has_of_water_per_serve.setUpdateStatus("Created");
        model.addElement(has_of_water_per_serve, "BinaryRole");

        ORMBinaryRole inverse__has_of_water_per_serve = new ORMBinaryRole("inverse__has...of_water_per_serve", mass, nonAlcoholicDrink, "has...of_water_per_serve");
        inverse__has_of_water_per_serve.setUpdateStatus("Created");
        model.addElement(inverse__has_of_water_per_serve, "BinaryRole");

        ORMBinaryRole has_of_cholesterol = new ORMBinaryRole("has...of_cholesterol_per_serve", milkBasedDrink, mass, "inverse__has...of_cholesterol_per_serve");
        has_of_cholesterol.setUpdateStatus("Created");
        model.addElement(has_of_cholesterol, "BinaryRole");

        ORMBinaryRole inverse__has_of_cholesterol = new ORMBinaryRole("inverse__has...of_cholesterol_per_serve", mass, milkBasedDrink, "has...of_cholesterol_per_serve");
        inverse__has_of_cholesterol.setUpdateStatus("Created");
        model.addElement(inverse__has_of_cholesterol, "BinaryRole");
    }

    @Test
    public void test09() throws Exception {

        currentTestName = "test09";



        ORMEntityType food = new ORMEntityType("Food");
        food.setUpdateStatus("Created");
        model.addElement(food, "EntityType");

        ORMEntityType serveSize = new ORMEntityType("ServeSize");
        serveSize.setUpdateStatus("Created");
        model.addElement(serveSize, "EntityType");

        ORMBinaryRole has_standard = new ORMBinaryRole("has_standard", food, serveSize, "inverse__has_standard");
        has_standard.setUpdateStatus("Created");
        model.addElement(has_standard, "BinaryRole");

        ORMBinaryRole inverse__has_standard = new ORMBinaryRole("inverse__has_standard", serveSize, food, "has_standard");
        inverse__has_standard.setUpdateStatus("Created");
        model.addElement(inverse__has_standard, "BinaryRole");

        ORMEntityType form = new ORMEntityType("Form");
        form.setUpdateStatus("Created");
        model.addElement(form, "EntityType");

        ORMBinaryRole has_Form = new ORMBinaryRole("has", food, form, "inverse__has");
        has_Form.setUpdateStatus("Created");
        model.addElement(has_Form, "BinaryRole");

        ORMBinaryRole inverse__has_Form = new ORMBinaryRole("inverse__has", form, food, "has");
        inverse__has_Form.setUpdateStatus("Created");
        model.addElement(inverse__has_Form, "BinaryRole");

        ORMEntityType drink = new ORMEntityType("Drink");
        drink.setUpdateStatus("Created");
        model.addElement(drink, "EntityType");

        ORMSubtyping drink_Food = new ORMSubtyping(drink, food);
        drink_Food.setUpdateStatus("Created");
        model.addElement(drink_Food, "Subtyping");

        ORMEntityType drinkType = new ORMEntityType("DrinkType");
        drinkType.setUpdateStatus("Created");
        model.addElement(drinkType, "EntityType");

        ORMBinaryRole is_of = new ORMBinaryRole("is_of", drink, drinkType, "inverse__is_of");
        is_of.setUpdateStatus("Created");
        model.addElement(is_of, "BinaryRole");

        ORMBinaryRole inverse__is_of = new ORMBinaryRole("inverse__is_of", drinkType, drink, "is_of");
        inverse__is_of.setUpdateStatus("Created");
        model.addElement(inverse__is_of, "BinaryRole");

        ORMEntityType energy = new ORMEntityType("Energy");
        energy.setUpdateStatus("Created");
        model.addElement(energy, "EntityType");

        ORMBinaryRole provides_per_serve = new ORMBinaryRole("provides...per_serve", drink, energy, "inverse__provides...per_serve");
        provides_per_serve.setUpdateStatus("Created");
        model.addElement(provides_per_serve, "BinaryRole");

        ORMBinaryRole inverse__provides_per_serve = new ORMBinaryRole("inverse__provides...per_serve", energy, drink, "provides...per_serve");
        inverse__provides_per_serve.setUpdateStatus("Created");
        model.addElement(inverse__provides_per_serve, "BinaryRole");

        ORMEntityType alcoholicDrink = new ORMEntityType("Alcoholic_Drink");
        alcoholicDrink.setUpdateStatus("Created");
        model.addElement(alcoholicDrink, "EntityType");

        ORMSubtyping alcoholicDrink_Drink = new ORMSubtyping(alcoholicDrink, drink);
        alcoholicDrink_Drink.setUpdateStatus("Created");
        model.addElement(alcoholicDrink_Drink, "Subtyping");

        ORMValueType containsAlcoholPercent = new ORMValueType("contains", "AlcoholPercent", alcoholicDrink);
        containsAlcoholPercent.setUpdateStatus("Created");
        model.addElement(containsAlcoholPercent, "ValueType");

        ORMEntityType nonAlcoholicDrink = new ORMEntityType("NonAlcoholic_Drink");
        nonAlcoholicDrink.setUpdateStatus("Created");
        model.addElement(nonAlcoholicDrink, "EntityType");

        ORMSubtyping nonAlcoholicDrink_Drink = new ORMSubtyping(nonAlcoholicDrink, drink);
        nonAlcoholicDrink_Drink.setUpdateStatus("Created");
        model.addElement(nonAlcoholicDrink_Drink, "Subtyping");

        ORMValueType hasWaterPercent = new ORMValueType("has", "WaterPercent", drink);
        hasWaterPercent.setUpdateStatus("Created");
        model.addElement(hasWaterPercent, "ValueType");

        ORMEntityType milkBasedDrink = new ORMEntityType("MilkBased_Drink");
        milkBasedDrink.setUpdateStatus("Created");
        model.addElement(milkBasedDrink, "EntityType");

        ORMSubtyping milkBasedDrink_alcoholicDrink = new ORMSubtyping(milkBasedDrink, alcoholicDrink);
        milkBasedDrink_alcoholicDrink.setUpdateStatus("Created");
        model.addElement(milkBasedDrink_alcoholicDrink, "Subtyping");

        ORMEntityType fattyAcidType = new ORMEntityType("FattyAcidType");
        fattyAcidType.setUpdateStatus("Created");
        model.addElement(fattyAcidType, "EntityType");

        ORMBinaryRole contains = new ORMBinaryRole("contains", milkBasedDrink, fattyAcidType, "inverse__contains");
        contains.setUpdateStatus("Created");
        model.addElement(contains, "BinaryRole");

        ORMBinaryRole inverse__contains = new ORMBinaryRole("inverse__contains", fattyAcidType, milkBasedDrink, "contains");
        inverse__contains.setUpdateStatus("Created");
        model.addElement(inverse__contains, "BinaryRole");

        ORMEntityType mass = new ORMEntityType("Mass");
        mass.setUpdateStatus("Created");
        model.addElement(mass, "EntityType");

        ORMBinaryRole has_per_serve = new ORMBinaryRole("has...per_serve", drink, mass, "inverse__has...per_serve");
        has_per_serve.setUpdateStatus("Created");
        model.addElement(has_per_serve, "BinaryRole");

        ORMBinaryRole inverse__has_per_serve = new ORMBinaryRole("inverse__has...per_serve", mass, drink, "has...per_serve");
        inverse__has_per_serve.setUpdateStatus("Created");
        model.addElement(inverse__has_per_serve, "BinaryRole");

        ORMBinaryRole has_of_water_per_serve = new ORMBinaryRole("has...of_water_per_serve", nonAlcoholicDrink, mass, "inverse__has...of_water_per_serve");
        has_of_water_per_serve.setUpdateStatus("Created");
        model.addElement(has_of_water_per_serve, "BinaryRole");

        ORMBinaryRole inverse__has_of_water_per_serve = new ORMBinaryRole("inverse__has...of_water_per_serve", mass, nonAlcoholicDrink, "has...of_water_per_serve");
        inverse__has_of_water_per_serve.setUpdateStatus("Created");
        model.addElement(inverse__has_of_water_per_serve, "BinaryRole");

        ORMBinaryRole has_of_cholesterol = new ORMBinaryRole("has...of_cholesterol_per_serve", milkBasedDrink, mass, "inverse__has...of_cholesterol_per_serve");
        has_of_cholesterol.setUpdateStatus("Created");
        model.addElement(has_of_cholesterol, "BinaryRole");

        ORMBinaryRole inverse__has_of_cholesterol = new ORMBinaryRole("inverse__has...of_cholesterol_per_serve", mass, milkBasedDrink, "has...of_cholesterol_per_serve");
        inverse__has_of_cholesterol.setUpdateStatus("Created");
        model.addElement(inverse__has_of_cholesterol, "BinaryRole");
    }

    @Test
    public void test10() throws Exception {

        currentTestName = "test10";



        ORMEntityType drink = new ORMEntityType("Drink");
        drink.setUpdateStatus("Created");
        model.addElement(drink, "EntityType");

        ORMValueType hasWaterPercent = new ORMValueType("has", "WaterPercent", drink);
        hasWaterPercent.setUpdateStatus("Created");
        model.addElement(hasWaterPercent, "ValueType");

        ORMEntityType drinkType = new ORMEntityType("DrinkType");
        drinkType.setUpdateStatus("Created");
        model.addElement(drinkType, "EntityType");

        ORMBinaryRole is_of = new ORMBinaryRole("is_of", drink, drinkType, "inverse__is_of");
        is_of.setUpdateStatus("Created");
        model.addElement(is_of, "BinaryRole");

        ORMBinaryRole inverse__is_of = new ORMBinaryRole("inverse__is_of", drinkType, drink, "is_of");
        inverse__is_of.setUpdateStatus("Created");
        model.addElement(inverse__is_of, "BinaryRole");

        ORMEntityType alcoholicDrink = new ORMEntityType("Alcoholic_Drink");
        alcoholicDrink.setUpdateStatus("Created");
        model.addElement(alcoholicDrink, "EntityType");

        ORMSubtyping alcoholicDrink_Drink = new ORMSubtyping(alcoholicDrink, drink);
        alcoholicDrink_Drink.setUpdateStatus("Created");
        model.addElement(alcoholicDrink_Drink, "Subtyping");

        ORMValueType containsAlcoholPercent = new ORMValueType("contains", "AlcoholPercent", alcoholicDrink);
        containsAlcoholPercent.setUpdateStatus("Created");
        model.addElement(containsAlcoholPercent, "ValueType");

        ORMEntityType nonAlcoholicDrink = new ORMEntityType("NonAlcoholic_Drink");
        nonAlcoholicDrink.setUpdateStatus("Created");
        model.addElement(nonAlcoholicDrink, "EntityType");

        ORMSubtyping nonAlcoholicDrink_Drink = new ORMSubtyping(nonAlcoholicDrink, drink);
        nonAlcoholicDrink_Drink.setUpdateStatus("Created");
        model.addElement(nonAlcoholicDrink_Drink, "Subtyping");

        ORMEntityType milkBasedDrink = new ORMEntityType("MilkBased_Drink");
        milkBasedDrink.setUpdateStatus("Created");
        model.addElement(milkBasedDrink, "EntityType");

        ORMSubtyping milkBasedDrink_alcoholicDrink = new ORMSubtyping(milkBasedDrink, alcoholicDrink);
        milkBasedDrink_alcoholicDrink.setUpdateStatus("Created");
        model.addElement(milkBasedDrink_alcoholicDrink, "Subtyping");

        ORMEntityType fattyAcidType = new ORMEntityType("FattyAcidType");
        fattyAcidType.setUpdateStatus("Created");
        model.addElement(fattyAcidType, "EntityType");

        ORMBinaryRole contains = new ORMBinaryRole("contains", milkBasedDrink, fattyAcidType, "inverse__contains");
        contains.setUpdateStatus("Created");
        model.addElement(contains, "BinaryRole");

        ORMBinaryRole inverse__contains = new ORMBinaryRole("inverse__contains", fattyAcidType, milkBasedDrink, "contains");
        inverse__contains.setUpdateStatus("Created");
        model.addElement(inverse__contains, "BinaryRole");

        ORMEntityType mass = new ORMEntityType("Mass");
        mass.setUpdateStatus("Created");
        model.addElement(mass, "EntityType");

        ORMBinaryRole has_per_serve = new ORMBinaryRole("has...per_serve", drink, mass, "inverse__has...per_serve");
        has_per_serve.setUpdateStatus("Created");
        model.addElement(has_per_serve, "BinaryRole");

        ORMBinaryRole inverse__has_per_serve = new ORMBinaryRole("inverse__has...per_serve", mass, drink, "has...per_serve");
        inverse__has_per_serve.setUpdateStatus("Created");
        model.addElement(inverse__has_per_serve, "BinaryRole");

        ORMBinaryRole has_of_water_per_serve = new ORMBinaryRole("has...of_water_per_serve", nonAlcoholicDrink, mass, "inverse__has...of_water_per_serve");
        has_of_water_per_serve.setUpdateStatus("Created");
        model.addElement(has_of_water_per_serve, "BinaryRole");

        ORMBinaryRole inverse__has_of_water_per_serve = new ORMBinaryRole("inverse__has...of_water_per_serve", mass, nonAlcoholicDrink, "has...of_water_per_serve");
        inverse__has_of_water_per_serve.setUpdateStatus("Created");
        model.addElement(inverse__has_of_water_per_serve, "BinaryRole");
    }

    @Test
    public void test11() throws Exception {

        currentTestName = "test11";



        ORMEntityType drink = new ORMEntityType("Drink");
        drink.setUpdateStatus("Created");
        model.addElement(drink, "EntityType");

        ORMValueType hasWaterPercent = new ORMValueType("has", "WaterPercent", drink);
        hasWaterPercent.setUpdateStatus("Created");
        model.addElement(hasWaterPercent, "ValueType");

        ORMEntityType drinkType = new ORMEntityType("DrinkType");
        drinkType.setUpdateStatus("Created");
        model.addElement(drinkType, "EntityType");

        ORMBinaryRole is_of = new ORMBinaryRole("is_of", drink, drinkType, "inverse__is_of");
        is_of.setUpdateStatus("Created");
        model.addElement(is_of, "BinaryRole");

        ORMBinaryRole inverse__is_of = new ORMBinaryRole("inverse__is_of", drinkType, drink, "is_of");
        inverse__is_of.setUpdateStatus("Created");
        model.addElement(inverse__is_of, "BinaryRole");

        ORMEntityType nonAlcoholicDrink = new ORMEntityType("NonAlcoholic_Drink");
        nonAlcoholicDrink.setUpdateStatus("Created");
        model.addElement(nonAlcoholicDrink, "EntityType");

        ORMSubtyping nonAlcoholicDrink_Drink = new ORMSubtyping(nonAlcoholicDrink, drink);
        nonAlcoholicDrink_Drink.setUpdateStatus("Created");
        model.addElement(nonAlcoholicDrink_Drink, "Subtyping");

        ORMEntityType milkBasedDrink = new ORMEntityType("MilkBased_Drink");
        milkBasedDrink.setUpdateStatus("Created");
        model.addElement(milkBasedDrink, "EntityType");

        ORMSubtyping milkBasedDrink_Drink = new ORMSubtyping(milkBasedDrink, drink);
        milkBasedDrink_Drink.setUpdateStatus("Created");
        model.addElement(milkBasedDrink_Drink, "Subtyping");

        ORMUnaryRole isAlcoholic = new ORMUnaryRole("isAlcoholic", milkBasedDrink);
        isAlcoholic.setUpdateStatus("Created");
        model.addElement(isAlcoholic, "UnaryRole");

        ORMEntityType fattyAcidType = new ORMEntityType("FattyAcidType");
        fattyAcidType.setUpdateStatus("Created");
        model.addElement(fattyAcidType, "EntityType");

        ORMBinaryRole contains = new ORMBinaryRole("contains", milkBasedDrink, fattyAcidType, "inverse__contains");
        contains.setUpdateStatus("Created");
        model.addElement(contains, "BinaryRole");

        ORMBinaryRole inverse__contains = new ORMBinaryRole("inverse__contains", fattyAcidType, milkBasedDrink, "contains");
        inverse__contains.setUpdateStatus("Created");
        model.addElement(inverse__contains, "BinaryRole");

        ORMEntityType mass = new ORMEntityType("Mass");
        mass.setUpdateStatus("Created");
        model.addElement(mass, "EntityType");

        ORMBinaryRole has_per_serve = new ORMBinaryRole("has...per_serve", drink, mass, "inverse__has...per_serve");
        has_per_serve.setUpdateStatus("Created");
        model.addElement(has_per_serve, "BinaryRole");

        ORMBinaryRole inverse__has_per_serve = new ORMBinaryRole("inverse__has...per_serve", mass, drink, "has...per_serve");
        inverse__has_per_serve.setUpdateStatus("Created");
        model.addElement(inverse__has_per_serve, "BinaryRole");

        ORMBinaryRole has_of_water_per_serve = new ORMBinaryRole("has...of_water_per_serve", nonAlcoholicDrink, mass, "inverse__has...of_water_per_serve");
        has_of_water_per_serve.setUpdateStatus("Created");
        model.addElement(has_of_water_per_serve, "BinaryRole");

        ORMBinaryRole inverse__has_of_water_per_serve = new ORMBinaryRole("inverse__has...of_water_per_serve", mass, nonAlcoholicDrink, "has...of_water_per_serve");
        inverse__has_of_water_per_serve.setUpdateStatus("Created");
        model.addElement(inverse__has_of_water_per_serve, "BinaryRole");
    }
}
