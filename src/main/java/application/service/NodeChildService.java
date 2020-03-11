package application.service;

import application.controller.json_model.AssignmentRealAnswer;
import application.controller.json_model.NodeValue;
import application.controller.json_model.NodesAccuracy;
import application.model.*;
import application.repository.*;
//import jdk.nashorn.internal.ir.Assignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

@Service
public class NodeChildService {
    @Autowired
    private CoursewareRepository coursewareRepository;
    @Autowired
    private LinkRepository linkRepository;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private AssignmentMultipleRepository assignmentMultipleRepository;
    @Autowired
    private AssignmentShortRepository assignmentShortRepository;
    @Autowired
    private StudentAnswerRepository studentAnswerRepository;
    @Autowired
    private AssignmentJudgmentRepository assignmentJudgmentRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private MindmapRepository mindmapRepository;
    @Autowired
    private NodeRepository nodeRepository;


    public void deleteCoursewareFather(String coursewareName) {
        coursewareRepository.deleteFather(coursewareName);
    }

    public void createCoursewareFather(String coursewareName, String course_mindmap, String nodeId) {
        coursewareRepository.createFather(coursewareName, course_mindmap, nodeId);
    }

    public void deleteLinkFather(String linkAddress) {
        linkRepository.deleteFather(linkAddress);
    }

    public void createLinkFather(String linkAddress, String course_mindmap, String nodeId) {
        linkRepository.createFather(linkAddress, course_mindmap, nodeId);
    }

    public void deleteMaterialFather(String materialName) {
        materialRepository.deleteFather(materialName);
    }

    public void createMaterialFather(String materialName, String course_mindmap, String nodeId) {
        materialRepository.createFather(materialName, course_mindmap, nodeId);
    }

    public void deleteAssignmentMultiFather(long id) {
        assignmentMultipleRepository.deleteFather(id);
    }

    public void deleteAssignmentJudgeFather(long id) {
        assignmentJudgmentRepository.deleteFather(id);
    }

    public void createAssignmentMultiFather(long id, String course_mindmap, String nodeId) {
        assignmentMultipleRepository.createFather(id, course_mindmap, nodeId);
    }

    public void createAssignmentJudgeFather(long id, String course_mindmap, String nodeId) {
        assignmentJudgmentRepository.createFather(id, course_mindmap, nodeId);
    }

    public void deleteAssignmentShortFather(long id) {
        assignmentShortRepository.deleteFather(id);
    }

    public void createAssignmentShortFather(long id, String course_mindmap, String nodeId) {
        assignmentShortRepository.createFather(id, course_mindmap, nodeId);
    }

    public List<AssignmentShort> findShorts(String shortId) {
        return assignmentShortRepository.findByShortId(shortId);
    }

    public List<AssignmentMultiple> findMultis(String multiId) {
        return assignmentMultipleRepository.findByMultiId(multiId);
    }

    public void saveMulti(AssignmentMultiple assignmentMultiple) {
        assignmentMultipleRepository.save(assignmentMultiple);
    }

    public List<AssignmentJudgment> findJudgements(String judgeId) {
        return assignmentJudgmentRepository.findByJudge_id(judgeId);
    }

    public void saveJudge(AssignmentJudgment assignmentJudgment) {
        assignmentJudgmentRepository.save(assignmentJudgment);
    }

    public StudentAnswer getStudentAns(long studentId, long assignmentId) {
        return studentAnswerRepository.findByStudentIdAndAndAssignmentLongId(studentId, assignmentId);
    }

    public List<StudentAnswer> getStudentAns(long assignmentId) {
        return studentAnswerRepository.findByAssignmentLongId(assignmentId);
    }

    public StudentAnswer addStudentAnswer(StudentAnswer studentAnswer) {
        return studentAnswerRepository.save(studentAnswer);
    }

    public void saveShort(AssignmentShort assignmentShort) {
        assignmentShortRepository.save(assignmentShort);
    }

    public void saveCourseware(Courseware courseware) {
        coursewareRepository.save(courseware);
    }

    public Material saveMaterial(Material material) {
       return materialRepository.save(material);
    }

    public void saveLink(Link link) {
        linkRepository.save(link);
    }

    public Courseware findCourseware(String storeAddress) {
        return coursewareRepository.findByStoreAddress(storeAddress);
    }

    public Material findMaterial(String storeAddress) {
        return materialRepository.findByStoreAddress(storeAddress);
    }

    private Material getMaterial(String materialName, String storeAddress) {
        return materialRepository.findByMaterialNameAndStoreAddress(materialName, storeAddress);
    }

    public void deleteMaterial(String materialName, String storeAddress) {
        Material material = getMaterial(materialName, storeAddress);
        materialRepository.delete(material);
    }

    private Courseware getCourseware(String courseware_name, String store_address) {
        return coursewareRepository.findByCourseware_nameAndStore_address(courseware_name, store_address);
    }

    public void deleteCourseware(String courseware_name, String store_address) {
        Courseware courseware = getCourseware(courseware_name, store_address);
        coursewareRepository.delete(courseware);
    }

    private Link getLink(String link_name, String nodeId) {
        return linkRepository.findByNode(link_name, nodeId);
    }

    public void deleteLink(String link_name, String nodeId) {
        Link link = getLink(link_name, nodeId);
        linkRepository.delete(link);
    }

    public void setAssignmentShortScore(long assignmentLongId, long studentId, int score){
        assignmentShortRepository.setAnswerScore(assignmentLongId, studentId, score);
    }

    public List<StudentAnswer> getStudentAnswersForANode(String course_id, String mindmap_id, String node_id, String username) {
        List<StudentAnswer> results = new ArrayList<>();

        String assignmentId =course_id + " " + mindmap_id + " " + node_id;
        List<AssignmentMultiple> multiples = findMultis(assignmentId);
        List<AssignmentJudgment> judgments = findJudgements(assignmentId);
        List<AssignmentShort> shorts = findShorts(assignmentId);

        for (AssignmentMultiple multiple: multiples) {
            StudentAnswer studentAnswer = studentAnswerRepository.findByStudentNameAndAndAssignmentId(username, assignmentId+multiple.getId());
            if (studentAnswer != null)
                results.add(studentAnswer);
        }

        for (AssignmentJudgment judgment: judgments) {
            StudentAnswer studentAnswer = studentAnswerRepository.findByStudentNameAndAndAssignmentId(username, assignmentId+judgment.getId());
            if (studentAnswer != null)
                results.add(studentAnswer);
        }

        for (AssignmentShort aShort: shorts) {
            StudentAnswer studentAnswer = studentAnswerRepository.findByStudentNameAndAndAssignmentId(username, assignmentId+aShort.getId());
            if (studentAnswer != null)
                results.add(studentAnswer);
        }

        return results;
    }

    public AssignmentRealAnswer getRealAnswer(Long id, int type, String username) {
        String assignmentId = "";
        String answer = "";
        switch (type) {
            case 1:
                AssignmentMultiple multiple = assignmentMultipleRepository.getAssignmentMultipleById(id);
                answer = multiple.getCorrect_answer();
                assignmentId = multiple.getMulti_id();
                break;
            case 2:
                AssignmentShort aShort = assignmentShortRepository.getAssignmentShortById(id);
                answer = aShort.getCorrect_answer();
                assignmentId = aShort.getShort_id();
                break;
            case 3:
                AssignmentJudgment judgment = assignmentJudgmentRepository.getAssignmentJudgmentById(id);
                answer = judgment.getCorrect_answer();
                assignmentId = judgment.getJudge_id();
                break;
        }

        StudentAnswer studentAnswer = studentAnswerRepository.findByStudentNameAndAndAssignmentId(username, assignmentId+id);
        if (studentAnswer == null)
            return null;
        else {
            AssignmentRealAnswer realAnswer = new AssignmentRealAnswer();
            realAnswer.setAnswer(answer);

            return realAnswer;
        }


    }

    public List<AssignmentShort> getAllAssignmentShort(){
        return assignmentShortRepository.getAll();
    }

    public List<AssignmentShort> getAssignmentShortsByNodeId(long nid) {
        return assignmentShortRepository.getAssignmentShortsByNodeId(nid);
    }

    public List<NodesAccuracy> getAccuracy(String mindmap_id, String type) {
        List<NodesAccuracy> nodesAccuracyList = new LinkedList<>();

        //获得mindmap
        Mindmap tempMindmap = mindmapRepository.findByMindmap_id(mindmap_id);

        Node root_node = mindmapRepository.findRootNode(tempMindmap.getId());
        if (root_node == null)
            return nodesAccuracyList;

        //深度遍历
        Queue<Node> nodes = new LinkedList<>();
        Queue<Node> nodesChildren = new LinkedList<>();
        nodes.add(root_node);

        while (!nodes.isEmpty() || !nodesChildren.isEmpty()) {

            if (nodes.isEmpty()) {
                nodes = nodesChildren;
                nodesChildren = new LinkedList<>();
            }
            Node thisNode = nodes.peek();

            //获得答题人数
            int number = 0;
            int correctNumber = 0;

            if (type.equals("Multiple")) {
                AssignmentMultiple[] multiples = nodeRepository.findAssignmentMultiple(thisNode.getLong_id());
                for (AssignmentMultiple mul : multiples) {
                    number += Integer.parseInt(mul.getNumber());
                    correctNumber += Integer.parseInt(mul.getCorrect_number());
                }
            } else if (type.equals("Judgment")) {
                AssignmentJudgment[] judgments = nodeRepository.findAssignmentJudgments(thisNode.getLong_id());
                for (AssignmentJudgment judgment : judgments) {
                    number += Integer.parseInt(judgment.getNumber());
                    correctNumber += Integer.parseInt(judgment.getCorrect_number());
                }
            }


            //加入到nodesAccuracyList中
            NodesAccuracy nodesAccuracy = new NodesAccuracy();
            nodesAccuracy.setNode_id(thisNode.getId());
            nodesAccuracy.setNode_topic(thisNode.getTopic());
            nodesAccuracy.setNumber(number + "");
            nodesAccuracy.setCorrect_number(correctNumber + "");

            String acc = "0.00";
            DecimalFormat df = new DecimalFormat("#.00");
            if (number != 0)
                acc = df.format((double) correctNumber / number);
            nodesAccuracy.setAccuracy(acc);
            nodesAccuracyList.add(nodesAccuracy);

            nodesChildren.addAll(Arrays.asList(nodeRepository.findChildren(thisNode.getLong_id())));
            //移除
            nodes.remove();
        }

        return nodesAccuracyList;
    }

    public List<NodeValue> getNodeValue(String mindmap_id) {
        List<NodeValue> nodeValueList = new ArrayList<>();
        //获得mindmap
        Mindmap tempMindmap = mindmapRepository.findByMindmap_id(mindmap_id);

        Node root_node = mindmapRepository.findRootNode(tempMindmap.getId());
        if (root_node == null)
            return nodeValueList;

        //深度遍历
        Queue<Node> nodes = new LinkedList<>();
        Queue<Node> nodesChildren = new LinkedList<>();
        nodes.add(root_node);

        while (!nodes.isEmpty() || !nodesChildren.isEmpty()) {

            if (nodes.isEmpty()) {
                nodes = nodesChildren;
                nodesChildren = new LinkedList<>();
            }
            Node thisNode = nodes.peek();

            //获得答题人数
            int number = 0;
            int correctNumber = 0;

            int tmpScore = 0;
            int tmpStudentScore = 0;

            // 每个node创建一个nodeValue
            NodeValue nodeValue = new NodeValue();
            nodeValue.setNode_id(thisNode.getId());
            nodeValue.setNid(thisNode.getLong_id());
            nodeValue.setNode_topic(thisNode.getTopic());


            // 选择题回答情况统计
            AssignmentMultiple[] multiples = nodeRepository.findAssignmentMultiple(thisNode.getLong_id());
            for (AssignmentMultiple mul : multiples) {
                number = Integer.parseInt(mul.getNumber());
                correctNumber = Integer.parseInt(mul.getCorrect_number());

                tmpScore += number * mul.getValue();
                tmpStudentScore += correctNumber * mul.getValue();
            }

            // 判断题回答情况统计
            AssignmentJudgment[] judgments = nodeRepository.findAssignmentJudgments(thisNode.getLong_id());
            for (AssignmentJudgment judgment : judgments) {
                number = Integer.parseInt(judgment.getNumber());
                correctNumber = Integer.parseInt(judgment.getCorrect_number());

                tmpScore += number * judgment.getValue();
                tmpStudentScore += correctNumber * judgment.getValue();
            }

            nodeValue.setScore(tmpScore);
            nodeValue.setStudentScore(tmpStudentScore);
            nodeValue.setValue(tmpScore == 0 ? 0.0 : (double)tmpStudentScore/tmpScore);

            //加入到nodesValueList中
            nodeValueList.add(nodeValue);

            Collections.addAll(nodesChildren, nodeRepository.findChildren(thisNode.getLong_id()));

            //移除
            nodes.remove();
        }

        return nodeValueList;
    }

    public List<NodeValue> getNodeValueForStudent(String mindmap_id, long studentId) {
        List<NodeValue> nodeValueList = new ArrayList<>();
        //获得mindmap
        Mindmap tempMindmap = mindmapRepository.findByMindmap_id(mindmap_id);

        Node root_node = mindmapRepository.findRootNode(tempMindmap.getId());
        if (root_node == null)
            return nodeValueList;

        //深度遍历
        Queue<Node> nodes = new LinkedList<>();
        Queue<Node> nodesChildren = new LinkedList<>();
        nodes.add(root_node);

        while (!nodes.isEmpty() || !nodesChildren.isEmpty()) {

            if (nodes.isEmpty()) {
                nodes = nodesChildren;
                nodesChildren = new LinkedList<>();
            }
            Node thisNode = nodes.peek();

            int totalScore = 0;
            int score = 0;
            int studentScore = 0;

            // 每个node创建一个nodeValue
            NodeValue nodeValue = new NodeValue();
            nodeValue.setNode_id(thisNode.getId());
            nodeValue.setNid(thisNode.getLong_id());
            nodeValue.setNode_topic(thisNode.getTopic());

            StudentAnswer studentAnswer;

            // 选择题回答情况统计
            AssignmentMultiple[] multiples = nodeRepository.findAssignmentMultiple(thisNode.getLong_id());
            for (AssignmentMultiple mul : multiples) {
                totalScore += mul.getValue();
                studentAnswer = studentAnswerRepository.findByStudentIdAndAndAssignmentLongId(studentId, mul.getId());
               if (studentAnswer != null) {
                   score += mul.getValue();
                   if (studentAnswer.getAnswer().equals(mul.getCorrect_answer())) {//学生回答,并且答案正确
                       studentScore += mul.getValue();
                    }
               }
            }

            // 判断题回答情况统计
            AssignmentJudgment[] judgments = nodeRepository.findAssignmentJudgments(thisNode.getLong_id());
            for (AssignmentJudgment judgment : judgments) {
                totalScore += judgment.getValue();
                studentAnswer = studentAnswerRepository.findByStudentIdAndAndAssignmentLongId(studentId, judgment.getId());
                if (studentAnswer != null) {
                    score += judgment.getValue();
                    if (studentAnswer.getAnswer().equals(judgment.getCorrect_answer())) {//学生回答,并且答案正确
                        studentScore += judgment.getValue();
                    }
                }
            }

            nodeValue.setScore(score);
            nodeValue.setTotalScore(totalScore);
            nodeValue.setStudentScore(studentScore);
            nodeValue.setValue(score == 0.0 ? 1.0 : (double)studentScore/score);

            //加入到nodesValueList中
            nodeValueList.add(nodeValue);

            Collections.addAll(nodesChildren, nodeRepository.findChildren(thisNode.getLong_id()));

            //移除
            nodes.remove();
        }

        return nodeValueList;
    }

//    public void updateAnswers2() {
//        List<AssignmentShort> shortList = assignmentShortRepository.getAll();
//        List<AssignmentJudgment> judgmentList = assignmentJudgmentRepository.getAll();
//        List<AssignmentMultiple> multipleList = assignmentMultipleRepository.getAll();
//
//        for (AssignmentShort assignmentShort: shortList) {
//            List<StudentAnswer> studentAnswerList = studentAnswerRepository.
//                    findByAssignmentId(assignmentShort.getShort_id()+assignmentShort.getId());
//            for (StudentAnswer studentAnswer: studentAnswerList) {
//                studentAnswer.setAssignmentLongId(assignmentShort.getId());
//                studentAnswerRepository.save(studentAnswer);
//            }
//        }
//
//        for (AssignmentJudgment judgment: judgmentList) {
//            List<StudentAnswer> studentAnswerList = studentAnswerRepository.
//                    findByAssignmentId(judgment.getJudge_id()+judgment.getId());
//            for (StudentAnswer studentAnswer: studentAnswerList) {
//                studentAnswer.setAssignmentLongId(judgment.getId());
//                studentAnswerRepository.save(studentAnswer);
//            }
//        }
//
//        for (AssignmentMultiple multiple: multipleList) {
//            List<StudentAnswer> studentAnswerList = studentAnswerRepository.
//                    findByAssignmentId(multiple.getMulti_id()+multiple.getId());
//            for (StudentAnswer studentAnswer: studentAnswerList) {
//                studentAnswer.setAssignmentLongId(multiple.getId());
//                studentAnswerRepository.save(studentAnswer);
//            }
//        }
//    }
}
