package application.controller.json_model;

import java.util.Map;

public class MindmapJson {
    private Map<String, String> meta;
    private String format;
    private MindmapData data;

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public MindmapData getData() {
        return data;
    }

    public void setData(MindmapData data) {
        this.data = data;
    }
}


