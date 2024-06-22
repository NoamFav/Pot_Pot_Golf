package com.um_project_golf.Core.Physics;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import com.um_project_golf.Core.SimpleRK4;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.Utils.BallCollisionDetector;
import com.um_project_golf.Core.Utils.Consts;

public abstract class PhysicsEngine {
    protected static final double g = Consts.GRAVITY; // gravitational constant

    protected final double muK_grass = Consts.KINETIC_FRICTION_GRASS; // kinetic friction grass
    protected final double muS_grass = Consts.STATIC_FRICTION_GRASS; // static friction grass
    protected final double muK_sand = Consts.KINETIC_FRICTION_SAND; // kinetic friction sand
    protected final double muS_sand = Consts.STATIC_FRICTION_SAND; // static friction sand
    protected final HeightMap heightMap;
    @SuppressWarnings("FieldCanBeLocal") protected static final double sandLevel = Consts.SAND_HEIGHT; // get this value from the constant file
    protected final double VELOCITY_THRESHOLD = 0.05; // get this value from the constant file
    protected final double ACCELERATION_THRESHOLD = 0.5; // get this value from the constant file
    protected final BallCollisionDetector ballCollisionDetector;

    public PhysicsEngine(HeightMap heightMap, SceneManager scene) {
        this.heightMap = heightMap;
        this.ballCollisionDetector = new BallCollisionDetector(heightMap, scene);
    }

    /**
     * Provide the equations of motion.
     * Uses the height map to calculate the equations of motion.
     *
     * @param t  The time
     * @param x The state vector of the ball where x = [x, z, vx, vz]
     * @return The derivatives dxdt = [vx, vz, ax, az]
     */
    protected abstract double @NotNull [] equationsOfMotion(double t, double @NotNull [] x);

    /**
     * Run the Runge-Kutta 4 method.
     * Improvement on the Euler method.
     * Offers numerical stability and accuracy.
     * initialState = [x, z, vx, vz]
     *
     * @param initialState The initial state
     * @param stepSize The step size
     * @return The final position
     */
    public List<Vector3f> runRK4(double[] initialState, double stepSize) { // initialState = [x, z, vx, vz]
        List<Vector3f> positions = new ArrayList<>();
        float startHeight = heightMap.getHeight(new Vector3f((float) initialState[0], 0, (float) initialState[1]));
        positions.add(new Vector3f((float) initialState[0],startHeight, (float) initialState[1] ));
        double[] currentState = initialState;
        double magnitudeVelocity;
        double magnitudeAcceleration;
        double currentTime = 0;

        double[] nextStep;
        double[] acceleration;

        do { // Repeat until the ball is completely at rest
            nextStep = SimpleRK4.simpleRK4(currentTime, currentState, currentTime + stepSize, stepSize, this::equationsOfMotion);
            acceleration = equationsOfMotion(currentTime, currentState);
            magnitudeVelocity = Math.sqrt(nextStep[2] * nextStep[2] + nextStep[3] * nextStep[3]);
            magnitudeAcceleration = Math.sqrt(acceleration[2] * acceleration[2] + acceleration[3] * acceleration[3]);
            
            // // Log intermediate state for debugging
            // System.out.println("Before update - Current State: " + Arrays.toString(currentState));
            // System.out.println("Next Step: " + Arrays.toString(nextStep));

            currentState = nextStep;

            // // Log updated state for debugging
            // System.out.println("After update - Current State: " + Arrays.toString(currentState));

            currentTime += stepSize;

            assert heightMap != null;
            float height = heightMap.getHeight(new Vector3f((float) currentState[0], 0, (float) currentState[1]));
            Vector3f position = new Vector3f((float) currentState[0], height, (float) currentState[1]);
            ballCollisionDetector.checkCollisionBall(position);
            positions.add(position);

            // Logs for debugging
            // System.out.println("Velocity: " + magnitudeVelocity + " Acceleration: " + magnitudeAcceleration);
            // System.out.println(position);
        } while (magnitudeVelocity >= VELOCITY_THRESHOLD || magnitudeAcceleration >= ACCELERATION_THRESHOLD); // While the ball is not at rest

        if (positions.size() > 1) {
            int index = 1;
            while (index < positions.size()) {
                Vector3f currentCheck = positions.get(index);
                if (currentCheck.equals(positions.get(index - 1))) {
                    positions.remove(index);
                }
                else {
                    index++;
                }
            }
        }

        return positions;
    }

    /**
     * Calculate the centred difference of the height map in the x-direction.
     *
     * @param x The x coordinate
     * @param z The z coordinate
     * @return The derivative
     */
    protected double dh_dxCentredDifferenceMap(double x, double z) {
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
    protected double dh_dzCentredDifferenceMap(double x, double z) {
        float stepSize = Consts.SIZE_Z / (Consts.VERTEX_COUNT - 1);
        assert heightMap != null;
        double h1 = this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (z - 2 * stepSize)));
        double h2 = this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (z - stepSize)));
        double h3 = this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (z + stepSize)));
        double h4 = this.heightMap.getHeight(new Vector3f((float) x, 0, (float) (z + 2 * stepSize)));

        return (h1 - 8 * h2 + 8 * h3 - h4) / (12 * stepSize);
    }
}