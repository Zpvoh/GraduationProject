package application.repository;

import application.model.*;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface StudentRepository extends Neo4jRepository<Student, Long> {

    @Query("MATCH (n:Student) WHERE n.name = ($name) RETURN n")
    Student findByName(@Param("name") String name);

    @Query("start student = node($student_id) match (student)-[:STUDY_IN]->(courses) return courses")
    Course[] findCourses(@Param("student_id") long student_id);

    @Query("start student = node($student_id) match (student)-[:WRITE]->(notes) return notes")
    Note[] findNotes(@Param("student_id") long student_id);

    @Query("MATCH (s:Student) - [i:STUDY_IN] -> (c:Course)" +
            " WHERE c.course_id = {0} RETURN s.name AS name, ID(s) AS sid")
    Iterable<Map<String, Object>> findStudentsToCourse(String course_id);
}

