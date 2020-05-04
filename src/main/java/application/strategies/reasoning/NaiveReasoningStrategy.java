package application.strategies.reasoning;

import application.model.Graph;
import application.model.GraphNode;
import application.service.GraphNodeService;
import application.strategies.Edge;
import application.strategies.PrecursorGraph;
import application.strategies.Vertex;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class NaiveReasoningStrategy implements ReasoningStrategy {
    @Autowired
    private GraphNodeService graphNodeService;

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
//            Iterator<GraphNode> successors = new HashSet<>(Arrays.asList(graphNodeService.findSuccessor(node.getLong_id()))).iterator();
            while(successors.hasNext()){
                GraphNode successor = successors.next();
                precursorGraph.addEdges(node.getLong_id(), successor.getLong_id());
            }
            Iterator<GraphNode> children = node.getChildren().iterator();
//            Iterator<GraphNode> children = new HashSet<>(Arrays.asList(graphNodeService.findChildren(node.getLong_id()))).iterator();
            while(children.hasNext()){
                GraphNode child = children.next();
                precursorGraph.addEdges(node.getLong_id(), child.getLong_id());
            }
        }
        return precursorGraph;
    }
}
