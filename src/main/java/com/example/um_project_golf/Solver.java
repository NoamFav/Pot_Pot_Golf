package com.example.um_project_golf;

import net.objecthunter.exp4j.Expression;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Solver {
    static InputManagement inputManagement = new InputManagement(); // Initializes the input management

    public List<LinkedHashMap<Double, LinkedHashMap<String, Double>>> solve(double stepSize, double tInitial, double tFinal, HashMap<String, Double> variables, HashMap<String, String> equations)
    {
        // Initialize functions with variables
        HashMap<String, Expression> functionsHard = inputManagement.constructCompleteExpression(equations, variables);
        //HashMap<String, List<InputManagement.Token>> functions = inputManagement.constructCompleteFunctions(equations, variables);

        // Solve using Euler's method
        //HashMap<String, Double> solutionsEuler = EulerSolver.eulerMethod(functions, variables, stepSize, tInitial, tFinal, equations);
        LinkedHashMap<Double, LinkedHashMap<String, Double>> solutionsEulerHard = EulerSolver.eulerMethodHard(functionsHard, variables, stepSize, tInitial, tFinal);
        //solutionsEuler.remove("t",solutionsEuler.get("t"));
        for (LinkedHashMap<String, Double> innerMap : solutionsEulerHard.values()) {
            innerMap.remove("t");
        }

        // Solve using Improved Euler's method
        //HashMap<String, Double> solutionsImprovedEuler = ImprovedEuler.improvedEulerMethod(functions, variables, stepSize, tInitial, tFinal, equations);
        LinkedHashMap<Double, LinkedHashMap<String, Double>> solutionsImprovedEulerHard = ImprovedEuler.improvedEulerMethodHard(functionsHard, variables, stepSize, tInitial, tFinal);
        //solutionsImprovedEuler.remove("t",solutionsImprovedEuler.get("t"));
        for (LinkedHashMap<String, Double> innerMap : solutionsImprovedEulerHard.values()) {
            innerMap.remove("t");
        }

        // Solve using RK4 method
        //HashMap<String, Double> solutionsRK4 = RK4.RK4Method(tInitial, variables, tFinal, functions, stepSize, equations);
        LinkedHashMap<Double, LinkedHashMap<String, Double>> solutionsRK4Hard = RK4.RK4MethodHard(functionsHard, variables, stepSize ,tInitial, tFinal, equations);
        for (LinkedHashMap<String, Double> innerMap : solutionsRK4Hard.values()) {
            innerMap.remove("t");
        }

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(7);

        // Print the solutions
        //print(tFinal, solutionsEuler, solutionsImprovedEuler, solutionsRK4, df, false);

        //print(tFinal, solutionsEulerHard.get(1), solutionsImprovedEulerHard, solutionsRK4Hard, df, true);

        System.out.println(solutionsEulerHard);
        System.out.println(solutionsImprovedEulerHard);
        System.out.println(solutionsRK4Hard);

        return List.of(solutionsEulerHard, solutionsImprovedEulerHard, solutionsRK4Hard);
    }

    private void print(double tFinal, HashMap<String, Double> solutionsEulerHard, HashMap<String, Double> solutionsImprovedEulerHard, HashMap<String, Double> solutionsRK4Hard, DecimalFormat df, boolean hard) {
        for (String solution : solutionsEulerHard.keySet()) {
            Double valueE = solutionsEulerHard.get(solution);
            Double valueIE = solutionsImprovedEulerHard.get(solution);
            Double valueRK4 = solutionsRK4Hard.get(solution);
            System.out.println(hard ? "Hard" : "Normal");
            System.out.println("Euler's method:");
            System.out.println("The value of " + solution + " at t = " + tFinal + " is: " + df.format(valueE));
            System.out.println("Improved Euler's method:");
            System.out.println("The value of " + solution + " at t = " + tFinal + " is: " + df.format(valueIE));
            System.out.println("Runge-Kutta 4th order method:");
            System.out.println("The value of " + solution + " at t = " + tFinal + " is: " + df.format(valueRK4));
        }
    }

    public static void main(String[] args)
    {
        // Step size, initial time and final time
        double stepSize = 0.1;
        double tInitial = 0.0;
        double tFinal = 1.0;

        HashMap<String, Double> variables = new HashMap<>();
        // Placeholders for each variable
        variables.put("t", tInitial);
        variables.put("y", 1.0);
        variables.put("x", 2.5);

        List<String> equations = List.of("yx", "x");


        Solver solver = new Solver();
        //solver.solve(stepSize,tInitial,tFinal,variables, equations);
    }
}
