package application.strategies.recommendation;

import application.strategies.Edge;
import application.strategies.EvaluationList;
import application.strategies.PrecursorGraph;
import application.strategies.Vertex;

import java.util.*;

public class PathDecayRecommendationStrategy implements RecommendationStrategy {
    public double theta = 1;
    public double alpha = 0.5;

    public PathDecayRecommendationStrategy() {
    }

    public PathDecayRecommendationStrategy(double theta, double alpha) {
        this.theta = theta;
        this.alpha = alpha;
    }

    @Override
    public ImportanceSortedList useStrategy(PrecursorGraph precursorGraph, EvaluationList evaluationList) {
        List<Vertex> N_n = precursorGraph.getVertices();
        List<Double> importanceList = new ArrayList<>(precursorGraph.getVertices().size());

        for (int i = 0; i < N_n.size(); i++) {
            importanceList.add(0.0);
        }

        for (Vertex v : N_n) {
            int index = precursorGraph.getIndexById(v.getLongId());
            double value = evaluationList.getValueById(v.getLongId());

            Map<Vertex, Long> successors = new HashMap<>();
            for (Edge edge : precursorGraph.getEdges()) {
                long beginId = precursorGraph.getVertices().get(edge.getBeginIndex()).getLongId();
                if (beginId == v.getLongId()) {
                    Vertex vertex = precursorGraph.getVertices().get(edge.getEndIndex());
                    if(!successors.containsKey(vertex) || successors.get(vertex)>edge.getStep()) {
                        successors.put(vertex, edge.getStep());
                    }
                }
            }
            double delta_importance_sum = 0;
            double sum_P = 0;
            for (Vertex successor : successors.keySet()) {
                double n_value = evaluationList.getValueById(successor.getLongId());

                double step = successors.get(successor);
                double p = evaluationList.getRelevanceByIndex(evaluationList.getIndexById(successor.getLongId()));

                delta_importance_sum += p * (n_value - value) * Math.exp(-theta * step);
                sum_P += p;
            }

//            double delta_importance_avg = sum_P != 0 ? delta_importance_sum / sum_P : 0;

            double importance = sum_P != 0 ? value + alpha * delta_importance_sum / sum_P : value;

            importanceList.set(index, importance);
        }

        return new ImportanceSortedList(precursorGraph, importanceList, evaluationList);
    }
}
