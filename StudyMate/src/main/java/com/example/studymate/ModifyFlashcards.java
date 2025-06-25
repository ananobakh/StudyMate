package com.example.studymate;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ModifyFlashcards implements Initializable {
    @FXML
    private TableView<Flashcard> table;
    @FXML
    private TableColumn<Flashcard, String> frontColumn;
    @FXML
    private TableColumn<Flashcard, String> backColumn;
    @FXML
    private Label frontTextLabel;
    @FXML
    private Label backTextLabel;
    @FXML
    private Label frontLabel;
    @FXML
    private Label backLabel;
    @FXML
    private Label editFlashcardLabel;
    @FXML
    private Label noFlashcardsLabel;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editFrontButton;
    @FXML
    private Button editBackButton;
    @FXML
    private Button backToTableButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button backToMenuButton;
    @FXML
    private TextField editTextField;
    @FXML
    private Pane scalingPane;

    private ArrayList<Flashcard> flashcards;
    private Flashcard currentFlashcard;
    private String editParameter;

    @FXML
    private void deleteButton() {
        try {
            Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
            Statement statement = connection.createStatement();
            statement.execute("use accounts_StudyMate");
            statement.executeUpdate("delete from flashcards_StudyMate where username = '" + LogIn.username +
                    "' and question = '" + currentFlashcard.getQuestion() + "' and answer = '" + currentFlashcard.getAnswer() + "'");
            flashcards.clear();
            ResultSet flashcardsResultSet = statement.executeQuery("select question, answer from flashcards_StudyMate where username = '" + LogIn.username + "'");
            while (flashcardsResultSet.next()) {
                flashcards.add(new Flashcard(flashcardsResultSet.getString(1), flashcardsResultSet.getString(2)));
            }
            if (flashcards.isEmpty()) {
                noFlashcardsLabel.setText("There are no flashcards yet");
                table.setVisible(false);
            } else {
                table.setVisible(true);
                table.refresh();
            }
            frontColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
            backColumn.setCellValueFactory(new PropertyValueFactory<>("answer"));
            table.setItems(FXCollections.observableArrayList(flashcards));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        frontLabel.setVisible(false);
        backLabel.setVisible(false);
        frontTextLabel.setText("");
        backTextLabel.setText("");
        deleteButton.setVisible(false);
        editFrontButton.setVisible(false);
        editBackButton.setVisible(false);
        backToTableButton.setVisible(false);
        editFlashcardLabel.setVisible(false);
        editTextField.setVisible(false);
        saveButton.setVisible(false);
        backToMenuButton.setVisible(true);
        StudyFlashcards.finished = true;
    }

    @FXML
    private void editEvent(ActionEvent e) {
        editTextField.setVisible(true);
        editFlashcardLabel.setVisible(true);
        saveButton.setVisible(true);
        Button pressedButton = (Button) e.getSource();
        if (pressedButton.getText().equals("Edit Front")) {
            editParameter = pressedButton.getText();
            editTextField.setText(currentFlashcard.getQuestion());
        } else if (pressedButton.getText().equals("Edit Back")){
            editParameter = pressedButton.getText();
            editTextField.setText(currentFlashcard.getAnswer());
        }
        StudyFlashcards.finished = true;
    }

    @FXML
    private void saveButton() {
        try {
            Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
            Statement statement = connection.createStatement();
            statement.execute("use accounts_StudyMate");
            if (editParameter.equals("Edit Front")) {
                frontTextLabel.setText(editTextField.getText());
                statement.executeUpdate("update flashcards_StudyMate set question = '" + editTextField.getText()
                        + "' where username = '" + LogIn.username + "' and question = '" + currentFlashcard.getQuestion() +
                        "' and answer = '" + currentFlashcard.getAnswer() + "'");
                currentFlashcard.setQuestion(editTextField.getText());
            } else {
                backTextLabel.setText(editTextField.getText());
                statement.executeUpdate("update flashcards_StudyMate set answer = '" + editTextField.getText()
                        + "' where username = '" + LogIn.username + "' and question = '" + currentFlashcard.getQuestion() +
                        "' and answer = '" + currentFlashcard.getAnswer() + "'");
                currentFlashcard.setAnswer(editTextField.getText());
            }
            flashcards.clear();
            ResultSet flashcardsResultSet = statement.executeQuery("select question, answer from flashcards_StudyMate where username = '" + LogIn.username + "'");
            while (flashcardsResultSet.next()) {
                flashcards.add(new Flashcard(flashcardsResultSet.getString(1), flashcardsResultSet.getString(2)));
            }
            frontColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
            backColumn.setCellValueFactory(new PropertyValueFactory<>("answer"));
            table.setItems(FXCollections.observableArrayList(flashcards));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        editTextField.setVisible(false);
        editFlashcardLabel.setVisible(false);
        saveButton.setVisible(false);
    }

    @FXML
    private void backToTableButton() {
        table.setVisible(true);
        table.refresh();
        backToMenuButton.setVisible(true);
        frontLabel.setVisible(false);
        backLabel.setVisible(false);
        deleteButton.setVisible(false);
        editFrontButton.setVisible(false);
        editBackButton.setVisible(false);
        backToTableButton.setVisible(false);
        editFlashcardLabel.setVisible(false);
        editTextField.setVisible(false);
        saveButton.setVisible(false);
    }

    @FXML
    private void backToMenuButton (ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "FlashcardsMenu");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HelloApplication.scalingMethod(scalingPane);

        frontLabel.setVisible(false);
        backLabel.setVisible(false);
        deleteButton.setVisible(false);
        editFrontButton.setVisible(false);
        editBackButton.setVisible(false);
        backToTableButton.setVisible(false);
        editFlashcardLabel.setVisible(false);
        editTextField.setVisible(false);
        saveButton.setVisible(false);

        flashcards = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
            Statement statement = connection.createStatement();
            statement.execute("use accounts_StudyMate");
            ResultSet flashcardsResultSet = statement.executeQuery("select question, answer from flashcards_StudyMate where username = '" + LogIn.username + "'");
            while (flashcardsResultSet.next()) {
                flashcards.add(new Flashcard(flashcardsResultSet.getString(1), flashcardsResultSet.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (flashcards.isEmpty()) {
            noFlashcardsLabel.setText("There are no flashcards yet");
            table.setVisible(false);
        } else {
            table.setVisible(true);
        }

        frontColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        backColumn.setCellValueFactory(new PropertyValueFactory<>("answer"));
        table.setItems(FXCollections.observableArrayList(flashcards));
        table.setOnMouseClicked(event -> {
            Flashcard selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                currentFlashcard = selected;
                frontTextLabel.setText(currentFlashcard.getQuestion());
                backTextLabel.setText(currentFlashcard.getAnswer());
                frontLabel.setVisible(true);
                backLabel.setVisible(true);
                deleteButton.setVisible(true);
                editFrontButton.setVisible(true);
                editBackButton.setVisible(true);
                backToTableButton.setVisible(true);
                backToMenuButton.setVisible(false);
                table.setVisible(false);
            }
        });
        table.refresh();
    }
}
