package com.um_project_golf.Game;

import com.um_project_golf.Core.*;
import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.Entity.Texture;
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
        //TODO: Allow multiple models for the same entity

        Model model = loader.loadOBJModel("/Models/HumanHeart_OBJ/Heart.obj");
        model.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/HumanHeart_OBJ/HumanBase__normals.png")), 1f);
        model.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/HumanHeart_OBJ/HumanBase___cavity.png")), 1f);
        model.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/HumanHeart_OBJ/HumanOpening__cavity.png")), 1f);
        model.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/HumanHeart_OBJ/HumanOpening__color.png")), 1f);
        model.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/HumanHeart_OBJ/HumanOpening__normals.png")), 1f);
        model.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/HumanHeart_OBJ/lightbox-ny-600.jpg")), 1f);
        model.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/HumanHeart_OBJ/MitralValve__cavity.png")), 1f);
        model.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/HumanHeart_OBJ/MitralValve_normals.png")), 1f);
        model.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/HumanHeart_OBJ/TricuspidValve_cavity.png")), 1f);
        model.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/HumanHeart_OBJ/TricuspidValve_normals.png")), 1f);
        model.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/HumanHeart_OBJ/HumanBase__color.png")), 1f);
        Entity entity = new Entity(model, new Vector3f(0,0,-1), new Vector3f(0,0,0), 1);
        entities.add(entity);

        Model skull = loader.loadOBJModel("/Models/Skull_v3_L2.123c1407fc1e-ea5c-4cb9-9072-d28b8aba4c36/skulls.obj");
        skull.setTexture(new Texture(loader.loadTexture("src/main/resources/Models/Skull_v3_L2.123c1407fc1e-ea5c-4cb9-9072-d28b8aba4c36/Skull.jpg")), 1f);
        Entity entity2 = new Entity(skull, new Vector3f(0,0,1), new Vector3f(0,1,0), 1);
        entities.add(entity2);
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
            entity.increaseRotation(0.0f, 0.25f, 0.0f);
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
            renderer.render(entity, camera);
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
