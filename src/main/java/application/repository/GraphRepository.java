package application.repository;

import application.controller.json_model.Graph_json;
import application.model.Graph;
import application.model.Mindmap;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface GraphRepository extends Neo4jRepository<Graph, Long> {
    @Query("MATCH (n:Graph) WHERE  n.graph_id = ({graph_id}) RETURN n")
    Graph findByGraph_id(@Param("graph_id") String graph_id);

    @Query("MATCH (graph:Graph)-[r:HAS_NODE]-(node:GraphNode) WHERE graph.graph_id = ({graph_id}) detach delete graph,node")
    void deleteGraphByGraph_id(@Param("graph_id") String graph_id);
}
