package application.controller.json_model;

public class GraphEdge extends GraphElement{
    private String source;
    private String target;
    private String type;
    private int weight;

    public GraphEdge() {
    }

    public GraphEdge(String id, String source, String target, String type, int weight, String name) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.type = type;
        this.weight = weight;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
