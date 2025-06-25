package com.example.studymate;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class HelloApplication extends Application {
    public static String url = "jdbc:mysql://localhost:3306/sys";
    public static String username = "";
    public static String password = ""
;
    public static ArrayList<ArrayList<String>> dictionaryList = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Parent root;
        boolean rememberUser = rememberUser();
        BufferedReader bufferedReader = new BufferedReader(new FileReader("SQL Username & Password"));
        String str = bufferedReader.readLine();
        if (str.equals("false")) {
            root = FXMLLoader.load(getClass().getResource("MySQLLogIn.fxml"));
        } else {
            username = bufferedReader.readLine();
            password = bufferedReader.readLine();
            if (rememberUser) {
                root = FXMLLoader.load(getClass().getResource("Menu.fxml"));
            } else {
                root = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
            }
        }
        bufferedReader.close();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        createDictionaryList();
    }

    public static void changeScene(ActionEvent e, String sceneName) throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource(sceneName + ".fxml"));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(root);
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
    }

    public boolean rememberUser() {
        boolean rememberUser = false;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("Remember User.txt"));
            String str = bufferedReader.readLine();
            if (str.equalsIgnoreCase("false"))
                rememberUser = false;
            else {
                rememberUser = true;
                LogIn.username = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rememberUser;
    }

    public void createDictionaryList() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("Dictionary.txt"));
            String englishWord;
            String georgianWord;
            int spaceIndex;
            String str = bufferedReader.readLine();
            while (str != null) {
                spaceIndex = str.indexOf("\t");
                englishWord = str.substring(0, spaceIndex);
                georgianWord = str.substring(spaceIndex+1);
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(englishWord);
                arrayList.add(georgianWord);
                dictionaryList.add(arrayList);
                str = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void scalingMethod (Pane scalingPane) {
        scalingPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                Runnable rescale = () -> {
                    double scaleX = newScene.getWidth() / 600.0;
                    double scaleY = newScene.getHeight() / 400.0;
                    double scale = Math.min(scaleX, scaleY);
                    scale = Math.min(Math.max(scale, 0.7), 1.5);
                    scalingPane.setScaleX(scale);
                    scalingPane.setScaleY(scale);
                };
                newScene.widthProperty().addListener((obs, oldVal, newVal) -> rescale.run());
                newScene.heightProperty().addListener((obs, oldVal, newVal) -> rescale.run());
                rescale.run();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}