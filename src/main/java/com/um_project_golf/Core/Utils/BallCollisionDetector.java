package com.um_project_golf.Core.Utils;

import org.joml.Vector3f;

import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;

public class BallCollisionDetector {

    private final HeightMap heightMap;
    private final SceneManager scene;

    public BallCollisionDetector(HeightMap heightMap, SceneManager scene) {
        this.heightMap = heightMap;
        this.scene = scene;
    }

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
    private void borderCollisionBall(Vector3f newPosition) {
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

    private void treeCollisionBall(Vector3f newPosition) {
        float[][] treePositions = scene.getTreePositions();
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