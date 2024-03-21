package com.example.um_project_golf;
import net.objecthunter.exp4j.Expression;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RK4 {

    public static HashMap<String, Double> RK4Method(double tInitial, HashMap<String, Double> values, double tFinal, List<List<InputManagement.Token>> derivatives, double stepSize, List<String> equations)
    {
        int numSteps = (int) Math.ceil((tFinal - tInitial) / stepSize); // Number of steps necessary to get to the final time

        HashMap<String, Double> valuesNoTime = new HashMap<>(values); // Values without the time
        valuesNoTime.remove("t");

        double t = tInitial;

        double k1, k2, k3, k4; // Variables for mid-way derivative calculation

        for (int i = 0; i < numSteps; i++) {
            HashMap<String, Double> valuesMid = new HashMap<>(values);
            int j = 0;
            for (List<InputManagement.Token> function : derivatives) {
                HashMap<String, Double> valuesK = new HashMap<>(values); // Values for calculation of the k's
                String variableName = valuesNoTime.keySet().toArray(new String[0])[j];
                List<List<InputManagement.Token>> updatedFunctions = inputManagement.constructCompleteFunctions(equations, values);
                function = updatedFunctions.get(j);
                k1 = inputManagement.doPEMDAS(function); // Compute k1

                // Compute k2
                valuesK.put(variableName, values.get(variableName) + k1 * stepSize / 2);
                valuesK.put("t", t + stepSize / 2);
                updatedFunctions = inputManagement.constructCompleteFunctions(equations, valuesK);
                function = updatedFunctions.get(j);
                k2 = inputManagement.doPEMDAS(function);

                // Compute k3
                valuesK.put(variableName, values.get(variableName) + k2 * stepSize / 2);
                updatedFunctions = inputManagement.constructCompleteFunctions(equations, valuesK);
                function = updatedFunctions.get(j);
                k3 = inputManagement.doPEMDAS(function);

                // Compute k4
                valuesK.put(variableName, values.get(variableName) + stepSize * k3);
                valuesK.put("t", t + stepSize);
                updatedFunctions = inputManagement.constructCompleteFunctions(equations, valuesK);
                function = updatedFunctions.get(j);
                k4 = inputManagement.doPEMDAS(function);

                // Update the variables
                valuesMid.put(variableName, values.get(variableName) + stepSize * (k1 + 2 * k2 + 2 * k3 + k4) / 6);
                j++;
            }
            t += stepSize;
            for (int l = 0; l< valuesNoTime.size(); l++){
                String variableName = valuesNoTime.keySet().toArray(new String[0])[l];
                values.put(variableName, valuesMid.get(variableName));
            }
            values.put("t",t);
        }
        return values;

    }

    public static LinkedHashMap<Double, LinkedHashMap<String, Double>> RK4MethodHard(
            HashMap<String, Expression> derivatives,
            HashMap<String, Double> initialValues,
            double stepSize,
            double tInitial,
            double tFinal) {

        LinkedHashMap<Double, LinkedHashMap<String, Double>> solutions = new LinkedHashMap<>();
        HashMap<String, Double> currentValues = new HashMap<>(initialValues);
        double t = tInitial;
        solutions.put(t, new LinkedHashMap<>(currentValues));

        while (t < tFinal) {
            HashMap<String, Double> k1Values = calculateDerivativeValues(derivatives, currentValues, t);
            HashMap<String, Double> k2Values = calculateDerivativeValues(derivatives, updateIntermediateValues(currentValues, k1Values, stepSize / 2), t + stepSize / 2);
            HashMap<String, Double> k3Values = calculateDerivativeValues(derivatives, updateIntermediateValues(currentValues, k2Values, stepSize / 2), t + stepSize / 2);
            HashMap<String, Double> k4Values = calculateDerivativeValues(derivatives, updateIntermediateValues(currentValues, k3Values, stepSize), t + stepSize);

            // Update current values for all variables except 't'
            for (String var : currentValues.keySet()) {
                if (!"t".equals(var)) { // Skip time variable 't' for direct integration
                    double newValue = currentValues.get(var) + (stepSize / 6.0) * (k1Values.get(var) + 2 * k2Values.get(var) + 2 * k3Values.get(var) + k4Values.get(var));
                    currentValues.put(var, newValue);
                }
            }

            t += stepSize;
            currentValues.put("t", t); // Update the time variable
            solutions.put(t, new LinkedHashMap<>(currentValues)); // Store the solution for the current step
        }

        return solutions;
    }

    private static HashMap<String, Double> calculateDerivativeValues(HashMap<String, Expression> derivatives, HashMap<String, Double> values, double t) {
        HashMap<String, Double> derivativeValues = new HashMap<>();
        for (Map.Entry<String, Expression> entry : derivatives.entrySet()) {
            String varName = entry.getKey();
            Expression derivativeExp = entry.getValue();
            derivativeExp.setVariables(values); // Set all variable values
            derivativeExp.setVariable("t", t); // Set the current time
            double derivativeValue = derivativeExp.evaluate();
            derivativeValues.put(varName, derivativeValue);
        }
        return derivativeValues;
    }

    private static HashMap<String, Double> updateIntermediateValues(HashMap<String, Double> currentValues, HashMap<String, Double> derivativeValues, double delta) {
        HashMap<String, Double> intermediateValues = new HashMap<>();
        for (String var : currentValues.keySet()) {
            if (!"t".equals(var)) { // Skip time variable 't' for direct integration
                double intermediateValue = currentValues.get(var) + delta * derivativeValues.get(var);
                intermediateValues.put(var, intermediateValue);
            }
        }
        return intermediateValues;
    }

    static InputManagement inputManagement = new InputManagement(); // Initializes the input management
}

