package com.pot_pot_golf.Core.Utils;

import com.pot_pot_golf.Core.Camera;
import com.pot_pot_golf.Core.Entity.SceneManager;
import com.pot_pot_golf.Core.Entity.Terrain.HeightMap;
import com.pot_pot_golf.Game.GameUtils.Consts;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

/**
 * The class responsible for detecting collisions with the camera.
 */
public class CollisionsDetector {

    private Camera camera;
    private Vector3f cameraInc;
    private HeightMap heightMap;
    private SceneManager scene;


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
        entityCollision(position); // Comment it out to avoid collision with trees as only useful for the ball

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
        List<float[]> treePositions = scene.getTreePositions();
        float treeRadius = Consts.TREE_SIZE / 2; // Define the tree radius

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

                if (distanceX != 0 || distanceZ != 0) {
                    Vector3f direction = new Vector3f(distanceX, 0, distanceZ).normalize();
                    position.x = treeX + direction.x * treeRadius;
                    position.z = treeZ + direction.z * treeRadius;
                } else {
                    // Handle the case where the camera is directly above the tree
                    position.x = treeX; // Or some other logic to handle this case
                    position.z = treeZ;
                }

                collisionDetected = true;
                break; // Exit loop after handling the collision
            }
        }

        if (collisionDetected) {
            cameraInc.set(0, 0, 0); // Optionally, stop camera movement
        }
    }


}
