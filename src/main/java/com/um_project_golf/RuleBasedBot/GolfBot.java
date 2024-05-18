package com.um_project_golf.RuleBasedBot;

import java.util.function.Function;

public class GolfBot {

    private static double startingPositionX = 0;
    private static double startingPositionY = 0;
    private static Function<Double, Double> heightProfile;
    private static Obstacle[] obstacles;
    private static double flagPositionX = 0;
    private static double flagPositionY = 0;
    private static double flagRadius = 0;
    public static void main(String[] args) {
        // Initialize game elements
        Ball ball = new Ball(startingPositionX, startingPositionY);
        Green green = new Green(heightProfile, obstacles, flagPositionX, flagPositionY, flagRadius);

        // Find the best shot
        Shot bestShot = findBestShot(ball, green);

        // Output the best shot
        System.out.println("Best shot: " + bestShot);
    }

    public static Shot findBestShot(Ball ball, Green green) {
        double minDistance = Double.MAX_VALUE;
        Shot bestShot = null;

        // Placeholder for iterating over possible velocities
        for (double velocityX = 1; velocityX <= 300; velocityX += 5) {
            for (double velocityY = 1; velocityY <= 300; velocityY += 5) {
                // Apply the velocities to the ball
                applyVelocities(ball, velocityX, velocityY);

                // Simulate the ball's movement using the physics engine
                simulateBallMovement(ball);

                // Check if the ball reached the hole
                if (isInHole(ball, green)) {
                    // Check if it's the best shot so far
                    if (ball.distanceToFlag(green) < minDistance) {
                        minDistance = ball.distanceToFlag(green);
                        bestShot = new Shot(velocityX, velocityY);
                    }
                }

                // Reset ball position for the next shot
                ball.resetPosition(startingPositionX, startingPositionY);
            }
        }

        return bestShot;
    }

    // Placeholder for applying velocities to the ball
    public static void applyVelocities(Ball ball, double velocityX, double velocityY) {
        ball.setVelocityX(velocityX);
        ball.setVelocityY(velocityY);
    }

    // Placeholder for simulating the ball's movement
    public static void simulateBallMovement(Ball ball) {
        // Placeholder for physics engine simulation
        // Update ball position based on velocity and physics rules
        ball.updatePosition();
    }

    // Placeholder for checking if the ball is in the hole
    public static boolean isInHole(Ball ball, Green green) {
        // Placeholder for hole-checking logic
        // Check if the ball is within the hole radius of the flag
        double distanceToFlag = ball.distanceToFlag(green);
        return distanceToFlag <= green.getFlagRadius();
    }
}
