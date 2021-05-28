package ORMModel;

public class ORMValueType extends ORMElement {

    private ORMEntityType entityType;
    private String roleName;

    public ORMValueType(String roleName, String name, ORMEntityType entityType) {
        setName(name);
        setRoleName(roleName);
        this.entityType = entityType;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setEntityType(ORMEntityType entityType) {
        this.entityType = entityType;
    }

    public ORMEntityType getEntityType() {
        return entityType;
    }
}
