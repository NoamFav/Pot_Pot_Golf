package com.um_project_golf.Game.FieldManager;

import com.um_project_golf.Core.*;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.Entity.Terrain.HeightMapPathfinder;
import com.um_project_golf.Core.Rendering.RenderManager;
import com.um_project_golf.Core.Utils.CollisionsDetector;
import com.um_project_golf.Core.Utils.StartEndPoint;
import com.um_project_golf.Core.Utils.TerrainSwitch;
import com.um_project_golf.Game.GameLogic.InitManager;
import com.um_project_golf.Game.GameLogic.InputManager;
import com.um_project_golf.Game.GameLogic.UpdateManager;
import com.um_project_golf.Game.Launcher;
import org.joml.Vector3f;

@SuppressWarnings("unused")
public class MainFieldManager {
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

    // Managers for the game logic
    private final InitManager initManager;
    private final InputManager inputManager;
    private final UpdateManager updateManager;

    // Camera (Player)
    private final Camera camera;
    private final Vector3f cameraInc;

    public MainFieldManager() {
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
        updateManager = new UpdateManager();
    }

    public RenderManager getRenderer() {return renderer;}
    public ObjectLoader getLoader() {return loader;}
    public WindowManager getWindow() {return window;}
    public SceneManager getScene() {return scene;}
    public MouseInput getMouseInputs() {return mouseInputs;}
    public void setMouseInputs(MouseInput mouseInputs) {this.mouseInputs = mouseInputs;}
    public AudioManager getAudioManager() {return audioManager;}
    public void setAudioManager(AudioManager audioManager) {this.audioManager = audioManager;}
    public HeightMap getHeightMap() {return heightMap;}
    public HeightMapPathfinder getPathfinder() {return pathfinder;}
    public CollisionsDetector getCollisionsDetector() {return collisionsDetector;}
    public TerrainSwitch getTerrainSwitch() {return terrainSwitch;}
    public StartEndPoint getStartEndPoint() {return startEndPoint;}
    public GameStateManager getGameStateManager() {return gameStateManager;}
    public GuiElementManager getGuiElementManager() {return guiElementManager;}
    public EntitiesManager getEntitiesManager() {return entitiesManager;}
    public ModelManager getModelManager() {return modelManager;}
    public TerrainManager getTerrainManager() {return terrainManager;}
    public GameVarManager getGameVarManager() {return gameVarManager;}
    public PathManager getPathManager() {return pathManager;}
    public InitManager getInitManager() {return initManager;}
    public InputManager getInputManager() {return inputManager;}
    public UpdateManager getUpdateManager() {return updateManager;}
    public Camera getCamera() {return camera;}
    public Vector3f getCameraInc() {return cameraInc;}
}
