package com.um_project_golf.Core;

import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.Utils.Utils;
import com.um_project_golf.Game.Launcher;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class RenderManager {

    private final WindowManager window;
    private ShaderManager shader;

    public RenderManager() {
        this.window = Launcher.getWindow();
    }

    public void init() throws Exception {
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.glsl"));
        shader.link();
    }

    public void render(Model model) {
        clear();
        shader.bind();
        GL30.glBindVertexArray(model.id());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.vertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.unbind();
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        shader.cleanup();
    }
}
