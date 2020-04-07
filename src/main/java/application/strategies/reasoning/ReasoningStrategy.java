package application.strategies.reasoning;

import application.model.Graph;
import application.strategies.PrecursorGraph;

public interface ReasoningStrategy {
    PrecursorGraph useStrategy(Graph graph);
}
