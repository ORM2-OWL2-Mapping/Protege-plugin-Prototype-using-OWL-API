import ORMModel.*;
import org.junit.After;
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

    public String currentTestName;
    public boolean preparedOntologyIsExist = true;

    @Before
    public void beforeTest() throws Exception {
        model = new ORMModel();
        preparedOntologyIsExist = true;
    }

    @After
    public void afterTest() throws Exception {

        setLogPrintStream(currentTestName);
        OWLOntology ontology = null;
        String pathToOntology = preparedOntologyIsExist ? makePreparedOntologyFilename(currentTestName) : "";
        try {
            ontology = ORM_OWL_Mapper.convertORMtoOWL(model, pathToOntology);
        } catch (TestOntologyException e) {
            System.out.println(e.getErrorMessage());
            throw new TestFailedException(currentTestName);
        }

        saveOntologyInFile(ontology, makeActualOntologyFilename(currentTestName));

        try {
            compareOntologies(makeExpectedOntologyFilename(currentTestName), makeActualOntologyFilename(currentTestName));
            System.out.println("-----------------------");
            System.out.println(currentTestName + " пройден успешно");
        } catch (TestOntologyException e) {
            System.out.println(e.getErrorMessage());
            System.out.println("-----------------------");
            System.out.println(currentTestName + " провален");
            throw new TestFailedException(currentTestName);
        }
    }

    @Test
    //@DisplayName("01 - Добавление EntityType в пустую онтологию")
    public void test01() throws Exception {

        currentTestName = "test01";
        preparedOntologyIsExist = false;

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
    }

    @Test
    public void test02() throws Exception {

        currentTestName = "test02";



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
    }

    @Test
    public void test03() throws Exception {

        currentTestName = "test03";



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
    }

    @Test
    public void test04() throws Exception {

        currentTestName = "test04";
        preparedOntologyIsExist = false;



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
    }

    @Test
    public void test05() throws Exception {

        currentTestName = "test05";



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
    }

    @Test
    public void test06() throws Exception {

        currentTestName = "test06";



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
    }

    @Test
    public void test07() throws Exception {

        currentTestName = "test07";



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
    }

    @Test
    public void test08() throws Exception {

        currentTestName = "test08";
        preparedOntologyIsExist = false;



        ORMEntityType food = new ORMEntityType("Food");
        food.setUpdateStatus("Created");
        model.addElement(food, "EntityType");

        ORMEntityType serveSize = new ORMEntityType("ServeSize");
        serveSize.setUpdateStatus("Created");
        model.addElement(serveSize, "EntityType");

        ORMBinaryRole has_standard = new ORMBinaryRole("has_standard", food, serveSize);
        has_standard.setUpdateStatus("Created");
        model.addElement(has_standard, "BinaryRole");

        ORMEntityType form = new ORMEntityType("Form");
        form.setUpdateStatus("Created");
        model.addElement(form, "EntityType");

        ORMBinaryRole has_Form = new ORMBinaryRole("has", food, form);
        has_Form.setUpdateStatus("Created");
        model.addElement(has_Form, "BinaryRole");

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

        ORMEntityType energy = new ORMEntityType("Energy");
        energy.setUpdateStatus("Created");
        model.addElement(energy, "EntityType");

        ORMBinaryRole provides_per_serve = new ORMBinaryRole("provides...per_serve", drink, energy);
        provides_per_serve.setUpdateStatus("Created");
        model.addElement(provides_per_serve, "BinaryRole");

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

        ORMBinaryRole contains = new ORMBinaryRole("contains", milkBasedDrink, fattyAcidType);
        contains.setUpdateStatus("Created");
        model.addElement(contains, "BinaryRole");

        ORMEntityType mass = new ORMEntityType("Mass");
        mass.setUpdateStatus("Created");
        model.addElement(mass, "EntityType");

        ORMBinaryRole has_per_serve = new ORMBinaryRole("has...per_serve", drink, mass);
        has_per_serve.setUpdateStatus("Created");
        model.addElement(has_per_serve, "BinaryRole");

        ORMBinaryRole has_of_water_per_serve = new ORMBinaryRole("has...of_water_per_serve", nonAlcoholicDrink, mass);
        has_of_water_per_serve.setUpdateStatus("Created");
        model.addElement(has_of_water_per_serve, "BinaryRole");

        ORMBinaryRole has_of_cholesterol = new ORMBinaryRole("has...of_cholesterol_per_serve", milkBasedDrink, mass);
        has_of_cholesterol.setUpdateStatus("Created");
        model.addElement(has_of_cholesterol, "BinaryRole");
    }

    @Test
    public void test09() throws Exception {

        currentTestName = "test09";



        ORMEntityType food = new ORMEntityType("Food");
        food.setUpdateStatus("Stable");
        model.addElement(food, "EntityType");

        ORMEntityType serveSize = new ORMEntityType("ServeSize");
        serveSize.setUpdateStatus("Stable");
        model.addElement(serveSize, "EntityType");

        ORMBinaryRole has_standard = new ORMBinaryRole("has_standard", food, serveSize);
        has_standard.setUpdateStatus("Stable");
        model.addElement(has_standard, "BinaryRole");

        ORMEntityType form = new ORMEntityType("Form");
        form.setUpdateStatus("Stable");
        model.addElement(form, "EntityType");

        ORMBinaryRole has_Form = new ORMBinaryRole("has", food, form);
        has_Form.setUpdateStatus("Stable");
        model.addElement(has_Form, "BinaryRole");

        ORMEntityType drink = new ORMEntityType("Drink");
        drink.setUpdateStatus("Stable");
        model.addElement(drink, "EntityType");

        ORMSubtyping drink_Food = new ORMSubtyping(drink, food);
        drink_Food.setUpdateStatus("Stable");
        model.addElement(drink_Food, "Subtyping");

        ORMEntityType drinkType = new ORMEntityType("DrinkType");
        drinkType.setUpdateStatus("Stable");
        model.addElement(drinkType, "EntityType");

        ORMBinaryRole is_of_is_of = new ORMBinaryRole("is_of", drink, drinkType, "is_of");
        is_of_is_of.setUpdateStatus("Stable");
        //model.addElement(is_of_is_of, "BinaryRole");

        ORMBinaryRole is_of = new ORMBinaryRole("is_of", drink, drinkType);
        is_of.setUpdateStatus("Modified");
        is_of.setLastState(is_of_is_of);
        model.addElement(is_of, "BinaryRole");

        ORMEntityType energy = new ORMEntityType("Energy");
        energy.setUpdateStatus("Stable");
        model.addElement(energy, "EntityType");

        ORMBinaryRole provides_per_serve = new ORMBinaryRole("provides...per_serve", drink, energy);
        provides_per_serve.setUpdateStatus("Stable");
        model.addElement(provides_per_serve, "BinaryRole");

        ORMEntityType alcoholicDrink = new ORMEntityType("Alcoholic_Drink");
        alcoholicDrink.setUpdateStatus("Stable");
        model.addElement(alcoholicDrink, "EntityType");

        ORMSubtyping alcoholicDrink_Drink = new ORMSubtyping(alcoholicDrink, drink);
        alcoholicDrink_Drink.setUpdateStatus("Stable");
        model.addElement(alcoholicDrink_Drink, "Subtyping");

        ORMValueType hasAlcoholPercent = new ORMValueType("has", "AlcoholPercent", alcoholicDrink);
        hasAlcoholPercent.setUpdateStatus("Stable");
        //model.addElement(hasAlcoholPercent, "ValueType");

        ORMValueType containsAlcoholPercent = new ORMValueType("contains", "AlcoholPercent", alcoholicDrink);
        containsAlcoholPercent.setUpdateStatus("Modified");
        containsAlcoholPercent.setLastState(hasAlcoholPercent);
        model.addElement(containsAlcoholPercent, "ValueType");

        ORMEntityType nonAlcoholicDrink = new ORMEntityType("NonAlcoholic_Drink");
        nonAlcoholicDrink.setUpdateStatus("Stable");
        model.addElement(nonAlcoholicDrink, "EntityType");

        ORMSubtyping nonAlcoholicDrink_Drink = new ORMSubtyping(nonAlcoholicDrink, drink);
        nonAlcoholicDrink_Drink.setUpdateStatus("Stable");
        model.addElement(nonAlcoholicDrink_Drink, "Subtyping");

        ORMValueType hasWaterPercent_NonAlcoholic = new ORMValueType("has", "WaterPercent", nonAlcoholicDrink);
        hasWaterPercent_NonAlcoholic.setUpdateStatus("Stable");
        //model.addElement(hasWaterPercent_NonAlcoholic, "ValueType");

        ORMValueType hasWaterPercent = new ORMValueType("has", "WaterPercent", drink);
        hasWaterPercent.setUpdateStatus("Modified");
        hasWaterPercent.setLastState(hasWaterPercent_NonAlcoholic);
        model.addElement(hasWaterPercent, "ValueType");

        ORMEntityType milkBasedDrink = new ORMEntityType("MilkBased_Drink");
        milkBasedDrink.setUpdateStatus("Stable");
        model.addElement(milkBasedDrink, "EntityType");

        ORMSubtyping milkBasedDrink_nonAlcoholicDrink = new ORMSubtyping(milkBasedDrink, nonAlcoholicDrink);
        milkBasedDrink_nonAlcoholicDrink.setUpdateStatus("Stable");
        //model.addElement(milkBasedDrink_nonAlcoholicDrink, "Subtyping");

        ORMSubtyping milkBasedDrink_alcoholicDrink = new ORMSubtyping(milkBasedDrink, alcoholicDrink);
        milkBasedDrink_alcoholicDrink.setUpdateStatus("Modified");
        milkBasedDrink_alcoholicDrink.setLastState(milkBasedDrink_nonAlcoholicDrink);
        model.addElement(milkBasedDrink_alcoholicDrink, "Subtyping");

        ORMEntityType fattyAcidType = new ORMEntityType("FattyAcidType");
        fattyAcidType.setUpdateStatus("Stable");
        model.addElement(fattyAcidType, "EntityType");

        ORMBinaryRole contains = new ORMBinaryRole("contains", milkBasedDrink, fattyAcidType);
        contains.setUpdateStatus("Stable");
        model.addElement(contains, "BinaryRole");

        ORMEntityType mass = new ORMEntityType("Mass");
        mass.setUpdateStatus("Stable");
        model.addElement(mass, "EntityType");

        ORMBinaryRole has_per_serve = new ORMBinaryRole("has...per_serve", drink, mass);
        has_per_serve.setUpdateStatus("Stable");
        model.addElement(has_per_serve, "BinaryRole");

        ORMBinaryRole has_of_water_per_serve = new ORMBinaryRole("has...of_water_per_serve", nonAlcoholicDrink, mass);
        has_of_water_per_serve.setUpdateStatus("Stable");
        model.addElement(has_of_water_per_serve, "BinaryRole");

        ORMBinaryRole has_of_cholesterol = new ORMBinaryRole("has...of_cholesterol_per_serve", milkBasedDrink, mass);
        has_of_cholesterol.setUpdateStatus("Stable");
        model.addElement(has_of_cholesterol, "BinaryRole");
    }

    @Test
    public void test10() throws Exception {

        currentTestName = "test10";



        ORMEntityType food = new ORMEntityType("Food");
        food.setUpdateStatus("Deleted");
        model.addElement(food, "EntityType");

        ORMEntityType serveSize = new ORMEntityType("ServeSize");
        serveSize.setUpdateStatus("Deleted");
        model.addElement(serveSize, "EntityType");

        ORMBinaryRole has_standard = new ORMBinaryRole("has_standard", food, serveSize);
        has_standard.setUpdateStatus("Deleted");
        model.addElement(has_standard, "BinaryRole");

        ORMEntityType form = new ORMEntityType("Form");
        form.setUpdateStatus("Deleted");
        model.addElement(form, "EntityType");

        ORMBinaryRole has_Form = new ORMBinaryRole("has", food, form);
        has_Form.setUpdateStatus("Deleted");
        model.addElement(has_Form, "BinaryRole");

        ORMEntityType drink = new ORMEntityType("Drink");
        drink.setUpdateStatus("Stable");
        model.addElement(drink, "EntityType");

        ORMSubtyping drink_Food = new ORMSubtyping(drink, food);
        drink_Food.setUpdateStatus("Deleted");
        model.addElement(drink_Food, "Subtyping");

        ORMEntityType drinkType = new ORMEntityType("DrinkType");
        drinkType.setUpdateStatus("Stable");
        model.addElement(drinkType, "EntityType");

        ORMBinaryRole is_of = new ORMBinaryRole("is_of", drink, drinkType);
        is_of.setUpdateStatus("Stable");
        model.addElement(is_of, "BinaryRole");

        ORMEntityType energy = new ORMEntityType("Energy");
        energy.setUpdateStatus("Deleted");
        model.addElement(energy, "EntityType");

        ORMBinaryRole provides_per_serve = new ORMBinaryRole("provides...per_serve", drink, energy);
        provides_per_serve.setUpdateStatus("Deleted");
        model.addElement(provides_per_serve, "BinaryRole");

        ORMEntityType alcoholicDrink = new ORMEntityType("Alcoholic_Drink");
        alcoholicDrink.setUpdateStatus("Stable");
        model.addElement(alcoholicDrink, "EntityType");

        ORMSubtyping alcoholicDrink_Drink = new ORMSubtyping(alcoholicDrink, drink);
        alcoholicDrink_Drink.setUpdateStatus("Stable");
        model.addElement(alcoholicDrink_Drink, "Subtyping");

        ORMValueType containsAlcoholPercent = new ORMValueType("contains", "AlcoholPercent", alcoholicDrink);
        containsAlcoholPercent.setUpdateStatus("Stable");
        model.addElement(containsAlcoholPercent, "ValueType");

        ORMEntityType nonAlcoholicDrink = new ORMEntityType("NonAlcoholic_Drink");
        nonAlcoholicDrink.setUpdateStatus("Stable");
        model.addElement(nonAlcoholicDrink, "EntityType");

        ORMSubtyping nonAlcoholicDrink_Drink = new ORMSubtyping(nonAlcoholicDrink, drink);
        nonAlcoholicDrink_Drink.setUpdateStatus("Stable");
        model.addElement(nonAlcoholicDrink_Drink, "Subtyping");

        ORMValueType hasWaterPercent = new ORMValueType("has", "WaterPercent", drink);
        hasWaterPercent.setUpdateStatus("Stable");
        model.addElement(hasWaterPercent, "ValueType");

        ORMEntityType milkBasedDrink = new ORMEntityType("MilkBased_Drink");
        milkBasedDrink.setUpdateStatus("Stable");
        model.addElement(milkBasedDrink, "EntityType");

        ORMSubtyping milkBasedDrink_alcoholicDrink = new ORMSubtyping(milkBasedDrink, alcoholicDrink);
        milkBasedDrink_alcoholicDrink.setUpdateStatus("Stable");
        model.addElement(milkBasedDrink_alcoholicDrink, "Subtyping");

        ORMEntityType fattyAcidType = new ORMEntityType("FattyAcidType");
        fattyAcidType.setUpdateStatus("Stable");
        model.addElement(fattyAcidType, "EntityType");

        ORMBinaryRole contains = new ORMBinaryRole("contains", milkBasedDrink, fattyAcidType);
        contains.setUpdateStatus("Stable");
        model.addElement(contains, "BinaryRole");

        ORMEntityType mass = new ORMEntityType("Mass");
        mass.setUpdateStatus("Stable");
        model.addElement(mass, "EntityType");

        ORMBinaryRole has_per_serve = new ORMBinaryRole("has...per_serve", drink, mass);
        has_per_serve.setUpdateStatus("Stable");
        model.addElement(has_per_serve, "BinaryRole");

        ORMBinaryRole has_of_water_per_serve = new ORMBinaryRole("has...of_water_per_serve", nonAlcoholicDrink, mass);
        has_of_water_per_serve.setUpdateStatus("Stable");
        model.addElement(has_of_water_per_serve, "BinaryRole");

        ORMBinaryRole has_of_cholesterol = new ORMBinaryRole("has...of_cholesterol_per_serve", milkBasedDrink, mass);
        has_of_cholesterol.setUpdateStatus("Deleted");
        model.addElement(has_of_cholesterol, "BinaryRole");
    }

    @Test
    public void test11() throws Exception {

        currentTestName = "test11";



        ORMEntityType drink = new ORMEntityType("Drink");
        drink.setUpdateStatus("Stable");
        model.addElement(drink, "EntityType");

        ORMEntityType drinkType = new ORMEntityType("DrinkType");
        drinkType.setUpdateStatus("Stable");
        model.addElement(drinkType, "EntityType");

        ORMBinaryRole is_of = new ORMBinaryRole("is_of", drink, drinkType);
        is_of.setUpdateStatus("Stable");
        model.addElement(is_of, "BinaryRole");

        ORMEntityType alcoholicDrink = new ORMEntityType("Alcoholic_Drink");
        alcoholicDrink.setUpdateStatus("Deleted");
        model.addElement(alcoholicDrink, "EntityType");

        ORMSubtyping alcoholicDrink_Drink = new ORMSubtyping(alcoholicDrink, drink);
        alcoholicDrink_Drink.setUpdateStatus("Deleted");
        model.addElement(alcoholicDrink_Drink, "Subtyping");

        ORMValueType containsAlcoholPercent = new ORMValueType("contains", "AlcoholPercent", alcoholicDrink);
        containsAlcoholPercent.setUpdateStatus("Deleted");
        model.addElement(containsAlcoholPercent, "ValueType");

        ORMEntityType nonAlcoholicDrink = new ORMEntityType("NonAlcoholic_Drink");
        nonAlcoholicDrink.setUpdateStatus("Stable");
        model.addElement(nonAlcoholicDrink, "EntityType");

        ORMSubtyping nonAlcoholicDrink_Drink = new ORMSubtyping(nonAlcoholicDrink, drink);
        nonAlcoholicDrink_Drink.setUpdateStatus("Stable");
        model.addElement(nonAlcoholicDrink_Drink, "Subtyping");

        ORMValueType hasWaterPercent = new ORMValueType("has", "WaterPercent", drink);
        hasWaterPercent.setUpdateStatus("Stable");
        model.addElement(hasWaterPercent, "ValueType");

        ORMEntityType milkBasedDrink = new ORMEntityType("MilkBased_Drink");
        milkBasedDrink.setUpdateStatus("Stable");
        model.addElement(milkBasedDrink, "EntityType");

        ORMUnaryRole isAlcoholic = new ORMUnaryRole("isAlcoholic", milkBasedDrink);
        isAlcoholic.setUpdateStatus("Created");
        model.addElement(isAlcoholic, "UnaryRole");

        ORMSubtyping milkBasedDrink_alcoholicDrink = new ORMSubtyping(milkBasedDrink, alcoholicDrink);
        milkBasedDrink_alcoholicDrink.setUpdateStatus("Stable");
        //model.addElement(milkBasedDrink_alcoholicDrink, "Subtyping");

        ORMSubtyping milkBasedDrink_drink = new ORMSubtyping(milkBasedDrink, drink);
        milkBasedDrink_drink.setUpdateStatus("Modified");
        milkBasedDrink_drink.setLastState(milkBasedDrink_alcoholicDrink);
        model.addElement(milkBasedDrink_drink, "Subtyping");

        ORMEntityType fattyAcidType = new ORMEntityType("FattyAcidType");
        fattyAcidType.setUpdateStatus("Stable");
        model.addElement(fattyAcidType, "EntityType");

        ORMBinaryRole contains = new ORMBinaryRole("contains", milkBasedDrink, fattyAcidType);
        contains.setUpdateStatus("Stable");
        model.addElement(contains, "BinaryRole");

        ORMEntityType mass = new ORMEntityType("Mass");
        mass.setUpdateStatus("Stable");
        model.addElement(mass, "EntityType");

        ORMBinaryRole has_per_serve = new ORMBinaryRole("has...per_serve", drink, mass);
        has_per_serve.setUpdateStatus("Stable");
        model.addElement(has_per_serve, "BinaryRole");

        ORMBinaryRole has_of_water_per_serve = new ORMBinaryRole("has...of_water_per_serve", nonAlcoholicDrink, mass);
        has_of_water_per_serve.setUpdateStatus("Stable");
        model.addElement(has_of_water_per_serve, "BinaryRole");
    }
}
