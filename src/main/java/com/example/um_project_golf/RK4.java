package com.example.um_project_golf;
import java.util.function.BiFunction;

public class RK4 {
    public static double RK4Method(double x0, double y0, double x, BiFunction<Double, Double, Double> function, double stepSize)
    {
        double xi = x0;
        double y = y0;
        double k1, k2, k3, k4;

        while (xi <= x) {
            k1 = stepSize * function.apply(x, y);
            k2 = stepSize * function.apply(x + stepSize / 2, y + k1 / 2);
            k3 = stepSize * function.apply(x + stepSize / 2, y + k2 / 2);
            k4 = stepSize * function.apply(x + stepSize, y + k3);

            y = y + k1/6 + k2/3 + k3/3 + k4/6;
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
        double h = 0.1;
        System.out.println(RK4Method(x0, y0, x, g, h));

    }
}
