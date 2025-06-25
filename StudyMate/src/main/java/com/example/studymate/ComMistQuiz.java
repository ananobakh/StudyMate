package com.example.studymate;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class ComMistQuiz implements Initializable {
    @FXML
    private Label questionLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label timesUpLabel;
    @FXML
    private Label finishedAllQuestionsLabel;
    @FXML
    private Label noMistakesLabel;
    @FXML
    private Label quizProgressLabel;
    @FXML
    private Label noteLabel;
    @FXML
    private TextField answerTextField;
    @FXML
    private Button answerButton;
    @FXML
    private Button viewResultsButton;
    @FXML
    private Button backToMenuButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private RadioButton answer1RadioButton;
    @FXML
    private RadioButton answer2RadioButton;
    @FXML
    private RadioButton answer3RadioButton;
    @FXML
    private RadioButton answer4RadioButton;
    @FXML
    private Pane scalingPane;


    private Timeline quizTimer;
    private Timeline stopwatchTimer;
    private static ArrayList<Mistake> mistakesList;
    public static Mistake currentQuestion;
    private static int index;
    private static int numofQuestions;
    private static double progress;

    @FXML
    private void answerButton() throws SQLException {
        Statement statement = null;
        try {
            Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
            statement = connection.createStatement();
            statement.execute("use accounts_StudyMate");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (currentQuestion.getChoices() == null) {
            if (!answerTextField.getText().isEmpty()) {
                if (currentQuestion.getAnswer().equalsIgnoreCase(answerTextField.getText())) {
                    QuizMenu.correctAnswers++;
                    QuizMenu.pointsForLeaderboard += 2;
                    currentQuestion.setQuantity(currentQuestion.getQuantity()-1);
                    if (currentQuestion.getQuantity() == 0) {
                        statement.executeUpdate("delete from mistakes_StudyMate where username = '" + LogIn.username +
                                "' and question = '" + currentQuestion.getQuestion() + "'");
                    } else {
                        statement.executeUpdate("delete from mistakes_StudyMate where username = '" + LogIn.username +
                                "' and question = '" + currentQuestion.getQuestion() + "'");
                        statement.executeUpdate("insert into mistakes_StudyMate (username, question, answer, quantity) values ('"
                                + LogIn.username + "', '" + currentQuestion.getQuestion() + "', '" + currentQuestion.getAnswer() +
                                "', " + currentQuestion.getQuantity() + ")");
                    }
                } else {
                    QuizMenu.wrongAnswers++;
                    QuizMenu.pointsForLeaderboard--;
                    currentQuestion.setQuantity(currentQuestion.getQuantity()+1);
                    statement.executeUpdate("delete from mistakes_StudyMate where username = '" + LogIn.username +
                            "' and question = '" + currentQuestion.getQuestion() + "'");
                    statement.executeUpdate("insert into mistakes_StudyMate (username, question, answer, quantity) values ('" +
                            LogIn.username + "', '" + currentQuestion.getQuestion() + "', '" + currentQuestion.getAnswer() + "', " + currentQuestion.getQuantity() + ")");
                }
            }
        } else {
            if (selectedAnswer() != null) {
                if (currentQuestion.getAnswer().equals(selectedAnswer())) {
                    QuizMenu.correctAnswers++;
                    QuizMenu.pointsForLeaderboard += 2;
                    currentQuestion.setQuantity(currentQuestion.getQuantity()-1);
                    if (currentQuestion.getQuantity() == 0) {
                        statement.executeUpdate("delete from mistakes_StudyMate where username = '" + LogIn.username +
                                "' and question = '" + currentQuestion.getQuestion() + "'");
                    } else {
                        statement.executeUpdate("delete from mistakes_StudyMate where username = '" + LogIn.username +
                                "' and question = '" + currentQuestion.getQuestion() + "'");
                        String choices = answer1RadioButton.getText() + " " + answer2RadioButton.getText() + " " + answer3RadioButton.getText() + " " + answer4RadioButton.getText();
                        statement.executeUpdate("insert into mistakes_StudyMate values ('"
                                + LogIn.username + "', '" + currentQuestion.getQuestion() + "', '" + currentQuestion.getAnswer() +
                                "', " + currentQuestion.getQuantity() + ", '" + choices + "')");
                    }
                } else {
                    QuizMenu.wrongAnswers++;
                    QuizMenu.pointsForLeaderboard--;
                    currentQuestion.setQuantity(currentQuestion.getQuantity()+1);
                    statement.executeUpdate("delete from mistakes_StudyMate where username = '" + LogIn.username +
                            "' and question = '" + currentQuestion.getQuestion() + "'");
                    String choices = answer1RadioButton.getText() + " " + answer2RadioButton.getText() + " " + answer3RadioButton.getText() + " " + answer4RadioButton.getText();
                    statement.executeUpdate("insert into mistakes_StudyMate values ('" + LogIn.username + "', '" + currentQuestion.getQuestion() +
                            "', '" + currentQuestion.getAnswer() + "', " + currentQuestion.getQuantity() + ", '" + choices + "')");
                }
            }
        }
        if (index == mistakesList.size()-1) {
            questionLabel.setText("");
            answerButton.setVisible(false);
            viewResultsButton.setVisible(true);
            timeLabel.setText("");
            answerTextField.setVisible(false);
            answer1RadioButton.setVisible(false);
            answer2RadioButton.setVisible(false);
            answer3RadioButton.setVisible(false);
            answer4RadioButton.setVisible(false);
            noteLabel.setVisible(false);
            finishedAllQuestionsLabel.setText("You finished the quiz!");
            if (quizTimer != null)
                quizTimer.stop();
            if (stopwatchTimer != null)
                stopwatchTimer.stop();
        } else {
            index++;
            currentQuestion = mistakesList.get(index);
            if (currentQuestion.getChoices() == null) {
                answerTextField.setVisible(true);
                answer1RadioButton.setVisible(false);
                answer2RadioButton.setVisible(false);
                answer3RadioButton.setVisible(false);
                answer4RadioButton.setVisible(false);
            } else {
                answerTextField.setVisible(false);
                answer1RadioButton.setVisible(true);
                answer2RadioButton.setVisible(true);
                answer3RadioButton.setVisible(true);
                answer4RadioButton.setVisible(true);
                answer1RadioButton.setSelected(false);
                answer2RadioButton.setSelected(false);
                answer3RadioButton.setSelected(false);
                answer4RadioButton.setSelected(false);
                generatingProbableAnswers(currentQuestion.getChoices());
            }
            questionLabel.setText(currentQuestion.getQuestion());
            answerTextField.clear();
        }
        numofQuestions++;
        progress = (double) numofQuestions/QuizMenu.totalQuestions;
        progressBar.setProgress(progress);
    }

    @FXML
    private void viewResultsButton (ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "ViewResultsQuizes");
    }

    @FXML
    private void backToMenuButton (ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "QuizMenu");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HelloApplication.scalingMethod(scalingPane);

        QuizMenu.quizType = "Common Mistakes";
        viewResultsButton.setVisible(false);
        backToMenuButton.setVisible(false);

        mistakesList = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
            Statement statement = connection.createStatement();
            statement.execute("use accounts_StudyMate");
            ResultSet mistakesResultSet = statement.executeQuery("select question, answer, quantity, multipleChoices from mistakes_StudyMate where username = '" + LogIn.username + "'");
            int i = 0;
            while (mistakesResultSet.next() && i<10) {
                mistakesList.add(new Mistake(mistakesResultSet.getString(1), mistakesResultSet.getString(2),
                        mistakesResultSet.getInt(3), mistakesResultSet.getString(4)));
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (mistakesList.size() < 10) {
            noMistakesLabel.setText("You haven't made enough mistakes yet\nYou have to make at least 10 mistakes to write this quiz");
            answerTextField.setVisible(false);
            answerButton.setVisible(false);
            timeLabel.setText("");
            backToMenuButton.setVisible(true);
            quizProgressLabel.setVisible(false);
            progressBar.setVisible(false);
            answerTextField.setVisible(true);
            answer1RadioButton.setVisible(false);
            answer2RadioButton.setVisible(false);
            answer3RadioButton.setVisible(false);
            answer4RadioButton.setVisible(false);
            answerTextField.setVisible(false);
            noteLabel.setVisible(false);
        } else {
            if (QuizMenu.useTimeIsChecked && QuizMenu.timeInfo.equals("timer")) {
                QuizMenu.quizTotalTime = 300;
                timeLabel.setText("Time left: " + QuizMenu.quizTotalTime/60 + "m");
                quizTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                    QuizMenu.quizTotalTime--;
                    int minutes = QuizMenu.quizTotalTime/60;
                    int seconds = QuizMenu.quizTotalTime%60;
                    if (minutes > 0 && seconds != 0) {
                        timeLabel.setText("Time left: " + minutes + "m & " + seconds + "s");
                    } else if (minutes > 0 && seconds == 0) {
                        timeLabel.setText("Time left: " + minutes + "m");
                    } else {
                        timeLabel.setText("Time left: " + seconds + "s");
                    }
                    if (QuizMenu.quizTotalTime == 0) {
                        QuizMenu.finishedInTime = "No";
                        quizTimer.stop();
                        timesUpLabel.setText("Your time is up!");
                        questionLabel.setText("");
                        timeLabel.setText("");
                        answerTextField.setVisible(false);
                        answerButton.setVisible(false);
                        viewResultsButton.setVisible(true);
                    }
                }));
                quizTimer.setCycleCount(Timeline.INDEFINITE);
                quizTimer.play();
            } else if (QuizMenu.useTimeIsChecked && QuizMenu.timeInfo.equals("stopwatch")) {
                timeLabel.setText("Time used: " + QuizMenu.quizTotalTime + "s");
                stopwatchTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                    QuizMenu.quizTotalTime++;
                    int minutes = QuizMenu.quizTotalTime/60;
                    int seconds = QuizMenu.quizTotalTime%60;
                    if (minutes > 0 && seconds != 0) {
                        timeLabel.setText("Time used: " + minutes + "m & " + seconds + "s");
                    } else if (minutes > 0 && seconds == 0) {
                        timeLabel.setText("Time used: " + minutes + "m");
                    } else {
                        timeLabel.setText("Time used: " + seconds + "s");
                    }
                }));
                stopwatchTimer.setCycleCount(Timeline.INDEFINITE);
                stopwatchTimer.play();
            }

            QuizMenu.totalQuestions = mistakesList.size();
            numofQuestions = 0;
            progress = (double) numofQuestions/QuizMenu.totalQuestions;
            progressBar.setProgress(progress);

            index = 0;
            currentQuestion = mistakesList.get(index);
            if (currentQuestion.getChoices() == null) {
                answerTextField.setVisible(true);
                answer1RadioButton.setVisible(false);
                answer2RadioButton.setVisible(false);
                answer3RadioButton.setVisible(false);
                answer4RadioButton.setVisible(false);
            } else {
                answerTextField.setVisible(false);
                answer1RadioButton.setVisible(true);
                answer2RadioButton.setVisible(true);
                answer3RadioButton.setVisible(true);
                answer4RadioButton.setVisible(true);
                generatingProbableAnswers(currentQuestion.getChoices());
            }
            questionLabel.setText(currentQuestion.getQuestion());
        }
    }

    private void generatingProbableAnswers(String choices) {
        ArrayList<String> probableAns = new ArrayList<>();
        int startIndex = 0;
        choices += " ";
        for (int i = 0; i < choices.length(); i++) {
            if (choices.charAt(i) == ' ') {
                probableAns.add(choices.substring(startIndex, i));
                startIndex = i+1;
            }
        }
        Collections.shuffle(probableAns);
        answer1RadioButton.setText(probableAns.get(0));
        answer2RadioButton.setText(probableAns.get(1));
        answer3RadioButton.setText(probableAns.get(2));
        answer4RadioButton.setText(probableAns.get(3));
    }

    private String selectedAnswer() {
        if (answer1RadioButton.isSelected()) {
            return answer1RadioButton.getText();
        } else if (answer2RadioButton.isSelected()) {
            return answer2RadioButton.getText();
        } else if (answer3RadioButton.isSelected()) {
            return answer3RadioButton.getText();
        } else if (answer4RadioButton.isSelected()){
            return answer4RadioButton.getText();
        } else {
            return null;
        }
    }
}
