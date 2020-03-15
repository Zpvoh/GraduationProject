package application.repository;

import application.model.GraphNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface GraphNodeRepository extends Neo4jRepository<GraphNode, Long> {

}
