    package com.pot_pot_golf.Core;

    import org.joml.Vector3f;

    /**
     * The camera class.
     * This class is responsible for the camera of the game.
     */
    public class Camera {

        private final Vector3f position, rotation; // The position and rotation of the camera.

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
        @SuppressWarnings("unused")
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
            // Check if the camera is moving on the z-axis.
            if (offsetZ != 0) {
                // Move on the z-axis based on the rotation of the camera. Using sin and cos to calculate the new position.
                position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ; // -1.0f is used to invert the direction of the camera.
                position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
            }
            // Check if the camera is moving on the x-axis.
            if (offsetX != 0) {
                // Move on the x-axis based on the rotation of the camera. Using sin and cos to calculate the new position.
                position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX; // -1.0f is used to invert the direction of the camera.
                position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
            }
            // Offset the y-axis.
            position.y += offsetY;
        }

        /**
         * Set the position of the camera.
         *
         * @param x The x-axis position.
         * @param y The y-axis position.
         * @param z The z-axis position.
         */
        @SuppressWarnings("unused")
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

        public void setPosition(Vector3f newPosition) {
            position.set(newPosition);
        }
    }
