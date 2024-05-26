package com.um_project_golf.Core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.io.File;


import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.Utils.Consts;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/**
 * The physics engine class.
 * This class is responsible for handling the physics of the game.
 */
@SuppressWarnings("FieldCanBeLocal")
public class PhysicsEngine {

    private static final double GRAVITY = Consts.GRAVITY; // gravitational constant

    private final double muK_grass = Consts.KINETIC_FRICTION_GRASS; // kinetic friction grass
    private final double muS_grass = Consts.STATIC_FRICTION_GRASS; // static friction grass
    private final double muK_sand = Consts.KINETIC_FRICTION_SAND; // kinetic friction sand
    private final double muS_sand = Consts.STATIC_FRICTION_SAND; // static friction sand
    private final BiFunction<Double, Double, Double> heightFunction;
    private final HeightMap heightMap;
    @SuppressWarnings("FieldCanBeLocal") private static final double sandLevel = 0.5; // get this value from the constant file
    private final double VELOCITY_THRESHOLD = 0.05; // get this value from the constant file
    private final double ACCELERATION_THRESHOLD = 0.1; // get this value from the constant file
    private final double DAMPING_COEFFICIENT = 0.1; // get this value from the constant file

    File file = new File("output.txt");
    File file2 = new File("output2.txt");



    StringBuilder sb = new StringBuilder();
    StringBuilder sb2 = new StringBuilder();

    /**
     * Create a new physics engine.
     * This constructor is deprecated and should not be used.
     * This constructor is kept for backward compatibility.
     * It uses the height function to calculate the height of the ball.
     * Which is already done by the height map.
     *
     * @param height The height function
     */
    @Deprecated
    @SuppressWarnings("unused")
    public PhysicsEngine(BiFunction<Double, Double, Double> height) {
        this.heightFunction = height;
        this.heightMap = null;
    }


    /**
     * Create a new physics engine.
     *
     * @param heightMap The height map
     */
    public PhysicsEngine(HeightMap heightMap) {
        this.heightFunction = null;
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
            Vector3f postion = new Vector3f((float) currentState[0], height, (float) currentState[1]);
            positions.add(postion);
            sb2.append("Position: ").append(postion).append("\n");
        } while (magnitudeVelocity >= VELOCITY_THRESHOLD || magnitudeAcceleration >= ACCELERATION_THRESHOLD); // While the ball is not at rest

        try (java.io.FileWriter fw = new java.io.FileWriter(file2, true)) {
            fw.write(sb2.toString());
            sb2.setLength(0);
        } catch (java.io.IOException e) {
            e.printStackTrace(System.err);
        }

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

        double dh_dxValue = dh_dxCentredDifferenceMap(x[0], x[1]);
        double dh_dzValue = dh_dyCentredDifferenceMap(x[0], x[1]);

        double kineticFriction;
        double staticFriction;

        assert heightMap != null;
        if (heightMap.getHeight(new Vector3f((float) x[0], 0, (float) x[1])) < sandLevel) { // if the ball is on sand
            kineticFriction = muK_sand;
            staticFriction = muS_sand;
        } else { // if the ball is on grass
            kineticFriction = muK_grass;
            staticFriction = muS_grass;
        }

        // Threshold for numerical approximation
        if (Math.abs(dh_dxValue) < 0.00001) {
            dh_dxValue = 0;
        }
        if (Math.abs(dh_dzValue) < 0.00001) {
            dh_dzValue = 0;
        }

//        sb.append("Time: ").append(t).append("\n");
//        sb.append("State: ").append(Arrays.toString(x)).append("\n");
//        sb.append("Magnitude velocity: ").append(magnitudeVelocity).append("\n");
//        sb.append("dh_dxValue: ").append(dh_dxValue).append("\n");
//        sb.append("dh_dzValue: ").append(dh_dzValue).append("\n");
//        sb.append("Kinetic Friction: ").append(kineticFriction).append("\n");
//        sb.append("Static Friction: ").append(staticFriction).append("\n");

        if (magnitudeVelocity >= VELOCITY_THRESHOLD) {  // Ball is moving
            sb.append("Moving\n");
            dxdt[0] = vx;
            dxdt[1] = vz;
            dxdt[2] = -GRAVITY * dh_dxValue - kineticFriction * GRAVITY * vx / magnitudeVelocity;
            dxdt[3] = -GRAVITY * dh_dzValue - kineticFriction * GRAVITY * vz / magnitudeVelocity;

//            sb.append("vx: ").append(vx).append("\n");
//            sb.append("vz: ").append(vz).append("\n");
//            sb.append("ax: ").append(dxdt[2]).append("\n");
//            sb.append("az: ").append(dxdt[3]).append("\n");
//            sb.append("Moving: ").append(Arrays.toString(dxdt)).append("\n");
        } else { // Ball is at rest
            sb.append("At rest\n");
            dxdt[0] = 0;
            dxdt[1] = 0;

            if (dh_dxValue != 0 || dh_dzValue != 0) { // Ball is on a slope
                sb.append("On slope\n");
                double dh2 = Math.sqrt(dh_dxValue * dh_dxValue + dh_dzValue * dh_dzValue);
                double staticFrictionForce = staticFriction * GRAVITY;
                double slopeForce = dh2 * GRAVITY;

//                sb.append("dh2: ").append(dh2).append("\n");
//                sb.append("Static friction force: ").append(staticFrictionForce).append("\n");
//                sb.append("Slope force: ").append(slopeForce).append("\n");

                if (staticFrictionForce < slopeForce) { // Slope force overcomes static friction
                    sb.append("Sliding\n");
                    dxdt[2] = -GRAVITY * dh_dxValue;
                    dxdt[3] = -GRAVITY * dh_dzValue;

//                    sb.append("Sliding: ").append(Arrays.toString(dxdt)).append("\n");
                } else {
                    sb.append("Stationary\n");
                    dxdt[2] = 0;
                    dxdt[3] = 0;
//                    sb.append("Stationary: Static friction holding\n");
                }
            } else {
                sb.append("No slope\n");
                dxdt[2] = 0;
                dxdt[3] = 0;
//                sb.append("Stationary: No slope\n");
            }
        }

        sb.append("dxdt: ").append(Arrays.toString(dxdt)).append("\n");

        try (java.io.FileWriter fw = new java.io.FileWriter(file, true)) {
            fw.write(sb.toString());
            sb.setLength(0);
        } catch (java.io.IOException e) {
            e.printStackTrace(System.err);
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
