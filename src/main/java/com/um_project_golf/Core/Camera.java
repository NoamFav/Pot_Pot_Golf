package com.um_project_golf.Core;

import org.joml.Vector3f;

/**
 * The camera class.
 * This class is responsible for the camera of the game.
 */
public class Camera {

    private final Vector3f position, rotation;

    /**
     * The constructor of the camera.
     * It initializes the position and rotation of the camera.
     */
    public Camera() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
    }

    /**
     * The constructor of the camera.
     * It initializes the position and rotation of the camera.
     *
     * @param position The position of the camera.
     * @param rotation The rotation of the camera.
     */
    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    /**
     * Moves the position of the camera.
     *
     * @param offsetX The offset of the x-axis.
     * @param offsetY The offset of the y-axis.
     * @param offsetZ The offset of the z-axis.
     */
    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if (offsetZ != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if (offsetX != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    /**
     * Set the position of the camera.
     *
     * @param x The x-axis position.
     * @param y The y-axis position.
     * @param z The z-axis position.
     */
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    /**
     * Set the rotation of the camera.
     *
     * @param x The x-axis rotation.
     * @param y The y-axis rotation.
     * @param z The z-axis rotation.
     */
    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

    /**
     * Moves the rotation of the camera.
     *
     * @param offsetX The offset of the x-axis.
     * @param offsetY The offset of the y-axis.
     * @param offsetZ The offset of the z-axis.
     */
    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.add(offsetX, offsetY, offsetZ);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }
}
