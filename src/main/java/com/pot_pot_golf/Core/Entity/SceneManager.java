package com.pot_pot_golf.Core.Entity;

import com.pot_pot_golf.Core.Lighting.DirectionalLight;
import com.pot_pot_golf.Core.Entity.Terrain.Terrain;
import com.pot_pot_golf.Game.GameUtils.Consts;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * The scene manager class.
 * This class is responsible for the scene manager of the game.
 */

public class SceneManager {

    private List<Entity> entities; // The list of entities.
    private List<Terrain> terrains; // The list of terrains.
    private static Texture defaultTexture; // The default texture.

    private DirectionalLight directionalLight; // The directional light.
    private Vector3f ambientLight; // The ambient light.
    private float lightAngle; // The light angle.
    private float spotAngle = 0; // The spot angle.
    private float spotInc = 1; // The spot increment.

    private static float[][] heightMap; // The height map.
    private static List<float[]> treePositions; // The tree positions.

    /**
     * The constructor of the scene manager.
     * It initializes the entities, terrains, and the ambient light.
     *
     * @param lightAngle The light angle.
     */
    public SceneManager(float lightAngle) {
        entities = new ArrayList<>();
        terrains = new ArrayList<>();
        ambientLight = Consts.AMBIENT_LIGHT;
        this.lightAngle = lightAngle;
    }

    public static float[][] getHeightMap() {return heightMap;}
    public static void setHeightMap(float[][] heightMaps) {heightMap = heightMaps;}
    public List<Entity> getEntities() {return entities;}
    public void addEntity(Entity entity) {entities.add(entity);}
    public List<Terrain> getTerrains() {return terrains;}
    public void addTerrain(Terrain terrain) {terrains.add(terrain);}
    public DirectionalLight getDirectionalLight() {return directionalLight;}
    public void setDirectionalLight(DirectionalLight directionalLight) {this.directionalLight = directionalLight;}
    public void setLightAngle(float lightAngle) {this.lightAngle = lightAngle;}
    public void increaseLightAngle(float lightAngle) {this.lightAngle += lightAngle;}
    public float getSpotAngle() {return spotAngle;}
    public void increaseSpotAngle(float spotAngle) {this.spotAngle *= spotAngle;}
    public void setSpotInc(float spotInc) {this.spotInc = spotInc;}
    public void setDefaultTexture(Texture defaultTexture) {SceneManager.defaultTexture = defaultTexture;}
    public static Texture getDefaultTexture() { return defaultTexture;}
    public List<float[]> getTreePositions() { return treePositions;}
    public void setTreePositions(List<float[]> treePosition) {treePositions = treePosition;}
    public void addTreePosition(float[] treePosition) {treePositions.add(treePosition);}
    public void clearTreePositions() {treePositions.clear();}
}
