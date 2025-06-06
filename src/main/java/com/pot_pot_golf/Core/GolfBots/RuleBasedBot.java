package com.pot_pot_golf.Core.GolfBots;

import com.pot_pot_golf.Core.Entity.Entity;
import com.pot_pot_golf.Core.Entity.SceneManager;
import com.pot_pot_golf.Core.Entity.Terrain.HeightMap;
import com.pot_pot_golf.Core.Physics.*;
import com.pot_pot_golf.Core.Utils.Noise;
import com.pot_pot_golf.Game.GameUtils.Consts;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class responsible for the logic behind the Rule-based bot.
 */

public class RuleBasedBot {
    private Vector3f startingPosition;
    private final double flagRadius;
    private final HeightMap heightMap;
    private final Entity ball;
    private Vector3f velocityBall;
    private final Entity flag;
    private HashMap<Vector3f,List<Vector3f>> fullPath;
    private final SceneManager scene;
    private final Noise noise;

    /**
     * Constructor for the Rule-based bot.
     *
     * @param ball the ball entity
     * @param flag the flag entity
     * @param heightMap the height map of the terrain
     * @param flagRadius the radius of the flag
     * @param scene the scene manager
     */
    public RuleBasedBot(@NotNull Entity ball, Entity flag, HeightMap heightMap, double flagRadius, SceneManager scene) {
        startingPosition = new Vector3f(ball.getPosition());
        this.ball = ball;
        this.flag = flag;
        this.heightMap = heightMap;
        this.flagRadius = flagRadius;
        this.scene = scene;
        noise = new Noise();
    }

    /**
     * Method returns the path the ball takes from its initial position until the last position found by the bot.
    * It does so by iterating over every possible velocity pair
     * (x and z) within the constraints given (min -5m/s max 5m/s),
    * and comparing it to the other shots already taken,
     * saving the information of the shot that gets closer to the flag.
    * If it is not possible to get to the flag in one shot, the shot that gets closer will be taken.
     * The process will
    * continue until the flag is reached,
     * or when it is not possible to move anymore (for more details on this issue, please
    * consult the README).
    * @return the path the ball takes from its initial position until the last position found by the bot
    */
    public List<List<Vector3f>> findBestShot() {
        int shotCounter = 0;
        double minDistance;
        fullPath = new HashMap<>();
        List<List<Vector3f>> path = new ArrayList<>();
        Vector3f velocity;
        Vector3f bestVelocity = new Vector3f(0, 0, 0);
        System.out.println("Start");

        // Main loop to keep shooting until the ball is in the hole
        while (!isInHole()) {
            System.out.println("Starting position: " + startingPosition);
            minDistance = Double.MAX_VALUE; // Reset minDistance for this shot sequence
            startingPosition = noise.addNoiseToInitialPosition(startingPosition, Consts.WANT_ERROR ? Consts.ERROR_POSITION_RADIUS : 0); // Add noise to the starting position

            // Iterate over possible velocities to find the best shot for this turn
            for (float velocityX = -5; velocityX <= 5; velocityX += Consts.BOT_SENSITIVITY) {
                for (float velocityZ = -5; velocityZ <= 5; velocityZ += Consts.BOT_SENSITIVITY) {
                    velocity = new Vector3f(velocityX, 0, velocityZ);

                    // Apply the velocities to the ball
                    applyVelocities(velocity);

                    // Simulate the ball's movement using the physics engine
                    simulateBallMovement();

                    // Check if the shot is better than the best one already saved
                    if (distanceToFlag() < minDistance) {
                        minDistance = distanceToFlag();
                        bestVelocity.set(velocity);
                    }

                    // Reset ball position for the next shot attempt
                    ball.setPosition(startingPosition.x, startingPosition.y, startingPosition.z);
                }
            }

            // Apply noise to the bestVelocity to simulate human error
            Vector3f noisyBestVelocity = noise.addNoiseToVelocity(bestVelocity, Consts.WANT_ERROR ? Consts.ERROR_DIRECTION_DEGREES : 0, Consts.WANT_ERROR ? Consts.ERROR_MAGNITUDE_PERCENTAGE : 0);
            noisyBestVelocity.x = clamp(noisyBestVelocity.x, -5, 5);
            noisyBestVelocity.z = clamp(noisyBestVelocity.z, -5, 5);

            applyVelocities(noisyBestVelocity);
            simulateBallMovement();
            double distanceFlag = distanceToFlag();
            boolean sameShot = startingPosition.equals(ball.getPosition());
            shotCounter++;

            // Check if the ball is in the hole after applying noise
            if (isInHole()) {
                path.add(fullPath.get(new Vector3f(ball.getPosition()))); // Add the last position to the path
                System.out.println("Ball is in the hole!!");
                System.out.println("Total shots taken: " + shotCounter);
                System.out.println("Final velocity: " + noisyBestVelocity);
                System.out.println("Theoretical best velocity: " + bestVelocity);
                break; // Exit the loop if the ball is in the hole
            } else if(sameShot || shotCounter == Consts.MAX_SHOTS) {
                System.out.println("FAIL. Shots taken: " + shotCounter + ". Distance to flag: " + distanceFlag + "\n");
                break;
            }

            // Update starting position to the new position after the noisy shot
            startingPosition = new Vector3f(ball.getPosition());
            path.add(fullPath.get(startingPosition));
            System.out.println("Shot " + shotCounter + ". Distance to flag: " + distanceToFlag());
            System.out.println("Best velocity: " + bestVelocity);
            System.out.println("Noisy best velocity: " + noisyBestVelocity + "\n");
            fullPath.clear();
        }

        return path;
    }


    /**
     * Method to evaluate a shot by applying the velocities to the ball and simulating its movement.
     * @param velocity the velocity vector to apply to the ball
     */
    public void applyVelocities(Vector3f velocity) {
        velocityBall = (velocity);
    }

    /**
     * Method to simulate the movement of the ball using the physics engine.
     */
    public void simulateBallMovement() {
        double[] initialState = {ball.getPosition().x, ball.getPosition().z, velocityBall.x, velocityBall.z};
        double h = 0.1; // Time step
        PhysicsEngine engine = new CompletePhysicsEngine(heightMap, scene);
        List<Vector3f> positions = engine.runRK4(initialState, h);

        Vector3f finalPosition = positions.get(positions.size() - 1);
        if (finalPosition.y <= -0.2){
            ball.setPosition(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
            return;
        }
        fullPath.put(finalPosition,positions);

        // Update ball position based on velocity and physics rules
        ball.setPosition(finalPosition.x,finalPosition.y,finalPosition.z);
    }

    /**
     * Method to check if the ball is in the hole.
     * @return true if the ball is in the hole, false otherwise
     */
    public boolean isInHole() {
        double distanceToFlag = distanceToFlag();
        return distanceToFlag <= flagRadius;
    }

    /**
     * Method to calculate the distance between the ball and the flag.
     * @return the distance between the ball and the flag
     */
    public double distanceToFlag() {
        double dx = flag.getPosition().x - ball.getPosition().x;
        double dy = flag.getPosition().y - ball.getPosition().y;
        double dz = flag.getPosition().z - ball.getPosition().z;

        // Calculate the distance using the 3D distance formula
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Method to clamp a value between a minimum and maximum value.
     * @param value the value to clamp
     * @param min the minimum value
     * @param max the maximum value
     * @return the clamped value
     */
    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}
