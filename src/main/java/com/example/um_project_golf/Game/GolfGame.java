package com.example.um_project_golf.Game;

import com.example.um_project_golf.Core.ILogic;
import com.example.um_project_golf.Core.RenderManager;
import com.example.um_project_golf.Core.WindowManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class GolfGame implements ILogic {

    private int direction = 0;
    private float colour = 0.0f;

    private final RenderManager renderer;
    private final WindowManager window;

    public GolfGame() {
        this.renderer = new RenderManager();
        this.window = Launcher.getWindow();
    }

    @Override
    public void init() throws Exception {
        renderer.init();
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
        renderer.clear();
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
    }
}
