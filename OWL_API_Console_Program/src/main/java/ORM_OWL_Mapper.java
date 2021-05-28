import ORMModel.*;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;


import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ORM_OWL_Mapper {

    private static final IRI DEFAULT_IRI = IRI.create("http://www.semanticweb.org/example");

    public static OWLOntology convertORMtoOWL(ORMModel model, String pathToOntology) throws Exception {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        if (!pathToOntology.isEmpty()) {
            File file = new File(pathToOntology);
            manager.loadOntologyFromOntologyDocument(file);
        } else {
            manager.createOntology(DEFAULT_IRI);
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


}
