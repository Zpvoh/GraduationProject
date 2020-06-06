package application.repository;

import application.controller.json_model.Graph_json;
import application.model.Graph;
import application.model.GraphNode;
import application.model.Mindmap;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface GraphRepository extends Neo4jRepository<Graph, Long> {
    @Query("MATCH (n:Graph) WHERE  n.graph_id = ({graph_id}) RETURN n")
    Graph findByGraph_id(@Param("graph_id") String graph_id);

    @Query("match (g:Graph) - [:HAS_NODE] -(n) where g.graph_id = ({graph_id}) detach delete g,n")
    void deleteGraphByGraph_id(@Param("graph_id") String graph_id);

    @Query("match (g:Graph) where g.graph_id = ({graph_id}) detach delete g")
    void deleteGraphWithoutNodeByGraph_id(@Param("graph_id") String graph_id);

    @Query("match (g:Graph) - [:HAS_NODE] -(n) where g.graph_id = ({graph_id}) return n")
    List<GraphNode> getGraphNodeByGraph_id(@Param("graph_id") String graph_id);

    @Query("MATCH (g:Graph)-[:HAS_NODE]-(x:GraphNode)-[:IS_ANTONYM*2]-(y:GraphNode) " +
            "where g.graph_id=({graph_id}) " +
            "and not exists((x)-[:IS_SYNONYM]->(y)) " +
            "and not exists((y)-[:IS_SYNONYM]->(x)) " +
            "WITH x,y CREATE (x)-[:IS_SYNONYM]->(y)")
    void reasonSynonymWithAntonym(@Param("graph_id") String graph_id);

    @Query("match (g:Graph) - [:HAS_NODE] - (n:GraphNode) " +
            "where g.graph_id = ({graph_id}) " +
            "with n " +
            "match (n) - [:HAS_SUCCESSOR|INCLUDE] -> (child:GraphNode) " +
            "return ID(n) as node_id, count(child) as outDegree")
    LinkedTreeMap[] getAllOutDegreeByGraph_id(@Param("graph_id") String graph_id);
}
