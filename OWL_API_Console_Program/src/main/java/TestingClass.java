import ForOntologyTesting.TestBase;
import ORMModel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.io.OWLOntologyInputSourceException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRenamer;


import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;



abstract class TestOntologyException extends Exception {
    private String errorMessage;

    TestOntologyException() {
        super();
    }

    TestOntologyException(String message) {
        super(message);
    }

    protected void setErrorMessage(String message) {
        errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

class NotFoundOntologyFileException extends TestOntologyException {
    NotFoundOntologyFileException(String filename) {
        super();
        String[] splitFilename = filename.split("/");
        String message = "Файл " + splitFilename[splitFilename.length - 1] + " не найден в текущей папке";
        setErrorMessage(message);
    }
}

class EmptyOntologyFileException extends TestOntologyException {
    EmptyOntologyFileException(String filename) {
        super();
        String[] splitFilename = filename.split("/");
        String message = "Файл " + splitFilename[splitFilename.length - 1] + " пустой";
        setErrorMessage(message);
    }
}

class InvalidOntologyFileException extends TestOntologyException {
    InvalidOntologyFileException(String filename) {
        super();
        String[] splitFilename = filename.split("/");
        String message = "Файл " + splitFilename[splitFilename.length - 1] + " содержит некорректную онтологию";
        setErrorMessage(message);
    }
}

class DifferentOntologiesException extends TestOntologyException {
    DifferentOntologiesException(String message) {
        super();
        setErrorMessage(message);
    }
}

class TestFailedException extends TestOntologyException {
    TestFailedException(String testname) {
        super();
        String message = testname + "провален";
        setErrorMessage(message);
    }
}

class NotFoundDirectoryException extends TestOntologyException {
    NotFoundDirectoryException(String path) {
        super();
        String[] splitPath = path.split("\\\\");
        setErrorMessage("Папка " + splitPath[splitPath.length - 1] + " отсутствует в папке " + splitPath[splitPath.length - 2]);
    }
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
        OWLOntology ontology_1 = null;
        try {
            ontology_1 = manager_1.loadOntologyFromOntologyDocument(file);
            if (ontology_1.isAnonymous()) {
                throw new EmptyOntologyFileException(ontology_file_1);
            }
        } catch (OWLOntologyInputSourceException e) {
            throw new NotFoundOntologyFileException(ontology_file_1);
        } catch (TestOntologyException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidOntologyFileException(ontology_file_1);
        }
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

        try {
            boolean res_1_2 = equal(ontology_1, ontology_2);
            assertTrue(res_1_2);
            boolean res_2_1 = equal(ontology_2, ontology_1);
            assertTrue(res_2_1);
        } catch (AssertionError e) {
            throw new DifferentOntologiesException(e.getMessage());
        }

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

    protected void setLogPrintStream(String testName) throws Exception {

        // Создание объекта File, представляющего файл диска.
        PrintStream o = new PrintStream(new File(TEST_DIR + testName + "/testResult_" + testName + ".txt"));

        // Сохраняем текущий System.out перед присвоением нового значения
        PrintStream console = System.out;

        // Назначаем o для выходного потока
        System.setOut(o);
        System.out.println("Выполнение " + testName);
        System.out.println("-----------------------");
    }

    protected ORMModel model;

}
