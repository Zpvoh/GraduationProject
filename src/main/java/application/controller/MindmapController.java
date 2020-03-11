package application.controller;

import application.jena.JenaService;
import com.google.gson.Gson;
import application.controller.json_model.*;
import application.model.*;
import application.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class MindmapController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private MindmapService mindmapService;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private NodeChildService nodeChildService;
    @Autowired
    private JenaService jenaService;

    private Gson gson = new Gson();

    private String course_id;
    private String mindmap_id;

    @RequestMapping(value = "/mindmap/{course_id}/{mindmap_id}", method = RequestMethod.GET)
    public String mindmap(@PathVariable String course_id, @PathVariable String mindmap_id) {
        String json = null;
        //先找到mindmap
        Course course = courseService.findByCourseId(course_id);
        Mindmap[] mindmaps_course = courseService.findMindmaps(course.getId());

        Mindmap result_mindmap = null;
        for (Mindmap mindmap : mindmaps_course) {
            if (mindmap.getMindmap_id().equals(mindmap_id)) {
                result_mindmap = mindmap;
                break;
            }
        }

        if (result_mindmap != null) {
            json = result_mindmap.getJson_string();
            MindmapJson mindmapJson = gson.fromJson(json, MindmapJson.class);
            Graph graph = mindmapToGraph(mindmapJson);
            json = gson.toJson(graph);
        }
        return json;
    }

    @RequestMapping(value = "/mindmap_id_list/{course_id}", method = RequestMethod.GET)
    public MindmapIdName[] mindmap_id_list(@PathVariable String course_id) {
        //先找到course
        Course course = courseService.findByCourseId(course_id);

        if (course == null)
            return null;

        //找OWN关系
        Mindmap[] mindmaps = courseService.findMindmaps(course.getId());

        MindmapIdName[] mindmapList = new MindmapIdName[mindmaps.length];

        for (int i = 0; i < mindmaps.length; i++) {
            mindmapList[i] = new MindmapIdName();
            mindmapList[i].setId(mindmaps[i].getMindmap_id());
            mindmapList[i].setName(mindmaps[i].getMindmap_name());
        }

        return mindmapList;
    }

    @RequestMapping(value = "/save_mindmap/{course_id}/{mindmap_id}", method = RequestMethod.POST)
    public Success save_mindmap(@PathVariable String course_id, @PathVariable String mindmap_id, @RequestBody String json_string) {
        this.course_id = course_id;
        this.mindmap_id = mindmap_id;

        Success success = new Success();
        success.setSuccess(false);

        //获得课程
        Course course = courseService.findByCourseId(course_id);
        if (course == null) {
            return success;
        }

        // 需要判断该mindmap是否已经存在
        // 若存在，则做修改，否则新建
        boolean if_exist = false;
        Mindmap tempMindmap = mindmapService.findByMindmapId(mindmap_id);
        if (tempMindmap != null)
            if_exist = true;

        Mindmap_json mindmap_json = gson.fromJson(json_string, Mindmap_json.class);

        String mindmap_name = mindmap_json.getMeta().getName();

        Node root_node = mindmap_json.getData();

        //向每个node添加course_mindmap属性
        //并且对于已存在的node 将所有和次node有关系的节点都全都链接到新节点上
        root_node.setCourse_mindmap(course_id + " " + mindmap_id);
        root_node = recurseForNode(root_node);

        //存下node_root，其余node会自动生成
        nodeService.save(root_node);

        //保存mindmap
        Mindmap mindmap = new Mindmap();
        mindmap.setJson_string(json_string);
        mindmap.setMindmap_id(mindmap_id);
        mindmap.setMindmap_name(mindmap_name);

        //保存两者关系
        mindmap.setRootNode(root_node);
        mindmapService.save(mindmap);

        course.owns(mindmap);
        courseService.save(course);

        // 若已存在，则删除原先的mindmap
        if (if_exist) {
            Node tempRootNode = mindmapService.findRootNode(tempMindmap.getId());
            deleteChildren(tempRootNode);
            nodeService.delete(tempRootNode);
            mindmapService.delete(tempMindmap);
        }

        success.setSuccess(true);
        return success;
    }

    @RequestMapping(value = "/mindmap_node_count/{mindmap_id}", method = RequestMethod.GET)
    public List<NodeCount> getNodeCounts(@PathVariable String mindmap_id) {
        return mindmapService.getNodeCount(mindmap_id);
    }

    @RequestMapping(value = "/mindmap_delete/{mindmap_id}", method = RequestMethod.DELETE)
    public Success deleteMindmap(@PathVariable String mindmap_id) {
        Success success = new Success();
        boolean flag = mindmapService.deleteMindmapById(mindmap_id);
        success.setSuccess(flag);
        return success;
    }

    @RequestMapping(value = "/mindmap_resetName/{mindmap_id}/{newName}", method = RequestMethod.PUT)
    public Success resetName(@PathVariable String mindmap_id, @PathVariable String newName) {
        Success success = new Success();
        boolean flag = mindmapService.resetName(mindmap_id, newName);
        success.setSuccess(flag);
        return success;
    }

    @RequestMapping(value = "/getNode/{id}", method = RequestMethod.GET)
    public Node getNode(@PathVariable long id) {
        Node node = nodeService.getNodeByLongId(id);
        return node;
    }

    @RequestMapping(value = "/mindmap_suggestion/{mindmap_id}/{student_id}", method = RequestMethod.GET)
    public List<Suggestion> getSuggestion(@PathVariable String mindmap_id, @PathVariable long student_id) {
        return jenaService.getSuggestion(mindmap_id, student_id);
    }

    //recursion 递归
    private Node recurseForNode(Node node_root) {
        if (node_root.getChildren() != null) {
            for (Node child : node_root.getChildren()) {

                String course_mindmap = course_id + " " + mindmap_id;

                child.setCourse_mindmap(course_mindmap);
                // 若该节点在数据库中已经存在，则把它的所有子节点全都链接到新节点上
                String nodeId = child.getId();
                Node tempNode = nodeService.findByNodeId(course_mindmap, nodeId);
                if (tempNode != null) {
                    Long id = tempNode.getLong_id();
                    // Courseware
                    Courseware[] coursewares = nodeService.findCoursewares(id);
                    // Link
                    Link[] links = nodeService.findLinks(id);
                    // Material
                    Material[] materials = nodeService.findMaterials(id);
                    // Assignment-Multiple
                    AssignmentMultiple[] assignmentMultiples = nodeService.findAssignmentMultiple(id);
                    // Assignment-Short
                    AssignmentShort[] assignmentShorts = nodeService.findAssignmentShort(id);
                    //Assignment-Judge
                    AssignmentJudgment[] assignmentJudgments = nodeService.findAssignmentJudgements(id);
                    // delete the origin node
                    nodeService.delete(tempNode);
                    // save the new node
                    nodeService.save(child);

                    if (coursewares.length > 0) {
                        for (Courseware c : coursewares) {
                            String coursewareName = c.getCourseware_name();
                            nodeChildService.deleteCoursewareFather(coursewareName);
                            nodeChildService.createCoursewareFather(coursewareName, course_mindmap, nodeId);
                        }
                    }

                    if (links.length > 0) {
                        for (Link l : links) {
                            String linkAddress = l.getLink_address();
                            nodeChildService.deleteLinkFather(linkAddress);
                            nodeChildService.createLinkFather(linkAddress, course_mindmap, nodeId);
                        }
                    }

                    if (materials.length > 0) {
                        for (Material m : materials) {
                            String materialName = m.getMaterialName();
                            nodeChildService.deleteMaterialFather(materialName);
                            nodeChildService.createMaterialFather(materialName, course_mindmap, nodeId);
                        }
                    }

                    if (assignmentMultiples.length > 0) {
                        for (AssignmentMultiple am : assignmentMultiples) {
                            Long multiId = am.getId();
                            nodeChildService.deleteAssignmentMultiFather(multiId);
                            nodeChildService.createAssignmentMultiFather(multiId, course_mindmap, nodeId);
                        }
                    }

                    if (assignmentShorts.length > 0) {
                        for (AssignmentShort as : assignmentShorts) {
                            Long shortId = as.getId();
                            nodeChildService.deleteAssignmentShortFather(shortId);
                            nodeChildService.createAssignmentShortFather(shortId, course_mindmap, nodeId);
                        }
                    }

                    if (assignmentJudgments.length > 0) {
                        for (AssignmentJudgment aj : assignmentJudgments) {
                            Long ajId = aj.getId();
                            nodeChildService.deleteAssignmentShortFather(ajId);
                            nodeChildService.createAssignmentShortFather(ajId, course_mindmap, nodeId);
                        }
                    }

                }
                child = recurseForNode(child);
            }
        }
        return node_root;
    }

    private void deleteChildren(Node node_root) {
        if (node_root.getChildren() != null) {
            for (Node child : node_root.getChildren()) {
                nodeService.delete(child);
                deleteChildren(child);
            }
        }
    }

    private void changeMindmapNodeToGraphNode(MindmapData currentNode, String parentID, ArrayList<Map<String, Object>> graph) {
        // MindmapData currentNode = mindmap.getData();
        // ArrayList<Map<String, Object>> graph = new ArrayList<>();

        String id = currentNode.getId();
        String name = currentNode.getTopic();
        int weight = 50;
        int labelSize = weight / 4;
        int width = labelSize * name.length() + 60;
        GraphNode node = new GraphNode(id, name, weight, width, labelSize, parentID);

        Map<String, Object> nodeStructure = new HashMap<>();
        nodeStructure.put("group", "nodes");
        nodeStructure.put("data", node);
        graph.add(nodeStructure);

        if (currentNode.getChildren() == null) {
            return;
        }

        for (int i = 0; i < currentNode.getChildren().size(); i++) {
            MindmapData currentChild = currentNode.getChildren().get(i);
            changeMindmapNodeToGraphNode(currentChild, currentNode.getId(), graph);

            GraphEdge graphEdge = new GraphEdge(currentNode.getId() + currentChild.getId(), currentNode.getId(), currentChild.getId(), "pre-suc");

            Map<String, Object> edgeStructure = new HashMap<>();
            edgeStructure.put("group", "edges");
            edgeStructure.put("data", graphEdge);
            graph.add(edgeStructure);
        }
    }

    private Graph mindmapToGraph(MindmapJson mindmapJson) {
        ArrayList<Map<String, Object>> graphData = new ArrayList<>();
        changeMindmapNodeToGraphNode(mindmapJson.getData(), "", graphData);
        return new Graph(mindmapJson.getMeta(), mindmapJson.getFormat(), graphData);
    }
}
