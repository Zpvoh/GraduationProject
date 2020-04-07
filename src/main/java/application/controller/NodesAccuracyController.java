package application.controller;

import application.controller.json_model.NodeValue;
import application.controller.json_model.NodesAccuracy;
import application.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
public class NodesAccuracyController {
    @Autowired
    private MindmapService mindmapService;
    @Autowired
    private GraphNodeService graphNodeService;
    @Autowired
    private NodeChildService nodeChildService;

    @RequestMapping(value = "/nodes_accuracy_mul/{mindmap_id}", method = RequestMethod.GET)
    public List<NodesAccuracy> nodesAccuracyOfMul(@PathVariable String mindmap_id) {
        return nodeChildService.getAccuracy(mindmap_id, "Multiple");
    }

    @RequestMapping(value = "/nodes_accuracy_jud/{mindmap_id}", method = RequestMethod.GET)
    public List<NodesAccuracy> nodesAccuracyOfJud(@PathVariable String mindmap_id) {
        return nodeChildService.getAccuracy(mindmap_id, "Judgment");
    }

    @RequestMapping(value = "/nodes_value/{mindmap_id}", method = RequestMethod.GET)
    public List<NodeValue> nodesValue(@PathVariable String mindmap_id) {
        return nodeChildService.getNodeValue(mindmap_id);
    }

    @RequestMapping(value = "/nodes_value_student/{mindmap_id}/{student_id}", method = RequestMethod.GET)
    public List<NodeValue> nodesValueForStudent(@PathVariable String mindmap_id, @PathVariable long student_id) {
        return nodeChildService.getNodeValueForStudent(mindmap_id, student_id);
    }

}
