package application.strategies.recommendation;

import application.strategies.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DecayRecommendationStrategy implements RecommendationStrategy {
    public double theta = 0.5;
    public double mu = 0.5;
    public double alpha = 1;

    public DecayRecommendationStrategy() {
    }

    public DecayRecommendationStrategy(double theta, double mu, double alpha) {
        this.theta = theta;
        this.mu = mu;
        this.alpha = alpha;
    }

    @Override
    public ImportanceSortedList useStrategy(PrecursorGraph precursorGraph, EvaluationList evaluationList) {
//        List<VertexWithValue> verticesWithValue = ImportanceSortedList.generateVertexWithValue(precursorGraph.getVertices(), evaluationList.getValues());
        for (int i = 0; i < evaluationList.getValues().size(); i++) {
            evaluationList.setValueByIndex(i, evaluationList.getValueByIndex(i));
        }
        List<VertexWithValue> N_n_sorted = ImportanceSortedList.argsort(precursorGraph.getVertices(), evaluationList.getValues());
        List<Double> importanceList = new ArrayList<>(precursorGraph.getVertices().size());
        HashMap<Long, Integer> N_n_map = new HashMap<>(N_n_sorted.size());

        int map_order = -1;
        double lastValue = -1;
        for (VertexWithValue v : N_n_sorted) {
            importanceList.add(0.0);

            double thisValue = v.getValue();
            if (lastValue != thisValue) {
                map_order++;
            }
            lastValue = thisValue;

            N_n_map.put(v.getVertex().getLongId(), map_order);
        }

        for (VertexWithValue v : N_n_sorted) {
            int index = precursorGraph.getIndexById(v.getVertex().getLongId());
            int order = N_n_map.get(v.getVertex().getLongId());
            double importance_n = Math.exp(-theta * order);

            List<VertexWithValue> successors = new ArrayList<>();
            for (Edge edge : precursorGraph.getEdges()) {
                long beginId = precursorGraph.getVertices().get(edge.getEndIndex()).getLongId();
                if (beginId == v.getVertex().getLongId()) {
                    Vertex vertex = precursorGraph.getVertices().get(edge.getBeginIndex());
//                    successors.add(new VertexWithValue(vertex, -edge.getP()));
                    successors.add(new VertexWithValue(vertex, evaluationList.getRelevanceByIndex(edge.getBeginIndex())));
                }
            }
            Collections.sort(successors);

            double importance_r = 0;
            int r_order = -1;
            double lastP = 1;
            for (VertexWithValue successor : successors) {
                double n_order = N_n_map.get(successor.getVertex().getLongId());
                double s_importance_n = Math.exp(-theta * n_order);

                double thisP = successor.getValue();
                if (lastP != thisP) {
                    r_order++;
                }
                lastP = thisP;

                double s_importance_r = s_importance_n * Math.exp(-mu * r_order) * thisP;
                importance_r += s_importance_r;
            }

            double importance = importance_n + alpha * importance_r;

            importanceList.set(index, -importance);
        }

        return new ImportanceSortedList(precursorGraph, importanceList, evaluationList);
    }
}
