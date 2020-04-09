package application.strategies.evaluation;

import application.strategies.EvaluationList;
import application.strategies.PrecursorGraph;
import application.strategies.Vertex;

import java.util.Iterator;
import java.util.List;

public class NaiveEvaluationStrategy implements EvaluationStrategy {

    @Override
    public EvaluationList useStrategy(ScoreList scores) {
        PrecursorGraph precursorGraph = scores.getPrecursorGraph();
        EvaluationList evaluationList = new EvaluationList(scores.getPrecursorGraph());
        for (int i = 0; i < precursorGraph.getVertices().size(); i++) {
            List<Integer> scoreActual = scores.getScoreActual().get(i);
            List<Integer> scoreTotal = scores.getScoreTotal().get(i);
            double sumActual = (double) sum(scoreActual);
            double sumTotal = (double) sum(scoreTotal);
            double value = sumTotal > 0 ? sumActual / sumTotal : 0;
            evaluationList.getValues().add(value);
        }
        return evaluationList;
    }

    private int sum(List<Integer> scores) {
        int sum = 0;
        for (Integer score : scores) {
            sum += score;
        }
        return sum;
    }
}
