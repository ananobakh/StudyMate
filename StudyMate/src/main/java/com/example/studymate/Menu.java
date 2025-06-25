package com.example.studymate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Menu implements Initializable {
    @FXML
    private Label welcomeUserLabel;
    @FXML
    private Pane scalingPane;

    @FXML
    private void quizButton(ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "QuizMenu");
    }

    @FXML
    private void flashcardsButton(ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "FlashcardsMenu");
    }

    @FXML
    private void statisticsButton(ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "Statistics");
    }

    @FXML
    private void leaderboardButton(ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "Leaderboard");
    }

    @FXML
    private void logOutButton(ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "LogIn");
        FileWriter fileWriter = new FileWriter("Remember User.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write("false");
        bufferedWriter.close();
        fileWriter.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HelloApplication.scalingMethod(scalingPane);

        welcomeUserLabel.setText("Welcome " + LogIn.username);
    }
}