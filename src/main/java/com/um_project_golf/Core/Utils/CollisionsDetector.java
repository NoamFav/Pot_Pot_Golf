package com.um_project_golf.Core.Utils;

import com.um_project_golf.Core.Camera;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.io.File;
import java.io.PrintWriter;

/**
 * The class responsible for detecting collisions with the camera.
 */
public class CollisionsDetector {

    private Camera camera;
    private Vector3f cameraInc;
    private HeightMap heightMap;
    private SceneManager scene;
    private final File file = new File("output.txt");
    private final StringBuilder sb = new StringBuilder();

    /**
     * Checks for collision with the camera.
     *
     * @param camera The camera.
     * @param cameraInc The camera increment.
     * @param heightMap The height map.
     * @param scene The scene manager.
     */
    public void checkCollision(@NotNull Camera camera, Vector3f cameraInc, HeightMap heightMap, SceneManager scene) {
        this.camera = camera;
        this.cameraInc = cameraInc;
        this.heightMap = heightMap;
        this.scene = scene;
        Vector3f position = camera.getPosition();

        terrainCollision(position);
        borderCollision(position);
        entityCollision(position);

        camera.setPosition(position);
    }

    /**
     * Checks for collision with the border.
     *
     * @param position The position of the camera.
     */
    private void borderCollision(Vector3f position) {
        if (camera.getPosition().x < -Consts.SIZE_X / 2) {
            position.x = -Consts.SIZE_X / 2;
            cameraInc.x = 0;
        } else if (camera.getPosition().x > Consts.SIZE_X / 2) {
            position.x = Consts.SIZE_X / 2;
            cameraInc.x = 0;
        }
        if (camera.getPosition().z < -Consts.SIZE_Z / 2) {
            position.z = -Consts.SIZE_Z / 2;
            cameraInc.z = 0;
        } else if (camera.getPosition().z > Consts.SIZE_Z / 2) {
            position.z = Consts.SIZE_Z / 2;
            cameraInc.z = 0;
        }
        if (camera.getPosition().y > Consts.MAX_HEIGHT) {
            position.y = Consts.MAX_HEIGHT;
            cameraInc.y = 0;
        }
    }

    /**
     * Checks for collision with the terrain.
     *
     * @param position The position of the camera.
     */
    private void terrainCollision(Vector3f position) {
        float terrainHeight = heightMap.getHeight(position) + Consts.PLAYER_HEIGHT;
        if (position.y <= terrainHeight) {
            position.y = terrainHeight;
        }
    }

    /**
     * Checks for collision with entities.
     *
     * @param position The position of the camera.
     */
    private void entityCollision(Vector3f position) {
        float[][] treePositions = scene.getTreePositions();
        float treeRadius = Consts.TREE_SIZE / 2; // Define the tree radius

        sb.append("Checking collisions at position: ").append(position).append("\n");

        boolean collisionDetected = false;

        for (float[] treePosition : treePositions) {
            float treeX = treePosition[0];
            float treeY = treePosition[1];
            float treeZ = treePosition[2];

            // Calculate the distance between the camera and the tree
            float distanceX = position.x - treeX;
            float distanceY = position.y - treeY;
            float distanceZ = position.z - treeZ;

            // Check if the camera is within the tree radius
            boolean isColliding = (distanceX * distanceX + distanceZ * distanceZ) < (treeRadius * treeRadius) && Math.abs(distanceY) < 8.0f;

            if (isColliding) {
                sb.append("Collision detected with tree at: (").append(treeX).append(", ").append(treeY).append(", ").append(treeZ).append(")").append("\n");

                // Normalize the direction vector from the tree to the camera
                Vector3f direction = new Vector3f(distanceX, 0, distanceZ).normalize();

                // Move the camera to the edge of the tree radius
                position.x = treeX + direction.x * treeRadius;
                 // Ensure the camera is above the tree
                position.z = treeZ + direction.z * treeRadius;

                collisionDetected = true;
                sb.append("Adjusted position to: ").append(position).append("\n");
                break; // Exit loop after handling the collision
            }
        }

        if (collisionDetected) {
            cameraInc.set(0, 0, 0); // Optionally, stop camera movement
            sb.append("Final position after handling collision: ").append(position).append("\n");

            try (PrintWriter out = new PrintWriter(file)) {
                out.println(sb);
            } catch (Exception e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        }
    }


}
