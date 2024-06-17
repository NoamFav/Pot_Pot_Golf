package com.um_project_golf.Core.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class SimpleRK4 {
    /**
     * Simple RK4 method.
     *
     * @param t0       The initial time.
     * @param y0       The initial state.
     * @param tFinal   The final time.
     * @param stepSize The step size.
     * @param function The function to evaluate the derivative.
     * @return The final state.
     */
    public static double @NotNull [] simpleRK4(double t0, double @NotNull [] y0, double tFinal, double stepSize, BiFunction<Double, double[], double[]> function) {
        double t = t0;
        int numberOfSteps = (int) ((tFinal - t0) / stepSize);
        int length = y0.length;
        double[] y = new double[length];
        
        double[] k1, k2, k3, k4;
        double[] k_tilde = new double[length];

        System.arraycopy(y0, 0, y, 0, length);

        for (int i = 0; i < numberOfSteps; i++) {
            k1 = function.apply(t, y);

            for (int j = 0; j < length; j++) {
                k_tilde[j] = y[j] + 0.5 * stepSize *k1[j];
            }

            k2 = function.apply(t + 0.5 * stepSize, k_tilde);
            
            for (int j = 0; j < length; j++) {
                k_tilde[j] = y[j] + 0.5 * stepSize * k2[j];
            }

            k3 = function.apply(t + 0.5 * stepSize, k_tilde);
            
            for (int j = 0; j < length; j++) {
                k_tilde[j] = y[j] + stepSize * k3[j];
            }
            
            k4 = function.apply(t + stepSize, k_tilde);

            for (int j = 0; j < length; j++) {
                y[j] = y[j] + (stepSize / 6.0) * (k1[j]+ 2 * k2[j] + 2 * k3[j] + k4[j]);
            }
            t += stepSize;
        }
        
        return y;
    }

}
