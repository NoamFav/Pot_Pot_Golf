package com.example.um_project_golf;
import java.util.function.BiFunction;

public class ImprovedEuler{
    public static double improvedEulerMethod(double x0, double y0, double x, BiFunction<Double, Double, Double> function, double stepSize)
    {
        double xi = x0;
        double y = y0;
        double y_tilde = y0;

        while (xi <= x) {
            y_tilde = y + stepSize * function.apply(x, y);
            y = y + (stepSize / 2) * (function.apply(x, y) + function.apply(x + stepSize, y_tilde));
            xi += stepSize;
        } 

        return y;
    }
    public static void main(String[] args)
    {
        BiFunction<Double, Double, Double> g = (x, y) -> -3.0 * y;
        double x0 = 0;
        double y0 = 1;
        double x = 3;
        double h = 0.00001;
        System.out.println(improvedEulerMethod(x0, y0, x, g, h));

    }
}