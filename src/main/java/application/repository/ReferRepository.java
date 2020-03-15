package application.repository;

import application.model.ReferRelationship;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ReferRepository extends Neo4jRepository<ReferRelationship, Long> {
}
