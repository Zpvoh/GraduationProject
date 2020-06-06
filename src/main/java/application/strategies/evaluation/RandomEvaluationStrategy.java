package application.strategies.evaluation;

import application.strategies.*;

import java.util.ArrayList;
import java.util.List;

public class RandomEvaluationStrategy implements EvaluationStrategy {
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
            double relevance = scoreTotal.size() > 0 ? (float) scoreActual.size() / scoreTotal.size() : 0;
            evaluationList.getValues().add(value);
            evaluationList.getRelevance().add(relevance);
        }

        List<Vertex> N_n = precursorGraph.getVertices();
        List<Double> importanceList = new ArrayList<>(precursorGraph.getVertices().size());

        for (int i = 0; i < N_n.size(); i++) {
            importanceList.add(Math.random());
            N_n.get(i).setCouldPredict(true);
        }

        return new ImportanceSortedList(precursorGraph, importanceList, evaluationList);
    }

    private int sum(List<Integer> scores) {
        int sum = 0;
        for (Integer score : scores) {
            sum += score;
        }
        return sum;
    }
}
