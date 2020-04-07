package application.strategies.evaluation;

import application.strategies.EvaluationList;
import application.strategies.Vertex;

import java.util.List;

public interface EvaluationStrategy {
    EvaluationList useStrategy(ScoreList scores);
}
