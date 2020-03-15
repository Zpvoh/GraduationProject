package application.model;

import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type="REFER_TO")
public class ReferRelationship {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private GraphNode source;

    @EndNode
    private GraphNode target;

    @Property
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GraphNode getSource() {
        return source;
    }

    public void setSource(GraphNode source) {
        this.source = source;
    }

    public GraphNode getTarget() {
        return target;
    }

    public void setTarget(GraphNode target) {
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
