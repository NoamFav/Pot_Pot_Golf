package com.example.um_project_golf.Core;

import com.example.um_project_golf.Game.Launcher;
import org.lwjgl.opengl.GL11;

public class RenderManager {

    private final WindowManager window;

    public RenderManager() {
        this.window = Launcher.getWindow();
    }

    public void init() throws Exception {
    }

    public void render() {
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanUp() {

    }
}
