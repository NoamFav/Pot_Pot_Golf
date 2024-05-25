package com.um_project_golf.GolfBots;

import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.PhysicsEngine;
import org.joml.Vector3f;

import java.util.Random;
import java.util.function.Function;

public class AIBot {
    private static Vector3f startingPosition;
    private static Vector3f flagPosition;
    private static double flagRadius = 0;
    private static double minVelocityX = 0;
    private static double maxVelocityX = 100;
    private static double minVelocityZ = 0;
    private static double maxVelocityZ = 0;
    private static double velocityStepX = 0.1;
    private static double velocityStepZ = 0.1;
    public static void main(String[] args) {
        // Initialize game elements
        Ball ball = new Ball(startingPosition);
        Green green = new Green(flagPosition, flagRadius);

        // Find the best shot using Hill-Climbing
        Shot bestShot = findBestShotUsingHillClimbing(ball, green);

        // Output the best shot
        System.out.println("Best shot: " + bestShot);
    }

    public static Shot findBestShotUsingHillClimbing(Ball ball, Green green) {
        Random random = new Random();

        // Initialize with a random shot
        double velocityX = minVelocityX + (maxVelocityX - minVelocityX) * random.nextDouble();
        double velocityZ = minVelocityZ + (maxVelocityZ - minVelocityZ) * random.nextDouble();
        Shot currentShot = new Shot(velocityX, velocityZ);

        boolean improvement = true;

        while (improvement) {
            improvement = false;
            Shot bestNeighbor = currentShot;
            double bestNeighborDistance = evaluateShot(bestNeighbor, ball, green);

            // Generate neighboring shots by adjusting velocity slightly
            for (double dX = -velocityStepX; dX <= velocityStepX; dX += velocityStepX) {
                for (double dZ = -velocityStepZ; dZ <= velocityStepZ; dZ += velocityStepZ) {
                    if (dX == 0 && dZ == 0) continue; // Skip the current shot

                    Shot neighborShot = new Shot(currentShot.getVelocityX() + dX, currentShot.getVelocityZ() + dZ);
                    double neighborDistance = evaluateShot(neighborShot, ball, green);

                    if (neighborDistance < bestNeighborDistance) {
                        bestNeighbor = neighborShot;
                        bestNeighborDistance = neighborDistance;
                        improvement = true;
                    }
                }
            }

            currentShot = bestNeighbor;
        }

        return currentShot;
    }

    public static double evaluateShot(Shot shot, Ball ball, Green green) {
        applyVelocities(ball, shot.getVelocityX(), shot.getVelocityZ());
        simulateBallMovement(ball);
        return ball.distanceToFlag(green);
    }

    public static void applyVelocities(Ball ball, double velocityX, double velocityZ) {
        ball.setVelocityX(velocityX);
        ball.setVelocityZ(velocityZ);
    }

    public static void simulateBallMovement(Ball ball) {
        // Testing with height map
        HeightMap testMap = new HeightMap();
        testMap.createHeightMap();
        double[] initialState = {startingPosition.x, startingPosition.z, ball.getVelocityX(), ball.getVelocityZ()}; // initialState = [x, z, vx, vz]
        double h = 0.1; // Time step
        //double totalTime = 5; // Total time
        PhysicsEngine engine = new PhysicsEngine(testMap, 0.08, 0.2, 0.1, 0.3);
        Vector3f finalPosition = startingPosition;
        for (int i = 0; i < 50; i++) {
            finalPosition = engine.runImprovedEuler(initialState, h).get(engine.runImprovedEuler(initialState, h).size()-1);
            initialState[0] = finalPosition.x;
            initialState[1] = finalPosition.z;
            System.out.println(finalPosition.x + ", " + finalPosition.y + ", " + finalPosition.z);
        }
        // Update ball position based on velocity and physics rules
        ball.updatePosition(finalPosition);
    }

    public static boolean isInHole(Ball ball, Green green) {
        double distanceToFlag = ball.distanceToFlag(green);
        return distanceToFlag <= green.getFlagRadius();
    }
}