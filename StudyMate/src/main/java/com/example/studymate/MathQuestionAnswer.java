package com.example.studymate;

public class MathQuestionAnswer {
    private String question, answer;

    public MathQuestionAnswer(String question, String answer) {
        setQuestion(question);
        setAnswer(answer);
    }

    public String getQuestion() {
        return question;
    }
    public String getAnswer() {
        return answer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
