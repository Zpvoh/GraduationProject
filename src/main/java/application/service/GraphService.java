package application.service;

import application.model.Graph;
import application.model.GraphNode;
import application.model.ReferRelationship;
import application.repository.GraphNodeRepository;
import application.repository.GraphRepository;
import application.repository.NodeRepository;
import application.repository.ReferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
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

    public void deleteGraphById(String id) {
        graphRepository.deleteGraphByGraph_id(id);
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
}
