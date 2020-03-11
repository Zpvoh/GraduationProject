package application.model;

public class GraphEdge {
    private String id;
    private String source;
    private String target;
    private String type;

    public GraphEdge(String id, String source, String target, String type) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.type = type;
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
}
