import ORMModel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
import org.junit.Test;
import org.junit.Before;
import org.semanticweb.owlapi.model.OWLOntology;

public class ORM_OWL_Testing extends TestingClass {

    public ORM_OWL_Testing() {
        TEST_DIR = System.getProperty("user.dir") + "/tests/ORM_to_OWL/";
    }

    @Before
    public void beforeTest() throws Exception {
        model = new ORMModel();
    }

    @Test
    //@DisplayName("01 - Добавление EntityType в пустую онтологию")
    public void test01() throws Exception {

        String testName = "test01";

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

        ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, committee, "is_chaired_by");
        chairs.setUpdateStatus("Created");
        model.addElement(chairs, "BinaryRole");

        ORMEntityType budget = new ORMEntityType("Budget");
        budget.setUpdateStatus("Created");
        model.addElement(budget, "EntityType");

        ORMBinaryRole has_budget = new ORMBinaryRole("has", committee, budget);
        has_budget.setUpdateStatus("Created");
        model.addElement(has_budget, "BinaryRole");

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, "");
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    public void test02() throws Exception {

        String testName = "test02";

        ORMEntityType person = new ORMEntityType("Person");
        person.setUpdateStatus("Stable");
        model.addElement(person, "EntityType");

        ORMValueType personName = new ORMValueType("has", "PersonName", person);
        personName.setUpdateStatus("Stable");
        //model.addElement(personName, "ValueType");

        ORMValueType personAge = new ORMValueType("has", "PersonAge", person);
        personAge.setUpdateStatus("Modified");
        personAge.setLastState(personName);
        model.addElement(personAge, "ValueType");

        ORMUnaryRole smokes = new ORMUnaryRole("smokes", person);
        smokes.setUpdateStatus("Stable");
        //model.addElement(smokes, "UnaryRole");

        ORMUnaryRole is_sportsman = new ORMUnaryRole("is_sportsman", person);
        is_sportsman.setUpdateStatus("Modified");
        is_sportsman.setLastState(smokes);
        model.addElement(is_sportsman, "UnaryRole");

        ORMEntityType committee = new ORMEntityType("Committee");
        committee.setUpdateStatus("Stable");
        //model.addElement(committee, "EntityType");

        ORMEntityType club = new ORMEntityType("Club");
        club.setUpdateStatus("Modified");
        club.setLastState(committee);
        model.addElement(club, "EntityType");

        ORMBinaryRole is_a_member_of = new ORMBinaryRole("is_a_member_of", person, club, "includes");
        is_a_member_of.setUpdateStatus("Stable");
        model.addElement(is_a_member_of, "BinaryRole");

        ORMBinaryRole chairs = new ORMBinaryRole("chairs", person, club, "is_chaired_by");
        chairs.setUpdateStatus("Stable");
        //model.addElement(chairs, "BinaryRole");

        ORMBinaryRole heads = new ORMBinaryRole("heads", person, club, "is_headed_by");
        heads.setUpdateStatus("Modified");
        heads.setLastState(chairs);
        model.addElement(heads, "BinaryRole");

        ORMEntityType budget = new ORMEntityType("Budget");
        budget.setUpdateStatus("Stable");
        model.addElement(budget, "EntityType");

        ORMBinaryRole has_committee_budget = new ORMBinaryRole("has", club, budget);
        has_committee_budget.setUpdateStatus("Stable");
        //model.addElement(has_budget, "BinaryRole");

        ORMBinaryRole has_budget = new ORMBinaryRole("has", person, budget);
        has_budget.setUpdateStatus("Modified");
        has_budget.setLastState(has_committee_budget);
        model.addElement(has_budget, "BinaryRole");

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    public void test03() throws Exception {

        String testName = "test03";

        ORMEntityType person = new ORMEntityType("Person");
        person.setUpdateStatus("Stable");
        model.addElement(person, "EntityType");

        ORMValueType personAge = new ORMValueType("has", "PersonAge", person);
        personAge.setUpdateStatus("Deleted");
        model.addElement(personAge, "ValueType");

        ORMUnaryRole is_sportsman = new ORMUnaryRole("is_sportsman", person);
        is_sportsman.setUpdateStatus("Deleted");
        model.addElement(is_sportsman, "UnaryRole");

        ORMEntityType club = new ORMEntityType("Club");
        club.setUpdateStatus("Deleted");
        model.addElement(club, "EntityType");

        ORMBinaryRole is_a_member_of = new ORMBinaryRole("is_a_member_of", person, club, "includes");
        is_a_member_of.setUpdateStatus("Deleted");
        model.addElement(is_a_member_of, "BinaryRole");

        ORMBinaryRole heads = new ORMBinaryRole("heads", person, club, "is_headed_by");
        heads.setUpdateStatus("Deleted");
        model.addElement(heads, "BinaryRole");

        ORMEntityType budget = new ORMEntityType("Budget");
        budget.setUpdateStatus("Stable");
        model.addElement(budget, "EntityType");

        ORMBinaryRole has_budget = new ORMBinaryRole("has", person, budget);
        has_budget.setUpdateStatus("Stable");
        model.addElement(has_budget, "BinaryRole");

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    public void test04() throws Exception {

        String testName = "test04";

        ORMEntityType book = new ORMEntityType("Book");
        book.setUpdateStatus("Created");
        model.addElement(book, "EntityType");

        ORMValueType bookTitle = new ORMValueType("has", "BookTitle", book);
        bookTitle.setUpdateStatus("Created");
        model.addElement(bookTitle, "ValueType");

        ORMBinaryRole is_translated_from = new ORMBinaryRole("is_translated_from", book, book);
        is_translated_from.setUpdateStatus("Created");
        model.addElement(is_translated_from, "BinaryRole");

        ORMEntityType year = new ORMEntityType("Year");
        year.setUpdateStatus("Created");
        model.addElement(year, "EntityType");

        ORMBinaryRole was_published_in = new ORMBinaryRole("was_published_in", book, year);
        was_published_in.setUpdateStatus("Created");
        model.addElement(was_published_in, "BinaryRole");

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

        ORMBinaryRole is_written_in = new ORMBinaryRole("is_written_in", book, language);
        is_written_in.setUpdateStatus("Created");
        model.addElement(is_written_in, "BinaryRole");

        ORMEntityType person = new ORMEntityType("Person");
        person.setUpdateStatus("Created");
        model.addElement(person, "EntityType");

        ORMBinaryRole is_assigned_for_review_by = new ORMBinaryRole("is_assigned_for_review_by", book, person);
        is_assigned_for_review_by.setUpdateStatus("Created");
        model.addElement(is_assigned_for_review_by, "BinaryRole");

        ORMBinaryRole is_authored_by = new ORMBinaryRole("is_authored_by", book, person, "authored");
        is_authored_by.setUpdateStatus("Created");
        model.addElement(is_authored_by, "BinaryRole");

        ORMEntityType editor = new ORMEntityType("Editor");
        editor.setUpdateStatus("Created");
        model.addElement(editor, "EntityType");

        ORMSubtyping editor_Person = new ORMSubtyping(editor, person);
        editor_Person.setUpdateStatus("Created");
        model.addElement(editor_Person, "Subtyping");

        ORMBinaryRole is_assigned = new ORMBinaryRole("is_assigned", book, editor);
        is_assigned.setUpdateStatus("Created");
        model.addElement(is_assigned, "BinaryRole");

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, "");
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    public void test05() throws Exception {

        String testName = "test05";

        ORMEntityType book = new ORMEntityType("Book");
        book.setUpdateStatus("Stable");
        model.addElement(book, "EntityType");

        ORMValueType bookTitle = new ORMValueType("has", "BookTitle", book);
        bookTitle.setUpdateStatus("Stable");
        model.addElement(bookTitle, "ValueType");

        ORMBinaryRole is_translated_from = new ORMBinaryRole("is_translated_from", book, book);
        is_translated_from.setUpdateStatus("Stable");
        model.addElement(is_translated_from, "BinaryRole");

        ORMEntityType year = new ORMEntityType("Year");
        year.setUpdateStatus("Stable");
        //model.addElement(year, "EntityType");

        ORMEntityType publishedDate = new ORMEntityType("PublishedDate");
        publishedDate.setUpdateStatus("Modified");
        publishedDate.setLastState(year);
        model.addElement(publishedDate, "EntityType");

        ORMBinaryRole was_published_in = new ORMBinaryRole("was_published_in", book, publishedDate);
        was_published_in.setUpdateStatus("Stable");
        //model.addElement(was_published_in, "BinaryRole");

        ORMBinaryRole has_PublishedDate = new ORMBinaryRole("has", book, publishedDate);
        has_PublishedDate.setUpdateStatus("Modified");
        has_PublishedDate.setLastState(was_published_in);
        model.addElement(has_PublishedDate, "BinaryRole");

        ORMEntityType publishedBook = new ORMEntityType("PublishedBook");
        publishedBook.setUpdateStatus("Stable");
        model.addElement(publishedBook, "EntityType");

        ORMSubtyping publishedBook_Book = new ORMSubtyping(publishedBook, book);
        publishedBook_Book.setUpdateStatus("Stable");
        model.addElement(publishedBook_Book, "Subtyping");

        ORMUnaryRole is_a_best_seller = new ORMUnaryRole("is_a_best_seller", publishedBook);
        is_a_best_seller.setUpdateStatus("Stable");
        //model.addElement(is_a_best_seller, "UnaryRole");

        ORMUnaryRole is_a_popular = new ORMUnaryRole("is_a_popular", book);
        is_a_popular.setUpdateStatus("Modified");
        is_a_popular.setLastState(is_a_best_seller);
        model.addElement(is_a_popular, "UnaryRole");

        ORMValueType nrCopies = new ORMValueType("sold_total", "NrCopies", publishedBook);
        nrCopies.setUpdateStatus("Stable");
        model.addElement(nrCopies, "ValueType");

        ORMEntityType language = new ORMEntityType("Language");
        language.setUpdateStatus("Stable");
        model.addElement(language, "EntityType");

        ORMBinaryRole is_written_in = new ORMBinaryRole("is_written_in", book, language);
        is_written_in.setUpdateStatus("Stable");
        model.addElement(is_written_in, "BinaryRole");

        ORMEntityType person = new ORMEntityType("Person");
        person.setUpdateStatus("Stable");
        model.addElement(person, "EntityType");

        ORMBinaryRole is_assigned_for_review_by = new ORMBinaryRole("is_assigned_for_review_by", book, person);
        is_assigned_for_review_by.setUpdateStatus("Stable");
        model.addElement(is_assigned_for_review_by, "BinaryRole");

        ORMBinaryRole is_authored_by = new ORMBinaryRole("is_authored_by", book, person, "authored");
        is_authored_by.setUpdateStatus("Stable");
        model.addElement(is_authored_by, "BinaryRole");

        ORMEntityType editor = new ORMEntityType("Editor");
        editor.setUpdateStatus("Stable");
        model.addElement(editor, "EntityType");

        ORMSubtyping editor_Person = new ORMSubtyping(editor, person);
        editor_Person.setUpdateStatus("Stable");
        model.addElement(editor_Person, "Subtyping");

        ORMBinaryRole is_assigned = new ORMBinaryRole("is_assigned", book, editor);
        is_assigned.setUpdateStatus("Stable");
        model.addElement(is_assigned, "BinaryRole");

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    public void test06() throws Exception {

        String testName = "test06";

        ORMEntityType book = new ORMEntityType("Book");
        book.setUpdateStatus("Stable");
        model.addElement(book, "EntityType");

        ORMValueType bookTitle = new ORMValueType("has", "BookTitle", book);
        bookTitle.setUpdateStatus("Stable");
        model.addElement(bookTitle, "ValueType");

        ORMBinaryRole is_translated_from = new ORMBinaryRole("is_translated_from", book, book);
        is_translated_from.setUpdateStatus("Deleted");
        model.addElement(is_translated_from, "BinaryRole");

        ORMEntityType publishedDate = new ORMEntityType("PublishedDate");
        publishedDate.setUpdateStatus("Stable");
        model.addElement(publishedDate, "EntityType");

        ORMBinaryRole has_PublishedDate = new ORMBinaryRole("has", book, publishedDate);
        has_PublishedDate.setUpdateStatus("Stable");
        model.addElement(has_PublishedDate, "BinaryRole");

        ORMEntityType publishedBook = new ORMEntityType("PublishedBook");
        publishedBook.setUpdateStatus("Deleted");
        model.addElement(publishedBook, "EntityType");

        ORMSubtyping publishedBook_Book = new ORMSubtyping(publishedBook, book);
        publishedBook_Book.setUpdateStatus("Deleted");
        model.addElement(publishedBook_Book, "Subtyping");

        ORMUnaryRole is_a_popular = new ORMUnaryRole("is_a_popular", book);
        is_a_popular.setUpdateStatus("Deleted");
        model.addElement(is_a_popular, "UnaryRole");

        ORMValueType nrCopies = new ORMValueType("sold_total", "NrCopies", publishedBook);
        nrCopies.setUpdateStatus("Deleted");
        model.addElement(nrCopies, "ValueType");

        ORMEntityType language = new ORMEntityType("Language");
        language.setUpdateStatus("Stable");
        model.addElement(language, "EntityType");

        ORMBinaryRole is_written_in = new ORMBinaryRole("is_written_in", book, language);
        is_written_in.setUpdateStatus("Stable");
        model.addElement(is_written_in, "BinaryRole");

        ORMEntityType person = new ORMEntityType("Person");
        person.setUpdateStatus("Stable");
        model.addElement(person, "EntityType");

        ORMBinaryRole is_assigned_for_review_by = new ORMBinaryRole("is_assigned_for_review_by", book, person);
        is_assigned_for_review_by.setUpdateStatus("Stable");
        model.addElement(is_assigned_for_review_by, "BinaryRole");

        ORMBinaryRole is_authored_by = new ORMBinaryRole("is_authored_by", book, person, "authored");
        is_authored_by.setUpdateStatus("Stable");
        model.addElement(is_authored_by, "BinaryRole");

        ORMEntityType editor = new ORMEntityType("Editor");
        editor.setUpdateStatus("Stable");
        model.addElement(editor, "EntityType");

        ORMSubtyping editor_Person = new ORMSubtyping(editor, person);
        editor_Person.setUpdateStatus("Stable");
        model.addElement(editor_Person, "Subtyping");

        ORMBinaryRole is_assigned = new ORMBinaryRole("is_assigned", book, editor);
        is_assigned.setUpdateStatus("Stable");
        model.addElement(is_assigned, "BinaryRole");

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }

    @Test
    public void test07() throws Exception {

        String testName = "test07";

        ORMEntityType book = new ORMEntityType("Book");
        book.setUpdateStatus("Stable");
        model.addElement(book, "EntityType");

        ORMValueType bookTitle = new ORMValueType("has", "BookTitle", book);
        bookTitle.setUpdateStatus("Stable");
        model.addElement(bookTitle, "ValueType");

        ORMEntityType publishedDate = new ORMEntityType("PublishedDate");
        publishedDate.setUpdateStatus("Stable");
        model.addElement(publishedDate, "EntityType");

        ORMBinaryRole has_PublishedDate = new ORMBinaryRole("has", book, publishedDate);
        has_PublishedDate.setUpdateStatus("Stable");
        model.addElement(has_PublishedDate, "BinaryRole");

        ORMEntityType language = new ORMEntityType("Language");
        language.setUpdateStatus("Stable");
        model.addElement(language, "EntityType");

        ORMBinaryRole is_written_in = new ORMBinaryRole("is_written_in", book, language);
        is_written_in.setUpdateStatus("Stable");
        model.addElement(is_written_in, "BinaryRole");

        ORMEntityType person = new ORMEntityType("Person");
        person.setUpdateStatus("Deleted");
        model.addElement(person, "EntityType");

        ORMBinaryRole is_assigned_for_review_by = new ORMBinaryRole("is_assigned_for_review_by", book, person);
        is_assigned_for_review_by.setUpdateStatus("Deleted");
        model.addElement(is_assigned_for_review_by, "BinaryRole");

        ORMBinaryRole is_authored_by = new ORMBinaryRole("is_authored_by", book, person, "authored");
        is_authored_by.setUpdateStatus("Deleted");
        model.addElement(is_authored_by, "BinaryRole");

        ORMEntityType editor = new ORMEntityType("Editor");
        editor.setUpdateStatus("Stable");
        model.addElement(editor, "EntityType");

        ORMSubtyping editor_Person = new ORMSubtyping(editor, person);
        editor_Person.setUpdateStatus("Deleted");
        model.addElement(editor_Person, "Subtyping");

        ORMBinaryRole is_assigned = new ORMBinaryRole("is_assigned", book, editor);
        is_assigned.setUpdateStatus("Stable");
        model.addElement(is_assigned, "BinaryRole");

        ORMEntityType artist = new ORMEntityType("Artist");
        artist.setUpdateStatus("Created");
        model.addElement(artist, "EntityType");

        ORMBinaryRole is_assigned_Artist = new ORMBinaryRole("is_assigned", book, artist);
        is_assigned_Artist.setUpdateStatus("Created");
        model.addElement(is_assigned_Artist, "BinaryRole");

        OWLOntology ontology = ORM_OWL_Mapper.convertORMtoOWL(model, makePreparedOntologyFilename(testName));
        saveOntologyInFile(ontology, makeActualOntologyFilename(testName));

        compareOntologies(makeExpectedOntologyFilename(testName), makeActualOntologyFilename(testName));
    }
}
