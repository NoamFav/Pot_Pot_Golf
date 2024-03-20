package com.example.um_project_golf;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class Solver {
    static InputManagement inputManagement = new InputManagement(); // Initializes the input management

    public void solve(double stepSize, double tInitial, double tFinal, HashMap<String, Double> variables, List<String> equations)
    {
        // Initialize functions with variables
        List<List<InputManagement.Token>> functions = inputManagement.constructCompleteFunctions(equations, variables);

        // Solve using Euler's method
        HashMap<String, Double> solutionsEuler = EulerSolver.eulerMethod(functions, variables, stepSize, tInitial, tFinal, equations);
        solutionsEuler.remove("t",solutionsEuler.get("t"));

        // Solve using Improved Euler's method
        HashMap<String, Double> solutionsImprovedEuler = ImprovedEuler.improvedEulerMethod(functions, variables, stepSize, tInitial, tFinal, equations);
        solutionsImprovedEuler.remove("t",solutionsImprovedEuler.get("t"));

        // Solve using RK4 method
        HashMap<String, Double> solutionsRK4 = RK4.RK4Method(tInitial, variables, tFinal, functions, stepSize, equations);

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(7);

        // Print the solutions
        for (String solution : solutionsEuler.keySet()) {
            Double valueE = solutionsEuler.get(solution);
            Double valueIE = solutionsImprovedEuler.get(solution);
            Double valueRK4 = solutionsRK4.get(solution);
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

        List<String> equations = List.of("y");

        Solver solver = new Solver();
        solver.solve(stepSize,tInitial,tFinal,variables,equations);
    }
}
