package com.example.studymate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SignUp implements Initializable {
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField otherCityTextField;
    @FXML
    private Label incorrectInfoLabel;
    @FXML
    private Label successfulSignUpLabel;
    @FXML
    private Label otherCityLabel;
    @FXML
    private Label strongPasswordLabel;
    @FXML
    private ChoiceBox<String> citiesChoiceBox;
    @FXML
    private RadioButton femaleRadioButton;
    @FXML
    private RadioButton maleRadioButton;
    @FXML
    private RadioButton otherRadioButton;
    @FXML
    private DatePicker birthdayDatePicker;
    @FXML
    private Pane scalingPane;

    @FXML
    private void signUpButton(ActionEvent e) throws SQLException, IOException {
        successfulSignUpLabel.setText("");
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String email = emailTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        LocalDate birthDate = birthdayDatePicker.getValue();
        String birthday = null;
        if (birthDate != null) {
            birthday = birthDate.toString();
        }
        String gender = null;
        if (femaleRadioButton.isSelected()) {
            gender = "female";
        } else if (maleRadioButton.isSelected()) {
            gender = "male";
        } else if (otherRadioButton.isSelected()) {
            gender = "other";
        }
        String city = citiesChoiceBox.getValue();
        if (city != null && city.equals("Other")) {
            city = otherCityTextField.getText();
            if (city.equals(""))
                city = null;
        }
        Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
        Statement statement = connection.createStatement();
        statement.execute("use accounts_StudyMate");
        ResultSet resultSet = statement.executeQuery("select * from users_StudyMate");
        boolean indicator = false;
        while (resultSet.next()) {
            if (username.equals(resultSet.getString(7))) {
                incorrectInfoLabel.setText("This username already exists, try again");
                indicator = true;
                usernameTextField.clear();
                break;
            }
        }
        if (!indicator) {
            if (!firstName.isEmpty() && !lastName.isEmpty() && birthday != null && gender != null && city != null && !email.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                if (isStrongPassword(password)) {
                    PreparedStatement preparedStatement = connection.prepareStatement("insert into users_StudyMate values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    preparedStatement.setString(1, firstName);
                    preparedStatement.setString(2, lastName);
                    preparedStatement.setString(3, birthday);
                    preparedStatement.setString(4, gender);
                    preparedStatement.setString(5, city);
                    preparedStatement.setString(6, email);
                    preparedStatement.setString(7, username);
                    preparedStatement.setString(8, password);
                    preparedStatement.setInt(9, 0);
                    preparedStatement.executeUpdate();
                    firstNameTextField.clear();
                    lastNameTextField.clear();
                    otherCityTextField.clear();
                    otherCityLabel.setVisible(false);
                    otherCityTextField.setVisible(false);
                    birthdayDatePicker.setValue(null);
                    femaleRadioButton.setSelected(false);
                    maleRadioButton.setSelected(false);
                    otherRadioButton.setSelected(false);
                    citiesChoiceBox.setValue(null);
                    emailTextField.clear();
                    usernameTextField.clear();
                    passwordTextField.clear();
                    successfulSignUpLabel.setText("You signed up successfully!");
                    incorrectInfoLabel.setText("");
                    strongPasswordLabel.setText("");
                } else {
                    incorrectInfoLabel.setText("Your password is weak");
                    strongPasswordLabel.setText("Your password must contain at least:\n" +
                            "- One uppercase letter\n" +
                            "- One lowercase letter\n" +
                            "- One number\n" +
                            "- One special character\n(~!@#$%^&*()_+=-[]\\{}|;\"':,./<>?`)");
                    passwordTextField.clear();
                }
            } else {
                incorrectInfoLabel.setText("Input all information!");
            }
        }
    }

    @FXML
    private void backToLogInButton (ActionEvent e) throws IOException{
        HelloApplication.changeScene(e, "LogIn");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HelloApplication.scalingMethod(scalingPane);

        otherCityLabel.setVisible(false);
        otherCityTextField.setVisible(false);
        String[] cities = {"Tbilisi", "Batumi", "Qutaisi", "Rustavi", "Telavi", "Mtskheta", "Axalcixe", "Poti", "Other"};
        citiesChoiceBox.getItems().addAll(cities);
        citiesChoiceBox.setOnAction(e -> {
            if (citiesChoiceBox.getValue() != null) {
                boolean ind = citiesChoiceBox.getValue().equals("Other");
                if (ind) {
                    otherCityLabel.setVisible(true);
                    otherCityTextField.setVisible(true);
                } else {
                    otherCityLabel.setVisible(false);
                    otherCityTextField.setVisible(false);
                }
            }
        });
    }

    private boolean isStrongPassword(String password) {
        boolean upperCaseInd = false;
        boolean lowerCaseInd = false;
        boolean numberInd = false;
        boolean specialCharInd = false;
        String specialChars = "~!@#$%^&*()_+=-[]\\{}|;\"':,./<>?`";
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i)))
                upperCaseInd = true;
            else if (Character.isLowerCase(password.charAt(i)))
                lowerCaseInd = true;
            if (Character.isDigit(password.charAt(i)))
                numberInd = true;
            if (specialChars.contains(String.valueOf(password.charAt(i))))
                specialCharInd = true;
        }
        if (password.length() >= 8 && upperCaseInd && lowerCaseInd && numberInd && specialCharInd)
            return true;
        return false;
    }
}