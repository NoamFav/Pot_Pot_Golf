package com.example.um_project_golf;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Slider;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.*;


public class Main extends Application {

    private TextField inputField;
    private List<TextField> variableValueFields = new ArrayList<>();
    private List<Label> variableLabels = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        // ChoiceBox to select a solver
        ChoiceBox<String> solverChoiceBox = new ChoiceBox<>();
        solverChoiceBox.getItems().addAll("Euler solver", "Other solver");
        solverChoiceBox.setLayoutX(28);
        solverChoiceBox.setLayoutY(52);
        solverChoiceBox.setPrefWidth(136);
        solverChoiceBox.setPrefHeight(18);


        Label titleLabel = new Label("Group 14");
        titleLabel.setLayoutX(47);
        titleLabel.setLayoutY(14);
        titleLabel.setFont(new Font("Tahoma Bold", 16));

        // Create Button to run the app
        Button runButton = new Button("Run");
        runButton.setLayoutX(60);
        runButton.setLayoutY(104);
        runButton.setPrefWidth(78);
        runButton.setPrefHeight(52);
        runButton.setStyle("-fx-background-color: #486E3F; -fx-border-color: #FFFFFF;");
        runButton.setFont(new Font("Calisto MT", 23));

        // TextArea for the output
        TextArea outputTextArea = new TextArea();
        outputTextArea.setLayoutX(20);
        outputTextArea.setLayoutY(180);
        outputTextArea.setPrefWidth(153);
        outputTextArea.setPrefHeight(200);
        outputTextArea.setEditable(false);

        // Create Label and TextField
        Label inputLabel = new Label("Input a differential equation:");
        inputLabel.setFont(new Font("Calisto MT", 13));
        inputField = new TextField();
        inputField.setStyle("-fx-background-color: #FFFFFF;");
        VBox inputVBox = new VBox(inputLabel, inputField);
        inputVBox.setLayoutX(245);
        inputVBox.setLayoutY(36);
        inputVBox.setPrefWidth(202);
        inputVBox.setPrefHeight(86);

        inputField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleInput(newValue);
        });

        // Create empty VBox
        VBox emptyVBox = new VBox();
        emptyVBox.setLayoutX(411);
        emptyVBox.setLayoutY(16);
        emptyVBox.setPrefWidth(74);
        emptyVBox.setPrefHeight(39);

        //ex graph to change
        NumberAxis timeAxis = new NumberAxis();
        NumberAxis variableAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<>(timeAxis, variableAxis);
        lineChart.setTitle("Evolution of Variables Over Time");
        lineChart.setLayoutX(200);
        lineChart.setLayoutY(200);
        lineChart.setPrefWidth(500);
        lineChart.setPrefHeight(400);

        //slider to select the num of equations
        Slider equationNumSlider = new Slider(1, 10, 1);
        equationNumSlider.setLayoutX(220);
        equationNumSlider.setLayoutY(88);
        equationNumSlider.setPrefWidth(283);
        equationNumSlider.setPrefHeight(39);
        equationNumSlider.setStyle("-fx-text-fill: black;");
        equationNumSlider.setShowTickLabels(true);
        equationNumSlider.setShowTickMarks(true);
        equationNumSlider.setBlockIncrement(1);
        equationNumSlider.setMajorTickUnit(1);
        equationNumSlider.setMinorTickCount(0);

        AnchorPane leftAnchorPane = new AnchorPane();
        leftAnchorPane.setLayoutX(0);
        leftAnchorPane.setPrefWidth(200);
        leftAnchorPane.setPrefHeight(600);
        leftAnchorPane.setStyle("-fx-background-color: #5d9448;");

        // Add children to the left AnchorPane
        leftAnchorPane.getChildren().addAll(titleLabel, solverChoiceBox, runButton, outputTextArea);

        // Create AnchorPane and add children
        AnchorPane root = new AnchorPane();
        root.setPrefWidth(800);
        root.setPrefHeight(600);
        root.setStyle("-fx-background-color: #98de7e;");
        root.getChildren().addAll(
                leftAnchorPane,
                inputVBox, emptyVBox, lineChart, equationNumSlider
        );


        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Group 14 - phase 1");
        primaryStage.show();
    }

    private void handleInput(String equation)
    {
        variableLabels.forEach(label -> label.setText(""));
        variableValueFields.forEach(field -> field.setText(""));

        Pattern pattern = Pattern.compile("[a-zA-Z]");
        Matcher matcher = pattern.matcher(equation);
        List<String> variables = new ArrayList<>();
        while (matcher.find())
        {
            String variable = matcher.group();
            if (!variables.contains(variable))
            {
                variables.add(variable);
            }
        }
        //to make both the label and textField go away when you delete a variable
        AnchorPane parent = (AnchorPane) inputField.getParent().getParent();
        parent.getChildren().removeAll(variableLabels);
        parent.getChildren().removeAll(variableValueFields);
        variableLabels.clear();
        variableValueFields.clear();

        for (int i = 0; i < variables.size(); i++)
        {
            String variable = variables.get(i);
            Label label = new Label("Variable " + (i + 1) + ": " + variable);
            TextField textField = new TextField();
            textField.setPromptText("value");
            label.setLayoutX(600);
            label.setLayoutY(50 + i * 30);
            textField.setLayoutX(700);
            textField.setLayoutY(50 + i * 30);
            textField.setPrefWidth(50);


            variableLabels.add(label);
            variableValueFields.add(textField);

            ((AnchorPane) inputField.getParent().getParent()).getChildren().addAll(label, textField);

        }
    }
    }


