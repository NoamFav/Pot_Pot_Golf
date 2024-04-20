package com.um_project_golf.Core;

import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
import com.um_project_golf.Core.Utils.Consts;
import com.um_project_golf.Core.Utils.Transformation;
import com.um_project_golf.Core.Utils.Utils;
import com.um_project_golf.Game.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * The class responsible for rendering the game.
 */
public class RenderManager {

    private static final Logger log = LogManager.getLogger(RenderManager.class);
    private final WindowManager window;
    private ShaderManager shader;

    /**
     * The constructor of the render manager.
     * It initializes the window.
     */
    public RenderManager() {
        window = Launcher.getWindow();
    }

    /**
     * Initializes the render manager.
     *
     * @throws Exception If the render manager fails to initialize.
     */
    public void init() throws Exception {
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.glsl"));
        shader.link();

        shader.createUniform("textureSampler");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("ambientLight");
        shader.createMaterialUniform("material");
        shader.createUniform("specularPower");
        shader.createDirectionalLightUniform("directionalLight");
        shader.createPointLightUniform("pointLight");
        shader.createSpotLightUniform("spotLight");
    }

    /**
     * Renders the entity.
     *
     * @param entity The entity to render.
     * @param camera The camera of the game.
     */
    public void render(Entity entity, Camera camera, DirectionalLight directionalLight, PointLight pointLights, SpotLight spotLight) {
        shader.bind();

        shader.setUniform("textureSampler", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));
        shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
        shader.setUniform("ambientLight", Consts.AMBIENT_LIGHT);
        shader.setUniform("material", entity.getModel().getMaterial());
        shader.setUniform("specularPower", Consts.SPECULAR_POWER);
        shader.setUniform("directionalLight", directionalLight);
        shader.setUniform("pointLight", pointLights);
        shader.setUniform("spotLight", spotLight);

        GL30.glBindVertexArray(entity.getModel().getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getModel().getTexture().getId());
        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
        shader.unbind();
    }

    /**
     * Clears the screen.
     */
    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Cleans up the render manager.
     */
    public void cleanup() {
        shader.cleanup();
    }
}
