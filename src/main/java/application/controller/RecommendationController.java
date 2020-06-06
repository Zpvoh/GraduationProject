package application.controller;

import application.model.*;
import application.service.*;
import application.strategies.*;
import application.strategies.evaluation.EvaluationStrategy;
import application.strategies.evaluation.PathDecayEvaluationStrategy;
import application.strategies.evaluation.RandomEvaluationStrategy;
import application.strategies.recommendation.*;
import application.strategies.testRecommend.InfluenceTestRecommendStrategy;
import application.strategies.testRecommend.TestRecommendStrategy;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
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
                                             @PathVariable String student_name) {
        long time = 0;
        // 1. 根据graph_id从数据库查询知识图谱
        time = System.currentTimeMillis();
        Graph graph = graphService.findByGraphId(graph_id);
        System.out.println("1. 根据graph_id从数据库查询知识图谱: " + (System.currentTimeMillis() - time) + "ms");
        // 2. 获取知识图谱中所有节点
        time = System.currentTimeMillis();
        graph.setGraphNodes(new HashSet<>(graphService.getAllNodes(graph_id)));
        Iterator<GraphNode> nodeIterator = graph.getGraphNodes().iterator();
        System.out.println("2. 获取知识图谱中所有节点: " + (System.currentTimeMillis() - time) + "ms");

        // 3. 形成前序图（调用前序图算法）
        time = System.currentTimeMillis();
        PrecursorGraph precursorGraph = graphService.getPrecursorGraphUseReasoning(graph);
        System.out.println("3. 形成前序图（调用前序图算法): " + (System.currentTimeMillis() - time) + "ms");

        // 4. 构造evaluation list
        ScoreList scoreList = new ScoreList(precursorGraph);
        nodeIterator = graph.getGraphNodes().iterator();
        while (nodeIterator.hasNext()) {
            time = System.currentTimeMillis();
            String node_id = nodeIterator.next().getId();
            List<StudentAnswer> answers = nodeChildService.getStudentAnswersForANode(course_id, graph_id, node_id, student_name);
            String assignmentId = course_id + " " + graph_id + " " + node_id;
            List<AssignmentMultiple> multiples = nodeChildService.findMultis(assignmentId);
            List<AssignmentJudgment> judgments = nodeChildService.findJudgements(assignmentId);
            List<AssignmentShort> shorts = nodeChildService.findShorts(assignmentId);
            List<Integer> scoreActual = new ArrayList<>();
            List<Integer> scoreTotal = new ArrayList<>();
            boolean found = false;
            for (StudentAnswer ans : answers) {
                found = false;
                scoreActual.add(ans.getScore());
                for (AssignmentMultiple ele : multiples) {
                    if (found) {
                        break;
                    }
                    if (ele.getId() == ans.getAssignmentLongId()) {
                        scoreTotal.add(ele.getValue());
                        found = true;
                        break;
                    }
                }

                for (AssignmentJudgment ele : judgments) {
                    if (found) {
                        break;
                    }
                    if (ele.getId() == ans.getAssignmentLongId()) {
                        scoreTotal.add(ele.getValue());
                        found = true;
                        break;
                    }
                }

                for (AssignmentShort ele : shorts) {
                    if (found) {
                        break;
                    }
                    if (ele.getId() == ans.getAssignmentLongId()) {
                        scoreTotal.add(ele.getValue());
                        found = true;
                        break;
                    }
                }
            }

//            for (AssignmentMultiple ele : multiples) {
//                scoreTotal.add(ele.getValue());
//            }
//
//            for (AssignmentJudgment ele : judgments) {
//                scoreTotal.add(ele.getValue());
//            }
//
//            for (AssignmentShort ele : shorts) {
//                scoreTotal.add(ele.getValue());
//            }

            scoreList.addScoreActualList(scoreActual);
            scoreList.addScoreTotalList(scoreTotal);
            scoreList.getAssignmentNumber().add(judgments.size() + multiples.size() + shorts.size());
            System.out.println("4. 构造evaluation list: " + (System.currentTimeMillis() - time) + "ms");
        }

        // 5. 使用学习情况分析算法进行学情分析
        time = System.currentTimeMillis();
        ImportanceSortedList learningSituation = graphService.evaluateGraphNodePathDecay(scoreList, 1, 0.5);
        System.out.println("5. 使用学习情况分析算法进行学情分析: " + (System.currentTimeMillis() - time) + "ms");

        // 6. 使用学习推荐算法进行资源推荐排序
        time = System.currentTimeMillis();
        ImportanceSortedList recommendImportance = graphService.evaluateRecommendationByOutdegree(graph_id, precursorGraph, learningSituation,
                1, 0.5, 0.1);
        System.out.println("6. 使用学习推荐算法进行资源推荐排序: " + (System.currentTimeMillis() - time) + "ms");

        time = System.currentTimeMillis();
        TestRecommendStrategy testRecommendStrategy = new InfluenceTestRecommendStrategy(1, 0.5);
        List<VertexWithValue> vertexList = testRecommendStrategy.useStrategy(precursorGraph, learningSituation.getEvaluationList());
        System.out.println("7. 使用试题推荐算法进行推荐排序: " + (System.currentTimeMillis() - time) + "ms");

        System.out.println();

        List<Object> result = new ArrayList<>();
        result.add(learningSituation);
        result.add(vertexList);
        result.add(recommendImportance);
        return gson.toJson(result);
    }
}
