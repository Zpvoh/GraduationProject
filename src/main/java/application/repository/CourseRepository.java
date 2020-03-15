package application.repository;

import application.model.*;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface CourseRepository extends Neo4jRepository<Course, Long>{
    @Query("MATCH (n:Course) WHERE n.course_id = ({course_id}) RETURN n")
    Course findByCourseId(@Param("course_id") String course_id);

    @Query("start course = node({id}) match (course)-[:OWN]->(mindmaps:Mindmap) return mindmaps")
    Mindmap[] findMindmaps(@Param("id") long id);

    @Query("start course = node({id}) match (course)-[:OWN]->(graphs:Graph) return graphs")
    Graph[] findGraphs(@Param("id") long id);

    @Query("Match (n:Course) RETURN n")
    Course[] findAllCourses();

//    @Query("MATCH (n:Course) WHERE n.course_id = ({course_id}) " +
//            "MATCH (m:Mindmap) WHERE m.mindmap_id = ({mindmap_id})" +
//            "CREATE (n)-[:OWN]->(m)")
//    void saveOwn(@Param("course_id") String course_id, @Param("mindmap_id") String mindmap_id);

    @Query("match (s:Student) - [rs:STUDY_IN] - (c:Course) where id(c)={0} delete rs")
    void quitCourse(long id);

    @Query("match (t:Teacher) - [rs:TEACH_IN] - (c:Course) - [*] -(n) where id(c) = {0} detach delete c,n")
    void deleteCourse(long id);

    Course findBySelectCode(String selectCode);
}
