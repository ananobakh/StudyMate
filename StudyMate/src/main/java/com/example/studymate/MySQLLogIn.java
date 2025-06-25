package com.example.studymate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class MySQLLogIn implements Initializable {
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Label incorrectInfoLabel;
    @FXML
    private Pane scalingPane;

    private boolean ind = false;

    @FXML
    private void logInButton (ActionEvent e) throws IOException, SQLException {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        try {
            Connection connection = DriverManager.getConnection(HelloApplication.url, username, password);
            ind = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            incorrectInfoLabel.setText("Incorrect username or password");
            usernameTextField.clear();
            passwordTextField.clear();
        }

        if (ind) {
            try {
                Connection connection = DriverManager.getConnection(HelloApplication.url, username, password);
                Statement statement = connection.createStatement();
                statement.execute("create database if not exists accounts_StudyMate");
                statement.execute("use accounts_StudyMate");
                statement.executeUpdate("create table if not exists users_StudyMate (first_name varchar(15), last_name varchar(20), " +
                        "birthday date, gender varchar(10), city varchar(15), email varchar(20), username varchar(30), password varchar(30), totalPoints int)");
                statement.executeUpdate("create table if not exists flashcards_StudyMate (username varchar(30), question varchar(200), answer varchar(50))");
                statement.executeUpdate("create table if not exists mistakes_StudyMate (username varchar(30), question varchar(200), answer varchar(50), quantity int, multipleChoices varchar(100))");
                statement.executeUpdate("create table if not exists quiz_statistics_StudyMate (username varchar(30), quizType varchar(20), " +
                        "numOfCorrectAns int, numOfWrongAns int, numOfUnansweredQuest int, usedTimeParameter boolean, timeParameterUsed varchar(15), " +
                        "finishedInTime boolean, timeLeft int, timeTaken int)");
                statement.execute("set global event_scheduler = on");
                statement.executeUpdate("create event if not exists reset_points_weekly on schedule every 1 week do update users_StudyMate set totalPoints = 0");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("SQL Username & Password"));
            bufferedWriter.write("true\n");
            bufferedWriter.write(username);
            bufferedWriter.write("\n");
            bufferedWriter.write(password);
            bufferedWriter.close();

            HelloApplication.username = username;
            HelloApplication.password = password;

            HelloApplication.changeScene(e, "LogIn");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HelloApplication.scalingMethod(scalingPane);
    }
}
