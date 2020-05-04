package application.controller;

import application.model.*;
import application.service.*;
import application.strategies.EvaluationList;
import application.strategies.PrecursorGraph;
import application.strategies.evaluation.EvaluationStrategy;
import application.strategies.evaluation.NaiveEvaluationStrategy;
import application.strategies.evaluation.ScoreList;
import application.strategies.reasoning.NaiveReasoningStrategy;
import application.strategies.recommendation.*;
import application.strategies.testRecommend.ConductTestRecommendStrategy;
import application.strategies.testRecommend.TestRecommendStrategy;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@CrossOrigin
public class RecommendationController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private GraphService graphService;
    @Autowired
    private GraphNodeService graphNodeService;
    @Autowired
    private NodeChildService nodeChildService;

    private Gson gson = new Gson();

    @RequestMapping(value = "/recommendation/{course_id}/{graph_id}/{student_name}")
    public String generateRecommendationList(@PathVariable String course_id, @PathVariable String graph_id,
                                             @PathVariable String student_name){
        long time = 0;
        // 1. 根据graph_id从数据库查询知识图谱
        time = System.currentTimeMillis();
        Graph graph = graphService.findByGraphId(graph_id);
        System.out.println("1. 根据graph_id从数据库查询知识图谱: "+(System.currentTimeMillis()-time)+"ms");
        // 2. 获取知识图谱中所有节点
        time = System.currentTimeMillis();
        graph.setGraphNodes(new HashSet<>(graphService.getAllNodes(graph_id)));
        Iterator<GraphNode> nodeIterator = graph.getGraphNodes().iterator();
        System.out.println("2. 获取知识图谱中所有节点: "+(System.currentTimeMillis()-time)+"ms");
        // 3. 获取知识图谱中的所有关系
//        while(nodeIterator.hasNext()){
//            time = System.currentTimeMillis();
//            GraphNode node = nodeIterator.next();
//            node.setSuccessors(new HashSet<>(Arrays.asList(graphNodeService.findSuccessor(node.getLong_id()))));
//            node.setChildren(new HashSet<>(Arrays.asList(graphNodeService.findChildren(node.getLong_id()))));
//            node.setAntonyms(new HashSet<>(Arrays.asList(graphNodeService.findAntonym(node.getLong_id()))));
//            node.setSynonyms(new HashSet<>(Arrays.asList(graphNodeService.findSynonym(node.getLong_id()))));
//            System.out.println("3. 获取知识图谱中的所有关系: "+(System.currentTimeMillis()-time)+"ms");
//        }
        // 4. 形成前序图（调用前序图算法）
        time = System.currentTimeMillis();
//        PrecursorGraph precursorGraph = graphService.getPrecursorGraph(graph, new NaiveReasoningStrategy());
        PrecursorGraph precursorGraph = graphService.getPrecursorGraphUseReasoning(graph);
        System.out.println("4. 形成前序图（调用前序图算法): "+(System.currentTimeMillis()-time)+"ms");

        // 5. 构造evaluation list
        ScoreList scoreList = new ScoreList(precursorGraph);
        nodeIterator = graph.getGraphNodes().iterator();
        while(nodeIterator.hasNext()){
            time = System.currentTimeMillis();
            String node_id = nodeIterator.next().getId();
            List<StudentAnswer> answers = nodeChildService.getStudentAnswersForANode(course_id, graph_id, node_id, student_name);
            String assignmentId =course_id + " " + graph_id + " " + node_id;
            List<AssignmentMultiple> multiples = nodeChildService.findMultis(assignmentId);
            List<AssignmentJudgment> judgments = nodeChildService.findJudgements(assignmentId);
            List<AssignmentShort> shorts = nodeChildService.findShorts(assignmentId);
            List<Integer> scoreActual = new ArrayList<>();
            List<Integer> scoreTotal = new ArrayList<>();
            for(StudentAnswer ans : answers){
                scoreActual.add(ans.getScore());
            }

            for(AssignmentMultiple ele : multiples){
                scoreTotal.add(ele.getValue());
            }

            for(AssignmentJudgment ele : judgments){
                scoreTotal.add(ele.getValue());
            }

            for(AssignmentShort ele : shorts){
                scoreTotal.add(ele.getValue());
            }

            scoreList.addScoreActualList(scoreActual);
            scoreList.addScoreTotalList(scoreTotal);
            System.out.println("5. 构造evaluation list: "+(System.currentTimeMillis()-time)+"ms");
        }

        EvaluationStrategy evaluationStrategy = new NaiveEvaluationStrategy();
        EvaluationList evaluationList = evaluationStrategy.useStrategy(scoreList);

        // 6. 使用推荐算法进行推荐排序
        time = System.currentTimeMillis();
        RecommendationStrategy recommendationStrategy = new PathDecayRecommendationStrategy(1, 0.5);
        ImportanceSortedList importanceSortedList = recommendationStrategy.useStrategy(precursorGraph, evaluationList);
        System.out.println("6. 使用推荐算法进行推荐排序: "+(System.currentTimeMillis()-time)+"ms");

        time = System.currentTimeMillis();
        TestRecommendStrategy testRecommendStrategy = new ConductTestRecommendStrategy(1);
        List<VertexWithValue> vertexList = testRecommendStrategy.useStrategy(precursorGraph, evaluationList);
        System.out.println("7. 使用试题推荐算法进行推荐排序: "+(System.currentTimeMillis()-time)+"ms");

        System.out.println();
        
        List<Object> result = new ArrayList<>();
        result.add(importanceSortedList);
        result.add(vertexList);
        return gson.toJson(result);
    }
}
