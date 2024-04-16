package com.um_project_golf.Core.Entity;

import org.joml.Vector3f;

public class Entity {
    public Object getRotation;
    private final Model model;
    private final Vector3f pos, rotation;
    private final float scale;

    public Entity(Model model, Vector3f pos, Vector3f rotation, float scale) {
        this.model = model;
        this.pos = pos;
        this.rotation = rotation;
        this.scale = scale;
    }

    public void increasePos(float x, float y, float z) {
        this.pos.add(x, y, z);
    }

    public void setPos(float x, float y, float z) {
        this.pos.set(x, y, z);
    }

    public void increaseRotation(float x, float y, float z) {
        this.rotation.add(x, y, z);
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.set(x, y, z);
    }

    public Model getModel() {
        return model;
    }

    public Vector3f getPos() {
        return pos;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }
}
