package com.example.um_project_golf;

import javafx.application.Application;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GraphSolver extends Application {
    public LineChart<Number,Number> createGraph(LinkedHashMap<Double, LinkedHashMap<String, Double>> solutionsEuler, LinkedHashMap<Double, LinkedHashMap<String, Double>> solutionsImprovedEuler, LinkedHashMap<Double, LinkedHashMap<String, Double>> solutionsRK4, DecimalFormat df, double tInitial, double tFinal, double StepSize, boolean hard) {
        LineChart<Number, Number> lineChart = new LineChart<>(new NumberAxis(), new NumberAxis());

        XYChart.Data<Number, Number> dataEuler;
        XYChart.Data<Number, Number> dataImprovedEuler;
        XYChart.Data<Number, Number> dataRK4;
        XYChart.Series<Number, Number> seriesEuler;
        XYChart.Series<Number, Number> seriesImprovedEuler;
        XYChart.Series<Number, Number> seriesRK4;

        for (String variable : solutionsEuler.get(tInitial).keySet()) {
            seriesEuler = new XYChart.Series<>();
            seriesImprovedEuler = new XYChart.Series<>();
            seriesRK4 = new XYChart.Series<>();
            seriesEuler.setName(variable);
            seriesImprovedEuler.setName(variable);
            seriesRK4.setName(variable);

            for (Map.Entry<Double, LinkedHashMap<String, Double>> entry : solutionsEuler.entrySet()) {
                double t = entry.getKey();
                LinkedHashMap<String, Double> solutionEuler = entry.getValue();
                LinkedHashMap<String, Double> solutionImprovedEuler = solutionsImprovedEuler.get(t);
                LinkedHashMap<String, Double> solutionRK4 = solutionsRK4.get(t);

                if (solutionEuler != null && solutionImprovedEuler != null && solutionRK4 != null) {
                    dataEuler = new XYChart.Data<>(t, solutionEuler.get(variable));
                    seriesEuler.getData().add(dataEuler);

                    dataImprovedEuler = new XYChart.Data<>(t, solutionImprovedEuler.get(variable));
                    seriesImprovedEuler.getData().add(dataImprovedEuler);

                    dataRK4 = new XYChart.Data<>(t, solutionRK4.get(variable));
                    seriesRK4.getData().add(dataRK4);
                }
            }
            lineChart.getData().add(seriesEuler);
            lineChart.getData().add(seriesImprovedEuler);
            lineChart.getData().add(seriesRK4);
        }

        return lineChart;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Solver solver = new Solver();
        LinkedHashMap<Double, LinkedHashMap<String, Double>> solutionsEuler = new LinkedHashMap<>();
        LinkedHashMap<Double, LinkedHashMap<String, Double>> solutionsImprovedEuler = new LinkedHashMap<>();
        LinkedHashMap<Double, LinkedHashMap<String, Double>> solutionsRK4 = new LinkedHashMap<>();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(7);
        double stepSize = 0.1;
        double tInitial = 0.0;
        double tFinal = 1.0;

        HashMap<String, Double> variables = new HashMap<>();
        // Placeholders for each variable
        variables.put("t", tInitial);
        variables.put("y", 1.0);
        variables.put("x", 2.5);

        List<String> equations = List.of("yx", "x");

        List<LinkedHashMap<Double, LinkedHashMap<String, Double>>> plot = solver.solve(stepSize, tInitial, tFinal, variables, equations);
        solutionsEuler = plot.get(0);
        solutionsImprovedEuler = plot.get(1);
        solutionsRK4 = plot.get(2);

        LineChart<Number, Number> lineChart = createGraph(solutionsEuler, solutionsImprovedEuler, solutionsRK4, df, 0, 10, 0.1, true);
        lineChart.setTitle("Graph of y against t");
        primaryStage.setScene(new javafx.scene.Scene(lineChart, 800, 600));
        primaryStage.show();
    }
}
