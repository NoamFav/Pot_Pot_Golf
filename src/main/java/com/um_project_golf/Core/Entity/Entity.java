package com.um_project_golf.Core.Entity;

import org.joml.Vector3f;

/**
 * The entity class.
 * This class is responsible for the entities of the game.
 */
public class Entity {
    public Object getRotation;
    private final Model model;
    private final Vector3f pos, rotation;
    private final float scale;

    /**
     * The constructor of the entity.
     * It initializes the model, position, rotation, and scale of the entity.
     *
     * @param model The model of the entity.
     * @param pos The position of the entity.
     * @param rotation The rotation of the entity.
     * @param scale The scale of the entity.
     */
    public Entity(Model model, Vector3f pos, Vector3f rotation, float scale) {
        this.model = model;
        this.pos = pos;
        this.rotation = rotation;
        this.scale = scale;
    }

    /**
     * Increases the position of the entity.
     *
     * @param x The x-axis position.
     * @param y The y-axis position.
     * @param z The z-axis position.
     */
    public void increasePos(float x, float y, float z) {
        this.pos.add(x, y, z);
    }

    /**
     * Set the position of the entity.
     *
     * @param x The x-axis position.
     * @param y The y-axis position.
     * @param z The z-axis position.
     */
    public void setPos(float x, float y, float z) {
        this.pos.set(x, y, z);
    }

    /**
     * Increases the rotation of the entity.
     *
     * @param x The x-axis rotation.
     * @param y The y-axis rotation.
     * @param z The z-axis rotation.
     */
    public void increaseRotation(float x, float y, float z) {
        this.rotation.add(x, y, z);
    }

    /**
     * Set the rotation of the entity.
     *
     * @param x The x-axis rotation.
     * @param y The y-axis rotation.
     * @param z The z-axis rotation.
     */
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
