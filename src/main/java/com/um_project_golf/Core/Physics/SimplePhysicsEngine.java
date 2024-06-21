package com.um_project_golf.Core.Physics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.Utils.SimpleRK2;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/**
 * The physics engine class.
 * This class is responsible for handling the physics of the game.
 */
@SuppressWarnings("FieldCanBeLocal")
public class SimplePhysicsEngine extends PhysicsEngine {
    private final BiFunction<Double, Double, Double> heightFunction;

    /**
     * Create a new physics' engine.
     * This constructor is deprecated and should not be used.
     * This constructor is kept for backward compatibility.
     * It uses the height function to calculate the height of the ball.
     * Which is already done by the height map.
     *
     * @param height The height function
     */
    @Deprecated
    @SuppressWarnings("unused")
    public SimplePhysicsEngine(BiFunction<Double, Double, Double> height, SceneManager scene) {
        super(null, scene);
        this.heightFunction = height;
    }

    /**
     * Create a new physics' engine.
     *
     * @param heightMap The height map
     */
    public SimplePhysicsEngine(HeightMap heightMap, SceneManager scene) {
        super(heightMap, scene);
        this.heightFunction = null;
    }

    @Override
    protected double @NotNull [] equationsOfMotion(double t, double @NotNull [] x) { // x = [x, z, vx, vz]
        double[] dxdt = new double[4];
        double vx = x[2];
        double vz = x[3];
        double magnitudeVelocity = Math.sqrt(vx * vx + vz * vz);

        double dh_dxValue = dh_dxCentredDifferenceMap(x[0], x[1]);
        double dh_dzValue = dh_dzCentredDifferenceMap(x[0], x[1]);

        double kineticFriction;
        double staticFriction;

        assert heightMap != null;
        if (heightMap.getHeight(new Vector3f((float) x[0], 0, (float) x[1])) < sandLevel) {
            kineticFriction = muK_sand;
            staticFriction = muS_sand;
        } else {
            kineticFriction = muK_grass;
            staticFriction = muS_grass;
        }

        // Simplify small value adjustments
        dh_dxValue = Math.abs(dh_dxValue) < 0.00001 ? 0 : dh_dxValue;
        dh_dzValue = Math.abs(dh_dzValue) < 0.00001 ? 0 : dh_dzValue;

        if (magnitudeVelocity >= VELOCITY_THRESHOLD) {
            // Moving
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
     * initialState = [x, z, vx, vz]
     *
     * @param initialState The initial state
     * @param stepSize The step size
     * @return The final position
     */
    @SuppressWarnings("unused") // This method is an alternative to the rk4 method
    public List<Vector3f> runImprovedEuler(double[] initialState, double stepSize) {
        List<Vector3f> positions = new ArrayList<>();
        double[] currentState = initialState;
        double magnitudeVelocity;
        double magnitudeAcceleration;
        double currentTime = 0;

        double[] nextStep;
        double[] acceleration;

        do {
            nextStep = SimpleRK2.simpleImprovedEuler(currentTime, currentState, currentTime + stepSize, stepSize, this::equationsOfMotion);
            acceleration = equationsOfMotion(currentTime, currentState);
            magnitudeVelocity = Math.sqrt(nextStep[2] * nextStep[2] + nextStep[3] * nextStep[3]);
            magnitudeAcceleration = Math.sqrt(acceleration[2] * acceleration[2] + acceleration[3] * acceleration[3]);
            currentState = nextStep;
            currentTime += stepSize;
            assert heightMap != null;
            float height = heightMap.getHeight(new Vector3f((float) currentState[0], 0, (float) currentState[1]));
            Vector3f position = new Vector3f((float) currentState[0], height, (float) currentState[1]);
            ballCollisionDetector.checkCollisionBall(position);
            positions.add(position);
        } while (magnitudeVelocity >= VELOCITY_THRESHOLD || magnitudeAcceleration >= ACCELERATION_THRESHOLD);

        return positions;
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
}
