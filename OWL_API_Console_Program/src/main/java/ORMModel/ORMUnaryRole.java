package ORMModel;

public class ORMUnaryRole extends ORMElement {

    private ORMEntityType entityType;

    public ORMUnaryRole(String name, ORMEntityType entityType) {
        setName(name);
        this.entityType = entityType;
    }

    public void setEntityType(ORMEntityType entityType) {
        this.entityType = entityType;
    }

    public ORMEntityType getEntityType() {
        return entityType;
    }
}
