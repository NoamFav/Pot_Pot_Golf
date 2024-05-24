package com.um_project_golf.GolfBots;

import org.joml.Vector3f;

class Ball {
    private Vector3f position;
    private double velocityX;
    private double velocityZ;

    // Constructor
    public Ball(Vector3f position) {
        this.position = position;
    }

    // Getters and setters
    public double getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public double getVelocityZ() {
        return velocityZ;
    }

    public void setVelocityZ(double velocityZ) {
        this.velocityZ = velocityZ;
    }

    // Placeholder for updating the ball's position
    public void updatePosition(Vector3f newPosition) {
        position = newPosition;
    }
    public Vector3f getPosition(){
        return position;
    }

    // Placeholder for calculating the distance to the flag
    public double distanceToFlag(Green green) {
        double dx = green.getFlagPosition().x - position.x;
        double dy = green.getFlagPosition().y - position.y;
        double dz = green.getFlagPosition().z - position.z;

        // Calculate the distance using the 3D distance formula
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        return distance;
    }
}