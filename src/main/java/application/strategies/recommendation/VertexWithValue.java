package application.strategies.recommendation;

import application.strategies.Vertex;

public class VertexWithValue implements Comparable<VertexWithValue>{
    private Vertex vertex;
    private double value;

    public VertexWithValue() {
    }

    public VertexWithValue(Vertex vertex, double value) {
        this.vertex = vertex;
        this.value = value;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int compareTo(VertexWithValue o) {
        Double a = this.value;
        Double b = o.value;
        return a.compareTo(b);
    }
}
