package com.um_project_golf.Game;

import com.um_project_golf.Core.*;
import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.Entity.Terrain.HeightMapPathfinder;
import com.um_project_golf.Core.Entity.Terrain.Terrain;
import com.um_project_golf.Core.Entity.Texture;
import com.um_project_golf.Core.Rendering.RenderManager;
import com.um_project_golf.Core.Utils.CollisionsDetector;
import com.um_project_golf.Core.Utils.Consts;
import com.um_project_golf.Core.Utils.StartEndPoint;
import com.um_project_golf.Core.Utils.TerrainSwitch;
import com.um_project_golf.Game.FieldManager.*;
import com.um_project_golf.Game.GUIs.DefaultGUI;
import com.um_project_golf.Game.GUIs.InGameGUI;
import com.um_project_golf.Game.GUIs.MenuGUI;
import com.um_project_golf.Game.GUIs.RecreateGUIs;
import com.um_project_golf.Game.GameLogic.InitManager;
import com.um_project_golf.Game.GameLogic.InputManager;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static org.lwjgl.nanovg.NanoVGGL3.*;

/**
 * The main game logic class.
 * This class is responsible for initializing the game, handling input, updating the game state and rendering the game.
 */
public class GolfGame implements ILogic {

    // Debug mode for Examination purposes
    public static boolean debugMode = false; //Do not change this value to true,
    // it will break the game (you can change it to true in-game only)

    // NanoVG context
    private long vg;

    // Main game Classes and Managers
    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;
    private final SceneManager scene;
    private MouseInput mouseInputs;
    private AudioManager audioManager;
    private final HeightMap heightMap;
    private final HeightMapPathfinder pathfinder;
    private final CollisionsDetector collisionsDetector;

    // Utilities for the game
    private final TerrainSwitch terrainSwitch;
    private final StartEndPoint startEndPoint;

    // Field Managers for the game
    private final GameStateManager gameStateManager;
    private final GuiElementManager guiElementManager;
    private final EntitiesManager entitiesManager;
    private final ModelManager modelManager;
    private final TerrainManager terrainManager;
    private final GameVarManager gameVarManager;
    private final PathManager pathManager;

    private final InitManager initManager;
    private final InputManager inputManager;

    // Camera (Player)
    private final Camera camera;
    private final Vector3f cameraInc;

    /**
     * The constructor of the game.
     * It initializes the renderer, window, loader and camera.
     */
    public GolfGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        scene = new SceneManager(-90);
        camera = new Camera();
        pathfinder = new HeightMapPathfinder();
        collisionsDetector = new CollisionsDetector();
        cameraInc = new Vector3f(0, 0, 0);
        heightMap = new HeightMap();
        gameStateManager = new GameStateManager();
        guiElementManager = new GuiElementManager();
        entitiesManager = new EntitiesManager();
        modelManager = new ModelManager();
        terrainManager = new TerrainManager();
        gameVarManager = new GameVarManager();
        pathManager = new PathManager();
        terrainSwitch = new TerrainSwitch(scene, renderer, heightMap, loader, terrainManager, modelManager, entitiesManager);
        startEndPoint = new StartEndPoint();

        initManager = new InitManager();
        inputManager = new InputManager();
    }

    /**
     * Initializes the game.
     * It loads the model and texture of the game.
     *
     * @throws Exception If the game fails to initialize.
     */
    @Override
    public void init(MouseInput mouseInput) throws Exception {
        System.out.println("Initializing game");

        mouseInputs = mouseInput;

        scene.setDefaultTexture(new Texture(loader.loadTexture("src/main/resources/Texture/Default.png")));
        window.setAntiAliasing(true);
        window.setResized(false);

        renderer.init();
        window.setClearColor(0.529f, 0.808f, 0.922f, 0.0f);

        heightMap.createHeightMap();
        List<Vector2i> path = pathfinder.getPath(Consts.RADIUS_DOWN, Consts.RADIUS_UP, Consts.SIZE_GREEN); // upper and lower bounds for the radius of the path
        pathManager.setPath(path);

        initManager.modelAndEntityCreation(scene, entitiesManager, modelManager, startEndPoint, pathManager, gameStateManager, gameVarManager, terrainSwitch, heightMap, loader);
        initManager.terrainCreation(terrainManager, scene, loader);
        initManager.setUpLight(scene);

        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);

        gameStateManager.setGuiVisible(true);
        gameStateManager.setCanMove(false);
        gameStateManager.setOnMenu(true);

        audioManager = new AudioManager("src/main/resources/SoundTrack/skippy-mr-sunshine-fernweh-goldfish-main-version-02-32-7172.wav");
        audioManager.playSound();
        gameStateManager.setSoundPlaying(true);

        new MenuGUI(camera, vg, guiElementManager,
                gameStateManager, modelManager, pathManager, terrainManager, entitiesManager,
                gameVarManager, heightMap, pathfinder, audioManager, scene,
                terrainManager.getBlendMapTerrain(), loader, mouseInputs,
                terrainSwitch, startEndPoint);

        new InGameGUI(vg, camera, audioManager, guiElementManager,
                gameStateManager, gameVarManager, entitiesManager);

        new DefaultGUI(vg, heightMap, scene, gameStateManager,
                entitiesManager, gameVarManager, guiElementManager);
    }

    /**
     * Handles the input of the game.
     * It sets the cameraInc vector based on the input of the user.
     */
    @Override
    public void input() {
        cameraInc.set(0, 0, 0);
        //float lightPos = scene.getSpotLights()[0].getPointLight().getPosition().z;
        //float lightPos2 = scene.getSpotLights()[1].getPointLight().getPosition().z;

        if (window.is_keyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            gameStateManager.setGuiVisible(true);  // Toggle GUI visibility
            gameStateManager.setCanMove(false); // Disable movement
        }

        inputManager.movementControl(gameStateManager, window, cameraInc,
                camera, pathManager, gameVarManager, entitiesManager,
                guiElementManager, scene, modelManager);

        inputManager.cameraMovement(gameStateManager, mouseInputs, camera);

        inputManager.startEndPointDebugMode(gameStateManager, window, scene,
                entitiesManager, heightMap, pathManager, camera,
                pathfinder, terrainManager, terrainSwitch,
                modelManager, loader);
    }

    /**
     * Updates the game state.
     * It moves the camera and the entity based on the input of the user.
     */
    @Override
    public void update() {
        guiElementManager.update(gameStateManager);

        if (window.isResized()) {
            Vector3f oldPosition = new Vector3f(camera.getPosition());
            window.setResized(false);

            new RecreateGUIs(guiElementManager, camera, vg,
                    gameStateManager, modelManager, pathManager, entitiesManager,
                    gameVarManager, heightMap, pathfinder, audioManager, scene,
                    terrainManager, loader, mouseInputs, terrainSwitch, startEndPoint);

            mouseInputs.init();
            camera.setPosition(oldPosition);
        }

        camera.movePosition(cameraInc.x * Consts.CAMERA_MOVEMENT_SPEED, (cameraInc.y * Consts.CAMERA_MOVEMENT_SPEED), cameraInc.z * Consts.CAMERA_MOVEMENT_SPEED);

        collisionsDetector.checkCollision(camera, cameraInc, heightMap, scene);

        daytimeCycle();

        updateTreeAnimations();

        animateBall();

        guiElementManager.updateTextFields(gameStateManager, debugMode);

        for (Entity entity : scene.getEntities()) {
            renderer.processEntity(entity);
        }

        for (Terrain terrain : scene.getTerrains()) {
            renderer.processTerrain(terrain);
        }
    }

    /**
     * Renders the game.
     * It renders the entity and the camera.
     */
    @Override
    public void render() {
        renderer.clear();
        renderer.render(camera, scene);

        guiElementManager.render(gameStateManager);

        Entity arrowEntity = entitiesManager.getArrowEntity();
        if (gameStateManager.isAnimating()) {
            scene.getEntities().remove(arrowEntity);
        } else {
            scene.addEntity(arrowEntity);
        }
    }

    /**
     * Cleans up the game.
     * It cleans up the renderer and loader.
     */
    @Override
    public void cleanUp() {
        renderer.cleanup();
        loader.cleanUp();
        audioManager.cleanup();
        guiElementManager.cleanup();

        nvgDelete(vg);
    }

    /**
     * Make the day and night cycle.
     * Not used for now.
     */
    private void daytimeCycle() {
        scene.increaseSpotAngle(0.01f);
        if (scene.getSpotAngle() > 4) {
            scene.setSpotInc(-1);
        } else if (scene.getSpotAngle() < -4) {
            scene.setSpotInc(1);
        }

        scene.increaseLightAngle(1.1f);
        scene.setLightAngle(65);
        scene.getDirectionalLight().setIntensity(0.5f);

//        if (scene.getLightAngle() > 90) {
//            scene.getDirectionalLight().setIntensity(0);
//            if (scene.getLightAngle() >= 360)
//                scene.setLightAngle(-90);
//        } else if (scene.getLightAngle() <= -80 || scene.getLightAngle() >= 80) {
//            float factor = 1 - (Math.abs(scene.getLightAngle()) - 80) / 10.0f;
//            scene.getDirectionalLight().setIntensity(factor);
//            scene.getDirectionalLight().getColor().x = Math.max(factor, 0.9f);
//            scene.getDirectionalLight().getColor().z = Math.max(factor, 0.5f);
//        } else {
//            scene.getDirectionalLight().setIntensity(1);
//            scene.getDirectionalLight().getColor().x = 1;
//            scene.getDirectionalLight().getColor().z = 1;
//            scene.getDirectionalLight().getColor().y = 1;
//        }
//
//        double angle = Math.toRadians(scene.getLightAngle());
//        scene.getDirectionalLight().getDirection().x = (float) Math.sin(angle);
//        scene.getDirectionalLight().getDirection().y = (float) Math.cos(angle);
    }

    /**
     * Update the ball for multiplayer.
     * Switches the player's turn.
     * Used for multiplayer.
     */
    private void updateBallMultiplayer() {
        if (gameStateManager.isIs2player()) {
            gameStateManager.switchPlayer1Turn();
            boolean isPlayer1Turn = gameStateManager.isPlayer1Turn();
            entitiesManager.updateCurrentBall(isPlayer1Turn);
            System.out.println("Player " + (isPlayer1Turn ? "1's" : "2's") + " turn");
            guiElementManager.getCurrentPlayer().setText("Player " + (isPlayer1Turn ? "1's" : "2's") + " turn");
            Entity currentBall = entitiesManager.getCurrentBall();
            int numberOfShots = gameVarManager.getNumberOfShots();
            int numberOfShots2 = gameVarManager.getNumberOfShots2();
            guiElementManager.getInfoTextPane().setText("Position: (" + (int) currentBall.getPosition().x + ", " + (int) currentBall.getPosition().z + "). Number of shots: " + (!isPlayer1Turn ? numberOfShots : numberOfShots2));
        }
    }

    /**
     * Update the Directional Arrow.
     * Used for the ball's direction.
     */
    private void updateDirectionalArrow() {
        String vx = guiElementManager.getVxTextField().getText().replaceAll("[a-zA-Z]", "");
        String vz = guiElementManager.getVzTextField().getText().replaceAll("[a-zA-Z]", "");

        if (vx.isEmpty() || vz.isEmpty()) return;
        if (vx.equals("Enter vx") || vz.equals("Enter vz")) return;
        if (isNotValidFloat(vx) || isNotValidFloat(vz)) return;

        float vxValue = Float.parseFloat(vx);
        float vzValue = Float.parseFloat(vz);

        Vector3f position = entitiesManager.getCurrentBall().getPosition();
        Vector3f rotation = entitiesManager.getArrowEntity().getRotation();

        // Calculate the direction vector from vx and vz
        Vector3f direction = new Vector3f(vxValue, 0, vzValue).normalize();

        // Compute the y rotation based on the direction vector
        float yRotation = (float) Math.toDegrees(Math.atan2(direction.x, direction.z));

        // Update the arrow entity's position and rotation
        entitiesManager.setArrowEntityPosition(new Vector3f(position.x, position.y + .5f, position.z));
        entitiesManager.setArrowEntityRotation(new Vector3f(rotation.x, yRotation - 90, rotation.z));
    }

    /**
     * Update the tree animations.
     * Used for end game animation.
     */
    private void updateTreeAnimations() {
        if (entitiesManager.getTrees().isEmpty() || gameVarManager.isTreeAnimationIdle()) {
            return;
        }

        gameVarManager.incrementTreeAnimationTime(0.1f); // Adjust the time increment as needed

        // Total duration of the animation (5 seconds up and 5 seconds down)
        float treeAnimationDuration = 10f;
        float t = gameVarManager.getTreeAnimationTime() / (treeAnimationDuration / 2);
        if (t > 1f) t = 1f;

        for (int i = 0; i < entitiesManager.getTrees().size(); i++) {
            Entity tree = entitiesManager.getTrees().get(i);
            float baseHeight = entitiesManager.getTreeHeights().get(i);
            float treeHeightOffset = 10f;

            if (gameVarManager.isTreeAnimationGoingUp()) {
                if (gameVarManager.getTreeAnimationTime() <= treeAnimationDuration / 2) {
                    float newY = baseHeight + treeHeightOffset * t;
                    float newRotation = -90 + 180 * t;
                    tree.setPosition(tree.getPosition().x, newY, tree.getPosition().z);
                    tree.setRotation(newRotation, tree.getRotation().y, tree.getRotation().z);
                } else {
                    gameVarManager.setTreeAnimationGoingDown();
                    gameVarManager.resetTreeAnimationTime();
                }
            } else if(gameVarManager.isTreeAnimationGoingDown()) {
                if (gameVarManager.getTreeAnimationTime() <= treeAnimationDuration / 2) {
                    float newY = baseHeight + treeHeightOffset * (1 - t);
                    float newRotation = 90 + 180f * t;
                    tree.setPosition(tree.getPosition().x, newY, tree.getPosition().z);
                    tree.setRotation(newRotation, tree.getRotation().y, tree.getRotation().z);
                } else {
                    gameVarManager.setTreeAnimationIdle();}
            }
        }
    }

    /**
     * Animate the ball.
     * Used for the ball's movement.
     * Makes the ball move to the end point.
     */
    private void animateBall() {
        if (gameStateManager.isAnimating()) {
            float timeStep = 0.1f;
            gameVarManager.incrementAnimationTimeAccumulator(timeStep);

            if (gameVarManager.getAnimationTimeAccumulator() >= timeStep) {
                gameVarManager.decrementAnimationTimeAccumulator(timeStep);
                List<Vector3f> ballPositions = gameVarManager.getBallPositions();

                if (gameVarManager.getCurrentPositionIndex() < ballPositions.size()) {
                    Vector3f nextPosition = ballPositions.get(gameVarManager.getCurrentPositionIndex());

                    if (nextPosition == ballPositions.get(ballPositions.size() - 1)) {
                        float isInHoleThreshold = Consts.TARGET_RADIUS;
                        Vector3f endPoint = new Vector3f(pathManager.getEndPoint());
                        if (nextPosition.x <= endPoint.x + isInHoleThreshold && nextPosition.x >= endPoint.x - isInHoleThreshold) {
                            if (nextPosition.z <= endPoint.z + isInHoleThreshold && nextPosition.z >= endPoint.z - isInHoleThreshold) {
                                int numberOfShots = gameVarManager.getNumberOfShots();
                                int numberOfShots2 = gameVarManager.getNumberOfShots2();
                                int shot = gameStateManager.isPlayer1Turn() ? numberOfShots : numberOfShots2;
                                System.out.println("Ball reached the end point!");
                                System.out.println("You took " + shot + " shots to reach the end point!");
                                System.out.println(endPoint);
                                if (gameStateManager.isIs2player()) gameStateManager.setPlayer1Won(gameStateManager.isPlayer1Turn());
                                guiElementManager.getCurrentPlayer().setText("Player " + (gameStateManager.isPlayer1Won() ? "1" : "2") + " wins!");
                                System.out.println("Player " + (gameStateManager.isPlayer1Won() ? "1" : "2") + " wins!");
                                guiElementManager.getWarningTextPane().setText("You Win! In " + shot + " shots!");
                                gameVarManager.setTreeAnimationGoingUp();
                                gameVarManager.resetTreeAnimationTime();
                            }
                        }
                    }

                    gameVarManager.getBallCollisionDetector().checkCollisionBall(nextPosition);
                    if (nextPosition.y <= -0.1) { // Ball in water
                        entitiesManager.setCurrentBallPosition(gameVarManager.getShotStartPosition());
                        gameStateManager.setAnimating(false);
                        updateBallMultiplayer();
                        guiElementManager.getWarningTextPane().setText("Ploof! Ball in water! Resetting to last shot position.");
                    } else {
                        entitiesManager.setCurrentBallPosition(nextPosition);
                        gameVarManager.incrementCurrentPositionIndex();}
                } else {
                    gameStateManager.setAnimating(false); // Animation completed
                    updateBallMultiplayer();
                }
            }
        } else {
            updateDirectionalArrow();
        }
    }

    /**
     * Checks if the string is a valid float.
     *
     * @param str The string to check.
     * @return True if the string is not a valid float, false otherwise.
     */
    private boolean isNotValidFloat(String str) {
        try {
            Float.parseFloat(str);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
