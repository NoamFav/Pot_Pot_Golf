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
import com.um_project_golf.Core.Utils.ButtonTimer;
import com.um_project_golf.Core.Utils.CollisionsDetector;
import com.um_project_golf.Core.Utils.Consts;
import com.um_project_golf.Core.Entity.Terrain.HeightMapPathfinder;
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
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

import static org.lwjgl.nanovg.NanoVGGL3.*;

/**
 * The main game logic class.
 * This class is responsible for initializing the game, handling input, updating the game state and rendering the game.
 */
public class GolfGame implements ILogic {

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;
    private final SceneManager scene;
    private final ButtonTimer timer;
    private MouseInput mouseInputs;
    private long vg;

    private final String imageButton = "Texture/buttons.png";

    private final List<Button> menuButtons;
    private final List<Button> inGameMenuButtons;
    //private Button infoButton;
    private TextField textField;
    private Title title;
    private TextPane pane;

    private Entity arrowEntity;

    private Vector2i startPoint;
    private Vector2i endPoint;

    private final Camera camera;
    private Terrain terrain;
    private Terrain ocean;
    private final HeightMap heightMap;
    private BlendMapTerrain blendMapTerrain;
    private BlendMapTerrain blueTerrain;
    private AudioManager audioManager;
    private final HeightMapPathfinder pathfinder;
    private final CollisionsDetector collisionsDetector;
    Vector3f cameraInc;

    private boolean canMove = false;
    private boolean isJumping = false;
    private boolean isGuiVisible = true;
    private boolean isOnMenu = true;
    private boolean isSoundPlaying = false;
    private boolean wasPressed = false;
    public static boolean debugMode = false;

    private float lastY;

    private Entity golfBall;
    private static float xStartPosition = 0f;
    private static float yStartPosition; // Make sure to change y to the correct value when initializing
    private static float zStartPosition = 0f;
    private BallCollisionDetector ballCollisionDetector;

    private TextField vxTextField;
    private TextField vzTextField;
    private Button applyButton;
    private TextPane vxTextPane;
    private TextPane vzTextPane;
    private TextPane infoTextPane;
    private TextPane warningTextPane;
    private int numberOfShots;

    private boolean gameStarted = false;
    /**
     * The constructor of the game.
     * It initializes the renderer, window, loader and camera.
     */
    public GolfGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        scene = new SceneManager(-90);
        timer = new ButtonTimer();
        camera = new Camera();
        pathfinder = new HeightMapPathfinder();
        collisionsDetector = new CollisionsDetector();
        cameraInc = new Vector3f(0, 0, 0);
        heightMap = new HeightMap();
        menuButtons = new ArrayList<>();
        inGameMenuButtons = new ArrayList<>();
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

        heightMap.createHeightMap();
        List<Vector2i> path = pathfinder.getPath(410, 550); // upper and lower bounds for the radius of the path

        scene.setDefaultTexture(new Texture(loader.loadTexture("Texture/Default.png")));
        window.setAntiAliasing(true);
        window.setResized(false);

        renderer.init();
        window.setClearColor(0.529f, 0.808f, 0.922f, 0.0f);

        //Model cube = loader.loadAssimpModel("src/main/resources/Models/Minecraft_Grass_Block_OBJ/SkyBox.obj");
        List<Model> tree = loader.loadAssimpModel("src/main/resources/Models/tree/Tree.obj");
        List<Model> wolf = loader.loadAssimpModel("src/main/resources/Models/Wolf_dae/wolf.dae");
        List<Model> skyBox = loader.loadAssimpModel("src/main/resources/Models/Skybox/SkyBox.obj");
        List<Model> ball = loader.loadAssimpModel("src/main/resources/Models/Ball/ImageToStl.com_ball.obj");
        List<Model> arrow = loader.loadAssimpModel("src/main/resources/Models/Arrow/Arrow5.obj");
        List<Model> flag = loader.loadAssimpModel("src/main/resources/Models/flag/flag.obj");

        for (Model model : tree) {
            model.getMaterial().setDisableCulling(true);
        }
        for (Model model : wolf) {
            model.getMaterial().setDisableCulling(true);
        }
        for (Model model : skyBox) {
            model.getMaterial().setDisableCulling(true);
        }
        for (Model model : arrow) {
            model.getMaterial().setDisableCulling(true);
        }
        for (Model model : flag) {
            model.getMaterial().setDisableCulling(true);
        }
        for (Model model : ball) {
            model.getMaterial().setDisableCulling(true);
        }

        TerrainTexture rock = new TerrainTexture(loader.loadTexture("Texture/rock.png"));
        TerrainTexture sand = new TerrainTexture(loader.loadTexture("Texture/sand.png"));
        TerrainTexture grass = new TerrainTexture(loader.loadTexture("Texture/grass.png"));
        TerrainTexture dryGrass = new TerrainTexture(loader.loadTexture("Texture/dryGrass.png"));
        TerrainTexture fairway = new TerrainTexture(loader.loadTexture("Texture/fairway.png"));
        TerrainTexture snow = new TerrainTexture(loader.loadTexture("Texture/snow.png"));
        TerrainTexture mold = new TerrainTexture(loader.loadTexture("Texture/mold.png"));
        TerrainTexture water = new TerrainTexture(loader.loadTexture("Texture/water.png"));

        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("Texture/heightmap.png"));

        List<TerrainTexture> textures = new ArrayList<>(List.of(sand, grass, fairway, dryGrass, mold, rock, snow));
        List<TerrainTexture> waterTextures = new ArrayList<>();
        for (TerrainTexture texture : textures) {
            waterTextures.add(water);
        }

        blendMapTerrain = new BlendMapTerrain(textures);
        blueTerrain = new BlendMapTerrain(waterTextures);

        terrain = new Terrain(new Vector3f(-Consts.SIZE_X/2 , 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0,0,0,0), 0.1f), blendMapTerrain, blendMap, false);
        ocean = new Terrain(new Vector3f(-Consts.SIZE_X / 2, -1, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0, 0, 0, 0), 0.1f), blueTerrain, blendMap, true);
        scene.addTerrain(terrain);
        scene.addTerrain(ocean);
        ocean.getModel().getMaterial().setDisableCulling(true);

        createTrees(tree);

        scene.addEntity(new Entity(skyBox, new Vector3f(0, -10, 0), new Vector3f(90, 0, 0), Consts.SIZE_X / 2));
        scene.addEntity(new Entity(wolf, new Vector3f(0, heightMap.getHeight(new Vector3f()), 0), new Vector3f(45, 0 , 0), 10 ));
        //scene.addEntity(new Entity(ball, new Vector3f(0, Terrain.getHeight(0, 0), 0), new Vector3f(50, 0, 0), 10));

        arrowEntity = new Entity(arrow, new Vector3f(0, 50, 0), new Vector3f(-90,0 ,90), 1);
        scene.addEntity(arrowEntity);

        startPoint = path.get(0);
        startPoint.x = (int) (startPoint.x / 4 - Consts.SIZE_X / 2);
        startPoint.y = (int) (startPoint.y / 4 - Consts.SIZE_Z / 2);

        endPoint = path.get(path.size() - 1);
        endPoint.x = (int) (endPoint.x / 4 - Consts.SIZE_X / 2);
        endPoint.y = (int) (endPoint.y / 4 - Consts.SIZE_Z / 2);

        //scene.addEntity(new Entity(flag, new Vector3f(startPoint.x, heightMap.getHeight(new Vector3f(startPoint.x, 0 , startPoint.y)), startPoint.y), new Vector3f(0, 0, 0), 3));
        scene.addEntity(new Entity(flag, new Vector3f(endPoint.x, heightMap.getHeight(new Vector3f(startPoint.x, 0 , startPoint.y)), endPoint.y), new Vector3f(0, 0, 0), 5));

        //scene.addEntity(new Entity(wolf, new Vector3f(0, Terrain.getHeight(0,0), 0), new Vector3f(45, 0 , 0), 10 ));

        yStartPosition = heightMap.getHeight(new Vector3f(xStartPosition, 0, zStartPosition));
        golfBall = new Entity(ball, new Vector3f(xStartPosition, yStartPosition, zStartPosition), new Vector3f(50, 0, 0), 5);
        System.out.println("Start position of golfball: " + golfBall.getPosition().x + "," + golfBall.getPosition().y + "," + golfBall.getPosition().z);
        scene.addEntity(golfBall);

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

        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        createMenu(blendMapTerrain, tree);
        createInGameMenu();
        isGuiVisible = true;
        canMove = false;
        isOnMenu = true;

        float width = window.getWidthConverted(1000);
        float height = window.getHeightConverted(300);
        float x = window.getWidthConverted(10);
        float y = window.getHeightConverted(10);
        float font = window.getHeightConverted(70);

        //infoButton = new Button(x, y, width, height, "Info", 70, () -> {}, vg, imageButton);
        numberOfShots = 0;
        infoTextPane = new TextPane(x, y, width, height / 2, "Position: (" + (int) golfBall.getPosition().x + ", " + (int) golfBall.getPosition().z + "). Number of shots: " + numberOfShots, 40, vg, imageButton);
        warningTextPane = new TextPane(x, y + height / 2, width, height / 2, "", 40, vg, imageButton);
        ballCollisionDetector = new BallCollisionDetector(heightMap, scene, warningTextPane);
        //infoButton = new Button(x, y, width, height, "Info", font, () -> {}, vg, imageButton);

        //textField = new TextField(x, y * 30, width, height, "Enter text here", 70, vg, imageButton);
        textField = new TextField(100, 600, 500, 250, "Enter text here", 70, vg, imageButton);

        GLFW.glfwSetKeyCallback(window.getWindow(), (window, key, scancode, action, mods) -> textField.handleKeyInput(key, action, mods));

        GLFW.glfwSetMouseButtonCallback(window.getWindow(), (window, button, action, mods) -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                double[] xPos = new double[1];
                double[] yPos = new double[1];
                GLFW.glfwGetCursorPos(window, xPos, yPos);
                textField.handleMouseClick((float) xPos[0], (float) yPos[0]);
            }
        });

        // Creating textfields and textpanes for entering the velocities
        vxTextPane = new TextPane(x * 4, y * 30, width / 5, height / 2, "vx: ", 70, vg, imageButton);
        vxTextField = new TextField(x * 25, y * 30, width / 3, height / 2, "Enter vx", 50, vg, imageButton);

        vzTextPane = new TextPane(x * 4, y * 30 + height / 2, width / 5, height / 2, "vz: ", 70, vg, imageButton);
        vzTextField = new TextField(x * 25, y * 30 + height / 2, width / 3, height / 2, "Enter vz", 50, vg, imageButton);

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

        PhysicsEngine engine = new PhysicsEngine(heightMap, Consts.KINETIC_FRICTION_GRASS, Consts.STATIC_FRICTION_GRASS, Consts.KINETIC_FRICTION_SAND, Consts.STATIC_FRICTION_SAND);

        Runnable applyVelocity = () -> {
            try {
                warningTextPane.setText("");
                double vx = Double.parseDouble(vxTextField.getText());
                double vz = Double.parseDouble(vzTextField.getText());
                System.out.println("vx: " + vx + ", vz: " + vz);

                if (Math.abs(vx) > Consts.MAX_SPEED || Math.abs(vz) > Consts.MAX_SPEED) {
                    warningTextPane.setText("Speed too high: max " + Consts.MAX_SPEED + " m/s");
                }
                else {
                    double[] initialState = {golfBall.getPosition().x, golfBall.getPosition().z, vx, vz}; // initialState = [x, z, vx, vz]
                    Vector3f initialPosition = new Vector3f(golfBall.getPosition().x, golfBall.getPosition().y, golfBall.getPosition().z);
                    double h = 0.1; // Time step
                    Vector3f finalPosition = engine.runImprovedEuler(initialState, h);
                    ballCollisionDetector.checkCollisionBall(initialPosition, finalPosition);
                    // Update the position of the golf ball
                    golfBall.setPosition(finalPosition.x, finalPosition.y, finalPosition.z);
                    System.out.println("Position:" + finalPosition.x + ", " + finalPosition.y  + ", " + finalPosition.z);
                    numberOfShots ++;
                    infoTextPane.setText("Position: (" + (int) golfBall.getPosition().x + ", " + (int) golfBall.getPosition().z + "). Number of shots: " + numberOfShots);
                }
            } catch (Exception e) {
                System.out.println("Exception when applying velocity");
                throw new RuntimeException(e);
            }
        };

        applyButton = new Button(x, y * 30 + height, 3 * width / 5, 2 * height / 3, "Apply Velocity", 70, applyVelocity, vg, imageButton);

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
        float gravity = -9.81f;

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
                isJumping = true;
            }
            if(window.is_keyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                cameraInc.y = -Consts.JUMP_FORCE / EngineManager.getFps();
            }
        }

        /*
        if (mouseInputs.isLeftButtonPressed()) {
            if (!wasPressed) { // If the button was not previously pressed
                timer.startTimer();
                wasPressed = true; // Update the tracked state
            } else {
                // Continuously update the button text with the current time pressed
                String time = timer.getFormattedTime();
                float velocity = secondToVelocity(timer.getTime());
                infoButton.setText(time + " - velocity: " + velocity + " m/s");
                arrowEntity.setScale(velocity);
            }
        } else {
            if (wasPressed) { // If the button was previously pressed
                wasPressed = false; // Update the tracked state
            }
        } */

        if (canMove) {
            if (mouseInputs.isRightButtonPressed()) {
                Vector2f rotVec = mouseInputs.getDisplayVec();
                camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
                arrowEntity.increaseRotation(0, 0, - rotVec.y * Consts.MOUSE_SENSITIVITY);
            }
        } else if (isOnMenu && isGuiVisible) {
            camera.moveRotation(0, 0.1f, 0);
        }

//
//        if (window.is_keyPressed(GLFW.GLFW_KEY_LEFT)) {
//            scene.getPointLights()[0].getPosition().x += 0.1f;
//        }
//        if (window.is_keyPressed(GLFW.GLFW_KEY_RIGHT)) {
//            scene.getPointLights()[0].getPosition().x -= 0.1f;
       // }

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

        if (debugMode) {
            textField.update();
        } else {
            //infoButton.update();
        }

        if (window.isResized()) {
            window.setResized(false);
            recreateGUIs();
        }

        camera.movePosition(
                cameraInc.x * Consts.CAMERA_MOVEMENT_SPEED,
                (cameraInc.y * Consts.CAMERA_MOVEMENT_SPEED),
                cameraInc.z * Consts.CAMERA_MOVEMENT_SPEED
        );

        if (isJumping && camera.getPosition().y <= lastY) {
            isJumping = false;
        }
        lastY = camera.getPosition().y;

        collisionsDetector.checkCollision(camera, cameraInc, heightMap, scene);

        scene.increaseSpotAngle(0.01f);
        if(scene.getSpotAngle() > 4) {
            scene.setSpotInc(-1);
        } else if(scene.getSpotAngle() < -4) {
            scene.setSpotInc(1);
        }
//        double spotAngleRad = Math.toRadians(scene.getSpotAngle());
//        Vector3f coneDir = scene.getSpotLights()[0].getPointLight().getPosition();
//        coneDir.y = (float) Math.sin(spotAngleRad);

        daytimeCycle();

        for (Entity entity : scene.getEntities()) {
            renderer.processEntity(entity);
            //entity.increaseRotation(1, 1, 1);
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
                //pane.render();
            } else {
                for (Button button : inGameMenuButtons) {
                    button.render();
                }
            }
        }
        if (debugMode){
            textField.render();
        } else {
            //infoButton.render();
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
        textField.cleanup();
        vxTextField.cleanup();
        vzTextField.cleanup();
        vxTextPane.cleanup();
        vzTextPane.cleanup();

        //infoButton.cleanup();
        textField.cleanup();
        pane.cleanup();
        nvgDelete(vg);
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
                scene.addEntity(new Entity(tree, new Vector3f(position.x, position.y, position.z), new Vector3f(-90, 0, 0), 0.03f));
                treePositions[i] = new float[]{position.x, position.y, position.z};
            } else {
                System.out.println("Position is null at index: " + i);
            }
        }
        System.out.println("Tree length: " + treePositions.length);

        scene.setTreePositions(treePositions);
    }

    /**
     * Creates the menu.
     *
     * @param blendMapTerrain The terrain to create the menu for.
     * @param tree The tree model to add to the menu.
     */
    private void createMenu(BlendMapTerrain blendMapTerrain, List<Model> tree) {

        camera.setPosition(new Vector3f(Consts.SIZE_X/4, 50, Consts.SIZE_Z/4));
        camera.setRotation(20, 0, 0);

        float width = window.getWidth();
        float height = window.getHeight();

        Runnable terrainChanger = () -> {
            try {
                System.out.println("Changing terrain");
                heightMap.createHeightMap();
                TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture("Texture/heightmap.png"));
                SimplexNoise.shufflePermutation();
                terrainSwitch(blendMapTerrain, tree, blendMap2);
                yStartPosition = heightMap.getHeight(new Vector3f(xStartPosition, 0, zStartPosition));
                golfBall.setPosition(xStartPosition, yStartPosition, zStartPosition);
                numberOfShots = 0;
                infoTextPane.setText("Position: (" + (int) golfBall.getPosition().x + ", " + (int) golfBall.getPosition().z + "). Number of shots: " + numberOfShots);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        Runnable startGame = () -> {
            System.out.println("Allowing movement");

            camera.setPosition(new Vector3f(startPoint.x, heightMap.getHeight(new Vector3f(startPoint.x, 0, startPoint.y)), startPoint.y));
            camera.setRotation(0, 0, 0);
            canMove = true;
            isGuiVisible = false;
            isOnMenu = false;
            gameStarted = true;
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
            recreateGUIs();

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

        Button debugButton = new Button( window.getWidthConverted(30), window.getHeight() - heightButton, widthButton/4, heightButton, "Debug Mode", font * 0.7f, enableDebugMode, vg, imageButton);
        menuButtons.add(debugButton);

        float paneWidth = window.getWidthConverted(500);
        float paneHeight = window.getHeightConverted(500);
        float paneX = (window.getWidth() - paneWidth);
        float paneY = window.getHeight() - paneHeight - window.getHeightConverted(10);

        pane = new TextPane(paneX, paneY, paneWidth, paneHeight, "Pane test", font * 0.7f,  vg, imageButton);
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
            createTrees(tree);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        renderer.processTerrain(terrain);
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
            yStartPosition = heightMap.getHeight(new Vector3f(xStartPosition, 0, zStartPosition));
            golfBall.setPosition(xStartPosition, yStartPosition, zStartPosition);
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

        createMenu(blendMapTerrain, scene.getEntities().get(2).getModels());
        createInGameMenu();

        float width = window.getWidthConverted(1000);
        float height = window.getHeightConverted(300);
        float x = window.getWidthConverted(10);
        float y = window.getHeightConverted(10);
        float font = window.getHeightConverted(70);

        //infoButton = new Button(x, y, width, height, "Info", font, () -> {}, vg, imageButton);

        textField = new TextField(x, y * 30, width, height, "Enter text here", font, vg, imageButton);
    }

    private float secondToVelocity(long ms) {
        float second = (float) ms / 1000;
        return cappedExponentialFunc(second, 3, 1, 74);
    }

    private float cappedExponentialFunc(float s, float C, float k, float max) {
        return (float) Math.min(C*(Math.exp(k*s)-1), max);
    }

    public boolean canMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }
}
