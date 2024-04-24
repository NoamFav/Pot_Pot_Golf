package com.um_project_golf.Core.Rendering;

import com.um_project_golf.Core.Camera;
import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.Entity.Terrain.Terrain;
import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
import com.um_project_golf.Core.ShaderManager;
import com.um_project_golf.Core.Utils.Consts;
import com.um_project_golf.Core.Utils.Transformation;
import com.um_project_golf.Core.Utils.Utils;
import com.um_project_golf.Game.Launcher;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The class responsible for rendering the entities.
 */
public class TerrainRenderer implements IRenderer{

    ShaderManager shader;
    private final List<Terrain> terrains;

    /**
     * The constructor of the entity render.
     *
     * @throws Exception If the entity render fails to initialize.
     */
    public TerrainRenderer() throws Exception{
        terrains = new ArrayList<>();
        shader = new ShaderManager();
    }

    /**
     * Initializes the entity render.
     *
     * @throws Exception If the entity render fails to initialize.
     */
    @Override
    public void init() throws Exception {
        shader.createVertexShader(Utils.loadResource("/shaders/terrain_vertex.glsl"));
        shader.createFragmentShader(Utils.loadResource("/shaders/terrain_fragment.glsl"));
        shader.link();

        shader.createUniform("backgroundTexture");
        shader.createUniform("RTexture");
        shader.createUniform("GTexture");
        shader.createUniform("BTexture");
        shader.createUniform("blendMap");
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
     * @param spotLights The spotlights of the game.
     * @param directionalLight The directional light of the game.
     */
    @Override
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight) {
        shader.bind();
        shader.setUniform("projectionMatrix", Launcher.getWindow().updateProjectionMatrix());
        RenderManager.renderLight(pointLights, spotLights, directionalLight, shader);
        for(Terrain terrain : terrains) {
            bind(terrain.getModel());
            prepare(terrain, camera);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbind();
        }
        terrains.clear();
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
        GL30.glBindVertexArray(model.getId());

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        RenderManager.enableCulling();

        shader.setUniform("backgroundTexture", 0);
        shader.setUniform("RTexture", 1);
        shader.setUniform("GTexture", 2);
        shader.setUniform("BTexture", 3);
        shader.setUniform("blendMap", 4);
        shader.setUniform("material", model.getMaterial());


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
     * @param terrain The entity to prepare.
     * @param camera The camera of the game.
     */
    @Override
    public void prepare(Object terrain, Camera camera) {

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain) terrain).getBlendMapTerrain().getBackground().getId());

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain) terrain).getBlendMapTerrain().getRTexture().getId());

        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain) terrain).getBlendMapTerrain().getGTexture().getId());

        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain) terrain).getBlendMapTerrain().getBTexture().getId());

        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain) terrain).getBlendMap().getId());

        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Terrain) terrain));
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
    }

    public List<Terrain> getTerrain() {
        return terrains;
    }
}
