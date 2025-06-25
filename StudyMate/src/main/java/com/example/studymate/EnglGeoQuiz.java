package com.example.studymate;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class EnglGeoQuiz implements Initializable {
    @FXML
    private Label questionLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label timesUpLabel;
    @FXML
    private Label finishedAllQuestionsLabel;
    @FXML
    private Button answerButton;
    @FXML
    private Button viewResultsButton;
    @FXML
    private RadioButton answer1RadioButton;
    @FXML
    private RadioButton answer2RadioButton;
    @FXML
    private RadioButton answer3RadioButton;
    @FXML
    private RadioButton answer4RadioButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Pane scalingPane;

    private Timeline quizTimer;
    private Timeline stopwatchTimer;
    private static ArrayList<ArrayList<String>> wordsList;
    public static ArrayList<String> currentQuestion;
    private static String language;
    private static int index;
    private static int numofQuestions;
    private static double progress;

    @FXML
    private void answerButton () throws SQLException {
        Statement statement = null;
        try {
            Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
            statement = connection.createStatement();
            statement.execute("use accounts_StudyMate");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (selectedAnswer() != null) {
            if (language.equals("Georgian")) {
                if (currentQuestion.get(1).equals(selectedAnswer())) {
                    QuizMenu.correctAnswers++;
                    QuizMenu.pointsForLeaderboard++;
                } else {
                    QuizMenu.wrongAnswers++;
                    ResultSet resultSet = statement.executeQuery("select * from mistakes_StudyMate where username = '" + LogIn.username
                            + "' and question = '" + currentQuestion.get(0) + "' and answer = '" + currentQuestion.get(1) + "'");
                    String choices = answer1RadioButton.getText() + " " + answer2RadioButton.getText() + " " + answer3RadioButton.getText() + " " + answer4RadioButton.getText();
                    if (resultSet.next()) {
                        int currentQuestionQuantity = resultSet.getInt(4) + 1;
                        statement.executeUpdate("delete from mistakes_StudyMate where username = '" + LogIn.username +
                                "' and question = '" + currentQuestion.get(0) + "'");
                        statement.executeUpdate("insert into mistakes_StudyMate values ('"
                                + LogIn.username + "', '" + currentQuestion.get(0) + "', '" + currentQuestion.get(1) +
                                "', " + currentQuestionQuantity + ", '" + choices + "')");
                    } else {
                        statement.executeUpdate("insert into mistakes_StudyMate values ('" + LogIn.username + "', '" +
                                currentQuestion.get(0) + "', '" + currentQuestion.get(1) + "', " + 1 + ", '" + choices + "')");
                    }
                }
            } else {
                if (currentQuestion.get(0).equals(selectedAnswer())) {
                    QuizMenu.correctAnswers++;
                    QuizMenu.pointsForLeaderboard++;
                } else {
                    QuizMenu.wrongAnswers++;
                    ResultSet resultSet = statement.executeQuery("select * from mistakes_StudyMate where username = '" + LogIn.username
                            + "' and question = '" + currentQuestion.get(1) + "' and answer = '" + currentQuestion.get(0) + "'");
                    String choices = answer1RadioButton.getText() + " " + answer2RadioButton.getText() + " " + answer3RadioButton.getText() + " " + answer4RadioButton.getText();
                    if (resultSet.next()) {
                        int currentQuestionQuantity = resultSet.getInt(4) + 1;
                        statement.executeUpdate("delete from mistakes_StudyMate where username = '" + LogIn.username +
                                "' and question = '" + currentQuestion.get(1) + "'");
                        statement.executeUpdate("insert into mistakes_StudyMate values ('"
                                + LogIn.username + "', '" + currentQuestion.get(1) + "', '" + currentQuestion.get(0) +
                                "', " + currentQuestionQuantity + ", '" + choices + "')");
                    } else {
                        statement.executeUpdate("insert into mistakes_StudyMate values ('" + LogIn.username + "', '" +
                                currentQuestion.get(1) + "', '" + currentQuestion.get(0) + "', " + 1 + ", '" + choices + "')");
                    }
                }
            }
            if (index == wordsList.size()-1) {
                questionLabel.setText("");
                answerButton.setVisible(false);
                viewResultsButton.setVisible(true);
                timeLabel.setText("");
                answer1RadioButton.setVisible(false);
                answer2RadioButton.setVisible(false);
                answer3RadioButton.setVisible(false);
                answer4RadioButton.setVisible(false);
                finishedAllQuestionsLabel.setText("You finished the quiz!");
                if (quizTimer != null)
                    quizTimer.stop();
                if (stopwatchTimer != null)
                    stopwatchTimer.stop();
            } else {
                index++;
                currentQuestion = wordsList.get(index);
                generatingQuestions();
                answer1RadioButton.setSelected(false);
                answer2RadioButton.setSelected(false);
                answer3RadioButton.setSelected(false);
                answer4RadioButton.setSelected(false);
            }
            numofQuestions++;
            progress = (double) numofQuestions/QuizMenu.totalQuestions;
            progressBar.setProgress(progress);
        }
    }

    @FXML
    private void viewResultsButton (ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "ViewResultsQuizes");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HelloApplication.scalingMethod(scalingPane);

        QuizMenu.quizType = "Language";

        viewResultsButton.setVisible(false);
        if (QuizMenu.useTimeIsChecked && QuizMenu.timeInfo.equals("timer")) {
            QuizMenu.quizTotalTime = 120;
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
                    answer1RadioButton.setVisible(false);
                    answer2RadioButton.setVisible(false);
                    answer3RadioButton.setVisible(false);
                    answer4RadioButton.setVisible(false);
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

        wordsList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            int randomIndex = random.nextInt(HelloApplication.dictionaryList.size());
            if (!wordsList.contains(HelloApplication.dictionaryList.get(randomIndex))) {
                wordsList.add(HelloApplication.dictionaryList.get(randomIndex));
            } else {
                i--;
            }
        }

        QuizMenu.totalQuestions = wordsList.size();
        numofQuestions = 0;
        progress = (double) numofQuestions/QuizMenu.totalQuestions;
        progressBar.setProgress(progress);

        index = 0;
        currentQuestion = wordsList.get(index);
        generatingQuestions();
    }

    private void generatingQuestions() {
        Random random = new Random();
        if (QuizMenu.langQuizType.equals("Both Languages")) {
            int randomLang = random.nextInt(2);
            if (randomLang == 0) {
                language = "Georgian";
                generatingProbableAnswers(currentQuestion.get(1), currentQuestion.get(0), 1, 0);
            } else {
                language = "English";
                generatingProbableAnswers(currentQuestion.get(0), currentQuestion.get(1), 0, 1);
            }
            questionLabel.setText("Translate into " + language + " - " + currentQuestion.get(randomLang));
        } else if (QuizMenu.langQuizType.equals("Eng -> Geo")) {
            language = "Georgian";
            generatingProbableAnswers(currentQuestion.get(1), currentQuestion.get(0), 1, 0);
            questionLabel.setText("Translate into Georgian - " + currentQuestion.get(0));
        } else {
            language = "English";
            generatingProbableAnswers(currentQuestion.get(0), currentQuestion.get(1), 0, 1);
            questionLabel.setText("Translate into English - " + currentQuestion.get(1));
        }
    }

    private void generatingProbableAnswers(String correctAns, String question, int ansIndex, int questIndex) {
        ArrayList<String> probableAnswers = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            probableAnswers.add("");
        }
        Random random = new Random();
        int answerIndex = random.nextInt(4);
        probableAnswers.set(answerIndex, correctAns);
        for(int i = 0; i < 4; i++) {
            if (i != answerIndex) {
                int dictionaryIndex = random.nextInt(HelloApplication.dictionaryList.size());
                if (!probableAnswers.contains(HelloApplication.dictionaryList.get(dictionaryIndex).get(ansIndex)) &&
                        !question.equals(HelloApplication.dictionaryList.get(dictionaryIndex).get(questIndex))) {
                    probableAnswers.set(i, HelloApplication.dictionaryList.get(dictionaryIndex).get(ansIndex));
                } else {
                    i--;
                }
            }
        }
        answer1RadioButton.setText(probableAnswers.get(0));
        answer2RadioButton.setText(probableAnswers.get(1));
        answer3RadioButton.setText(probableAnswers.get(2));
        answer4RadioButton.setText(probableAnswers.get(3));
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
