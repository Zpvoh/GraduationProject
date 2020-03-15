package application.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity(label = "Graph")
public class Graph {
    public Graph() {
        this.graphNodes=new HashSet<>();
    }

    @Id
    @GeneratedValue
    private Long id;

    private String graph_name;
    private String graph_id;
    private String json_string;

    @Relationship(type = "HAS_NODE")
    private Set<GraphNode> graphNodes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGraph_name() {
        return graph_name;
    }

    public void setGraph_name(String graph_name) {
        this.graph_name = graph_name;
    }

    public String getGraph_id() {
        return graph_id;
    }

    public void setGraph_id(String graph_id) {
        this.graph_id = graph_id;
    }

    public String getJson_string() {
        return json_string;
    }

    public void setJson_string(String json_string) {
        this.json_string = json_string;
    }

    public Set<GraphNode> getGraphNodes() {
        return graphNodes;
    }

    public void setGraphNodes(Set<GraphNode> graphNodes) {
        this.graphNodes = graphNodes;
    }
}
