package com.example.studymate;

public class Mistake {
    private String question, answer, choices;
    private int quantity;

    public Mistake(String question, String answer, int quantity, String choices) {
        setQuestion(question);
        setAnswer(answer);
        setQuantity(quantity);
        setChoices(choices);
    }

    public String getQuestion() {
        return question;
    }
    public String getAnswer() {
        return answer;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getChoices() {
        return choices;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setChoices(String choices) {
        this.choices = choices;
    }
}
