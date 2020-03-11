package application.repository;

import application.model.AssignmentShort;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AssignmentShortRepository extends Neo4jRepository<AssignmentShort, Long> {

    @Query("MATCH (n:Assignment_short) WHERE n.short_id = ({short_id}) RETURN n")
    List<AssignmentShort> findByShortId(@Param("short_id") String short_id);

    @Query("start n = node({id}) " +
            "MATCH (:Node)-[r:HAS_ASSIGNMENT_SHORT]->(n) DELETE r")
    void deleteFather(@Param("id") Long id);

    @Query("start n = node({id}) " +
            "MATCH (m:Node) WHERE m.course_mindmap = ({course_mindmap}) and m.node_id=({node_id}) " +
            "CREATE (m)-[:HAS_ASSIGNMENT_SHORT]->(n)")
    void createFather(@Param("id") Long id, @Param("course_mindmap") String course_mindmap, @Param("node_id") String node_id);

    @Query("MATCH (a:Assignment_short) where ID(a) = {0} RETURN a")
    AssignmentShort getAssignmentShortById(Long id);

    @Query("MATCH (n:Assignment_short) RETURN n ")
    List<AssignmentShort> getAll();

    @Query("MATCH (n:Node)-[r:HAS_ASSIGNMENT_SHORT]-(s:Assignment_short)" +
            " where ID(n) = {0} return s")
    List<AssignmentShort> getAssignmentShortsByNodeId(long nid);

    @Query("MATCH (n:StudentAnswers) WHERE n.assignmentLongId=($assignmentLongId) AND n.studentId=($studentId) SET n.score=($score)")
    void setAnswerScore(@Param("assignmentLongId") long assignmentLongId, @Param("studentId") long studentId, @Param("score") int score);
}
