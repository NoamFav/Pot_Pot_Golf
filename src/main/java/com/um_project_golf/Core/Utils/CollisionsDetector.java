package com.um_project_golf.Core.Utils;

import com.um_project_golf.Core.Camera;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import org.joml.Vector3f;

public class CollisionsDetector {

    private Camera camera;
    private Vector3f cameraInc;
    private HeightMap heightMap;

    public void checkCollision(Camera camera, Vector3f cameraInc, HeightMap heightMap) {

        this.camera = camera;
        this.cameraInc = cameraInc;
        this.heightMap = heightMap;

        Vector3f newPosition = camera.getPosition();

        terrainCollision(newPosition);
        borderCollision(newPosition);

        camera.setPosition(newPosition);
    }

    private void borderCollision(Vector3f newPosition) {
        float outOfBounds = 1;
        if (camera.getPosition().x < -Consts.SIZE_X / 2) {
            newPosition.x = -Consts.SIZE_X / 2;
            cameraInc.x = 0;
        } else if (camera.getPosition().x > Consts.SIZE_X / 2) {
            newPosition.x = Consts.SIZE_X / 2;
            cameraInc.x = 0;
        }
        if (camera.getPosition().z < -Consts.SIZE_Z / 2) {
            newPosition.z = -Consts.SIZE_Z / 2;
            cameraInc.z = 0;
        } else if (camera.getPosition().z > Consts.SIZE_Z / 2) {
            newPosition.z = Consts.SIZE_Z / 2;
            cameraInc.z = 0;
        }
        if (camera.getPosition().y > Consts.MAX_HEIGHT) {
            newPosition.y = Consts.MAX_HEIGHT;
            cameraInc.y = 0;
        }
    }

    private void terrainCollision(Vector3f newPosition) {
        // Correct the translation so -1000 maps to index 0

        // Retrieve the terrain height using the clamped indices
        float terrainHeight = heightMap.getHeight(newPosition) + Consts.PLAYER_HEIGHT;
        if (newPosition.y <= terrainHeight) {
            newPosition.y = terrainHeight;
        }
    }

    private void entityCollision(Vector3f newPosition) {
        //TODO: Implement entity collision
        // Check if the new position is inside an entityOpenSafari
        // Implement a hit box for the entity
    }
}
