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
import com.um_project_golf.Game.FieldManager.*;
import com.um_project_golf.Game.GUIs.DefaultGUI;
import com.um_project_golf.Game.GUIs.InGameGUI;
import com.um_project_golf.Game.GUIs.MenuGUI;
import com.um_project_golf.Game.GUIs.RecreateGUIs;
import com.um_project_golf.Game.GameLogic.InitManager;
import com.um_project_golf.Game.GameLogic.InputManager;
import com.um_project_golf.Game.GameLogic.UpdateManager;
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

    // Field Managers for the game
    private final GameStateManager gameStateManager;
    private final GuiElementManager guiElementManager;
    private final EntitiesManager entitiesManager;
    private final PathManager pathManager;

    // Managers for the game logic
    private final InitManager initManager;
    private final InputManager inputManager;
    private final UpdateManager updateManager;

    // Camera (Player)
    private final Camera camera;
    private final Vector3f cameraInc;

    MainFieldManager context;

    /**
     * The constructor of the game.
     * It initializes the renderer, window, loader and camera.
     */
    public GolfGame() {
        context = new MainFieldManager();
        renderer = context.getRenderer();
        window = Launcher.getWindow();
        loader = context.getLoader();
        scene = context.getScene();
        camera = context.getCamera();
        pathfinder = context.getPathfinder();
        collisionsDetector = context.getCollisionsDetector();
        cameraInc = context.getCameraInc();
        heightMap = context.getHeightMap();
        gameStateManager = context.getGameStateManager();
        guiElementManager = context.getGuiElementManager();
        entitiesManager = context.getEntitiesManager();
        pathManager = context.getPathManager();

        initManager = new InitManager(context);
        inputManager = new InputManager(context);
        updateManager = new UpdateManager(context);
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
        context.setMouseInputs(mouseInputs);

        scene.setDefaultTexture(new Texture(loader.loadTexture("src/main/resources/Texture/Default.png")));
        window.setAntiAliasing(true);
        window.setResized(false);

        renderer.init();
        window.setClearColor(0.529f, 0.808f, 0.922f, 0.0f);

        heightMap.createHeightMap();
        List<Vector2i> path = pathfinder.getPath(Consts.RADIUS_DOWN, Consts.RADIUS_UP, Consts.SIZE_GREEN); // upper and lower bounds for the radius of the path
        pathManager.setPath(path);

        initManager.modelAndEntityCreation();
        initManager.terrainCreation();
        initManager.setUpLight();

        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);

        gameStateManager.setGuiVisible(true);
        gameStateManager.setCanMove(false);
        gameStateManager.setOnMenu(true);

        audioManager = new AudioManager("src/main/resources/SoundTrack/skippy-mr-sunshine-fernweh-goldfish-main-version-02-32-7172.wav");
        audioManager.playSound();
        gameStateManager.setSoundPlaying(true);
        context.setAudioManager(audioManager);

        new MenuGUI(vg, context);

        new InGameGUI(vg, context);

        new DefaultGUI(vg, context);
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

        inputManager.movementControl();
        inputManager.cameraMovement();
        inputManager.startEndPointDebugMode();
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

            new RecreateGUIs(vg,
                    context
            );

            mouseInputs.init();
            camera.setPosition(oldPosition);
        }

        camera.movePosition(cameraInc.x * Consts.CAMERA_MOVEMENT_SPEED, (cameraInc.y * Consts.CAMERA_MOVEMENT_SPEED), cameraInc.z * Consts.CAMERA_MOVEMENT_SPEED);

        collisionsDetector.checkCollision(camera, cameraInc, heightMap, scene);

        updateManager.daytimeCycle();
        updateManager.updateTreeAnimations();
        updateManager.animateBall();

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
}
