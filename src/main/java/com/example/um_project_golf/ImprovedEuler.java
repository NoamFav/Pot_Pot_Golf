package com.example.um_project_golf;
import java.util.HashMap;
import java.util.List;

public class ImprovedEuler {

    // Improved Euler's method for solving systems of first-order differential equations
    public static HashMap<String, Double> improvedEulerMethod(List<List<InputManagement.Token>> derivatives, HashMap<String, Double> initialValues, double stepSize, double tInitial, double tFinal, List<String> equations) {
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
            System.out.println("k1:"+k1);

            // Compute derivatives at the midpoint of the step
            HashMap<String, Double> valuesMid = new HashMap<>(valuesNoTime);
            for (String variableName : valuesMid.keySet()) {
                valuesMid.put(variableName, values.get(variableName) + k1.get(variableName) * stepSize);
            }
            valuesMid.put("t",t + stepSize);
            System.out.println("values mid:"+valuesMid);

            HashMap<String, Double> k2 = new HashMap<>();
            List<List<InputManagement.Token>> derivativesMid = inputManagement.constructCompleteFunctions(equations, valuesMid);
            System.out.println("derivatives mid:"+derivativesMid);
            int j = 0;
            for (List<InputManagement.Token> function : derivativesMid) {
                String variableName = valuesNoTime.keySet().toArray(new String[0])[j];
                System.out.println(variableName);
                k2.put(variableName, inputManagement.doPEMDAS(function));
                j++;
            }
            System.out.println("k2:"+k2);

            // Update values using improved Euler method
            for (String variableName : values.keySet()) {
                if (variableName.equals("t")) {
                    values.put(variableName, t + stepSize); // Update time
                } else {
                    double newValue = values.get(variableName) + stepSize * (k1.get(variableName) + k2.get(variableName)) / 2;
                    values.put(variableName, newValue); // Update variable
                }
            }
            System.out.println("values:"+values);

            derivatives = inputManagement.constructCompleteFunctions(equations, values);

            // Update time
            t += stepSize;
        }

        return values;
    }

    static InputManagement inputManagement = new InputManagement(); // Initializes the input management


}
