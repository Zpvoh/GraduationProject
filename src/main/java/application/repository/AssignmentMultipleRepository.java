package application.repository;

import application.model.AssignmentJudgment;
import application.model.AssignmentMultiple;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AssignmentMultipleRepository extends Neo4jRepository<AssignmentMultiple, Long> {
    @Query("MATCH (n:Assignment_multiple) WHERE n.multi_id = ($multi_id) RETURN n")
    List<AssignmentMultiple> findByMultiId(@Param("multi_id") String multi_id);

    @Query("start  n = node($id) " +
            "MATCH (:Node)-[r:HAS_ASSIGNMENT_MULTI]->(n) DELETE r")
    void deleteFather(@Param("id") Long id);

    @Query("start  n = node($id) " +
            "MATCH (m:Node) WHERE m.course_mindmap = ($course_mindmap) and m.node_id=($node_id) " +
            "CREATE (m)-[:HAS_ASSIGNMENT_MULTI]->(n)")
    void createFather(@Param("id") Long id, @Param("course_mindmap") String course_mindmap, @Param("node_id") String node_id);

    @Query("MATCH (a:Assignment_multiple) where ID(a) = {0} RETURN a")
    AssignmentMultiple getAssignmentMultipleById(Long id);

    @Query("MATCH (a:Assignment_multiple) where ID(a) = {0} DETACH DELETE a")
    void deleteAssignmentMultipleById(Long id);

    @Query("MATCH (n:Assignment_multiple) RETURN n ")
    List<AssignmentMultiple> getAll();
}
