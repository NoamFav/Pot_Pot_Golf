package com.um_project_golf.Core.Lighting;

import org.joml.Vector3f;

public class PointLight {

    private Vector3f color, position; // color of the light, position of the light
    private float intensity, constant, linear, exponent; // intensity of the light, constant, linear, and exponent values

    public PointLight(Vector3f color, Vector3f position, float intensity, float constant, float linear, float exponent) {
        this.color = color;
        this.position = position;
        this.intensity = intensity;
        this.constant = constant;
        this.linear = linear;
        this.exponent = exponent;
    }

    public PointLight(Vector3f color, Vector3f position, float intensity) {
        this.color = color;
        this.position = position;
        this.intensity = intensity;
        this.constant = 1; // default constant value
        this.linear = 0; // default linear value
        this.exponent = 0; // default exponent value
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getExponent() {
        return exponent;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }
}