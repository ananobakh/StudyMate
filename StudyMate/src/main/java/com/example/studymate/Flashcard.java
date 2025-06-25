package com.example.studymate;

public class Flashcard {
    private String question, answer;
    private boolean revealed;

    public Flashcard(String question, String answer) {
        setQuestion(question);
        setAnswer(answer);
        setRevealed(false);
    }

    public String getQuestion() {
        return question;
    }
    public String getAnswer() {
        return answer;
    }
    public boolean isRevealed() {
        return revealed;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }
}
