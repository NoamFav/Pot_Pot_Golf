package com.example.um_project_golf;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Slider;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.*;
import javafx.util.converter.DoubleStringConverter;
import net.objecthunter.exp4j.Expression;


public class Main extends Application {

    private TextField inputField;
    private final List<TextField> variableValueFields = new ArrayList<>();
    private final  List<Label> variableLabels = new ArrayList<>();
    private ScrollPane scrollPane;

    private final Map<String, Object> equationDetails = new HashMap<>();

    private Slider equationNumSlider;
    private int numEquationsEntered = 0;
    private int maxEquations = 1;
    private Label warningLabel;

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
        URL cssUrl =  getClass().getResource("Style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("Styling.css not found");
        }

        // ChoiceBox to select a solver
        ChoiceBox<String> solverChoiceBox = new ChoiceBox<>();
        solverChoiceBox.getItems().addAll("Euler solver", "Other solver");
        solverChoiceBox.setLayoutX(28);
        solverChoiceBox.setLayoutY(52);
        solverChoiceBox.setPrefWidth(136);
        solverChoiceBox.setPrefHeight(18);
        solverChoiceBox.valueProperty().setValue("Euler solver");


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
        VBox inputVBox = new VBox(inputLabel, inputField);
        inputVBox.setLayoutX(245);
        inputVBox.setLayoutY(36);
        inputVBox.setPrefWidth(202);
        inputVBox.setPrefHeight(86);

        inputField.textProperty().addListener((observable, oldValue, newValue) -> handleInput(newValue));

        //warning if you don't input the correct value
        warningLabel = new Label();
        warningLabel.setLayoutX(220);
        warningLabel.setLayoutY(130);
        warningLabel.setPrefWidth(283);
        warningLabel.setPrefHeight(20);
        warningLabel.setStyle("-fx-text-fill: #2196F3;");
        warningLabel.setText("");


        inputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Check if the number of equations entered exceeds the limit
                int maxEquations = (int) equationNumSlider.getValue();
                if (numEquationsEntered >= maxEquations) {
                    warningLabel.setText("Number of equations exceeds the limit!");
                    return;
                }

                // Save the equation details and increment the counter
                saveEquationDetails(inputField.getText());
                inputField.clear();
                numEquationsEntered++;

                if (numEquationsEntered == maxEquations) {
                    warningLabel.setText("Number of equations reached the limit!");

                }
            }
        });

        // Create Label and TextField
        inputLabel = new Label("Input a differential equation:");
        inputLabel.setFont(new Font("Calisto MT", 13));
        inputField = new TextField();
        inputVBox = new VBox(inputLabel, inputField);
        inputVBox.setLayoutX(245);
        inputVBox.setLayoutY(36);
        inputVBox.setPrefWidth(202);
        inputVBox.setPrefHeight(86);

        inputField.textProperty().addListener((observable, oldValue, newValue) -> handleInput(newValue));


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

        // Label for step size input
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

        TextFormatter<Double> startDoubleFormatter = new TextFormatter<>(new DoubleStringConverter(), 0.0, doubleFilter);
        startTextField.setTextFormatter(startDoubleFormatter);

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

        TextFormatter<Double> endDoubleFormatter = new TextFormatter<>(new DoubleStringConverter(), 0.0, doubleFilter);
        endTextField.setTextFormatter(endDoubleFormatter);


        //slider to select the num of equations
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

        equationNumSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            numEquationsEntered = 0; // Reset the counter when slider value changes
            maxEquations = newVal.intValue(); // Update the maximum number of equations allowed
            warningLabel.setText(""); // Clear any previous warnings
            if (numEquationsEntered >= maxEquations) {
                inputField.setDisable(true); // Disable input field if limit is reached
            } else {
                inputField.setDisable(false); // Enable input field if below the limit
            }
        });

        AnchorPane leftAnchorPane = new AnchorPane();
        leftAnchorPane.setLayoutX(0);
        leftAnchorPane.setPrefWidth(200);
        leftAnchorPane.setPrefHeight(600);
        leftAnchorPane.getStyleClass().add("pane");


        // Add children to the left AnchorPane
        leftAnchorPane.getChildren().addAll(titleLabel, solverChoiceBox, runButton, outputTextArea, stepSizeTextField, stepSizeLabel, startLabel, startTextField, endLabel, endTextField);

        // Create AnchorPane and add children
        root.getChildren().addAll(
                leftAnchorPane,
                inputVBox, lineChart, equationNumSlider, warningLabel
        );

        runButton.setOnAction(event -> {
            List<String> equations = List.of(inputField.getText());
            HashMap<String, Double> variables = new HashMap<>();
            for (int i = 0; i < variableLabels.size(); i++)
            {
                String variable = variableLabels.get(i).getText().split(":")[1].trim();
                String value = variableValueFields.get(i).getText();
                if (!value.isEmpty())
                {
                    variables.put(variable, Double.parseDouble(value));
                }
            }
            System.out.println(variables);
            System.out.println(equations);

            InputManagement inputManagement = new InputManagement();
            StringBuilder output = new StringBuilder();
            for (String equation : equations)
            {
                if (equation.contains("cos") || equation.contains("sin") || equation.contains("tan") || equation.contains("log") || equation.contains("sqrt") || equation.contains("!") || equation.contains("%") || equation.contains("abs") || equation.contains("e") || equation.contains("pi"))
                {
                    System.out.println("The equation contains a function that is not supported by the simple solver. Using the hard solver instead.");
                    List<Expression> list = inputManagement.constructExpression(List.of(equation), variables); //constructs the expression
                    List<Double> results2 = inputManagement.solveHard(list, variables); //solves the equations
                    System.out.println(results2); //prints the results
                    output.append("The equation contains a function that is not supported by the simple solver. Using the hard solver instead.").append("\n");
                    output.append(equation).append("\n");
                    output.append(results2).append("\n");
                }
                else
                {
                    System.out.println("Equation is supported by the simple solver. Using the simple solver.");
                    List<List<InputManagement.Token>> tokens = inputManagement.getFunctions(List.of(equation)); //constructs the functions
                    System.out.println(tokens); //prints the functions
                    List<Double> results = inputManagement.solve(tokens, variables); //solves the equations
                    System.out.println(results); //prints the results
                    output.append("Equation is supported by the simple solver. Using the simple solver.").append("\n");
                    output.append(equation).append("\n");
                    output.append(results).append("\n");
                }
            }
            outputTextArea.setText(output.toString());
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Group 14 - phase 1");
        primaryStage.show();
    }

    private void handleInput(String equation) {
        // Clearing previous content

        ((AnchorPane) inputField.getParent().getParent()).getChildren().remove(scrollPane);
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
            ((AnchorPane) inputField.getParent().getParent()).getChildren().add(scrollPane);
        }


    }

    private void saveEquationDetails(String equation) {

        List<String> variables = extractVariables(equation);

        // Extract variable values from text fields
        Map<String, Double> variableValues = new HashMap<>();
        for (int i = 0; i < variables.size(); i++) {
            String variable = variables.get(i);
            String value = variableValueFields.get(i).getText();
            try {
                double doubleValue = Double.parseDouble(value);
                variableValues.put(variable, doubleValue);
            } catch (NumberFormatException e) {
                // Handle invalid input
                System.err.println("Invalid value for variable: " + variable);
            }
        }


        equationDetails.put("Equation", equation);
        equationDetails.put("Variables", variables);
        equationDetails.put("VariableValues", variableValues);

        System.out.println("Equation: " + equation);
        System.out.println("Variables: " + variables);
        System.out.println("VariableValues: " + variableValues);
    }

    private List<String> extractVariables(String equation) {
        List<String> variables = new ArrayList<>();
        Pattern pattern = Pattern.compile("[a-zA-Z]");
        Matcher matcher = pattern.matcher(equation);
        while (matcher.find()) {
            String variable = matcher.group();
            if (!variables.contains(variable)) {
                variables.add(variable);
            }
        }
        return variables;
    }
}


