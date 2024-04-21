package com.um_project_golf.Core;

import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.Model;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class responsible for rendering the game.
 */
public class RenderManager {

    private static final Logger log = LogManager.getLogger(RenderManager.class);
    private final WindowManager window;
    private ShaderManager shader;

    private final Map<Model, List<Entity>> entities = new HashMap<>();

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

        shader.createPointLightListUniform("pointLights", Consts.MAX_POINT_LIGHTS);
        shader.createSpotLightListUniform("spotLights" , Consts.MAX_SPOT_LIGHTS);
    }

    public void bind(Model model) {

        shader.setUniform("material", model.getMaterial());

        GL30.glBindVertexArray(model.getId());

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void unbind() {

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    public void prepare(Entity entity, Camera camera) {
        shader.setUniform("textureSampler", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
    }

    public void renderLight(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight) {
        shader.setUniform("ambientLight", Consts.AMBIENT_LIGHT);
        shader.setUniform("specularPower", Consts.SPECULAR_POWER);

        int numLights = pointLights != null ? pointLights.length : 0;
        for (int i = 0 ; i < numLights; i++) {
            shader.setUniform("pointLights[" + i + "]", pointLights[i]);
        }

        numLights = spotLights != null ? spotLights.length : 0;
        for (int i = 0 ; i < numLights; i++) {
            shader.setUniform("spotLights[" + i + "]", spotLights[i]);
        }

        shader.setUniform("directionalLight", directionalLight);
    }

    /**
     * Renders the entity.
     *
     * @param camera The camera of the game.
     */
    public void render(Camera camera, DirectionalLight directionalLight, PointLight[] pointLights, SpotLight[] spotLights) {

        if (window.isResized()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(true);
        }

        shader.bind();
        shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
        renderLight(camera, pointLights, spotLights, directionalLight);
        for(Model model : entities.keySet()) {
            bind(model);
            List<Entity> batch = entities.get(model);
            for(Entity ent : batch) {
                prepare(ent, camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, ent.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbind();
        }
        entities.clear();
        shader.unbind();
    }

    public void processEntity(Entity entity) {
        List<Entity> batch = entities.get(entity.getModel());
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new java.util.ArrayList<>();
            newBatch.add(entity);
            entities.put(entity.getModel(), newBatch);
        }
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
