package application.strategies.recommendation;

import application.strategies.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class LogRecommendationStrategy implements RecommendationStrategy {
    public double theta = 1;
    public double alpha = 0.5;

    public LogRecommendationStrategy() {
    }

    public LogRecommendationStrategy(double theta, double alpha) {
        this.theta = theta;
        this.alpha = alpha;
    }

    @Override
    public ImportanceSortedList useStrategy(PrecursorGraph precursorGraph, EvaluationList evaluationList) {
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
                long beginId = precursorGraph.getVertices().get(edge.getBeginIndex()).getLongId();
                if (beginId == v.getVertex().getLongId()) {
                    Vertex vertex = precursorGraph.getVertices().get(edge.getEndIndex());
                    successors.add(new VertexWithValue(vertex, evaluationList.getRelevanceByIndex(edge.getEndIndex())));
                }
            }
            Collections.sort(successors);

            double importance_r = 0;
            int r_order = -1;
            double lastP = 1;
            double delta_importance_sum = 0;
            double sum_P = 0;
            for (VertexWithValue successor : successors) {
                double n_order = N_n_map.get(successor.getVertex().getLongId());
//                double s_importance_n = Math.exp(-theta * n_order);

                double thisP = successor.getValue();
                if (lastP != thisP) {
                    r_order++;
                }
                lastP = thisP;

                delta_importance_sum += thisP * (order - n_order);
                sum_P += thisP;
            }

            double delta_importance_avg = sum_P != 0 ? delta_importance_sum / sum_P : 0;

            double P = evaluationList.getRelevance().get(precursorGraph.getIndexById(v.getVertex().getLongId()));
            double importance = importance_n * (1 + alpha * delta_importance_avg);

            importanceList.set(index, -importance);
        }

        return new ImportanceSortedList(precursorGraph, importanceList, evaluationList);
    }
}
