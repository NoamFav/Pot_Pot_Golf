package com.pot_pot_golf.Core.Rendering;

import com.pot_pot_golf.Core.Camera;
import com.pot_pot_golf.Core.Entity.Entity;
import com.pot_pot_golf.Core.Entity.Model;
import com.pot_pot_golf.Core.Entity.SceneManager;
import com.pot_pot_golf.Core.ShaderManager;
import com.pot_pot_golf.Core.Utils.Transformation;
import com.pot_pot_golf.Core.Utils.Utils;
import com.pot_pot_golf.Game.Launcher;
import org.jetbrains.annotations.NotNull;
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
public class EntityRenderer implements IRenderer<Entity> {

    ShaderManager shader; // The shader manager of the entity render.
    private final Map<Model, List<Entity>> entities; // The entities to render.

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
        shader.createVertexShader(Utils.loadResource("/shaders/entity_vertex.glsl")); // Load the vertex shader.
        shader.createFragmentShader(Utils.loadResource("/shaders/entity_fragment.glsl")); // Load the fragment shader.
        shader.link(); // Link the shaders.

        shader.createUniform("textureSampler"); // Create the texture sampler uniform.
        shader.createUniform("transformationMatrix"); // Create the transformation matrix uniform.
        shader.createUniform("projectionMatrix"); // Create the projection matrix uniform.
        shader.createUniform("viewMatrix"); // Create the view matrix uniform.
        shader.createUniform("ambientLight"); // Create the ambient light uniform.
        shader.createMaterialUniform("material"); // Create the material uniform.
    }

    /**
     * Renders the entities.
     *
     * @param camera The camera of the game.
     */
    @Override
    public void render(Camera camera) {
        shader.bind(); // Bind the shader.
        shader.setUniform("projectionMatrix", Launcher.getWindow().updateProjectionMatrix()); // Update the projection matrix.
        RenderManager.renderLight(shader); // Render the lights.

        for (Model model : entities.keySet()) { // For each model in the entities.
            bind(model); // Bind the model.
            List<Entity> batch = entities.get(model); // Get the entities.

            for (Entity entity : batch) { // For each entity in the batch.
                for (Model subModel : entity.getModels()) { // For each submodel in the entity.
                    bind(subModel); // Bind the submodel.
                    prepare(entity, camera); // Prepare the entity.
                    GL11.glDrawElements(GL11.GL_TRIANGLES, subModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0); // Draw the elements.
                    unbind(); // Unbind the submodel.
                }
            }
        }
        entities.clear(); // Clear the entities.
        shader.unbind(); // Unbind the shader.
    }

    /**
     * Cleans up the entity render.
     */
    @Override
    public void cleanup() {
        shader.cleanup(); // Clean up the shader for memory management.
    }

    /**
     * Binds the model.
     *
     * @param model The model to bind.
     */
    @Override
    public void bind(@NotNull Model model) {
        shader.setUniform("material", model.getMaterial()); // Set the material uniform.

        GL30.glBindVertexArray(model.getId()); // Bind the vertex array.

        GL20.glEnableVertexAttribArray(0); // Enable the vertex array.
        GL20.glEnableVertexAttribArray(1); // Enable the texture array.
        GL20.glEnableVertexAttribArray(2); // Enable the normal array.

        if (model.getMaterial().isDisableCulling()) { // If the material is set to disable culling.
            RenderManager.disableCulling(); // Disable culling.
        } else { // Otherwise.
            RenderManager.enableCulling(); // Enable culling.
        }

        if (model.getTexture() == null) { // If the model has no texture.
            model.setTexture(SceneManager.getDefaultTexture()); // Set the default texture.
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0); // Activate the texture.
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().id()); // Bind the texture.
        GL11.glEnable(GL11.GL_BLEND); // Enable blending.
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // Set the blending function.
    }

    /**
     * Unbinds the model.
     */
    @Override
    public void unbind() { // Unbind the model.
        GL20.glDisableVertexAttribArray(0); // Disable the vertex array.
        GL20.glDisableVertexAttribArray(1); // Disable the texture array.
        GL20.glDisableVertexAttribArray(2); // Disable the normal array.
        GL30.glBindVertexArray(0); // Unbind the vertex array.
    }

    /**
     * Prepares the entity.
     *
     * @param entity The entity to prepare.
     * @param camera The camera of the game.
     */
    @Override
    public void prepare(Entity entity, Camera camera) {
        shader.setUniform("textureSampler", 0); // Set the texture sampler.
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity)); // Set the transformation matrix.
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera)); // Set the view matrix.
    }

    public Map<Model, List<Entity>> getEntities() {
        return entities;
    }
}
