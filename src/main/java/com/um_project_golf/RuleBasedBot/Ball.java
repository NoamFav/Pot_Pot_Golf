package com.um_project_golf.RuleBasedBot;

class Ball {
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;

    // Constructor
    public Ball(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    // Getters and setters
    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    // Placeholder for updating the ball's position
    public void updatePosition() {

    }

    // Placeholder for calculating the distance to the flag
    public double distanceToFlag(Green green) {
        // Placeholder for distance calculation
        // Example: Calculate distance to the flag based on current ball position and flag position
        return Math.sqrt(Math.pow(green.getFlagPositionX() - positionX, 2) + Math.pow(green.getFlagPositionY() - positionY, 2));
    }

    // Placeholder for resetting the ball's position
    public void resetPosition(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.velocityX = 0;
        this.velocityY = 0;
    }
}