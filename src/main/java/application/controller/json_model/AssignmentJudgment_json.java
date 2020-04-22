package application.controller.json_model;

public class AssignmentJudgment_json {
    private long id;
    private String judge_id;
    private String title;
    private String correct_answer;
    private String number;
    private String correct_number;
    private int value; //1-10

    public String getJudge_id() {
        return judge_id;
    }

    public void setJudge_id(String judge_id) {
        this.judge_id = judge_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCorrect_number() {
        return correct_number;
    }

    public void setCorrect_number(String correct_number) {
        this.correct_number = correct_number;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
