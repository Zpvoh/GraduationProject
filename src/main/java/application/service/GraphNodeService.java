package application.service;

import application.controller.json_model.NodeValue;
import application.model.*;
import application.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GraphNodeService {
    @Autowired
    private GraphNodeRepository nodeRepository;
    @Autowired
    private AssignmentJudgmentRepository assignmentJudgmentRepository;
    @Autowired
    private AssignmentMultipleRepository assignmentMultipleRepository;
    @Autowired
    private AssignmentShortRepository assignmentShortRepository;

    public GraphNode findByNodeId(String course_id, String mindmap_id, String nodeId) {
        return nodeRepository.findByNodeId(course_id, mindmap_id, nodeId);
    }

    public Courseware[] findCoursewares(long id) {
        return nodeRepository.findCoursewares(id);
    }

    public Material[] findMaterials(long id) {
        return nodeRepository.findMaterials(id);
    }

    public Link[] findLinks(long id) {
        return nodeRepository.findLinks(id);
    }

    public AssignmentMultiple[] findAssignmentMultiple(long id) {
        return nodeRepository.findAssignmentMultiple(id);
    }

    public AssignmentJudgment[] findAssignmentJudgements(long id) {
        return nodeRepository.findAssignmentJudgments(id);
    }

    public AssignmentShort[] findAssignmentShort(long id) {
        return nodeRepository.findAssignmentShort(id);
    }

    public void delete(GraphNode node) {
        nodeRepository.delete(node);
    }

    public void save(GraphNode node) {
        nodeRepository.save(node);
    }

    public GraphNode[] findChildren(long id ){
        return nodeRepository.findChildren(id);
    }

    public GraphNode[] findSuccessor(long id ){
        return nodeRepository.findSuccessor(id);
    }

    public GraphNode[] findSynonym(long id ){
        return nodeRepository.findSynonym(id);
    }

    public GraphNode[] findAntonym(long id ){
        return nodeRepository.findAntonym(id);
    }

    public Note[] getNotes(long id){
        return nodeRepository.findNotes(id);
    }

    public GraphNode getNodeByLongId(long id) {
        return nodeRepository.findByNodeLongId(id);
    }

    /**
     * 获得单个节点的w1值
     * @param nid 节点id
     * @param sid 学生id
     * @return 单个节点的w1值
     */
    public NodeValue getSingleNodeValue(long nid, long sid) {
        return null;
    }

//    public void updateNetwork() {
//        // 1. 获取所有节点
//        List<Node> nodeList = (List<Node>) nodeRepository.findAll();
//
//        // 2. 遍历所有节点
//        for (Node node: nodeList) {
//
//            // 3. 生成id
//            String assignmentId = node.getCourse_mindmap() + " " + node.getId();
//
//            // 4. 根据id找到三种题目
//            List<AssignmentMultiple> assignmentMultipleList = assignmentMultipleRepository.findByMultiId(assignmentId);
//            List<AssignmentJudgment> assignmentJudgmentList = assignmentJudgmentRepository.findByJudge_id(assignmentId);
//            List<AssignmentShort> assignmentShortList = assignmentShortRepository.findByShortId(assignmentId);
//
//            if (node.getId().equals("7a6440a66b671c1e")) {
//                System.out.println(node.getTopic());
//                System.out.println(assignmentMultipleList.size() + "  " +  assignmentJudgmentList.size() +
//                "  "  +  assignmentShortList.size());
//            }
//
//            // 5.添加关系
//            for (AssignmentMultiple multiple: assignmentMultipleList) {
//                node.setAssignmentMultiple(multiple);
//            }
//            for (AssignmentJudgment judgment: assignmentJudgmentList) {
//                node.setAssignmentJudgments(judgment);
//            }
//            for (AssignmentShort assignmentShort: assignmentShortList) {
//                node.setAssignmentShorts(assignmentShort);
//            }
//            nodeRepository.save(node);
//        }
//    }

//    public  List<NodeValue> getNodeValue(String mindmap_id) {
//        List<NodeValue> nodeValueList = new ArrayList<>();
//        //获得mindmap
//        Mindmap tempMindmap = mindmapService.findByMindmapId(mindmap_id);
//
//        Node root_node = mindmapService.findRootNode(tempMindmap.getId());
//        if (root_node == null)
//            return nodeValueList;
//
//        //深度遍历
//        Queue<Node> nodes = new LinkedList<>();
//        Queue<Node> nodesChildren = new LinkedList<>();
//        nodes.add(root_node);
//
//        while (!nodes.isEmpty() || !nodesChildren.isEmpty()) {
//
//            if (nodes.isEmpty()) {
//                nodes = nodesChildren;
//                nodesChildren = new LinkedList<>();
//            }
//            Node thisNode = nodes.peek();
//
//            //获得答题人数
//            int number = 0;
//            int correctNumber = 0;
//
//            int tmpScore = 0;
//            int tmpStudentScore = 0;
//
//            // 每个node创建一个nodeValue
//            NodeValue nodeValue = new NodeValue();
//            nodeValue.setNode_id(thisNode.getId());
//            nodeValue.setNid(thisNode.getLong_id());
//            nodeValue.setNode_topic(thisNode.getTopic());
//
//
//            // 选择题回答情况统计
//            AssignmentMultiple[] multiples = nodeService.findAssignmentMultiple(thisNode.getLong_id());
//            for (AssignmentMultiple mul : multiples) {
//                number = Integer.parseInt(mul.getNumber());
//                correctNumber = Integer.parseInt(mul.getCorrect_number());
//
//                tmpScore += number * mul.getValue();
//                tmpStudentScore += correctNumber * mul.getValue();
//            }
//
//            // 判断题回答情况统计
//            AssignmentJudgment[] judgments = nodeService.findAssignmentJudgements(thisNode.getLong_id());
//            for (AssignmentJudgment judgment : judgments) {
//                number = Integer.parseInt(judgment.getNumber());
//                correctNumber = Integer.parseInt(judgment.getCorrect_number());
//
//                tmpScore += number * judgment.getValue();
//                tmpStudentScore += correctNumber * judgment.getValue();
//            }
//
//            nodeValue.setScore(tmpScore);
//            nodeValue.setStudentScore(tmpStudentScore);
//            nodeValue.setValue(tmpScore == 0 ? 0.0 : (double)tmpStudentScore/tmpScore);
//
//            //加入到nodesValueList中
//            nodeValueList.add(nodeValue);
//
//            Collections.addAll(nodesChildren, nodeService.findChildren(thisNode.getLong_id()));
////
////            for (Node child : nodeService.findChildren(thisNode.getLong_id())) {
////                nodesChildren.add(child);
////            }
////
//            //移除
//            nodes.remove();
//        }
//
//        return nodeValueList;
//    }
}
