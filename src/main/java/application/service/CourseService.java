package application.service;

import application.model.*;
import application.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentAnswerRepository studentAnswerRepository;
    @Autowired
    private MindmapRepository mindmapRepository;
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;

    private final String verifyCodes = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
    private final int  verifySize = 6;

    public boolean selectCourse(String user_name, String selectCode, Course course_json) {

        String course_id = course_json.getCourse_id();

        //找到course
        Course course = findByCourseId(course_id);
        if (course == null)
            return false;

        //找到student
        Student student = studentRepository.findByName(user_name);
        if (student == null) {
            return false;
        }

        //...首先判断这个学生是否已经选了这门课

        Boolean ifChosen =false;
        Course[] studentCourses = studentRepository.findCourses(student.getId());
        for (Course studentCourse: studentCourses){
            if(studentCourse.getCourse_id().equals(course_id)){
                ifChosen=true;
                break;
            }
        }

        //验证选课码
        if (!course.getSelectCode().equals(selectCode))
            return false;

        //学生还未选这门课
        if (!ifChosen) {
            //course的选课人数加1
            int number_before = Integer.parseInt(course.getCourse_number());
            course.setCourse_number((number_before + 1) + "");
            courseRepository.save(course);

            Course course_in_db = courseRepository.findByCourseId(course_id);
            //再创建course和student的关系
            student.studyIn(course_in_db);
            studentRepository.save(student);
        }

        return true;
    }

    public boolean openCourse(String user_name, Course course) {
        String course_id = course.getCourse_id();

        //首先判断course_id是否已经存在
        Course course_db = courseRepository.findByCourseId(course_id);
        if (course_db != null) {
            return false;
        }

        Teacher teacher = teacherRepository.findByName(user_name);
        if (teacher == null){
            return false;
        }

        //创建新的course
        course.setCourse_number("0");
        course.setSelectCode(generateSelectCode());

        courseRepository.save(course);

        Course course_in_db = courseRepository.findByCourseId(course_id);
        //再创建course和teacher的关系
        teacher.teachIn(course_in_db);
        teacherRepository.save(teacher);
        return true;
    }

    public boolean deleteCourse(String user_name, String course_id, String course_name) {
        // 首先检查该用户有无权限
        Teacher teacher = teacherRepository.findByName(user_name);
        Course[] courses = teacherRepository.findCourses(teacher.getId());
        boolean flag = false;
        for (Course course: courses) {
            if (course.getCourse_id().equals(course_id)) {
                flag = true;
                break;
            }
        }
        if (!flag) //flag 是false表示这个用户没有开这门课
            return false;



        Course target = courseRepository.findByCourseId(course_id);

        if (!target.getCourse_name().equals(course_name))
            return false;

        courseRepository.quitCourse(target.getId());

        deleteStudentAnsByCourseId(course_id);

        Mindmap[] mindmaps = courseRepository.findMindmaps(target.getId());
        if (mindmaps.length > 0)
            courseRepository.deleteCourse(target.getId());
        else
            courseRepository.delete(target);

        return true;
    }

    public Course[] student_courses(String user_name) {

        Student student = studentRepository.findByName(user_name);
        if (student == null) {
            return null;
        }

        //得到课程列表
        return studentRepository.findCourses(student.getId());
    }

    public void deleteStudentAnsByCourseId(String course_id) {
        Course course = courseRepository.findByCourseId(course_id);
        Mindmap[] mindmaps = courseRepository.findMindmaps(course.getId());

        for (Mindmap mindmap: mindmaps) {
            deleteStudentAnsByMindmapId(mindmap.getMindmap_id());
        }
    }

    public void deleteStudentAnsByMindmapId(String mindmap_id) {
        //获得mindmap
        Mindmap tempMindmap = mindmapRepository.findByMindmap_id(mindmap_id);
        Node root_node = mindmapRepository.findRootNode(tempMindmap.getId());

        //深度遍历
        Queue<Node> nodes = new LinkedList<>();
        Queue<Node> nodesChildren = new LinkedList<>();
        nodes.add(root_node);

        while (!nodes.isEmpty() || !nodesChildren.isEmpty()) {

            if (nodes.isEmpty()) {
                nodes = nodesChildren;
                nodesChildren = new LinkedList<>();
            }
            Node thisNode = nodes.peek();

            // 选择题删除答案
            AssignmentMultiple[] multiples = nodeRepository.findAssignmentMultiple(thisNode.getLong_id());
            for (AssignmentMultiple mul : multiples) {
                String mul_id =mul.getMulti_id() + mul.getId();
                deleteStudentAns(mul_id);
            }

            // 判断题删除答案
            AssignmentJudgment[] judgments = nodeRepository.findAssignmentJudgments(thisNode.getLong_id());
            for (AssignmentJudgment judgment : judgments) {
                String jud_id =judgment.getJudge_id() + judgment.getId();
                deleteStudentAns(jud_id);
            }

            // 简答题删除答案
            AssignmentShort[] shorts = nodeRepository.findAssignmentShort(thisNode.getLong_id());
            for (AssignmentShort assignmentShort: shorts) {
                String sho_id = assignmentShort.getShort_id() + assignmentShort.getId();
                deleteStudentAns(sho_id);
            }

            Collections.addAll(nodesChildren, nodeRepository.findChildren(thisNode.getLong_id()));
            nodes.remove();
        }
    }

    public void deleteStudentAns(String assignmentId) {
        List<StudentAnswer> studentAnswerList = studentAnswerRepository.findByAssignmentId(assignmentId);
       studentAnswerRepository.deleteAll(studentAnswerList);
    }

    public Course findByCourseId(String courseId) {
        return courseRepository.findByCourseId(courseId);
    }


    public Course[] findCourses() {
        return courseRepository.findAllCourses();
    }

    public Mindmap[] findMindmaps(long id) {
        return courseRepository.findMindmaps(id);
    }

    public Graph[] findGraphs(long id) {return courseRepository.findGraphs(id);}

    public void save(Course course) {
        courseRepository.save(course);
    }


    private  String generateSelectCode() {

        boolean flag = true;
        String code = "";
        while (flag) {
            int codesLen = verifyCodes.length();
            Random rand = new Random(System.currentTimeMillis());

            try {
                Thread.sleep(1);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            StringBuilder verifyCode = new StringBuilder(verifySize);
            for (int i = 0; i < verifySize; i++) {
                verifyCode.append(verifyCodes.charAt(rand.nextInt(codesLen - 1)));
            }
            code = verifyCode.toString();
            if (courseRepository.findBySelectCode(code) == null)
                flag = false;
        }
        return code;
    }
}
