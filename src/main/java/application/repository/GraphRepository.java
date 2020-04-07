package application.repository;

import application.controller.json_model.Graph_json;
import application.model.Graph;
import application.model.GraphNode;
import application.model.Mindmap;
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
}
