package com.example.um_project_golf;

import java.util.function.BiFunction;

public class EulerSolver {

    // Euler's method for solving systems of first-order differential equations
    public static double[] eulerMethod(BiFunction<Double, double[], Double>[] derivatives, double[] initialValues, double stepSize, double tInitial, double tFinal)
    {
        int numVariables = initialValues.length; // Number of variables that we want a result for
        int numSteps = (int) Math.ceil((tFinal-tInitial) / stepSize); // Number of steps necessary to get to the final time (Math.ceil just rounds up the number to an integer)
        double[] values = initialValues.clone();

        double t = tInitial;
        double[] temporaryValues = new double[numVariables]; // Stores the results of each step

        for (int i = 0; i < numSteps; i++) {
            // Compute derivatives at current time
            for (int j = 0; j < numVariables; j++) {
                temporaryValues[j] = derivatives[j].apply(t, values);
            }

            // Update values using Euler method
            for (int j = 0; j < numVariables; j++) {
                values[j] += temporaryValues[j] * stepSize;
            }

            // Update time
            t += stepSize;
        }

        return values;
    }

    public static void main(String[] args)
    {
        // Define the system of differential equations
        BiFunction<Double, double[], Double>[] derivatives = new BiFunction[2];
        derivatives[0] = (t, values) -> t + 2 * values[0];  // dy/dt = t + 2y
        derivatives[1] = (t, values) -> -2 * values[1];     // dx/dt = -2x

        // Initial values
        double[] initialValues = {3.0, 2.0}; // y(0) = 3, x(0) = 2
        String[] variables = {"y","x"};

        // step size, initial time and final time
        double stepSize = 0.1;
        double tInitial = 0;
        double tFinal = 2.5;

        // Solve using Euler method
        double[] solution = eulerMethod(derivatives, initialValues, stepSize, tInitial, tFinal);

        // Print the solution
        for (int i = 0 ; i < solution.length ; i++) {
            double value = solution[i];
            System.out.println("The value of " + variables[i] + " at t = " + tFinal + " is: " + value);
        }
    }
}
