package application.service;

import application.controller.json_model.NodeCount;
import application.model.Graph;
import application.model.GraphNode;
import application.model.ReferRelationship;
import application.repository.GraphNodeRepository;
import application.repository.GraphRepository;
import application.repository.NodeRepository;
import application.repository.ReferRepository;
import application.strategies.Edge;
import application.strategies.PrecursorGraph;
import application.strategies.Vertex;
import application.strategies.evaluation.PathDecayEvaluationStrategy;
import application.strategies.ScoreList;
import application.strategies.evaluation.RecursivePathDecayEvaluationStrategy;
import application.strategies.reasoning.ReasoningStrategy;
import application.strategies.ImportanceSortedList;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public List<GraphNode> getAllNodes(String id) {
        return graphRepository.getGraphNodeByGraph_id(id);
    }

    public PrecursorGraph getPrecursorGraph(Graph graph, ReasoningStrategy strategy) {
        return strategy.useStrategy(graph);
    }

    public PrecursorGraph getPrecursorGraphUseNaive(Graph graph) {
        PrecursorGraph precursorGraph = new PrecursorGraph();
        Iterator<GraphNode> nodes = graph.getGraphNodes().iterator();
        while (nodes.hasNext()) {
            GraphNode node = nodes.next();
            Vertex tmp = new Vertex(node.getLong_id(), node.getId());
            tmp.setName(node.getName());
            precursorGraph.addVertex(tmp);
        }
        nodes = graph.getGraphNodes().iterator();
        while (nodes.hasNext()) {
            GraphNode node = nodes.next();
            Iterator<GraphNode> successors = new HashSet<>(Arrays.asList(graphNodeRepository.findSuccessor(node.getLong_id()))).iterator();
            while (successors.hasNext()) {
                GraphNode successor = successors.next();
                precursorGraph.addEdges(node.getLong_id(), successor.getLong_id());
            }
            Iterator<GraphNode> children = new HashSet<>(Arrays.asList(graphNodeRepository.findChildren(node.getLong_id()))).iterator();
            while (children.hasNext()) {
                GraphNode child = children.next();
                precursorGraph.addEdges(node.getLong_id(), child.getLong_id());
            }
        }
        return precursorGraph;
    }

    public PrecursorGraph getPrecursorGraphUseReasoning(Graph graph) {
        PrecursorGraph precursorGraph = new PrecursorGraph();
        Iterator<GraphNode> nodes = graph.getGraphNodes().iterator();
        graphRepository.reasonSynonymWithAntonym(graph.getGraph_id());
        while (nodes.hasNext()) {
            GraphNode node = nodes.next();
            Vertex tmp = new Vertex(node.getLong_id(), node.getId());
            tmp.setName(node.getName());
            precursorGraph.addVertex(tmp);
        }
        nodes = graph.getGraphNodes().iterator();
        while (nodes.hasNext()) {
            GraphNode node = nodes.next();
            LinkedTreeMap[] graphNodeWithSteps = graphNodeRepository.findSuccessorReasoning(node.getLong_id());
            for (LinkedTreeMap successor : new HashSet<>(Arrays.asList(graphNodeWithSteps))) {
                precursorGraph.addEdges(node.getLong_id(), ((GraphNode) (successor.get("graphNode"))).getLong_id(), (long) successor.get("step"));
            }
        }
        return precursorGraph;
    }

    public ImportanceSortedList evaluateGraphNodePathDecay(ScoreList scoreList, double alpha, double theta) {
        return new PathDecayEvaluationStrategy(alpha, theta).useStrategy(scoreList);
    }

    public ImportanceSortedList evaluateGraphNodeRecursivePathDecay(ScoreList scoreList, double alpha, double theta) {
        return new RecursivePathDecayEvaluationStrategy(alpha, theta).useStrategy(scoreList);
    }

    public ImportanceSortedList evaluateRecommendationByOutdegree(String graph_id, PrecursorGraph precursorGraph, ImportanceSortedList learningSituation, double alpha, double theta, double gamma) {
        List<Vertex> N_n = precursorGraph.getVertices();
        List<Double> recommendImportanceList = new ArrayList<>(precursorGraph.getVertices().size());
        List<Double> structureImportanceList = new ArrayList<>(precursorGraph.getVertices().size());
        List<Double> learningImportanceList = new ArrayList<>(precursorGraph.getVertices().size());

        for (int i = 0; i < N_n.size(); i++) {
            recommendImportanceList.add(0.0);
            structureImportanceList.add(0.0);
            learningImportanceList.add(0.0);
        }

        Map<Long, Long> outDegreeMap = getOutDegreeMap(graphRepository.getAllOutDegreeByGraph_id(graph_id));

        double minStructureImportance = -1, maxStructureImportance = -1, minLearningImportance = -1, maxLearningImportance = -1;
        boolean startFlag = true;

        for (Vertex v : N_n) {
            int index = precursorGraph.getIndexById(v.getLongId());
            double value = 0;
            if (outDegreeMap.containsKey(v.getLongId())) {
                value = outDegreeMap.get(v.getLongId());
            } else {
                value = 0;
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
            double importance_sum = 0;
            for (Vertex successor : successors.keySet()) {
                double n_value = 0;
                if (outDegreeMap.containsKey(successor.getLongId())) {
                    n_value = outDegreeMap.get(successor.getLongId());
                } else {
                    n_value = 0;
                }

                double step = successors.get(successor);

                importance_sum += n_value * Math.exp(-theta * step);
            }

            double structureImportance = value + alpha * importance_sum;
            double learningImportance = learningSituation.getImportanceList().get(precursorGraph.getIndexById(v.getLongId()));

            if (startFlag) {
                maxStructureImportance = minStructureImportance = structureImportance;
                maxLearningImportance = minLearningImportance = learningImportance;
                startFlag = false;
            } else {
                maxStructureImportance = maxStructureImportance < structureImportance ? structureImportance : maxStructureImportance;
                minStructureImportance = minStructureImportance > structureImportance ? structureImportance : minStructureImportance;
                maxLearningImportance = maxLearningImportance < learningImportance ? learningImportance : maxLearningImportance;
                minLearningImportance = minLearningImportance > learningImportance ? learningImportance : minLearningImportance;
            }

            structureImportanceList.add(index, structureImportance);
            learningImportanceList.add(index, learningImportance);
        }

        for (Vertex v : N_n) {
            int index = precursorGraph.getIndexById(v.getLongId());
            double structureImportance = structureImportanceList.get(index);
            double learningImportance = learningImportanceList.get(index);
            double recommendImportance = gamma * normalize(structureImportance, maxStructureImportance, minStructureImportance)
                    - normalize(learningImportance, maxLearningImportance, minLearningImportance);
            recommendImportanceList.set(index, -recommendImportance);
        }

        return new ImportanceSortedList(precursorGraph, recommendImportanceList, learningSituation.getEvaluationList());
    }

    public ImportanceSortedList evaluateRecommendationByTFIDF(String graph_id, PrecursorGraph precursorGraph, ImportanceSortedList learningSituation){
        List<Vertex> N_n = precursorGraph.getVertices();
        List<Double> learningSituationList = new ArrayList<>();
        List<Double> normalizedLearningSituationList = new ArrayList<>();

        double minLearningImportance = -1, maxLearningImportance = -1;
        boolean startFlag = true;
        for(Vertex v:N_n){
            double learningImportance = learningSituation.getImportanceList().get(precursorGraph.getIndexById(v.getLongId()));
            if (startFlag) {
                maxLearningImportance = minLearningImportance = learningImportance;
                startFlag = false;
            } else {
                maxLearningImportance = maxLearningImportance < learningImportance ? learningImportance : maxLearningImportance;
                minLearningImportance = minLearningImportance > learningImportance ? learningImportance : minLearningImportance;
            }
            learningSituationList.add(learningImportance);
        }

        for(int i=0; i<N_n.size(); i++){
            double normalized_value = normalize(learningSituationList.get(i), maxLearningImportance, minLearningImportance);
            normalizedLearningSituationList.add(normalized_value);
        }
        return null;
    }

    private int calculateOutdegree(PrecursorGraph precursorGraph, long id) {
        int outdegree = 0;
        for (Edge edge : precursorGraph.getEdges()) {
            if (edge.getBeginIndex() == precursorGraph.getIndexById(id)) {
                outdegree++;
            }
        }
        return outdegree;
    }

    private Map<Long, Long> getOutDegreeMap(LinkedTreeMap[] mapList) {
        Map<Long, Long> result = new HashMap<>();

        for (int i = 0; i < mapList.length; i++) {
            Long id = (Long) mapList[i].get("node_id");
            Long outDegree = (Long) mapList[i].get("outDegree");
            result.put(id, outDegree);
        }

        return result;
    }

    private double normalize(double value, double max, double min) {
        return max - min > 0 ? (value - min) / (max - min) : 0;
    }
}
