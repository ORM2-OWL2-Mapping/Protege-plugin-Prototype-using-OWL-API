package ORMModel;

public abstract class ORMElement {
    protected String updateStatus;

    public void setUpdateStatus(String updateStatus) {
        this.updateStatus = updateStatus;
    }

    public String getUpdateStatus() {
        return updateStatus;
    }

    protected String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected ORMElement lastState;

    public void setLastState(ORMElement element) {
        lastState = element;
    }

    public ORMElement getLastState() {
        return lastState;
    }
}
