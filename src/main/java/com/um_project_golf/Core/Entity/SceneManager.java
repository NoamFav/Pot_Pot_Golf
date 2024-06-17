package com.um_project_golf.Core.Entity;

import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
import com.um_project_golf.Core.Entity.Terrain.Terrain;
import com.um_project_golf.Game.GameUtils.Consts;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * The scene manager class.
 * This class is responsible for the scene manager of the game.
 */
@SuppressWarnings("unused")
public class SceneManager {

    private List<Entity> entities; // The list of entities.
    private List<Terrain> terrains; // The list of terrains.
    private static Texture defaultTexture; // The default texture.

    private SpotLight[] spotLights; // The spotlights.
    private DirectionalLight directionalLight; // The directional light.
    private PointLight[] pointLights; // The point lights.
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
    public void setEntities(List<Entity> entities) {this.entities = entities;}
    public void addEntity(Entity entity) {entities.add(entity);}
    public List<Terrain> getTerrains() {return terrains;}
    public void setTerrains(List<Terrain> terrains) {this.terrains = terrains;}
    public void addTerrain(Terrain terrain) {terrains.add(terrain);}
    public SpotLight[] getSpotLights() {return spotLights;}
    public void setSpotLights(SpotLight[] spotLights) {this.spotLights = spotLights;}
    public DirectionalLight getDirectionalLight() {return directionalLight;}
    public void setDirectionalLight(DirectionalLight directionalLight) {this.directionalLight = directionalLight;}
    public PointLight[] getPointLights() {return pointLights;}
    public void setPointLights(PointLight[] pointLights) {this.pointLights = pointLights;}
    public Vector3f getAmbientLight() {return ambientLight;}
    public void setAmbientLight(float x, float y, float z) {this.ambientLight = new Vector3f(x, y, z);}
    public void setAmbientLight(Vector3f ambientLight) {this.ambientLight = ambientLight;}
    public float getLightAngle() {return lightAngle;}
    public void setLightAngle(float lightAngle) {this.lightAngle = lightAngle;}
    public void increaseLightAngle(float lightAngle) {this.lightAngle += lightAngle;}
    public float getSpotAngle() {return spotAngle;}
    public void setSpotAngle(float spotAngle) {this.spotAngle = spotAngle;}
    public void increaseSpotAngle(float spotAngle) {this.spotAngle *= spotAngle;}
    public float getSpotInc() {return spotInc;}
    public void setSpotInc(float spotInc) {this.spotInc = spotInc;}
    public void setDefaultTexture(Texture defaultTexture) {SceneManager.defaultTexture = defaultTexture;}
    public static Texture getDefaultTexture() { return defaultTexture;}
    public List<float[]> getTreePositions() { return treePositions;}
    public void setTreePositions(List<float[]> treePosition) {treePositions = treePosition;}
    public void addTreePosition(float[] treePosition) {treePositions.add(treePosition);}
}
