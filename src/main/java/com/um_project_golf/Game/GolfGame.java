package com.um_project_golf.Game;

import com.um_project_golf.Core.*;
import com.um_project_golf.Core.AWT.Button;
import com.um_project_golf.Core.AWT.Title;
import com.um_project_golf.Core.Entity.*;
import com.um_project_golf.Core.Entity.Terrain.*;
import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
import com.um_project_golf.Core.MouseInput;
import com.um_project_golf.Core.Rendering.RenderManager;
import com.um_project_golf.Core.Utils.Consts;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

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
    private long vg;
    private final List<Button> buttons;
    private Title title;

    private final Camera camera;
    private Terrain terrain;
    private Terrain ocean;
    private final HeightMap heightMap;
    private BlendMapTerrain blendMapTerrain;
    private AudioManager audioManager;

    Vector3f cameraInc;
    private boolean canMove = false;
    private boolean isJumping = false;
    private boolean isGuiVisible = true;
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
        heightMap = new HeightMap();
        buttons = new ArrayList<>();
    }

    /**
     * Initializes the game.
     * It loads the model and texture of the game.
     *
     * @throws Exception If the game fails to initialize.
     */
    @Override
    public void init() throws Exception {
        System.out.println("Initializing game");

        heightMap.createHeightMap();
        scene.setDefaultTexture(new Texture(loader.loadTexture("Texture/Default.png")));
        window.setAntiAliasing(true);
        window.setResized(false);

        renderer.init();
        window.setClearColor(0.529f, 0.808f, 0.922f, 0.0f);

        //Model cube = loader.loadAssimpModel("src/main/resources/Models/Minecraft_Grass_Block_OBJ/SkyBox.obj");
        //Model skull = loader.loadAssimpModel("src/main/resources/Models/Skull/skulls.obj");
        Model tree = loader.loadAssimpModel("src/main/resources/Models/tree/Tree.obj");
        Model wolf = loader.loadAssimpModel("src/main/resources/Models/Wolf_dae/wolf.dae");
        Model skyBox = loader.loadAssimpModel("src/main/resources/Models/Skybox/SkyBox.obj");
        Model ball = loader.loadAssimpModel("src/main/resources/Models/Ball/ImageToStl.com_ball.obj");

        //cube.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Minecraft_Grass_Block_OBJ/Grass_Block_TEX.png")), 1f);
        //skull.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Skull/Skull.jpg")), 1f);
        tree.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/tree/Tree.jpg")), 1f);
        wolf.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Wolf_dae/Material__wolf_col_tga_diffuse.jpeg.001.jpg")), 1f);
        skyBox.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Skybox/DayLight.png")), 1f);
        ball.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Ball/Ball_texture/Golf_Ball.png")), 1f);

        tree.getMaterial().setDisableCulling(true);
        wolf.getMaterial().setDisableCulling(true);
        skyBox.getMaterial().setDisableCulling(true);
        ball.getMaterial().setDisableCulling(true);

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
        BlendMapTerrain blueTerrain = new BlendMapTerrain(waterTextures);

        terrain = new Terrain(new Vector3f(-Consts.SIZE_X/2 , 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0,0,0,0), 0.1f), blendMapTerrain, blendMap, false);
        ocean = new Terrain(new Vector3f(-Consts.SIZE_X/2 , -1, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0,0,0,0), 0.1f), blueTerrain, blendMap, true);
        scene.addTerrain(terrain);
        scene.addTerrain(ocean);
        ocean.getModel().getMaterial().setDisableCulling(true);

        createTrees(tree);

        scene.addEntity(new Entity(skyBox, new Vector3f(0, -10, 0), new Vector3f(90, 0, 0), Consts.SIZE_X / 2));

        scene.addEntity(new Entity(wolf, new Vector3f(0, terrain.getHeight(0,0), 0), new Vector3f(45, 0 , 0), 10 ));
        //scene.addEntity(new Entity(cube, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1 ));
        scene.addEntity(new Entity(ball, new Vector3f(0, terrain.getHeight(0, 0), 0), new Vector3f(50, 0, 0), 10));

        //TODO: Allow multiple textures for the same model
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
        createGUIs(blueTerrain, tree);
        isGuiVisible = true;
        canMove = false;

        audioManager = new AudioManager("src/main/resources/SoundTrack/wii.wav");
        audioManager.playSound();
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
     *
     * @param mouseInput The mouse input of the user.
     */
    @Override
    public void update(MouseInput mouseInput) {

        for (Button button : buttons) {
            button.update();
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

        if (canMove) {
            if (mouseInput.isRightButtonPressed()) {
                Vector2f rotVec = mouseInput.getDisplVec();
                camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
            }
        } else {
            camera.moveRotation(0, 0.1f, 0);
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

        // Render your UI elements like buttons
        if (isGuiVisible) {  // Add this line
            for (Button button : buttons) {
                button.render();
            }
            title.render();
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
        for (Button button : buttons) {
            button.cleanup();
        }
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

    private void checkCollision() {
        Vector3f newPosition = camera.getPosition();

        terrainCollision(newPosition);
        borderCollision(newPosition);

        camera.setPosition(newPosition);
    }

    private void borderCollision(Vector3f newPosition) {
        float outOfBounds = 1;
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
        if (camera.getPosition().y > Consts.MAX_HEIGHT) {
            newPosition.y = Consts.MAX_HEIGHT;
            cameraInc.y = 0;
        }
    }

    private void terrainCollision(Vector3f newPosition) {
        // Correct the translation so -1000 maps to index 0

        // Retrieve the terrain height using the clamped indices
        float terrainHeight = heightMap.getHeight(newPosition) + Consts.PLAYER_HEIGHT;
        if (newPosition.y <= terrainHeight) {
            newPosition.y = terrainHeight;
        }
    }

    private void entityCollision(Vector3f newPosition) {
        //TODO: Implement entity collision
        // Check if the new position is inside an entityOpenSafari
        // Implement a hit box for the entity
    }

    private void createTrees(Model tree) throws IOException {
        BufferedImage heightmapImage = ImageIO.read(new File("Texture/heightmap.png"));

        List<Vector3f> positions = new ArrayList<>();

        for (int x = 0; x < heightmapImage.getWidth(); x++) {
            for (int z = 0; z < heightmapImage.getHeight(); z++) {
                Color pixelColor = new Color(heightmapImage.getRGB(x, z));

                if (pixelColor.equals(Color.GREEN) || pixelColor.equals(Color.BLUE)) {
                    float terrainX = x / (float) heightmapImage.getWidth() * Consts.SIZE_X - Consts.SIZE_X / 2;
                    float terrainZ = z / (float) heightmapImage.getHeight() * Consts.SIZE_Z - Consts.SIZE_Z / 2;
                    float terrainY = heightMap.getHeight(new Vector3f(terrainX, 0, terrainZ));

                    positions.add(new Vector3f(terrainX, terrainY, terrainZ));
                }
            }
        }
        Random rnd = new Random();
        for (int i = 0; i < Math.max(Consts.SIZE_X, Consts.SIZE_Z) * 0.1; i++) {
            Vector3f position = positions.get(rnd.nextInt(positions.size()));
            scene.addEntity(new Entity(tree, new Vector3f(position.x, position.y, position.z), new Vector3f(-90, 0, 0), 0.03f));
        }
    }

    private void createGUIs(BlendMapTerrain blendMapTerrain, Model tree) {

        System.out.println("Creating GUIs from: " + Thread.currentThread().getStackTrace()[2]);

        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);

        float width = window.getWidth();
        float height = window.getHeight();

        Runnable terrainChanger = () -> {
            try {
                System.out.println("Changing terrain");
                heightMap.createHeightMap();
                TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture("Texture/heightmap.png"));
                scene.getTerrains().remove(terrain);
                scene.getTerrains().remove(ocean);
                SimplexNoise.shufflePermutation();
                terrain = new Terrain(new Vector3f(-Consts.SIZE_X/2 , 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0,0,0,0), 0.1f), blendMapTerrain, blendMap2, false);
                ocean = new Terrain(new Vector3f(-Consts.SIZE_X/2 , -1, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0,0,0,0), 0.1f), blendMapTerrain, blendMap2, true);
                scene.addTerrain(ocean);
                scene.addTerrain(terrain);
                scene.getEntities().removeIf(entity -> entity.getModel().equals(tree));
                try {
                    createTrees(tree);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                renderer.processTerrain(terrain);
                renderer.processTerrain(ocean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        Runnable startGame = () -> {
            System.out.println("Allowing movement");
            canMove = true;
            isGuiVisible = false;
        };

        Runnable quit = () -> {
            System.out.println("Quitting game");
            GLFW.glfwSetWindowShouldClose(Launcher.getWindow().getWindow(), true);;
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

        Button start = new Button(centerButtonX, centerButtonY, widthButton, heightButton, "Start", 100, startGame, vg, "Texture/buttons.png");
        buttons.add(start);

        Button changeTerrain = new Button(centerButtonX, centerButtonY + heightButton, widthButton, heightButton, "Change Terrain", 100, terrainChanger, vg, "Texture/buttons.png");
        buttons.add(changeTerrain);

        Button exit = new Button(centerButtonX, centerButtonY + heightButton * 2 , widthButton, heightButton, "Exit", 100, quit, vg, "Texture/buttons.png");
        buttons.add(exit);
    }

    private void recreateGUIs() {
        buttons.clear();

        if (vg != 0) {
            nvgDelete(vg); // Properly delete the old NanoVG context if needed
        }

        createGUIs(blendMapTerrain, scene.getEntities().get(2).getModel());
    }

    private void setUpMusic() {

    }
}
