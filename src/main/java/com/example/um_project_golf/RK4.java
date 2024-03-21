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
            double tFinal,
            HashMap<String, String> equations) {

        int numSteps = (int) Math.ceil((tFinal - tInitial) / stepSize);
        LinkedHashMap<Double, LinkedHashMap<String, Double>> solutions = new LinkedHashMap<>();
        HashMap<String, Double> values = new HashMap<>(initialValues);
        double t = tInitial;

        solutions.put(t, new LinkedHashMap<>(values));

        for (int i = 0; i < numSteps; i++) {
            HashMap<String, Double> k1 = new HashMap<>();
            HashMap<String, Double> k2 = new HashMap<>();
            HashMap<String, Double> k3 = new HashMap<>();
            HashMap<String, Double> k4 = new HashMap<>();
            for (Map.Entry<String, Expression> derivativeEntry : derivatives.entrySet()) {
                String variableName = derivativeEntry.getKey();
                Expression function = derivativeEntry.getValue();

                // Ensure the correct current time and variable values are set before evaluating
                function.setVariable("t", t);
                initialValues.forEach(function::setVariable); // Set initial values for all variables
                double k1Value = function.evaluate();
                k1.put(variableName, k1Value);

                // Calculate k2
                HashMap<String, Double> valuesForK2 = new HashMap<>(values);
                valuesForK2.put(variableName, values.get(variableName) + k1Value * stepSize / 2);
                valuesForK2.put("t", t + stepSize / 2);
                function.setVariable(variableName, valuesForK2.get(variableName));
                function.setVariable("t", valuesForK2.get("t"));
                double k2Value = function.evaluate();
                k2.put(variableName, k2Value);

                // Similar process for k3
                HashMap<String, Double> valuesForK3 = new HashMap<>(values);
                valuesForK3.put(variableName, values.get(variableName) + k2Value * stepSize / 2);
                valuesForK3.put("t", t + stepSize / 2);
                function.setVariable(variableName, valuesForK3.get(variableName));
                function.setVariable("t", valuesForK3.get("t"));
                double k3Value = function.evaluate();
                k3.put(variableName, k3Value);

                // And for k4
                HashMap<String, Double> valuesForK4 = new HashMap<>(values);
                valuesForK4.put(variableName, values.get(variableName) + k3Value * stepSize);
                valuesForK4.put("t", t + stepSize);
                function.setVariable(variableName, valuesForK4.get(variableName));
                function.setVariable("t", valuesForK4.get("t"));
                double k4Value = function.evaluate();
                k4.put(variableName, k4Value);
            }

            for (String variableName : derivatives.keySet()) {
                if (!"t".equals(variableName)) { // Exclude "t" from this update
                    double newValue = values.get(variableName) + stepSize / 6.0 * (k1.get(variableName) + 2 * k2.get(variableName) + 2 * k3.get(variableName) + k4.get(variableName));
                    values.put(variableName, newValue);
                }
            }

            t += stepSize;
            values.put("t", t); // Update the time variable for the next iteration

            derivatives = inputManagement.constructCompleteExpression(equations, values);
        }

        return solutions;
    }


    static InputManagement inputManagement = new InputManagement(); // Initializes the input management
}

