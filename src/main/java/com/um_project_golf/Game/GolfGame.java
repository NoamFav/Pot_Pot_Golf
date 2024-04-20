package com.um_project_golf.Game;

import com.um_project_golf.Core.*;
import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.Entity.Texture;
import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
import com.um_project_golf.Core.MouseInput;
import com.um_project_golf.Core.Utils.Consts;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * The main game logic class.
 * This class is responsible for initializing the game, handling input, updating the game state and rendering the game.
 */
public class GolfGame implements ILogic {

    private static final float CAMERA_MOVEMENT_SPEED = 0.05f;

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private final List<Entity> entities = new ArrayList<>();
    private final Camera camera;

    Vector3f cameraInc;

    private float lightAngle;
    private DirectionalLight directionalLight;
    private PointLight pointLight;
    private SpotLight spotLight;
    /**
     * The constructor of the game.
     * It initializes the renderer, window, loader and camera.
     */
    public GolfGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        lightAngle = -90;
    }

    /**
     * Initializes the game.
     * It loads the model and texture of the game.
     *
     * @throws Exception If the game fails to initialize.
     */
    @Override
    public void init() throws Exception {
        renderer.init();

        //TODO: Allow multiple textures for the same model

        Model skull = loader.loadOBJModel("/Models/Minecraft_Grass_Block_OBJ/Grass_Block.obj");
        skull.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Minecraft_Grass_Block_OBJ/Grass_Block_TEX.png")), 1f);
        Entity skull_entity = new Entity(skull, new Vector3f(0,0,1), new Vector3f(0,1,0), 1);
        entities.add(skull_entity);

        float lightIntensity = 1.0f;
        //point light
        Vector3f lightPosition = new Vector3f(0, 0, -3.2f);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        pointLight = new PointLight(lightColor, lightPosition, lightIntensity);

        //spot light
        Vector3f coneDirection = new Vector3f(0, 0, -1);
        float cutOff = (float) Math.cos(Math.toRadians(180));
        spotLight = new SpotLight(new PointLight(lightColor, new Vector3f(0,0,1f), lightIntensity, 0,0,1), coneDirection, cutOff);

        //directional light
        lightPosition = new Vector3f(-1, -10, 0);
        lightColor = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColor, lightPosition, lightIntensity);
    }

    /**
     * Handles the input of the game.
     * It sets the cameraInc vector based on the input of the user.
     */
    @Override
    public void input() {
        cameraInc.set(0, 0, 0);

        float moveSpeed = 1;
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
        if(window.is_keyPressed(GLFW.GLFW_KEY_SPACE) && window.is_keyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            cameraInc.y = -moveSpeed;
        } else if(window.is_keyPressed(GLFW.GLFW_KEY_SPACE)) {
            cameraInc.y = moveSpeed;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_LEFT)) {
            pointLight.getPosition().x += 0.1f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_RIGHT)) {
            pointLight.getPosition().x -= 0.1f;
        }

        float lightPos = spotLight.getPointLight().getPosition().z;
        if (window.is_keyPressed(GLFW.GLFW_KEY_I)) {
            spotLight.getPointLight().getPosition().z = lightPos + 0.1f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_K)) {
            spotLight.getPointLight().getPosition().z = lightPos - 0.1f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_L)) {
            spotLight.getPointLight().getPosition().x += 0.1f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_J)) {
            spotLight.getPointLight().getPosition().x -= 0.1f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_O)) {
            spotLight.getPointLight().getPosition().y += 0.1f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_U)) {
            spotLight.getPointLight().getPosition().y -= 0.1f;
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
        camera.movePosition(cameraInc.x * CAMERA_MOVEMENT_SPEED, cameraInc.y * CAMERA_MOVEMENT_SPEED, cameraInc.z * CAMERA_MOVEMENT_SPEED);

        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
        }

        for (Entity entity : entities)
            entity.increaseRotation(0.0f, 0, 0.0f);

        lightAngle += 0.5f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360)
                lightAngle = -90;
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().x = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().z = 1;
            directionalLight.getColor().y = 1;
        }
        double angle = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angle);
        directionalLight.getDirection().y = (float) Math.cos(angle);
    }

    /**
     * Renders the game.
     * It renders the entity and the camera.
     */
    @Override
    public void render() {
        if (window.isResized()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(true);
        }

        renderer.clear();

        for (Entity entity : entities)
            renderer.render(entity, camera, directionalLight, pointLight, spotLight);
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
