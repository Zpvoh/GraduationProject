package application.strategies.testRecommend;

import application.strategies.Edge;
import application.strategies.EvaluationList;
import application.strategies.PrecursorGraph;
import application.strategies.Vertex;
import application.strategies.ScoreList;
import application.strategies.recommendation.VertexWithValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InfluenceTestRecommendStrategy implements TestRecommendStrategy {
    private double alpha = 0.5;
    private double theta = 1;

    public InfluenceTestRecommendStrategy(double alpha, double theta) {
        this.alpha = alpha;
        this.theta = theta;
    }

    public InfluenceTestRecommendStrategy() {
    }

    @Override
    public List<VertexWithValue> useStrategy(PrecursorGraph precursorGraph, EvaluationList evaluationList) {
        List<VertexWithValue> result = new ArrayList<>();
        ScoreList scoreList = evaluationList.getScoreList();
        for (Vertex v : precursorGraph.getVertices()) {
            int index = precursorGraph.getIndexById(v.getLongId());
            double N_node = scoreList.getAssignmentNumber().get(index);
            N_node = N_node == 0 ? 0 : 1 / N_node;

            double N_total_parent = 0;
            for (Edge edge : precursorGraph.getEdges()) {
                long beginId = precursorGraph.getVertices().get(edge.getEndIndex()).getLongId();
                if (beginId == v.getLongId()) {
                    Vertex parent = precursorGraph.getVertices().get(edge.getBeginIndex());
                    double influenceParent = Math.exp(-theta * edge.getStep());
                    double N_total_sibling = 0;
                    for (Edge edgeParent : precursorGraph.getEdges()) {
                        long parentBeginId = precursorGraph.getVertices().get(edgeParent.getBeginIndex()).getLongId();
                        if (parentBeginId == parent.getLongId()) {
                            Vertex sibling = precursorGraph.getVertices().get(edgeParent.getEndIndex());
                            double influenceSibling = Math.exp(-theta * edgeParent.getStep());
//                            double N_sibling = scoreList.getScoreTotal().get(precursorGraph.getIndexById(sibling.getLongId())).size();
                            N_total_sibling += influenceSibling;
                        }
                    }
                    N_total_parent += N_total_sibling == 0 ? 0 : (influenceParent * influenceParent / N_total_sibling);
                }
            }

            double dR = N_node + alpha * N_total_parent;
            double relevance = evaluationList.getRelevanceByIndex(precursorGraph.getIndexById(v.getLongId()));
            if (relevance < 1 && N_node != 0) {
                result.add(new VertexWithValue(v, -dR));
            }
        }
        Collections.sort(result);
        return result;
    }
}
