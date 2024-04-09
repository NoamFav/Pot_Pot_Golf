package com.example.um_project_golf.Phase1;

import net.objecthunter.exp4j.Expression;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImprovedEuler {

    // Improved Euler's method for solving systems of first-order differential equations
    public static HashMap<String, Double> improvedEulerMethod(List<List<InputManagement.Token>> derivatives, HashMap<String, Double> initialValues, double stepSize, double tInitial, double tFinal, List<String> equations)
    {
        int numSteps = (int) Math.ceil((tFinal - tInitial) / stepSize); // Number of steps necessary to get to the final time

        HashMap<String, Double> values = new HashMap<>(initialValues); // HashMap with all the values
        HashMap<String, Double> valuesNoTime = new HashMap<>(initialValues);// HashMap with the values except time "t"
        valuesNoTime.remove("t" , tInitial);

        double t = tInitial; // Variable for time

        // Iterate through the times necessary to get to the final time
        for (int i = 0; i < numSteps; i++) {
            // Compute derivatives at the beginning of the step
            HashMap<String, Double> k1 = new HashMap<>();
            int l = 0;
            for (List<InputManagement.Token> function : derivatives) {
                String variableName = valuesNoTime.keySet().toArray(new String[0])[l];
                k1.put(variableName, inputManagement.doPEMDAS(function));
                l++;
            }

            // Compute normal Euler
            HashMap<String, Double> valuesMid = new HashMap<>(valuesNoTime);
            valuesMid.replaceAll((n, v) -> values.get(n) + k1.get(n) * stepSize);
            valuesMid.put("t",t + stepSize);

            HashMap<String, Double> k2 = new HashMap<>();
            List<List<InputManagement.Token>> derivativesMid = inputManagement.constructCompleteFunctions(equations, valuesMid);

            int j = 0;
            for (List<InputManagement.Token> function : derivativesMid) {
                String variableName = valuesNoTime.keySet().toArray(new String[0])[j];
                k2.put(variableName, inputManagement.doPEMDAS(function));
                j++;
            }

            // Update values using improved Euler method
            for (String variableName : values.keySet()) {
                if (variableName.equals("t")) {
                    values.put(variableName, t + stepSize); // Update time
                } else {
                    double newValue = values.get(variableName) + stepSize * (k1.get(variableName) + k2.get(variableName)) / 2;
                    values.put(variableName, newValue); // Update variable
                }
            }

            derivatives = inputManagement.constructCompleteFunctions(equations, values); // Update functions with the updated values

            // Update time
            t += stepSize;
        }

        return values;
    }

    public static LinkedHashMap<Double, LinkedHashMap<String, Double>> improvedEulerMethodHard(
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
            // Calculate k1 for each variable
            HashMap<String, Double> k1 = new HashMap<>();
            for (Map.Entry<String, Expression> entry : derivatives.entrySet()) {
                String variableName = entry.getKey();
                Expression function = entry.getValue();

                // Set the current time 't' and all other variable values for the expression
                function.setVariable("t", t);
                values.forEach(function::setVariable); // Use current values for setting variables in the function

                if (!"t".equals(variableName)) { // Exclude "t" from direct updates here
                    double derivativeValue = function.evaluate();
                    k1.put(variableName, derivativeValue);
                }
            }

            // Prepare valuesMid for k2 calculation by estimating mid-point values
            HashMap<String, Double> valuesMid = new HashMap<>(values);
            k1.forEach((var, val) -> valuesMid.put(var, values.get(var) + val * stepSize / 2)); // Estimate mid-point values
            valuesMid.put("t", t + stepSize / 2); // Update time for mid-step

            // Calculate k2 based on the mid-step values
            HashMap<String, Double> k2 = new HashMap<>();
            for (Map.Entry<String, Expression> entry : derivatives.entrySet()) {
                String variableName = entry.getKey();
                Expression function = entry.getValue();

                // Set mid-step values for k2 calculation
                function.setVariable("t", t + stepSize / 2);
                valuesMid.forEach(function::setVariable); // Ensure function uses mid-step values

                if (!"t".equals(variableName)) { // Exclude "t" from direct updates here
                    double derivativeValue = function.evaluate();
                    k2.put(variableName, derivativeValue);
                }
            }

            // Update variables with the average of k1 and k2
            for (String variableName : derivatives.keySet()) {
                if (!variableName.equals("t")) { // Exclude "t" from this update
                    double newValue = values.get(variableName) + stepSize * (k1.get(variableName) + k2.get(variableName)) / 2;
                    values.put(variableName, newValue);
                }
            }

            // Update time for the next iteration
            t += stepSize;
            values.put("t", t); // Now update "t"

            // Store the updated state after each iteration
            solutions.put(t, new LinkedHashMap<>(values));
        }

        return solutions;
    }

    static InputManagement inputManagement = new InputManagement(); // Initializes the input management


}
