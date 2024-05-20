package com.um_project_golf.GolfBots;

class Shot {
    private double velocityX;
    private double velocityY;

    // Constructor
    public Shot(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    // Getters
    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    @Override
    public String toString() {
        return "VelocityX: " + velocityX + ", VelocityY: " + velocityY;
    }
}
