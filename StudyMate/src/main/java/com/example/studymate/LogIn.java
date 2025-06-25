package com.example.studymate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LogIn implements Initializable {
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Label incorrectInfoLabel;
    @FXML
    private CheckBox rememberUserCheckBox;
    @FXML
    private Pane scalingPane;

    public static String username = "";

    @FXML
    private void signUpButton(ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "SignUp");
    }

    @FXML
    private void logInButton(ActionEvent e) throws IOException, SQLException {
        Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
        Statement statement = connection.createStatement();
        statement.execute("use accounts_StudyMate");
        ResultSet resultSet = statement.executeQuery("select * from users_StudyMate");
        username = usernameTextField.getText();
        String password = passwordTextField.getText();
        boolean indicator = false;
        while (resultSet.next()) {
            if (username.equals(resultSet.getString(7))) {
                if (password.equals(resultSet.getString(8))) {
                    FileWriter fileWriter = new FileWriter("Remember User.txt");
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    if (rememberUserCheckBox.isSelected()) {
                        bufferedWriter.write("true");
                        bufferedWriter.write("\n");
                        bufferedWriter.write(username);
                    } else {
                        bufferedWriter.write("false");
                    }
                    bufferedWriter.close();
                    fileWriter.close();
                    HelloApplication.changeScene(e, "Menu");
                    break;
                } else {
                    incorrectInfoLabel.setText("incorrect password");
                    passwordTextField.clear();
                    rememberUserCheckBox.setSelected(false);
                }
                indicator = true;
                break;
            }
        }
        if (!indicator) {
            incorrectInfoLabel.setText("username doesn't exist, try again");
            usernameTextField.clear();
            passwordTextField.clear();
            rememberUserCheckBox.setSelected(false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HelloApplication.scalingMethod(scalingPane);
    }
}
