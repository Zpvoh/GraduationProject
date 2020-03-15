package application.model;

import application.controller.json_model.GraphNode_json;
import org.neo4j.ogm.annotation.*;

import java.util.HashSet;
import java.util.Set;

@NodeEntity(label = "GraphNode")
public class GraphNode {
    public GraphNode() {
        this.successors = new HashSet<>();
        this.synonyms = new HashSet<>();
        this.antonyms = new HashSet<>();
    }

    public GraphNode(String id, String name, int weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.successors = new HashSet<>();
        this.synonyms = new HashSet<>();
        this.antonyms = new HashSet<>();
    }

    public GraphNode(String id, String name, int weight, Set<Material> materials, Set<Courseware> coursewares, Set<Link> links, Set<AssignmentMultiple> assignmentMultiples, Set<AssignmentJudgment> assignmentJudgments, Set<AssignmentShort> assignmentShorts, Set<Note> notes) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.materials = materials;
        this.coursewares = coursewares;
        this.links = links;
        this.assignmentMultiples = assignmentMultiples;
        this.assignmentJudgments = assignmentJudgments;
        this.assignmentShorts = assignmentShorts;
        this.notes = notes;
        this.successors = new HashSet<>();
        this.synonyms = new HashSet<>();
        this.antonyms = new HashSet<>();
    }

    @Id
    @GeneratedValue
    private Long long_id;

    @Property(name = "node_id")
    private String id;

    private String name;
    private int weight;

    @Relationship(type="HAS_SUCCESSOR")
    private Set<GraphNode> successors;

    @Relationship(type="IS_SYNONYM", direction = Relationship.UNDIRECTED)
    private Set<GraphNode> synonyms;

    @Relationship(type="IS_ANTONYM", direction = Relationship.UNDIRECTED)
    private Set<GraphNode> antonyms;

    @Relationship(type = "HAS_MATERIAL")
    private Set<Material> materials;
    @Relationship(type = "HAS_COURSEWARE")
    private Set<Courseware> coursewares;
    @Relationship(type = "HAS_LINK")
    private Set<Link> links;
    @Relationship(type = "HAS_ASSIGNMENT_MULTI")
    private Set<AssignmentMultiple> assignmentMultiples;
    @Relationship(type = "HAS_ASSIGNMENT_JUDGMENT")
    private Set<AssignmentJudgment> assignmentJudgments;
    @Relationship(type = "HAS_ASSIGNMENT_SHORT")
    private Set<AssignmentShort> assignmentShorts;

    @Relationship(type = "HAS_NOTE")
    private Set<Note> notes;

    public Set<Note> getNotes() {
        return notes;
    }

    public void setNotes(Set<Note> notes) {
        this.notes = notes;
    }

    public void saveNote(Note note) {
        if (notes == null) {
            notes = new HashSet<>();
        }
        notes.add(note);
    }


    public Set<Material> getMaterials() {
        return materials;
    }

    public void setMaterial(Material material) {
        if (materials == null) {
            materials = new HashSet<>();
        }
        materials.add(material);
    }

    public Set<Courseware> getCoursewares() {
        return coursewares;
    }

    public void addCourseware(Courseware courseware) {
        if (coursewares == null) {
            coursewares = new HashSet<>();
        }
        coursewares.add(courseware);
    }

    public Set<Link> getLinks() {
        return links;
    }

    public void setLink(Link link) {
        if (links == null) {
            links = new HashSet<>();
        }
        links.add(link);
    }

    public Set<AssignmentMultiple> getAssignmentMultiples() {
        return assignmentMultiples;
    }

    public void setAssignmentMultiple(AssignmentMultiple assignmentMultiple) {
        if (assignmentMultiples == null) {
            assignmentMultiples = new HashSet<>();
        }
        assignmentMultiples.add(assignmentMultiple);
    }

    public Set<AssignmentShort> getAssignmentShorts() {
        return assignmentShorts;
    }

    public void setAssignmentShorts(AssignmentShort assignmentShort) {
        if (assignmentShorts == null) {
            assignmentShorts = new HashSet<>();
        }
        assignmentShorts.add(assignmentShort);
    }

    public void setAssignmentJudgments(AssignmentJudgment judgment) {
        if (assignmentJudgments == null)
            assignmentJudgments = new HashSet<>();
        assignmentJudgments.add(judgment);
    }

    public Set<AssignmentJudgment> getAssignmentJudgments() {
        return assignmentJudgments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Set<GraphNode> getSuccessors() {
        return successors;
    }

    public void setSuccessors(Set<GraphNode> successors) {
        this.successors = successors;
    }

    public Set<GraphNode> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<GraphNode> synonyms) {
        this.synonyms = synonyms;
    }

    public Set<GraphNode> getAntonyms() {
        return antonyms;
    }

    public void setAntonyms(Set<GraphNode> antonyms) {
        this.antonyms = antonyms;
    }

    public Long getLong_id() {
        return long_id;
    }

    public void setLong_id(Long long_id) {
        this.long_id = long_id;
    }

    public GraphNode_json toJsonObject(){
        String id = this.id;
        String name = this.name;
        int weight = this.weight;
        int labelSize = weight / 4;
        int width = labelSize * name.length() + 60;
        return new GraphNode_json(id, name, weight, width, labelSize, "");
    }
}
