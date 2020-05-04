package application.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PrecursorGraph {
    private HashMap<Long, Integer> indexIdMap;
    private List<Vertex> vertices;
    private List<Edge> edges;

    public PrecursorGraph(){
        this.indexIdMap = new HashMap<>();
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public void addVertex(Vertex vertex){
        vertices.add(vertex);
        indexIdMap.put(vertex.getLongId(), indexIdMap.size());
    }

//    public void setVertices(List<Vertex> vertices) {
//        this.vertices = vertices;
//    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void addEdges(Long beginId, Long endId){
        Edge edge = new Edge(indexIdMap.get(beginId), indexIdMap.get(endId));
        edges.add(edge);
    }

    public void addEdges(Long beginId, Long endId, Long step){
        Edge edge = new Edge(indexIdMap.get(beginId), indexIdMap.get(endId));
        edge.setStep(step);
        edges.add(edge);
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public int getIndexById(long id){
        return indexIdMap.get(id);
    }
}
