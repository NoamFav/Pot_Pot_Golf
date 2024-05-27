package com.um_project_golf.GolfBots;

import org.joml.Vector3f;

import java.util.function.Function;

class Green {
    private Vector3f flagPosition;
    private double flagRadius;

    // Constructor
    public Green(Vector3f flagPosition, double flagRadius) {
        this.flagPosition = flagPosition;
        this.flagRadius = flagRadius;
    }

    // Getters
    public Vector3f getFlagPosition(){
        return flagPosition;
    }

    public double getFlagRadius() {
        return flagRadius;
    }
}