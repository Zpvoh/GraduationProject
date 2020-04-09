package application.controller;

import application.model.*;
import application.service.*;
import application.strategies.EvaluationList;
import application.strategies.PrecursorGraph;
import application.strategies.evaluation.EvaluationStrategy;
import application.strategies.evaluation.NaiveEvaluationStrategy;
import application.strategies.evaluation.ScoreList;
import application.strategies.reasoning.NaiveReasoningStrategy;
import application.strategies.recommendation.DecayRecommendationStrategy;
import application.strategies.recommendation.ImportanceSortedList;
import application.strategies.recommendation.RecommendationStrategy;
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
        Graph graph = graphService.findByGraphId(graph_id);
        graph.setGraphNodes(new HashSet<>(graphService.getAllNodes(graph_id)));
        Iterator<GraphNode> nodeIterator = graph.getGraphNodes().iterator();
        while(nodeIterator.hasNext()){
            GraphNode node = nodeIterator.next();
            node.setSuccessors(new HashSet<>(Arrays.asList(graphNodeService.findSuccessor(node.getLong_id()))));
            node.setChildren(new HashSet<>(Arrays.asList(graphNodeService.findChildren(node.getLong_id()))));
            node.setAntonyms(new HashSet<>(Arrays.asList(graphNodeService.findAntonym(node.getLong_id()))));
            node.setSynonyms(new HashSet<>(Arrays.asList(graphNodeService.findSynonym(node.getLong_id()))));
        }
        PrecursorGraph precursorGraph = graphService.getPrecursorGraph(graph, new NaiveReasoningStrategy());

        ScoreList scoreList = new ScoreList(precursorGraph);
        nodeIterator = graph.getGraphNodes().iterator();
        while(nodeIterator.hasNext()){
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
        }

        EvaluationStrategy evaluationStrategy = new NaiveEvaluationStrategy();
        EvaluationList evaluationList = evaluationStrategy.useStrategy(scoreList);

        RecommendationStrategy recommendationStrategy = new DecayRecommendationStrategy();
        ImportanceSortedList importanceSortedList = recommendationStrategy.useStrategy(precursorGraph, evaluationList);
        
        return gson.toJson(importanceSortedList);
    }
}
