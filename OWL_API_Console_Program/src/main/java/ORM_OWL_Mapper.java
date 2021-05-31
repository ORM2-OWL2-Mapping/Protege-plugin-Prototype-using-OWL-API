import ORMModel.*;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyInputSourceException;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public class ORM_OWL_Mapper {

    private static final IRI DEFAULT_IRI = IRI.create("http://www.semanticweb.org/example");

    public static OWLOntology convertORMtoOWL(ORMModel model, String pathToOntology) throws Exception {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        try {
            if (!pathToOntology.isEmpty()) {
                File file = new File(pathToOntology);
                OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
                if (ontology.isAnonymous()) {
                    throw new EmptyOntologyFileException(pathToOntology);
                }
            } else {
                manager.createOntology(DEFAULT_IRI);
            }
        } catch (OWLOntologyInputSourceException e) {
            throw new NotFoundOntologyFileException(pathToOntology);
        } catch (TestOntologyException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidOntologyFileException(pathToOntology);
        }

        ORMtoOWLEntityTypeMapper entityTypeMapper = new ORMtoOWLEntityTypeMapper(manager);

        for (ORMElement element : model.getElements("EntityType")) {

            String updateStatus = element.getUpdateStatus();
            ORMEntityType entityType = (ORMEntityType) element;

            if (updateStatus.equals("Created")) {
                entityTypeMapper.addElement(entityType);
            } else if (updateStatus.equals("Modified")) {
                ORMEntityType lastElementState = (ORMEntityType)entityType.getLastState();
                entityTypeMapper.updateElement(entityType, lastElementState);
            }
        }

        ORMtoOWLSubtypingMapper subtypeMapper = new ORMtoOWLSubtypingMapper(manager);

        for (ORMElement element : model.getElements("Subtyping")) {

            String updateStatus = element.getUpdateStatus();
            ORMSubtyping subtype = (ORMSubtyping) element;

            if (updateStatus.equals("Created")) {
                subtypeMapper.addElement(subtype);
            } else if (updateStatus.equals("Modified")) {
                ORMSubtyping elementCommittedState = (ORMSubtyping)subtype.getLastState();
                subtypeMapper.updateElement(subtype, elementCommittedState);
            } else if (updateStatus.equals("Deleted")) {
                subtypeMapper.removeElement(subtype);
            }
        }

        ORMtoOWLValueTypeMapper valueTypeMapper = new ORMtoOWLValueTypeMapper(manager);

        for (ORMElement element : model.getElements("ValueType")) {

            String updateStatus = element.getUpdateStatus();
            ORMValueType valueType = (ORMValueType) element;

            if (updateStatus.equals("Created")) {
                valueTypeMapper.addElement(valueType);
            } else if (updateStatus.equals("Modified")) {
                ORMValueType elementCommittedState = (ORMValueType)valueType.getLastState();
                valueTypeMapper.updateElement(valueType, elementCommittedState);
            } else if (updateStatus.equals("Deleted")) {
                valueTypeMapper.removeElement(valueType);
            }
        }

        ORMtoOWLUnaryRoleMapper unaryRoleMapper = new ORMtoOWLUnaryRoleMapper(manager);

        for (ORMElement element : model.getElements("UnaryRole")) {

            String updateStatus = element.getUpdateStatus();
            ORMUnaryRole unaryRole = (ORMUnaryRole) element;

            if (updateStatus.equals("Created")) {
                unaryRoleMapper.addElement(unaryRole);
            } else if (updateStatus.equals("Modified")) {
                ORMUnaryRole elementCommittedState = (ORMUnaryRole)unaryRole.getLastState();
                unaryRoleMapper.updateElement(unaryRole, elementCommittedState);
            } else if (updateStatus.equals("Deleted")) {
                unaryRoleMapper.removeElement(unaryRole);
            }
        }

        ORMtoOWLBinaryRoleMapper binaryRoleMapper = new ORMtoOWLBinaryRoleMapper(manager);

        for (ORMElement element : model.getElements("BinaryRole")) {

            String updateStatus = element.getUpdateStatus();
            ORMBinaryRole binaryRole = (ORMBinaryRole) element;

            if (updateStatus.equals("Created")) {
                binaryRoleMapper.addElement(binaryRole);
            } else if (updateStatus.equals("Modified")) {
                ORMBinaryRole elementCommittedState = (ORMBinaryRole)binaryRole.getLastState();
                binaryRoleMapper.updateElement(binaryRole, elementCommittedState);
            } else if (updateStatus.equals("Deleted")) {
                binaryRoleMapper.removeElement(binaryRole);
            }
        }

        for (ORMElement element : model.getElements("EntityType")) {

            String updateStatus = element.getUpdateStatus();
            ORMEntityType entityType = (ORMEntityType) element;

            if (updateStatus.equals("Deleted")) {
                entityTypeMapper.removeElement(entityType);
            }
        }

        OWLOntology ontology = manager.getOntologies().iterator().next();
        return ontology;
    }

    public static ORMModel convertOWLtoORM(String pathToOntology) throws Exception {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        try {
            if (!pathToOntology.isEmpty()) {
                File file = new File(pathToOntology);
                OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
                if (ontology.isAnonymous()) {
                    throw new EmptyOntologyFileException(pathToOntology);
                }
            }
        } catch (OWLOntologyInputSourceException e) {
            throw new NotFoundOntologyFileException(pathToOntology);
        } catch (EmptyOntologyFileException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidOntologyFileException(pathToOntology);
        }

        ORMModel newModel = new ORMModel();

        ORMtoOWLEntityTypeMapper entityTypeMapper = new ORMtoOWLEntityTypeMapper(manager, newModel);
        for (ORMEntityType entityType : entityTypeMapper.getElementsFromOntology()) {
            entityType.setUpdateStatus("Created");
            newModel.addElement(entityType, "EntityType");
        }

        ORMtoOWLSubtypingMapper subtypingMapper = new ORMtoOWLSubtypingMapper(manager, newModel);
        for (ORMSubtyping subtyping : subtypingMapper.getElementsFromOntology()) {
            subtyping.setUpdateStatus("Created");
            newModel.addElement(subtyping, "Subtyping");
        }

        ORMtoOWLValueTypeMapper valueTypeMapper = new ORMtoOWLValueTypeMapper(manager, newModel);
        for (ORMValueType valueType : valueTypeMapper.getElementsFromOntology()) {
            valueType.setUpdateStatus("Created");
            newModel.addElement(valueType, "ValueType");
        }

        ORMtoOWLUnaryRoleMapper unaryRoleMapper = new ORMtoOWLUnaryRoleMapper(manager, newModel);
        for (ORMUnaryRole unaryRole : unaryRoleMapper.getElementsFromOntology()) {
            unaryRole.setUpdateStatus("Created");
            newModel.addElement(unaryRole, "UnaryRole");
        }

        ORMtoOWLBinaryRoleMapper binaryRoleMapper = new ORMtoOWLBinaryRoleMapper(manager, newModel);
        for (ORMBinaryRole binaryRole : binaryRoleMapper.getElementsFromOntology()) {
            binaryRole.setUpdateStatus("Created");
            newModel.addElement(binaryRole, "BinaryRole");
        }

        return newModel;
    }
}
