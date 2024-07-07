package com.pot_pot_golf.Core.Utils;

import com.pot_pot_golf.Game.GameUtils.Consts;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import com.pot_pot_golf.Core.Entity.SceneManager;
import com.pot_pot_golf.Core.Entity.Terrain.HeightMap;

import java.util.List;

/**
 * The class responsible for detecting collisions with the ball.
 */
public class BallCollisionDetector {

    private final HeightMap heightMap;
    private final SceneManager scene;

    /**
     * The constructor of the ball collision detector.
     * It initializes the height map and scene manager.
     *
     * @param heightMap The height map.
     * @param scene The scene manager.
     */
    public BallCollisionDetector(HeightMap heightMap, SceneManager scene) {
        this.heightMap = heightMap;
        this.scene = scene;
    }

    /**
     * Checks for collision with the ball.
     *
     * @param finalPosition The final position of the ball.
     */
    public void checkCollisionBall(Vector3f finalPosition) {
        // For the golf ball:
        // Check for collision with terrain
        terrainCollisionBall(finalPosition);
        // Check for collision with the border
        borderCollisionBall(finalPosition);
        // Check for collision with trees
        treeCollisionBall(finalPosition);
        // Check for collision with water
    }

    /**
     * Checks for collision with the terrain.
     *
     * @param newPosition The new position of the ball.
     */
    private void terrainCollisionBall(Vector3f newPosition) {
        // Correct the translation so -1000 maps to index 0

        // Retrieve the terrain height using the clamped indices
        // float terrainHeight = heightMap.getHeight(newPosition) + golfBall.getScale();
        float terrainHeight = heightMap.getHeight(newPosition);
        //System.out.println("Terrain height: " + terrainHeight);
        if (newPosition.y < terrainHeight) {
            newPosition.y = terrainHeight;
        }
    }

    /**
     * Checks for collision with the border.
     *
     * @param newPosition The new position of the ball.
     */
    private void borderCollisionBall(@NotNull Vector3f newPosition) {
        if (newPosition.x < -Consts.SIZE_X / 2) {
            newPosition.x = -Consts.SIZE_X / 2;
        } else if (newPosition.x > Consts.SIZE_X / 2) {
            newPosition.x = Consts.SIZE_X / 2;
        }
        if (newPosition.z < -Consts.SIZE_Z / 2) {
            newPosition.z = -Consts.SIZE_Z / 2;
        } else if (newPosition.z > Consts.SIZE_Z / 2) {
            newPosition.z = Consts.SIZE_Z / 2;
        }
        if (newPosition.y > Consts.MAX_HEIGHT) {
            newPosition.y = Consts.MAX_HEIGHT;
        }
    }

    /**
     * Checks for collision with the trees.
     * When the ball collides with a tree, it moves the ball to the edge of the tree.
     *
     * @param newPosition The new position of the ball.
     */
    private void treeCollisionBall(Vector3f newPosition) {
        List<float[]> treePositions = scene.getTreePositions();
        if (treePositions == null) {
            return; // No trees to check for collisions
        }
        float treeRadius = Consts.TREE_SIZE / 2; // Define the tree radius

        for (float[] treePosition : treePositions) {
            float treeX = treePosition[0];
            float treeY = treePosition[1];
            float treeZ = treePosition[2];

            // Calculate the distance between the ball and the tree
            float distanceX = newPosition.x - treeX;
            float distanceY = newPosition.y - treeY;
            float distanceZ = newPosition.z - treeZ;

            // Check if the ball is within the tree radius
            boolean isColliding = (distanceX * distanceX + distanceZ * distanceZ) < (treeRadius * treeRadius) && Math.abs(distanceY) < 8.0f;

            if (isColliding) {

                // Normalize the direction vector from the tree to the ball
                Vector3f direction = new Vector3f(distanceX, 0, distanceZ).normalize();

                // Move the ball to the edge of the tree radius
                newPosition.x = treeX + direction.x * treeRadius;
                newPosition.z = treeZ + direction.z * treeRadius;
                break; // Exit loop after handling the collision
            }
        }
    }


}
