package com.um_project_golf.Core.Lighting;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class SpotLight {

    private PointLight pointLight; // point light

    private Vector3f coneDirection; // direction of the cone
    private float cutOff; // cut off value

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOff) {
        this.pointLight = pointLight;
        this.coneDirection = coneDirection;
        this.cutOff = cutOff;
    }

    public SpotLight(@NotNull SpotLight spotLight) {
        this.pointLight = spotLight.getPointLight();
        this.coneDirection = spotLight.getConeDirection();
        this.cutOff = spotLight.getCutOff();
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public float getCutOff() {
        return cutOff;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }
}
