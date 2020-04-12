package application.strategies;

import java.util.ArrayList;
import java.util.List;

public class EvaluationList {
    private PrecursorGraph precursorGraph;
    private List<Double> values;
    private List<Double> relevance;

    public EvaluationList(){
        this.precursorGraph = new PrecursorGraph();
        this.values = new ArrayList<>();
        this.relevance = new ArrayList<>();
    }

    public EvaluationList(PrecursorGraph precursorGraph){
        this.precursorGraph = precursorGraph;
        this.values = new ArrayList<>(precursorGraph.getVertices().size());
        this.relevance = new ArrayList<>(precursorGraph.getVertices().size());
    }

    public PrecursorGraph getPrecursorGraph() {
        return precursorGraph;
    }

    public List<Double> getValues() {
        return values;
    }

    public Vertex getVertexByIndex(int index){
        return this.precursorGraph.getVertices().get(index);
    }

    public double getRelevanceByIndex(int index){
        return this.relevance.get(index);
    }

    public void setRelevanceByIndex(int index, double value){
        relevance.set(index, value);
    }

    public double getValueByIndex(int index){
        return this.values.get(index);
    }

    public void setValueByIndex(int index, double value){
        values.set(index, value);
    }

    public int getIndexById(long id){
        return this.precursorGraph.getIndexById(id);
    }

    public double getValueById(long id){
        return getValueByIndex(getIndexById(id));
    }

    public List<Double> getRelevance() {
        return relevance;
    }

    public void setRelevance(List<Double> relevance) {
        this.relevance = relevance;
    }
}