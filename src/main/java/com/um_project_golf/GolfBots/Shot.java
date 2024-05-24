package com.um_project_golf.GolfBots;

class Shot {
    private double velocityX;
    private double velocityZ;

    // Constructor
    public Shot(double velocityX, double velocityZ) {
        this.velocityX = velocityX;
        this.velocityZ = velocityZ;
    }

    // Getters
    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityZ() {
        return velocityZ;
    }

    @Override
    public String toString() {
        return "VelocityX: " + velocityX + ", VelocityZ: " + velocityZ;
    }
}
