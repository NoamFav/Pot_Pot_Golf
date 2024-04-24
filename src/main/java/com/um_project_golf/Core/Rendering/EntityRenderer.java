package com.um_project_golf.Core.Rendering;

import com.um_project_golf.Core.Camera;
import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
import com.um_project_golf.Core.ShaderManager;
import com.um_project_golf.Core.Utils.Consts;
import com.um_project_golf.Core.Utils.Transformation;
import com.um_project_golf.Core.Utils.Utils;
import com.um_project_golf.Game.Launcher;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class responsible for rendering the entities.
 */
public class EntityRenderer implements IRenderer{

    ShaderManager shader;
    private final Map<Model, List<Entity>> entities;

    /**
     * The constructor of the entity render.
     *
     * @throws Exception If the entity render fails to initialize.
     */
    public EntityRenderer() throws Exception{
        entities = new HashMap<>();
        shader = new ShaderManager();
    }

    /**
     * Initializes the entity render.
     *
     * @throws Exception If the entity render fails to initialize.
     */
    @Override
    public void init() throws Exception {
        shader.createVertexShader(Utils.loadResource("/shaders/entity_vertex.glsl"));
        shader.createFragmentShader(Utils.loadResource("/shaders/entity_fragment.glsl"));
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

    /**
     * Renders the entities.
     *
     * @param camera The camera of the game.
     * @param pointLights The point lights of the game.
     * @param spotLights The spot lights of the game.
     * @param directionalLight The directional light of the game.
     */
    @Override
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight) {
        shader.bind();
        shader.setUniform("projectionMatrix", Launcher.getWindow().updateProjectionMatrix());
        RenderManager.renderLight(pointLights, spotLights, directionalLight, shader);
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

    /**
     * Cleans up the entity render.
     */
    @Override
    public void cleanup() {
        shader.cleanup();
    }

    /**
     * Binds the model.
     *
     * @param model The model to bind.
     */
    @Override
    public void bind(Model model) {
        shader.setUniform("material", model.getMaterial());

        GL30.glBindVertexArray(model.getId());

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        if (model.getMaterial().isDisableCulling()) {
            RenderManager.disableCulling();
        } else {
            RenderManager.enableCulling();
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Unbinds the model.
     */
    @Override
    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    /**
     * Prepares the entity.
     *
     * @param entity The entity to prepare.
     * @param camera The camera of the game.
     */
    @Override
    public void prepare(Object entity, Camera camera) {
        shader.setUniform("textureSampler", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Entity) entity));
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
    }

    public Map<Model, List<Entity>> getEntities() {
        return entities;
    }
}
