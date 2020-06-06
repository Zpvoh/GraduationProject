package application.strategies.recommendation;

import application.strategies.*;

import java.util.ArrayList;
import java.util.List;

public class DirectRecommendationStrategy implements RecommendationStrategy {
    public double theta = 1;
    public double alpha = 0.5;

    public DirectRecommendationStrategy() {
    }

    public DirectRecommendationStrategy(double theta, double alpha) {
        this.theta = theta;
        this.alpha = alpha;
    }

    @Override
    public ImportanceSortedList useStrategy(PrecursorGraph precursorGraph, EvaluationList evaluationList) {
        List<Vertex> N_n = precursorGraph.getVertices();
        List<Double> importanceList = new ArrayList<>(precursorGraph.getVertices().size());

        for (int i = 0; i<N_n.size(); i++) {
            importanceList.add(0.0);
        }

        for (Vertex v : N_n) {
            int index = precursorGraph.getIndexById(v.getLongId());
            double value = evaluationList.getValueById(v.getLongId());
            double importance_n = Math.exp(-theta * value);

            List<VertexWithValue> successors = new ArrayList<>();
            for (Edge edge : precursorGraph.getEdges()) {
                long beginId = precursorGraph.getVertices().get(edge.getBeginIndex()).getLongId();
                if (beginId == v.getLongId()) {
                    Vertex vertex = precursorGraph.getVertices().get(edge.getEndIndex());
                    successors.add(new VertexWithValue(vertex, evaluationList.getRelevanceByIndex(edge.getEndIndex())));
                }
            }
//            Collections.sort(successors);
            double delta_importance_sum = 0;
            double sum_P = 0;
            for (VertexWithValue successor : successors) {
                double n_value = evaluationList.getValueById(successor.getVertex().getLongId());

                double thisP = successor.getValue();

                delta_importance_sum += thisP * (value - n_value);
                sum_P += thisP;
            }

            double delta_importance_avg = sum_P != 0 ? delta_importance_sum / sum_P : 0;

            double P = evaluationList.getRelevance().get(precursorGraph.getIndexById(v.getLongId()));
            double importance = importance_n * (1 + alpha * delta_importance_avg);

            importanceList.set(index, -importance);
        }

        return new ImportanceSortedList(precursorGraph, importanceList, evaluationList);
    }
}
