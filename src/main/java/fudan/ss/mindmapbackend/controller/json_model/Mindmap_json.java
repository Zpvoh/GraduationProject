package fudan.ss.mindmapbackend.controller.json_model;

import fudan.ss.mindmapbackend.model.Node;

public class Mindmap_json {
    private Meta meta;
    private Node data;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Node getData() {
        return data;
    }

    public void setData(Node data) {
        this.data = data;
    }
}

