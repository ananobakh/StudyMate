package com.example.studymate;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class StudyFlashcards implements Initializable {
    @FXML
    private Label questionLabel;
    @FXML
    private Label finishedLabel;
    @FXML
    private Label revealedAnswerLabel;
    @FXML
    private Label progressLabel;
    @FXML
    private Label noFlashcardsLabel;
    @FXML
    private Label revealedOrNotLabel;
    @FXML
    private Label resumeProgressLabel;
    @FXML
    private Button lastQuestionButton;
    @FXML
    private Button nextQuestionButton;
    @FXML
    private Button revealAnswerButton;
    @FXML
    private Button resetProgressButton;
    @FXML
    private Button backToTableButton;
    @FXML
    private TableView<Flashcard> table;
    @FXML
    private TableColumn<Flashcard, String> flashcardQuestionColumn;
    @FXML
    private TableColumn<Flashcard, String> revealedColumn;
    @FXML
    private Pane scalingPane;

    public static ArrayList<Flashcard> flashcards = new ArrayList<>();
    private static ArrayList<Flashcard> displayedFlashcards = new ArrayList<>();
    public static Flashcard currentFlashcard;
    private static int index;
    private static int flashcardNum;
    public static boolean finished = true;

    @FXML
    private void studyingFlashcards (ActionEvent e) throws SQLException {
        Random random = new Random();
        Button pressedButton = (Button) e.getSource();

        if (pressedButton.getText().equals("Next Question")) {
            lastQuestionButton.setVisible(true);
            flashcardNum++;
            if (!displayedFlashcards.contains(currentFlashcard)) {
                displayedFlashcards.add(currentFlashcard);
                flashcards.remove(currentFlashcard);
                index++;
            } else if (!displayedFlashcards.getLast().equals(currentFlashcard)){
                index++;
            }
            if (flashcards.isEmpty() && displayedFlashcards.getLast().equals(currentFlashcard)) {
                boolean ind = true;
                for (int i = 0; i < displayedFlashcards.size(); i++) {
                    if (!displayedFlashcards.get(i).isRevealed()) {
                        ind = false;
                        break;
                    }
                }
                if (ind) {
                    finishedLabel.setText("You finished studying all flashcards!");
                    finished = true;
                }
                table.setVisible(true);
                table.setItems(FXCollections.observableArrayList(displayedFlashcards));
                table.setRowFactory(tv -> new TableRow<Flashcard>() {
                    @Override
                    protected void updateItem(Flashcard flashcard, boolean empty) {
                        super.updateItem(flashcard, empty);
                        if (flashcard == null || empty) {
                            setStyle("");
                        } else {
                            if (flashcard.isRevealed()) {
                                setStyle("-fx-background-color: lightgreen;");
                            } else {
                                setStyle("-fx-background-color: salmon;");
                            }
                        }
                    }
                });
                questionLabel.setText("");
                revealedAnswerLabel.setText("");
                progressLabel.setText("");
                revealedOrNotLabel.setText("");
                lastQuestionButton.setVisible(false);
                nextQuestionButton.setVisible(false);
                revealAnswerButton.setVisible(false);
                table.setOnMouseClicked(event -> {
                    Flashcard selected = table.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        currentFlashcard = selected;
                        table.setVisible(false);
                        questionLabel.setText(currentFlashcard.getQuestion());
                        if (currentFlashcard.isRevealed()) {
                            revealedOrNotLabel.setText("Revealed");
                            revealedOrNotLabel.setTextFill(Color.GREEN);
                        } else {
                            revealedOrNotLabel.setText("Not Revealed");
                            revealedOrNotLabel.setTextFill(Color.RED);
                        }
                        finishedLabel.setText("");
                        resumeProgressLabel.setText("");
                        resetProgressButton.setVisible(false);
                        backToTableButton.setVisible(true);
                        revealAnswerButton.setVisible(true);
                        if (currentFlashcard.isRevealed()) {
                            revealedAnswerLabel.setText("Correct answer is: " + currentFlashcard.getAnswer());
                        } else {
                            revealedAnswerLabel.setText("");
                        }
                    }
                });
            } else {
                if (displayedFlashcards.getLast().equals(currentFlashcard)) {
                    int randomIndex = random.nextInt(flashcards.size());
                    currentFlashcard = flashcards.get(randomIndex);
                } else {
                    currentFlashcard = displayedFlashcards.get(index);
                }
                if (currentFlashcard.isRevealed()) {
                    revealedOrNotLabel.setText("Revealed");
                    revealedOrNotLabel.setTextFill(Color.GREEN);
                } else {
                    revealedOrNotLabel.setText("Not Revealed");
                    revealedOrNotLabel.setTextFill(Color.RED);
                }
                progressLabel.setText("Flashcard " + flashcardNum);
                questionLabel.setText(currentFlashcard.getQuestion());
                revealedAnswerLabel.setText("");
            }
            if (currentFlashcard.isRevealed()) {
                revealedAnswerLabel.setText("Correct answer is: " + currentFlashcard.getAnswer());
            } else {
                revealedAnswerLabel.setText("");
            }
        }

        if (pressedButton.getText().equals("Last Question")) {
            revealAnswerButton.setVisible(true);
            resetProgressButton.setVisible(true);
            nextQuestionButton.setVisible(true);
            flashcardNum--;
            if (!displayedFlashcards.contains(currentFlashcard)) {
                flashcards.remove(currentFlashcard);
                displayedFlashcards.add(currentFlashcard);
            } else if (!questionLabel.getText().equals("")) {
                index--;
            }
            progressLabel.setText("Flashcard " + flashcardNum);
            currentFlashcard = displayedFlashcards.get(index);
            if (currentFlashcard.isRevealed()) {
                revealedOrNotLabel.setText("Revealed");
                revealedOrNotLabel.setTextFill(Color.GREEN);
            } else {
                revealedOrNotLabel.setText("Not Revealed");
                revealedOrNotLabel.setTextFill(Color.RED);
            }
            questionLabel.setText(currentFlashcard.getQuestion());
            if (currentFlashcard.isRevealed()) {
                revealedAnswerLabel.setText("Correct answer is: " + currentFlashcard.getAnswer());
            } else {
                revealedAnswerLabel.setText("");
            }
            if (index == 0)
                lastQuestionButton.setVisible(false);
        }

        if (pressedButton.getText().equals("Reveal Answer")) {
            currentFlashcard.setRevealed(true);
            revealedOrNotLabel.setText("Revealed");
            revealedOrNotLabel.setTextFill(Color.GREEN);
            revealedAnswerLabel.setText("Correct answer is: " + currentFlashcard.getAnswer());
        }
    }

    @FXML
    private void backToTableButton() {
        boolean ind = true;
        for (int i = 0; i < flashcards.size(); i++) {
            if (!flashcards.get(i).isRevealed()) {
                ind = false;
                break;
            }
        }
        if (ind) {
            finishedLabel.setText("You finished studying all flashcards!");
            finished = true;
        }
        backToTableButton.setVisible(false);
        resetProgressButton.setVisible(true);
        revealAnswerButton.setVisible(false);
        revealedAnswerLabel.setText("");
        revealedOrNotLabel.setText("");
        questionLabel.setText("");
        table.setVisible(true);
        table.refresh();
    }

    @FXML
    private void backToMenuButton (ActionEvent e) throws IOException {
        boolean ind = true;
        for (int i = 0; i < flashcards.size(); i++) {
            if (!flashcards.get(i).isRevealed()) {
                ind = false;
                break;
            }
        }
        if (ind) {
            finishedLabel.setText("You finished studying all flashcards!");
            finished = true;
        }
        HelloApplication.changeScene(e, "FlashcardsMenu");
    }

    @FXML
    private void resetProgressButton() {
        table.setVisible(false);
        nextQuestionButton.setVisible(true);
        revealAnswerButton.setVisible(true);
        createNewList();
        Random random = new Random();
        int randomIndex = random.nextInt(flashcards.size());
        currentFlashcard = flashcards.get(randomIndex);
        revealedOrNotLabel.setText("Not Revealed");
        revealedOrNotLabel.setTextFill(Color.RED);
        questionLabel.setText(currentFlashcard.getQuestion());
        revealedAnswerLabel.setText("");
        finishedLabel.setText("");
        resumeProgressLabel.setText("");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        HelloApplication.scalingMethod(scalingPane);

        backToTableButton.setVisible(false);
        table.setVisible(false);
        flashcardQuestionColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        revealedColumn.setCellValueFactory(cellData -> {
            boolean isRevealed = cellData.getValue().isRevealed();
            if (isRevealed)
                return new SimpleStringProperty("Yes");
            return new SimpleStringProperty("No");
        });

        if (finished) {
            resumeProgressLabel.setText("");
            createNewList();
            if (flashcards.isEmpty()) {
                noFlashcardsLabel.setText("You haven't created flashcards yet");
                revealAnswerButton.setVisible(false);
                lastQuestionButton.setVisible(false);
                nextQuestionButton.setVisible(false);
                resetProgressButton.setVisible(false);
                progressLabel.setText("");
                finished = true;
            } else {
                Random random = new Random();
                int randomIndex = random.nextInt(flashcards.size());
                currentFlashcard = flashcards.get(randomIndex);
                if (currentFlashcard.isRevealed()) {
                    revealedOrNotLabel.setText("Revealed");
                    revealedOrNotLabel.setTextFill(Color.GREEN);
                } else {
                    revealedOrNotLabel.setText("Not Revealed");
                    revealedOrNotLabel.setTextFill(Color.RED);
                }
                questionLabel.setText(currentFlashcard.getQuestion());
                progressLabel.setText("Flashcard " + flashcardNum);
                finished = false;
            }
        } else {
            table.setVisible(true);
            resumeProgressLabel.setText("Continuing from where you left off");
            lastQuestionButton.setVisible(false);
            nextQuestionButton.setVisible(false);
            revealAnswerButton.setVisible(false);
            ArrayList<Flashcard> arrayList = new ArrayList<>();
            arrayList.addAll(displayedFlashcards);
            arrayList.addAll(flashcards);
            table.setItems(FXCollections.observableArrayList(arrayList));
            table.setRowFactory(tv -> new TableRow<Flashcard>() {
                @Override
                protected void updateItem(Flashcard flashcard, boolean empty) {
                    super.updateItem(flashcard, empty);
                    if (flashcard == null || empty) {
                        setStyle("");
                    } else {
                        if (flashcard.isRevealed()) {
                            setStyle("-fx-background-color: lightgreen;");
                        } else {
                            setStyle("-fx-background-color: salmon;");
                        }
                    }
                }
            });
            table.setOnMouseClicked(event -> {
                Flashcard selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    currentFlashcard = selected;
                    table.setVisible(false);
                    questionLabel.setText(currentFlashcard.getQuestion());
                    if (currentFlashcard.isRevealed()) {
                        revealedOrNotLabel.setText("Revealed");
                        revealedOrNotLabel.setTextFill(Color.GREEN);
                    } else {
                        revealedOrNotLabel.setText("Not Revealed");
                        revealedOrNotLabel.setTextFill(Color.RED);
                    }
                    finishedLabel.setText("");
                    resumeProgressLabel.setText("");
                    resetProgressButton.setVisible(false);
                    backToTableButton.setVisible(true);
                    revealAnswerButton.setVisible(true);
                    if (currentFlashcard.isRevealed()) {
                        revealedAnswerLabel.setText("Correct answer is: " + currentFlashcard.getAnswer());
                    } else {
                        revealedAnswerLabel.setText("");
                    }
                }
            });
            table.refresh();
        }
    }

    public void createNewList() {
        lastQuestionButton.setVisible(false);
        index = -1;
        flashcards.clear();
        displayedFlashcards.clear();
        finished = false;
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
        flashcardNum = 1;
        progressLabel.setText("Flashcard " + flashcardNum);
    }
}