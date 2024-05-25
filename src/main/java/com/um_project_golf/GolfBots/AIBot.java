package com.um_project_golf.GolfBots;

import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.PhysicsEngine;
import org.joml.Vector3f;

import java.util.Random;
import java.util.function.Function;

public class AIBot {
    private Vector3f startingPosition;
    private Vector3f flagPosition;
    private double flagRadius = 0;
    private double minVelocityX = 0;
    private double maxVelocityX = 100;
    private double minVelocityZ = 0;
    private double maxVelocityZ = 0;
    private float velocityStepX = 0.1F;
    private double velocityStepZ = 0.1;
    private HeightMap testMap;
    private Entity ball;
    private Entity flag;

    public AIBot(Entity ball, Entity flag, HeightMap testMap){
        startingPosition = ball.getPosition();
        flagPosition = flag.getPosition();
        this.testMap = testMap;
    }
    public static void main(String[] args) {
        // Initialize game elements
        /*
        Ball ball = new Ball(startingPosition);
        Green green = new Green(flagPosition, flagRadius);

        // Find the best shot using Hill-Climbing
        Shot bestShot = findBestShotUsingHillClimbing(ball, green);

        // Output the best shot
        System.out.println("Best shot: " + bestShot);

         */
    }

    public Shot findBestShotUsingHillClimbing(Ball ball, Green green) {
        Random random = new Random();

        // Initialize with a random shot
        double velocityX = minVelocityX + (maxVelocityX - minVelocityX) * random.nextDouble();
        double velocityZ = minVelocityZ + (maxVelocityZ - minVelocityZ) * random.nextDouble();
        Vector3f velocity = new Vector3f(velocityX, 0,velocityZ);
        Shot currentShot = new Shot(velocity);

        boolean improvement = true;

        while (improvement) {
            improvement = false;
            Shot bestNeighbor = currentShot;
            double bestNeighborDistance = evaluateShot(bestNeighbor, green);

            // Generate neighboring shots by adjusting velocity slightly
            for (double dX = -velocityStepX; dX <= velocityStepX; dX += velocityStepX) {
                for (double dZ = -velocityStepZ; dZ <= velocityStepZ; dZ += velocityStepZ) {
                    if (dX == 0 && dZ == 0) continue; // Skip the current shot

                    Shot neighborShot = new Shot(new Vector3f(currentShot.getVelocity().x + dX,0,currentShot.getVelocity().z + dZ) );
                    double neighborDistance = evaluateShot(neighborShot, green);

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

    public double evaluateShot(Shot shot, Green green) {
        applyVelocities(shot.getVelocity());
        simulateBallMovement();
        return distanceToFlag();
    }

    public void applyVelocities(Vector3f velocity) {
        ball.setRotation(velocity.x, velocity.y, velocity.z);
    }

    public void simulateBallMovement() {
        double[] initialState = {startingPosition.x, startingPosition.z, ball.getRotation().x, ball.getRotation().z};
        double h = 0.1; // Time step
        PhysicsEngine engine = new PhysicsEngine(testMap, 0.08, 0.2, 0.1, 0.3);
        Vector3f finalPosition = new Vector3f(startingPosition);
        for (int i = 0; i < 50; i++) {
            finalPosition = engine.runImprovedEuler(initialState, h).get(engine.runImprovedEuler(initialState, h).size()-1);
            initialState[0] = finalPosition.x;
            initialState[1] = finalPosition.z;
        }
        // Update ball position based on velocity and physics rules
        ball.setPosition(finalPosition.x,finalPosition.y,finalPosition.z);
    }

    public boolean isInHole(Green green) {
        double distanceToFlag = distanceToFlag();
        return distanceToFlag <= green.getFlagRadius();
    }

    public double distanceToFlag() {
        double dx = flag.getPosition().x - ball.getPosition().x;
        double dy = flag.getPosition().y - ball.getPosition().y;
        double dz = flag.getPosition().z - ball.getPosition().z;

        // Calculate the distance using the 3D distance formula
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        return distance;
    }
}