package application.strategies;

import java.util.ArrayList;
import java.util.List;

public class EvaluationList {
    private PrecursorGraph precursorGraph;
    private List<Double> values;

    public EvaluationList(){
        this.precursorGraph = new PrecursorGraph();
        this.values = new ArrayList<>();
    }

    public EvaluationList(PrecursorGraph precursorGraph){
        this.precursorGraph = precursorGraph;
        this.values = new ArrayList<>(precursorGraph.getVertices().size());
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
}
