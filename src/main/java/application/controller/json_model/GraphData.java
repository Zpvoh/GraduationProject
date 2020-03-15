package application.controller.json_model;

public class GraphData {
    private String group;
    private GraphElement data;

    public GraphData() {
    }

    public GraphData(String group, GraphElement data) {
        this.group = group;
        this.data = data;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public GraphElement getData() {
        return data;
    }

    public void setData(GraphElement data) {
        this.data = data;
    }
}
