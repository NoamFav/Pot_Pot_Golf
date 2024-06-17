package com.um_project_golf.Core.Rendering;

import com.um_project_golf.Core.Camera;
import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.Terrain;
import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
import com.um_project_golf.Core.ShaderManager;
import com.um_project_golf.Game.GameUtils.Consts;
import com.um_project_golf.Core.WindowManager;
import com.um_project_golf.Game.Launcher;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * The class responsible for rendering the game.
 */
public class RenderManager {

    private final WindowManager window;
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;

    private static boolean isCulling = false; // If the culling is enabled.

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
        entityRenderer = new EntityRenderer();
        terrainRenderer = new TerrainRenderer();

        entityRenderer.init(); // Initialize the entity renderer.
        terrainRenderer.init(); // Initialize the terrain renderer.
    }

    /**
     * Renders the light.
     *
     * @param pointLights The point lights of the game.
     * @param spotLights The spotlights of the game.
     * @param directionalLight The directional light of the game.
     * @param shader The shader of the game.
     */
    public static void renderLight(PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight, @NotNull ShaderManager shader) {
        shader.setUniform("ambientLight", Consts.AMBIENT_LIGHT); // Set the ambient light.
        shader.setUniform("specularPower", Consts.SPECULAR_POWER); // Set the specular power.

        int numLights = pointLights != null ? pointLights.length : 0; // Get the number of point lights.
        for (int i = 0 ; i < numLights; i++) { // For each point light.
            shader.setUniform("pointLights[" + i + "]", pointLights[i]); // Set the point light uniform.
        }

        numLights = spotLights != null ? spotLights.length : 0; // Get the number of spotlights.
        for (int i = 0 ; i < numLights; i++) { // For each spotlight.
            shader.setUniform("spotLights[" + i + "]", spotLights[i]); // Set the spotlight uniform.
        }

        shader.setUniform("directionalLight", directionalLight); // Set the directional light.
    }

    /**
     * Renders the game.
     *
     * @param camera The camera of the game.
     * @param scene The scene of the game.
     */
    public void render(Camera camera, SceneManager scene) {

        if (window.isResized()) { // If the window is resized.
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight()); // Set the viewport.
            window.setResized(true); // Set the window as resized.
        }

        entityRenderer.render(camera, scene.getPointLights(), scene.getSpotLights(), scene.getDirectionalLight()); // Render the entities.
        terrainRenderer.render(camera,  scene.getPointLights(), scene.getSpotLights(), scene.getDirectionalLight()); // Render the terrain.
    }

    public static void enableCulling() {
        if (!isCulling) { // If culling is not enabled.
            GL11.glEnable(GL11.GL_CULL_FACE); // Enable culling.
            GL11.glCullFace(GL11.GL_BACK); // Cull the back face.
            isCulling = true; // Set culling as enabled.
        }
    }

    public static void disableCulling() {
        if (isCulling) { // If culling is enabled.
            GL11.glDisable(GL11.GL_CULL_FACE); // Disable culling.
            isCulling = false; // Set culling as disabled.
        }
    }

    /**
     * Processes the entity.
     *
     * @param entity The entity to process.
     */
    public void processEntity(@NotNull Entity entity) {
        for (Model subModel : entity.getModels()) { // Loop through each submodel of the entity
            List<Entity> batch = entityRenderer.getEntities().get(subModel); // Get the batch for the submodel
            if (batch != null) { // If the batch is not null
                batch.add(entity); // Add the entity to the batch
            } else { // If the batch is null
                List<Entity> newBatch = new ArrayList<>(); // Create a new batch
                newBatch.add(entity); // Add the entity to the new batch
                entityRenderer.getEntities().put(subModel, newBatch); // Put the new batch in the entity map
            }
        }
    }

    public void processTerrain(Terrain terrain) {
        terrainRenderer.getTerrain().add(terrain); // Add the terrain to the terrain renderer.
    }
    /**
     * Clears the screen.
     */
    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // Clear the screen.
    }

    /**
     * Cleans up the render manager.
     */
    public void cleanup() {
        entityRenderer.cleanup(); // Clean up the entity renderer.
        terrainRenderer.cleanup(); // Clean up the terrain renderer.
    }
}
