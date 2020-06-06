package application.strategies.evaluation;

import application.strategies.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecursivePathDecayEvaluationStrategy implements EvaluationStrategy {
    public double theta = 1;
    public double alpha = 0.5;

    public RecursivePathDecayEvaluationStrategy() {
    }

    public RecursivePathDecayEvaluationStrategy(double alpha, double theta) {
        this.theta = theta;
        this.alpha = alpha;
    }

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
        List<Double> lastList = new ArrayList<>(precursorGraph.getVertices().size());

        for (int i = 0; i < N_n.size(); i++) {
            lastList.add(evaluationList.getValueByIndex(i));
        }

        double diff = 100;

        while (diff > 1) {
            if(diff>100){
                break;
            }
            importanceList = new ArrayList<>(precursorGraph.getVertices().size());
            for (int i = 0; i < N_n.size(); i++) {
                importanceList.add(0.0);
            }
            for (Vertex v : N_n) {
                int index = precursorGraph.getIndexById(v.getLongId());
                double value = lastList.get(index);
                double relevance_v = evaluationList.getRelevanceByIndex(index);
                if (relevance_v > 0) {
                    v.setCouldPredict(true);
                }

                Map<Vertex, Long> successors = new HashMap<>();
                for (Edge edge : precursorGraph.getEdges()) {
                    long beginId = precursorGraph.getVertices().get(edge.getBeginIndex()).getLongId();
                    if (beginId == v.getLongId()) {
                        Vertex vertex = precursorGraph.getVertices().get(edge.getEndIndex());
                        if (!successors.containsKey(vertex) || successors.get(vertex) > edge.getStep()) {
                            successors.put(vertex, edge.getStep());
                        }
                    }
                }
                double delta_importance_sum = 0;
                double sum_P = 0;
                for (Vertex successor : successors.keySet()) {
                    int n_index = precursorGraph.getIndexById(successor.getLongId());
                    double n_value = lastList.get(n_index);

                    double step = successors.get(successor);
                    double p = evaluationList.getRelevanceByIndex(evaluationList.getIndexById(successor.getLongId()));

                    if (!v.isCouldPredict() && p > 0) {
                        v.setCouldPredict(true);
                    }

                    delta_importance_sum += p * (n_value - value) * Math.exp(-theta * step);
                    sum_P += p * Math.exp(-theta * step);
                }

                double successor_rect = sum_P != 0 ? delta_importance_sum / sum_P : 0;

                Map<Vertex, Long> precursors = new HashMap<>();
                for (Edge edge : precursorGraph.getEdges()) {
                    long endId = precursorGraph.getVertices().get(edge.getEndIndex()).getLongId();
                    if (endId == v.getLongId()) {
                        Vertex vertex = precursorGraph.getVertices().get(edge.getBeginIndex());
                        if (!precursors.containsKey(vertex) || precursors.get(vertex) > edge.getStep()) {
                            precursors.put(vertex, edge.getStep());
                        }
                    }
                }
                double sum_precursor = 0;
                sum_P = 0;
                for (Vertex precursor : precursors.keySet()) {
                    int n_index = precursorGraph.getIndexById(precursor.getLongId());
                    double n_value = lastList.get(n_index);

                    double p = evaluationList.getRelevanceByIndex(evaluationList.getIndexById(precursor.getLongId()));

                    double step = precursors.get(precursor);

                    sum_precursor += p * n_value * Math.exp(-theta * step);
                    sum_P += p;
                }

                double avg_precursor = sum_P == 0 ? 0 : sum_precursor / sum_P;

//            double importance = relevance_v * (value + alpha * successor_rect) + (1 - relevance_v) * avg_precursor;
//            double importance = (1-alpha)*value + alpha * successor_rect;
                double importance = value + alpha * successor_rect;
//            double importance = relevance_v * value + (1 - relevance_v) * successor_rect;
                importanceList.set(index, importance);
            }

            diff = calcDiff(importanceList, lastList);
            System.out.println(diff);
            lastList = importanceList;
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

    private double calcDiff(List<Double> importanceList, List<Double> lastList) {
        double diff = 0;
        for (int i = 0; i < importanceList.size(); i++) {
            double diff_sqrt = importanceList.get(i) - lastList.get(i);
            diff += (diff_sqrt * diff_sqrt);
        }
        return diff;
    }
}
