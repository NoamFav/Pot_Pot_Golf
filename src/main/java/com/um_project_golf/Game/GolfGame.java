package com.um_project_golf.Game;

import com.um_project_golf.Core.*;
import com.um_project_golf.Core.Entity.*;
import com.um_project_golf.Core.Entity.Terrain.*;
import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
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
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVGGL3.*;

/**
 * The main game logic class.
 * This class is responsible for initializing the game, handling input, updating the game state and rendering the game.
 */
public class GolfGame implements ILogic {

    // Records for storing the models and terrains
    private record ModelLoader(List<Model> skyBox, List<Model> ball, List<Model> arrow, List<Model> flag, List<Model> mill) {}

    private record Terrains(TerrainTexture blendMap, List<TerrainTexture> textures, List<TerrainTexture> waterTextures) {}

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
    private final GameStateManager gameStateManager;
    private final GuiElementManager guiElementManager;
    private final EntitiesManager entitiesManager;
    private final ModelManager modelManager;
    private final TerrainManager terrainManager;
    private final GameVarManager gameVarManager;
    private final PathManager pathManager;
    private final TerrainSwitch terrainSwitch;
    private final StartEndPoint startEndPoint;

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

        modelAndEntityCreation();
        terrainCreation();
        setUpLight();

        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);

        gameStateManager.setGuiVisible(true);
        gameStateManager.setCanMove(false);
        gameStateManager.setOnMenu(true);

        audioManager = new AudioManager("src/main/resources/SoundTrack/skippy-mr-sunshine-fernweh-goldfish-main-version-02-32-7172.wav");
        audioManager.playSound();
        gameStateManager.setSoundPlaying(true);

        new MenuGUI(camera, window, vg, guiElementManager,
                gameStateManager, modelManager, pathManager, terrainManager, entitiesManager,
                gameVarManager, heightMap, pathfinder, audioManager, scene,
                terrainManager.getBlendMapTerrain(), loader, mouseInputs,
                terrainSwitch, startEndPoint);

        new InGameGUI(vg, window, camera, audioManager,
                guiElementManager, gameStateManager, gameVarManager,
                entitiesManager);

        new DefaultGUI(window, vg, heightMap, scene, gameStateManager,
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

        float moveSpeed = Consts.CAMERA_MOVEMENT_SPEED / EngineManager.getFps();

        if (window.is_keyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            gameStateManager.setGuiVisible(true);  // Toggle GUI visibility
            gameStateManager.setCanMove(false); // Disable movement
        }

        movementControl(moveSpeed);

        if (debugMode && gameStateManager.isCanMove()) {
            if (window.is_keyPressed(GLFW.GLFW_KEY_LEFT)) {
                setUpStartPoint();
            }
            if (window.is_keyPressed(GLFW.GLFW_KEY_RIGHT) && gameStateManager.isHasStartPoint()) {
                setUpEndPoint();
            }
        }

        if (gameStateManager.isCanMove()) {
            if (mouseInputs.isRightButtonPressed()) {
                Vector2f rotVec = mouseInputs.getDisplayVec();
                camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
            }
        } else if (gameStateManager.isOnMenu() && gameStateManager.isGuiVisible()) {
            camera.moveRotation(0, 0.1f, 0);
        }

        // Not used for now (unused)
        // Controls for moving the lights
//        if (window.is_keyPressed(GLFW.GLFW_KEY_I)) {
//            scene.getSpotLights()[0].getPointLight().getPosition().z = lightPos + 0.1f;
//        }
//        if (window.is_keyPressed(GLFW.GLFW_KEY_K)) {
//            scene.getSpotLights()[0].getPointLight().getPosition().z = lightPos - 0.1f;
//        }
//        if (window.is_keyPressed(GLFW.GLFW_KEY_L)) {
//            scene.getSpotLights()[0].getPointLight().getPosition().x += 0.1f;
//        }
//        if (window.is_keyPressed(GLFW.GLFW_KEY_J)) {
//            scene.getSpotLights()[0].getPointLight().getPosition().x -= 0.1f;
//        }
//        if (window.is_keyPressed(GLFW.GLFW_KEY_O)) {
//            scene.getSpotLights()[0].getPointLight().getPosition().y += 0.1f;
//        }
//        if (window.is_keyPressed(GLFW.GLFW_KEY_U)) {
//            scene.getSpotLights()[0].getPointLight().getPosition().y -= 0.1f;
//        }
//        if (window.is_keyPressed(GLFW.GLFW_KEY_0)) {
//            scene.getSpotLights()[1].getPointLight().getPosition().z = lightPos2 + 0.1f;
//        }
//        if (window.is_keyPressed(GLFW.GLFW_KEY_P)) {
//            scene.getSpotLights()[1].getPointLight().getPosition().z = lightPos2 - 0.1f;
//        }
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

            new RecreateGUIs(guiElementManager, camera, window, vg,
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

    private void movementControl(float moveSpeed) {
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
                restartBalls();
            }

            // Create a tree at the camera position
            boolean isTPressed = window.is_keyPressed(GLFW.GLFW_KEY_T);

            if (isTPressed && !gameStateManager.istKeyWasPressed()) {
                plantTree();
            } else if (!isTPressed) {
                gameStateManager.settKeyWasPressed(false); // Reset the flag when the key is released
            }
        }
    }

    /**
     * Creates the models and entities for the game.
     * Uses a record to store the models.
     */
    private void modelAndEntityCreation() throws Exception {

        ModelLoader models = getModels();

        scene.addEntity(new Entity(models.skyBox(), new Vector3f(0, -10, 0), new Vector3f(90, 0, 0), Consts.SIZE_X / 2));

        Entity arrowEntity = new Entity(models.arrow(), new Vector3f(0, 0, 0), new Vector3f(0, -90, 0), 2);
        entitiesManager.setArrowEntity(arrowEntity);
        scene.addEntity(arrowEntity);

        startEndPoint.startEndPointConversion(pathManager, heightMap);

        Vector3f startPoint = pathManager.getStartPoint();
        Vector3f endPoint = pathManager.getEndPoint();

        System.out.println("Start point: " + startPoint);
        System.out.println("End point: " + endPoint);

        Entity endFlag = new Entity(models.flag(), new Vector3f(endPoint), new Vector3f(0, 0, 0), 150);
        entitiesManager.setEndFlag(endFlag);
        scene.addEntity(endFlag);

        Entity golfBall = new Entity(models.ball(), new Vector3f(startPoint), new Vector3f(50, 0, 0), 5);
        entitiesManager.setGolfBall(golfBall);
        scene.addEntity(golfBall);

        gameStateManager.setPlayer1Turn(true);
        entitiesManager.setCurrentBall(golfBall);

        gameVarManager.resetNumberOfShots();

        terrainSwitch.createTrees();
    }

    /**
     * Create the terrain for the game.
     * Uses a record to store the terrains.
     */
    private void terrainCreation() throws Exception {
        Terrains terrains = getTerrains();

        BlendMapTerrain blendMapTerrain = new BlendMapTerrain(terrains.textures());
        BlendMapTerrain blueTerrain = new BlendMapTerrain(terrains.waterTextures());
        terrainManager.setBlendMapTerrain(blendMapTerrain);
        terrainManager.setBlueTerrain(blueTerrain);

        Terrain terrain = new Terrain(new Vector3f(-Consts.SIZE_X / 2, 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0, 0, 0, 0), 0.1f), blendMapTerrain, terrains.blendMap(), false);
        Terrain ocean = new Terrain(new Vector3f(-Consts.SIZE_X / 2, 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0, 0, 0, 0), 0.1f), blueTerrain, terrains.blendMap(), true);
        terrainManager.setTerrain(terrain);
        terrainManager.setOcean(ocean);
        scene.addTerrain(terrain);
        scene.addTerrain(ocean);
        ocean.getModel().getMaterial().setDisableCulling(true);
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
     * Set up the light for the game.
     * Not used for now.
     */
    @SuppressWarnings("unused")
    private void setUpLight() {
        float lightIntensity = 10f;

        //point light
        Vector3f lightPosition = new Vector3f(Consts.SIZE_X / 2, 10, 0);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        PointLight pointLight = new PointLight(lightColor, lightPosition, lightIntensity, 0, 0, 1);

        //spotlight 1
        Vector3f coneDir = new Vector3f(0, -50, 0);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        lightIntensity = 2;
        SpotLight spotLight = new SpotLight(new PointLight(new Vector3f(0, 0.25f, 0), new Vector3f(0, 0, 0), lightIntensity), coneDir, cutoff);
        SpotLight spotLight2 = new SpotLight(new PointLight(new Vector3f(0.25f, 0, 0), new Vector3f(0, 0, 0), lightIntensity), coneDir, cutoff);

        //directional light
        lightPosition = new Vector3f(-1, 10, 0);
        lightColor = new Vector3f(1, 1, 1);
        scene.setDirectionalLight(new DirectionalLight(lightColor, lightPosition, lightIntensity));

        //scene.setPointLights(new PointLight[]{pointLight});
        //scene.setSpotLights(new SpotLight[]{spotLight, spotLight2});
    }

    /**
     * Restart the balls to the start point.
     */
    private void restartBalls() {
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
     * Set up the start Point in debug mode.
     */
    private void setUpStartPoint() {
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
            TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/heightmap.png"));
            terrainSwitch.terrainSwitch(terrainManager.getBlendMapTerrain(), modelManager.getTree(), blendMap2);
        } catch (Exception ignore) {
        }
        entitiesManager.setEndFlagPosition(endPoint);
        gameStateManager.setHasStartPoint(false);
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

    /**
     * Gets the model for the game.
     * Uses a record to store the models.
     *
     * @return The models for the game.
     */
    private @NotNull ModelLoader getModels() throws Exception {
        List<Model> tree = loader.loadAssimpModel("src/main/resources/Models/tree/tree.obj"); modelManager.setTree(tree);
        List<Model> skyBox = loader.loadAssimpModel("src/main/resources/Models/Skybox/SkyBox.obj");
        List<Model> ball = loader.loadAssimpModel("src/main/resources/Models/Ball/ImageToStl.com_ball.obj");
        List<Model> ball2 = loader.loadAssimpModel("src/main/resources/Models/Ball/ImageToStl.com_ball.obj"); modelManager.setBall2(ball2);
        List<Model> botBallModel = loader.loadAssimpModel("src/main/resources/Models/Ball/ImageToStl.com_ball.obj"); modelManager.setBotBallModel(botBallModel);
        List<Model> aiBotBallModel = loader.loadAssimpModel("src/main/resources/Models/Ball/ImageToStl.com_ball.obj"); modelManager.setAiBotBallModel(aiBotBallModel);
        List<Model> arrow = loader.loadAssimpModel("src/main/resources/Models/Arrow/Arrow5.obj");
        List<Model> flag = loader.loadAssimpModel("src/main/resources/Models/flag/flag.obj");
        List<Model> tree3 = loader.loadAssimpModel("src/main/resources/Models/sakura/sakura-A.obj");
        List<Model> cloud = loader.loadAssimpModel("src/main/resources/Models/cloud/cloud lowpoly(big) -A.obj");
        List<Model> mill = loader.loadAssimpModel("src/main/resources/Models/mill/LowPolyMill.obj");

        ball.get(0).setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Ball/Ball_texture/Golf_Ball.png")));
        ball2.get(0).setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Ball/Ball_texture/Golf_Ball2.png")));
        botBallModel.get(0).setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Ball/Ball_texture/Golf_Ball.png")));
        aiBotBallModel.get(0).setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Ball/Ball_texture/Golf_Ball.png")));

        for (Model model : tree) model.getMaterial().setDisableCulling(true);
        for (Model model : tree3) model.getMaterial().setDisableCulling(true);
        for (Model model : skyBox) model.getMaterial().setDisableCulling(true);
        for (Model model : arrow) model.getMaterial().setDisableCulling(true);
        for (Model model : flag) model.getMaterial().setDisableCulling(true);
        for (Model model : ball) model.getMaterial().setDisableCulling(true);
        for (Model model : ball2) model.getMaterial().setDisableCulling(true);
        for (Model model : botBallModel) model.getMaterial().setDisableCulling(true);
        for (Model model : aiBotBallModel) model.getMaterial().setDisableCulling(true);
        for (Model model : cloud) model.getMaterial().setDisableCulling(true);
        for (Model model : mill) model.getMaterial().setDisableCulling(true);
        return new ModelLoader(skyBox, ball, arrow, flag, mill);
    }

    /**
     * Gets the terrains for the game.
     * Uses a record to store the terrains.
     *
     * @return The terrains for the game.
     */
    private @NotNull Terrains getTerrains() throws Exception {
        TerrainTexture sand = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/cartoonSand.jpg"));
        TerrainTexture grass = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/cartoonFlowers.jpg"));
        TerrainTexture fairway = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/cartoonGrass.jpg"));
        TerrainTexture water = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/cartoonWater.jpg"));

        //Not used for now (doesn't look good) (don't delete)
        TerrainTexture rock = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/cartoonFlowers.jpg"));
        TerrainTexture dryGrass = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/cartoonFlowers.jpg"));
        TerrainTexture snow = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/cartoonFlowers.jpg"));
        TerrainTexture mold = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/cartoonFlowers.jpg"));

        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/heightmap.png"));

        List<TerrainTexture> textures = new ArrayList<>(List.of(sand, grass, fairway, dryGrass, mold, rock, snow));
        List<TerrainTexture> waterTextures = new ArrayList<>();
        for (TerrainTexture ignored : textures) {
            waterTextures.add(water);
        }
        return new Terrains(blendMap, textures, waterTextures);
    }
}
