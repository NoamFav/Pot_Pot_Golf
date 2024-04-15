package com.um_project_golf.Game;

import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.Entity.Texture;
import com.um_project_golf.Core.ILogic;
import com.um_project_golf.Core.ObjectLoader;
import com.um_project_golf.Core.RenderManager;
import com.um_project_golf.Core.WindowManager;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class GolfGame implements ILogic {

    private int direction = 0;
    private float colour = 0.0f;

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private Entity entity;

    public GolfGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        float[] vertices = {
                -0.5f,  0.5f, 0f, // Top-left
                -0.5f, -0.5f, 0f, // Bottom-left
                0.5f, -0.5f, 0f,  // Bottom-right
                0.5f,  0.5f, 0f   // Top-right
        };

        int[] indices = {
                0, 1, 2,
                2, 3, 0
        };

        float[] textureCoords = {
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };

        Model model = loader.loadModel(vertices, textureCoords, indices);
        model.setTexture(new Texture(loader.loadTexture("Texture/Nyan.png")));
        entity = new Entity(model, new Vector3f(1,0,0), new Vector3f(0,0,0), 1);
    }

    @Override
    public void input() {
        if (window.is_keyPressed(GLFW.GLFW_KEY_SPACE)) {
            direction = 1;
        } else if (window.is_keyPressed(GLFW.GLFW_KEY_BACKSPACE)) {
            direction = -1;
        } else {
            direction = 0;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_LEFT)) {
            entity.getPos().x -= 0.02f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_RIGHT)) {
            entity.getPos().x += 0.02f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_UP)) {
            entity.getPos().y += 0.02f;
        }
        if (window.is_keyPressed(GLFW.GLFW_KEY_DOWN)) {
            entity.getPos().y -= 0.02f;
        }
    }

    @Override
    public void update() {
        colour += direction * 0.01f;
        if (colour > 1.0f) {
            colour = 1.0f;
        } else if (colour < 0.0f) {
            colour = 0.0f;
        }

        if(entity.getPos().x < -1.5f) {
            entity.getPos().x = 1.5f;
        }
        if (entity.getPos().x > 1.5f) {
            entity.getPos().x = -1.5f;
        }
        if (entity.getPos().y < -1.5f) {
            entity.getPos().y = 1.5f;
        }
        if (entity.getPos().y > 1.5f) {
            entity.getPos().y = -1.5f;
        }

    }

    @Override
    public void render() {
        if (window.isResized()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(true);
        }

        window.setClearColor(colour, colour, colour, 0.0f);
        renderer.render(entity);
    }

    @Override
    public void cleanUp() {
        renderer.cleanup();
        loader.cleanUp();
    }
}
