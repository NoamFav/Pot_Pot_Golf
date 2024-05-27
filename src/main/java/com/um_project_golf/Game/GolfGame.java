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

    private enum AnimationState {IDLE, GOING_UP, GOING_DOWN}

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;
    private final SceneManager scene;
    private MouseInput mouseInputs;
    private AudioManager audioManager;
    private final HeightMapPathfinder pathfinder;
    private final CollisionsDetector collisionsDetector;
    private long vg;

    private final String imageButton = "Texture/buttons.png";

    private Title title;
    private TextPane pane;
    private final List<Button> menuButtons;
    private final List<Button> inGameMenuButtons;

    private Vector3f startPoint;
    private Vector3f endPoint;
    private List<Vector2i> path;

    private final Camera camera;
    private Terrain terrain;
    private Terrain ocean;
    private final HeightMap heightMap;
    private BlendMapTerrain blendMapTerrain;
    private BlendMapTerrain blueTerrain;

    Vector3f cameraInc;

    private boolean canMove = false;
    private boolean isGuiVisible = true;
    private boolean isOnMenu = true;
    private boolean gameStarted = false;
    private boolean isSoundPlaying = false;
    //private boolean wasPressed = false;
    private boolean isAnimating;
    private boolean isBot;
    private boolean hasStartPoint = false;
    public static boolean debugMode = false;

    private AnimationState treeAnimationState = AnimationState.IDLE;
    private float treeAnimationTime = 0f;

    private List<Model> tree;
    private final List<Entity> trees;
    private final List<Float> treeHeights;
    private Entity golfBall;
    private Entity botBall;
    private Entity endFlag;
    private Entity arrowEntity;
    private BallCollisionDetector ballCollisionDetector;

    private Button applyButton;
    private TextField vxTextField;
    private TextField vzTextField;
    private TextPane vxTextPane;
    private TextPane vzTextPane;
    private TextPane infoTextPane;
    private TextPane warningTextPane;
    private int numberOfShots;
    private List<Model> botBallModel;

    private List<Vector3f> ballPositions;
    private List<List<Vector3f>> botPath;
    private int currentPositionIndex;
    private float animationTimeAccumulator;
    private Vector3f shotStartPosition;

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

        scene.setDefaultTexture(new Texture(loader.loadTexture("Texture/Default.png")));
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

        audioManager = new AudioManager("src/main/resources/SoundTrack/kavinsky.wav");
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

        float moveSpeed = Consts.CAMERA_MOVEMENT_SPEED  / EngineManager.getFps();

        if (window.is_keyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            isGuiVisible = true;  // Toggle GUI visibility
            canMove = false;  // Disable movement
        }

        if (canMove) {
            if(window.is_keyPressed(GLFW.GLFW_KEY_W)) {
                cameraInc.z = -moveSpeed;
            }
            if(window.is_keyPressed(GLFW.GLFW_KEY_S)) {
                cameraInc.z = moveSpeed;
            }
            if(window.is_keyPressed(GLFW.GLFW_KEY_A)) {
                cameraInc.x = -moveSpeed;
            }
            if(window.is_keyPressed(GLFW.GLFW_KEY_D)) {
                cameraInc.x = moveSpeed;
            }
            if(window.is_keyPressed(GLFW.GLFW_KEY_SPACE)) {
                cameraInc.y = Consts.JUMP_FORCE / EngineManager.getFps();
            }
            if(window.is_keyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                cameraInc.y = -Consts.JUMP_FORCE / EngineManager.getFps();
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
        }

        if (debugMode && canMove) {
            if (window.is_keyPressed(GLFW.GLFW_KEY_LEFT)) {
                if (!hasStartPoint) { // Ensure start point is only set once
                    scene.getEntities().removeAll(trees);
                    heightMap.createHeightMap();
                    startPoint = new Vector3f(camera.getPosition()); // Create a new instance to avoid reference issues
                    golfBall.setPosition(startPoint.x, startPoint.y - Consts.PLAYER_HEIGHT, startPoint.z);
                    hasStartPoint = true;
                    System.out.println("Start point set: " + startPoint); // Print with more decimal places for clarity
                }
            }
            if (window.is_keyPressed(GLFW.GLFW_KEY_RIGHT) && hasStartPoint) {
                endPoint = new Vector3f(camera.getPosition()); // Create a new instance to avoid reference issues

                Vector2i start = new Vector2i((int) startPoint.x, (int) startPoint.z);
                start.x = (int) ((start.x + Consts.SIZE_X / 2) * 4);
                start.y = (int) ((start.y + Consts.SIZE_Z / 2) * 4);

                Vector2i end = new Vector2i((int) endPoint.x, (int) endPoint.z);
                end.x = (int) ((end.x + Consts.SIZE_X / 2) * 4);
                end.y = (int) ((end.y + Consts.SIZE_Z / 2) * 4);

                System.out.println("Start point: " + start);
                System.out.println("End point: " + end);

                path = pathfinder.getPathDebug(start, end, 5);
                try {
                    TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture("Texture/heightmap.png"));
                    terrainSwitch(blendMapTerrain, tree, blendMap2);
                    createTrees(tree);
                } catch (Exception ignore) {
                }
                endFlag.setPosition(endPoint.x, endPoint.y - Consts.PLAYER_HEIGHT, endPoint.z);
                hasStartPoint = false;
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
            window.setResized(false);
            recreateGUIs();
            mouseInputs.init();
        }

        camera.movePosition(
                cameraInc.x * Consts.CAMERA_MOVEMENT_SPEED,
                (cameraInc.y * Consts.CAMERA_MOVEMENT_SPEED),
                cameraInc.z * Consts.CAMERA_MOVEMENT_SPEED
        );

        collisionsDetector.checkCollision(camera, cameraInc, heightMap, scene);

        scene.increaseSpotAngle(0.01f);
        if(scene.getSpotAngle() > 4) {
            scene.setSpotInc(-1);
        } else if(scene.getSpotAngle() < -4) {
            scene.setSpotInc(1);
        }

        daytimeCycle();

        updateTreeAnimations();

        if (isAnimating) {
            float timeStep = 0.1f;
            animationTimeAccumulator += timeStep;

            if (animationTimeAccumulator >= timeStep) {
                animationTimeAccumulator -= timeStep;

                if (currentPositionIndex < ballPositions.size()) {
                    Vector3f nextPosition = ballPositions.get(currentPositionIndex);

                    if (nextPosition == ballPositions.get(ballPositions.size() - 1)) {
                        float isInHoleThreshold = 1.5f;
                        if (nextPosition.x <= endPoint.x + isInHoleThreshold && nextPosition.x >= endPoint.x - isInHoleThreshold) {
                            if (nextPosition.z <= endPoint.z + isInHoleThreshold && nextPosition.z >= endPoint.z - isInHoleThreshold) {
                                System.out.println("Ball reached the end point!");
                                System.out.println(endPoint);
                                warningTextPane.setText("Ball reached the end point!");
                                treeAnimationState = AnimationState.GOING_UP;
                                treeAnimationTime = 0f;
                            }
                        }
                    }

                    ballCollisionDetector.checkCollisionBall(nextPosition);
                    if (nextPosition.y <= - 0.3) { // Ball in water
                        golfBall.setPosition(shotStartPosition.x, shotStartPosition.y, shotStartPosition.z);
                        isAnimating = false;
                        warningTextPane.setText("Ball in water! Resetting to last shot position.");
                    } else {
                        golfBall.setPosition(nextPosition.x, nextPosition.y, nextPosition.z);
                        currentPositionIndex++;
                    }

                    if (isBot) {
                        System.out.println("Bot path size: " + botPath.get(numberOfShots-1));
                    }

                } else {
                    isAnimating = false; // Animation completed
                }
            }
        } else {
            updateArrow();
        }

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

        //infoButton.cleanup();
        pane.cleanup();
        nvgDelete(vg);
    }

    private void terrainCreation() throws Exception {

        TerrainTexture sand = new TerrainTexture(loader.loadTexture("Texture/cartoonSand.jpg"));
        TerrainTexture grass = new TerrainTexture(loader.loadTexture("Texture/cartoonFlowers.jpg"));
        TerrainTexture fairway = new TerrainTexture(loader.loadTexture("Texture/cartoonGrass.jpg"));
        TerrainTexture water = new TerrainTexture(loader.loadTexture("Texture/cartoonWater.jpg"));

        //Not used for now (doesn't look good) (don't delete)
        TerrainTexture rock = new TerrainTexture(loader.loadTexture("Texture/cartoonFlowers.jpg"));
        TerrainTexture dryGrass = new TerrainTexture(loader.loadTexture("Texture/cartoonFlowers.jpg"));
        TerrainTexture snow = new TerrainTexture(loader.loadTexture("Texture/cartoonFlowers.jpg"));
        TerrainTexture mold = new TerrainTexture(loader.loadTexture("Texture/cartoonFlowers.jpg"));

        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("Texture/heightmap.png"));

        List<TerrainTexture> textures = new ArrayList<>(List.of(sand, grass, fairway, dryGrass, mold, rock, snow));
        List<TerrainTexture> waterTextures = new ArrayList<>();
        for (TerrainTexture ignored : textures) {
            waterTextures.add(water);
        }

        blendMapTerrain = new BlendMapTerrain(textures);
        blueTerrain = new BlendMapTerrain(waterTextures);

        terrain = new Terrain(new Vector3f(-Consts.SIZE_X/2 , 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0,0,0,0), 0.1f), blendMapTerrain, blendMap, false);
        ocean = new Terrain(new Vector3f(-Consts.SIZE_X / 2, 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0, 0, 0, 0), 0.1f), blueTerrain, blendMap, true);
        scene.addTerrain(terrain);
        scene.addTerrain(ocean);
        ocean.getModel().getMaterial().setDisableCulling(true);
    }

    private void modelAndEntityCreation() throws Exception {
        //Model cube = loader.loadAssimpModel("src/main/resources/Models/Minecraft_Grass_Block_OBJ/SkyBox.obj");
        tree = loader.loadAssimpModel("src/main/resources/Models/tree2/tree3-N.obj");
        List<Model> skyBox = loader.loadAssimpModel("src/main/resources/Models/Skybox/SkyBox.obj");
        List<Model> ball = loader.loadAssimpModel("src/main/resources/Models/Ball/ImageToStl.com_ball.obj");
        botBallModel = loader.loadAssimpModel("src/main/resources/Models/Ball/ImageToStl.com_ball.obj");
        List<Model> arrow = loader.loadAssimpModel("src/main/resources/Models/Arrow/Arrow5.obj");
        List<Model> flag = loader.loadAssimpModel("src/main/resources/Models/flag/flag.obj");
        List<Model> tree2 = loader.loadAssimpModel("src/main/resources/Models/tree2/tree3-N.obj");
        List<Model> tree3 = loader.loadAssimpModel("src/main/resources/Models/sakura/sakura-A.obj");
        List<Model> cloud = loader.loadAssimpModel("src/main/resources/Models/cloud/cloud lowpoly(big) -A.obj");

        ball.get(0).setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Ball/Ball_texture/Golf_Ball.png")));
        botBallModel.get(0).setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Ball/Ball_texture/Golf_Ball.png")));

        for (Model model : tree) model.getMaterial().setDisableCulling(true);
        for (Model model : tree2) model.getMaterial().setDisableCulling(true);
        for (Model model : tree3) model.getMaterial().setDisableCulling(true);
        for (Model model : skyBox) model.getMaterial().setDisableCulling(true);
        for (Model model : arrow) model.getMaterial().setDisableCulling(true);
        for (Model model : flag) model.getMaterial().setDisableCulling(true);
        for (Model model : ball) model.getMaterial().setDisableCulling(true);
        for (Model model : botBallModel) model.getMaterial().setDisableCulling(true);
        for (Model model : cloud) model.getMaterial().setDisableCulling(true);

        scene.addEntity(new Entity(skyBox, new Vector3f(0, -10, 0), new Vector3f(90, 0, 0), Consts.SIZE_X / 2));
        scene.addEntity(new Entity(cloud, new Vector3f(0, 30, 0), new Vector3f(0, 0 , 0), 1 ));

        //scene.addEntity(new Entity(tree2, new Vector3f(0, 10, 0), new Vector3f(0, 0, 0), 5));
        //scene.addEntity(new Entity(tree3, new Vector3f(10, 10, 0), new Vector3f(0, 0, 0), 5));

        arrowEntity = new Entity(arrow, new Vector3f(0, 0, 0), new Vector3f(0,-90 ,0), 2);
        scene.addEntity(arrowEntity);

        startPoint = new Vector3f(path.get(0).x, 0, path.get(0).y);
        System.out.println("Start point: " + startPoint);
        startPoint.x = (int) (startPoint.x / 4 - Consts.SIZE_X / 2);
        startPoint.z = (int) (startPoint.z / 4 - Consts.SIZE_Z / 2);
        startPoint.y = heightMap.getHeight(new Vector3f(startPoint.x, 0 , startPoint.z));

        endPoint = new Vector3f(path.get(path.size() - 1).x ,  0, path.get(path.size() - 1).y);
        System.out.println("End point: " + endPoint);
        endPoint.x = (int) (endPoint.x / 4) - Consts.SIZE_X / 2;
        endPoint.z = (int) (endPoint.z / 4) - Consts.SIZE_Z / 2;
        endPoint.y = heightMap.getHeight(new Vector3f(endPoint.x, 0 , endPoint.z));

        System.out.println("Start point: " + startPoint);
        System.out.println("End point: " + endPoint);

        //scene.addEntity(new Entity(flag, new Vector3f(startPoint.x, heightMap.getHeight(new Vector3f(startPoint.x, 0 , startPoint.y)), startPoint.y), new Vector3f(0, 0, 0), 3));
        endFlag = new Entity(flag, new Vector3f(endPoint), new Vector3f(0, 0, 0), 150);
        scene.addEntity(endFlag);

        golfBall = new Entity(ball, new Vector3f(startPoint), new Vector3f(50, 0, 0), 5);
        scene.addEntity(golfBall);

        createTrees(tree);
    }

    private void daytimeCycle() {

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

    private void setUpLight() {
        float lightIntensity =10f;

        //point light
        Vector3f lightPosition = new Vector3f(Consts.SIZE_X / 2, 10, 0);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        PointLight pointLight = new PointLight(lightColor, lightPosition, lightIntensity, 0,0,1);

        //spotlight 1
        Vector3f coneDir = new Vector3f(0, -50, 0);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        lightIntensity = 2;
        SpotLight spotLight = new SpotLight(new PointLight(new Vector3f(0,0.25f,0), new Vector3f(0,0,0), lightIntensity), coneDir, cutoff);
        SpotLight spotLight2 = new SpotLight(new PointLight(new Vector3f(0.25f,0,0), new Vector3f(0,0,0), lightIntensity), coneDir, cutoff);

        //directional light
        lightPosition = new Vector3f(-1, 10, 0);
        lightColor = new Vector3f(1, 1, 1);
        scene.setDirectionalLight(new DirectionalLight(lightColor, lightPosition, lightIntensity));

        //scene.setPointLights(new PointLight[]{pointLight});
        //scene.setSpotLights(new SpotLight[]{spotLight, spotLight2});
    }

    private void updateArrow() {
        String vx = vxTextField.getText();
        String vz = vzTextField.getText();

        if (vx.isEmpty() || vz.isEmpty()) {
            return;
        }
        if (vx.equals("Enter vx") || vz.equals("Enter vz")) {
            return;
        }
        if (isNotValidFloat(vx) || isNotValidFloat(vz)) {
            return;
        }

        float vxValue = Float.parseFloat(vx);
        float vzValue = Float.parseFloat(vz);

        Vector3f position = golfBall.getPosition();
        Vector3f rotation = arrowEntity.getRotation();

        // Calculate the direction vector from vx and vz
        Vector3f direction = new Vector3f(vxValue, 0, vzValue).normalize();

        // Compute the y rotation based on the direction vector
        float yRotation = (float) Math.toDegrees(Math.atan2(direction.x, direction.z));

        // Update the arrow entity's position and rotation
        arrowEntity.setPosition(position.x, position.y + .5f, position.z);
        arrowEntity.setRotation(rotation.x, yRotation - 90, rotation.z);
    }

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
                    System.out.println("Going up");
                    if (treeAnimationTime <= treeAnimationDuration / 2) {
                        float newY = baseHeight + treeHeightOffset * t;
                        float newRotation = 180 * t;
                        tree.setPosition(tree.getPosition().x, newY, tree.getPosition().z);
                        tree.setRotation(newRotation, tree.getRotation().y, tree.getRotation().z);
                    } else {
                        treeAnimationState = AnimationState.GOING_DOWN;
                        treeAnimationTime = 0f;
                    }
                    break;

                case GOING_DOWN:
                    System.out.println("Going down");
                    if (treeAnimationTime <= treeAnimationDuration / 2) {
                        float newY = baseHeight + treeHeightOffset * (1 - t);
                        float newRotation = 180 + 180f * t;
                        tree.setPosition(tree.getPosition().x, newY, tree.getPosition().z);
                        tree.setRotation(newRotation, tree.getRotation().y, tree.getRotation().z);
                    } else {
                        treeAnimationState = AnimationState.IDLE;
                    }
                    break;
            }
        }
    }

    private boolean isNotValidFloat(String str) {
        try {
            Float.parseFloat(str);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private void createTrees(List<Model> tree) throws IOException {
        BufferedImage heightmapImage = ImageIO.read(new File("Texture/heightmap.png"));

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

        float[][] treePositions = new float[Consts.NUMBER_OF_TREES][3];
        Random rnd = new Random();
        Vector3f zero = new Vector3f(0, 0, 0);

        // Populate tree positions and add entities to the scene
        for (int i = 0; i < Consts.NUMBER_OF_TREES; i++) {
            Vector3f position = positions.get(rnd.nextInt(positions.size()));
            if (position != zero) {
                Entity aTree = new Entity(tree, new Vector3f(position.x, position.y, position.z), new Vector3f(0, 0, 0), 2); // - 90 and 0.03f
                scene.addEntity(aTree);
                trees.add(aTree);
                treeHeights.add(position.y);
                treePositions[i] = new float[]{position.x, position.y, position.z};
            } else {
                System.out.println("Position is null at index: " + i);
            }
        }
        System.out.println("Tree length: " + treePositions.length);

        scene.setTreePositions(treePositions);
    }

    private void createDefaultGui(){
        float width = window.getWidthConverted(1000);
        float height = window.getHeightConverted(300);
        float x = window.getWidthConverted(10);
        float y = window.getHeightConverted(10);
        float font = window.getHeightConverted(70);
        float textFieldFont = window.getHeightConverted(50);

        //infoButton = new Button(x, y, width, height, "Info", 70, () -> {}, vg, imageButton);
        numberOfShots = 0;
        infoTextPane = new TextPane(x, y, width, height / 2, "Position: (" + (int) golfBall.getPosition().x + ", " + (int) golfBall.getPosition().z + "). Number of shots: " + numberOfShots, 40, vg, imageButton);
        warningTextPane = new TextPane(x, y + height / 2, width, height / 2, "", textFieldFont * .8f, vg, imageButton);
        ballCollisionDetector = new BallCollisionDetector(heightMap, scene);
        //infoButton = new Button(x, y, width, height, "Info", font, () -> {}, vg, imageButton);

        // Creating text-fields and text panes for entering the velocities
        vxTextPane = new TextPane(x * 4, y * 30, width / 5, height / 2, "vx: ", font, vg, imageButton);
        vxTextField = new TextField(x * 25, y * 30, width / 3, height / 2, "Enter vx", textFieldFont, vg, imageButton);

        vzTextPane = new TextPane(x * 4, y * 30 + height / 2, width / 5, height / 2, "vz: ", font, vg, imageButton);
        vzTextField = new TextField(x * 25, y * 30 + height / 2, width / 3, height / 2, "Enter vz", textFieldFont, vg, imageButton);

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

        Runnable applyVelocity = getPhysicsRunnable();

        applyButton = new Button(x, y * 30 + height, 3 * width / 5, 2 * height / 3, "Apply Velocity", font, applyVelocity, vg, imageButton);
    }

    @NotNull
    @Contract(pure = true)
    private Runnable getPhysicsRunnable() {
        PhysicsEngine engine = new PhysicsEngine(heightMap, scene);

        return () -> {
            if (isAnimating) {
                return; // Exit if already animating
            }
            try {
                warningTextPane.setText("");
                double vx = Double.parseDouble(vxTextField.getText());
                double vz = Double.parseDouble(vzTextField.getText());
                System.out.println("vx: " + vx + ", vz: " + vz);

                if (Math.abs(vx) > Consts.MAX_SPEED || Math.abs(vz) > Consts.MAX_SPEED) {
                    warningTextPane.setText("Speed too high: max " + Consts.MAX_SPEED + " m/s");
                } else {
                    double[] initialState = {golfBall.getPosition().x, golfBall.getPosition().z, vx, vz}; // initialState = [x, z, vx, vz]
                    double h = 0.1; // Time step
                    ballPositions = engine.runRK4(initialState, h);

                    currentPositionIndex = 0;
                    isAnimating = true;
                    animationTimeAccumulator = 0f;
                    numberOfShots++;
                    shotStartPosition = new Vector3f(golfBall.getPosition()); // Store the start position of the shot
                    infoTextPane.setText("Position: (" + (int) golfBall.getPosition().x + ", " + (int) golfBall.getPosition().z + "). Number of shots: " + numberOfShots);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @NotNull
    @Contract(pure = true)
    public Runnable createBotBall(){

        return () -> {
            botBall = new Entity(botBallModel, new Vector3f(startPoint), new Vector3f(50, 0, 0), 5);
            scene.addEntity(botBall);

            RuleBasedBot ruleBasedBot = new RuleBasedBot(new Entity(botBall), new Entity(endFlag), heightMap, 1.5, scene);
            botPath = ruleBasedBot.findBestShot();
        };

    }

    /**
     * Creates the menu.
     *
     * @param blendMapTerrain The terrain to create the menu for.
     */
    private void createMenu(BlendMapTerrain blendMapTerrain) {

        camera.setPosition(new Vector3f(Consts.SIZE_X/4, 50, Consts.SIZE_Z/4));
        camera.setRotation(20, 0, 0);

        float width = window.getWidth();

        Runnable terrainChanger = () -> {
            try {
                trees.clear();
                System.out.println("Changing terrain");
                heightMap.createHeightMap();
                path = pathfinder.getPath(Consts.RADIUS_DOWN, Consts.RADIUS_UP, Consts.SIZE_GREEN);
                startPoint = new Vector3f(path.get(0).x, 0, path.get(0).y);
                startPoint.x = (int) (startPoint.x / 4 - Consts.SIZE_X / 2);
                startPoint.z = (int) (startPoint.z / 4 - Consts.SIZE_Z / 2);
                startPoint.y = heightMap.getHeight(new Vector3f(startPoint.x, 0 , startPoint.z));

                endPoint = new Vector3f(path.get(path.size() - 1).x, 0, path.get(path.size() - 1).y);
                endPoint.x = (int) (endPoint.x / 4 - Consts.SIZE_X / 2);
                endPoint.z = (int) (endPoint.z / 4 - Consts.SIZE_Z / 2);
                endPoint.y = heightMap.getHeight(new Vector3f(endPoint.x, 0 , endPoint.z));

                TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture("Texture/heightmap.png"));
                SimplexNoise.shufflePermutation();
                terrainSwitch(blendMapTerrain, tree, blendMap2);
                golfBall.setPosition(startPoint.x, startPoint.y, startPoint.z);
                endFlag.setPosition(endPoint.x, endPoint.y, endPoint.z);
                numberOfShots = 0;
                infoTextPane.setText("Position: (" + (int) golfBall.getPosition().x + ", " + (int) golfBall.getPosition().z + "). Number of shots: " + numberOfShots);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        Runnable startGame = () -> {
            System.out.println("Allowing movement");
            camera.setPosition(startPoint);
            camera.setRotation(0, 0, 0);
            canMove = true;
            isGuiVisible = false;
            isOnMenu = false;
            gameStarted = true;

            if(isBot){
                createBotBall().run();
            }

            golfBall.setPosition(startPoint.x, startPoint.y, startPoint.z);
            endFlag.setPosition(endPoint.x, endPoint.y, endPoint.z);
        };

        Runnable sound = () -> {
            System.out.println("Playing sound");
            if (isSoundPlaying) {
                audioManager.stopSound();
            } else {
                audioManager.playSound();
            }
            isSoundPlaying = !isSoundPlaying;
        };

        Runnable quit = () -> {
            System.out.println("Quitting game");
            GLFW.glfwSetWindowShouldClose(Launcher.getWindow().getWindow(), true);
        };

        Runnable enableDebugMode = () -> {
            trees.clear();
            audioManager.stopSound();
            System.out.println("Enabling debug mode");
            debugMode = !debugMode;
            try {
                heightMap.createHeightMap();
                TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture("Texture/heightmap.png"));
                terrainSwitch(blendMapTerrain, tree, blendMap2);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            endPoint = new Vector3f(0, 0, 0);
            startPoint = new Vector3f(0, 0, 0);
            golfBall.setPosition(startPoint.x, heightMap.getHeight(new Vector3f(0,0,0)), startPoint.z);
            endFlag.setPosition(endPoint.x, heightMap.getHeight(new Vector3f(0,0,0)), endPoint.z);

            recreateGUIs();
            mouseInputs.init();

            if (debugMode) {
                audioManager = new AudioManager("src/main/resources/SoundTrack/nothing2.wav");
            } else {
                audioManager = new AudioManager("src/main/resources/SoundTrack/nothing.wav");
            }
            audioManager.playSound();
        };

        float titleWidth = window.getWidthConverted(1200);
        float titleHeight = window.getHeightConverted(1200);
        float titleX = (window.getWidth() - titleWidth) / 2;
        float titleY = window.getHeightConverted(10);

        title = new Title("Texture/title.png", titleX, titleY, titleWidth, titleHeight, vg);

        float heightButton = window.getHeightConverted(300);
        float widthButton = window.getWidthConverted(2000);
        float centerButtonX = (width - widthButton) / 2;
        float centerButtonY = titleHeight + titleY;
        float font = window.getHeightConverted(100);

        Button start = new Button(centerButtonX, centerButtonY, widthButton, heightButton, "Start", font, startGame, vg, imageButton);
        menuButtons.add(start);

        Button changeTerrain = new Button(centerButtonX, centerButtonY + heightButton, widthButton, heightButton, "Change Terrain", font, terrainChanger, vg, imageButton);
        menuButtons.add(changeTerrain);

        Button soundButton = new Button(window.getWidth() - window.getWidthConverted(300), window.getHeightConverted(20), window.getWidthConverted(300), heightButton, "Sound", font, sound, vg, imageButton);
        menuButtons.add(soundButton);

        Button exit = new Button(centerButtonX, centerButtonY + heightButton * 2 , widthButton, heightButton, "Exit", font, quit, vg, imageButton);
        menuButtons.add(exit);

        Button botButton = new Button(window.getWidthConverted(30), window.getHeight() - heightButton * 2, widthButton/4, heightButton, "Play with bot", font, () -> isBot = !isBot, vg, imageButton);
        menuButtons.add(botButton);

        Button debugButton = new Button( window.getWidthConverted(30), window.getHeight() - heightButton, widthButton/4, heightButton, "Debug Mode", font * 0.7f, enableDebugMode, vg, imageButton);
        menuButtons.add(debugButton);

        float paneWidth = window.getWidthConverted(500);
        float paneHeight = window.getHeightConverted(500);
        float paneX = (window.getWidth() - paneWidth);
        float paneY = window.getHeight() - paneHeight - window.getHeightConverted(10);

        pane = new TextPane(paneX, paneY, paneWidth, paneHeight, "Pane test", font * 0.7f,  vg, imageButton);
    }

    /**
     * Creates the in-game menu.
     */
    private void createInGameMenu() {

        Runnable resume = () -> {
            System.out.println("Resuming game");
            canMove = true;
            isGuiVisible = false;
        };

        Runnable backToMenu = () -> {
            System.out.println("Returning to menu");
            camera.setPosition(new Vector3f(Consts.SIZE_X/4, 50, Consts.SIZE_Z/4));
            camera.setRotation(20, 0, 0);
            canMove = false;
            isGuiVisible = true;
            isOnMenu = true;
            gameStarted = false;
            numberOfShots = 0;
            infoTextPane.setText("Position: (" + (int) golfBall.getPosition().x + ", " + (int) golfBall.getPosition().z + "). Number of shots: " + numberOfShots);
        };

        Runnable sound = () -> {
            System.out.println("Playing sound");
            if (isSoundPlaying) {
                audioManager.stopSound();
            } else {
                audioManager.playSound();
            }
            isSoundPlaying = !isSoundPlaying;
        };

        Runnable quit = () -> {
            System.out.println("Quitting game");
            GLFW.glfwSetWindowShouldClose(Launcher.getWindow().getWindow(), true);
        };

        float heightButton = window.getHeightConverted(300);
        float widthButton = window.getWidthConverted(2000);
        float centerButtonX = (window.getWidth() - widthButton) / 2;
        float centerButtonY = (window.getHeight() - heightButton * 3) / 2;
        float font = window.getHeightConverted(100);

        Button resumeButton = new Button(centerButtonX, centerButtonY, widthButton, heightButton, "Resume", font, resume, vg, "Texture/inGameMenu.png");
        inGameMenuButtons.add(resumeButton);

        Button backToMenuButton = new Button(centerButtonX, centerButtonY + heightButton, widthButton, heightButton, "Back to Menu", font, backToMenu, vg, "Texture/inGameMenu.png");
        inGameMenuButtons.add(backToMenuButton);

        Button soundButton = new Button(centerButtonX, centerButtonY + heightButton * 2, widthButton, heightButton, "Sound", font, sound, vg, "Texture/inGameMenu.png");
        inGameMenuButtons.add(soundButton);

        Button exitButton = new Button(centerButtonX, centerButtonY + heightButton * 3, widthButton, heightButton, "Exit", font, quit, vg, "Texture/inGameMenu.png");
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
     * Switches the terrain of the game.
     *
     * @param blendMapTerrain The new terrain to switch to.
     * @param tree The tree model to add to the new terrain.
     * @param blendMap2 The new blend map to use.
     */
    private void terrainSwitch(BlendMapTerrain blendMapTerrain, List<Model> tree, TerrainTexture blendMap2) {
        scene.getTerrains().remove(terrain);
        scene.getTerrains().remove(ocean);
        terrain = new Terrain(new Vector3f(-Consts.SIZE_X/2 , 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0,0,0,0), 0.1f), blendMapTerrain, blendMap2, false);
        ocean = new Terrain(new Vector3f(-Consts.SIZE_X/2 , 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0,0,0,0), 0.1f), blueTerrain, blendMap2, true);
        scene.addTerrain(terrain);
        scene.addTerrain(ocean);
        scene.getEntities().removeIf(entity -> entity.getModels().equals(tree));
        try {
            if (!debugMode) {
                createTrees(tree);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        renderer.processTerrain(terrain);
    }
}
