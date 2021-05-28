import ORMModel.*;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.vstu.nodelinkdiagram.ClientDiagramModel;
import org.vstu.nodelinkdiagram.statuses.CommitStatus;
import org.vstu.nodelinkdiagram.statuses.UpdateStatus;
import org.vstu.orm2diagram.model.ORM_EntityType;
import org.vstu.orm2diagram.model.ORM_Subtyping;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ORM_OWL_Mapper {

    private static final IRI DEFAULT_IRI = IRI.create("http://www.semanticweb.org/example");


    // Для модели Литовкина
    public static OWLOntology convertORMtoOWL(ClientDiagramModel ORMModel, String pathToOntology) throws Exception {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        if (!pathToOntology.isEmpty()) {
            File file = new File(pathToOntology);
            manager.loadOntologyFromOntologyDocument(file);
        } else {
            manager.createOntology(DEFAULT_IRI);
        }


        //Stream<ORM_EntityType> s = ORMModel.getElements(CommitStatus.NotPresented, ORM_EntityType.class);
        //s.filter(e -> e.getUpdateStatus() == UpdateStatus.Deleted);
        //s.count();

        List<ORM_EntityType> createdOrModifiedEntityTypes = ORMModel.getElements(CommitStatus.NotPresented, ORM_EntityType.class)
                .filter(e -> e.getUpdateStatus() == UpdateStatus.Created || e.getUpdateStatus() == UpdateStatus.Modified)
                //.filter(e -> e.getValidateStatus() == ValidateStatus.Acceptable)
                .collect(Collectors.toList());


        ORMtoOWLEntityTypeMapper entityTypeMapper = new ORMtoOWLEntityTypeMapper(manager);

        //l.forEach(ORMModel::markElementAsPresented);

        for (ORM_EntityType entityType : createdOrModifiedEntityTypes) {

            UpdateStatus updateStatus = entityType.getUpdateStatus();

            if (updateStatus == UpdateStatus.Created) {
                entityTypeMapper.addElement(entityType);
            } else if (updateStatus == UpdateStatus.Modified) {
                ORM_EntityType elementCommittedState = (ORM_EntityType)entityType.getCommittedState();
                entityTypeMapper.updateElement(entityType, elementCommittedState);
            }

        }

        // Добавление/обновление/удаление Subtype в OWL-онтологии
        List<ORM_Subtyping> subtypeEdges = ORMModel.getElements(CommitStatus.NotPresented, ORM_Subtyping.class)
                .collect(Collectors.toList());

        ORMtoOWLSubtypingMapper subtypeMapper = new ORMtoOWLSubtypingMapper(manager);

        for (ORM_Subtyping subtype : subtypeEdges) {
            UpdateStatus updateStatus = subtype.getUpdateStatus();
            if (updateStatus == UpdateStatus.Created) {
                subtypeMapper.addElement(subtype);
            } else if (updateStatus == UpdateStatus.Modified) {
                ORM_Subtyping elementCommittedState = (ORM_Subtyping)subtype.getCommittedState();
                subtypeMapper.updateElement(subtype, elementCommittedState);
            } else if (updateStatus == UpdateStatus.Deleted) {
                subtypeMapper.removeElement(subtype);
            }
        }


        // Удаление EntityType из OWL-онтологии, которые были удалены из ORM-диаграммы
        List<ORM_EntityType> deletedEntityTypes = ORMModel.getElements(CommitStatus.NotPresented, ORM_EntityType.class)
                .filter(e -> e.getUpdateStatus() == UpdateStatus.Deleted)
                .collect(Collectors.toList());

        for (ORM_EntityType entityType : deletedEntityTypes) {
            entityTypeMapper.removeElement(entityType);
        }

        // Получить и вернуть обновлённую онтологию
        OWLOntology ontology = manager.getOntologies().iterator().next();
        return ontology;
    }

    public static void convertOWLtoORM(ClientDiagramModel model) {

    }


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
