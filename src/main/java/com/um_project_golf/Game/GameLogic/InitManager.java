package com.um_project_golf.Game.GameLogic;

import com.um_project_golf.Core.Entity.*;
import com.um_project_golf.Core.Entity.Terrain.BlendMapTerrain;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.Entity.Terrain.Terrain;
import com.um_project_golf.Core.Entity.Terrain.TerrainTexture;
import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
import com.um_project_golf.Core.ObjectLoader;
import com.um_project_golf.Core.Utils.Consts;
import com.um_project_golf.Core.Utils.StartEndPoint;
import com.um_project_golf.Core.Utils.TerrainSwitch;
import com.um_project_golf.Game.FieldManager.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class InitManager {

    // Records for storing the models and terrains
    private record ModelLoader(List<Model> skyBox, List<Model> ball, List<Model> arrow, List<Model> flag, List<Model> mill) {}

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
     */
    public void modelAndEntityCreation() throws Exception {

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
     * Set up the light for the game.
     * Not used for now.
     */
    @SuppressWarnings("unused")
    public void setUpLight() {
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