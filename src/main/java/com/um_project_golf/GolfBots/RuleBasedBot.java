package com.um_project_golf.GolfBots;

import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.PhysicsEngine;
import com.um_project_golf.Core.Utils.BallCollisionDetector;
import com.um_project_golf.Core.Utils.Consts;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RuleBasedBot {
    private Vector3f startingPosition;
    private final double flagRadius;
    private final HeightMap testMap;
    private Entity ball;
    private Vector3f velocityBall;
    private Entity flag;
    private SceneManager scene;
    private HashMap<Vector3f,List<Vector3f>> fullPath;


    public RuleBasedBot(Entity ball, Entity flag, HeightMap testMap, double flagRadius, SceneManager scene){
        startingPosition = ball.getPosition();
        this.ball = ball;
        this.flag = flag;
        this.testMap = testMap;
        this.flagRadius = flagRadius;
        this.scene = scene;
    }

    public List<List<Vector3f>> findBestShot() {
        double minDistance = Double.MAX_VALUE;
        List<List<Vector3f>> path = new ArrayList<>();
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
                            path.add(fullPath.get(bestPosition));
                            return path;
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
            path.add(fullPath.get(bestPosition));
        }

        return path;
    }

    public void applyVelocities(Vector3f velocity) {
        velocityBall = velocity;
    }

    public void simulateBallMovement() {
        fullPath = new HashMap<>();
        double[] initialState = {ball.getPosition().x, ball.getPosition().z, velocityBall.x, velocityBall.z};
        double h = 0.1; // Time step
        PhysicsEngine engine = new PhysicsEngine(testMap);
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
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        return distance;
    }
}
