package com.um_project_golf.Game;

import com.um_project_golf.Core.*;
import com.um_project_golf.Core.AWT.Button;
import com.um_project_golf.Core.Entity.*;
import com.um_project_golf.Core.Entity.Terrain.BlendMapTerrain;
import com.um_project_golf.Core.Entity.Terrain.SimplexNoise;
import com.um_project_golf.Core.Entity.Terrain.TerrainTexture;
import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
import com.um_project_golf.Core.MouseInput;
import com.um_project_golf.Core.Rendering.RenderManager;
import com.um_project_golf.Core.Entity.Terrain.Terrain;
import com.um_project_golf.Core.Utils.Consts;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

/**
 * The main game logic class.
 * This class is responsible for initializing the game, handling input, updating the game state and rendering the game.
 */
public class GolfGame implements ILogic {

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;
    private final SceneManager scene;
    private final Button button;

    private final Camera camera;
    private Terrain terrain;

    Vector3f cameraInc;
    private boolean canMove = true;
    private boolean isJumping = false;
    private float lastY;

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
        cameraInc = new Vector3f(0, 0, 0);
        button = new Button(0, 10, 100, 100, "Button", () -> {
            System.out.println("Button clicked");
        });
    }

    /**
     * Initializes the game.
     * It loads the model and texture of the game.
     *
     * @throws Exception If the game fails to initialize.
     */
    @Override
    public void init() throws Exception {

        scene.setDefaultTexture(new Texture(loader.loadTexture("Texture/Default.png")));

        renderer.init();
        window.setClearColor(0.529f, 0.808f, 0.922f, 0.0f);

        //Model cube = loader.loadAssimpModel("src/main/resources/Models/Minecraft_Grass_Block_OBJ/Grass_Block.obj");
        //Model skull = loader.loadAssimpModel("src/main/resources/Models/Skull/skulls.obj");
        Model tree = loader.loadAssimpModel("src/main/resources/Models/tree/Tree.obj");
        Model wolf = loader.loadAssimpModel("src/main/resources/Models/Wolf_dae/wolf.dae");
        //cube.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Minecraft_Grass_Block_OBJ/Grass_Block_TEX.png")), 1f);
        //skull.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Skull/Skull.jpg")), 1f);
        tree.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/tree/Tree.jpg")), 1f);
        wolf.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Wolf_dae/Material__wolf_col_tga_diffuse.jpeg.001.jpg")), 1f);
        tree.getMaterial().setDisableCulling(true);
        wolf.getMaterial().setDisableCulling(true);

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("Texture/stone.png"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("Texture/sand.png"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("Texture/grass.png"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("Texture/blue.png"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("Texture/heightmap.png"));
        TerrainTexture blue = new TerrainTexture(loader.loadTexture("Texture/blue.png"));

        BlendMapTerrain blendMapTerrain = new BlendMapTerrain(backgroundTexture, rTexture, gTexture, bTexture);
        BlendMapTerrain blueTerrain = new BlendMapTerrain(blue, blue, blue, blue);

        terrain = new Terrain(new Vector3f(-Consts.SIZE_X/2 , -1, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0,0,0,0), 0.1f), blendMapTerrain, blendMap, false);
        //Terrain water = new Terrain(new Vector3f(-Consts.SIZE_X/2 , -1, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0,0,0,0), 0.1f), blueTerrain, blendMap, true);
        scene.addTerrain(terrain);
        //scene.addTerrain(water);

        Random rnd = new Random();
        for (int i = 0; i < 500 ; i++) {
            float x = rnd.nextFloat() * Consts.SIZE_X - Consts.SIZE_X / 2;
            float z = rnd.nextFloat() * Consts.SIZE_Z - Consts.SIZE_Z / 2;
            float y = (float) (SimplexNoise.octaveSimplexNoise(x * Consts.scales, z * Consts.scales, 0, Consts.octaves, Consts.persistence) * (Consts.MAX_HEIGHT/2));
            float scale = rnd.nextFloat() * 0.1f + 0.1f;
            //entities.add(new Entity(skull, new Vector3f(x * 4, y * 4, z), new Vector3f(rnd.nextFloat() * 180, rnd.nextFloat() * 180, 0), 1));
            //scene.addEntity(new Entity(cube, new Vector3f(x, y, z), new Vector3f(rnd.nextFloat() * 180, rnd.nextFloat() * 180, 0), 1.5f));
            scene.addEntity(new Entity(tree, new Vector3f(x, y, z), new Vector3f(-90, 0, 0), 0.03f));
        }



        scene.addEntity(new Entity(wolf, new Vector3f(0, terrain.getHeight(0,0), 0), new Vector3f(0, 0, 0), 10 ));
        //scene.addEntity(new Entity(cube, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1 ));


        //TODO: Allow multiple textures for the same model

        float lightIntensity =10f;

        //point light
        Vector3f lightPosition = new Vector3f(Consts.SIZE_X / 2, 10, 0);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        PointLight pointLight = new PointLight(lightColor, lightPosition, lightIntensity, 0,0,1);

        //spot light 1
        Vector3f coneDir = new Vector3f(0, -50, 0);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        lightIntensity = 2;
        SpotLight spotLight = new SpotLight(new PointLight(new Vector3f(0,0.25f,0), new Vector3f(0,0,0), lightIntensity), coneDir, cutoff);
        SpotLight spotLight2 = new SpotLight(new PointLight(new Vector3f(0.25f,0,0), new Vector3f(0,0,0), lightIntensity), coneDir, cutoff);

        //directional light
        lightPosition = new Vector3f(-1, 10, 0);
        lightColor = new Vector3f(1, 1, 1);
        scene.setDirectionalLight(new DirectionalLight(lightColor, lightPosition, lightIntensity));

        scene.setPointLights(new PointLight[]{pointLight});
        scene.setSpotLights(new SpotLight[]{spotLight, spotLight2});
    }

    /**
     * Handles the input of the game.
     * It sets the cameraInc vector based on the input of the user.
     */
    @Override
    public void input() {
        cameraInc.set(0, 0, 0);
        float lightPos = scene.getSpotLights()[0].getPointLight().getPosition().z;
        float lightPos2 = scene.getSpotLights()[1].getPointLight().getPosition().z;

        float moveSpeed = Consts.CAMERA_MOVEMENT_SPEED  / EngineManager.getFps();
        float gravity = -9.81f;


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

        if (window.is_keyPressed(GLFW.GLFW_KEY_LEFT)) {
            scene.getPointLights()[0].getPosition().x += 0.1f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_RIGHT)) {
            scene.getPointLights()[0].getPosition().x -= 0.1f;
        }

        if (window.is_keyPressed(GLFW.GLFW_KEY_I)) {
            scene.getSpotLights()[0].getPointLight().getPosition().z = lightPos + 0.1f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_K)) {
            scene.getSpotLights()[0].getPointLight().getPosition().z = lightPos - 0.1f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_L)) {
            scene.getSpotLights()[0].getPointLight().getPosition().x += 0.1f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_J)) {
            scene.getSpotLights()[0].getPointLight().getPosition().x -= 0.1f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_O)) {
            scene.getSpotLights()[0].getPointLight().getPosition().y += 0.1f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_U)) {
            scene.getSpotLights()[0].getPointLight().getPosition().y -= 0.1f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_0)) {
            scene.getSpotLights()[1].getPointLight().getPosition().z = lightPos2 + 0.1f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_P)) {
            scene.getSpotLights()[1].getPointLight().getPosition().z = lightPos2 - 0.1f;
        }
    }

    /**
     * Updates the game state.
     * It moves the camera and the entity based on the input of the user.
     *
     * @param mouseInput The mouse input of the user.
     */
    @Override
    public void update(MouseInput mouseInput) {

        camera.movePosition(
                cameraInc.x * Consts.CAMERA_MOVEMENT_SPEED,
                (cameraInc.y * Consts.CAMERA_MOVEMENT_SPEED),
                cameraInc.z * Consts.CAMERA_MOVEMENT_SPEED
        );

        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
        }

        if (isJumping && camera.getPosition().y <= lastY) {
            isJumping = false;
        }
        lastY = camera.getPosition().y;

        checkCollision();

        scene.increaseSpotAngle(0.01f);
        if(scene.getSpotAngle() > 4) {
            scene.setSpotInc(-1);
        } else if(scene.getSpotAngle() < -4) {
            scene.setSpotInc(1);
        }
        double spotAngleRad = Math.toRadians(scene.getSpotAngle());
        Vector3f coneDir = scene.getSpotLights()[0].getPointLight().getPosition();
        coneDir.y = (float) Math.sin(spotAngleRad);

        daytimeCycle();

        for (Entity entity : scene.getEntities()) {
            renderer.processEntity(entity);
            //entity.increaseRotation(1, 1, 1);
        }

        for (Terrain terrain : scene.getTerrains()) {
            renderer.processTerrain(terrain);
        }
    }

    private void daytimeCycle() {

        scene.increaseLightAngle(1.1f);

        if (scene.getLightAngle() > 90) {
            scene.getDirectionalLight().setIntensity(0);
            if (scene.getLightAngle() >= 360)
                scene.setLightAngle(-90);
        } else if (scene.getLightAngle() <= -80 || scene.getLightAngle() >= 80) {
            float factor = 1 - (Math.abs(scene.getLightAngle()) - 80) / 10.0f;
            scene.getDirectionalLight().setIntensity(factor);
            scene.getDirectionalLight().getColor().x = Math.max(factor, 0.9f);
            scene.getDirectionalLight().getColor().z = Math.max(factor, 0.5f);
        } else {
            scene.getDirectionalLight().setIntensity(1);
            scene.getDirectionalLight().getColor().x = 1;
            scene.getDirectionalLight().getColor().z = 1;
            scene.getDirectionalLight().getColor().y = 1;
        }

        double angle = Math.toRadians(scene.getLightAngle());
        scene.getDirectionalLight().getDirection().x = (float) Math.sin(angle);
        scene.getDirectionalLight().getDirection().y = (float) Math.cos(angle);
    }

    private void checkCollision() {
        Vector3f newPosition = camera.getPosition();

        terrainCollision(newPosition);
        borderCollision(newPosition);

        camera.setPosition(newPosition);
    }

    private void borderCollision(Vector3f newPosition) {
        if (camera.getPosition().x < -Consts.SIZE_X / 2) {
            newPosition.x = -Consts.SIZE_X / 2;
            cameraInc.x = 0;
        } else if (camera.getPosition().x > Consts.SIZE_X / 2) {
            newPosition.x = Consts.SIZE_X / 2;
            cameraInc.x = 0;
        }
        if (camera.getPosition().z < -Consts.SIZE_Z / 2) {
            newPosition.z = -Consts.SIZE_Z / 2;
            cameraInc.z = 0;
        } else if (camera.getPosition().z > Consts.SIZE_Z / 2) {
            newPosition.z = Consts.SIZE_Z / 2;
            cameraInc.z = 0;
        }
    }

    private void terrainCollision(Vector3f newPosition) {
        // Correct the translation so -1000 maps to index 0
        int heightX = (int) ((newPosition.x + Consts.SIZE_X/2) * ((Consts.VERTEX_COUNT/2) / Consts.SIZE_X));
        int heightZ = (int) ((newPosition.z + Consts.SIZE_Z/2) * ((Consts.VERTEX_COUNT/2) / Consts.SIZE_Z));

        // Clamp values to ensure they fall within the heightmap's index range
        heightX = Math.max(0, Math.min(heightX, (Consts.VERTEX_COUNT/2)));
        heightZ = Math.max(0, Math.min(heightZ, (Consts.VERTEX_COUNT/2)));

        // Retrieve the terrain height using the clamped indices
        float terrainHeight = SceneManager.getHeightMap()[heightZ][heightX] + 5;
        if (newPosition.y <= terrainHeight) {
            newPosition.y = terrainHeight;
        }

        // Debug output to verify correct scaling and translation
        System.out.println("Debug - World Pos: (" + newPosition.x + ", " + newPosition.z +
                "), Scaled Indices: (" + heightX + ", " + heightZ +
                "), Height: " + terrainHeight);
    }

    private void entityCollision(Vector3f newPosition) {
        //TODO: Implement entity collision
        // Check if the new position is inside an entityOpenSafari
        // Implement a hit box for the entity
    }

    /**
     * Renders the game.
     * It renders the entity and the camera.
     */
    @Override
    public void render() {
        renderer.clear();

        renderer.render(camera, scene);
    }

    /**
     * Cleans up the game.
     * It cleans up the renderer and loader.
     */
    @Override
    public void cleanUp() {
        renderer.cleanup();
        loader.cleanUp();
    }
}
