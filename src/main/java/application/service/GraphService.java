package application.service;

import application.controller.json_model.NodeCount;
import application.model.Graph;
import application.model.GraphNode;
import application.model.ReferRelationship;
import application.repository.GraphNodeRepository;
import application.repository.GraphRepository;
import application.repository.NodeRepository;
import application.repository.ReferRepository;
import application.strategies.PrecursorGraph;
import application.strategies.reasoning.ReasoningStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class GraphService {
    @Autowired
    private GraphRepository graphRepository;
    @Autowired
    private ReferRepository referRepository;
    @Autowired
    private GraphNodeRepository graphNodeRepository;
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private CourseService courseService;

    public Graph findByGraphId(String id) {
        return graphRepository.findByGraph_id(id);
    }

    public boolean deleteGraphById(String id) {
        List<GraphNode> nodes = graphRepository.getGraphNodeByGraph_id(id);
        if (nodes.size() > 0) {
            graphRepository.deleteGraphByGraph_id(id);
        } else {
            graphRepository.deleteGraphWithoutNodeByGraph_id(id);
        }
        return true;
    }

    public void save(Graph graph) {
        graphRepository.save(graph);
    }

    public void saveReferences(Set<ReferRelationship> references) {
        referRepository.saveAll(references);
    }

    public void saveGraphNodes(Set<GraphNode> nodes) {
        graphNodeRepository.saveAll(nodes);
    }

    public List<NodeCount> getNodeCount(String id) {
        List<GraphNode> nodes = graphRepository.getGraphNodeByGraph_id(id);
        List<NodeCount> counts = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            GraphNode node = nodes.get(i);
            NodeCount nodeCount = new NodeCount();
            nodeCount.setNodeId(node.getId());
            nodeCount.setNodeTopic(node.getName());
            nodeCount.setCoursewareNum(graphNodeRepository.getCountOfCoursewares(node.getLong_id()));
            int homeworkNum = graphNodeRepository.getCountOfAssignmentJudgments(node.getLong_id()) +
                    graphNodeRepository.getCountOfAssignmentMultiple(node.getLong_id()) +
                    graphNodeRepository.getCountOfAssignmentShort(node.getLong_id());
            nodeCount.setHomeworkNum(homeworkNum);
            counts.add(nodeCount);
        }

        return counts;
    }

    public List<GraphNode> getAllNodes(String id){
        return graphRepository.getGraphNodeByGraph_id(id);
    }

    public PrecursorGraph getPrecursorGraph(Graph graph, ReasoningStrategy strategy){
        return strategy.useStrategy(graph);
    }
}
