package com.um_project_golf.GolfBots;

import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.PhysicsEngine;
import com.um_project_golf.Core.Utils.Consts;
import org.joml.Vector3f;


public class RuleBasedBot {
    private Vector3f startingPosition;
    private double flagRadius = 1;
    private Vector3f flagPosition;
    private HeightMap testMap;
    private Entity ball;
    private Entity flag;


    public RuleBasedBot(Entity ball, Entity flag, HeightMap testMap){
        startingPosition = ball.getPosition();
        flagPosition = flag.getPosition();
        this.testMap = testMap;
    }

    public static void main(String[] args) {
        /*HeightMap testMap = new HeightMap();
        testMap.createHeightMap();
        float yInitial = testMap.getHeight(new Vector3f(1, 0, 0));
        float yFlag = testMap.getHeight(new Vector3f(4, 0, 1));

        // Initialize game elements
        RuleBasedBot bot = new RuleBasedBot(new Vector3f(1, yInitial, 0), new Vector3f(4, yFlag, 1), testMap);
        Ball ball = new Ball(bot.startingPosition);
        Green green = new Green(bot.flagPosition, bot.flagRadius);

        // Find the best shot
        Shot bestShot = bot.findBestShot(ball, green);

        // Output the best shot
        System.out.println("Best shot: " + bestShot);

         */
    }

    // public Shot findBestShot(Ball ball, Green green) {
    //     Shot bestShot = null;
    //     Vector3f bestPosition = new Vector3f(startingPosition);
    //     System.out.println("start");

    //     boolean foundBestShot = false;

    //     while (!isInHole(ball, green) && !foundBestShot) {
    //         for (double velocityX = -5; velocityX <= 5; velocityX += 0.1) {
    //             for (double velocityZ = -5; velocityZ <= 5; velocityZ += 0.1) {
    //                 // Apply the velocities to the ball
    //                 applyVelocities(ball, velocityX, velocityZ);

    //                 // Simulate the ball's movement using the physics engine
    //                 simulateBallMovement(ball);

    //                 // Check if it's in the hole
    //                 if (isInHole(ball, green)) {
    //                     bestShot = new Shot(velocityX, velocityZ);
    //                     bestPosition.set(ball.getPosition());
    //                     foundBestShot = true;
    //                     break;
    //                 }

    //                 // Reset ball position for the next shot
    //                 ball.updatePosition(new Vector3f(startingPosition));
    //             }
    //             if (foundBestShot) break;
    //         }
    //         if (!foundBestShot) {
    //             System.out.println("Can't make a hole in one");
    //             ball.updatePosition(bestPosition);
    //         }
    //     }

    //     return bestShot;
    // }

    public Shot findBestShot(Green green) {
        double minDistance = Double.MAX_VALUE;
        Shot bestShot = null;
        Vector3f bestPosition = null;
        Vector3f velocity = null;
        int count = 0;
        System.out.println("start");

        while (!isInHole(green)) {
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
                        if (isInHole(green)) {
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
        }

        return bestShot;
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
