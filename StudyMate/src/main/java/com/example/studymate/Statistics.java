package com.example.studymate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Statistics implements Initializable {
    @FXML
    private Button resetYesButton;
    @FXML
    private Button resetBackButton;
    @FXML
    private Label resetLabel;
    @FXML
    private Label averageResultsLabel;
    @FXML
    private Label flashcardsQuizLabel;
    @FXML
    private Label mathQuizLabel;
    @FXML
    private Label languageQuizLabel;
    @FXML
    private Label comMistQuizLabel;
    @FXML
    private Label usedTimeLabel;
    @FXML
    private Label usedTimerLabel;
    @FXML
    private Label usedStopWatchLabel;
    @FXML
    private Label finishedInTimeLabel;
    @FXML
    private Label avgTimeTakenLabel;
    @FXML
    private Label avgTimeLeftLabel;
    @FXML
    private PieChart pieChart1;
    @FXML
    private PieChart pieChart2;

    private static int numOfFlashQuiz, numOfMathQuiz, numOfLangQuiz, numOfComMistQuiz;
    private static int numOfCorrectAns, numOfWrongAns, numOfUnansweredQuest;
    private static int numOfUsedTime, numOfTimerUsed, numOfStopwatchUsed, numOfFinishedInTime, sumOfTimeLeft, sumOfTimeTaken;
    private static double averageTimeLeft, averageTimeTaken, averageCorrectAns, averageWrongAns, averageUnanswered;

    @FXML
    private void resetButton() {
        resetYesButton.setVisible(true);
        resetBackButton.setVisible(true);
        resetLabel.setVisible(true);
    }

    @FXML
    private void areYouSureReset(ActionEvent e) throws IOException {
        Button pressedButton = (Button) e.getSource();
        if (pressedButton.getText().equals("Yes")) {
            try {
                Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
                Statement statement = connection.createStatement();
                statement.execute("use accounts_StudyMate");
                statement.executeUpdate("truncate table quiz_statistics_StudyMate");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            resetEverythingToZero();
            setTexts();
            resetYesButton.setVisible(false);
            resetBackButton.setVisible(false);
            resetLabel.setVisible(false);
        } else {
            resetYesButton.setVisible(false);
            resetBackButton.setVisible(false);
            resetLabel.setVisible(false);
        }
    }

    @FXML
    private void backToMenuButton (ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "Menu");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetYesButton.setVisible(false);
        resetBackButton.setVisible(false);
        resetLabel.setVisible(false);
        resetEverythingToZero();

        try {
            Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
            Statement statement = connection.createStatement();
            statement.execute("use accounts_StudyMate");
            ResultSet resultSet = statement.executeQuery("select * from quiz_statistics_StudyMate where username = '" + LogIn.username + "'");
            while (resultSet.next()) {
                if (resultSet.getString(2).equals("Flashcards")) {
                    numOfFlashQuiz++;
                } else if (resultSet.getString(2).equals("Math")) {
                    numOfMathQuiz++;
                } else if (resultSet.getString(2).equals("Language")) {
                    numOfLangQuiz++;
                } else {
                    numOfComMistQuiz++;
                }
                numOfCorrectAns += resultSet.getInt(3);
                numOfWrongAns += resultSet.getInt(4);
                numOfUnansweredQuest += resultSet.getInt(5);
                if (resultSet.getBoolean(6)) {
                    numOfUsedTime++;
                }
                if (resultSet.getString(7) != null && resultSet.getString(7).equals("Timer")) {
                    numOfTimerUsed++;
                } else if (resultSet.getString(7) != null && resultSet.getString(7).equals("StopWatch")) {
                    numOfStopwatchUsed++;
                }
                if (resultSet.getBoolean(8)) {
                    numOfFinishedInTime++;
                }
                sumOfTimeLeft += resultSet.getInt(9);
                sumOfTimeTaken += resultSet.getInt(10);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int totalAnswers = numOfCorrectAns+numOfWrongAns+numOfUnansweredQuest;
        if (totalAnswers != 0) {
            averageCorrectAns = (double) numOfCorrectAns*100/ (double) totalAnswers;
            averageCorrectAns = Math.round(averageCorrectAns*100.0)/100.0;
            averageWrongAns = (double) numOfWrongAns*100/ (double) totalAnswers;
            averageWrongAns = Math.round(averageWrongAns*100.0)/100.0;
            averageUnanswered = (double) numOfUnansweredQuest*100/ (double) totalAnswers;
            averageUnanswered = Math.round(averageUnanswered*100.0)/100.0;
        }
        if (numOfFinishedInTime != 0) {
            averageTimeLeft = (double) sumOfTimeLeft/ (double) numOfFinishedInTime;
            averageTimeLeft = Math.round(averageTimeLeft*100.0)/100.0;
        }
        if (numOfStopwatchUsed != 0) {
            averageTimeTaken = (double) sumOfTimeTaken/ (double) numOfStopwatchUsed;
            averageTimeTaken = Math.round(averageTimeTaken*100.0)/100.0;
        }
        setTexts();
    }

    private void setTexts() {
        averageResultsLabel.setText("Average result: " + averageCorrectAns + "%");
        flashcardsQuizLabel.setText("Flashcards Quiz: Taken " + numOfFlashQuiz + " " + timeOrTimes(numOfFlashQuiz));
        mathQuizLabel.setText("Math Quiz: Taken " + numOfMathQuiz + " " + timeOrTimes(numOfMathQuiz));
        languageQuizLabel.setText("Language Quiz: Taken " + numOfLangQuiz + " " + timeOrTimes(numOfLangQuiz));
        comMistQuizLabel.setText("Common Mitakes Quiz: Taken " + numOfComMistQuiz + " " + timeOrTimes(numOfComMistQuiz));
        usedTimeLabel.setText("Used time: " + numOfUsedTime + " " + timeOrTimes(numOfUsedTime));
        usedTimerLabel.setText("Used timer: " + numOfTimerUsed + " " + timeOrTimes(numOfTimerUsed));
        usedStopWatchLabel.setText("Used stopwatch: " + numOfStopwatchUsed + " " + timeOrTimes(numOfStopwatchUsed));
        finishedInTimeLabel.setText("Finished in time while using timer: " + numOfFinishedInTime + " " + timeOrTimes(numOfFinishedInTime));
        int minutes = (int) averageTimeLeft/60;
        double seconds = averageTimeLeft%60.0;
        seconds = Math.round(seconds*100.0)/100.0;
        if (minutes > 0 && seconds != 0.0) {
            avgTimeLeftLabel.setText("Average time left while using timer: " + minutes + "m & " + seconds + "s");
        } else if (minutes > 0.0 && seconds == 0.0) {
            avgTimeLeftLabel.setText("Average time left while using timer: " + minutes + "m");
        } else {
            avgTimeLeftLabel.setText("Average time left while using timer: " + seconds + "s");
        }

        int minutes1 = (int) averageTimeTaken/60;
        double seconds1 = averageTimeTaken%60.0;
        seconds1 = Math.round(seconds1*100.0)/100.0;
        if (minutes1 > 0 && seconds1 != 0.0) {
            avgTimeTakenLabel.setText("Average time taken while using stopwatch: " + minutes1 + "m & " + seconds1 + "s");
        } else if (minutes1 > 0.0 && seconds1 == 0.0) {
            avgTimeTakenLabel.setText("Average time taken while using stopwatch: " + minutes1 + "m");
        } else {
            avgTimeTakenLabel.setText("Average time taken while using stopwatch: " + seconds1 + "s");
        }

        pieChart1.getData().addAll(
                new PieChart.Data("Correct", averageCorrectAns),
                new PieChart.Data("Wrong", averageWrongAns),
                new PieChart.Data("Not Answered", averageUnanswered)
        );
        pieChart1.setTitle("Quiz Performance");
        pieChart1.setPrefWidth(300);
        pieChart1.setPrefHeight(300);

        pieChart2.getData().addAll(
                new PieChart.Data("Flashcards", numOfFlashQuiz),
                new PieChart.Data("Math", numOfMathQuiz),
                new PieChart.Data("Language", numOfLangQuiz),
                new PieChart.Data("Common Mistakes", numOfComMistQuiz)
        );
        pieChart2.setTitle("Quiz Types");
        pieChart2.setPrefWidth(300);
        pieChart2.setPrefHeight(300);
    }

    private void resetEverythingToZero() {
        numOfFlashQuiz = 0; numOfMathQuiz = 0; numOfLangQuiz = 0; numOfComMistQuiz = 0;
        numOfCorrectAns = 0; numOfWrongAns = 0; numOfUnansweredQuest = 0;
        numOfUsedTime = 0; numOfTimerUsed = 0; numOfStopwatchUsed = 0; numOfFinishedInTime = 0; sumOfTimeLeft = 0; sumOfTimeTaken = 0;
        averageTimeLeft = 0.0; averageTimeTaken = 0.0; averageCorrectAns = 0.0; averageWrongAns = 0.0; averageUnanswered = 0.0;
        pieChart1.getData().clear();
        pieChart2.getData().clear();
    }

    private String timeOrTimes(int x) {
        if (x == 1)
            return "time";
        return "times";
    }
}
