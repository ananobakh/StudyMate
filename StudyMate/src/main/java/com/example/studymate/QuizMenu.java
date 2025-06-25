package com.example.studymate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class QuizMenu implements Initializable {
    @FXML
    private RadioButton timerRadioButton;
    @FXML
    private RadioButton stopWatchRadioButton;
    @FXML
    private CheckBox useTimeCheckbox;
    @FXML
    private Label selectRadioButtonLabel;
    @FXML
    private Label engGeoQuizParameter;
    @FXML
    private Label mathQuizParameter;
    @FXML
    private Button engToGeobutton;
    @FXML
    private Button geoToEngButton;
    @FXML
    private Button bothLangButton;
    @FXML
    private Button multipleChoiceButton;
    @FXML
    private Button openQuestionsButton;
    @FXML
    private Pane scalingPane;

    public static boolean useTimeIsChecked;
    public static String timeInfo;
    public static int correctAnswers;
    public static int wrongAnswers;
    public static int totalQuestions;
    public static int quizTotalTime;
    public static String finishedInTime;
    public static String quizType;
    public static String langQuizType;
    public static String mathQuizType;
    public static int pointsForLeaderboard;

    @FXML
    private void flascardsQuizButton (ActionEvent e) throws IOException {
        timeParameters();
        if ((useTimeIsChecked && timeInfo != null) || !useTimeIsChecked) {
            HelloApplication.changeScene(e, "FlashcardsQuiz");
        }
    }

    @FXML
    private void mathQuizButton (ActionEvent e) throws IOException {
        mathQuizParameter.setText("Choose what type of\nmath quiz you want to write");
        multipleChoiceButton.setVisible(true);
        openQuestionsButton.setVisible(true);
        engToGeobutton.setVisible(false);
        geoToEngButton.setVisible(false);
        bothLangButton.setVisible(false);
        engGeoQuizParameter.setText("");
    }
    @FXML
    private void mathQuizType(ActionEvent e) throws IOException {
        Button pressedButton = (Button) e.getSource();
        mathQuizType = pressedButton.getText();
        timeParameters();
        if ((useTimeIsChecked && timeInfo != null) || !useTimeIsChecked) {
            HelloApplication.changeScene(e, "MathQuiz");
        }
    }

    @FXML
    private void englGeoQuizButton (ActionEvent e) {
        engGeoQuizParameter.setText("Choose what type of\nlanguage quiz you want to write");
        engToGeobutton.setVisible(true);
        geoToEngButton.setVisible(true);
        bothLangButton.setVisible(true);
        multipleChoiceButton.setVisible(false);
        openQuestionsButton.setVisible(false);
        mathQuizParameter.setText("");
    }
    @FXML
    private void langQuizType(ActionEvent e) throws IOException {
        Button pressedButton = (Button) e.getSource();
        langQuizType = pressedButton.getText();
        timeParameters();
        if ((useTimeIsChecked && timeInfo != null) || !useTimeIsChecked) {
            HelloApplication.changeScene(e, "EnglGeoQuiz");
        }
    }

    @FXML
    private void comMistQuizButton (ActionEvent e) throws IOException {
        timeParameters();
        if ((useTimeIsChecked && timeInfo != null) || !useTimeIsChecked) {
            HelloApplication.changeScene(e, "ComMistQuiz");
        }
    }

    @FXML
    public void useTimeCheckBox() {
        if (useTimeCheckbox.isSelected()) {
            timerRadioButton.setVisible(true);
            stopWatchRadioButton.setVisible(true);
        } else {
            timerRadioButton.setSelected(false);
            stopWatchRadioButton.setSelected(false);
            timerRadioButton.setVisible(false);
            stopWatchRadioButton.setVisible(false);
        }
    }

    @FXML
    private void backButton(ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "Menu");
    }

    public void timeParameters() {
        useTimeIsChecked = useTimeCheckbox.isSelected();
        if (useTimeIsChecked) {
            if (timerRadioButton.isSelected()) {
                timeInfo = "timer";
            } else if (stopWatchRadioButton.isSelected()) {
                timeInfo = "stopwatch";
            }
        }
        if (useTimeIsChecked && timeInfo == null) {
            selectRadioButtonLabel.setText("Select which time parameter you want to use");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HelloApplication.scalingMethod(scalingPane);

        useTimeIsChecked = false;
        timeInfo = null;
        timerRadioButton.setVisible(false);
        stopWatchRadioButton.setVisible(false);
        engToGeobutton.setVisible(false);
        geoToEngButton.setVisible(false);
        bothLangButton.setVisible(false);
        multipleChoiceButton.setVisible(false);
        openQuestionsButton.setVisible(false);
        correctAnswers = 0;
        wrongAnswers = 0;
        quizTotalTime = 0;
        pointsForLeaderboard = 0;
        finishedInTime = "Yes";
    }
}
