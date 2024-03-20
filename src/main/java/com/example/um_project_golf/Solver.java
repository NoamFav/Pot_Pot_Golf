package com.example.um_project_golf;

import net.objecthunter.exp4j.Expression;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class Solver {
    static InputManagement inputManagement = new InputManagement(); // Initializes the input management

    public void solve(double stepSize, double tInitial, double tFinal, HashMap<String, Double> variables, List<String> equations)
    {
        // Initialize functions with variables
        List<List<InputManagement.Token>> functions = inputManagement.constructCompleteFunctions(equations, variables);
        List<Expression> functionsHard = inputManagement.constructCompleteExpression(equations, variables);

        // Solve using Euler's method
        HashMap<String, Double> solutionsEuler = EulerSolver.eulerMethod(functions, variables, stepSize, tInitial, tFinal, equations);
        HashMap<String, Double> solutionsEulerHard = EulerSolver.eulerMethodHard(functionsHard, variables, stepSize, tInitial, tFinal, equations);
        solutionsEuler.remove("t",solutionsEuler.get("t"));
        solutionsEulerHard.remove("t",solutionsEulerHard.get("t"));

        // Solve using Improved Euler's method
        HashMap<String, Double> solutionsImprovedEuler = ImprovedEuler.improvedEulerMethod(functions, variables, stepSize, tInitial, tFinal, equations);
        HashMap<String, Double> solutionsImprovedEulerHard = ImprovedEuler.improvedEulerMethodHard(functionsHard, variables, stepSize, tInitial, tFinal, equations);
        solutionsImprovedEuler.remove("t",solutionsImprovedEuler.get("t"));
        solutionsImprovedEulerHard.remove("t",solutionsImprovedEulerHard.get("t"));

        // Solve using RK4 method
        HashMap<String, Double> solutionsRK4 = RK4.RK4Method(tInitial, variables, tFinal, functions, stepSize, equations);
        HashMap<String, Double> solutionsRK4Hard = RK4.RK4MethodHard(tInitial, variables, tFinal, functionsHard, stepSize, equations);

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(7);

        // Print the solutions
        print(tFinal, solutionsEuler, solutionsImprovedEuler, solutionsRK4, df, false);

        print(tFinal, solutionsEulerHard, solutionsImprovedEulerHard, solutionsRK4Hard, df, true);
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

        List<String> equations = List.of("y");

        Solver solver = new Solver();
        solver.solve(stepSize,tInitial,tFinal,variables,equations);
    }
}
