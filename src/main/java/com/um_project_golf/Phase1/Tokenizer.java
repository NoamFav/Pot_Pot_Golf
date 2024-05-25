package com.um_project_golf.Phase1;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Tokenizer extends Application {

    TextField textField = new TextField();
    Label label = new Label("Output:");
    HashMap<String, Double> variables = new HashMap<>();

    public void start(@NotNull Stage primaryStage) {
        Pane pane = new Pane();
        Scene scene = new Scene(pane, 800, 600);
        textField.setPromptText("enter variable (x = 5) press enter to tokenize");
        textField.setAlignment(Pos.CENTER);
        textField.setPrefSize(200, 50);
        textField.setLayoutX(300);
        textField.setLayoutY(200);
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(200, 50);
        label.setLayoutX(300);
        label.setLayoutY(300);

        pane.getChildren().addAll(textField, label);
        primaryStage.setScene(scene);
        textFieldListener();

        primaryStage.setTitle("Tokenizer");
        primaryStage.show();
    }

    public void tokenize() {
        String[] token = textField.getText().split("=");
        if (token.length == 2) {
            try {
                if (!token[0].trim().matches("[a-zA-Z]+")) {
                    label.setText("Output: Invalid variable name");
                    return;
                }
                if (!token[1].trim().matches("[0-9]+")) {
                    label.setText("Output: Invalid variable value");
                    return;
                }
                variables.put(token[0].trim(), Double.parseDouble(token[1].trim()));
                label.setText("Output: " + variables);
            } catch (NumberFormatException e) {
                label.setText("Output: Invalid input");
            }
        }
    }

    public void textFieldListener() {
        textField.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                tokenize();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
