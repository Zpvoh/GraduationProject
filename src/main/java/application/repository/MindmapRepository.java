package application.repository;

import application.model.*;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface MindmapRepository extends Neo4jRepository<Mindmap, Long> {
    @Query("MATCH (n:Mindmap) WHERE  n.mindmap_id = ({mindmap_id}) RETURN n")
    Mindmap findByMindmap_id(@Param("mindmap_id") String mindmap_id);

    @Query("start mindmap = node({id}) match (mindmap)-[:HAS_ROOT]->(rootNode) return rootNode")
    Node findRootNode(@Param("id") long id);

    @Query("match (c:Course) - [ro:OWN] - (m:Mindmap) - [*] -(n) where m.mindmap_id = {0} detach delete m,n")
    void deleteMindmap(String mindmap_id);

    @Query("match (t:Teacher) - [rt:TEACH_IN] - (c:Course) - [ro:OWN] - (m:Mindmap) " +
            "where m.mindmap_id = {0} and t.name = {1} return m ")
    Mindmap hasAuthToDeleteMap(String mindmapId, String username);

    @Query("match (m:Mindmap) where m.mindmap_name = {0} return m")
    Mindmap findByMindmap_name(String mindmap_name);

    @Query("Match (m:Mindmap) RETURN m")
    Mindmap[] findAllMindMap();

    @Query("MATCH (c:Course)-[r:OWN]-(n:Mindmap) WHERE n.mindmap_id={0} RETURN c")
    Course findCourseOfMindmap(String mindmap_id);
}
