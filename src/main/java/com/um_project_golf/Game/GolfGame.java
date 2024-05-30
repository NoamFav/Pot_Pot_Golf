package com.um_project_golf.Game;

import com.um_project_golf.Core.AWT.Button;
import com.um_project_golf.Core.AWT.TextField;
import com.um_project_golf.Core.AWT.TextPane;
import com.um_project_golf.Core.AWT.Title;
import com.um_project_golf.Core.*;
import com.um_project_golf.Core.Entity.*;
import com.um_project_golf.Core.Entity.Terrain.*;
import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
import com.um_project_golf.Core.Rendering.RenderManager;
import com.um_project_golf.Core.Utils.BallCollisionDetector;
import com.um_project_golf.Core.Utils.CollisionsDetector;
import com.um_project_golf.Core.Utils.Consts;
import com.um_project_golf.Core.Entity.Terrain.HeightMapPathfinder;
import com.um_project_golf.GolfBots.AIBot;
import com.um_project_golf.GolfBots.RuleBasedBot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static org.lwjgl.nanovg.NanoVGGL3.*;

/**
 * The main game logic class.
 * This class is responsible for initializing the game, handling input, updating the game state and rendering the game.
 */
public class GolfGame implements ILogic {

    // Enum for the animation state of the trees
    private enum AnimationState {IDLE, GOING_UP, GOING_DOWN}

    // Records for storing the models and terrains
    private record ModelLoader(List<Model> skyBox, List<Model> ball, List<Model> arrow, List<Model> flag,
                               List<Model> mill) {
    }

    private record Terrains(TerrainTexture blendMap, List<TerrainTexture> textures,
                            List<TerrainTexture> waterTextures) {
    }

    // Records for storing the Runnables for the buttons
    private record InGameMenuRunnable(Runnable resume, Runnable backToMenu, Runnable sound, Runnable quit) {
    }

    private record MenuRunnable(Runnable terrainChanger, Runnable startGame, Runnable sound, Runnable quit,
                                Runnable enableDebugMode) {
    }

    // Debug mode for Examination purposes
    public static boolean debugMode = false; //Do not change this value to true,
    // it will break the game (you can change it to true in the game only)

    // NanoVG context
    private long vg;

    // Main game Classes and Managers
    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;
    private final SceneManager scene;
    private MouseInput mouseInputs;
    private AudioManager audioManager;
    private final HeightMapPathfinder pathfinder;
    private final CollisionsDetector collisionsDetector;
    private BallCollisionDetector ballCollisionDetector;

    // GUI elements
    private Title title;
    private final List<Button> menuButtons;
    private final List<Button> inGameMenuButtons;
    private final String imageButton = "src/main/resources/Texture/buttons.png";

    // Menu GUI elements
    private Button startButton;
    private Button debugButton;
    private Button twoPlayerButton;
    private Button aiBotButton;
    private Button botButton;
    private Button soundButton;
    private Button soundButtonInGame;

    // In-game GUI elements
    private Button applyButton;
    private TextField vxTextField;
    private TextField vzTextField;
    private TextPane vxTextPane;
    private TextPane vzTextPane;
    private TextPane infoTextPane;
    private TextPane warningTextPane;
    private TextPane currentPlayer;

    // A* pathfinding variables
    private Vector3f startPoint;
    private Vector3f endPoint;
    private List<Vector2i> path;

    // Models
    private List<Model> botBallModel;
    private List<Model> aiBotBallModel;
    private List<Model> ball2;
    private List<Model> tree;

    // Entities
    private final List<Entity> trees;
    private final List<Float> treeHeights;
    private Entity golfBall;
    private Entity golfBall2;
    private Entity currentBall;
    private Entity botBall;
    private Entity aiBotBall;
    private Entity endFlag;
    private Entity arrowEntity;

    // Terrains
    private Terrain terrain;
    private Terrain ocean;
    private final HeightMap heightMap;
    private BlendMapTerrain blendMapTerrain;
    private BlendMapTerrain blueTerrain;

    // Camera (Player)
    private final Camera camera;
    Vector3f cameraInc;

    // Game state variables
    private boolean canMove = false;
    private boolean isGuiVisible = true;
    private boolean isOnMenu = true;
    private boolean gameStarted = false;
    private boolean isSoundPlaying = false;
    private boolean isAnimating;
    private boolean isBot;
    private boolean isAiBot;
    private boolean is2player;
    private boolean isPlayer1Turn;
    private boolean player1Won;
    private boolean hasStartPoint = false;
    private boolean tKeyWasPressed = false;

    // Game logic variables
    private AnimationState treeAnimationState = AnimationState.IDLE;
    private float treeAnimationTime = 0f;
    private int numberOfShots;
    private int numberOfShots2;
    private int currentPositionIndex;
    private float animationTimeAccumulator;
    private Vector3f shotStartPosition;

    // Positions of the balls for Animation purposes
    private List<Vector3f> ballPositions;

    // Path for the bots, not used currently as a problem with movement issue and threading issues.
    @SuppressWarnings("unused")
    private List<List<Vector3f>> botPath; // Path followed by the bot
    @SuppressWarnings("unused")
    private List<List<Vector3f>> aiBotPath; // Path followed by AI bot

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
        menuButtons = new ArrayList<>();
        inGameMenuButtons = new ArrayList<>();
        trees = new ArrayList<>();
        treeHeights = new ArrayList<>();
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
        path = pathfinder.getPath(Consts.RADIUS_DOWN, Consts.RADIUS_UP, Consts.SIZE_GREEN); // upper and lower bounds for the radius of the path

        modelAndEntityCreation();
        terrainCreation();
        setUpLight();

        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        createMenu(blendMapTerrain);
        createInGameMenu();
        isGuiVisible = true;
        canMove = false;
        isOnMenu = true;

        createDefaultGui();

        audioManager = new AudioManager("src/main/resources/SoundTrack/skippy-mr-sunshine-fernweh-goldfish-main-version-02-32-7172.wav");
        audioManager.playSound();
        isSoundPlaying = true;
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
            isGuiVisible = true;  // Toggle GUI visibility
            canMove = false;  // Disable movement
        }

        // Movement controls
        if (canMove) {
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
                camera.setPosition(startPoint);
            }
            if (window.is_keyPressed(GLFW.GLFW_KEY_DOWN)) {
                camera.setPosition(endPoint);
            }

            if (window.is_keyPressed(GLFW.GLFW_KEY_F) && treeAnimationState == AnimationState.IDLE) {
                treeAnimationState = AnimationState.GOING_UP;
                treeAnimationTime = 0f;
            }

            if (window.is_keyPressed(GLFW.GLFW_KEY_Q)) {
                camera.setPosition(new Vector3f(currentBall.getPosition().x, currentBall.getPosition().y + Consts.PLAYER_HEIGHT, currentBall.getPosition().z));
            }

            if (window.is_keyPressed(GLFW.GLFW_KEY_R) && !isAnimating) {
                restartBalls();
            }

            // Create a tree at the camera position
            boolean isTPressed = window.is_keyPressed(GLFW.GLFW_KEY_T);

            if (isTPressed && !tKeyWasPressed) {
                plantTree();
            } else if (!isTPressed) {
                tKeyWasPressed = false;  // Reset the flag when the key is released
            }
        }

        if (debugMode && canMove) {
            if (window.is_keyPressed(GLFW.GLFW_KEY_LEFT)) {
                setUpStartPoint();
            }
            if (window.is_keyPressed(GLFW.GLFW_KEY_RIGHT) && hasStartPoint) {
                setUpEndPoint();
            }
        }

        if (canMove) {
            if (mouseInputs.isRightButtonPressed()) {
                Vector2f rotVec = mouseInputs.getDisplayVec();
                camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
            }
        } else if (isOnMenu && isGuiVisible) {
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
        if (gameStarted) {
            vxTextField.update();
            vzTextField.update();
            applyButton.update();
        }

        if (isGuiVisible) {
            if (isOnMenu) {
                for (Button button : menuButtons) {
                    button.update();
                }
            } else {
                for (Button button : inGameMenuButtons) {
                    button.update();
                }
            }
        }

        if (window.isResized()) {
            Vector3f oldPosition = new Vector3f(camera.getPosition());
            window.setResized(false);
            recreateGUIs();
            mouseInputs.init();
            camera.setPosition(oldPosition);
        }

        camera.movePosition(cameraInc.x * Consts.CAMERA_MOVEMENT_SPEED, (cameraInc.y * Consts.CAMERA_MOVEMENT_SPEED), cameraInc.z * Consts.CAMERA_MOVEMENT_SPEED);

        collisionsDetector.checkCollision(camera, cameraInc, heightMap, scene);

        daytimeCycle();

        updateTreeAnimations();

        animateBall();

        updateTextFields();

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

        // Render your UI elements like menuButtons
        if (isGuiVisible) {
            if (isOnMenu) {
                for (Button button : menuButtons) {
                    if (!Objects.equals(button.getText(), "Change Terrain")) {
                        button.render();
                    } else {
                        if (!debugMode) {
                            button.render();
                        }
                    }
                }
                title.render();
            } else {
                for (Button button : inGameMenuButtons) {
                    button.render();
                }
            }
        }

        if (isAnimating) {
            scene.getEntities().remove(arrowEntity);
        } else {
            scene.addEntity(arrowEntity);
        }

        if (gameStarted) {
            warningTextPane.render();
            infoTextPane.render();
            applyButton.render();
            vxTextField.render();
            vzTextField.render();
            vxTextPane.render();
            vzTextPane.render();
            currentPlayer.render();
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
        if (title != null) {
            title.cleanup();  // Clean up the title resources
        }
        for (Button button : menuButtons) {
            button.cleanup();
        }
        for (Button button : inGameMenuButtons) {
            button.cleanup();
        }

        warningTextPane.cleanup();
        infoTextPane.cleanup();
        applyButton.cleanup();
        vxTextField.cleanup();
        vzTextField.cleanup();
        vxTextPane.cleanup();
        vzTextPane.cleanup();
        currentPlayer.cleanup();

        nvgDelete(vg);
    }

    /**
     * Creates the models and entities for the game.
     * Uses a record to store the models.
     */
    private void modelAndEntityCreation() throws Exception {

        ModelLoader models = getModels();

        scene.addEntity(new Entity(models.skyBox(), new Vector3f(0, -10, 0), new Vector3f(90, 0, 0), Consts.SIZE_X / 2));

        arrowEntity = new Entity(models.arrow(), new Vector3f(0, 0, 0), new Vector3f(0, -90, 0), 2);
        scene.addEntity(arrowEntity);

        startEndPointConversion();

        System.out.println("Start point: " + startPoint);
        System.out.println("End point: " + endPoint);

        endFlag = new Entity(models.flag(), new Vector3f(endPoint), new Vector3f(0, 0, 0), 150);
        scene.addEntity(endFlag);

        golfBall = new Entity(models.ball(), new Vector3f(startPoint), new Vector3f(50, 0, 0), 5);
        scene.addEntity(golfBall);

        isPlayer1Turn = true;
        currentBall = golfBall;

        numberOfShots2 = 0;
        numberOfShots = 0;

        createTrees();
    }

    /**
     * Create the terrain for the game.
     * Uses a record to store the terrains.
     */
    private void terrainCreation() throws Exception {
        Terrains terrains = getTerrains();

        blendMapTerrain = new BlendMapTerrain(terrains.textures());
        blueTerrain = new BlendMapTerrain(terrains.waterTextures());

        terrain = new Terrain(new Vector3f(-Consts.SIZE_X / 2, 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0, 0, 0, 0), 0.1f), blendMapTerrain, terrains.blendMap(), false);
        ocean = new Terrain(new Vector3f(-Consts.SIZE_X / 2, 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0, 0, 0, 0), 0.1f), blueTerrain, terrains.blendMap(), true);
        scene.addTerrain(terrain);
        scene.addTerrain(ocean);
        ocean.getModel().getMaterial().setDisableCulling(true);
    }

    /**
     * Create the gui when in game.
     */
    private void createDefaultGui() {
        float width = window.getWidthConverted(1000);
        float height = window.getHeightConverted(300);
        float x = window.getWidthConverted(10);
        float y = window.getHeightConverted(10);
        float font = window.getUniformScaleFactorFont(70);
        float textFieldFont = window.getUniformScaleFactorFont(50);

        currentPlayer = new TextPane(x, y, width, height / 2, "Player 1's turn", font, vg, imageButton);
        infoTextPane = new TextPane(x, y + height / 2, width, height / 2, "Position: (" + (int) currentBall.getPosition().x + ", " + (int) currentBall.getPosition().z + "). Number of shots: " + (isPlayer1Turn ? numberOfShots : numberOfShots2), textFieldFont, vg, imageButton);
        warningTextPane = new TextPane(x, y + height, width, height / 2, "", textFieldFont * .8f, vg, imageButton);
        ballCollisionDetector = new BallCollisionDetector(heightMap, scene);

        // Creating text-fields and text panes for entering the velocities
        vxTextPane = new TextPane(x * 4, y * 30 + height / 2, width / 5, height / 2, "vx: ", font, vg, imageButton);
        vxTextField = new TextField(x * 25, y * 30 + height / 2, width / 3, height / 2, "Enter vx", textFieldFont, vg, imageButton);

        vzTextPane = new TextPane(x * 4, y * 30 + height, width / 5, height / 2, "vz: ", font, vg, imageButton);
        vzTextField = new TextField(x * 25, y * 30 + height, width / 3, height / 2, "Enter vz", textFieldFont, vg, imageButton);

        setUpCallbacks();

        applyButton = new Button(x, y * 30 + height + height / 2, 3 * width / 5, 2 * height / 3, "Apply Velocity", font, runPhysics(), vg, imageButton);
    }

    /**
     * Creates the menu.
     *
     * @param blendMapTerrain The terrain to create the menu for.
     */
    private void createMenu(BlendMapTerrain blendMapTerrain) {

        camera.setPosition(new Vector3f(Consts.SIZE_X / 4, 50, Consts.SIZE_Z / 4));
        camera.setRotation(20, 0, 0);

        float width = window.getWidth();

        MenuRunnable runnable = getRunnable(blendMapTerrain);

        float titleWidth = window.getWidthConverted(1200);
        float titleHeight = window.getHeightConverted(1200);
        float titleX = (window.getWidth() - titleWidth) / 2;
        float titleY = window.getHeightConverted(10);

        title = new Title("src/main/resources/Texture/title.png", titleX, titleY, titleWidth, titleHeight, vg);

        float heightButton = window.getHeightConverted(300);
        float widthButton = window.getWidthConverted(2000);
        float centerButtonX = (width - widthButton) / 2;
        float centerButtonY = titleHeight + titleY;
        float font = window.getUniformScaleFactorFont(100);

        startButton = new Button(centerButtonX, centerButtonY, widthButton, heightButton, "Start", font, runnable.startGame(), vg, imageButton);
        menuButtons.add(startButton);

        Button changeTerrain = new Button(centerButtonX, centerButtonY + heightButton, widthButton, heightButton, "Change Terrain", font, runnable.terrainChanger(), vg, imageButton);
        menuButtons.add(changeTerrain);

        soundButton = new Button(window.getWidth() - window.getWidthConverted(450), window.getHeightConverted(20), window.getWidthConverted(400), heightButton, "Sound: ON", font, runnable.sound(), vg, imageButton);
        menuButtons.add(soundButton);

        Button exit = new Button(centerButtonX, centerButtonY + heightButton * 2, widthButton, heightButton, "Exit", font, runnable.quit(), vg, imageButton);
        menuButtons.add(exit);

        botButton = new Button(window.getWidthConverted(30), window.getHeight() - heightButton * 2, widthButton / 4, heightButton, "Play with bot", font, () -> isBot = !isBot, vg, imageButton);
        menuButtons.add(botButton);

        aiBotButton = new Button(window.getWidthConverted(30), window.getHeight() - heightButton * 3, widthButton / 4, heightButton, "Play with AI", font, () -> isAiBot = !isAiBot, vg, imageButton);
        menuButtons.add(aiBotButton);

        twoPlayerButton = new Button(window.getWidthConverted(30), window.getHeight() - heightButton * 4, widthButton / 4, heightButton, "2 Player", font, () -> is2player = !is2player, vg, imageButton);
        menuButtons.add(twoPlayerButton);

        debugButton = new Button(window.getWidthConverted(30), window.getHeight() - heightButton, widthButton / 4, heightButton, "Debug Mode", font * 0.7f, runnable.enableDebugMode(), vg, imageButton);
        menuButtons.add(debugButton);
    }

    /**
     * Creates the in-game menu.
     */
    private void createInGameMenu() {

        InGameMenuRunnable runnable = getInGameMenuRunnable();

        float heightButton = window.getHeightConverted(300);
        float widthButton = window.getWidthConverted(2000);
        float centerButtonX = (window.getWidth() - widthButton) / 2;
        float centerButtonY = (window.getHeight() - heightButton * 3) / 2;
        float font = window.getHeightConverted(100);

        Button resumeButton = new Button(centerButtonX, centerButtonY, widthButton, heightButton, "Resume", font, runnable.resume(), vg, "src/main/resources/Texture/inGameMenu.png");
        inGameMenuButtons.add(resumeButton);

        Button backToMenuButton = new Button(centerButtonX, centerButtonY + heightButton, widthButton, heightButton, "Back to Menu", font, runnable.backToMenu(), vg, "src/main/resources/Texture/inGameMenu.png");
        inGameMenuButtons.add(backToMenuButton);

        soundButtonInGame = new Button(centerButtonX, centerButtonY + heightButton * 2, widthButton, heightButton, "Sound", font, runnable.sound(), vg, "src/main/resources/Texture/inGameMenu.png");
        inGameMenuButtons.add(soundButtonInGame);

        Button exitButton = new Button(centerButtonX, centerButtonY + heightButton * 3, widthButton, heightButton, "Exit", font, runnable.quit(), vg, "src/main/resources/Texture/inGameMenu.png");
        inGameMenuButtons.add(exitButton);
    }

    /**
     * Recreates the GUIs.
     */
    private void recreateGUIs() {
        menuButtons.clear();
        inGameMenuButtons.clear();

        createMenu(blendMapTerrain);
        createInGameMenu();
        createDefaultGui();
    }

    /**
     * Sets up the callbacks for the text fields.
     */
    private void setUpCallbacks() {
        GLFW.glfwSetKeyCallback(window.getWindow(), (window, key, scancode, action, mods) -> {
            vxTextField.handleKeyInput(key, action, mods);
            vzTextField.handleKeyInput(key, action, mods);
        });

        GLFW.glfwSetMouseButtonCallback(window.getWindow(), (window, button, action, mods) -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                double[] xPos = new double[1];
                double[] yPos = new double[1];
                GLFW.glfwGetCursorPos(window, xPos, yPos);
                vxTextField.handleMouseClick((float) xPos[0], (float) yPos[0]);
                vzTextField.handleMouseClick((float) xPos[0], (float) yPos[0]);
            }
        });
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
        Vector3f start = new Vector3f(startPoint);
        if (is2player) {
            golfBall.setPosition(start);
            golfBall2.setPosition(start);
            numberOfShots = 0;
            numberOfShots2 = 0;
            isPlayer1Turn = true;
            currentBall = golfBall;
            currentPlayer.setText("Player 1's turn");
        } else {
            golfBall.setPosition(start);
            numberOfShots = 0;
        }
        infoTextPane.setText("Position: (" + (int) currentBall.getPosition().x + ", " + (int) currentBall.getPosition().z + "). Number of shots: " + (isPlayer1Turn ? numberOfShots : numberOfShots2));
    }

    /**
     * Save the forest
     * Plant a tree at the camera position.
     */
    private void plantTree() {
        Vector3f cameraPos = new Vector3f(camera.getPosition().x, camera.getPosition().y - Consts.PLAYER_HEIGHT, camera.getPosition().z);
        Entity newTree = new Entity(tree, new Vector3f(cameraPos.x, cameraPos.y, cameraPos.z), new Vector3f(-90, 0, 0), 0.03f);
        scene.addEntity(newTree);
        trees.add(newTree);
        treeHeights.add(cameraPos.y);
        scene.addTreePosition(new float[]{cameraPos.x, cameraPos.y, cameraPos.z});
        tKeyWasPressed = true;  // Mark the key as pressed
        System.out.println("Tree added at: " + cameraPos);
    }

    /**
     * Set up the start Point in debug mode.
     */
    private void setUpStartPoint() {
        if (!hasStartPoint) { // Ensure the start point is only set once
            scene.getEntities().removeAll(trees);
            heightMap.createHeightMap();
            startPoint = new Vector3f(camera.getPosition().x, camera.getPosition().y - Consts.PLAYER_HEIGHT, camera.getPosition().z); // Create a new instance to avoid reference issues
            golfBall.setPosition(startPoint);
            if (is2player) {
                golfBall2.setPosition(startPoint);
            }
            hasStartPoint = true;
            System.out.println("Start point set: " + startPoint); // Print with more decimal places for clarity
        }
    }

    /**
     * Set up the end Point in debug mode.
     */
    private void setUpEndPoint() {
        endPoint = new Vector3f(camera.getPosition().x, camera.getPosition().y - Consts.PLAYER_HEIGHT, camera.getPosition().z); // Create a new instance to avoid reference issues

        Vector2i start = new Vector2i((int) startPoint.x, (int) startPoint.z);
        start.x = (int) ((start.x + Consts.SIZE_X / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_X));
        start.y = (int) ((start.y + Consts.SIZE_Z / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_Z));

        Vector2i end = new Vector2i((int) endPoint.x, (int) endPoint.z);
        end.x = (int) ((end.x + Consts.SIZE_X / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_X));
        end.y = (int) ((end.y + Consts.SIZE_Z / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_X));

        System.out.println("Start point: " + start);
        System.out.println("End point: " + end);

        path = pathfinder.getPathDebug(start, end, Consts.SIZE_GREEN);
        try {
            TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/heightmap.png"));
            terrainSwitch(blendMapTerrain, tree, blendMap2);
            if (Consts.WANT_TREE) { // If the trees are enabled in the Consts
                createTrees();
            }
        } catch (Exception ignore) {
        }
        endFlag.setPosition(endPoint);
        hasStartPoint = false;
    }

    /**
     * Update the text fields.
     * Used for On/Off buttons.
     */
    private void updateTextFields() {
        if (isBot && isAiBot) {
            startButton.setText("Start with Ai Bot and Bot (really long)");
        } else if (isBot) {
            startButton.setText("Start with Bot (long)");
        } else if (isAiBot) {
            startButton.setText("Start with Ai Bot (kinda long)");
        } else {
            startButton.setText("Start");
        }

        twoPlayerButton.setText(is2player ? "2 Player: On" : "2 Player: Off");
        botButton.setText(isBot ? "Bot: On" : "Bot: Off");
        aiBotButton.setText(isAiBot ? "AI Bot: On" : "AI Bot: Off");
        debugButton.setText(debugMode ? "Debug: On" : "Debug: Off");
    }

    /**
     * Update the ball for multiplayer.
     * Switches the player's turn.
     * Used for multiplayer.
     */
    private void updateBallMultiplayer() {
        if (is2player) {
            isPlayer1Turn = !isPlayer1Turn;
            currentBall = isPlayer1Turn ? golfBall : golfBall2;
            System.out.println("Player " + (isPlayer1Turn ? "1's" : "2's") + " turn");
            currentPlayer.setText("Player " + (isPlayer1Turn ? "1's" : "2's") + " turn");
            infoTextPane.setText("Position: (" + (int) currentBall.getPosition().x + ", " + (int) currentBall.getPosition().z + "). Number of shots: " + (isPlayer1Turn ? numberOfShots : numberOfShots2));
        }
    }

    /**
     * Update the Directional Arrow.
     * Used for the ball's direction.
     */
    private void updateDirectionalArrow() {
        String vx = vxTextField.getText().replaceAll("[a-zA-Z]", "");
        String vz = vzTextField.getText().replaceAll("[a-zA-Z]", "");

        if (vx.isEmpty() || vz.isEmpty()) return;
        if (vx.equals("Enter vx") || vz.equals("Enter vz")) return;
        if (isNotValidFloat(vx) || isNotValidFloat(vz)) return;

        float vxValue = Float.parseFloat(vx);
        float vzValue = Float.parseFloat(vz);

        Vector3f position = currentBall.getPosition();
        Vector3f rotation = arrowEntity.getRotation();

        // Calculate the direction vector from vx and vz
        Vector3f direction = new Vector3f(vxValue, 0, vzValue).normalize();

        // Compute the y rotation based on the direction vector
        float yRotation = (float) Math.toDegrees(Math.atan2(direction.x, direction.z));

        // Update the arrow entity's position and rotation
        arrowEntity.setPosition(position.x, position.y + .5f, position.z);
        arrowEntity.setRotation(rotation.x, yRotation - 90, rotation.z);
    }

    /**
     * Update the tree animations.
     * Used for end game animation.
     */
    private void updateTreeAnimations() {
        if (trees.isEmpty() || treeAnimationState == AnimationState.IDLE) {
            return;
        }

        treeAnimationTime += 0.1f; // Adjust the time increment as needed

        // Total duration of the animation (5 seconds up and 5 seconds down)
        float treeAnimationDuration = 10f;
        float t = treeAnimationTime / (treeAnimationDuration / 2);
        if (t > 1f) t = 1f;

        for (int i = 0; i < trees.size(); i++) {
            Entity tree = trees.get(i);
            float baseHeight = treeHeights.get(i);
            float treeHeightOffset = 10f;

            switch (treeAnimationState) {
                case GOING_UP:
                    if (treeAnimationTime <= treeAnimationDuration / 2) {
                        float newY = baseHeight + treeHeightOffset * t;
                        float newRotation = -90 + 180 * t;
                        tree.setPosition(tree.getPosition().x, newY, tree.getPosition().z);
                        tree.setRotation(newRotation, tree.getRotation().y, tree.getRotation().z);
                    } else {
                        treeAnimationState = AnimationState.GOING_DOWN;
                        treeAnimationTime = 0f;
                    }
                    break;

                case GOING_DOWN:
                    if (treeAnimationTime <= treeAnimationDuration / 2) {
                        float newY = baseHeight + treeHeightOffset * (1 - t);
                        float newRotation = 90 + 180f * t;
                        tree.setPosition(tree.getPosition().x, newY, tree.getPosition().z);
                        tree.setRotation(newRotation, tree.getRotation().y, tree.getRotation().z);
                    } else {
                        treeAnimationState = AnimationState.IDLE;
                    }
                    break;
            }
        }
    }

    /**
     * Animate the ball.
     * Used for the ball's movement.
     * Makes the ball move to the end point.
     */
    private void animateBall() {
        if (isAnimating) {
            float timeStep = 0.1f;
            animationTimeAccumulator += timeStep;

            if (animationTimeAccumulator >= timeStep) {
                animationTimeAccumulator -= timeStep;

                if (currentPositionIndex < ballPositions.size()) {
                    Vector3f nextPosition = ballPositions.get(currentPositionIndex);

                    if (nextPosition == ballPositions.get(ballPositions.size() - 1)) {
                        float isInHoleThreshold = Consts.TARGET_RADIUS;
                        if (nextPosition.x <= endPoint.x + isInHoleThreshold && nextPosition.x >= endPoint.x - isInHoleThreshold) {
                            if (nextPosition.z <= endPoint.z + isInHoleThreshold && nextPosition.z >= endPoint.z - isInHoleThreshold) {
                                int shot = isPlayer1Turn ? numberOfShots : numberOfShots2;
                                System.out.println("Ball reached the end point!");
                                System.out.println("You took " + shot + " shots to reach the end point!");
                                System.out.println(endPoint);
                                if (is2player) player1Won = isPlayer1Turn;
                                currentPlayer.setText("Player " + (player1Won ? "1" : "2") + " wins!");
                                System.out.println("Player " + (player1Won ? "1" : "2") + " wins!");
                                warningTextPane.setText("You Win! In " + shot + " shots!");
                                treeAnimationState = AnimationState.GOING_UP;
                                treeAnimationTime = 0f;
                            }
                        }
                    }

                    ballCollisionDetector.checkCollisionBall(nextPosition);
                    if (nextPosition.y <= -0.1) { // Ball in water
                        currentBall.setPosition(shotStartPosition.x, shotStartPosition.y, shotStartPosition.z);
                        isAnimating = false;
                        updateBallMultiplayer();
                        warningTextPane.setText("Ploof! Ball in water! Resetting to last shot position.");
                    } else {
                        currentBall.setPosition(nextPosition.x, nextPosition.y, nextPosition.z);
                        currentPositionIndex++;
                    }

                } else {
                    isAnimating = false; // Animation completed
                    updateBallMultiplayer();
                }
            }
        } else {
            updateDirectionalArrow();
        }
    }

    /**
     * Place the trees on the terrain.
     * Randomly picks a position on the terrain.
     * As long as the position is green
     */
    private void createTrees() throws IOException {
        BufferedImage heightmapImage = ImageIO.read(new File("src/main/resources/Texture/heightmap.png"));

        List<Vector3f> positions = new ArrayList<>();

        // Populate positions based on the heightmap image
        for (int x = 0; x < heightmapImage.getWidth(); x++) {
            for (int z = 0; z < heightmapImage.getHeight(); z++) {
                Color pixelColor = new Color(heightmapImage.getRGB(x, z));

                // Check for green or blue pixels
                if (pixelColor.equals(Color.GREEN)) {
                    float terrainX = x / (float) heightmapImage.getWidth() * Consts.SIZE_X - Consts.SIZE_X / 2;
                    float terrainZ = z / (float) heightmapImage.getHeight() * Consts.SIZE_Z - Consts.SIZE_Z / 2;
                    float terrainY = heightMap.getHeight(new Vector3f(terrainX, 0, terrainZ));

                    positions.add(new Vector3f(terrainX, terrainY, terrainZ));
                }
            }
        }

        // Ensure we have valid positions
        if (positions.isEmpty()) {
            System.out.println("No valid positions found for trees.");
            return;
        }

        List<float[]> treePositions = new ArrayList<>();
        Random rnd = new Random();
        Vector3f zero = new Vector3f(0, 0, 0);

        // Populate tree positions and add entities to the scene
        for (int i = 0; i < Consts.NUMBER_OF_TREES; i++) {
            Vector3f position = positions.get(rnd.nextInt(positions.size()));
            if (position != zero) {
                Entity aTree = new Entity(tree, new Vector3f(position.x, position.y, position.z), new Vector3f(-90, 0, 0), 0.03f); // - 90 and 0.03f
                scene.addEntity(aTree);
                trees.add(aTree);
                treeHeights.add(position.y);
                treePositions.add(new float[]{position.x, position.y, position.z});
            } else {
                System.out.println("Position is null at index: " + i);
            }
        }
        System.out.println("Tree length: " + treePositions.size());

        scene.setTreePositions(treePositions);
    }

    /**
     * Convert distance from meters to vertices.
     */
    private void startEndPointConversion() {
        startPoint = new Vector3f(path.get(0).x, 0, path.get(0).y);
        System.out.println("Start point: " + startPoint);
        startPoint.x = (int) (startPoint.x / 4 - Consts.SIZE_X / 2);
        startPoint.z = (int) (startPoint.z / 4 - Consts.SIZE_Z / 2);
        startPoint.y = heightMap.getHeight(new Vector3f(startPoint.x, 0, startPoint.z));

        endPoint = new Vector3f(path.get(path.size() - 1).x, 0, path.get(path.size() - 1).y);
        System.out.println("End point: " + endPoint);
        endPoint.x = (int) (endPoint.x / 4) - Consts.SIZE_X / 2;
        endPoint.z = (int) (endPoint.z / 4) - Consts.SIZE_Z / 2;
        endPoint.y = heightMap.getHeight(new Vector3f(endPoint.x, 0, endPoint.z));
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
     * Switches the terrain of the game.
     *
     * @param blendMapTerrain The new terrain to switch to.
     * @param tree            The tree model to add to the new terrain.
     * @param blendMap2       The new blend map to use.
     */
    private void terrainSwitch(BlendMapTerrain blendMapTerrain, List<Model> tree, TerrainTexture blendMap2) {
        scene.getTerrains().remove(terrain);
        scene.getTerrains().remove(ocean);
        terrain = new Terrain(new Vector3f(-Consts.SIZE_X / 2, 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0, 0, 0, 0), 0.1f), blendMapTerrain, blendMap2, false);
        ocean = new Terrain(new Vector3f(-Consts.SIZE_X / 2, 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0, 0, 0, 0), 0.1f), blueTerrain, blendMap2, true);
        scene.addTerrain(terrain);
        scene.addTerrain(ocean);
        scene.getEntities().removeIf(entity -> entity.getModels().equals(tree));
        try {
            if (!debugMode) {
                createTrees();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        renderer.processTerrain(terrain);
    }

    /**
     * Gets the model for the game.
     * Uses a record to store the models.
     *
     * @return The models for the game.
     */
    private @NotNull ModelLoader getModels() throws Exception {
        tree = loader.loadAssimpModel("src/main/resources/Models/tree/tree.obj");
        List<Model> skyBox = loader.loadAssimpModel("src/main/resources/Models/Skybox/SkyBox.obj");
        List<Model> ball = loader.loadAssimpModel("src/main/resources/Models/Ball/ImageToStl.com_ball.obj");
        ball2 = loader.loadAssimpModel("src/main/resources/Models/Ball/ImageToStl.com_ball.obj");
        botBallModel = loader.loadAssimpModel("src/main/resources/Models/Ball/ImageToStl.com_ball.obj");
        aiBotBallModel = loader.loadAssimpModel("src/main/resources/Models/Ball/ImageToStl.com_ball.obj");
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

    /**
     * Gets the runnable for the menu.
     *
     * @param blendMapTerrain The terrain to create the menu for.
     * @return The runnable for the menu.
     */
    private @NotNull MenuRunnable getRunnable(BlendMapTerrain blendMapTerrain) {
        Runnable terrainChanger = () -> {
            try {
                trees.clear();
                System.out.println("Changing terrain");
                heightMap.createHeightMap();
                path = pathfinder.getPath(Consts.RADIUS_DOWN, Consts.RADIUS_UP, Consts.SIZE_GREEN);
                startEndPointConversion();

                TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/heightmap.png"));
                SimplexNoise.shufflePermutation();
                terrainSwitch(blendMapTerrain, tree, blendMap2);
                golfBall.setPosition(startPoint.x, startPoint.y, startPoint.z);
                if (is2player) {
                    golfBall2.setPosition(startPoint.x, startPoint.y, startPoint.z);
                }
                endFlag.setPosition(endPoint.x, endPoint.y, endPoint.z);
                numberOfShots = 0;
                numberOfShots2 = 0;
                infoTextPane.setText("Position: (" + (int) currentBall.getPosition().x + ", " + (int) currentBall.getPosition().z + "). Number of shots: " + (isPlayer1Turn ? numberOfShots : numberOfShots2));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        Runnable startGame = () -> {
            System.out.println("Starting game");
            camera.setPosition(startPoint);
            camera.setRotation(0, 0, 0);
            canMove = true;
            isGuiVisible = false;
            isOnMenu = false;
            gameStarted = true;

            if (isBot) {
                createBotBall().run();
            } else {
                if (botBall != null) scene.getEntities().removeIf(entity -> entity.equals(botBall));
            }

            if (isAiBot) {
                createAiBotBall().run();
            } else {
                if (aiBotBall != null) scene.getEntities().removeIf(entity -> entity.equals(aiBotBall));
            }

            Vector3f start = new Vector3f(startPoint);
            start.y = heightMap.getHeight(new Vector3f(startPoint.x, 0, startPoint.z));

            Vector3f end = new Vector3f(endPoint);
            end.y = heightMap.getHeight(new Vector3f(endPoint.x, 0, endPoint.z));

            if (is2player) {
                if (golfBall2 == null) {
                    golfBall2 = new Entity(ball2, new Vector3f(start), new Vector3f(50, 0, 0), 5);
                    scene.addEntity(golfBall2);
                }
            } else {
                if (golfBall2 != null) {
                    scene.getEntities().removeIf(entity -> entity.equals(golfBall2));
                }
            }

            golfBall.setPosition(start);
            endFlag.setPosition(end);
        };

        Runnable sound = () -> {
            if (isSoundPlaying) {
                System.out.println("Stopping sound");
                audioManager.stopSound();
                soundButton.setText("Sound: OFF");
                soundButtonInGame.setText("Sound: OFF");
            } else {
                System.out.println("Playing sound");
                audioManager.playSound();
                soundButton.setText("Sound: ON");
                soundButtonInGame.setText("Sound: ON");
            }
            isSoundPlaying = !isSoundPlaying;
        };

        Runnable quit = () -> {
            System.out.println("Quitting game");
            GLFW.glfwSetWindowShouldClose(Launcher.getWindow().getWindow(), true);
        };

        Runnable enableDebugMode = () -> {
            trees.clear();
            debugMode = !debugMode;
            if (debugMode) {
                System.out.println("Enabling debug mode");
                if (golfBall2 == null && is2player) {
                    golfBall2 = new Entity(ball2, new Vector3f(0, 0, 0), new Vector3f(50, 0, 0), 5);
                    scene.addEntity(golfBall2);
                }
                try {
                    heightMap.createHeightMap();
                    TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/heightmap.png"));
                    terrainSwitch(blendMapTerrain, tree, blendMap2);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                if (Consts.USE_PREDEFINED_POSITIONS && debugMode) {
                    endPoint = new Vector3f(Consts.HOLE_POSITION.x, heightMap.getHeight(new Vector3f(Consts.HOLE_POSITION.x, 0, Consts.HOLE_POSITION.z)), Consts.HOLE_POSITION.z);
                    startPoint = new Vector3f(Consts.TEE_POSITION.x, heightMap.getHeight(new Vector3f(Consts.TEE_POSITION.x, 0, Consts.TEE_POSITION.z)), Consts.TEE_POSITION.z);

                    Vector2i start = new Vector2i((int) startPoint.x, (int) startPoint.z);
                    start.x = (int) ((start.x + Consts.SIZE_X / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_X));
                    start.y = (int) ((start.y + Consts.SIZE_Z / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_X));

                    Vector2i end = new Vector2i((int) endPoint.x, (int) endPoint.z);
                    end.x = (int) ((end.x + Consts.SIZE_X / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_X));
                    end.y = (int) ((end.y + Consts.SIZE_Z / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_X));

                    System.out.println("Start point: " + start);
                    System.out.println("End point: " + end);

                    path = pathfinder.getPathDebug(start, end, Consts.SIZE_GREEN);
                    try {
                        TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture("src/main/resources/Texture/heightmap.png"));
                        terrainSwitch(blendMapTerrain, tree, blendMap2);
                        if (Consts.WANT_TREE) { // If the trees are enabled in the Consts
                            createTrees();
                        }
                    } catch (Exception ignore) {
                    }
                } else {
                    // Can be changed with the left and right arrow keys
                    endPoint = new Vector3f(0, 0, 0);
                    startPoint = new Vector3f(0, 0, 0);
                }

                golfBall.setPosition(startPoint.x, heightMap.getHeight(new Vector3f(startPoint.x, 0, startPoint.z)), startPoint.z);
                if (is2player) {
                    golfBall2.setPosition(startPoint.x, heightMap.getHeight(new Vector3f(startPoint.x, 0, startPoint.z)), startPoint.z);
                }
                endFlag.setPosition(endPoint.x, heightMap.getHeight(new Vector3f(endPoint.x, 0, endPoint.z)), endPoint.z);
            } else {
                System.out.println("Disabling debug mode");
                terrainChanger.run();
            }

            recreateGUIs();
            mouseInputs.init();
        };
        return new MenuRunnable(terrainChanger, startGame, sound, quit, enableDebugMode);
    }

    /**
     * Gets the runnable for the in-game menu.
     *
     * @return The runnable for the in-game menu.
     */
    private @NotNull InGameMenuRunnable getInGameMenuRunnable() {
        Runnable resume = () -> {
            System.out.println("Resuming game");
            canMove = true;
            isGuiVisible = false;
        };

        Runnable backToMenu = () -> {
            System.out.println("Returning to menu");
            camera.setPosition(new Vector3f(Consts.SIZE_X / 4, 50, Consts.SIZE_Z / 4));
            camera.setRotation(20, 0, 0);
            canMove = false;
            isGuiVisible = true;
            isOnMenu = true;
            gameStarted = false;
            numberOfShots = 0;
            numberOfShots2 = 0;
            infoTextPane.setText("Position: (" + (int) currentBall.getPosition().x + ", " + (int) currentBall.getPosition().z + "). Number of shots: " + (isPlayer1Turn ? numberOfShots : numberOfShots2));
        };

        Runnable sound = () -> {
            if (isSoundPlaying) {
                System.out.println("Stopping sound");
                audioManager.stopSound();
                soundButtonInGame.setText("Sound: OFF");
                soundButton.setText("Sound: OFF");
            } else {
                System.out.println("Playing sound");
                audioManager.playSound();
                soundButtonInGame.setText("Sound: ON");
                soundButton.setText("Sound: ON");
            }
            isSoundPlaying = !isSoundPlaying;
        };

        Runnable quit = () -> {
            System.out.println("Quitting game");
            GLFW.glfwSetWindowShouldClose(Launcher.getWindow().getWindow(), true);
        };
        return new InGameMenuRunnable(resume, backToMenu, sound, quit);
    }

    /**
     * Creates the physics runnable.
     *
     * @return The physics runnable.
     */
    @NotNull
    @Contract(pure = true)
    private Runnable runPhysics() {
        PhysicsEngine engine = new PhysicsEngine(heightMap, scene);

        return () -> {
            if (isAnimating) {
                return; // Exit if already animating
            }
            try {
                warningTextPane.setText("");
                // Remove any non-numeric characters from the text fields if present
                // (backup in case of threading issues)
                double vx = Double.parseDouble(vxTextField.getText().replaceAll("[a-zA-Z]", ""));
                double vz = Double.parseDouble(vzTextField.getText().replaceAll("[a-zA-Z]", ""));
                System.out.println("Applying physics with vx: " + vx + ", vz: " + vz);

                if (Math.abs(vx) > Consts.MAX_SPEED || Math.abs(vz) > Consts.MAX_SPEED) {
                    warningTextPane.setText("Speed too high: max " + Consts.MAX_SPEED + " m/s");
                } else {
                    double[] initialState = {currentBall.getPosition().x, currentBall.getPosition().z, vx, vz}; // initialState = [x, z, vx, vz]
                    double h = 0.1; // Time step
                    ballPositions = engine.runRK4(initialState, h);

                    currentPositionIndex = 0;
                    isAnimating = true;
                    animationTimeAccumulator = 0f;
                    if (isPlayer1Turn) {
                        numberOfShots++;
                    } else {
                        numberOfShots2++;
                    }
                    shotStartPosition = new Vector3f(currentBall.getPosition()); // Store the start position of the shot
                    infoTextPane.setText("Position: (" + (int) currentBall.getPosition().x + ", " + (int) currentBall.getPosition().z + "). Number of shots: " + (isPlayer1Turn ? numberOfShots : numberOfShots2));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Creates the bot ball.
     * And run the simulation.
     *
     * @return The bot ball.
     */
    @NotNull
    @Contract(pure = true)
    public Runnable createBotBall() {

        return () -> {
            System.out.println("Creating bot ball");
            botBall = new Entity(botBallModel, new Vector3f(startPoint), new Vector3f(50, 0, 0), 5);
            scene.addEntity(botBall);

            RuleBasedBot ruleBasedBot = new RuleBasedBot(new Entity(botBall), new Entity(endFlag), heightMap, Consts.TARGET_RADIUS, scene);
            botPath = ruleBasedBot.findBestShot();
        };

    }

    /**
     * Creates the AI bot ball.
     * And run the simulation.
     *
     * @return The AI bot ball.
     */
    @NotNull
    @Contract(pure = true)
    public Runnable createAiBotBall() {

        return () -> {
            System.out.println("Creating AI bot ball");
            aiBotBall = new Entity(aiBotBallModel, new Vector3f(startPoint), new Vector3f(50, 0, 0), 5);
            scene.addEntity(aiBotBall);

            AIBot aiBot = new AIBot(new Entity(aiBotBall), new Entity(endFlag), heightMap, Consts.TARGET_RADIUS, scene);
            aiBotPath = aiBot.findBestShotUsingHillClimbing();
        };

    }
}
