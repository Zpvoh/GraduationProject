package application.model;

import org.neo4j.ogm.annotation.*;

import java.util.HashSet;
import java.util.Set;

@NodeEntity(label = "Course")
public class Course {
    @Id
    @GeneratedValue
    private Long id;
    private String course_id;
    private String course_name;
    private String course_number;
    private String selectCode;

    @Relationship(type = "OWN")
    private Set<Mindmap> mindmaps;

    @Relationship(type = "OWN")
    private Set<Graph> graphs;

    public Set<Mindmap> getMindmaps() {
        return mindmaps;
    }

    public void owns(Mindmap mindmap) {
        if (mindmaps == null) {
            mindmaps = new HashSet<>();
        }
        mindmaps.add(mindmap);
    }

    public void owns(Graph graph) {
        if (graphs == null) {
            graphs = new HashSet<>();
        }
        graphs.add(graph);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getCourse_number() {
        return course_number;
    }

    public void setCourse_number(String course_number) {
        this.course_number = course_number;
    }

    public String getSelectCode() {
        return selectCode;
    }

    public void setSelectCode(String selectCode) {
        this.selectCode = selectCode;
    }

    public Set<Graph> getGraphs() {
        return graphs;
    }

    public void setGraphs(Set<Graph> graphs) {
        this.graphs = graphs;
    }
}
