package application.model;

import java.util.ArrayList;

public class MindmapData {
    private String id;
    private String topic;
    private boolean expand;
    private ArrayList<MindmapData> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public ArrayList<MindmapData> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<MindmapData> children) {
        this.children = children;
    }
}
