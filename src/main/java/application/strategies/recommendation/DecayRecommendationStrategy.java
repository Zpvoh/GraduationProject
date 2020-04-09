package application.strategies.recommendation;

import application.strategies.Edge;
import application.strategies.EvaluationList;
import application.strategies.PrecursorGraph;
import application.strategies.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DecayRecommendationStrategy implements RecommendationStrategy {
    public double theta = 0.5;
    public double mu = 0.5;
    public double alpha = 1;

    @Override
    public ImportanceSortedList useStrategy(PrecursorGraph precursorGraph, EvaluationList evaluationList) {
//        List<VertexWithValue> verticesWithValue = ImportanceSortedList.generateVertexWithValue(precursorGraph.getVertices(), evaluationList.getValues());
        for(int i =0; i<evaluationList.getValues().size(); i++){
            evaluationList.setValueByIndex(i, evaluationList.getValueByIndex(i));
        }
        List<VertexWithValue> N_n_sorted = ImportanceSortedList.argsort(precursorGraph.getVertices(), evaluationList.getValues());
        List<Double> importanceList = new ArrayList<>(precursorGraph.getVertices().size());
        HashMap<Long, Integer> N_n_map = new HashMap<>(N_n_sorted.size());
        for (int order = 0; order < N_n_sorted.size(); order++) {
            importanceList.add(0.0);
            N_n_map.put(N_n_sorted.get(order).getVertex().getLongId(), order);
        }

        for (int order = 0; order < N_n_sorted.size(); order++) {
            int index = precursorGraph.getIndexById(N_n_sorted.get(order).getVertex().getLongId());
            double importance_n = Math.exp(-theta * order);

            List<VertexWithValue> successors = new ArrayList<>();
            for (Edge edge : precursorGraph.getEdges()) {
                long beginId = precursorGraph.getVertices().get(edge.getEndIndex()).getLongId();
                if(beginId == N_n_sorted.get(order).getVertex().getLongId()){
                    Vertex vertex = precursorGraph.getVertices().get(edge.getBeginIndex());
                    successors.add(new VertexWithValue(vertex, -edge.getP()));
                }
            }
            Collections.sort(successors);

            double importance_r = 0;
            for(int r_order = 0; r_order<successors.size(); r_order++){
                double n_order = N_n_map.get(successors.get(r_order).getVertex().getLongId());
                double s_importance_n = Math.exp(-theta * n_order);
                double s_importance_r = s_importance_n * Math.exp(-mu * r_order);
                importance_r += s_importance_r;
            }

            double importance = importance_n + alpha * importance_r;

            importanceList.set(index, importance);
        }

        ImportanceSortedList result = new ImportanceSortedList(precursorGraph, importanceList);
        return result;
    }
}
