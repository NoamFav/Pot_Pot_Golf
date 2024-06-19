package com.um_project_golf.Game;

import com.um_project_golf.Core.*;
import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.Terrain.Terrain;
import com.um_project_golf.Core.Entity.Texture;
import com.um_project_golf.Game.GameUtils.Consts;
import com.um_project_golf.Game.GameUtils.FieldManager.GameStateManager;
import com.um_project_golf.Game.GameUtils.FieldManager.MainFieldManager;
import com.um_project_golf.Game.GameUtils.GUIs.DefaultGUI;
import com.um_project_golf.Game.GameUtils.GUIs.InGameGUI;
import com.um_project_golf.Game.GameUtils.GUIs.MenuGUI;
import com.um_project_golf.Game.GameUtils.GUIs.RecreateGUIs;
import com.um_project_golf.Game.GameUtils.GameLogic.InitManager;
import com.um_project_golf.Game.GameUtils.GameLogic.InputManager;
import com.um_project_golf.Game.GameUtils.GameLogic.UpdateManager;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.nanovg.NanoVGGL3.*;

/**
 * The main game logic class.
 * This class is responsible for initializing the game, handling input, updating the game state and rendering the game.
 */
public class GolfGame implements ILogic {

    // Debug mode for Examination purposes
    public static boolean debugMode = false; //Do not change this value to true,
    // it will break the game (you can change it to true in-game only)

    private long vg; // NanoVG context

    MainFieldManager context; // The main controller of the game

    /**
     * The constructor of the game.
     * It initializes the renderer, window, loader and camera.
     */
    public GolfGame() {
        context = new MainFieldManager();
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

        context.setMouseInputs(mouseInput);

        // Managers for the game logic
        InitManager initManager = new InitManager(context);

        context.getScene().setDefaultTexture(new Texture(context.getLoader().loadTexture(Consts.DEFAULT_TEXTURE)));
        context.getWindow().setAntiAliasing(true);
        context.getWindow().setResized(false);

        context.getRenderer().init();
        context.getWindow().setClearColor(0, 0, 0, 0.0f);

        context.getHeightMap().createHeightMap();
        context.getPathManager().setPath(context.getPathfinder().getPath(Consts.RADIUS_DOWN, Consts.RADIUS_UP, Consts.SIZE_GREEN));

        initManager.modelAndEntityCreation();
        initManager.terrainCreation();
        initManager.setUpLight();

        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);

        GameStateManager gameStateManager = context.getGameStateManager();
        gameStateManager.setGuiVisible(true);
        gameStateManager.setCanMove(false);
        gameStateManager.setOnMenu(true);

        AudioManager audioManager = new AudioManager(Consts.BACKGROUND_MUSIC);
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

        InputManager inputManager = new InputManager(context);
        //float lightPos = scene.getSpotLights()[0].getPointLight().getPosition().z;
        //float lightPos2 = scene.getSpotLights()[1].getPointLight().getPosition().z;

        if (context.getWindow().is_keyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            GameStateManager gameStateManager = context.getGameStateManager();
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

        UpdateManager updateManager = new UpdateManager(context);

        context.getGuiElementManager().update(context.getGameStateManager());

        if (context.getWindow().isResized()) {
            Vector3f oldPosition = new Vector3f(context.getCamera().getPosition());
            context.getWindow().setResized(false);
            new RecreateGUIs(vg, context);
            context.getMouseInputs().init();
            context.getCamera().setPosition(oldPosition);
        }

        Vector3f cameraInc = new Vector3f(context.getCameraInc());
        context.getCamera().movePosition(cameraInc.x * Consts.CAMERA_MOVEMENT_SPEED, (cameraInc.y * Consts.CAMERA_MOVEMENT_SPEED), cameraInc.z * Consts.CAMERA_MOVEMENT_SPEED);

        context.getCollisionsDetector().checkCollision(context.getCamera(), cameraInc, context.getHeightMap(), context.getScene());

        updateManager.daytimeCycle();
        updateManager.updateTreeAnimations();
        updateManager.animateBall();
        if (context.getGameStateManager().isAiBotAnimating()) {updateManager.animateAIBall();}
        if (context.getGameStateManager().isBotAnimating()) {updateManager.animateBotBall();}

        context.getGuiElementManager().updateTextFields(context.getGameStateManager());


        for (Entity entity : context.getScene().getEntities()) {context.getRenderer().processEntity(entity);}

        for (Terrain terrain : context.getScene().getTerrains()) {context.getRenderer().processTerrain(terrain);}
    }

    /**
     * Renders the game.
     * It renders the entity and the camera.
     */
    @Override
    public void render() {
        context.getRenderer().clear();
        context.getRenderer().render(context.getCamera(), context.getScene());

        context.getGuiElementManager().render(context.getGameStateManager());

        Entity arrowEntity = context.getEntitiesManager().getArrowEntity();
        if (context.getGameStateManager().isAnimating()) {
            context.getScene().getEntities().remove(arrowEntity);
        } else {
            context.getScene().addEntity(arrowEntity);
        }
    }

    /**
     * Cleans up the game.
     * It cleans up the renderer and loader.
     */
    @Override
    public void cleanUp() {
        context.getRenderer().cleanup();
        context.getLoader().cleanUp();
        context.getAudioManager().cleanup();
        context.getGuiElementManager().cleanup();

        nvgDelete(vg);
    }
}
