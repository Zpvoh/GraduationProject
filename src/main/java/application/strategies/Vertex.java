package application.strategies;

import java.util.Set;

public class Vertex {
    private long longId;
    private Set<Vertex> succesors;

    public Vertex() {
    }

    public Vertex(long longId) {
        this.longId = longId;
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
}
