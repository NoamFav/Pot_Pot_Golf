package com.um_project_golf.GolfBots;

import java.util.function.Function;

class Green {
    private Function<Double, Double> heightProfile;
    private Obstacle[] obstacles;
    private double flagPositionX;
    private double flagPositionY;
    private double flagRadius;

    // Constructor
    public Green(Function<Double, Double> heightProfile, Obstacle[] obstacles, double flagPositionX, double flagPositionY, double flagRadius) {
        this.heightProfile = heightProfile;
        this.obstacles = obstacles;
        this.flagPositionX = flagPositionX;
        this.flagPositionY = flagPositionY;
        this.flagRadius = flagRadius;
    }

    // Getters
    public double getFlagPositionX() {
        return flagPositionX;
    }

    public double getFlagPositionY() {
        return flagPositionY;
    }

    public double getFlagRadius() {
        return flagRadius;
    }
}