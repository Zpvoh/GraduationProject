package application.controller.json_model;

import application.model.Graph;
import application.model.GraphNode;

public class GraphNode_json extends GraphElement{
    private int weight;
    private int width;
    private int labelSize;
    private String parentID;

    public GraphNode_json() {
    }

    public GraphNode_json(String id, String name, int weight, int width, int labelSize, String parentID) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.width = width;
        this.labelSize = labelSize;
        this.parentID = parentID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLabelSize() {
        return labelSize;
    }

    public void setLabelSize(int labelSize) {
        this.labelSize = labelSize;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }
}
