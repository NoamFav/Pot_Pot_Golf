package com.pot_pot_golf.Core.Rendering;

import com.pot_pot_golf.Core.Camera;
import com.pot_pot_golf.Core.Entity.Entity;
import com.pot_pot_golf.Core.Entity.Model;
import com.pot_pot_golf.Core.Entity.Terrain.Terrain;
import com.pot_pot_golf.Core.ShaderManager;
import com.pot_pot_golf.Core.WindowManager;
import com.pot_pot_golf.Game.GameUtils.Consts;
import com.pot_pot_golf.Game.Launcher;
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
     * @param shader The shader of the game.
     */
    public static void renderLight(@NotNull ShaderManager shader) {
        shader.setUniform("ambientLight", Consts.AMBIENT_LIGHT); // Set the ambient light.
    }

    /**
     * Renders the game.
     *
     * @param camera The camera of the game.
     */
    public void render(Camera camera) {

        if (window.isResized()) { // If the window is resized.
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight()); // Set the viewport.
            window.setResized(true); // Set the window as resized.
        }

        entityRenderer.render(camera); // Render the entities.
        terrainRenderer.render(camera); // Render the terrain.
    }

    /**
     * Enables culling.
     * (Culling is the process of discarding objects that are not visible to the camera.)
     */
    public static void enableCulling() {
        if (!isCulling) { // If culling is not enabled.
            GL11.glEnable(GL11.GL_CULL_FACE); // Enable culling.
            GL11.glCullFace(GL11.GL_BACK); // Cull the back face.
            isCulling = true; // Set culling as enabled.
        }
    }

    /**
     * Disables culling.
     * (Culling is the process of discarding objects that are not visible to the camera.)
     */
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

    /**
     * Processes the terrain.
     *
     * @param terrain The terrain to process.
     */
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
