package com.example.um_project_golf;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main extends Application {

    private final List<TextField> functionFields = new ArrayList<>();
    private final List<TextField> variableValueFields = new ArrayList<>();
    private final List<Label> variableLabels = new ArrayList<>();
    private final List<ComboBox<String>> pickerValues = new ArrayList<>();
    private TextField inputField;
    private ComboBox<String> varMenu;
    private ScrollPane scrollPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        AnchorPane root = new AnchorPane();
        root.setPrefWidth(900);
        root.setPrefHeight(600);
        primaryStage.setResizable(false);

        Scene scene = new Scene(root);
        URL cssUrl = getClass().getResource("Style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("Styling.css not found");
        }

        // ChoiceBox to select a solver
        ChoiceBox<String> solverChoiceBox = new ChoiceBox<>();
        solverChoiceBox.getItems().addAll("Euler solver", "Improved Euler solver", "RK4 solver");
        solverChoiceBox.setLayoutX(28);
        solverChoiceBox.setLayoutY(52);
        solverChoiceBox.setPrefWidth(136);
        solverChoiceBox.setPrefHeight(18);
        solverChoiceBox.valueProperty().setValue("Euler solver");

        Slider equationNumSlider = new Slider(1, 10, 1);
        equationNumSlider.setLayoutX(220);
        equationNumSlider.setLayoutY(88);
        equationNumSlider.setPrefWidth(283);
        equationNumSlider.setPrefHeight(39);
        equationNumSlider.setShowTickLabels(true);
        equationNumSlider.setShowTickMarks(true);
        equationNumSlider.setBlockIncrement(1);
        equationNumSlider.setMajorTickUnit(1);
        equationNumSlider.setMinorTickCount(0);
        equationNumSlider.setSnapToTicks(true);


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
        runButton.setFont(new Font("Calisto MT", 23));

        // TextArea for the output
        TextArea outputTextArea = new TextArea();
        outputTextArea.setLayoutX(20);
        outputTextArea.setLayoutY(180);
        outputTextArea.setPrefWidth(153);
        outputTextArea.setPrefHeight(200);
        outputTextArea.setEditable(false);
        outputTextArea.getStyleClass().add("text-area");

        // Create Label and TextField
        Label inputLabel = new Label("Input a differential equation:");
        inputLabel.setFont(new Font("Calisto MT", 13));
        inputField = new TextField();
        functionFields.add(inputField);
        varMenu = new ComboBox<>();
        varMenu.setPromptText(varMenu.getValue());
        pickerValues.add(varMenu);
        VBox inputVBox = new VBox(inputLabel);
        inputVBox.setLayoutX(245);
        inputVBox.setLayoutY(36);
        inputVBox.setPrefHeight(86);

        inputField.textProperty().addListener((observable, oldValue, newValue) -> handleInput(root));

        //warning if you don't input the correct value
        Label warningLabel = new Label();
        warningLabel.setLayoutX(220);
        warningLabel.setLayoutY(130);
        warningLabel.setPrefWidth(283);
        warningLabel.setPrefHeight(20);
        warningLabel.setStyle("-fx-text-fill: #2196F3;");
        warningLabel.setText("");

        //ex graph to change
        NumberAxis timeAxis = new NumberAxis();
        NumberAxis variableAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<>(timeAxis, variableAxis);
        lineChart.setTitle("Evolution of Variables Over Time");
        lineChart.setLayoutX(200);
        lineChart.setLayoutY(200);
        lineChart.setPrefWidth(500);
        lineChart.setPrefHeight(400);

        Label stepSizeLabel = new Label("Input Step Size:");
        stepSizeLabel.setLayoutX(20);
        stepSizeLabel.setLayoutY(390);
        stepSizeLabel.setFont(Font.font("Calisto MT", 12));

        // TextField to input step size
        TextField stepSizeTextField = new TextField();
        stepSizeTextField.setLayoutX(20);
        stepSizeTextField.setLayoutY(410);
        stepSizeTextField.setPrefWidth(100);
        stepSizeTextField.setPromptText("Step size");

        Label startLabel = new Label("Input Start Value:");
        startLabel.setLayoutX(20);
        startLabel.setLayoutY(460);
        startLabel.setFont(Font.font("Calisto MT", 12));

        // TextField for start value input
        TextField startTextField = new TextField();
        startTextField.setLayoutX(20);
        startTextField.setLayoutY(480);
        startTextField.setPrefWidth(100);
        startTextField.setPromptText("Start value");

        // Label for end value input
        Label endLabel = new Label("Input End Value:");
        endLabel.setLayoutX(20);
        endLabel.setLayoutY(530);
        endLabel.setFont(Font.font("Calisto MT", 12));

        // TextField for end value input
        TextField endTextField = new TextField();
        endTextField.setLayoutX(20);
        endTextField.setLayoutY(550);
        endTextField.setPrefWidth(100);
        endTextField.setPromptText("End value");

        AnchorPane leftAnchorPane = new AnchorPane();
        leftAnchorPane.setLayoutX(0);
        leftAnchorPane.setPrefWidth(200);
        leftAnchorPane.setPrefHeight(600);
        leftAnchorPane.getStyleClass().add("pane");

        Pattern validDoubleText = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");
        UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
            if (validDoubleText.matcher(change.getControlNewText()).matches()) {
                return change;
            } else {
                return null;
            }
        };
        TextFormatter<Double> doubleFormatter = new TextFormatter<>(new DoubleStringConverter(), 0.0, doubleFilter);
        stepSizeTextField.setTextFormatter(doubleFormatter);

        // Label for start value input

        TextFormatter<Double> startDoubleFormatter = new TextFormatter<>(new DoubleStringConverter(), 0.0, doubleFilter);
        startTextField.setTextFormatter(startDoubleFormatter);

        TextFormatter<Double> endDoubleFormatter = new TextFormatter<>(new DoubleStringConverter(), 0.0, doubleFilter);
        endTextField.setTextFormatter(endDoubleFormatter);

        VBox dynamicInputPanel = new VBox(5);
        dynamicInputPanel.setStyle("-fx-padding: 10; -fx-background-color: lightgrey; -fx-border-color: black;");


        Popup inputPopup = new Popup();
        inputPopup.getContent().add(dynamicInputPanel);
        inputPopup.setAutoHide(false);

        AtomicReference<Double> initialX = new AtomicReference<>((double) 0);
        AtomicReference<Double> initialY = new AtomicReference<>((double) 0);

        dynamicInputPanel.setOnMousePressed(event -> {
            initialX.set(event.getSceneX());
            initialY.set(event.getSceneY());
        });

        dynamicInputPanel.setOnMouseDragged(event -> {
            inputPopup.setX(event.getScreenX() - initialX.get());
            inputPopup.setY(event.getScreenY() - initialY.get());
        });

        equationNumSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            for (TextField textField : functionFields) {
                textField.clear();
            }

            varMenu.getItems().clear();

            if (equationNumSlider.getValue() > 1) {
                inputPopup.show(primaryStage, primaryStage.getX() + 50, primaryStage.getY() + 50);
            }

            functionFields.clear();
            functionFields.add(inputField);

            dynamicInputPanel.getChildren().clear(); //clear existing TextFields
            int numberOfEquations = newVal.intValue();
            for (int i = 0; i < numberOfEquations - 1; i++) {
                TextField equationInput = new TextField();
                ComboBox<String> varMenuCopy = new ComboBox<>();
                varMenuCopy.setPromptText(varMenuCopy.getValue());
                pickerValues.add(varMenuCopy);
                equationInput.setPromptText("Equation " + (i + 2));
                equationInput.textProperty().addListener((observable, oldValue, newValue) -> handleInput(root));
                functionFields.add(equationInput);
                HBox hBox = new HBox(5);
                hBox.getChildren().addAll(equationInput, varMenuCopy);
                dynamicInputPanel.getChildren().add(hBox);
            }
        });

        // Button to toggle the popup
        Button togglePopupButton = new Button("Edit Equations");
        togglePopupButton.setOnAction(e -> {
            if (!inputPopup.isShowing() && equationNumSlider.getValue() > 1) {
                inputPopup.show(primaryStage, primaryStage.getX() + 50, primaryStage.getY() + 50);
            } else {
                inputPopup.hide();
            }
        });
        HBox inputHBox = new HBox(5);
        inputHBox.layout();
        inputHBox.getChildren().addAll(inputField, varMenu, togglePopupButton);
        inputVBox.getChildren().add(inputHBox);

        // Add children to the left AnchorPane
        leftAnchorPane.getChildren().addAll(titleLabel, solverChoiceBox, runButton, outputTextArea, stepSizeTextField, stepSizeLabel, startLabel, startTextField, endLabel, endTextField);

        // Create AnchorPane and add children
        root.getChildren().addAll(leftAnchorPane, inputVBox, lineChart, equationNumSlider, warningLabel);

        runButton.setOnAction(event -> {
            HashMap<String, Double> variables = new HashMap<>();
            HashMap<String, String> expressions = new HashMap<>();

            // Collect variables and their values
            for (int i = 0; i < variableLabels.size(); i++) {
                String variable = variableLabels.get(i).getText().split(":")[1].trim();// Adjust based on actual text format
                String valueText = variableValueFields.get(i).getText().trim();

                try {
                    double value = Double.parseDouble(valueText);
                    variables.put(variable, value);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid value for variable: " + variable);
                    // Optionally, notify the user and skip solving
                }
            }

            // Collect equations and their corresponding variable from the pickerValues (ComboBoxes)
            for (int i = 0; i < functionFields.size(); i++) {
                String equation = functionFields.get(i).getText().trim();
                String variable = pickerValues.get(i).getValue(); // Assuming each pickerValue corresponds to each functionField

                if (!equation.isEmpty() && variable != null) {
                    expressions.put(variable, equation);
                }
            }

            // Use the collected data for solving
            double stepSize = Double.parseDouble(stepSizeTextField.getText().trim());
            double tInitial = Double.parseDouble(startTextField.getText().trim());
            double tFinal = Double.parseDouble(endTextField.getText().trim());

            // Call your solver here with variables and expressions
            System.out.println("Variables: " + variables);
            System.out.println("Expressions: " + expressions);

            // Now, use Solver to solve the equations with the collected data
            Solver solver = new Solver();
            List<LinkedHashMap<Double, LinkedHashMap<String, Double>>> solutionLists = solver.solve(stepSize, tInitial, tFinal, variables, expressions);

            // Assuming solutionLists contains Euler, Improved Euler, and RK4 solutions respectively
            LinkedHashMap<Double, LinkedHashMap<String, Double>> solutionsEuler = solutionLists.get(0);
            LinkedHashMap<Double, LinkedHashMap<String, Double>> solutionsImprovedEuler = solutionLists.get(1);
            LinkedHashMap<Double, LinkedHashMap<String, Double>> solutionsRK4 = solutionLists.get(1);

            String choice = solverChoiceBox.getValue();
            boolean euler = choice.equals("Euler solver");
            boolean improvedEuler = choice.equals("Improved Euler solver");
            boolean rk4 = choice.equals("RK4 solver");
            // Update your graph with the new solutions
            GraphSolver graphSolver = new GraphSolver(); // Assuming GraphSolver can be instantiated like this
            LineChart<Number, Number> newLineChart = graphSolver.createGraph(solutionsEuler, solutionsImprovedEuler, solutionsRK4, tInitial, euler, improvedEuler, rk4);

            newLineChart.setTitle("Graph of y against t");
            lineChart.getData().clear();
            lineChart.getData().addAll(newLineChart.getData());

        });


        primaryStage.setScene(scene);
        primaryStage.setTitle("Group 14 - phase 1");
        primaryStage.show();
    }

    private void handleInput(AnchorPane root) {
        // Clearing previous content
        StringBuilder combinedText = new StringBuilder();
        for (TextField textField : functionFields) {
            combinedText.append(textField.getText());
        }
        String equation = combinedText.toString();

        root.getChildren().remove(scrollPane);
        boolean isPi = false;
        boolean isE = false;

        List<String> nonVariables = List.of("cos", "sin", "tan", "log", "sqrt", "abs");
        for (String nonVariable : nonVariables) {
            if (equation.contains(nonVariable)) {
                equation = equation.replace(nonVariable, "");
            }
        }
        if (equation.contains("pi") || equation.contains("e")) {
            isPi = equation.contains("pi");
            isE = equation.contains("e");

            equation = equation.replace("pi", "");
            equation = equation.replace("e", "");
        }

        // Creating a single GridPane for all variables
        GridPane grid = new GridPane();
        grid.setVgap(10); // Vertical spacing between rows
        grid.setHgap(10); // Horizontal spacing between columns
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.SOMETIMES); // Allows the column to grow if necessary
        ColumnConstraints column2 = new ColumnConstraints(50); // Sets the preferred width for the text fields
        grid.getColumnConstraints().addAll(column1, column2);
        grid.getStyleClass().add("grid-pane");

        Pattern pattern = Pattern.compile("[a-zA-Z]");
        Matcher matcher = pattern.matcher(equation);
        List<String> variables = new ArrayList<>();
        while (matcher.find()) {
            String variable = matcher.group();
            if (!variables.contains(variable)) {
                variables.add(variable);
            }
        }

        int basedIndex = 0;
        // Adding variable labels and text fields to the grid
        if (isE) {
            Label label = new Label("Euler's number: ");
            TextField textField = new TextField();
            textField.setPromptText("2.71828");
            textField.setPrefWidth(50);
            textField.setEditable(false);
            grid.add(label, 0, basedIndex); // Column 0, row 0
            grid.add(textField, 1, basedIndex); // Column 1, row 0
            basedIndex++;
        }
        if (isPi) {
            Label label = new Label("Pi: ");
            TextField textField = new TextField();
            textField.setPromptText("3.14159");
            textField.setPrefWidth(50);
            textField.setEditable(false);
            grid.add(label, 0, basedIndex); // Column 0, row 1
            grid.add(textField, 1, basedIndex); // Column 1, row 1
            basedIndex++;
        }

        for (int i = 0; i < variables.size(); i++) {
            String variable = variables.get(i);

            Label label = new Label("Variable " + (i + 1) + ": " + variable);
            TextField textField = new TextField();
            textField.setPromptText("value");
            textField.setPrefWidth(50);

            grid.add(label, 0, i + basedIndex); // Column 0, row i
            grid.add(textField, 1, i + basedIndex); // Column 1, row i

            variableLabels.add(label);
            variableValueFields.add(textField);
        }
        for (Label variableLabel : variableLabels) {
            String variable = variableLabel.getText().split(":")[1].trim();
            for (ComboBox<String> pickerValue : pickerValues) {
                if (!pickerValue.getItems().contains(variable)) {
                    pickerValue.getItems().add(variable);
                }
            }
        }

        scrollPane = new ScrollPane();
        scrollPane.setContent(grid);
        scrollPane.setLayoutX(700);
        scrollPane.setLayoutY(50);
        scrollPane.setPrefHeight(500); // Set preferred height, adjust as needed
        scrollPane.setPrefWidth(180); // Set preferred width, adjust as needed
        scrollPane.getStyleClass().add("scroll-pane");

        AnchorPane.setTopAnchor(scrollPane, 50.0); // Adjust layout anchors as needed
        AnchorPane.setLeftAnchor(scrollPane, 700.0); // Adjust layout anchors as needed

        // Adding the scrollPane to the parent only if there are variables to show
        if (!variables.isEmpty() || isPi || isE) {
            root.getChildren().add(scrollPane);
        }
    }

}


