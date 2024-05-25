package com.um_project_golf.GolfBots;

import org.joml.Vector3f;

class Shot {
    private Vector3f velocity;

    // Constructor
    public Shot(Vector3f velocity) {
        this.velocity = velocity;
    }

    // Getters
    public Vector3f getVelocity() {
        return velocity;
    }

    @Override
    public String toString() {
        return "VelocityX: " + velocity.x + ", VelocityZ: " + velocity.z;
    }
}
