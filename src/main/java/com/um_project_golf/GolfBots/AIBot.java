package com.um_project_golf.GolfBots;

import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.PhysicsEngine;
import org.joml.Vector3f;

import java.util.Random;
import java.util.function.Function;

public class AIBot {
    private Vector3f startingPosition;
    private double flagRadius;
    private double minVelocityX = -5;
    private double maxVelocityX = 5;
    private double minVelocityZ = -5;
    private double maxVelocityZ = 5;
    private double velocityStepX = 0.1;
    private double velocityStepZ = 0.1;
    private HeightMap testMap;
    private Entity ball;
    private Entity flag;

    public AIBot(Entity ball, Entity flag, HeightMap testMap, double flagRadius){
        startingPosition = ball.getPosition();
        this.ball = ball;
        this.flag = flag;
        this.testMap = testMap;
        this.flagRadius = flagRadius;
    }

    public Shot findBestShotUsingHillClimbing() {
        Random random = new Random();

        // Initialize with a random shot
        float velocityX = (float) (minVelocityX + (maxVelocityX - minVelocityX) * random.nextDouble());
        float velocityZ = (float) (minVelocityZ + (maxVelocityZ - minVelocityZ) * random.nextDouble());
        Vector3f velocity = new Vector3f(velocityX, 0,velocityZ);
        Shot currentShot = new Shot(velocity);

        boolean improvement = true;

        while (improvement) {
            improvement = false;
            Shot bestNeighbor = currentShot;
            double bestNeighborDistance = evaluateShot(bestNeighbor);

            applyVelocities(velocity);
            simulateBallMovement();

            if(isInHole()){
                return currentShot;
            } else{
                // Generate neighboring shots by adjusting velocity slightly
                for (double dX = -velocityStepX; dX <= velocityStepX; dX += velocityStepX) {
                    for (double dZ = -velocityStepZ; dZ <= velocityStepZ; dZ += velocityStepZ) {
                        if (dX == 0 && dZ == 0) continue; // Skip the current shot

                        float newVelocityX = (float) (velocity.x + dX);
                        float newVelocityZ = (float) (velocity.z + dZ);
                        Vector3f newVelocity = new Vector3f(newVelocityX, 0,newVelocityZ);
                        Shot neighborShot = new Shot(newVelocity);
                        double neighborDistance = evaluateShot(neighborShot);

                        applyVelocities(newVelocity);
                        simulateBallMovement();

                        if (neighborDistance < bestNeighborDistance) {
                            currentShot = neighborShot;
                            bestNeighborDistance = neighborDistance;
                            velocity = newVelocity;
                            improvement = true;
                            if (isInHole()){
                                return currentShot;
                            }
                            break;
                        }
                        ball.setPosition(startingPosition.x,startingPosition.y,startingPosition.z);
                    }
                    if(improvement){
                        break;
                    }
                }

            }

            // Generate neighboring shots by adjusting velocity slightly
            for (double dX = -velocityStepX; dX <= velocityStepX; dX += velocityStepX) {
                for (double dZ = -velocityStepZ; dZ <= velocityStepZ; dZ += velocityStepZ) {
                    if (dX == 0 && dZ == 0) continue; // Skip the current shot

                    float newVelocityX = (float) (currentShot.getVelocity().x + dX);
                    float newVelocityZ = (float) (currentShot.getVelocity().z + dZ);
                    Shot neighborShot = new Shot(new Vector3f(newVelocityX,0,newVelocityZ) );
                    double neighborDistance = evaluateShot(neighborShot);

                    if (neighborDistance < bestNeighborDistance) {
                        bestNeighbor = neighborShot;
                        bestNeighborDistance = neighborDistance;
                        improvement = true;
                    }
                    ball.setPosition(startingPosition.x,startingPosition.y,startingPosition.z);
                }
            }

            currentShot = bestNeighbor;

        }
        System.out.println("No improvement possible");
        return currentShot;
    }

    public double evaluateShot(Shot shot) {
        applyVelocities(shot.getVelocity());
        simulateBallMovement();
        return distanceToFlag();
    }

    public void applyVelocities(Vector3f velocity) {
        ball.setRotation(velocity.x, velocity.y, velocity.z);
    }

    public void simulateBallMovement() {
        double[] initialState = {ball.getPosition().x, ball.getPosition().z, ball.getRotation().x, ball.getRotation().z};
        double h = 0.1; // Time step
        PhysicsEngine engine = new PhysicsEngine(testMap);
        Vector3f finalPosition = new Vector3f(ball.getPosition());
        for (int i = 0; i < 50; i++) {
            finalPosition = engine.runImprovedEuler(initialState, h).get(engine.runImprovedEuler(initialState, h).size()-1);
            initialState[0] = finalPosition.x;
            initialState[1] = finalPosition.z;
        }
        // Update ball position based on velocity and physics rules
        ball.setPosition(finalPosition.x,finalPosition.y,finalPosition.z);
    }

    public boolean isInHole() {
        double distanceToFlag = distanceToFlag();
        return distanceToFlag <= flagRadius;
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