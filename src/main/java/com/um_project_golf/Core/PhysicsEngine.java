package com.um_project_golf.Core;

import java.io.File;
import java.io.PrintWriter;
import java.util.function.BiFunction;

import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.Utils.Consts;

import org.joml.Vector3f;

public class PhysicsEngine {

    private static final double g = Consts.GRAVITY; // gravitational constant

    private final double muK_grass; // kinetic friction grass
    private final double muS_grass; // static friction grass
    private final double muK_sand; // kinetic friction sand
    private final double muS_sand; // static friction sand
    private final BiFunction<Double, Double, Double> heightFunction;
    private final HeightMap heightMap;
    private File file = new File("output.txt");
    private StringBuilder sb = new StringBuilder();

    // Physics engine with height function
    public PhysicsEngine(BiFunction<Double, Double, Double> height, double muK_grass, double muS_grass, double muK_sand, double muS_sand) {
        this.heightFunction = height;
        this.muK_grass = muK_grass;
        this.muS_grass = muS_grass;
        this.muK_sand = muK_sand;
        this.muS_sand = muS_sand;
        this.heightMap = null;
    }

    // Physics engine with height map
    public PhysicsEngine(HeightMap heightMap, double muK_grass, double muS_grass, double muK_sand, double muS_sand) {
        this.heightFunction = null;
        this.muK_grass = muK_grass;
        this.muS_grass = muS_grass;
        this.muK_sand = muK_sand;
        this.muS_sand = muS_sand;
        this.heightMap = heightMap;
    }

    public Vector3f runImprovedEuler(double[] initialState, double stepSize, double totalTime) {
        double[] currentState = initialState;
        currentState = SimpleRK2.simpleImprovedEuler(0, currentState, totalTime, stepSize, this::equationsOfMotion);
        assert heightMap != null;
        float height = heightMap.getHeight(new Vector3f((float) currentState[0], 0, (float) currentState[1]));
        System.out.println("height" + height);
        return new Vector3f((float) currentState[0], height, (float) currentState[1]);
    }

    private double[] equationsOfMotion(double t, double[] x) {
        double[] dxdt = new double[4];
        double vx = x[2];
        double vy = x[3];
        double magnitudeVelocity = Math.sqrt(vx * vx + vy * vy);

        sb.append("x: ").append(x[0]).append(", y: ").append(x[1]).append("\n");
        sb.append("vx: ").append(vx).append(", vy: ").append(vy).append("\n");
        sb.append("magnitude: ").append(magnitudeVelocity).append("\n");

        double dh_dxValue;
        double dh_dyValue;

        if (heightFunction != null) {
            dh_dxValue = dh_dxCentredDifferenceFunction(x[0], x[1]);
            dh_dyValue = dh_dyCentredDifferenceFunction(x[0], x[1]);
        } else {
            dh_dxValue = dh_dxCentredDifferenceMap(x[0], x[1]);
            dh_dyValue = dh_dyCentredDifferenceMap(x[0], x[1]);
        }

        sb.append("dh_dx: ").append(dh_dxValue).append(", dh_dy: ").append(dh_dyValue).append("\n");

        if (Math.abs(dh_dxValue) < 0.00001) {
            dh_dxValue = 0;
        }
        if (Math.abs(dh_dyValue) < 0.00001) {
            dh_dyValue = 0;
        }

        if (dh_dxValue == 0 && dh_dyValue == 0) {
            sb.append("dh_dx and dh_dy are zero\n");
        } else {
            sb.append("dh_dx and dh_dy are not zero\n");
        }

        sb.append("dh_dx (after threshold): ").append(dh_dxValue).append(", dh_dy (after threshold): ").append(dh_dyValue).append("\n");

        double minVelocityThreshold = 0.005;
        double dampingFactor = 0.99;

        if (magnitudeVelocity >= minVelocityThreshold) {
            dxdt[0] = vx;
            dxdt[1] = vy;

            dxdt[2] = -g * dh_dxValue - this.muK_grass * g * vx / magnitudeVelocity;
            dxdt[3] = -g * dh_dyValue - this.muK_grass * g * vy / magnitudeVelocity;

            dxdt[2] *= dampingFactor;
            dxdt[3] *= dampingFactor;

            sb.append("Ball is moving\n");
            sb.append("ax: ").append(dxdt[2]).append(", ay: ").append(dxdt[3]).append("\n");
        } else {
            vx = 0;
            vy = 0;
            dxdt[0] = vx;
            dxdt[1] = vy;

            if (dh_dxValue != 0 || dh_dyValue != 0) {
                double dh2 = Math.sqrt(dh_dxValue * dh_dxValue + dh_dyValue * dh_dyValue);

                if (this.muS_grass <= dh2) {
                    dxdt[2] = -g * dh_dxValue - this.muK_grass * g * dh_dxValue / dh2;
                    dxdt[3] = -g * dh_dyValue - this.muK_grass * g * dh_dyValue / dh2;
                }
            } else {
                dxdt[2] = 0;
                dxdt[3] = 0;
            }

            sb.append("Ball is at rest\n");
            sb.append("ax: ").append(dxdt[2]).append(", ay: ").append(dxdt[3]).append("\n");
        }

        try (PrintWriter out = new PrintWriter(file)) {
            out.println(sb.toString());
        } catch (Exception e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        return dxdt;
    }

    // Using five-point centred difference for numerically approximating the derivatives
    private double dh_dxCentredDifferenceFunction(double x, double y) {
        double stepSize = 0.001;
        return (this.heightFunction.apply(x - 2 * stepSize, y) - 8 * this.heightFunction.apply(x - stepSize, y) + 8 * this.heightFunction.apply(x + stepSize, y) - this.heightFunction.apply(x + 2 * stepSize, y)) / (12 * stepSize);
    }

    private double dh_dyCentredDifferenceFunction(double x, double y) {
        double stepSize = 0.001;
        return (this.heightFunction.apply(x, y - 2 * stepSize) - 8 * this.heightFunction.apply(x, y - stepSize) + 8 * this.heightFunction.apply(x, y + stepSize) - this.heightFunction.apply(x, y + 2 * stepSize)) / (12 * stepSize);
    }

    private double dh_dxCentredDifferenceMap(double x, double y) {
        float stepSize = Consts.SIZE_X / (Consts.VERTEX_COUNT - 1);
        assert heightMap != null;
        double h1 = this.heightMap.getHeight(new Vector3f((float) (x - 2 * stepSize), 0, (float) y));
        double h2 = this.heightMap.getHeight(new Vector3f((float) (x - stepSize), 0, (float) y));
        double h3 = this.heightMap.getHeight(new Vector3f((float) (x + stepSize), 0, (float) y));
        double h4 = this.heightMap.getHeight(new Vector3f((float) (x + 2 * stepSize), 0, (float) y));
        double derivative = (h1 - 8 * h2 + 8 * h3 - h4) / (12 * stepSize);

        sb.append("dh_dxCentredDifferenceMap: ")
                .append("h1=").append(h1).append(", ")
                .append("h2=").append(h2).append(", ")
                .append("h3=").append(h3).append(", ")
                .append("h4=").append(h4).append(", ")
                .append("derivative=").append(derivative).append("\n");

        return derivative;
    }

    private double dh_dyCentredDifferenceMap(double x, double y) {
        float stepSize = Consts.SIZE_Z / (Consts.VERTEX_COUNT - 1);
        assert heightMap != null;
        double h1 = this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (y - 2 * stepSize)));
        double h2 = this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (y - stepSize)));
        double h3 = this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (y + stepSize)));
        double h4 = this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (y + 2 * stepSize)));
        double derivative = (h1 - 8 * h2 + 8 * h3 - h4) / (12 * stepSize);

        sb.append("dh_dyCentredDifferenceMap: ")
                .append("h1=").append(h1).append(", ")
                .append("h2=").append(h2).append(", ")
                .append("h3=").append(h3).append(", ")
                .append("h4=").append(h4).append(", ")
                .append("derivative=").append(derivative).append("\n");

        return derivative;
    }


    public static void main(String[] args) {
        // Testing with height map
        HeightMap testMap = new HeightMap();
        testMap.createHeightMap();
        double[] initialState = {0, 0, 1, 1}; // initialState = [x, z, vx, vz]
        double h = 0.1; // Time step
        double totalTime = 5; // Total time
        PhysicsEngine engine = new PhysicsEngine(testMap, 0.08, 0.2, 0.1, 0.3);
        Vector3f finalPosition;
        for (int i = 0; i < 50; i++) {
            finalPosition = engine.runImprovedEuler(initialState, h, totalTime);
            initialState[0] = finalPosition.x;
            initialState[1] = finalPosition.z;
            System.out.println(finalPosition.x + ", " + finalPosition.y + ", " + finalPosition.z);
        }
    }
}
