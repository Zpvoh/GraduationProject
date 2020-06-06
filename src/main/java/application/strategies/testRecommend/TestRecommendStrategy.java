package application.strategies.testRecommend;

import application.strategies.EvaluationList;
import application.strategies.PrecursorGraph;
import application.strategies.recommendation.VertexWithValue;

import java.util.List;

public interface TestRecommendStrategy {
    List<VertexWithValue> useStrategy(PrecursorGraph precursorGraph, EvaluationList evaluationList);
}
