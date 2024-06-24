package com.um_project_golf.Core.Entity;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

/**
 * The entity class.
 * This class is responsible for the entities of the game.
 */
public class Entity {
    private final List<Model> model; // model of the entity
    private final Vector3f position, rotation; // position and rotation of the entity
    private float scale; // scale of the entity

    /**
     * The constructor of the entity.
     * It initializes the model, position, rotation, and scale of the entity.
     *
     * @param model The model of the entity.
     * @param position The position of the entity.
     * @param rotation The rotation of the entity.
     * @param scale The scale of the entity.
     */
    public Entity(List<Model> model, Vector3f position, Vector3f rotation, float scale) {
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Entity(@NotNull Entity entity) {
        this.model = entity.model;
        this.position = entity.position;
        this.rotation = entity.rotation;
        this.scale = entity.scale;
    }

    /**
     * Increases the position of the entity.
     *
     * @param x The x-axis position.
     * @param y The y-axis position.
     * @param z The z-axis position.
     */
    @SuppressWarnings("unused")
    public void increasePosition(float x, float y, float z) {
        this.position.add(x, y, z); // add the x, y, and z values to the position
    }

    /**
     * Set the position of the entity.
     *
     * @param x The x-axis position.
     * @param y The y-axis position.
     * @param z The z-axis position.
     */
    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z); // set the x, y, and z values to the position
    }

    /**
     * Increases the rotation of the entity.
     *
     * @param x The x-axis rotation.
     * @param y The y-axis rotation.
     * @param z The z-axis rotation.
     */
    @SuppressWarnings("unused")
    public void increaseRotation(float x, float y, float z) {
        this.rotation.add(x, y, z); // add the x, y, and z values to the rotation
    }

    /**
     * Set the rotation of the entity.
     *
     * @param x The x-axis rotation.
     * @param y The y-axis rotation.
     * @param z The z-axis rotation.
     */
    public void setRotation(float x, float y, float z) {
        this.rotation.set(x, y, z); // set the x, y, and z values to the rotation
    }

    public List<Model> getModels() {
        return model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    @SuppressWarnings("unused")
    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setPosition(Vector3f startPoint) {
        this.position.set(startPoint);
    }

    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
    }
}
