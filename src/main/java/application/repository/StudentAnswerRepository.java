package application.repository;

import application.model.StudentAnswer;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentAnswerRepository extends CrudRepository<StudentAnswer, Long> {

    StudentAnswer findByStudentNameAndAndAssignmentId(String name, String id);

    List<StudentAnswer> findByAssignmentId(String id);

    StudentAnswer findByStudentIdAndAndAssignmentLongId(long sid, long aid);

    List<StudentAnswer> findByAssignmentLongId(long aid);

}
