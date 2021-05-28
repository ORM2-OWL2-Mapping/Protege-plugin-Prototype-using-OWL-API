package ORMModel;

public class ORMBinaryRole extends ORMElement {

    private ORMEntityType sourceEntityType, targetEntityType;
    private String inverseRoleName;

    public ORMBinaryRole(String name, ORMEntityType sourceEntityType, ORMEntityType targetEntityType) {
        setName(name);
        this.sourceEntityType = sourceEntityType;
        this.targetEntityType = targetEntityType;
        this.inverseRoleName = "";
    }

    public ORMBinaryRole(String name, ORMEntityType sourceEntityType, ORMEntityType targetEntityType, String inverseName) {
        setName(name);
        this.sourceEntityType = sourceEntityType;
        this.targetEntityType = targetEntityType;
        this.inverseRoleName = inverseName;
    }

    public String getInverseRoleName() {
        return inverseRoleName;
    }

    public ORMEntityType getSource() {
        return sourceEntityType;
    }

    public ORMEntityType getTarget() {
        return targetEntityType;
    }
}
