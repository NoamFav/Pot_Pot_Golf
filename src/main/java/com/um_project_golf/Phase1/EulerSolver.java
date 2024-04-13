package com.um_project_golf.Phase1;

import net.objecthunter.exp4j.Expression;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EulerSolver {

    // Euler's method for solving systems of first-order differential equations
    public static HashMap<String, Double> eulerMethod(List<List<InputManagement.Token>> derivatives, HashMap<String, Double> initialValues, double stepSize, double tInitial, double tFinal, List<String> equations)
    {
        int numSteps = (int) Math.ceil((tFinal-tInitial) / stepSize);// Number of steps necessary to get to the final time (Math.ceil just rounds up the number to an integer)

        HashMap<String, Double> values = new HashMap<>(initialValues);// HashMap with all the values
        HashMap<String, Double> valuesNoTime = new HashMap<>(initialValues);// HashMap with the values except time "t"
        valuesNoTime.remove("t" , tInitial);

        double t = tInitial;// Variable for time
        HashMap<String, Double> temporaryValues = new HashMap<>(); // Stores the results of each step

        // Iterate through the times necessary to get to the final time
        for (int i = 0; i <= numSteps; i++) {
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
                    values.put(variableName, t + stepSize); // Makes sure the time is updated in each iteration
                } else {
                    values.put(variableName, values.get(variableName) + temporaryValues.get(variableName) * stepSize); // Updates the variables
                }
            }

            derivatives = inputManagement.constructCompleteFunctions(equations, values); // Update functions with the updated values

            // Update time
            t += stepSize;
        }

        return values;
    }

    public static LinkedHashMap<Double, LinkedHashMap<String, Double>> eulerMethodHard(
            HashMap<String, Expression> derivatives,
            HashMap<String, Double> initialValues,
            double stepSize,
            double tInitial,
            double tFinal) {

        int numSteps = (int) Math.ceil((tFinal - tInitial) / stepSize);
        HashMap<String, Double> values = new HashMap<>(initialValues);
        double t = tInitial;
        LinkedHashMap<Double, LinkedHashMap<String, Double>> solutions = new LinkedHashMap<>();
        solutions.put(t, new LinkedHashMap<>(values));

        for (int i = 0; i < numSteps; i++) {
            HashMap<String, Double> nextValues = new HashMap<>(values);

            // Update dynamic variables
            for (Map.Entry<String, Expression> entry : derivatives.entrySet()) {
                String variableName = entry.getKey();
                Expression function = entry.getValue();
                // Ensure the function is updated with the latest values, including 't'
                function.setVariable("t", t);
                values.forEach(function::setVariable); // Use current values for setting variables in the function

                if (!"t".equals(variableName)) { // Skip "t" as it's manually updated
                    double derivativeValue = function.evaluate();
                    double newValue = values.get(variableName) + stepSize * derivativeValue;
                    nextValues.put(variableName, newValue); // Store updated values in nextValues
                }
            }

            // Update time for the next iteration
            t += stepSize;
            nextValues.put("t", t); // Update 't' in nextValues

            // Store the updated state after each iteration
            solutions.put(t, new LinkedHashMap<>(nextValues));

            // Prepare for the next iteration
            values = nextValues; // Update values map for the next loop iteration
        }

        return solutions;
    }
    static InputManagement inputManagement = new InputManagement(); // Initializes the input management


}
