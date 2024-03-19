package com.example.um_project_golf;

import java.util.HashMap;
import java.util.List;

public class EulerSolver {

    // Euler's method for solving systems of first-order differential equations
    public static HashMap<String, Double> eulerMethod(List<List<InputManagement.Token>> derivatives, HashMap<String, Double> initialValues, double stepSize, double tInitial, double tFinal)
    {
        int numSteps = (int) Math.ceil((tFinal-tInitial) / stepSize);// Number of steps necessary to get to the final time (Math.ceil just rounds up the number to an integer)

        HashMap<String, Double> values = new HashMap<>(initialValues);// HashMap with all the values
        HashMap<String, Double> valuesNoTime = new HashMap<>(initialValues);// HashMap with the values except time "t"
        valuesNoTime.remove("t" , tInitial);

        double t = tInitial;// Variable for time
        HashMap<String, Double> temporaryValues = new HashMap<>(); // Stores the results of each step

        // Iterate through the times necessary to get to the final time
        for (int i = 0; i < numSteps; i++) {
            // Compute derivatives at current time
            int j=0; // Counter for position of variable name
            // Iterate through the functions
            for (List<InputManagement.Token> function : derivatives)
            {
                String variableName = valuesNoTime.keySet().toArray(new String[0])[j];
                temporaryValues.put(variableName, inputManagement.doPEMDAS(function)); // Calculate the derivatives for current time for each variable
                j++;
            }

            // Update values using Euler method
            for (String variableName : values.keySet()) {
                if (variableName.equals("t")) {
                    values.put(variableName, t); // Makes sure the time is updated in each iteration
                } else {
                    values.put(variableName, values.get(variableName) + temporaryValues.get(variableName) * stepSize); // Updates the variables
                }
            }

            derivatives = inputManagement.constructFunctions(equations, values); // Update functions with the updated values

            // Update time
            t += stepSize;
        }

        return values;
    }

    static InputManagement inputManagement = new InputManagement(); // Initializes the input management
    static List<String> equations = List.of("2t","t + 2y"); // Initializes the equations
    static HashMap<String, Double> variables = new HashMap<>(); // Initializes the variables

    public static void main(String[] args)
    {
        // Step size, initial time and final time
        double stepSize = 0.1;
        double tInitial = 2.0;
        double tFinal = 2.5;

        // Placeholders for each variable
        variables.put("t", tInitial);
        //variables.put("a", 1.0);
        //variables.put("b", 3.0);
        variables.put("x", 1.0);
        variables.put("y", 3.0);
        //variables.put("x", 0.0);
        //variables.put("x", 0.0);
        //variables.put("x", 0.0);

        // Initialize functions with variables
        List<List<InputManagement.Token>> functions = inputManagement.constructFunctions(equations, variables);

        // Solve using Euler method
        HashMap<String, Double> solutions = eulerMethod(functions, variables, stepSize, tInitial, tFinal);
        solutions.remove("t",solutions.get("t"));

        // Print the solution
        for (String solution : solutions.keySet()) {
            Double value = solutions.get(solution);
            System.out.println("The value of " + solution + " at t = " + tFinal + " is: " + value);
        }
    }
}
