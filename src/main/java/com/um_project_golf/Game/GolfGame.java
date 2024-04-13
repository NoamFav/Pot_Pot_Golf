package com.um_project_golf.Game;

import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.ILogic;
import com.um_project_golf.Core.ObjectLoader;
import com.um_project_golf.Core.RenderManager;
import com.um_project_golf.Core.WindowManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class GolfGame implements ILogic {

    private int direction = 0;
    private float colour = 0.0f;

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private Model model;

    public GolfGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        float[] vertices = new float[]{
                // First Triangle
                -0.5f,  0.5f, 0.0f,  // Top-left
                0.5f,  0.5f, 0.0f,  // Top-right
                -0.5f, -0.5f, 0.0f,  // Bottom-left
                // Second Triangle
                -0.5f, -0.5f, 0.0f,  // Bottom-left
                0.5f,  0.5f, 0.0f,  // Top-right
                0.5f, -0.5f, 0.0f   // Bottom-right
        };

        int[] indices = new int[]{
                0, 1, 3,  // First Triangle
                3, 1, 2   // Second Triangle
        };

        model = loader.loadModel(vertices, indices);
    }

    @Override
    public void input() {
        if (window.is_keyPressed(GLFW.GLFW_KEY_UP)) {
            direction = 1;
        } else if (window.is_keyPressed(GLFW.GLFW_KEY_DOWN)) {
            direction = -1;
        } else {
            direction = 0;
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
    }

    @Override
    public void render() {
        if (window.isResized()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(true);
        }

        window.setClearColor(colour, colour, colour, 0.0f);
        renderer.render(model);
    }

    @Override
    public void cleanUp() {
        renderer.cleanup();
        loader.cleanUp();
    }
}
