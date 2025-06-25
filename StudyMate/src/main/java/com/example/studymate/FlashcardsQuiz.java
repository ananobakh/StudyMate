package com.example.studymate;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class FlashcardsQuiz implements Initializable {
    @FXML
    private Label questionLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label timesUpLabel;
    @FXML
    private Label finishedAllQuestionsLabel;
    @FXML
    private Label noFlashcardsLabel;
    @FXML
    private Label quizProgressLabel;
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
    private Pane scalingPane;

    private Timeline quizTimer;
    private Timeline stopwatchTimer;
    private static ArrayList<Flashcard> flashcardsSQL;
    private static ArrayList<Flashcard> flashcards;
    public static Flashcard currentFlashcard;
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
        if (!answerTextField.getText().isEmpty()) {
            if (currentFlashcard.getAnswer().equalsIgnoreCase(answerTextField.getText())) {
                QuizMenu.correctAnswers++;
                QuizMenu.pointsForLeaderboard++;
            } else {
                QuizMenu.wrongAnswers++;
                ResultSet resultSet = statement.executeQuery("select * from mistakes_StudyMate where username = '" + LogIn.username
                        + "' and question = '" + currentFlashcard.getQuestion() + "' and answer = '" + currentFlashcard.getAnswer() + "'");
                if (resultSet.next()) {
                    int currentQuestionQuantity = resultSet.getInt(4) + 1;
                    statement.executeUpdate("delete from mistakes_StudyMate where username = '" + LogIn.username +
                            "' and question = '" + currentFlashcard.getQuestion() + "'");
                    statement.executeUpdate("insert into mistakes_StudyMate (username, question, answer, quantity) values ('" +
                            LogIn.username + "', '" + currentFlashcard.getQuestion() + "', '" + currentFlashcard.getAnswer() + "', "
                            + currentQuestionQuantity + ")");
                } else {
                    statement.executeUpdate("insert into mistakes_StudyMate (username, question, answer, quantity) values ('" +
                            LogIn.username + "', '" + currentFlashcard.getQuestion() + "', '" + currentFlashcard.getAnswer()
                            + "', " + 1 + ")");
                }
            }
            numofQuestions++;
            progress = (double) numofQuestions/QuizMenu.totalQuestions;
            progressBar.setProgress(progress);
            flashcards.remove(currentFlashcard);
            if (flashcards.isEmpty()) {
                questionLabel.setText("");
                answerButton.setVisible(false);
                viewResultsButton.setVisible(true);
                timeLabel.setText("");
                answerTextField.setVisible(false);
                finishedAllQuestionsLabel.setText("You finished the quiz!");
                if (quizTimer != null)
                    quizTimer.stop();
                if (stopwatchTimer != null)
                    stopwatchTimer.stop();
            } else {
                Random random = new Random();
                int randomIndex = random.nextInt(flashcards.size());
                currentFlashcard = flashcards.get(randomIndex);
                questionLabel.setText(currentFlashcard.getQuestion());
                answerTextField.clear();
            }
        }
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

        QuizMenu.quizType = "Flashcards";
        viewResultsButton.setVisible(false);
        backToMenuButton.setVisible(false);

        flashcardsSQL = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
            Statement statement = connection.createStatement();
            statement.execute("use accounts_StudyMate");
            ResultSet flashcardsResultSet = statement.executeQuery("select question, answer from flashcards_StudyMate where username = '" + LogIn.username + "'");
            while (flashcardsResultSet.next()) {
                flashcardsSQL.add(new Flashcard(flashcardsResultSet.getString(1), flashcardsResultSet.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (flashcardsSQL.size() < 10) {
            noFlashcardsLabel.setText("You haven't created enough flashcards yet\nYou have to make at least 10 flashcards to write this quiz");
            answerTextField.setVisible(false);
            answerButton.setVisible(false);
            timeLabel.setText("");
            backToMenuButton.setVisible(true);
            quizProgressLabel.setVisible(false);
            progressBar.setVisible(false);
        } else {
            Random random = new Random();
            flashcards = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                int randomIndex = random.nextInt(flashcardsSQL.size());
                if (!flashcards.contains(flashcardsSQL.get(randomIndex))) {
                    flashcards.add(flashcardsSQL.get(randomIndex));
                } else {
                    i--;
                }
            }
            if (QuizMenu.useTimeIsChecked && QuizMenu.timeInfo.equals("timer")) {
                QuizMenu.quizTotalTime = 100;
                timeLabel.setText("Time left: " + QuizMenu.quizTotalTime/60 + "m & " + QuizMenu.quizTotalTime%60 + "s");
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

            QuizMenu.totalQuestions = flashcards.size();
            numofQuestions = 0;
            progress = (double) numofQuestions/QuizMenu.totalQuestions;
            progressBar.setProgress(progress);

            int randomIndex = random.nextInt(flashcards.size());
            currentFlashcard = flashcards.get(randomIndex);
            questionLabel.setText(currentFlashcard.getQuestion());
        }
    }
}
