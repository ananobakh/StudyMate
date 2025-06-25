package com.example.studymate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class CreateFlashcards implements Initializable {
    @FXML
    private TextField questionTextField;
    @FXML
    private TextField answerTextField;
    @FXML
    private Pane scalingPane;

    @FXML
    private void createFlashcardButton() throws SQLException, IOException {
        String question = questionTextField.getText();
        String answer = answerTextField.getText();
        if (!question.isEmpty() && !answer.isEmpty()) {
            Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
            Statement statement = connection.createStatement();
            statement.execute("use accounts_StudyMate");
            PreparedStatement preparedStatement = connection.prepareStatement("insert into flashcards_StudyMate values (?, ?, ?)");
            preparedStatement.setString(1, LogIn.username);
            preparedStatement.setString(2, question);
            preparedStatement.setString(3, answer);
            preparedStatement.executeUpdate();
            questionTextField.clear();
            answerTextField.clear();
        }
    }

    @FXML
    private void doneButton (ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "FlashcardsMenu");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HelloApplication.scalingMethod(scalingPane);
    }
}
