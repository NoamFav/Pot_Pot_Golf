package com.um_project_golf.GolfBots;

import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.PhysicsEngine;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Class responsible for the logic behind the AI bot.
 */


@SuppressWarnings("FieldCanBeLocal")
public class AIBot {
    private final Vector3f startingPosition;
    private final double flagRadius;
    private Vector3f velocityBall;
    private final double minVelocityX = -5;
    private final double maxVelocityX = 5;
    private final double minVelocityZ = -5;
    private final double maxVelocityZ = 5;
    private final HeightMap testMap;
    private final Entity ball;
    private final Entity flag;
    private HashMap<Vector3f,List<Vector3f>> fullPath;
    private final SceneManager scene;

    public AIBot(@NotNull Entity ball, Entity flag, HeightMap testMap, double flagRadius, SceneManager scene) {
        startingPosition = new Vector3f(ball.getPosition());
        this.ball = ball;
        this.flag = flag;
        this.testMap = testMap;
        this.flagRadius = flagRadius;
        this.scene = scene;
    }

    // Method returns the path the ball takes from its initial position until the last position found by the AI bot.
    // It does so by using the Hill Climbing method: gets a random initial velocity, computes the shot's final position's
    // distance to the flag and saves it. Changes slightly the velocity and compares this new shot with the old one, if there
    // is an improvement, it takes the new shot and moves the ball to the end position of the shot. This process continues
    // until the ball reaches the flag or there is no possible improvement to be made (for more details on this issue please
    // consult the README).
    public List<List<Vector3f>> findBestShotUsingHillClimbing() {
        int shotCounter = 0;
        Random random = new Random();
        List<List<Vector3f>> path = new ArrayList<>();
        Vector3f currentPosition = new Vector3f(startingPosition);
        Vector3f nextPosition;

        System.out.println("Start");

        // Initialize with a random shot
        float velocityX = (float) (minVelocityX + (maxVelocityX - minVelocityX) * random.nextDouble());
        float velocityZ = (float) (minVelocityZ + (maxVelocityZ - minVelocityZ) * random.nextDouble());
        Vector3f velocity = new Vector3f(velocityX, 0,velocityZ);
        Shot currentShot = new Shot(velocity);

        Shot bestNeighbor = currentShot;
        double bestNeighborDistance = evaluateShot(bestNeighbor); // Finds the distance from the flag of the hypothetical shot
        ball.setPosition(currentPosition); // Resets position to initial position


        boolean improvement = true;

        // Loops until there is no more possible improvement to be made
        while (improvement) {
            improvement = false;
            Shot neighborShot = currentShot;

            double neighborDistance = evaluateShot(neighborShot); // New hypothetical shot

            nextPosition = new Vector3f(ball.getPosition());
            ball.setPosition(currentPosition);

            // Compares distance of current shot with the distance of the last shot taken
            if(neighborDistance < bestNeighborDistance){
                bestNeighborDistance = neighborDistance;
                improvement = true;
                shotCounter++;
                path.add(fullPath.get(nextPosition)); // adds the position to the path of points passed by
                currentPosition = new Vector3f(nextPosition);
                ball.setPosition(currentPosition); // set the ball to the end position of the shot
                System.out.println("Shot "+ shotCounter +".Improvement! Distance to flag: " + distanceToFlag());
                // Checks if its in hole
                if(isInHole()) {
                    System.out.println("Ball is in hole! With " + shotCounter + " shots taken");
                    return path;
                }
            } else{
                // Generate neighboring shots by adjusting velocity slightly
                for (double dX = -5; dX <= 5; dX += 1) {
                    for (double dZ = -5; dZ <= 5; dZ += 1) {
                        if (dX == velocity.x && dZ == velocity.z) continue; // Skip the current shot

                        float newVelocityX = (float) dX;
                        float newVelocityZ = (float) dZ;
                        Vector3f newVelocity = new Vector3f(newVelocityX, 0,newVelocityZ);
                        neighborShot = new Shot(newVelocity);

                        neighborDistance = evaluateShot(neighborShot); // new hypothetical shot evaluated

                        nextPosition = new Vector3f(ball.getPosition()); // saves new hypothetical position
                        ball.setPosition(currentPosition); // sets ball back to current position


                        if (neighborDistance < bestNeighborDistance) {
                            currentShot = neighborShot;
                            bestNeighborDistance = neighborDistance;
                            velocity = newVelocity;
                            improvement = true;
                            shotCounter++;
                            path.add(fullPath.get(nextPosition)); // adds the position to the path of points passed by
                            currentPosition = new Vector3f(nextPosition);
                            ball.setPosition(currentPosition); // sets the ball to the end position of the shot
                            System.out.println("Shot "+ shotCounter +".Improvement! Distance to flag: " + distanceToFlag());
                            // Checks if its in hole
                            if (isInHole()){
                                System.out.println("Ball is in hole!With " + shotCounter + " shots taken");
                                return path;
                            }
                            break;
                        }
                    }
                    if(improvement){
                        break;
                    }
                }

            }

        }
        System.out.println("No improvement possible");
        return path;
    }

    // Method that evaluates the hypothetical shot
    public double evaluateShot(Shot shot) {
        applyVelocities(shot.velocity());
        simulateBallMovement();
        return distanceToFlag();
    }

    // Updates velocity of the ball
    public void applyVelocities(Vector3f velocity) {
        velocityBall = velocity;
    }

    // Simulates ball movement with the use of the Physics Engine
    public void simulateBallMovement() {
        fullPath = new HashMap<>();
        double[] initialState = {ball.getPosition().x, ball.getPosition().z, velocityBall.x, velocityBall.z};
        double h = 0.1; // Time step
        PhysicsEngine engine = new PhysicsEngine(testMap, scene);
        List<Vector3f> positions = engine.runRK4(initialState, h);

        Vector3f finalPosition = positions.get(positions.size()-1);

        fullPath.put(finalPosition,positions);

        // Update ball position based on velocity and physics rules
        ball.setPosition(finalPosition.x,finalPosition.y,finalPosition.z);
    }

    // Method that checks whether the ball is in hole
    public boolean isInHole() {
        double distanceToFlag = distanceToFlag();
        return distanceToFlag <= flagRadius;
    }

    // Method that calculates the distance to the flag
    public double distanceToFlag() {
        double dx = flag.getPosition().x - ball.getPosition().x;
        double dy = flag.getPosition().y - ball.getPosition().y;
        double dz = flag.getPosition().z - ball.getPosition().z;

        // Calculate the distance using the 3D distance formula

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}