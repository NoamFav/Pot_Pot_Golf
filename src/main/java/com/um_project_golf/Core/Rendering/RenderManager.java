package com.um_project_golf.Core.Rendering;

import com.um_project_golf.Core.Camera;
import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
import com.um_project_golf.Core.ShaderManager;
import com.um_project_golf.Core.Utils.Consts;
import com.um_project_golf.Core.WindowManager;
import com.um_project_golf.Game.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * The class responsible for rendering the game.
 */
public class RenderManager {

    private static final Logger log = LogManager.getLogger(RenderManager.class);
    private final WindowManager window;
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;

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

        entityRenderer.init();
        terrainRenderer.init();
    }

    /**
     * Renders the light.
     *
     * @param pointLights The point lights of the game.
     * @param spotLights The spot lights of the game.
     * @param directionalLight The directional light of the game.
     * @param shader The shader of the game.
     */
    public static void renderLight(PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight, ShaderManager shader) {
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
     * Renders the game.
     *
     * @param camera The camera of the game.
     * @param scene The scene of the game.
     */
    public void render(Camera camera, SceneManager scene) {

        if (window.isResized()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(true);
        }

        entityRenderer.render(camera, scene.getPointLights(), scene.getSpotLights(), scene.getDirectionalLight());
        terrainRenderer.render(camera,  scene.getPointLights(), scene.getSpotLights(), scene.getDirectionalLight());
    }

    /**
     * Processes the entity.
     *
     * @param entity The entity to process.
     */
    public void processEntity(Entity entity) {
        List<Entity> batch = entityRenderer.getEntities().get(entity.getModel());
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new java.util.ArrayList<>();
            newBatch.add(entity);
            entityRenderer.getEntities().put(entity.getModel(), newBatch);
        }
    }

    public void processTerrain(Terrain terrain) {
        terrainRenderer.getTerrain().add(terrain);
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
        entityRenderer.cleanup();
        terrainRenderer.cleanup();
    }
}
