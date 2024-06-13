package com.um_project_golf.Game.GameLogic;

import com.um_project_golf.Core.*;
import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.Entity.Terrain.HeightMapPathfinder;
import com.um_project_golf.Core.Entity.Terrain.TerrainTexture;
import com.um_project_golf.Core.Utils.Consts;
import com.um_project_golf.Core.Utils.TerrainSwitch;
import com.um_project_golf.Game.FieldManager.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static com.um_project_golf.Game.GolfGame.debugMode;

public class InputManager {
    public void movementControl(@NotNull GameStateManager gameStateManager, WindowManager window,
                                Vector3f cameraInc, Camera camera, PathManager pathManager,
                                GameVarManager gameVarManager, EntitiesManager entitiesManager,
                                GuiElementManager guiElementManager, SceneManager scene, ModelManager modelManager) {

        float moveSpeed = Consts.CAMERA_MOVEMENT_SPEED / EngineManager.getFps();

        // Movement controls
        if (gameStateManager.isCanMove()) {
            if (window.is_keyPressed(GLFW.GLFW_KEY_W)) {
                cameraInc.z = -moveSpeed;
            }
            if (window.is_keyPressed(GLFW.GLFW_KEY_S)) {
                cameraInc.z = moveSpeed;
            }
            if (window.is_keyPressed(GLFW.GLFW_KEY_A)) {
                cameraInc.x = -moveSpeed;
            }
            if (window.is_keyPressed(GLFW.GLFW_KEY_D)) {
                cameraInc.x = moveSpeed;
            }
            if (window.is_keyPressed(GLFW.GLFW_KEY_SPACE)) {
                cameraInc.y = moveSpeed;
            }
            if (window.is_keyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                cameraInc.y = -moveSpeed;
            }
            if (window.is_keyPressed(GLFW.GLFW_KEY_UP)) {
                camera.setPosition(new Vector3f(pathManager.getStartPoint()));
            }
            if (window.is_keyPressed(GLFW.GLFW_KEY_DOWN)) {
                camera.setPosition(new Vector3f(pathManager.getEndPoint()));
            }

            if (window.is_keyPressed(GLFW.GLFW_KEY_F) && gameVarManager.isTreeAnimationIdle()) {
                gameVarManager.setTreeAnimationGoingUp();
                gameVarManager.resetTreeAnimationTime();
            }

            if (window.is_keyPressed(GLFW.GLFW_KEY_Q)) {
                Entity currentBall = entitiesManager.getCurrentBall();
                camera.setPosition(new Vector3f(currentBall.getPosition().x, currentBall.getPosition().y + Consts.PLAYER_HEIGHT, currentBall.getPosition().z));
            }

            if (window.is_keyPressed(GLFW.GLFW_KEY_R) && !gameStateManager.isAnimating()) {
                restartBalls(pathManager, gameStateManager, entitiesManager, gameVarManager, guiElementManager);
            }

            // Create a tree at the camera position
            boolean isTPressed = window.is_keyPressed(GLFW.GLFW_KEY_T);

            if (isTPressed && !gameStateManager.istKeyWasPressed()) {
                plantTree(camera, modelManager, scene, entitiesManager, gameStateManager);
            } else if (!isTPressed) {
                gameStateManager.settKeyWasPressed(false); // Reset the flag when the key is released
            }
        }
    }

    /**
     * Restart the balls to the start point.
     */
    private void restartBalls(@NotNull PathManager pathManager, @NotNull GameStateManager gameStateManager, EntitiesManager entitiesManager, GameVarManager gameVarManager, GuiElementManager guiElementManager) {
        Vector3f start = new Vector3f(pathManager.getStartPoint());
        if (gameStateManager.isIs2player()) {
            entitiesManager.setGolfBallPosition(start);
            entitiesManager.setGolfBall2Position(start);
            gameVarManager.resetNumberOfShots();
            gameStateManager.setPlayer1Turn(true);
            entitiesManager.setCurrentBall(entitiesManager.getGolfBall());
            guiElementManager.getCurrentPlayer().setText("Player 1's turn");
        } else {
            entitiesManager.setGolfBallPosition(start);
            gameVarManager.resetNumberOfShots();
        }
        Entity currentBall = entitiesManager.getCurrentBall();
        int numberOfShots = gameVarManager.getNumberOfShots();
        int numberOfShots2 = gameVarManager.getNumberOfShots2();
        guiElementManager.getInfoTextPane().setText("Position: (" + (int) currentBall.getPosition().x + ", " + (int) currentBall.getPosition().z + "). Number of shots: " + (gameStateManager.isPlayer1Turn() ? numberOfShots : numberOfShots2));
    }

    /**
     * Save the forest
     * Plant a tree at the camera position.
     */
    private void plantTree(@NotNull Camera camera, @NotNull ModelManager modelManager, @NotNull SceneManager scene, @NotNull EntitiesManager entitiesManager, @NotNull GameStateManager gameStateManager) {
        Vector3f cameraPos = new Vector3f(camera.getPosition().x, camera.getPosition().y - Consts.PLAYER_HEIGHT, camera.getPosition().z);
        Entity newTree = new Entity(modelManager.getTree(), new Vector3f(cameraPos.x, cameraPos.y, cameraPos.z), new Vector3f(-90, 0, 0), 0.03f);
        scene.addEntity(newTree);
        entitiesManager.addTree(newTree);
        entitiesManager.addTreeHeight(cameraPos.y);
        scene.addTreePosition(new float[]{cameraPos.x, cameraPos.y, cameraPos.z});
        gameStateManager.settKeyWasPressed(true); // Mark the key as pressed
        System.out.println("Tree added at: " + cameraPos);
    }


    public void cameraMovement(@NotNull GameStateManager gameStateManager, MouseInput mouseInputs, Camera camera) {
        if (gameStateManager.isCanMove()) {
            if (mouseInputs.isRightButtonPressed()) {
                Vector2f rotVec = mouseInputs.getDisplayVec();
                camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
            }
        } else if (gameStateManager.isOnMenu() && gameStateManager.isGuiVisible()) {
            camera.moveRotation(0, 0.1f, 0);
        }
    }

    public void startEndPointDebugMode(GameStateManager gameStateManager, WindowManager window, SceneManager scene,
                                        EntitiesManager entitiesManager, HeightMap heightMap, PathManager pathManager, Camera camera,
                                        HeightMapPathfinder pathfinder, TerrainManager terrainManager, TerrainSwitch terrainSwitch,
                                        ModelManager modelManager, ObjectLoader loader) {
        if (debugMode && gameStateManager.isCanMove()) {
            if (window.is_keyPressed(GLFW.GLFW_KEY_LEFT)) {
                setUpStartPoint(gameStateManager, scene, entitiesManager, heightMap, pathManager, camera);
            }
            if (window.is_keyPressed(GLFW.GLFW_KEY_RIGHT) && gameStateManager.isHasStartPoint()) {
                setUpEndPoint(camera, pathManager, pathfinder, terrainManager, terrainSwitch, modelManager, loader, entitiesManager, gameStateManager);
            }
        }
    }


    /**
     * Set up the start Point in debug mode.
     */
    private void setUpStartPoint(@NotNull GameStateManager gameStateManager, SceneManager scene, EntitiesManager entitiesManager,
                                 HeightMap heightMap, PathManager pathManager, Camera camera) {
        if (!gameStateManager.isHasStartPoint()) { // Ensure the start point is only set once
            scene.getEntities().removeAll(entitiesManager.getTrees());
            heightMap.createHeightMap();
            Vector3f startPoint = new Vector3f(camera.getPosition().x, camera.getPosition().y - Consts.PLAYER_HEIGHT, camera.getPosition().z); // Create a new instance to avoid reference issues
            pathManager.setStartPoint(startPoint);
            entitiesManager.setGolfBallPosition(startPoint);
            if (gameStateManager.isIs2player()) {
                entitiesManager.setGolfBall2Position(startPoint);
            }
            gameStateManager.setHasStartPoint(true);
            System.out.println("Start point set: " + startPoint); // Print with more decimal places for clarity
        }
    }

    /**
     * Set up the end Point in debug mode.
     */
    private void setUpEndPoint(Camera camera, PathManager pathManager, HeightMapPathfinder pathfinder,
                               TerrainManager terrainManager, TerrainSwitch terrainSwitch,
                               ModelManager modelManager, ObjectLoader loader,
                               EntitiesManager entitiesManager, GameStateManager gameStateManager) {
        Vector3f endPoint = new Vector3f(camera.getPosition().x, camera.getPosition().y - Consts.PLAYER_HEIGHT, camera.getPosition().z); // Create a new instance to avoid reference issues
        pathManager.setEndPoint(endPoint);

        Vector2i start = new Vector2i((int) pathManager.getStartPoint().x, (int) pathManager.getStartPoint().z);
        start.x = (int) ((start.x + Consts.SIZE_X / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_X));
        start.y = (int) ((start.y + Consts.SIZE_Z / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_Z));

        Vector2i end = new Vector2i((int) endPoint.x, (int) endPoint.z);
        end.x = (int) ((end.x + Consts.SIZE_X / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_X));
        end.y = (int) ((end.y + Consts.SIZE_Z / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_X));

        System.out.println("Start point: " + start);
        System.out.println("End point: " + end);

        pathManager.setPath(pathfinder.getPathDebug(start, end, Consts.SIZE_GREEN));
        try {
            TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/heightmap.png"));
            terrainSwitch.terrainSwitch(terrainManager.getBlendMapTerrain(), modelManager.getTree(), blendMap2);
        } catch (Exception ignore) {
        }
        entitiesManager.setEndFlagPosition(endPoint);
        gameStateManager.setHasStartPoint(false);
    }

}
