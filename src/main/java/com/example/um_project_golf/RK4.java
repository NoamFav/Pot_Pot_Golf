package com.example.um_project_golf;
import java.util.HashMap;
import java.util.List;

public class RK4 {

    public static HashMap<String, Double> RK4Method(double tInitial, HashMap<String, Double> values, double tFinal, List<List<InputManagement.Token>> derivatives, double stepSize, List<String> equations)
    {
        int numSteps = (int) Math.ceil((tFinal - tInitial) / stepSize); // Number of steps necessary to get to the final time

        HashMap<String, Double> valuesNoTime = new HashMap<>(values); // Values without the time
        valuesNoTime.remove("t");
        HashMap<String, Double> valuesK = new HashMap<>(values); // Values for calculation of the k's

        double t = tInitial;

        double k1, k2, k3, k4; // Variables for mid-way derivative calculation

        for (int i = 0; i < numSteps; i++) {
            int j = 0;
            for (List<InputManagement.Token> function : derivatives) {
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
                values.put(variableName, values.get(variableName) + stepSize * (k1 + 2 * k2 + 2 * k3 + k4) / 6);

                j++;
            }
            t += stepSize;
            values.put("t",t);
        }
        return values;
    }
    static InputManagement inputManagement = new InputManagement(); // Initializes the input management
}

