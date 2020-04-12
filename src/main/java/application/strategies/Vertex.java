package application.strategies;

import java.util.Set;

public class Vertex {
    private long longId;
    private String id;
    private Set<Vertex> succesors;

    public Vertex() {
    }

    public Vertex(long longId, String id) {
        this.longId = longId;
        this.id = id;
    }

    public long getLongId() {
        return longId;
    }

    public void setLongId(long longId) {
        this.longId = longId;
    }

    public Set<Vertex> getSuccesors() {
        return succesors;
    }

    public void setSuccesors(Set<Vertex> succesors) {
        this.succesors = succesors;
    }

    public void addSuccesor(Vertex vertex){
        succesors.add(vertex);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
