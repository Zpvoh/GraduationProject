package application.controller.json_model;

import java.util.ArrayList;
import java.util.Map;

public class Graph_json {
    private Map<String, String> meta;
    private String format;
    private ArrayList<Map<String, Object>> jsonData;

    public Graph_json() {
    }

    public Graph_json(Map<String, String> meta, String format, ArrayList<Map<String, Object>> jsonData) {
        this.meta = meta;
        this.format = format;
        this.jsonData = jsonData;
    }

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

    public ArrayList<Map<String, Object>> getJsonData() {
        return jsonData;
    }

    public void setJsonData(ArrayList<Map<String, Object>> jsonData) {
        this.jsonData = jsonData;
    }
}
