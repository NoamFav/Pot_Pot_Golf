package com.um_project_golf.Core.GolfBots;

import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.Physics.*;
import com.um_project_golf.Core.Utils.Noise;
import com.um_project_golf.Game.GameUtils.Consts;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class responsible for the logic behind the AI bot.
 */

@SuppressWarnings("FieldCanBeLocal")
public class AIBot {
    private final Vector3f startingPosition;
    private final double flagRadius;
    private Vector3f velocityBall;
    private final HeightMap testMap;
    private final Entity ball;
    private final Entity flag;
    private HashMap<Vector3f,List<Vector3f>> fullPath;
    private final SceneManager scene;
    private int shotCounter = 0;
    private final Noise noise;
    private final List<List<Vector3f>> path;

    public AIBot(@NotNull Entity ball, Entity flag, HeightMap testMap, double flagRadius, SceneManager scene) {
        startingPosition = new Vector3f(ball.getPosition());
        this.ball = ball;
        this.flag = flag;
        this.testMap = testMap;
        this.flagRadius = flagRadius;
        this.scene = scene;
        noise = new Noise();
        path = new ArrayList<>();
    }

    public List<List<Vector3f>> startAI(){
        System.out.println("Start");
        fullPath = new HashMap<>();
        path.clear();
        return findBestShotUsingHillClimbing(startingPosition);
    }

    public List<List<Vector3f>> findBestShotUsingHillClimbing(Vector3f startingPosition) {
        Vector3f currentPosition = new Vector3f(startingPosition);

        Vector3f bestVelocity = new Vector3f(0, 0, 0);
        double bestNeighborDistance = evaluateShot(bestVelocity);
        ball.setPosition(currentPosition); // Resets position to initial position
        boolean improvement = true;

        while (improvement) {
            improvement = false;

            // Generate neighboring shots by adjusting velocity slightly
            for (double dX = -0.1; dX <= 0.1; dX += Double.parseDouble(String.valueOf(Consts.BOT_SENSITIVITY))) {
                for (double dZ = -0.1; dZ <= 0.1; dZ += Double.parseDouble(String.valueOf(Consts.BOT_SENSITIVITY))) {
                    if (dX == 0 && dZ == 0) continue; // Skip the current shot

                    float newVelocityX = (float) (bestVelocity.x + dX);
                    float newVelocityZ = (float) (bestVelocity.z + dZ);
                    Vector3f newVelocity = new Vector3f(newVelocityX, 0, newVelocityZ);

                    double neighborDistance = evaluateShot(newVelocity); // new hypothetical shot evaluated
                    ball.setPosition(currentPosition); // sets ball back to current position

                    if (neighborDistance < bestNeighborDistance) {
                        bestNeighborDistance = neighborDistance;
                        bestVelocity = new Vector3f(newVelocity);
                        improvement = true;
                        break;
                    }
                }
                if(improvement){
                    break;
                }
            }
        }
        Vector3f noiseVelocity = noise.addNoiseToVelocity(bestVelocity, Consts.ERROR_DIRECTION_DEGREES, Consts.ERROR_MAGNITUDE_PERCENTAGE);
        double distanceFlag = evaluateShot(noiseVelocity);
        boolean sameShot = currentPosition.equals(ball.getPosition());
        shotCounter++;
        currentPosition = new Vector3f(ball.getPosition());
        path.add(fullPath.get(currentPosition));
        fullPath.clear();

        if (isInHole()) {
            System.out.println("Ball in hole!");
            System.out.println("Velocity theoretical: " + bestVelocity);
            System.out.println("Velocity with noise: " + noiseVelocity);
            System.out.println("End of game! Shots taken: " + shotCounter + "\n");
            return path;

        } else if(sameShot){
            System.out.println("FAIL. Shots taken: " + shotCounter + ". Distance to flag: " + distanceFlag + "\n");
            return path;
        }
        System.out.println("Shots taken: " + shotCounter + ". Distance to flag: " + distanceFlag);
        System.out.println("Best velocity theoretical: " + bestVelocity);
        System.out.println("Noisy best velocity: " + noiseVelocity);
        System.out.println("Starting position: " + currentPosition + "\n");
        return findBestShotUsingHillClimbing(currentPosition);
    }

    // Method that evaluates the hypothetical shot
    public double evaluateShot(Vector3f shot) {
        applyVelocities(shot);
        simulateBallMovement();
        return distanceToFlag();
    }

    // Updates velocity of the ball
    public void applyVelocities(Vector3f velocity) {
        velocityBall = velocity;
    }

    // Simulates ball movement with the use of the Physics Engine
    public void simulateBallMovement() {
        double[] initialState = {ball.getPosition().x, ball.getPosition().z, velocityBall.x, velocityBall.z};
        double h = 0.1; // Time step
        PhysicsEngine engine = new SimplePhysicsEngine(testMap, scene);
        List<Vector3f> positions = engine.runRK4(initialState, h);

        Vector3f finalPosition = positions.get(positions.size() - 1);
        if (finalPosition.y <= -0.2){
            ball.setPosition(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
            return;
        }
        fullPath.put(finalPosition, positions);

        // Update ball position based on velocity and physics rules
        ball.setPosition(finalPosition.x, finalPosition.y, finalPosition.z);
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