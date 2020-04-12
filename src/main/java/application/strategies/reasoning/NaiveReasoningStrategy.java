package application.strategies.reasoning;

import application.model.Graph;
import application.model.GraphNode;
import application.strategies.Edge;
import application.strategies.PrecursorGraph;
import application.strategies.Vertex;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NaiveReasoningStrategy implements ReasoningStrategy {
    @Override
    public PrecursorGraph useStrategy(Graph graph) {
        PrecursorGraph precursorGraph = new PrecursorGraph();
        Iterator<GraphNode> nodes = graph.getGraphNodes().iterator();
        while(nodes.hasNext()){
            GraphNode node = nodes.next();
            Vertex tmp = new Vertex(node.getLong_id(), node.getId());
            precursorGraph.addVertex(tmp);
        }
        nodes = graph.getGraphNodes().iterator();
        while(nodes.hasNext()){
            GraphNode node = nodes.next();
            Iterator<GraphNode> successors = node.getSuccessors().iterator();
            while(successors.hasNext()){
                GraphNode successor = successors.next();
                precursorGraph.addEdges(node.getLong_id(), successor.getLong_id());
            }
            Iterator<GraphNode> children = node.getChildren().iterator();
            while(children.hasNext()){
                GraphNode child = children.next();
                precursorGraph.addEdges(node.getLong_id(), child.getLong_id());
            }
        }
        return precursorGraph;
    }
}
