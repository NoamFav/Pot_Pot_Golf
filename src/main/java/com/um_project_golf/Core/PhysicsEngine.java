package com.um_project_golf.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.Utils.Consts;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/**
 * The physics engine class.
 * This class is responsible for handling the physics of the game.
 */
public class PhysicsEngine {

    private static final double g = Consts.GRAVITY; // gravitational constant

    private final double muK_grass; // kinetic friction grass
    private final double muS_grass; // static friction grass
    private final double muK_sand; // kinetic friction sand
    private final double muS_sand; // static friction sand
    private final BiFunction<Double, Double, Double> heightFunction;
    private final HeightMap heightMap;
    @SuppressWarnings("FieldCanBeLocal") private static final double sandLevel = 0.5; // get this value from the constant file

    /**
     * Create a new physics engine.
     * This constructor is deprecated and should not be used.
     * This constructor is kept for backward compatibility.
     * It uses the height function to calculate the height of the ball.
     * Which is already done by the height map.
     *
     * @param height The height function
     * @param muK_grass The kinetic friction of grass
     * @param muS_grass The static friction of grass
     * @param muK_sand The kinetic friction of sand
     * @param muS_sand The static friction of sand
     */
    @Deprecated
    @SuppressWarnings("unused")
    public PhysicsEngine(BiFunction<Double, Double, Double> height, double muK_grass, double muS_grass, double muK_sand, double muS_sand) {
        this.heightFunction = height;
        this.muK_grass = muK_grass;
        this.muS_grass = muS_grass;
        this.muK_sand = muK_sand;
        this.muS_sand = muS_sand;
        this.heightMap = null;
    }


    /**
     * Create a new physics engine.
     *
     * @param heightMap The height map
     * @param muK_grass The kinetic friction of grass
     * @param muS_grass The static friction of grass
     * @param muK_sand The kinetic friction of sand
     * @param muS_sand The static friction of sand
     */
    public PhysicsEngine(HeightMap heightMap, double muK_grass, double muS_grass, double muK_sand, double muS_sand) {
        this.heightFunction = null;
        this.muK_grass = muK_grass;
        this.muS_grass = muS_grass;
        this.muK_sand = muK_sand;
        this.muS_sand = muS_sand;
        this.heightMap = heightMap;
    }

    /**
     * Run the improved Euler method.
     * This method is deprecated and should not be used.
     * This method is kept for backward compatibility.
     * It uses the height function to calculate the height of the ball.
     * Which is already done by the height map.
     *
     * @param initialState The initial state
     * @param stepSize The step size
     * @return The final position
     */
    @Deprecated
    @SuppressWarnings("unused")
    public Vector3f runImprovedEuler(double[] initialState, double stepSize, double totalTime) {
        double[] currentState = initialState;
        currentState = SimpleRK2.simpleImprovedEuler(0, currentState, totalTime, stepSize, this::equationsOfMotion);
        assert heightMap != null;
        float height = heightMap.getHeight(new Vector3f((float) currentState[0], 0, (float) currentState[1]));
        //System.out.println("height" + height);
        return new Vector3f((float) currentState[0], height, (float) currentState[1]);
    }

    /**
     * Run the improved Euler method.
     *
     * @param initialState The initial state
     * @param stepSize The step size
     * @return The final position
     */
    public List<Vector3f> runImprovedEuler(double[] initialState, double stepSize) { // initialState = [x, z, vx, vz]
        List<Vector3f> positions = new ArrayList<>();
        double[] currentState = initialState;
        double magnitudeVelocity;
        double magnitudeAcceleration;
        double currentTime = 0;

        double[] nextStep;
        double[] acceleration;

        do { // Repeat until the ball is completely at rest
            nextStep = SimpleRK2.simpleImprovedEuler(currentTime, currentState, currentTime + stepSize, stepSize, this::equationsOfMotion);
            acceleration = equationsOfMotion(currentTime, currentState);
            magnitudeVelocity = Math.sqrt(nextStep[2] * nextStep[2] + nextStep[3] * nextStep[3]);
            magnitudeAcceleration = Math.sqrt(acceleration[2] * acceleration[2] + acceleration[3] * acceleration[3]);
            currentState = nextStep;
            currentTime += stepSize;

            assert heightMap != null;
            float height = heightMap.getHeight(new Vector3f((float) currentState[0], 0, (float) currentState[1]));
            positions.add(new Vector3f((float) currentState[0], height, (float) currentState[1]));
        } while (magnitudeVelocity >= 1 || magnitudeAcceleration >= 1); // While the ball is not at rest

        return positions;
    }

    /**
     * Provide the equations of motion.
     * Uses the height map to calculate the equations of motion.
     *
     * @param t  The time
     * @param x The state
     * @return The derivatives
     */
    private double @NotNull [] equationsOfMotion(double t, double @NotNull [] x) { // x = [x, z, vx, vz]
        double[] dxdt = new double[4];
        double vx = x[2];
        double vz = x[3];
        double magnitudeVelocity = Math.sqrt(vx * vx + vz * vz);

        double dh_dxValue;
        double dh_dzValue;

        double kineticFriction;
        double staticFriction;

        assert heightMap != null;
        if (heightMap.getHeight(new Vector3f((float) x[0], 0, (float) x[1])) < sandLevel) { // if the ball is on sand
            kineticFriction = this.muK_sand;
            staticFriction = this.muS_sand;
            //System.out.println("sand");
        }
        else { // if the ball is on grass
            kineticFriction = this.muK_grass;
            staticFriction = this.muS_grass;
            //System.out.println("grass");
        }

        dh_dxValue = dh_dxCentredDifferenceMap(x[0], x[1]);
        dh_dzValue = dh_dyCentredDifferenceMap(x[0], x[1]);

        //use the commented code below if you want to use the height function instead of the height map (not recommended)
        // if (heightFunction != null) {
        //     dh_dxValue = dh_dxCentredDifferenceFunction(x[0], x[1]);
        //     dh_dzValue = dh_dyCentredDifferenceFunction(x[0], x[1]);
        // } else {
        //     dh_dxValue = dh_dxCentredDifferenceMap(x[0], x[1]);
        //     dh_dzValue = dh_dyCentredDifferenceMap(x[0], x[1]);
        // }

        // the threshold for when the value of dh_dx is so small that it should be zero
        // Since we're using numerical approximation, it is nearly impossible to become exactly zero.
        if (Math.abs(dh_dxValue) < 0.00001) {
            dh_dxValue = 0;
        }
        if (Math.abs(dh_dzValue) < 0.00001) {
            dh_dzValue = 0;
        }

        if (magnitudeVelocity >= 1) {  // if the magnitude of velocity is not very small, then the ball is not at rest
            dxdt[0] = vx;
            dxdt[1] = vz;

            // Calculate acceleration in x-direction
            dxdt[2] = -g * dh_dxValue - kineticFriction * g * vx / magnitudeVelocity;
            // Calculate acceleration in z-direction
            dxdt[3] = -g * dh_dzValue - kineticFriction * g * vz / magnitudeVelocity;

        } else { // if the ball is at rest
            vx = 0;
            vz = 0;
            dxdt[0] = vx;
            dxdt[1] = vz;

            if (dh_dxValue != 0 || dh_dzValue != 0) { // if the ball is on a slope
                double dh2 = Math.sqrt(dh_dxValue * dh_dxValue + dh_dzValue * dh_dzValue);

                if (staticFriction <= dh2) { // if the friction force does not overcome the downhill force,
                    // the ball will continue to slide
                    dxdt[2] = -g * dh_dxValue - kineticFriction * g * dh_dxValue / dh2;
                    dxdt[3] = -g * dh_dzValue - kineticFriction * g * dh_dzValue / dh2;
                }
            } else {
                dxdt[2] = 0;
                dxdt[3] = 0;
            }
        }

        return dxdt; // dxdt = [vx, vz, ax, az]
    }

    /**
     * Calculate the centred difference of the height function in the x-direction.
     * This method is deprecated and should not be used.
     * This method is kept for backward compatibility.
     * It uses the height function to calculate the height of the ball.
     * Which is already done by the height map.
     *
     * @param x The x coordinate
     * @param z The z coordinate
     * @return The derivative
     */
    @Deprecated
    @SuppressWarnings("unused")
    private double dh_dxCentredDifferenceFunction(double x, double z) {
        double stepSize = 0.001;
        assert this.heightFunction != null;
        return (this.heightFunction.apply(x - 2 * stepSize, z) - 8 * this.heightFunction.apply(x - stepSize, z) + 8 * this.heightFunction.apply(x + stepSize, z) - this.heightFunction.apply(x + 2 * stepSize, z)) / (12 * stepSize);
    }

    /**
     * Calculate the centred difference of the height function in the y-direction.
     * This method is deprecated and should not be used.
     * This method is kept for backward compatibility.
     * It uses the height function to calculate the height of the ball.
     * Which is already done by the height map.
     *
     * @param x The x coordinate
     * @param z The z coordinate
     * @return The derivative
     */
    @Deprecated
    @SuppressWarnings("unused")
    private double dh_dyCentredDifferenceFunction(double x, double z) {
        double stepSize = 0.001;
        assert this.heightFunction != null;
        return (this.heightFunction.apply(x, z - 2 * stepSize) - 8 * this.heightFunction.apply(x, z - stepSize) + 8 * this.heightFunction.apply(x, z + stepSize) - this.heightFunction.apply(x, z + 2 * stepSize)) / (12 * stepSize);
    }

    /**
     * Calculate the centred difference of the height map in the x-direction.
     *
     * @param x The x coordinate
     * @param z The z coordinate
     * @return The derivative
     */
    private double dh_dxCentredDifferenceMap(double x, double z) {
        float stepSize = Consts.SIZE_X / (Consts.VERTEX_COUNT - 1);
        assert heightMap != null;
        double h1 = this.heightMap.getHeight(new Vector3f((float) (x - 2 * stepSize), 0, (float) z));
        double h2 = this.heightMap.getHeight(new Vector3f((float) (x - stepSize), 0, (float) z));
        double h3 = this.heightMap.getHeight(new Vector3f((float) (x + stepSize), 0, (float) z));
        double h4 = this.heightMap.getHeight(new Vector3f((float) (x + 2 * stepSize), 0, (float) z));

        return (h1 - 8 * h2 + 8 * h3 - h4) / (12 * stepSize);
    }

    /**
     * Calculate the centred difference of the height map in the y-direction.
     *
     * @param x The x coordinate
     * @param z The z coordinate
     * @return The derivative
     */
    private double dh_dyCentredDifferenceMap(double x, double z) {
        float stepSize = Consts.SIZE_Z / (Consts.VERTEX_COUNT - 1);
        assert heightMap != null;
        double h1 = this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (z - 2 * stepSize)));
        double h2 = this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (z - stepSize)));
        double h3 = this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (z + stepSize)));
        double h4 = this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (z + 2 * stepSize)));

        return (h1 - 8 * h2 + 8 * h3 - h4) / (12 * stepSize);
    }
}
