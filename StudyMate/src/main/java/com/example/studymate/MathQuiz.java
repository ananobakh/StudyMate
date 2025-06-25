package com.example.studymate;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class MathQuiz implements Initializable {
    @FXML
    private Label questionLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label timesUpLabel;
    @FXML
    private Label finishedAllQuestionsLabel;
    @FXML
    private Label roundAnsLabel;
    @FXML
    private TextField answerTextField;
    @FXML
    private Button answerButton;
    @FXML
    private Button viewResultsButton;
    @FXML
    private RadioButton answer1RadioButton;
    @FXML
    private RadioButton answer2RadioButton;
    @FXML
    private RadioButton answer3RadioButton;
    @FXML
    private RadioButton answer4RadioButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Pane scalingPane;

    private Timeline quizTimer;
    private Timeline stopwatchTimer;
    private ArrayList<MathQuestionAnswer> mathQuizQuestions;
    private ArrayList<MathQuestionAnswer> mathQuestionAnswers;
    public static MathQuestionAnswer currentQuestion;
    private static int numofQuestions;
    private static double progress;

    @FXML
    private void answerButton () throws SQLException {
        Statement statement = null;
        try {
            Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
            statement = connection.createStatement();
            statement.execute("use accounts_StudyMate");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (QuizMenu.mathQuizType.equals("Multiple Choice Test")) {
            if (selectedAnswer() != null) {
                if (currentQuestion.getAnswer().equals(selectedAnswer())) {
                    QuizMenu.correctAnswers++;
                    QuizMenu.pointsForLeaderboard++;
                } else {
                    QuizMenu.wrongAnswers++;
                    ResultSet resultSet = statement.executeQuery("select * from mistakes_StudyMate where username = '" + LogIn.username
                            + "' and question = '" + currentQuestion.getQuestion() + "' and answer = '" + currentQuestion.getAnswer() + "'");
                    String choices = answer1RadioButton.getText() + " " + answer2RadioButton.getText() + " " + answer3RadioButton.getText() + " " + answer4RadioButton.getText();
                    if (resultSet.next()) {
                        int currentQuestionQuantity = resultSet.getInt(4) + 1;
                        statement.executeUpdate("delete from mistakes_StudyMate where username = '" + LogIn.username +
                                "' and question = '" + currentQuestion.getQuestion() + "'");
                        statement.executeUpdate("insert into mistakes_StudyMate values ('"
                                + LogIn.username + "', '" + currentQuestion.getQuestion() + "', '" + currentQuestion.getAnswer() +
                                "', " + currentQuestionQuantity + ", '" + choices + "')");
                    } else {
                        statement.executeUpdate("insert into mistakes_StudyMate values ('" + LogIn.username + "', '" +
                                currentQuestion.getQuestion() + "', '" + currentQuestion.getAnswer() + "', " + 1 + ", '" + choices + "')");
                    }
                }
                mathQuestionAnswers.remove(currentQuestion);
                if (mathQuestionAnswers.isEmpty()) {
                    questionLabel.setText("");
                    answerButton.setVisible(false);
                    viewResultsButton.setVisible(true);
                    timeLabel.setText("");
                    answer1RadioButton.setVisible(false);
                    answer2RadioButton.setVisible(false);
                    answer3RadioButton.setVisible(false);
                    answer4RadioButton.setVisible(false);
                    finishedAllQuestionsLabel.setText("You finished the quiz!");
                    if (quizTimer != null)
                        quizTimer.stop();
                    if (stopwatchTimer != null)
                        stopwatchTimer.stop();
                } else {
                    Random random = new Random();
                    int randomIndex = random.nextInt(mathQuestionAnswers.size());
                    currentQuestion = mathQuestionAnswers.get(randomIndex);
                    questionLabel.setText(currentQuestion.getQuestion());
                    generatingProbableAnswers(currentQuestion.getAnswer());
                    answer1RadioButton.setSelected(false);
                    answer2RadioButton.setSelected(false);
                    answer3RadioButton.setSelected(false);
                    answer4RadioButton.setSelected(false);
                }
                numofQuestions++;
                progress = (double) numofQuestions/QuizMenu.totalQuestions;
                progressBar.setProgress(progress);
            }
        } else {
            if (!answerTextField.getText().isEmpty()) {
                if (answerTextField.getText().equals(currentQuestion.getAnswer())) {
                    QuizMenu.correctAnswers++;
                    QuizMenu.pointsForLeaderboard+=2;
                } else {
                    QuizMenu.wrongAnswers++;
                    ResultSet resultSet = statement.executeQuery("select * from mistakes_StudyMate where username = '" + LogIn.username
                            + "' and question = '" + currentQuestion.getQuestion() + "' and answer = '" + currentQuestion.getAnswer() + "'");
                    if (resultSet.next()) {
                        int currentQuestionQuantity = resultSet.getInt(4) + 1;
                        statement.executeUpdate("delete from mistakes_StudyMate where username = '" + LogIn.username +
                                "' and question = '" + currentQuestion.getQuestion() + "'");
                        statement.executeUpdate("insert into mistakes_StudyMate (username, question, answer, quantity) values ('"
                                + LogIn.username + "', '" + currentQuestion.getQuestion() + "', '" + currentQuestion.getAnswer() +
                                "', " + currentQuestionQuantity + "')");
                    } else {
                        statement.executeUpdate("insert into mistakes_StudyMate (username, question, answer, quantity) values ('"
                                + LogIn.username + "', '" + currentQuestion.getQuestion() + "', '" + currentQuestion.getAnswer()
                                + "', " + 1 + ")");
                    }
                }
                mathQuestionAnswers.remove(currentQuestion);
                if (mathQuestionAnswers.isEmpty()) {
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
                    int randomIndex = random.nextInt(mathQuestionAnswers.size());
                    currentQuestion = mathQuestionAnswers.get(randomIndex);
                    questionLabel.setText(currentQuestion.getQuestion());
                    answerTextField.clear();
                }
                numofQuestions++;
                progress = (double) numofQuestions/QuizMenu.totalQuestions;
                progressBar.setProgress(progress);
            }
        }
    }

    @FXML
    private void viewResultsButton (ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "ViewResultsQuizes");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HelloApplication.scalingMethod(scalingPane);

        QuizMenu.quizType = "Math";
        viewResultsButton.setVisible(false);

        if (QuizMenu.useTimeIsChecked && QuizMenu.timeInfo.equals("timer")) {
            QuizMenu.quizTotalTime = 750;
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
                    answer1RadioButton.setVisible(false);
                    answer2RadioButton.setVisible(false);
                    answer3RadioButton.setVisible(false);
                    answer4RadioButton.setVisible(false);
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

        generateQuestions();
        Random random = new Random();
        mathQuestionAnswers = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            int randomIndex = random.nextInt(mathQuizQuestions.size());
            if (!mathQuestionAnswers.contains(mathQuizQuestions.get(randomIndex))) {
                mathQuestionAnswers.add(mathQuizQuestions.get(randomIndex));
            } else {
                i--;
            }
        }

        QuizMenu.totalQuestions = mathQuestionAnswers.size();
        numofQuestions = 0;
        progress = (double) numofQuestions/QuizMenu.totalQuestions;
        progressBar.setProgress(progress);

        // generating first question
        int randomIndex = random.nextInt(mathQuestionAnswers.size());
        currentQuestion = mathQuestionAnswers.get(randomIndex);
        questionLabel.setText(currentQuestion.getQuestion());
        if (QuizMenu.mathQuizType.equals("Multiple Choice Test")) {
            generatingProbableAnswers(currentQuestion.getAnswer());
            answer1RadioButton.setVisible(true);
            answer2RadioButton.setVisible(true);
            answer3RadioButton.setVisible(true);
            answer4RadioButton.setVisible(true);
            answerTextField.setVisible(false);
            roundAnsLabel.setVisible(false);
        } else {
            answer1RadioButton.setVisible(false);
            answer2RadioButton.setVisible(false);
            answer3RadioButton.setVisible(false);
            answer4RadioButton.setVisible(false);
            answerTextField.setVisible(true);
            roundAnsLabel.setVisible(true);
        }
    }

    public void generateQuestions() {
        mathQuizQuestions = new ArrayList<>();
        mathQuizQuestions.add(simpleArithmeticExpressions('+'));
        mathQuizQuestions.add(simpleArithmeticExpressions('-'));
        mathQuizQuestions.add(simpleArithmeticExpressions('*'));
        mathQuizQuestions.add(simpleArithmeticExpressions('/'));
        mathQuizQuestions.add(simpleLinearEqautions('+', false));
        mathQuizQuestions.add(simpleLinearEqautions('-', false));
        mathQuizQuestions.add(simpleLinearEqautions('*', false));
        mathQuizQuestions.add(simpleLinearEqautions('/', false));
        mathQuizQuestions.add(simpleLinearEqautions('+', true));
        mathQuizQuestions.add(simpleLinearEqautions('-', true));
        mathQuizQuestions.add(percentageOperation());
        mathQuizQuestions.add(percentageOperation());
        mathQuizQuestions.add(findPerimeter(3));
        mathQuizQuestions.add(findPerimeter(4));
        mathQuizQuestions.add(findPerimeter(5));
        mathQuizQuestions.add(findArea(4, "regular"));
        mathQuizQuestions.add(findArea(4, ""));
        mathQuizQuestions.add(findArea(3, "regular"));
        mathQuizQuestions.add(findArea(3, ""));
        mathQuizQuestions.add(findArea(6, "regular"));
        mathQuizQuestions.add(findArea(0, ""));
        mathQuizQuestions.add(surfaceAreaOf3DObjects("cube", "totalArea"));
        mathQuizQuestions.add(surfaceAreaOf3DObjects("cube", ""));
        mathQuizQuestions.add(surfaceAreaOf3DObjects("rectangular parallelepiped", "totalArea"));
        mathQuizQuestions.add(surfaceAreaOf3DObjects("rectangular parallelepiped", ""));
        mathQuizQuestions.add(surfaceAreaOf3DObjects("sphere", ""));
        mathQuizQuestions.add(surfaceAreaOf3DObjects("cylinder", "totalArea"));
        mathQuizQuestions.add(surfaceAreaOf3DObjects("cylinder", ""));
        mathQuizQuestions.add(surfaceAreaOf3DObjects("cone", "totalArea"));
        mathQuizQuestions.add(surfaceAreaOf3DObjects("cone", ""));
        mathQuizQuestions.add(findVolume("right rectangular pyramid"));
        mathQuizQuestions.add(findVolume("cube"));
        mathQuizQuestions.add(findVolume("rectangular parallelepiped"));
        mathQuizQuestions.add(findVolume("sphere"));
        mathQuizQuestions.add(findVolume("cylinder"));
        mathQuizQuestions.add(findVolume("cone"));
        mathQuizQuestions.add(findDistanceBetweenCoordinates("2D"));
        mathQuizQuestions.add(findDistanceBetweenCoordinates("3D"));
    }

    private void generatingProbableAnswers(String correctAns) {
        ArrayList<String> probableAnswers = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            probableAnswers.add("");
        }
        Random random = new Random();
        int answerIndex = random.nextInt(4);
        probableAnswers.set(answerIndex, correctAns);
        for(int i = 0; i < 4; i++) {
            if (i != answerIndex) {
                double probableAns = Double.parseDouble(correctAns) + random.nextInt(-1500, 1500)/100.0; // integer parse int (double unda iyos)
                probableAns = Math.round(probableAns * 100.0) / 100.0;
                if (!probableAnswers.contains(String.valueOf(probableAns))) {
                    probableAnswers.set(i, String.valueOf(probableAns));
                } else {
                    i--;
                }
            }
        }
        answer1RadioButton.setText(probableAnswers.get(0));
        answer2RadioButton.setText(probableAnswers.get(1));
        answer3RadioButton.setText(probableAnswers.get(2));
        answer4RadioButton.setText(probableAnswers.get(3));
    }

    private String selectedAnswer() {
        if (answer1RadioButton.isSelected()) {
            return answer1RadioButton.getText();
        } else if (answer2RadioButton.isSelected()) {
            return answer2RadioButton.getText();
        } else if (answer3RadioButton.isSelected()) {
            return answer3RadioButton.getText();
        } else if (answer4RadioButton.isSelected()){
            return answer4RadioButton.getText();
        } else {
            return null;
        }
    }

    // Generating Algebra Problems
    public MathQuestionAnswer simpleArithmeticExpressions(char operator) {
        // a +-*/ b
        Random random = new Random();
        double a = random.nextInt(10000)/100.0;
        double b = random.nextInt(5000)/100.0;
        double ans;
        if (operator == '+')
            ans = a+b;
        else if (operator == '-')
            ans = a-b;
        else if (operator == '*')
            ans = a*b;
        else
            ans = a/b;
        ans = Math.round(ans * 100.0)/100.0;
        return new MathQuestionAnswer("Calculate: " + a + operator + b, String.valueOf(ans));
    }
    public MathQuestionAnswer simpleLinearEqautions(char operator, boolean isParametric) {
        // ax +-*/ b = c
        Random random = new Random();
        double a;
        if (isParametric) {
            a = random.nextInt(10000)/100.0;
        } else {
            a = 1;
        }
        double b = random.nextInt(10000)/100.0;
        double c = random.nextInt(10000)/100.0;
        double ans;
        if (operator == '+')
            ans = (c-b)/a;
        else if (operator == '-')
            ans = (c+b)/a;
        else if (operator == '*')
            ans = c/b/a;
        else
            ans = c*b/a;
        ans = Math.round(ans * 100.0)/100.0;
        if (a == 1) {
            return new MathQuestionAnswer("Calculate: x" + operator + b + "=" + c, String.valueOf(ans));
        } else {
            return new MathQuestionAnswer("Calculate: " + a +"x" + operator + b + "=" + c, String.valueOf(ans));
        }
    }
    public MathQuestionAnswer percentageOperation() {
        // a%b
        Random random = new Random();
        int a = random.nextInt(100)+1;
        int b = random.nextInt(500);
        double ans = (double) b * (double) a/100;
        ans = Math.round(ans * 100.0)/100.0;
        return new MathQuestionAnswer("Calculate: " + a + "% of " + b, String.valueOf(ans));
    }

    // Generating Geometry Problems
    public MathQuestionAnswer findPerimeter(int numOfSides) {
        Random random = new Random();
        double lengthOfTheSide = random.nextInt(3000)/100.0;
        double ans = lengthOfTheSide*numOfSides;
        ans = Math.round(ans * 100.0)/100.0;
        if (numOfSides == 3)
            return new MathQuestionAnswer("Calculate perimeter of an equilateral triangle if each side is the length of "
                    + lengthOfTheSide, String.valueOf(ans));
        else if (numOfSides == 4)
            return new MathQuestionAnswer("Calculate perimeter of a square if each side is the length of " + lengthOfTheSide,
                    String.valueOf(ans));
        else {
            // circle length
            int r = random.nextInt(30);
            ans = (int) (3.14*2*r);
            return new MathQuestionAnswer("Calculate length (circumference) of a circle if its radius is " + r, String.valueOf(ans));
        }
    }
    public MathQuestionAnswer findArea(int numOfSides, String type) {
        Random random = new Random();
        if (numOfSides == 4) {
            double ans = 0.0;
            if (type.equals("regular")) {
                // square
                double a = random.nextInt(2500)/100.0;
                ans = a*4;
                ans = Math.round(ans * 100.0)/100.0;
                return new MathQuestionAnswer("Calculate area of a square if its side length is " + a, String.valueOf(ans));
            } else {
                // rectangle
                double a = random.nextInt(2500)/100.0;
                double b = random.nextInt(2500)/100.0;
                ans = a*b;
                ans = Math.round(ans * 100.0)/100.0;
                return new MathQuestionAnswer("Calculate area of a rectangle if its sides are " + a + " and " + b,
                        String.valueOf(ans));
            }
        } else if (numOfSides == 3) {
            double ans = 0.0;
            if (type.equals("regular")) {
                // using formula s = a^2*√3/4
                double a = random.nextInt(2500)/100.0;
                ans = a * a * (int) Math.sqrt(3) / 4;
                ans = Math.round(ans * 100.0)/100.0;
                return new MathQuestionAnswer("Calculate area of a equilateral triangle if its side length is " + a, String.valueOf(ans));
            } else {
                // using formula s = a*h/2
                double a = random.nextInt(2500)/100.0;
                double h = random.nextInt(2500)/100.0;
                ans = a*h/2;
                ans = Math.round(ans * 100.0)/100.0;
                return new MathQuestionAnswer("Calculate area of a triangle if its base is " + a + " and height is " + h, String.valueOf(ans));
            }
        } else if (numOfSides == 6 && type.equals("regular")) {
            // using formula 6*a^2*√3/4
            double a = random.nextInt(2500)/100.0;
            double ans = 6 * a * a * Math.sqrt(3) / 4;
            ans = Math.round(ans * 100.0)/100.0;
            return new MathQuestionAnswer("Calculate area of a regular hexagon if its side length is " + a, String.valueOf(ans));
        } else {
            // circle
            double r = random.nextInt(2500)/100.0;
            double ans = 3.14*r*r;
            ans = Math.round(ans * 100.0)/100.0;
            return new MathQuestionAnswer("Calculate area of a circle if its radius is " + r, String.valueOf(ans));
        }
    }
    public MathQuestionAnswer surfaceAreaOf3DObjects(String object, String total) {
        Random random = new Random();
        double ans = 0.0;
        if (object.equals("cube")) {
            if (total.equals("totalArea")) {
                double a = random.nextInt(2000)/100.0;
                ans = 6*a*a;
                ans = Math.round(ans * 100.0)/100.0;
                return new MathQuestionAnswer("Calculate total surface area of a cube with a side of " + a, String.valueOf(ans));
            } else {
                double a = random.nextInt(2000)/100.0;
                ans = 4*a*a;
                ans = Math.round(ans * 100.0)/100.0;
                return new MathQuestionAnswer("Calculate lateral (side) surface area of a cube with a side of " + a, String.valueOf(ans));
            }
        } else if (object.equals("rectangular parallelepiped")) {
            if (total.equals("totalArea")) {
                double a = random.nextInt(2000)/100.0;
                double b = random.nextInt(2000)/100.0;
                double c = random.nextInt(2000)/100.0;
                ans = 2*a*b + 2*b*c + 2*a*c;
                ans = Math.round(ans * 100.0)/100.0;
                return new MathQuestionAnswer("Calculate total surface area of a rectangular parallelepiped with sides of "
                        + a + ", " + b + ", and " + c, String.valueOf(ans));
            } else {
                double a = random.nextInt(2000)/100.0;
                double b = random.nextInt(2000)/100.0;
                double h = random.nextInt(2000)/100.0;
                ans = 2*a*h + 2*b*h;
                ans = Math.round(ans * 100.0)/100.0;
                return new MathQuestionAnswer("Calculate lateral (side) surface area of a rectangular parallelepiped with sides of "
                        + a + " and " + b + ", and height of " + h, String.valueOf(ans));
            }
        } else if (object.equals("sphere")) {
            double r = random.nextInt(2000)/100.0;
            ans = 4*3.14*r*r;
            ans = Math.round(ans * 100.0)/100.0;
            return new MathQuestionAnswer("Calculate surface area of a sphere with a radius of " + r, String.valueOf(ans));
        } else if (object.equals("cylinder")) {
            if (total.equals("totalArea")) {
                double r = random.nextInt(2000)/100.0;
                double h = random.nextInt(2000)/100.0;
                ans = 2*3.14*r*h + 2*3.14*r*r;
                ans = Math.round(ans * 100.0)/100.0;
                return new MathQuestionAnswer("Calculate total surface area of a cylinder with a radius of " + r +
                        " and height of " + h, String.valueOf(ans));
            } else {
                double r = random.nextInt(2000)/100.0;
                double h = random.nextInt(2000)/100.0;
                ans = 2*3.14*r*h;
                ans = Math.round(ans * 100.0)/100.0;
                return new MathQuestionAnswer("Calculate lateral (side) surface area of a cylinder with a radius of " + r +
                        " and height of " + h, String.valueOf(ans));
            }
        } else {
            // cone
            if (total.equals("totalArea")) {
                double r = random.nextInt(2000)/100.0;
                double l = random.nextInt(2000)/100.0;
                ans = 3.14*r*l + 3.14*r*r;
                ans = Math.round(ans * 100.0)/100.0;
                return new MathQuestionAnswer("Calculate total surface area of a cone with a radius of " + r +
                        " and slant height of " + l, String.valueOf(ans));
            } else {
                double r = random.nextInt(2000)/100.0;
                double l = random.nextInt(2000)/100.0;
                ans = 3.14*r*l;
                ans = Math.round(ans * 100.0)/100.0;
                return new MathQuestionAnswer("Calculate lateral (side) surface area of a cone with a radius of " + r +
                        " and slant height of " + l, String.valueOf(ans));
            }
        }
    }
    public MathQuestionAnswer findVolume(String object) {
        Random random = new Random();
        double ans = 0;
        if (object.equals("right rectangular pyramid")) {
            double a = random.nextInt(2000)/100.0;
            double b = random.nextInt(2000)/100.0;
            double h = random.nextInt(2000)/100.0;
            ans = a*b*h/3;
            ans = Math.round(ans * 100.0)/100.0;
            return new MathQuestionAnswer("Calculate volume of a right rectangular pyramid with base width of " + a + ", base length of "
                    + b + " and height of " + h, String.valueOf(ans));
        } else if (object.equals("cube")) {
            double a = random.nextInt(2000)/100.0;
            ans = a*a*a;
            ans = Math.round(ans * 100.0)/100.0;
            return new MathQuestionAnswer("Calculate volume of a cube with a side of " + a, String.valueOf(ans));
        } else if (object.equals("rectangular parallelepiped")) {
            double a = random.nextInt(2000)/100.0;
            double b = random.nextInt(2000)/100.0;
            double c = random.nextInt(2000)/100.0;
            ans = a*b*c;
            ans = Math.round(ans * 100.0)/100.0;
            return new MathQuestionAnswer("Calculate volume of a rectangular parallelepiped with sides of " + a + ", " + b +
                    ", and " + c, String.valueOf(ans));
        } else if (object.equals("sphere")) {
            double r = random.nextInt(2000)/100.0;
            ans = 4*3.14*r*r*r/3;
            ans = Math.round(ans * 100.0)/100.0;
            return new MathQuestionAnswer("Calculate volume of a sphere with a radius of " + r, String.valueOf(ans));
        } else if (object.equals("cylinder")) {
            double r = random.nextInt(2000)/100.0;
            double h = random.nextInt(2000)/100.0;
            ans = 3.14*r*r*h;
            ans = Math.round(ans * 100.0)/100.0;
            return new MathQuestionAnswer("Calculate volume of a cylinder with a radius of " + r + " and height of " + h,
                    String.valueOf(ans));
        } else {
            // cone
            double r = random.nextInt(2000)/100.0;
            double l = random.nextInt(2000)/100.0;
            ans = 3.14*r*r*l/3;
            ans = Math.round(ans * 100.0)/100.0;
            return new MathQuestionAnswer("Calculate volume of a cone with a radius of " + r + " and slant height of " + l,
                    String.valueOf(ans));
        }
    }
    public MathQuestionAnswer findDistanceBetweenCoordinates(String plane) {
        Random random = new Random();
        double ans = 0.0;
        if (plane.equals("2D")) {
            int x1 = random.nextInt(-50, 50);
            int x2 = random.nextInt(-50, 50);
            int y1 = random.nextInt(-50, 50);
            int y2 = random.nextInt(-50, 50);
            ans = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
            ans = Math.round(ans * 100.0)/100.0;
            return new MathQuestionAnswer("Find distance between the points (" + x1 + ", " + y1 + ") and (" + x2 + ", " + y2 + ")",
                    String.valueOf(ans));
        } else {
            // 3D plane
            int x1 = random.nextInt(-50, 50);
            int x2 = random.nextInt(-50, 50);
            int y1 = random.nextInt(-50, 50);
            int y2 = random.nextInt(-50, 50);
            int z1 = random.nextInt(-50, 50);
            int z2 = random.nextInt(-50, 50);
            ans = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2) + Math.pow(z2-z1, 2));
            ans = Math.round(ans * 100.0)/100.0;
            return new MathQuestionAnswer("Find distance between the points (" + x1 + ", " + y1 + ", " + z1 + ") and (" +
                    x2 + ", " + y2 + ", " + z2 + ")", String.valueOf(ans));
        }
    }
}
