package application.strategies;

import java.util.Set;

public class Vertex {
    private long longId;
    private String id;
    private String name;
    private Set<Vertex> succesors;
    private boolean couldPredict;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCouldPredict() {
        return couldPredict;
    }

    public void setCouldPredict(boolean couldPredict) {
        this.couldPredict = couldPredict;
    }
}
