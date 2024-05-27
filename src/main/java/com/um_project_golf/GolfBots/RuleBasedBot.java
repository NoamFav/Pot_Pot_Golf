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

    public List<List<Vector3f>> findBestShot() {
        double minDistance = Double.MAX_VALUE;
        List<List<Vector3f>> path = new ArrayList<>();
        Vector3f bestPosition = null;
        Vector3f velocity;
        System.out.println("start");
        int count = 1;

        while (!isInHole()) {
            // Placeholder for iterating over possible velocities
            for (float velocityX = -5; velocityX <= 5; velocityX += 0.1f) {
                for (float velocityZ = -5; velocityZ <= 5; velocityZ += 0.1f) {
                    velocity = new Vector3f(velocityX, 0, velocityZ);
                    // Apply the velocities to the ball
                    applyVelocities(velocity);

                    // Simulate the ball's movement using the physics engine
                    simulateBallMovement();

                    // Check if it's in hole
                    if (distanceToFlag() < minDistance) {
                        if (isInHole()) {
                            bestPosition = new Vector3f(ball.getPosition());
                            path.add(fullPath.get(bestPosition));
                            System.out.println("Found a path");
                            return path;
                        }
                        minDistance = distanceToFlag();
                        bestPosition = new Vector3f(ball.getPosition());
                    }

                    // Reset ball position for the next shot
                    ball.setPosition(startingPosition.x,startingPosition.y,startingPosition.z);
                }
            }
            assert bestPosition != null;
            ball.setPosition(bestPosition.x,bestPosition.y,bestPosition.z);
            startingPosition = new Vector3f(bestPosition);
            path.add(fullPath.get(bestPosition));
            count++;
            System.out.println("count: " + count);
            System.out.println("Found a path");
        }

        return path;
    }

    public void applyVelocities(Vector3f velocity) {
        velocityBall = (velocity);
    }

    public void simulateBallMovement() {
        fullPath = new HashMap<>();
        double[] initialState = {ball.getPosition().x, ball.getPosition().z, velocityBall.x, velocityBall.z};
        double h = 0.1; // Time step
        PhysicsEngine engine = new PhysicsEngine(heightMap, scene);
        List<Vector3f> positions = engine.runImprovedEuler(initialState, h);

        Vector3f finalPosition = positions.get(positions.size()-1);

        fullPath.put(finalPosition,positions);

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

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
