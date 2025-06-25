package com.example.studymate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewResultsQuizes implements Initializable {
    @FXML
    private Label correctAnswersLabel;
    @FXML
    private Label wrongAnswersLabel;
    @FXML
    private Label timeParameterLabel;
    @FXML
    private Pane scalingPane;

    @FXML
    private void backToMenu (ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "QuizMenu");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HelloApplication.scalingMethod(scalingPane);

        correctAnswersLabel.setText(QuizMenu.correctAnswers + "/" + QuizMenu.totalQuestions);
        wrongAnswersLabel.setText(QuizMenu.wrongAnswers + "/" + QuizMenu.totalQuestions);
        int numOfUnansweredQuestions = QuizMenu.totalQuestions-QuizMenu.correctAnswers-QuizMenu.wrongAnswers;
        String timeParameterUsed;
        boolean finishedInTime = false;
        if (QuizMenu.useTimeIsChecked && QuizMenu.timeInfo.equals("stopwatch")) {
            int minutes = QuizMenu.quizTotalTime/60;
            int seconds = QuizMenu.quizTotalTime%60;
            if (minutes > 0 && seconds != 0) {
                timeParameterLabel.setText("Time parameter used - Stopwatch\nTime taken: " +
                        minutes + "m & " + seconds + "s");
            } else if (minutes > 0 && seconds == 0) {
                timeParameterLabel.setText("Time parameter used - Stopwatch\nTime taken: " +
                         minutes + "m");
            } else {
                timeParameterLabel.setText("Time parameter used - Stopwatch\nTime taken: " +
                        seconds + "s");
            }
            timeParameterUsed = "StopWatch";
        } else if (QuizMenu.useTimeIsChecked && QuizMenu.timeInfo.equals("timer")) {
            if (QuizMenu.finishedInTime.equals("Yes")) {
                QuizMenu.pointsForLeaderboard += 10;
                QuizMenu.pointsForLeaderboard += QuizMenu.correctAnswers;
                QuizMenu.pointsForLeaderboard -= QuizMenu.wrongAnswers;
                int minutes = QuizMenu.quizTotalTime/60;
                int seconds = QuizMenu.quizTotalTime%60;
                if (minutes > 0 && seconds != 0) {
                    timeParameterLabel.setText("Time parameter used - Timer\nFinished in time: Yes\nTime left: " +
                            minutes + "m & " + seconds + "s");
                } else if (minutes > 0 && seconds == 0) {
                    timeParameterLabel.setText("Time parameter used - Timer\nFinished in time: Yes\nTime left: " +
                            minutes + "m");
                } else {
                    timeParameterLabel.setText("Time parameter used - Timer\nFinished in time: Yes\nTime left: " +
                            seconds + "s");
                }
                finishedInTime = true;
            } else if (QuizMenu.finishedInTime.equals("No")) {
                QuizMenu.pointsForLeaderboard = -10;
                timeParameterLabel.setText("Time parameter used - Timer\nFinished in time: No");
                finishedInTime = false;
            }
            timeParameterUsed = "Timer";
        } else {
            timeParameterLabel.setText("Time parameter used - none");
            timeParameterUsed = "None";
        }

        try {
            Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
            Statement statement = connection.createStatement();
            statement.execute("use accounts_StudyMate");

            // For statistics
            if (QuizMenu.useTimeIsChecked) {
                if (timeParameterUsed.equals("Timer")) {
                    statement.executeUpdate("insert into quiz_statistics_StudyMate (username, quizType, numOfCorrectAns, " +
                            "numOfWrongAns, numOfUnansweredQuest, usedTimeParameter, timeParameterUsed, finishedInTime, timeLeft) values ('" +
                            LogIn.username + "', '" + QuizMenu.quizType + "', " + QuizMenu.correctAnswers + ", " + QuizMenu.wrongAnswers +
                            ", " + numOfUnansweredQuestions + ", " + QuizMenu.useTimeIsChecked + ", '" + timeParameterUsed + "', " + finishedInTime
                            + ", " + QuizMenu.quizTotalTime + ")");
                } else {
                    statement.executeUpdate("insert into quiz_statistics_StudyMate (username, quizType, numOfCorrectAns, " +
                            "numOfWrongAns, numOfUnansweredQuest, usedTimeParameter, timeParameterUsed, timeTaken) values ('" +
                            LogIn.username + "', '" + QuizMenu.quizType + "', " + QuizMenu.correctAnswers + ", " + QuizMenu.wrongAnswers +
                            ", " + numOfUnansweredQuestions + ", " + QuizMenu.useTimeIsChecked + ", '" + timeParameterUsed + "', " + QuizMenu.quizTotalTime + ")");
                }
            } else {
                statement.executeUpdate("insert into quiz_statistics_StudyMate (username, quizType, numOfCorrectAns, " +
                        "numOfWrongAns, numOfUnansweredQuest, usedTimeParameter) values ('" + LogIn.username + "', '" + QuizMenu.quizType +
                        "', " + QuizMenu.correctAnswers + ", " + QuizMenu.wrongAnswers + ", " + numOfUnansweredQuestions + ", "
                        + QuizMenu.useTimeIsChecked + ")");
            }

            // For leaderboard
            ResultSet resultSet = statement.executeQuery("select totalPoints from users_StudyMate where username = '" +
                    LogIn.username + "'");
            resultSet.next();
            QuizMenu.pointsForLeaderboard += resultSet.getInt(1);
            statement.executeUpdate("update users_StudyMate set totalPoints = " + QuizMenu.pointsForLeaderboard +
                    " where username = '" + LogIn.username + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
