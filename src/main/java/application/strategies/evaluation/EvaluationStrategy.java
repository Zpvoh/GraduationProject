package application.strategies.evaluation;

import application.strategies.ImportanceSortedList;
import application.strategies.ScoreList;

public interface EvaluationStrategy {
    ImportanceSortedList useStrategy(ScoreList scores);
}
