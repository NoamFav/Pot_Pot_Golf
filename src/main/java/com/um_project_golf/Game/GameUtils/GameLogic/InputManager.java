package com.um_project_golf.Game.GameUtils.GameLogic;

import com.um_project_golf.Core.*;
import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.Entity.Terrain.HeightMapPathfinder;
import com.um_project_golf.Core.Entity.Terrain.TerrainTexture;
import com.um_project_golf.Game.GameUtils.Consts;
import com.um_project_golf.Core.Utils.TerrainSwitch;
import com.um_project_golf.Game.GameUtils.FieldManager.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static com.um_project_golf.Game.GolfGame.debugMode;

/**
 * The input manager class.
 * This class is responsible for managing the inputs of the game.
 * Stores the input of the game.
 */
public class InputManager {

    private final MainFieldManager context;

    private final WindowManager window;
    private final Camera camera;
    private final SceneManager scene;
    private final MouseInput mouseInputs;
    private final HeightMap heightMap;
    private final HeightMapPathfinder pathfinder;
    private final TerrainSwitch terrainSwitch;
    private final ObjectLoader loader;

    private final ModelManager modelManager;
    private final PathManager pathManager;
    private final GameVarManager gameVarManager;
    private final EntitiesManager entitiesManager;
    private final GuiElementManager guiElementManager;
    private final GameStateManager gameStateManager;
    private final TerrainManager terrainManager;

    /**
     * The constructor of the input manager.
     * It initializes the input manager.
     * Extracts the instances of every class and manager from the main field manager.
     *
     * @param context The main field manager.
     */
    public InputManager(@NotNull MainFieldManager context) {

        this.context = context;

        this.window = context.getWindow();
        this.camera = context.getCamera();
        this.scene = context.getScene();
        this.mouseInputs = context.getMouseInputs();
        this.heightMap = context.getHeightMap();
        this.pathfinder = context.getPathfinder();
        this.terrainSwitch = context.getTerrainSwitch();
        this.loader = context.getLoader();

        this.modelManager = context.getModelManager();
        this.pathManager = context.getPathManager();
        this.gameVarManager = context.getGameVarManager();
        this.entitiesManager = context.getEntitiesManager();
        this.guiElementManager = context.getGuiElementManager();
        this.gameStateManager = context.getGameStateManager();
        this.terrainManager = context.getTerrainManager();
    }

    public void movementControl() {

        Vector3f cameraInc = new Vector3f(0, 0, 0);

        float moveSpeed = Consts.CAMERA_MOVEMENT_SPEED / EngineManager.getFps();

        // Movement controls
        if (gameStateManager.canMove()) {
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
                restartBalls();
                if (!gameStateManager.isAiBotAnimating() && !gameStateManager.isBotAnimating()) {
                    entitiesManager.getAiBotBall().setPosition(pathManager.getStartPoint());
                    entitiesManager.getBotBall().setPosition(pathManager.getStartPoint());
                    gameVarManager.resetCurrentShotIndexAI();
                    gameVarManager.resetCurrentShotIndexBot();
                }
            }

            // Create a tree at the camera position
            boolean isTPressed = window.is_keyPressed(GLFW.GLFW_KEY_T);

            if (isTPressed && !gameStateManager.istKeyWasPressed()) {
                plantTree();
            } else if (!isTPressed) {
                gameStateManager.settKeyWasPressed(false); // Reset the flag when the key is released
            }

            if (window.is_keyPressed(GLFW.GLFW_KEY_1) && gameStateManager.isAiBot()) {
                gameStateManager.setAiBotAnimating(true);
            }

            if (window.is_keyPressed(GLFW.GLFW_KEY_2) && gameStateManager.isBot()) {
                gameStateManager.setBotAnimating(true);
            }

        }

        context.setCameraInc(cameraInc);
    }

    /**
     * Restart the balls to the start point.
     */
    private void restartBalls() {
        Vector3f start = new Vector3f(pathManager.getStartPoint());
        if (gameStateManager.is2player()) {
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
    private void plantTree() {
        Vector3f cameraPos = new Vector3f(camera.getPosition().x, camera.getPosition().y - Consts.PLAYER_HEIGHT, camera.getPosition().z);
        Entity newTree = new Entity(modelManager.getTree(), new Vector3f(cameraPos.x, cameraPos.y, cameraPos.z), new Vector3f(-90, 0, 0), 0.03f);
        scene.addEntity(newTree);
        entitiesManager.addTree(newTree);
        entitiesManager.addTreeHeight(cameraPos.y);
        scene.addTreePosition(new float[]{cameraPos.x, cameraPos.y, cameraPos.z});
        gameStateManager.settKeyWasPressed(true); // Mark the key as pressed
        System.out.println("Tree added at: " + cameraPos);
    }

    /**
     * Method to control the camera movement.
     */
    public void cameraMovement() {
        if (gameStateManager.canMove()) {
            if (mouseInputs.isRightButtonPressed()) {
                Vector2f rotVec = mouseInputs.getDisplayVec();
                camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
            }
        } else if (gameStateManager.isOnMenu() && gameStateManager.isGuiVisible()) {
            camera.moveRotation(0, 0.1f, 0);
        }
    }

    /**
     * Controller for creating the start and end points in debug mode.
     */
    public void startEndPointDebugMode() {
        if (debugMode && gameStateManager.canMove()) {
            if (window.is_keyPressed(GLFW.GLFW_KEY_LEFT)) {
                setUpStartPoint();
            }
            if (window.is_keyPressed(GLFW.GLFW_KEY_RIGHT) && gameStateManager.hasStartPoint()) {
                setUpEndPoint();
            }
        }
    }


    /**
     * Set up the start Point in debug mode.
     */
    private void setUpStartPoint() {
        if (!gameStateManager.hasStartPoint()) { // Ensure the start point is only set once
            scene.getEntities().removeAll(entitiesManager.getTrees());
            heightMap.createHeightMap();
            Vector3f startPoint = new Vector3f(camera.getPosition().x, camera.getPosition().y - Consts.PLAYER_HEIGHT, camera.getPosition().z); // Create a new instance to avoid reference issues
            pathManager.setStartPoint(startPoint);
            entitiesManager.setGolfBallPosition(startPoint);
            if (gameStateManager.is2player()) {
                entitiesManager.setGolfBall2Position(startPoint);
            }
            gameStateManager.setHasStartPoint(true);
            System.out.println("Start point set: " + startPoint); // Print with more decimal places for clarity
        }
    }

    /**
     * Set up the end Point in debug mode.
     */
    private void setUpEndPoint() {
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
            TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture(Consts.HEIGHTMAP));
            terrainSwitch.terrainSwitch(terrainManager.getBlendMapTerrain(), modelManager.getTree(), blendMap2);
        } catch (Exception ignore) {
        }
        entitiesManager.setEndFlagPosition(endPoint);
        gameStateManager.setHasStartPoint(false);
    }

}
