package application.controller;

import application.controller.json_model.*;
import application.model.*;
import application.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@CrossOrigin
public class AssignmentController {
    @Autowired
    private NodeService nodeService;
    @Autowired
    private NodeChildService nodeChildService;
    @Autowired
    private UserService userService;

    // 获取简答题列表
    @RequestMapping(value = "/shorts/{course_id}/{mindmap_id}/{node_id}", method = RequestMethod.GET)
    public List<AssignmentShort_json> shorts(
            @PathVariable String course_id, @PathVariable String mindmap_id, @PathVariable String node_id) {

        String shortId = course_id + " " + mindmap_id + " " + node_id;
        List<AssignmentShort> shorts = nodeChildService.findShorts(shortId);

        List<AssignmentShort_json> short_jsons = new LinkedList<>();

        for (AssignmentShort assignment_short : shorts) {
            AssignmentShort_json short_json = new AssignmentShort_json();
            short_json.setAssignmentLongId(assignment_short.getId());
            short_json.setTitle(assignment_short.getTitle());
            short_json.setCorrect_answer(assignment_short.getCorrect_answer());

            List<StudentAnswer> studentAnswers = nodeChildService.getStudentAns(assignment_short.getId());
            short_json.setStudentAnswers(studentAnswers);

            short_jsons.add(short_json);
        }
        return short_jsons;
    }

    // 改 - 获取简答题列表
//    @RequestMapping(value = "/shorts/{node_id}", method = RequestMethod.GET)
//    public List<AssignmentShort_json> shorts2(@PathVariable long node_id) {
//
//        List<AssignmentShort> shorts = nodeChildService.getAssignmentShortsByNodeId(node_id);
//
//        List<AssignmentShort_json> short_jsons = new LinkedList<>();
//
//        for (AssignmentShort assignment_short :shorts){
//            AssignmentShort_json short_json = new AssignmentShort_json();
//            short_json.setAssignmentLongId(assignment_short.getId());
//            short_json.setTitle(assignment_short.getTitle());
//            short_json.setCorrect_answer(assignment_short.getCorrect_answer());
//
//            List<StudentAnswer> studentAnswers = nodeChildService.getStudentAns(assignment_short.getId());
//            short_json.setStudentAnswers(studentAnswers);
//
//            short_jsons.add(short_json);
//        }
//        return short_jsons;
//    }

    // 学生回答简答题
    @RequestMapping(value = "/answer_short/{course_id}/{mindmap_id}/{node_id}/{student_name}", method = RequestMethod.POST)
    public Success answer_short(@PathVariable String course_id, @PathVariable String mindmap_id, @PathVariable String node_id, @PathVariable String student_name,
                                @RequestBody StudentAnswers stu_ans) {
        Success s = new Success();
        s.setSuccess(false);

        //找到short
        String shortId = course_id + " " + mindmap_id + " " + node_id;
        List<AssignmentShort> shorts = nodeChildService.findShorts(shortId);

        AssignmentShort short_result = null;
        for (AssignmentShort assignmentShort : shorts) {
            if (assignmentShort.getTitle().equals(stu_ans.getTitle())) {
                short_result = assignmentShort;
                break;
            }
        }
        StudentAnswer studentAnswer;
        Student student = userService.findStudentByName(student_name);
        //比对答案
        if (short_result != null) { //找到题目
            studentAnswer = nodeChildService.getStudentAns(student.getId(), short_result.getId());
            if (studentAnswer == null) { //该学生之前没有回答过这个问题,需要初始化
                studentAnswer = new StudentAnswer();
                studentAnswer.setStudentName(student_name);
                studentAnswer.setStudentId(student.getId());
                studentAnswer.setAssignmentId(shortId + short_result.getId());
                studentAnswer.setAssignmentLongId(short_result.getId());
            }
            studentAnswer.setScore(0);
            studentAnswer.setAnswer(stu_ans.getAnswer());
            nodeChildService.addStudentAnswer(studentAnswer);
            s.setSuccess(true);
        }
        return s;
    }

    // 所有学生对于某道简答题的回答
    @RequestMapping(value = "/student_answer_short/{course_id}/{mindmap_id}/{node_id}/{short_title}", method = RequestMethod.GET)
    public List<StudentAnswer> student_answer_short(@PathVariable String course_id, @PathVariable String mindmap_id, @PathVariable String node_id,
                                                    @PathVariable String short_title) {

        //找到short
        String shortId = course_id + " " + mindmap_id + " " + node_id;
        List<AssignmentShort> shorts = nodeChildService.findShorts(shortId);

        AssignmentShort short_result = null;
        for (AssignmentShort assignmentShort : shorts) {
            if (assignmentShort.getTitle().equals(short_title)) {
                short_result = assignmentShort;
                break;
            }
        }
        return nodeChildService.getStudentAns(short_result.getId());
    }

    // 学生回答选择题
    @RequestMapping(value = "/answer_multiple/{course_id}/{mindmap_id}/{node_id}/{student_name}", method = RequestMethod.POST)
    public Success answer_multiple(@PathVariable String course_id, @PathVariable String mindmap_id, @PathVariable String node_id, @PathVariable String student_name,
                                   @RequestBody StudentAnswers stu_ans) {
        Success s = new Success();
        s.setSuccess(false);

        //找到multiple
        String multiId = course_id + " " + mindmap_id + " " + node_id;
        List<AssignmentMultiple> multiples = nodeChildService.findMultis(multiId);

        AssignmentMultiple multiple_result = null;
        for (AssignmentMultiple multiple : multiples) {
            if (multiple.getTitle().equals(stu_ans.getTitle())) {
                multiple_result = multiple;
                break;
            }
        }
        StudentAnswer studentAnswer;
        Student student = userService.findStudentByName(student_name);

        //比对答案
        if (multiple_result != null) { //找到题目
            int number_before = Integer.parseInt(multiple_result.getNumber());
            int correct_number_before = Integer.parseInt(multiple_result.getCorrect_number());

            studentAnswer = nodeChildService.getStudentAns(student.getId(), multiple_result.getId());
            if (studentAnswer == null) { //该学生之前没有回答过这个问题
                studentAnswer = new StudentAnswer();

                studentAnswer.setStudentName(student_name);
                studentAnswer.setStudentId(student.getId());
                studentAnswer.setAssignmentId(multiId + multiple_result.getId());
                studentAnswer.setAssignmentLongId(multiple_result.getId());

                studentAnswer.setAnswer(stu_ans.getAnswer());
                if (multiple_result.getCorrect_answer().equals(stu_ans.getAnswer())) {
                    multiple_result.setCorrect_number(correct_number_before + 1 + "");
                    //List<AssignmentMultiple> multiple = nodeChildService.findMultis(studentAnswer.getAssignmentId());
                    studentAnswer.setScore(multiple_result.getValue());
                } else {
                    studentAnswer.setScore(0);
                }
                nodeChildService.addStudentAnswer(studentAnswer);
                multiple_result.setNumber((number_before + 1) + "");
            } else { //回答过
                //原先的回答错误，现在的回答正确
                boolean isOldAnswerTrue = multiple_result.getCorrect_answer().equals(studentAnswer.getAnswer());
                boolean isNewAnswerTrue = multiple_result.getCorrect_answer().equals(stu_ans.getAnswer());
                if (!isOldAnswerTrue && isNewAnswerTrue) // 前错后对 +1
                {
                    multiple_result.setCorrect_number(correct_number_before + 1 + "");
                    studentAnswer.setScore(multiple_result.getValue());

                } else if (isOldAnswerTrue && !isNewAnswerTrue) //前对后错 -1
                {
                    multiple_result.setCorrect_number(correct_number_before - 1 + "");
                    studentAnswer.setScore(0);
                }
                studentAnswer.setAnswer(stu_ans.getAnswer());
                nodeChildService.addStudentAnswer(studentAnswer);
            }

            //保存multiple
            nodeChildService.saveMulti(multiple_result);
            s.setSuccess(true);
        }
        return s;
    }

    // 学生回答判断题
    @RequestMapping(value = "/answer_judgement/{course_id}/{mindmap_id}/{node_id}/{student_name}", method = RequestMethod.POST)
    public Success answer_judgement(@PathVariable String course_id, @PathVariable String mindmap_id, @PathVariable String node_id, @PathVariable String student_name,
                                    @RequestBody StudentAnswers stu_ans) {

        Success s = new Success();
        s.setSuccess(false);

        //找到multiple
        String judgeId = course_id + " " + mindmap_id + " " + node_id;
        List<AssignmentJudgment> judgments = nodeChildService.findJudgements(judgeId);

        AssignmentJudgment judgment_result = null;
        for (AssignmentJudgment judgment : judgments) {
            if (judgment.getTitle().equals(stu_ans.getTitle())) {
                judgment_result = judgment;
                break;
            }
        }
        StudentAnswer studentAnswer;
        Student student = userService.findStudentByName(student_name);

        //比对答案
        if (judgment_result != null) { //找到题目
            int number_before = Integer.parseInt(judgment_result.getNumber());
            int correct_number_before = Integer.parseInt(judgment_result.getCorrect_number());

            studentAnswer = nodeChildService.getStudentAns(student.getId(), judgment_result.getId());
            if (studentAnswer == null) { //该学生之前没有回答过这个问题
                studentAnswer = new StudentAnswer();

                studentAnswer.setStudentName(student_name);
                studentAnswer.setStudentId(student.getId());
                studentAnswer.setAssignmentId(judgeId + judgment_result.getId());
                studentAnswer.setAssignmentLongId(judgment_result.getId());

                studentAnswer.setAnswer(stu_ans.getAnswer());
                if (judgment_result.getCorrect_answer().equals(stu_ans.getAnswer())) {
                    judgment_result.setCorrect_number(correct_number_before + 1 + "");
                    studentAnswer.setScore(judgment_result.getValue());
                }else{
                    studentAnswer.setScore(0);
                }
                nodeChildService.addStudentAnswer(studentAnswer);
                judgment_result.setNumber((number_before + 1) + "");

            } else { //回答过
                //原先的回答错误，现在的回答正确
                boolean isOldAnswerTrue = judgment_result.getCorrect_answer().equals(studentAnswer.getAnswer());
                boolean isNewAnswerTrue = judgment_result.getCorrect_answer().equals(stu_ans.getAnswer());
                if (!isOldAnswerTrue && isNewAnswerTrue) { // 前错后对 +1
                    judgment_result.setCorrect_number(correct_number_before + 1 + "");
                    studentAnswer.setScore(judgment_result.getValue());
                }
                else if (isOldAnswerTrue && !isNewAnswerTrue) { //前对后错 -1
                    judgment_result.setCorrect_number(correct_number_before - 1 + "");
                    studentAnswer.setScore(0);
                }
                studentAnswer.setAnswer(stu_ans.getAnswer());
                nodeChildService.addStudentAnswer(studentAnswer);
            }

            //保存multiple
            nodeChildService.saveJudge(judgment_result);
            s.setSuccess(true);
        }
        return s;
    }

    // 给学生的选择题列表
    @RequestMapping(value = "/multiples_student/{course_id}/{mindmap_id}/{node_id}/{student_name}", method = RequestMethod.GET)
    public List<AssignmentMultipleStudent> multiples_student(@PathVariable String course_id, @PathVariable String mindmap_id,
                                                             @PathVariable String node_id, @PathVariable String student_name) {

        String multiId = course_id + " " + mindmap_id + " " + node_id;
        List<AssignmentMultiple> multiples = nodeChildService.findMultis(multiId);

        Student student = userService.findStudentByName(student_name);

        List<AssignmentMultipleStudent> multiples_student = new LinkedList<>();
        for (AssignmentMultiple multiple : multiples) {
            AssignmentMultipleStudent multiple_student = new AssignmentMultipleStudent();

            multiple_student.setAssignmentLongId(multiple.getId());
            multiple_student.setTitle(multiple.getTitle());
            multiple_student.setOptionA(multiple.getOptionA());
            multiple_student.setOptionB(multiple.getOptionB());
            multiple_student.setOptionC(multiple.getOptionC());
            multiple_student.setOptionD(multiple.getOptionD());
            multiple_student.setFullScore(multiple.getValue());

            //
            StudentAnswer studentAnswer = nodeChildService.getStudentAns(student.getId(), multiple.getId());
            if (studentAnswer == null) {
                multiple_student.setAnswer("");
                multiple_student.setScore(0);
            }
            else {
                multiple_student.setAnswer(studentAnswer.getAnswer());
                multiple_student.setScore(studentAnswer.getScore());
            }
            multiples_student.add(multiple_student);

        }

        return multiples_student;
    }

    // 给学生的判断题列表
    @RequestMapping(value = "/judgments_student/{course_id}/{mindmap_id}/{node_id}/{student_name}", method = RequestMethod.GET)
    public List<AssignmentJudgmentStudent> judgments_student(@PathVariable String course_id, @PathVariable String mindmap_id,
                                                             @PathVariable String node_id, @PathVariable String student_name) {

        String judgeId = course_id + " " + mindmap_id + " " + node_id;
        List<AssignmentJudgment> judgments = nodeChildService.findJudgements(judgeId);

        Student student = userService.findStudentByName(student_name);

        List<AssignmentJudgmentStudent> judgments_student = new LinkedList<>();
        for (AssignmentJudgment judgment : judgments) {
            AssignmentJudgmentStudent judgment_student = new AssignmentJudgmentStudent();

            judgment_student.setAssignmentLongId(judgment.getId());
            judgment_student.setTitle(judgment.getTitle());
            judgment_student.setFullScore(judgment.getValue());

            StudentAnswer studentAnswer = nodeChildService.getStudentAns(student.getId(), judgment.getId());
            if (studentAnswer == null) {
                judgment_student.setAnswer("");
                judgment_student.setScore(0);
            }
            else {
                judgment_student.setAnswer(studentAnswer.getAnswer());
                judgment_student.setScore(studentAnswer.getScore());
            }

            judgments_student.add(judgment_student);
        }

        return judgments_student;
    }

    // 给学生的简答题列表
    @RequestMapping(value = "/shorts_student/{course_id}/{mindmap_id}/{node_id}/{student_name}", method = RequestMethod.GET)
    public List<AssignmentShortStudent> shorts_student(@PathVariable String course_id, @PathVariable String mindmap_id,
                                                       @PathVariable String node_id, @PathVariable String student_name) {

        String shortId = course_id + " " + mindmap_id + " " + node_id;
        List<AssignmentShort> shorts = nodeChildService.findShorts(shortId);

        Student student = userService.findStudentByName(student_name);

        List<AssignmentShortStudent> shorts_student = new LinkedList<>();
        for (AssignmentShort aShort : shorts) {
            AssignmentShortStudent short_student = new AssignmentShortStudent();

            short_student.setAssignmentLongId(aShort.getId());
            short_student.setTitle(aShort.getTitle());
            short_student.setFullScore(10);

            StudentAnswer studentAnswer = nodeChildService.getStudentAns(student.getId(), aShort.getId());
            if (studentAnswer == null) {
                short_student.setAnswer("");
                short_student.setScore(0);
            }
            else {
                short_student.setAnswer(studentAnswer.getAnswer());
                short_student.setScore(studentAnswer.getScore());
            }

            shorts_student.add(short_student);
        }

        return shorts_student;
    }

    // 给老师的选择题列表
    @RequestMapping(value = "/multiples_teacher/{course_id}/{mindmap_id}/{node_id}", method = RequestMethod.GET)
    public List<AssignmentMultiple_json> multiples_teacher(@PathVariable String course_id, @PathVariable String mindmap_id,
                                                           @PathVariable String node_id) {

        String multiId = course_id + " " + mindmap_id + " " + node_id;
        List<AssignmentMultiple> multiples = nodeChildService.findMultis(multiId);

        List<AssignmentMultiple_json> multiple_jsons = new LinkedList<>();
        for (AssignmentMultiple multiple : multiples) {
            AssignmentMultiple_json multiple_json = new AssignmentMultiple_json();

            multiple_json.setTitle(multiple.getTitle());
            multiple_json.setOptionA(multiple.getOptionA());
            multiple_json.setOptionB(multiple.getOptionB());
            multiple_json.setOptionC(multiple.getOptionC());
            multiple_json.setOptionD(multiple.getOptionD());
            multiple_json.setCorrect_answer(multiple.getCorrect_answer());
            multiple_json.setNumber(multiple.getNumber());
            multiple_json.setCorrect_number(multiple.getCorrect_number());
            multiple_json.setValue(multiple.getValue());
            multiple_jsons.add(multiple_json);
        }

        return multiple_jsons;
    }

    // 给老师的判断题列表
    @RequestMapping(value = "/judgments_teacher/{course_id}/{mindmap_id}/{node_id}", method = RequestMethod.GET)
    public List<AssignmentJudgment_json> judgments_teacher(@PathVariable String course_id, @PathVariable String mindmap_id,
                                                           @PathVariable String node_id) {

        String judgeId = course_id + " " + mindmap_id + " " + node_id;
        List<AssignmentJudgment> judgments = nodeChildService.findJudgements(judgeId);

        List<AssignmentJudgment_json> judgment_jsons = new LinkedList<>();
        for (AssignmentJudgment judgment : judgments) {
            AssignmentJudgment_json judgment_json = new AssignmentJudgment_json();

            judgment_json.setTitle(judgment.getTitle());
            judgment_json.setCorrect_answer(judgment.getCorrect_answer());
            judgment_json.setNumber(judgment.getNumber());
            judgment_json.setCorrect_number(judgment.getCorrect_number());
            judgment_json.setValue(judgment.getValue());
            judgment_jsons.add(judgment_json);
        }

        return judgment_jsons;
    }

    // 发布选择题
    @RequestMapping(value = "/release_multiple/{course_id}/{mindmap_id}/{node_id}", method = RequestMethod.POST)
    public Success release_multiple(@PathVariable String course_id, @PathVariable String mindmap_id,
                                    @PathVariable String node_id, @RequestBody AssignmentMultiple multiple) {
        Success success = new Success();
        success.setSuccess(false);

        //找到node
        Node result_node = nodeService.findByNodeId(course_id + " " + mindmap_id, node_id);

        //向node节点添加HAS_ASSIGNMENT_MULTI关系
        if (result_node != null) {

            //向节点里增加multi_id number correct_number值
            multiple.setMulti_id(course_id + " " + mindmap_id + " " + node_id);
            multiple.setNumber("0");
            multiple.setCorrect_number("0");

            //增加节点
            nodeChildService.saveMulti(multiple);
            //建立关系
            result_node.setAssignmentMultiple(multiple);
            nodeService.save(result_node);
            success.setSuccess(true);
        }
        return success;
    }

    // 发布判断题
    @RequestMapping(value = "/release_judgement/{course_id}/{mindmap_id}/{node_id}", method = RequestMethod.POST)
    public Success release_judgment(@PathVariable String course_id, @PathVariable String mindmap_id,
                                    @PathVariable String node_id, @RequestBody AssignmentJudgment judgment) {
        Success success = new Success();
        success.setSuccess(false);

        //找到node
        Node result_node = nodeService.findByNodeId(course_id + " " + mindmap_id, node_id);

        //向node节点添加HAS_ASSIGNMENT_JUDGe关系
        if (result_node != null) {

            //向节点里增加multi_id number correct_number值
            judgment.setJudge_id(course_id + " " + mindmap_id + " " + node_id);
            judgment.setNumber("0");
            judgment.setCorrect_number("0");

            //增加节点
            nodeChildService.saveJudge(judgment);
            //建立关系
            result_node.setAssignmentJudgments(judgment);
            nodeService.save(result_node);
            success.setSuccess(true);
        }
        return success;
    }

    // 发布简答题
    @RequestMapping(value = "/release_short/{course_id}/{mindmap_id}/{node_id}", method = RequestMethod.POST)
    public Success release_short(@PathVariable String course_id, @PathVariable String mindmap_id, @PathVariable String node_id, @RequestBody AssignmentShort assignmentShort) {

        Success success = new Success();
        success.setSuccess(false);

        //找到node
        Node result_node = nodeService.findByNodeId(course_id + " " + mindmap_id, node_id);

        //向node节点添加HAS_ASSIGNMENT_MULTI关系
        if (result_node != null) {
            //向节点里增加multi_id number correct_number值
            assignmentShort.setShort_id(course_id + " " + mindmap_id + " " + node_id);

            //增加节点
            nodeChildService.saveShort(assignmentShort);

            //建立关系
            result_node.setAssignmentShorts(assignmentShort);
            nodeService.save(result_node);
            success.setSuccess(true);

        }
        return success;
    }

    // 批改简答题
    @RequestMapping(value="/correct_short/{assignment_long_id}/{student_id}", method = RequestMethod.POST)
    public Success correct_short(@PathVariable long student_id, @PathVariable long assignment_long_id, @RequestBody int score){
        Success success = new Success();
        success.setSuccess(false);

        StudentAnswer studentAnswer=nodeChildService.getStudentAns(student_id, assignment_long_id);
        if (studentAnswer != null) {
            //更改分数 批改
            nodeChildService.setAssignmentShortScore(assignment_long_id, student_id, score);
            success.setSuccess(true);

        }
        return success;
    }


    // 对于某个节点，学生的所有回答
    @RequestMapping(value = "/student_answer_node/{course_id}/{mindmap_id}/{node_id}/{username}", method = RequestMethod.GET)
    public List<StudentAnswer> student_answer_node(@PathVariable String course_id, @PathVariable String mindmap_id, @PathVariable String node_id, @PathVariable String username) {
        return nodeChildService.getStudentAnswersForANode(course_id, mindmap_id, node_id, username);
    }

    // type 传int类型，1,2,3分别表示选择题，简答题，和判断题
    @RequestMapping(value = "/student_real_answer/{longId}/{type}/{username}", method = RequestMethod.GET)
    public AssignmentRealAnswer getRealAnswer(@PathVariable Long longId, @PathVariable int type, @PathVariable String username) {
        return nodeChildService.getRealAnswer(longId, type, username);
    }


    //
//    @RequestMapping(value = "/updateAnswers2", method = RequestMethod.POST)
//    public Success updateAnswers2() {
//        nodeChildService.updateAnswers2();
//        return new Success();
//    }
//
//    @RequestMapping(value = "/updateNetwork", method = RequestMethod.POST)
//    public void updateNetwork() {
//        nodeService.updateNetwork();
//    }


    @RequestMapping(value = "/getAllAssignmentShorts", method = RequestMethod.GET)
    public List<AssignmentShort> getAllAssignmentShorts() {
        return nodeChildService.getAllAssignmentShort();
    }
}
