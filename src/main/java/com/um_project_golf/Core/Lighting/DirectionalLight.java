package com.um_project_golf.Core.Lighting;

import org.joml.Vector3f;

/**
 * The class responsible for the directional light.
 */
public class DirectionalLight {

    private Vector3f color, direction; // color of the light, direction of the light
    private float intensity; // intensity of the light

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
