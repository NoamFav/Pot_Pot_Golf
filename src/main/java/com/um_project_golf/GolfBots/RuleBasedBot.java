package com.um_project_golf.GolfBots;

import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.PhysicsEngine;
import org.joml.Vector3f;

import java.util.function.Function;

public class RuleBasedBot {
    private static Vector3f startingPosition;
    private static double flagRadius = 1;
    private static Vector3f flagPosition;
    private static HeightMap testMap = new HeightMap();

    public RuleBasedBot(Vector3f startingPosition, Vector3f flagPosition){
        RuleBasedBot.startingPosition = startingPosition;
        RuleBasedBot.flagPosition = flagPosition;
    }

    public static void main(String[] args) {
        testMap.createHeightMap();
        float yInitial = testMap.getHeight(new Vector3f(1,0,0));
        float yFlag = testMap.getHeight(new Vector3f(4,0,1));
        // Initialize game elements
        RuleBasedBot bot = new RuleBasedBot(new Vector3f(1,yInitial,0),new Vector3f(4,yFlag,1));
        Ball ball = new Ball(startingPosition);
        Green green = new Green(flagPosition, flagRadius);

        // Find the best shot
        Shot bestShot = findBestShot(ball, green);

        // Output the best shot
        System.out.println("Best shot: " + bestShot);
    }

    public static Shot findBestShot(Ball ball, Green green) {
        double minDistance = Double.MAX_VALUE;
        Shot bestShot = null;
        Vector3f bestPosition = null;
        int count = 0;
        System.out.println("start");

        while(!isInHole(ball,green)) {

            // Placeholder for iterating over possible velocities
            for (double velocityX = -5; velocityX <= 5; velocityX += 0.1) {
                for (double velocityZ = -5; velocityZ <= 5; velocityZ += 0.1) {
                    // Apply the velocities to the ball
                    applyVelocities(ball, velocityX, velocityZ);

                    // Simulate the ball's movement using the physics engine
                    simulateBallMovement(ball);

                    // Check if it's the best shot so far
                    if (ball.distanceToFlag(green) < minDistance) {
                        minDistance = ball.distanceToFlag(green);
                        bestShot = new Shot(velocityX, velocityZ);
                        bestPosition = ball.getPosition();
                    }

                    // Reset ball position for the next shot
                    ball.updatePosition(startingPosition);
                }
                System.out.println("2");
            }
            System.out.println("Can't make a hole in one");
            ball.updatePosition(bestPosition);
        }

        return bestShot;
    }

    // Placeholder for applying velocities to the ball
    public static void applyVelocities(Ball ball, double velocityX, double velocityZ) {
        ball.setVelocityX(velocityX);
        ball.setVelocityZ(velocityZ);
    }

    // Placeholder for simulating the ball's movement
    public static void simulateBallMovement(Ball ball) {
        // Testing with height map
        double[] initialState = {startingPosition.x, startingPosition.z, ball.getVelocityX(), ball.getVelocityZ()}; // initialState = [x, z, vx, vz]
        double h = 0.1; // Time step
        //double totalTime = 5; // Total time
        PhysicsEngine engine = new PhysicsEngine(testMap, 0.08, 0.2, 0.1, 0.3);
        Vector3f finalPosition = startingPosition;
        for (int i = 0; i < 50; i++) {
            finalPosition = engine.runImprovedEuler(initialState, h);
            initialState[0] = finalPosition.x;
            initialState[1] = finalPosition.z;
        }
        // Update ball position based on velocity and physics rules
        ball.updatePosition(finalPosition);
    }

    // Placeholder for checking if the ball is in the hole
    public static boolean isInHole(Ball ball, Green green) {
        // Check if the ball is within the hole radius of the flag
        double distanceToFlag = ball.distanceToFlag(green);
        return distanceToFlag <= green.getFlagRadius();
    }
}
