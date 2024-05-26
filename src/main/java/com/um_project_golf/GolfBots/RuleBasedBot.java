package com.um_project_golf.GolfBots;

import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.PhysicsEngine;
import com.um_project_golf.Core.Utils.BallCollisionDetector;
import com.um_project_golf.Core.Utils.Consts;
import org.joml.Vector3f;


public class RuleBasedBot {
    private Vector3f startingPosition;
    private final double flagRadius;
    private final HeightMap testMap;
    private Entity ball;
    private Entity flag;
    private SceneManager scene;


    public RuleBasedBot(Entity ball, Entity flag, HeightMap testMap, double flagRadius, SceneManager scene){
        startingPosition = ball.getPosition();
        this.ball = ball;
        this.flag = flag;
        this.testMap = testMap;
        this.flagRadius = flagRadius;
        this.scene = scene;
    }

    public Shot findBestShot() {
        double minDistance = Double.MAX_VALUE;
        Shot bestShot = null;
        Vector3f bestPosition = null;
        Vector3f velocity;
        System.out.println("start");

        while (!isInHole()) {
            // Placeholder for iterating over possible velocities
            for (float velocityX = -5; velocityX <= 5; velocityX += 0.1) {
                for (float velocityZ = -5; velocityZ <= 5; velocityZ += 0.1) {
                    velocity = new Vector3f(velocityX, 0, velocityZ);
                    // Apply the velocities to the ball
                    applyVelocities(velocity);

                    // Simulate the ball's movement using the physics engine
                    simulateBallMovement();

                    // Check if it's in hole
                    if (distanceToFlag() < minDistance) {
                        if (isInHole()) {
                            bestShot = new Shot(velocity);
                            bestPosition = ball.getPosition();
                            break;
                        }
                        minDistance = distanceToFlag();
                        bestShot = new Shot(velocity);
                        bestPosition = ball.getPosition();
                    }

                    // Reset ball position for the next shot
                    ball.setPosition(startingPosition.x,startingPosition.y,startingPosition.z);
                }
                System.out.println("2");
            }
            System.out.println("Can't make a hole in one");
            ball.setPosition(bestPosition.x,bestPosition.y,bestPosition.z);
            startingPosition = bestPosition;
        }

        return bestShot;
    }

    public void applyVelocities(Vector3f velocity) {
        ball.setRotation(velocity.x, velocity.y, velocity.z);
    }

    public void simulateBallMovement() {
        BallCollisionDetector ballCollisionDetector = new BallCollisionDetector(testMap, scene);
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
