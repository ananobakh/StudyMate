package com.example.studymate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Leaderboard implements Initializable {
    @FXML
    private TableView<User> table;
    @FXML
    private TableColumn<User, Integer> numColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, Integer> pointColumn;
    @FXML
    private RadioButton allUsersRadioButton;
    @FXML
    private Pane scalingPane;

    @FXML
    private void backToMenuButton(ActionEvent e) throws IOException {
        HelloApplication.changeScene(e, "Menu");
    }

    @FXML
    private void selectedRadioButton (ActionEvent e) {
        RadioButton selectedRadioButton = (RadioButton) e.getSource();
        if (selectedRadioButton.getText().equals("All Users")) {
            allUsersIsSelected();
        } else {
            cityIsSelected();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HelloApplication.scalingMethod(scalingPane);

        numColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        pointColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
        table.setRowFactory(tv -> new TableRow<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getUsername().equals(LogIn.username)) {
                    setStyle("-fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });

        allUsersIsSelected();
        allUsersRadioButton.setSelected(true);
    }

    private void allUsersIsSelected() {
        try {
            Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
            Statement statement = connection.createStatement();
            statement.execute("use accounts_StudyMate");
            ResultSet resultSet = statement.executeQuery("select username, totalPoints from users_StudyMate order by totalPoints desc");
            ObservableList<User> users = FXCollections.observableArrayList();
            int num = 1;
            while (resultSet.next()) {
                users.add(new User(resultSet.getString(1), resultSet.getInt(2), num));
                num++;
            }
            table.setItems(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cityIsSelected() {
        try {
            Connection connection = DriverManager.getConnection(HelloApplication.url, HelloApplication.username, HelloApplication.password);
            Statement statement = connection.createStatement();
            statement.execute("use accounts_StudyMate");
            ResultSet resultSet = statement.executeQuery("select city from users_StudyMate where username = '" + LogIn.username + "'");
            resultSet.next();
            String userCity = resultSet.getString(1);
            ResultSet resultSet1 = statement.executeQuery("select username, totalPoints from users_StudyMate where lower(city) = '"
                    + userCity.toLowerCase() + "' order by totalPoints desc");
            ObservableList<User> users = FXCollections.observableArrayList();
            int num = 1;
            while (resultSet1.next()) {
                users.add(new User(resultSet1.getString(1), resultSet1.getInt(2), num));
                num++;
            }
            table.setItems(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
