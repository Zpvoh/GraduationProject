package application.service;

import application.controller.json_model.NodeCount;
import application.model.*;
import application.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MindmapService {
    @Autowired
    private MindmapRepository mindmapRepository;
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private CourseService courseService;

    public Mindmap findByMindmapId(String id) {
        return mindmapRepository.findByMindmap_id(id);
    }

    public Node findRootNode(long id) {
        return mindmapRepository.findRootNode(id);
    }

    public void delete(Mindmap mindmap) {
        mindmapRepository.delete(mindmap);
    }

    public boolean deleteMindmapById(String mindmapId){
        // 先确定有没有权限
//        Mindmap mindmap = mindmapRepository.hasAuthToDeleteMap(mindmapId, username);
//        if (mindmap == null)
//            return false;

        // 再删掉map有关的学生答案
        courseService.deleteStudentAnsByMindmapId(mindmapId);

        // 最后删掉相关节点
        mindmapRepository.deleteMindmap(mindmapId);

        return true;
    };

    public void save(Mindmap mindmap) {
        mindmapRepository.save(mindmap);
    }

    public boolean resetName(String mindmap_id, String newName) {
        if (mindmapRepository.findByMindmap_name(newName) != null)
            return false;

        Mindmap mindmap = mindmapRepository.findByMindmap_id(mindmap_id);
        mindmap.setMindmap_name(newName);
        mindmapRepository.save(mindmap);
        return true;
    }

    /**
     * 得到一张图下所有节点的资源情况（包括课件，资源，作业）
     * @param mindmap_id
     * @return
     */
    public List<NodeCount> getNodeCount(String mindmap_id) {
        List<NodeCount> nodeCountList = new ArrayList<>();
        //获得mindmap
        Mindmap tempMindmap = mindmapRepository.findByMindmap_id(mindmap_id);

        Node root_node = mindmapRepository.findRootNode(tempMindmap.getId());
        if (root_node == null)
            return nodeCountList;

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


            // 每个node创建一个nodeCount
            NodeCount nodeCount = new NodeCount();
            nodeCount.setNodeId(thisNode.getId());
            nodeCount.setNodeTopic(thisNode.getTopic());

            // 选择题统计
            AssignmentMultiple[] multiples = nodeRepository.findAssignmentMultiple(thisNode.getLong_id());
            // 判断题统计
            AssignmentJudgment[] judgments = nodeRepository.findAssignmentJudgments(thisNode.getLong_id());
            // 简答题统计
            AssignmentShort[] shorts = nodeRepository.findAssignmentShort(thisNode.getLong_id());
            nodeCount.setHomeworkNum(multiples.length + judgments.length + shorts.length);

            // resourceNum
            Link[] links = nodeRepository.findLinks(thisNode.getLong_id());
            Material[] materials = nodeRepository.findMaterials(thisNode.getLong_id());
            nodeCount.setResourceNum(links.length + materials.length);

            // coursewareNum
            Courseware[] coursewares = nodeRepository.findCoursewares(thisNode.getLong_id());
            nodeCount.setCoursewareNum(coursewares.length);

            //加入到nodesCountList中
            nodeCountList.add(nodeCount);

            Collections.addAll(nodesChildren, nodeRepository.findChildren(thisNode.getLong_id()));
            nodes.remove();
        }

        return nodeCountList;
    }

    public Mindmap[] findAllMindmap() {
        return mindmapRepository.findAllMindMap();
    }

    public Course findCourseOfMindmap(String mindmap_id){
        return mindmapRepository.findCourseOfMindmap(mindmap_id);
    }
}
