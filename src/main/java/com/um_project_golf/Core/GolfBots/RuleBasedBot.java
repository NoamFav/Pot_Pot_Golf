package com.um_project_golf.Core.GolfBots;

import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.Physics.*;
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

    public RuleBasedBot(@NotNull Entity ball, Entity flag, HeightMap heightMap, double flagRadius, SceneManager scene) {
        startingPosition = new Vector3f(ball.getPosition());
        this.ball = ball;
        this.flag = flag;
        this.heightMap = heightMap;
        this.flagRadius = flagRadius;
        this.scene = scene;
    }

    /* Method returns the path the ball takes from its initial position until the last position found by the bot.
    It does so by iterating over every possible velocity pair (x and z) within the constraints given (min -5m/s max 5m/s),
    and comparing it to the other shots already taken, saving the information of the shot that gets closer to the flag.
    If it is not possible to get to the flag in one shot, the shot that gets closer will be taken. The process will
    continue until the flag is reached, or when it is not possible to move anymore (for more details on this issue, please
    consult the README). */
    public List<List<Vector3f>> findBestShot() {
        int shotCounter = 0;
        double minDistance = Double.MAX_VALUE;
        fullPath = new HashMap<>();
        List<List<Vector3f>> path = new ArrayList<>();
        Vector3f bestPosition = null;
        Vector3f velocity;
        System.out.println("Start");

        //Checks whether the ball is already in the hole
        while (!isInHole()) {
            // Placeholder for iterating over possible velocities
            for (float velocityX = -5; velocityX <= 5; velocityX += 0.1f) {
                for (float velocityZ = -5; velocityZ <= 5; velocityZ += 0.1f) {
                    velocity = new Vector3f(velocityX, 0, velocityZ);
                    // Apply the velocities to the ball
                    applyVelocities(velocity);

                    // Simulate the ball's movement using the physics engine
                    simulateBallMovement();

                    // Check if the shot is better than the best one already saved
                    if (distanceToFlag() < minDistance) {
                        // Check if the ball is in the hole
                        if (isInHole()) {
                            bestPosition = new Vector3f(ball.getPosition());
                            shotCounter++;
                            path.add(fullPath.get(bestPosition)); // adds the position to the path of points passed by
                            System.out.println("Shot "+ shotCounter +". Distance to flag: " + distanceToFlag());
                            System.out.println("Ball is in hole! With " + shotCounter + " shots taken");
                            return path;
                        }
                        minDistance = distanceToFlag(); // replaces the old best shot with the new one
                        bestPosition = new Vector3f(ball.getPosition()); // saves the end position of the new shot
                    }

                    // Reset ball position for the next shot
                    ball.setPosition(startingPosition.x,startingPosition.y,startingPosition.z);
                }
            }
            assert bestPosition != null;
            ball.setPosition(bestPosition.x,bestPosition.y,bestPosition.z); // sets ball to start in the new position
            startingPosition = new Vector3f(bestPosition); // sets initial position to be now the new position

            path.add(fullPath.get(bestPosition)); // adds the position to the path of points passed by
            shotCounter++;
            fullPath.clear(); // clears the path for the next shot
            System.out.println("Shot "+ shotCounter +". Distance to flag: " + distanceToFlag());
        }
        return path;
    }

    // Updates velocity of the ball
    public void applyVelocities(Vector3f velocity) {
        velocityBall = (velocity);
    }

    // Simulates ball movement with the use of the Physics Engine
    public void simulateBallMovement() {
        double[] initialState = {ball.getPosition().x, ball.getPosition().z, velocityBall.x, velocityBall.z};
        double h = 0.1; // Time step
        PhysicsEngine engine = new SimplePhysicsEngine(heightMap, scene);
        List<Vector3f> positions = engine.runRK4(initialState, h);

        Vector3f finalPosition = positions.get(positions.size()-1);

        fullPath.put(finalPosition,positions);

        // Update ball position based on velocity and physics rules
        ball.setPosition(finalPosition.x,finalPosition.y,finalPosition.z);
    }

    // Method to check whether the ball is in hole
    public boolean isInHole() {
        double distanceToFlag = distanceToFlag();
        return distanceToFlag <= flagRadius;
    }

    // Method to calculate the distance to the flag
    public double distanceToFlag() {
        double dx = flag.getPosition().x - ball.getPosition().x;
        double dy = flag.getPosition().y - ball.getPosition().y;
        double dz = flag.getPosition().z - ball.getPosition().z;

        // Calculate the distance using the 3D distance formula
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
