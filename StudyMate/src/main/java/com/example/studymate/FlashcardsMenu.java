package com.example.studymate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FlashcardsMenu implements Initializable {
    @FXML
    private Pane scalingPane;

    @FXML
    private void createFlashcardsButton (ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "CreateFlashcards");
    }
    @FXML
    private void studyFlashcardsButton (ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "StudyFlashcards");
    }
    @FXML
    private void modifyFlashcardsButton (ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "ModifyFlashcards");
    }

    @FXML
    private void backButton (ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "Menu");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HelloApplication.scalingMethod(scalingPane);
    }
}
