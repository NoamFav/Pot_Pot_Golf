package com.um_project_golf.Core;

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

    // public double[] runImprovedEulerFunction(double[] initialState, double stepSize, double totalTime) { // initialState = [x, y, vx, vy]
    //     System.out.println("Initial position: x: " + initialState[0] + ", y: " + initialState[1]);
    //     double[] currentState = initialState;
    //     currentState = SimpleRK2.simpleImprovedEuler(0, currentState, totalTime, stepSize, (t, y) -> this.equationsOfMotion(t, y));
    //     System.out.println("x: " + currentState[0] + ", y: " + currentState[1]);
    //     double[] result = {currentState[0], currentState[1]};
    //     return result;
    // }
    public Vector3f runImprovedEuler(double[] initialState, double stepSize, double totalTime) { // initialState = [x, y, vx, vy]
        double[] currentState = initialState;
        currentState = SimpleRK2.simpleImprovedEuler(0, currentState, totalTime, stepSize, (t, y) -> this.equationsOfMotion(t, y));
        float height = this.heightMap.getHeight(new Vector3f((float) currentState[0], 0, (float) currentState[1]));
        return new Vector3f((float) currentState[0], height, (float) currentState[1]);
    }

    private double[] equationsOfMotion(double t, double[] x) { // x = [x, y, vx, vy], f(x) = [vx, vy, ax, ay]
        double[] dxdt = new double[4];
        double vx = x[2];
        double vy = x[3];
        double magintudeVelocity = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));

        double dh_dxValue;
        double dh_dyValue;

        if (heightFunction != null) {
            dh_dxValue = dh_dxCentredDifferenceFunction(x[0], x[1]);
            dh_dyValue = dh_dyCentredDifferenceFunction(x[0], x[1]);
        }
        else {
            dh_dxValue = dh_dxCentredDifferenceMap(x[0], x[1]);
            dh_dyValue = dh_dyCentredDifferenceMap(x[0], x[1]);
        }

        // threshold for when the value of dh_dx is so small that it should be zero
        // Since we're using numerical approximation, it is nearly impossible to become exactly zero.
        if (Math.abs(dh_dxValue) < 0.00001) {
            dh_dxValue = 0;
        }
        if (Math.abs(dh_dyValue) < 0.00001) {
            dh_dyValue = 0;
        }

        if (magintudeVelocity >= 1) { // if the magnitude of velocity is not very small, then the ball is not at rest

            dxdt[0] = vx;
            dxdt[1] = vy;
            // Calculate acceleration in x-direction
            dxdt[2] = -g * dh_dxValue - this.muK_grass * g * vx / magintudeVelocity;

            // Calculate acceleration in y-direction
            dxdt[3] = -g * dh_dyValue - this.muK_grass * g * vy / magintudeVelocity;
        } else { // if the ball is at rest
            vx = 0;
            vy = 0;
            dxdt[0] = vx;
            dxdt[1] = vy;
            // System.out.println("Ball came to rest");
            if (dh_dxValue != 0 || dh_dyValue != 0) { // if the ball is on a slope
                double dh2 = Math.sqrt(Math.pow(dh_dxValue, 2) + Math.pow(dh_dyValue, 2));
                // System.out.println("Ball is on a slope");
                if (!(this.muS_grass > dh2)) { // if the friction force is not does not overcome the downhill force, the ball will continue to slide
                    // Calculate acceleration in x-direction
                    dxdt[2] = -g * dh_dxValue - this.muK_grass * g * dh_dxValue / dh2;

                    // Calculate acceleration in y-direction
                    dxdt[3] = -g * dh_dyValue - this.muK_grass * g * dh_dyValue / dh2;
                    // System.out.println("Ball continues to slide");
                }
            }
        }

        // System.out.println("vx:" + vx);
        // System.out.println("vy:" + vy);
        // System.out.println("ax:" + dxdt[2]);
        // System.out.println("ay:" + dxdt[3]);
  
        return dxdt;
    }

    // Using five-point centred difference for numerically approximating the derivatives
    // NOTE: if you use these, make sure to include a threshold uinstead of checking for exactly dh_dx = 0 and dh_dy = 0
    private double dh_dxCentredDifferenceFunction(double x, double y) {
        double stepSize = 0.001;
        return (this.heightFunction.apply(x - 2 * stepSize, y) - 8 * this.heightFunction.apply(x - stepSize, y) + 8 * this.heightFunction.apply(x + stepSize, y) - this.heightFunction.apply(x + 2 * stepSize, y)) / (12 * stepSize);
    }

    private double dh_dyCentredDifferenceFunction(double x, double y) {
        double stepSize = 0.001;
        return (this.heightFunction.apply(x, y - 2 * stepSize) - 8 * this.heightFunction.apply(x, y - stepSize) + 8 * this.heightFunction.apply(x, y + stepSize) - this.heightFunction.apply(x, y + 2 * stepSize)) / (12 * stepSize);
    }
    private double dh_dxCentredDifferenceMap(double x, double y) {
        double stepSize = 0.0001;
        return (this.heightMap.getHeight(new Vector3f((float) (x - 2 * stepSize), 0, (float) y)) - 8 * this.heightMap.getHeight(new Vector3f((float)(x - stepSize), 0, (float) y)) + 8 * this.heightMap.getHeight(new Vector3f((float) (x + stepSize), 0, (float) y)) - this.heightMap.getHeight(new Vector3f((float)(x + 2 * stepSize), 0, (float) y))) / (12 * stepSize);
    }

    private double dh_dyCentredDifferenceMap(double x, double y) {
        double stepSize = 0.0001;
        return (this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (y - 2 * stepSize))) - 8 * this.heightMap.getHeight(new Vector3f((float)x, 0, (float) (y - stepSize))) + 8 * this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (y + stepSize))) - this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (y + 2 * stepSize)))) / (12 * stepSize);
    }
    public static void main(String[] args) {

        // Testing with height map
        HeightMap testMap = new HeightMap();
        testMap.createHeightMap();
        double[] initialState = {0, 0, 1, 1}; // initialState = [x, z, vx, vz]
        double h = 0.1; // Time step
        double totalTime = 5; // Total time
        PhysicsEngine engine = new PhysicsEngine(testMap,0.08, 0.2, 0.1, 0.3);
        Vector3f finalPosition;
        for (int i = 0; i < 50; i++) {
        finalPosition = engine.runImprovedEuler(initialState, h, totalTime);
        initialState[0] = finalPosition.x;
        initialState[1] = finalPosition.z;
        //System.out.println("Before:" + finalPosition.x + ", " + finalPosition.y  + ", " + finalPosition.z);
        System.out.println(finalPosition.x + ", " + finalPosition.y  + ", " + finalPosition.z);
        }

        // testing with BiFunction
        // BiFunction<Double, Double, Double> height = (x,y) -> 0.4 * (0.9 - Math.exp(- (x * x + y * y) / 8.0));
        // PhysicsEngine engine = new PhysicsEngine(height, 0.08, 0.2, 0.1, 0.3);
        // double[] initialState = {0, 0, 2, 2}; 
        // double h = 0.1; // Time step
        // double totalTime = 5; // Total time
        // double[] result;
        // for (int i = 0; i < 50; i++) {
        //     result = engine.runImprovedEulerFunction(initialState, h, totalTime);
        //     initialState[0] = result[0];
        //     initialState[1] = result[1];
        // }
    }
    
}