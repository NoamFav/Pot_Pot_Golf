package com.pot_pot_golf.Core.Rendering;

import com.pot_pot_golf.Core.Camera;
import com.pot_pot_golf.Core.Entity.Model;
import com.pot_pot_golf.Core.Entity.Terrain.Terrain;
import com.pot_pot_golf.Core.Lighting.DirectionalLight;
import com.pot_pot_golf.Core.Lighting.PointLight;
import com.pot_pot_golf.Core.Lighting.SpotLight;
import com.pot_pot_golf.Core.ShaderManager;
import com.pot_pot_golf.Game.GameUtils.Consts;
import com.pot_pot_golf.Core.Utils.Transformation;
import com.pot_pot_golf.Core.Utils.Utils;
import com.pot_pot_golf.Game.Launcher;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The class responsible for rendering the entities.
 */
public class TerrainRenderer implements IRenderer<Terrain> {

    ShaderManager shader; // The shader of the entity render.
    private final List<Terrain> terrains; // The list of entities to render.

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
        shader.createVertexShader(Utils.loadResource("/shaders/terrain_vertex.glsl")); // Loads the vertex shader.
        shader.createFragmentShader(Utils.loadResource("/shaders/terrain_fragment.glsl")); // Loads the fragment shader.
        shader.link(); // Links the shader.

        for (int i = 0; i < Consts.MAX_TEXTURES; i++) {
            shader.createUniform("textures[" + i + "]");
        } // Creates the uniform for the textures.
        shader.createUniform("blendMap"); // Creates the uniform for the blend map.
        shader.createUniform("transformationMatrix"); // Creates the uniform for the transformation matrix.
        shader.createUniform("projectionMatrix"); // Creates the uniform for the projection matrix.
        shader.createUniform("viewMatrix"); // Creates the uniform for the view matrix.
        shader.createUniform("ambientLight"); // Creates the uniform for the ambient light.
        shader.createMaterialUniform("material"); // Creates the uniform for the material.
        shader.createUniform("specularPower"); // Creates the uniform for the specular power.
        shader.createDirectionalLightUniform("directionalLight"); // Creates the uniform for the directional light.

        shader.createPointLightListUniform("pointLights", Consts.MAX_POINT_LIGHTS); // Creates the uniform for the point lights.
        shader.createSpotLightListUniform("spotLights" , Consts.MAX_SPOT_LIGHTS); // Creates the uniform for the spotlights.
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
        shader.bind(); // Binds the shader.
        shader.setUniform("projectionMatrix", Launcher.getWindow().updateProjectionMatrix()); // Updates the projection matrix.
        RenderManager.renderLight(pointLights, spotLights, directionalLight, shader); // Renders the lights.
        for(Terrain terrain : terrains) { // Renders the entities.
            bind(terrain.getModel()); // Binds the entity.
            prepare(terrain, camera); // Prepares the entity.
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0); // Draws the entity.
            unbind(); // Unbinds the entity.
        }
        terrains.clear(); // Clears the list of entities.
        shader.unbind(); // Unbinds the shader.
    }

    /**
     * Cleans up the entity render.
     */
    @Override
    public void cleanup() {
        shader.cleanup(); // Cleans up the shader.
    }

    /**
     * Binds the model.
     *
     * @param model The model to bind.
     */
    @Override
    public void bind(@NotNull Model model) {
        GL30.glBindVertexArray(model.getId()); // Binds the VAO.

        GL20.glEnableVertexAttribArray(0); // Enables the vertex array.
        GL20.glEnableVertexAttribArray(1); // Enables the texture array.
        GL20.glEnableVertexAttribArray(2); // Enables the normal array.



        for (int i = 0; i < Consts.MAX_TEXTURES; i++) { // For each texture
            shader.setUniform("textures[" + i + "]", i); // Sets the texture
        }
        shader.setUniform("blendMap", Consts.MAX_TEXTURES); // Sets the blend map.
        shader.setUniform("material", model.getMaterial()); // Sets the material.


        GL11.glEnable(GL11.GL_BLEND); // Enables blending.
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // Enables blending.
    }

    /**
     * Unbinds the model.
     */
    @Override
    public void unbind() {
        GL20.glDisableVertexAttribArray(0); // Disables the vertex array.
        GL20.glDisableVertexAttribArray(1); // Disables the texture array.
        GL20.glDisableVertexAttribArray(2); // Disables the normal array.
        GL30.glBindVertexArray(0); // Unbinds the VAO.
    }

    /**
     * Prepares the entity.
     *
     * @param terrain The entity to prepare.
     * @param camera The camera of the game.
     */
    @Override
    public void prepare(@NotNull Terrain terrain, Camera camera) {
        for (int i = 0; i < (terrain).getBlendMapTerrain().getTextures().size(); i++) { // For each texture
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i); // Activates the texture
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, (terrain).getBlendMapTerrain().getTextures().get(i).getId()); // Binds the texture
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0 + Consts.MAX_TEXTURES); // Activates the texture. (Blend Map)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (terrain).getBlendMap().getId()); // Binds the texture.

        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(terrain)); // Sets the transformation matrix.
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera)); // Sets the view matrix.
    }

    public List<Terrain> getTerrain() {
        return terrains;
    }
}
