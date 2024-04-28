package com.um_project_golf.Core.Entity;

import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
import com.um_project_golf.Core.Entity.Terrain.Terrain;
import com.um_project_golf.Core.Utils.Consts;
import org.joml.Vector3f;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SceneManager {

    private List<Entity> entities;
    private List<Terrain> terrains;
    private static Texture defaultTexture;

    private SpotLight[] spotLights;
    private DirectionalLight directionalLight;
    private PointLight[] pointLights;
    private Vector3f ambientLight;
    private float lightAngle;
    private float spotAngle = 0;
    private float spotInc = 1;

    private static float[][] heightMap;

    public static float[][] getHeightMap() {
        return heightMap;
    }

    public static void setHeightMap(float[][] heightMaps) {
        heightMap = heightMaps;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }

    public void setTerrains(List<Terrain> terrains) {
        this.terrains = terrains;
    }

    public void addTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public SpotLight[] getSpotLights() {
        return spotLights;
    }

    public void setSpotLights(SpotLight[] spotLights) {
        this.spotLights = spotLights;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public PointLight[] getPointLights() {
        return pointLights;
    }

    public void setPointLights(PointLight[] pointLights) {
        this.pointLights = pointLights;
    }

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(float x, float y, float z) {
        this.ambientLight = new Vector3f(x, y, z);
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public float getLightAngle() {
        return lightAngle;
    }

    public void setLightAngle(float lightAngle) {
        this.lightAngle = lightAngle;
    }

    public void increaseLightAngle(float lightAngle) {
        this.lightAngle += lightAngle;
    }

    public float getSpotAngle() {
        return spotAngle;
    }

    public void setSpotAngle(float spotAngle) {
        this.spotAngle = spotAngle;
    }

    public void increaseSpotAngle(float spotAngle) {
        this.spotAngle *= spotAngle;
    }

    public float getSpotInc() {
        return spotInc;
    }

    public void setSpotInc(float spotInc) {
        this.spotInc = spotInc;
    }

    public SceneManager(float lightAngle) {
        entities = new ArrayList<>();
        terrains = new ArrayList<>();
        ambientLight = Consts.AMBIENT_LIGHT;
        this.lightAngle = lightAngle;
    }

    public void setDefaultTexture(Texture defaultTexture) {
        SceneManager.defaultTexture = defaultTexture;
    }

    public static Texture getDefaultTexture() {
        return defaultTexture;
    }
}
