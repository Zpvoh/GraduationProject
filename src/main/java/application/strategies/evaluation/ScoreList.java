package application.strategies.evaluation;

import application.strategies.PrecursorGraph;

import java.util.ArrayList;
import java.util.List;

public class ScoreList {
    private PrecursorGraph precursorGraph;
    private List<List<Integer>> scoreActual;
    private List<List<Integer>> scoreTotal;

    public ScoreList(){
        this.precursorGraph = new PrecursorGraph();
        this.scoreActual = new ArrayList<>();
        this.scoreTotal = new ArrayList<>();
    }

    public ScoreList(PrecursorGraph precursorGraph){
        this.precursorGraph = precursorGraph;
        this.scoreActual = new ArrayList<>(precursorGraph.getVertices().size());
        this.scoreTotal = new ArrayList<>(precursorGraph.getVertices().size());
    }

    public List<List<Integer>> getScoreActual() {
        return scoreActual;
    }

    public List<List<Integer>> getScoreTotal() {
        return scoreTotal;
    }

    public void addScoreActualList(List<Integer> scores){
        this.scoreActual.add(scores);
    }

    public void addScoreTotalList(List<Integer> scores){
        this.scoreTotal.add(scores);
    }

    public PrecursorGraph getPrecursorGraph() {
        return precursorGraph;
    }
}
