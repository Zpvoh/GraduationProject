package application.repository;

import application.model.*;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface GraphNodeRepository extends Neo4jRepository<GraphNode, Long> {
    @Query("MATCH (n:GraphNode) where ID(n)={0} return n")
    GraphNode findByNodeLongId(Long id);

    @Query("MATCH (c:Course)-[r1:OWN]-(g:Graph)-[r2:HAS_NODE]-(n:GraphNode) WHERE c.course_id ={course_id} and g.graph_id={graph_id} and n.node_id={node_id} RETURN n")
    GraphNode findByNodeId(@Param("course_id") String course_id, @Param("graph_id") String graph_id, @Param("node_id") String node_id);

    @Query("match (n:GraphNode) - [:HAS_COURSEWARE] - (c:Courseware) where id(n) = {0} return c")
    Courseware[] findCoursewares(long id);

    @Query("match (n:GraphNode) - [:HAS_COURSEWARE] - (c:Courseware) where id(n) = {0} return count(*)")
    int getCountOfCoursewares(long id);

    @Query("match (n:GraphNode) - [:HAS_LINK] - (l:Link) where id(n) = {0} return l")
    Link[] findLinks(long id);

    @Query("match (n:GraphNode) - [:HAS_MATERIAL] - (m:Material) where id(n) = {0} return m")
    Material[] findMaterials(long id);

    @Query("match (n:GraphNode) - [:HAS_MATERIAL] - (m:Material) where id(n) = {0} return count(*)")
    int getCountOfMaterials(long id);

    @Query("match (n:GraphNode) - [:HAS_ASSIGNMENT_MULTI] - (ass_multi:Assignment_multiple) where id(n) = {0} return ass_multi")
    AssignmentMultiple[] findAssignmentMultiple(long id);

    @Query("match (n:GraphNode) - [:HAS_ASSIGNMENT_MULTI] - (ass_multi:Assignment_multiple) where id(n) = {0} return count(*)")
    int getCountOfAssignmentMultiple(long id);

    @Query("match (n:GraphNode) - [h:HAS_ASSIGNMENT_JUDGMENT] - (j:Assignment_judgment) where ID(n) = {0} return j")
    AssignmentJudgment[] findAssignmentJudgments(long id);

    @Query("match (n:GraphNode) - [h:HAS_ASSIGNMENT_JUDGMENT] - (j:Assignment_judgment) where ID(n) = {0} return count(*)")
    int getCountOfAssignmentJudgments(long id);

    @Query("match (n:GraphNode) - [:HAS_ASSIGNMENT_SHORT] - (s:Assignment_short) where ID(n) = {0} return s")
    AssignmentShort[] findAssignmentShort(long id);

    @Query("match (n:GraphNode) - [:HAS_ASSIGNMENT_SHORT] - (s:Assignment_short) where ID(n) = {0} return count(*)")
    int getCountOfAssignmentShort(long id);

    @Query("match (n:GraphNode) - [:INCLUDE] - (children:GraphNode) where ID(n) = {0} return children")
    GraphNode[] findChildren(long id);

    @Query("match (n:GraphNode) - [:HAS_NOTE] - (note) where ID(n) = {0} return note")
    Note[] findNotes(long id);
}
