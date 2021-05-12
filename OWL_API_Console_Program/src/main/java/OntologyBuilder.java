import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.SimpleIRIMapper;


import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OntologyBuilder {

    public static OWLDataFactory df = OWLManager.getOWLDataFactory();

    public void createOntology1() throws Exception {
        //OWLOntologyManager m = create();
        IRI example_iri = IRI.create("http://www.semanticweb.org/ontologies/example.owl");
        //OWLOntology o = m.createOntology(example_iri);

        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        File output = File.createTempFile("example", "owl");
        IRI documentIRI = IRI.create(output);
        SimpleIRIMapper mapper = new SimpleIRIMapper(example_iri, documentIRI);
        m.addIRIMapper(mapper);
        File localFolder = new File("example_output.owl");
        m.addIRIMapper(new AutoIRIMapper(localFolder, true));
        OWLOntology o = m.createOntology(example_iri);
        m.saveOntology(o);


    }

    public void createOntology2() throws Exception {
        //Create the manager
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        //File with an existing ontology - make sure it's there!
        File file = new File("pizza.owl");
        //Load the ontology from the file
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
        //Check if the ontology contains any axioms
        System.out.println("Number of axioms: " + ontology.getAxiomCount());

        IRI example_iri = ontology.getOntologyID().getOntologyIRI().get();
        OWLIndividual individ = df.getOWLNamedIndividual(IRI.create(example_iri + "#individ_A"));

        OWLClass person = df.getOWLClass(IRI.create(example_iri + "#ВегетарианецПицца"));
        OWLClassAssertionAxiom classAssertionAx = df.getOWLClassAssertionAxiom(person, individ);

        manager.addAxiom(ontology, classAssertionAx);
        File fileformated = new File("example.owl");
        manager.saveOntology(ontology, new OWLXMLDocumentFormat(), IRI.create(fileformated.toURI()));

    }

    public void createOntology3() throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        IRI example_iri = IRI.create("http://www.semanticweb.org/ontologies/example.owl");
        OWLOntology ontology = manager.createOntology(example_iri);

        OWLClass owl_class = df.getOWLClass(IRI.create(example_iri + "#class_A"));
        //OWLClassAssertionAxiom classAssertionAx = df.getOWLClassAssertionAxiom(owl_class);
        OWLSubClassOfAxiom classAssertionAx = df.getOWLSubClassOfAxiom(df.getOWLThing(), owl_class);
        //OWLClassAxiom classAssertionAx = df.getOWLClassAxiom
        manager.addAxiom(ontology, classAssertionAx);

//        OWLIndividual individ = df.getOWLNamedIndividual(IRI.create(example_iri + "#individ_A"));
//        OWLClass person = df.getOWLClass(IRI.create(example_iri + owl_class.getIRI().getShortForm()));
//        OWLClassAssertionAxiom classAssertionAx2 = df.getOWLClassAssertionAxiom(person, individ);
//        manager.addAxiom(ontology, classAssertionAx2);

        OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
        for (OWLClass ind : ontology.getClassesInSignature()) {
            System.out.println(ind.getIRI().getShortForm());
            if (ind.getIRI().getShortForm().equals("class_A")) {
                ind.accept(remover);
                break;
            }
        }
        manager.applyChanges(remover.getChanges());
        remover.reset();


        File fileformated = new File("example_2.owl");
        manager.saveOntology(ontology, new OWLXMLDocumentFormat(), IRI.create(fileformated.toURI()));
        //manager.saveOntology(ontology, new TurtleDocumentFormat(), IRI.create(fileformated.toURI()));
    }

    public void createOntology4() throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File file = new File("example_2.owl");
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
        IRI example_iri = ontology.getOntologyID().getOntologyIRI().get();
        OWLClass owl_class = df.getOWLClass(IRI.create(example_iri + "#class_C"));
        OWLDeclarationAxiom classDeclarationAx = df.getOWLDeclarationAxiom(owl_class);
        manager.addAxiom(ontology, classDeclarationAx);
        OWLSubClassOfAxiom classAssertionAx = df.getOWLSubClassOfAxiom(df.getOWLThing(), owl_class);
        //manager.addAxiom(ontology, classAssertionAx);
        String str = "";
        for (OWLAxiom item : ontology.getAxioms()) {
            str = str.concat(item.toString());
        }
        System.out.println(str);
        //File fileformated = new File("example_2.owl");
        //manager.saveOntology(ontology, new OWLXMLDocumentFormat(), IRI.create(fileformated.toURI()));
    }

    public void createOntology() throws Exception {
        OwlAPI ontology_manager = new OwlAPI("http://www.semanticweb.org/example/");

        ontology_manager.declareEntityType("Person");
        ontology_manager.declareEntityType("Male");
        ontology_manager.declareEntityType("Female");

        ontology_manager.declareUnaryRole("is_sportsman", "Person");

        ontology_manager.declareValueType("has_gender", "", "Person");

        ontology_manager.declareBinaryRole("uses", "used_by", "Male", "Female");

        //ontology_manager.declareSubtype("Male", "Person");
        //ontology_manager.declareSubtype("Female", "Male");
        //ontology_manager.removeSubtype("Male", "Person");
        //ontology_manager.test();
        //ontology_manager.removeEntityType("Male");
        //ontology_manager.removeEntityType("Person");
        //ontology_manager.removeEntityType("Female");

        //ontology_manager.removeValueType("has_gender.Person");

        //ontology_manager.removeUnaryRole("is_sportsman.Person");

        //ontology_manager.removeBinaryRole("uses.Male.Female");

        //ontology_manager.updateValueType("has_gender.Person", "has_gender_new", "","Male");

        //ontology_manager.updateUnaryRole("is_sportsman.Person", "is_sportsman_new", "Person1");

        //ontology_manager.updateBinaryRole("uses.Male.Female", "uses_new", "used_by_new", "Female", "Male");

        ontology_manager.saveOntologyInFile("example.owl");
    }

    public void loadOntology() throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        //File with an existing ontology - make sure it's there!
        File file = new File("example.owl");
        //Load the ontology from the file
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
        OwlAPI ontology_manager = new OwlAPI(manager);
        //ontology_manager.updateValueType("has_gender_new.Male", "123", "","Female");

        HashMap<String, String> orm_elem_dict = new HashMap<String, String>();
        orm_elem_dict.put("A", "EntityType");
        orm_elem_dict.put("B", "EntityType");
        orm_elem_dict.put("C", "EntityType");
        HashMap<String, String> update_orm_elem_dict = ontology_manager.updateORM();


        HashMap<String, String> copy_orm_elem_dict = new HashMap<String, String>(orm_elem_dict);
        for (Map.Entry<String, String> orm_elem : copy_orm_elem_dict.entrySet()) {
            if (!update_orm_elem_dict.containsKey(orm_elem.getKey())) {
                orm_elem_dict.remove(orm_elem.getKey());
            }
        }
        for (Map.Entry<String, String> orm_elem : update_orm_elem_dict.entrySet()) {
            orm_elem_dict.put(orm_elem.getKey(), orm_elem.getValue());
        }

        System.out.println("=======================");
        System.out.println("Список элементов ORM:");
        for (Map.Entry<String, String> orm_elem : orm_elem_dict.entrySet()) {
            System.out.println("Элемент: " + orm_elem.getKey() + ", тип: " + orm_elem.getValue());
        }
        System.out.println("=======================");

        //ontology_manager.saveOntologyInFile("example.owl");
    }
}
