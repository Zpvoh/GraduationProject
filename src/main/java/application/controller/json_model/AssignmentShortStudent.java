package application.controller.json_model;

/**
 * Creator: DreamBoy
 * Date: 2018/12/25.
 */
public class AssignmentShortStudent {
    private Long assignmentLongId;
    private String title;
    private String answer;
    private int score;
    private int fullScore;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getAssignmentLongId() {
        return assignmentLongId;
    }

    public void setAssignmentLongId(Long assignmentLongId) {
        this.assignmentLongId = assignmentLongId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getFullScore() {
        return fullScore;
    }

    public void setFullScore(int fullScore) {
        this.fullScore = fullScore;
    }
}
