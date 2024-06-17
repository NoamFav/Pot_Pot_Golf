package com.um_project_golf.Core.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

/**
 * Simple Runge-Kutta 2nd order method.
 */
public class SimpleRK2 {    

    /**
     * Simple improved Euler method.
     *
     * @param t0       The initial time.
     * @param y0       The initial state.
     * @param tFinal   The final time.
     * @param stepSize The step size.
     * @param function The function to evaluate the derivative.
     * @return The final state.
     */
    public static double @NotNull [] simpleImprovedEuler(double t0, double @NotNull [] y0, double tFinal, double stepSize, BiFunction<Double, double[], double[]> function) {
        double t = t0;
        int numberOfSteps = (int) ((tFinal - t0) / stepSize);
        int length = y0.length;
        double[] y = new double[length];
        double[] y_tilde = new double[length];
        double[] dy1, dy2;

        System.arraycopy(y0, 0, y, 0, length);

        for (int i = 0; i < numberOfSteps; i++) {
            dy1 = function.apply(t, y);
            for (int j = 0; j < length; j++) {
                y_tilde[j] = y[j] + stepSize * dy1[j];
            }
            dy2 = function.apply(t + stepSize, y_tilde);
            for (int j = 0; j < length; j++) {
                y[j] = y[j] + stepSize / 2 * (dy1[j] + dy2[j]);
            }
            t += stepSize;
        }

        return y;
    }
}



