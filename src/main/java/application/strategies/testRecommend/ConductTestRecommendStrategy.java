package application.strategies.testRecommend;

import application.strategies.Edge;
import application.strategies.EvaluationList;
import application.strategies.PrecursorGraph;
import application.strategies.Vertex;
import application.strategies.evaluation.ScoreList;
import application.strategies.recommendation.ImportanceSortedList;
import application.strategies.recommendation.RecommendationStrategy;
import application.strategies.recommendation.VertexWithValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConductTestRecommendStrategy implements TestRecommendStrategy {
    private double beta = 1;

    public ConductTestRecommendStrategy(double beta) {
        this.beta = beta;
    }

    public ConductTestRecommendStrategy() {
    }

    @Override
    public List<VertexWithValue> useStrategy(PrecursorGraph precursorGraph, EvaluationList evaluationList) {
        List<VertexWithValue> result = new ArrayList<>();
        ScoreList scoreList = evaluationList.getScoreList();
        for (Vertex v : precursorGraph.getVertices()) {
            double N_node = scoreList.getScoreTotal().get(precursorGraph.getIndexById(v.getLongId())).size();
            N_node = N_node == 0 ? 0 : 1 / N_node;

            double N_total_parent = 0;
            for (Edge edge : precursorGraph.getEdges()) {
                long beginId = precursorGraph.getVertices().get(edge.getEndIndex()).getLongId();
                if (beginId == v.getLongId()) {
                    Vertex parent = precursorGraph.getVertices().get(edge.getBeginIndex());
                    double N_total_sibling = 0;
                    for (Edge edgeParent : precursorGraph.getEdges()) {
                        long parentBeginId = precursorGraph.getVertices().get(edgeParent.getBeginIndex()).getLongId();
                        if (parentBeginId == parent.getLongId()) {
                            Vertex sibling = precursorGraph.getVertices().get(edgeParent.getEndIndex());
                            double N_sibling = scoreList.getScoreTotal().get(precursorGraph.getIndexById(sibling.getLongId())).size();
                            N_total_sibling += N_sibling;
                        }
                    }
                    N_total_parent += N_total_sibling == 0 ? 0 : (1 / N_total_sibling);
                }
            }

            double dR = N_node + beta * N_total_parent;
            double relevance = evaluationList.getRelevanceByIndex(precursorGraph.getIndexById(v.getLongId()));
            if (relevance < 1 && N_node != 0) {
                result.add(new VertexWithValue(v, -dR));
            }
        }
        Collections.sort(result);
        return result;
    }
}
