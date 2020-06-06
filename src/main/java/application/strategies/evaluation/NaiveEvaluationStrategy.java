package application.strategies.evaluation;

import application.strategies.EvaluationList;
import application.strategies.PrecursorGraph;
import application.strategies.ImportanceSortedList;
import application.strategies.ScoreList;

import java.util.List;

public class NaiveEvaluationStrategy implements EvaluationStrategy {

    @Override
    public ImportanceSortedList useStrategy(ScoreList scores) {
        PrecursorGraph precursorGraph = scores.getPrecursorGraph();
        EvaluationList evaluationList = new EvaluationList(scores.getPrecursorGraph(), scores);
        for (int i = 0; i < precursorGraph.getVertices().size(); i++) {
            List<Integer> scoreActual = scores.getScoreActual().get(i);
            List<Integer> scoreTotal = scores.getScoreTotal().get(i);
            double sumActual = (double) sum(scoreActual);
            double sumTotal = (double) sum(scoreTotal);
            double value = sumTotal > 0 ? sumActual / sumTotal : 0;
            double relevance = scoreTotal.size() > 0 ? (float)scoreActual.size() / scoreTotal.size() : 0;
            evaluationList.getValues().add(value);
            evaluationList.getRelevance().add(relevance);
        }
//        return evaluationList;
        return null;
    }

    private int sum(List<Integer> scores) {
        int sum = 0;
        for (Integer score : scores) {
            sum += score;
        }
        return sum;
    }
}
