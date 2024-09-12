package com.pot_pot_golf.Core.Entity;

import com.pot_pot_golf.Core.Entity.Terrain.Terrain;

import java.util.ArrayList;
import java.util.List;

/**
 * The scene manager class.
 * This class is responsible for the scene manager of the game.
 */

public class SceneManager {

    private final List<Entity> entities; // The list of entities.
    private final List<Terrain> terrains; // The list of terrains.
    private static Texture defaultTexture; // The default texture.

    private static float[][] heightMap; // The height map.
    private static List<float[]> treePositions; // The tree positions.

    /**
     * The constructor of the scene manager.
     * It initializes the entities, terrains, and the ambient light.
     *
     */
    public SceneManager() {
        entities = new ArrayList<>();
        terrains = new ArrayList<>();
    }

    public static float[][] getHeightMap() {return heightMap;}
    public static void setHeightMap(float[][] heightMaps) {heightMap = heightMaps;}
    public List<Entity> getEntities() {return entities;}
    public void addEntity(Entity entity) {entities.add(entity);}
    public List<Terrain> getTerrains() {return terrains;}
    public void addTerrain(Terrain terrain) {terrains.add(terrain);}
    public void setDefaultTexture(Texture defaultTexture) {SceneManager.defaultTexture = defaultTexture;}
    public static Texture getDefaultTexture() { return defaultTexture;}
    public List<float[]> getTreePositions() { return treePositions;}
    public void setTreePositions(List<float[]> treePosition) {treePositions = treePosition;}
    public void addTreePosition(float[] treePosition) {treePositions.add(treePosition);}
    public void clearTreePositions() {treePositions.clear();}
}
