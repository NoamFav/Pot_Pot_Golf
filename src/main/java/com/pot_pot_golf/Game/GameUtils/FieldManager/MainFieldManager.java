package com.pot_pot_golf.Game.GameUtils.FieldManager;

import com.pot_pot_golf.Core.*;
import com.pot_pot_golf.Core.Entity.SceneManager;
import com.pot_pot_golf.Core.Entity.Terrain.HeightMap;
import com.pot_pot_golf.Core.Entity.Terrain.HeightMapPathfinder;
import com.pot_pot_golf.Core.Rendering.RenderManager;
import com.pot_pot_golf.Core.Utils.CollisionsDetector;
import com.pot_pot_golf.Core.Utils.StartEndPoint;
import com.pot_pot_golf.Core.Utils.TerrainSwitch;
import com.pot_pot_golf.Game.Launcher;
import org.joml.Vector3f;

/**
 * The main field manager class.
 * This class is responsible for managing the main field of the game.
 * Stores the main field of the game.
 */
public class MainFieldManager {
    // Main game Classes and Managers
    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;
    private final SceneManager scene;
    private MouseInput mouseInputs;
    private AudioManager audioManager;
    private HeightMap heightMap;
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

    // Camera (Player)
    private final Camera camera;
    private Vector3f cameraInc;

    /**
     * The constructor of the main field manager.
     * It initializes the main field manager.
     * Creates the instances of every class and manager needed for the game.
     */
    public MainFieldManager() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        scene = new SceneManager();
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
    public Camera getCamera() {return camera;}
    public Vector3f getCameraInc() {return cameraInc;}
    public void setCameraInc(Vector3f cameraInc) {this.cameraInc = cameraInc;}
    public void setHeightMap(HeightMap heightMap) {this.heightMap = heightMap;}
}
