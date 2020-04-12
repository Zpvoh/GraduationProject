package application.strategies.recommendation;

import application.strategies.EvaluationList;
import application.strategies.PrecursorGraph;
import application.strategies.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ImportanceSortedList {
    private PrecursorGraph precursorGraph;
    private List<Double> importanceList;
    private List<Integer> sortedIndices;
    private List<Long> sortedId;
    private List<VertexWithValue> sortedVertices;
    private EvaluationList evaluationList;

    public ImportanceSortedList() {
    }

    public ImportanceSortedList(PrecursorGraph precursorGraph, List<Double> importanceList, EvaluationList evaluationList) {
        this.precursorGraph = precursorGraph;
        this.importanceList = importanceList;
        this.evaluationList = evaluationList;
        this.sortedIndices = new ArrayList<>();
        this.sortedId = new ArrayList<>();
        List<VertexWithValue> vertexWithValues = ImportanceSortedList.argsort(precursorGraph.getVertices(), importanceList);
        sortedValueToRank(vertexWithValues);
        this.sortedVertices = vertexWithValues;
        for (VertexWithValue vertexWithValue : vertexWithValues) {
            long id = vertexWithValue.getVertex().getLongId();
            sortedId.add(id);
            sortedIndices.add(precursorGraph.getIndexById(id));
        }
    }

    public List<Double> getImportanceList() {
        return importanceList;
    }

    public void setImportanceList(List<Double> importanceList) {
        this.importanceList = importanceList;
    }

    public List<Integer> getSortedIndices() {
        return sortedIndices;
    }

    public void setSortedIndices(List<Integer> sortedIndices) {
        this.sortedIndices = sortedIndices;
    }

    public List<Long> getSortedId() {
        return sortedId;
    }

    public void setSortedId(List<Long> sortedId) {
        this.sortedId = sortedId;
    }

    public List<VertexWithValue> getSortedVertices() {
        return sortedVertices;
    }

    public void setSortedVertices(List<VertexWithValue> sortedVertices) {
        this.sortedVertices = sortedVertices;
    }

    public static List<VertexWithValue> argsort(List<Vertex> list, List<Double> values) {
        List<VertexWithValue> vertexWithValues = generateVertexWithValue(list, values);
        Collections.sort(vertexWithValues);
        return vertexWithValues;
    }

    public static List<VertexWithValue> generateVertexWithValue(List<Vertex> list, List<Double> values) {
        List<VertexWithValue> vertexWithValues = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            VertexWithValue vertexWithValue = new VertexWithValue(list.get(i),
                    values.get(i));
            vertexWithValues.add(i, vertexWithValue);
        }
        return vertexWithValues;
    }

    private void sortedValueToRank(List<VertexWithValue> vertices){
        int rank = 0;
        int order = 0;
        double lastValue = vertices.get(0).getValue();
        for(VertexWithValue v : vertices){
            if(lastValue!=v.getValue()){
                rank = order;
//                order++;
            }

            lastValue = v.getValue();
            v.setValue(rank);
            order++;
        }
        order--;

        if(order <= 0){
            return;
        }

        for(VertexWithValue v : vertices){
            v.setValue(v.getValue() / order);
        }
    }
}
