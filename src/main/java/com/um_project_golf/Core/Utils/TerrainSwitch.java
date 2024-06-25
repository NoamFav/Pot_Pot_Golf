package com.um_project_golf.Core.Utils;

import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.Material;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.BlendMapTerrain;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.Entity.Terrain.Terrain;
import com.um_project_golf.Core.Entity.Terrain.TerrainTexture;
import com.um_project_golf.Core.ObjectLoader;
import com.um_project_golf.Core.Rendering.RenderManager;
import com.um_project_golf.Game.GameUtils.Consts;
import com.um_project_golf.Game.GameUtils.FieldManager.EntitiesManager;
import com.um_project_golf.Game.GameUtils.FieldManager.ModelManager;
import com.um_project_golf.Game.GameUtils.FieldManager.TerrainManager;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.um_project_golf.Game.GolfGame.debugMode;

/**
 * The class responsible for switching the terrain of the game.
 */
public class TerrainSwitch {

    private final SceneManager scene;
    private final RenderManager renderer;
    private final HeightMap heightMap;
    private final ObjectLoader loader;

    private final TerrainManager terrainManager;
    private final ModelManager modelManager;
    private final EntitiesManager entitiesManager;

    /**
     * The constructor of the terrain switch.
     *
     * @param scene           The scene manager.
     * @param renderer        The render manager.
     * @param heightMap       The height map.
     * @param loader          The object loader.
     * @param terrainManager  The terrain manager.
     * @param modelManager    The model manager.
     * @param entitiesManager The entities manager.
     */
    public TerrainSwitch(SceneManager scene, RenderManager renderer, HeightMap heightMap, ObjectLoader loader, TerrainManager terrainManager, ModelManager modelManager, EntitiesManager entitiesManager) {
        this.scene = scene;
        this.renderer = renderer;
        this.heightMap = heightMap;
        this.loader = loader;
        this.terrainManager = terrainManager;
        this.modelManager = modelManager;
        this.entitiesManager = entitiesManager;
    }

    /**
     * Switches the terrain of the game.
     *
     * @param blendMapTerrain The new terrain to switch to.
     * @param blendMap2       The new blend map to use.
     */
    public void terrainSwitch(BlendMapTerrain blendMapTerrain, TerrainTexture blendMap2) {
        scene.getTerrains().remove(terrainManager.getTerrain());
        scene.getTerrains().remove(terrainManager.getOcean());
        Terrain terrain = new Terrain(new Vector3f(-Consts.SIZE_X / 2, 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0, 0, 0, 0), 0.1f), blendMapTerrain, blendMap2, false);
        Terrain ocean = new Terrain(new Vector3f(-Consts.SIZE_X / 2, 0, -Consts.SIZE_Z / 2), loader, new Material(new Vector4f(0, 0, 0, 0), 0.1f), terrainManager.getBlueTerrain(), blendMap2, true);
        terrainManager.setTerrain(terrain);
        terrainManager.setOcean(ocean);
        scene.addTerrain(terrain);
        scene.addTerrain(ocean);
        scene.getEntities().removeIf(entity -> entity.getModels().equals(modelManager.getTree()));
        scene.clearTreePositions();
        try {
            if (!debugMode) {
                createTrees();
            } else {
                if (Consts.WANT_TREE) { // If the trees are enabled in the Consts
                    createTrees();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        renderer.processTerrain(terrain);
    }

    /**
     * Place the trees on the terrain.
     * Randomly picks a position on the terrain.
     * As long as the position is green
     * @throws IOException If the heightmap image is not found.
     */
    public void createTrees() throws IOException {
        BufferedImage heightmapImage = ImageIO.read(new File(Consts.HEIGHTMAP));

        List<Vector3f> positions = new ArrayList<>();

        // Populate positions based on the heightmap image
        for (int x = 0; x < heightmapImage.getWidth(); x++) {
            for (int z = 0; z < heightmapImage.getHeight(); z++) {
                Color pixelColor = new Color(heightmapImage.getRGB(x, z));

                // Check for green or blue pixels
                if (pixelColor.equals(Color.GREEN)) {
                    float terrainX = x / (float) heightmapImage.getWidth() * Consts.SIZE_X - Consts.SIZE_X / 2;
                    float terrainZ = z / (float) heightmapImage.getHeight() * Consts.SIZE_Z - Consts.SIZE_Z / 2;
                    float terrainY = heightMap.getHeight(new Vector3f(terrainX, 0, terrainZ));

                    positions.add(new Vector3f(terrainX, terrainY, terrainZ));
                }
            }
        }

        // Ensure we have valid positions
        if (positions.isEmpty()) {
            System.out.println("No valid positions found for trees.");
            return;
        }

        List<float[]> treePositions = new ArrayList<>();
        Random rnd = new Random();
        Vector3f zero = new Vector3f(0, 0, 0);

        // Populate tree positions and add entities to the scene
        for (int i = 0; i < Consts.NUMBER_OF_TREES; i++) {
            Vector3f position = positions.get(rnd.nextInt(positions.size()));
            if (position != zero) {
                Entity aTree = new Entity(modelManager.getTree(), new Vector3f(position.x, position.y, position.z), new Vector3f(-90, 0, 0), 0.03f); // - 90 and 0.03f
                scene.addEntity(aTree);
                entitiesManager.addTree(aTree);
                entitiesManager.addTreeHeight(position.y);
                treePositions.add(new float[]{position.x, position.y, position.z});
            } else {
                System.out.println("Position is null at index: " + i);
            }
        }
        System.out.println("Tree length: " + treePositions.size());

        scene.setTreePositions(treePositions);
    }
}
