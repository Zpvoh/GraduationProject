package application.strategies.recommendation;

import application.strategies.EvaluationList;
import application.strategies.PrecursorGraph;

public interface RecommendationStrategy {
    ImportanceSortedList useStrategy(PrecursorGraph precursorGraph, EvaluationList evaluationList);
}
