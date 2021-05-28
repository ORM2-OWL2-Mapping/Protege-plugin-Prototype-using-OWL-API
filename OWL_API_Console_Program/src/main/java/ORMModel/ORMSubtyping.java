package ORMModel;

public class ORMSubtyping extends ORMElement {

    private ORMEntityType sourceEntityType, targetEntityType;

    public ORMSubtyping(ORMEntityType sourceEntityType, ORMEntityType targetEntityType) {
        this.sourceEntityType = sourceEntityType;
        this.targetEntityType = targetEntityType;
    }

    public ORMEntityType getSource() {
        return sourceEntityType;
    }

    public ORMEntityType getTarget() {
        return targetEntityType;
    }
}
