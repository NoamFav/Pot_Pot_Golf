package com.pot_pot_golf.Game.GameUtils.GameLogic;

import com.pot_pot_golf.Core.Entity.*;
import com.pot_pot_golf.Core.Entity.Terrain.BlendMapTerrain;
import com.pot_pot_golf.Core.Entity.Terrain.HeightMap;
import com.pot_pot_golf.Core.Entity.Terrain.Terrain;
import com.pot_pot_golf.Core.Entity.Terrain.TerrainTexture;
import com.pot_pot_golf.Core.ObjectLoader;
import com.pot_pot_golf.Core.Utils.StartEndPoint;
import com.pot_pot_golf.Core.Utils.TerrainSwitch;
import com.pot_pot_golf.Game.GameUtils.Consts;
import com.pot_pot_golf.Game.GameUtils.FieldManager.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * The init manager class.
 * This class is responsible for initializing the game.
 * Stores the initialization of the game.
 */
public class InitManager {

    // Records for storing the models and terrains
    private record ModelLoader(List<Model> skyBox, List<Model> ball, List<Model> arrow, List<Model> flag) {}

    private record Terrains(TerrainTexture blendMap, List<TerrainTexture> textures, List<TerrainTexture> waterTextures) {}

    private final SceneManager scene;
    private final EntitiesManager entitiesManager;
    private final ModelManager modelManager;
    private final StartEndPoint startEndPoint;
    private final PathManager pathManager;
    private final GameStateManager gameStateManager;
    private final GameVarManager gameVarManager;
    private final TerrainManager terrainManager;
    private final TerrainSwitch terrainSwitch;
    private final HeightMap heightMap;
    private final ObjectLoader loader;

    /**
     * The constructor of the init manager.
     * It initializes the init manager.
     * Gets the main field manager context for the main classes and managers.
     *
     * @param context The main field manager context.
     */
    public InitManager(@NotNull MainFieldManager context) {
        this.scene = context.getScene();
        this.entitiesManager = context.getEntitiesManager();
        this.modelManager = context.getModelManager();
        this.startEndPoint = context.getStartEndPoint();
        this.pathManager = context.getPathManager();
        this.gameStateManager = context.getGameStateManager();
        this.gameVarManager = context.getGameVarManager();
        this.terrainManager = context.getTerrainManager();
        this.terrainSwitch = context.getTerrainSwitch();
        this.heightMap = context.getHeightMap();
        this.loader = context.getLoader();
    }

    /**
     * Creates the models and entities for the game.
     * Uses a record to store the models.
     * @throws Exception If the models cannot be loaded.
     */
    public void modelAndEntityCreation() throws Exception {

        ModelLoader models = getModels();

        Entity skybox = new Entity(models.skyBox(), new Vector3f(0, -10, 0), new Vector3f(90, 0, 0), Consts.SIZE_X / 2);
        scene.addEntity(skybox);

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

        Entity golfBall = new Entity(models.ball(), new Vector3f(startPoint), new Vector3f(0, 0, 0), 5);
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
     * @throws Exception If the terrains cannot be loaded.
     */
    public void terrainCreation() throws Exception {
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
     * Gets the model for the game.
     * Uses a record to store the models.
     *
     * @return The models for the game.
     */
    private @NotNull ModelLoader getModels() throws Exception {
        List<Model> tree = loader.loadAssimpModel(Consts.OBJ.MAIN_TREE); modelManager.setTree(tree);
        List<Model> skyBox = loader.loadAssimpModel(Consts.OBJ.SKYBOX);
        List<Model> ball = loader.loadAssimpModel(Consts.OBJ.BALL);
        List<Model> ball2 = loader.loadAssimpModel(Consts.OBJ.BALL); modelManager.setBall2(ball2);
        List<Model> botBallModel = loader.loadAssimpModel(Consts.OBJ.BALL); modelManager.setBotBallModel(botBallModel);
        List<Model> aiBotBallModel = loader.loadAssimpModel(Consts.OBJ.BALL); modelManager.setAiBotBallModel(aiBotBallModel);
        List<Model> arrow = loader.loadAssimpModel(Consts.OBJ.ARROW);
        List<Model> flag = loader.loadAssimpModel(Consts.OBJ.FLAG);

        ball.get(0).setTexture(new Texture(loader.loadTexture(Consts.BallTexture.BALL1)));
        ball2.get(0).setTexture(new Texture(loader.loadTexture(Consts.BallTexture.BALL2)));
        botBallModel.get(0).setTexture(new Texture(loader.loadTexture(Consts.BallTexture.BALL_BOT)));
        aiBotBallModel.get(0).setTexture(new Texture(loader.loadTexture(Consts.BallTexture.BALL_AI_BOT)));

        for (Model model : tree) model.getMaterial().setDisableCulling(true);
        for (Model model : skyBox) model.getMaterial().setDisableCulling(true);
        for (Model model : arrow) model.getMaterial().setDisableCulling(true);
        for (Model model : flag) model.getMaterial().setDisableCulling(true);
        for (Model model : ball) model.getMaterial().setDisableCulling(true);
        for (Model model : ball2) model.getMaterial().setDisableCulling(true);
        for (Model model : botBallModel) model.getMaterial().setDisableCulling(true);
        for (Model model : aiBotBallModel) model.getMaterial().setDisableCulling(true);

        return new ModelLoader(skyBox, ball, arrow, flag);
    }

    /**
     * Gets the terrains for the game.
     * Uses a record to store the terrains.
     *
     * @return The terrains for the game.
     */
    private @NotNull Terrains getTerrains() throws Exception {
        TerrainTexture sand = new TerrainTexture(loader.loadTexture(Consts.TerrainTexture.SAND));
        TerrainTexture grass = new TerrainTexture(loader.loadTexture(Consts.TerrainTexture.GRASS));
        TerrainTexture fairway = new TerrainTexture(loader.loadTexture(Consts.TerrainTexture.FAIRWAY));
        TerrainTexture water = new TerrainTexture(loader.loadTexture(Consts.TerrainTexture.WATER));

        //Not used for now (doesn't look good) (don't delete)
        TerrainTexture rock = new TerrainTexture(loader.loadTexture(Consts.DEFAULT_TEXTURE));
        TerrainTexture dryGrass = new TerrainTexture(loader.loadTexture(Consts.DEFAULT_TEXTURE));
        TerrainTexture snow = new TerrainTexture(loader.loadTexture(Consts.DEFAULT_TEXTURE));
        TerrainTexture mold = new TerrainTexture(loader.loadTexture(Consts.DEFAULT_TEXTURE));

        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(Consts.HEIGHTMAP));

        List<TerrainTexture> textures = new ArrayList<>(List.of(sand, grass, fairway, dryGrass, mold, rock, snow));
        List<TerrainTexture> waterTextures = new ArrayList<>();
        while (waterTextures.size() < textures.size()) waterTextures.add(water);
        return new Terrains(blendMap, textures, waterTextures);
    }
}
