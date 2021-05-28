package ORMModel;

import java.util.*;

public class ORMModel {

    //public ArrayList<ORMElement> elementList = new ArrayList<>();
    private Map<String, Set<ORMElement>> elementList = new HashMap<>();

    public ORMModel() {
        elementList.put("EntityType", new HashSet<>());
        elementList.put("ValueType", new HashSet<>());
        elementList.put("UnaryRole", new HashSet<>());
        elementList.put("BinaryRole", new HashSet<>());
        elementList.put("Subtyping", new HashSet<>());
    }

    public void addElement(ORMElement element, String nodeType) {
        elementList.get(nodeType).add(element);
    }

    public Set<ORMElement> getElements(String nodeType) {
        return elementList.get(nodeType);
    }
}

