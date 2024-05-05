package com.um_project_golf.Core.Entity.Terrain;

/**
 * The BlendMapTerrain class is used to store the textures of the terrain.
 */
public class BlendMapTerrain {

    TerrainTexture background, RTexture, GTexture, BTexture; // textures of the terrain

    /**
     * Create a new blend map terrain
     * @param background The background texture
     * @param rTexture The red texture
     * @param gTexture The green texture
     * @param bTexture The blue texture
     */
    public BlendMapTerrain(TerrainTexture background, TerrainTexture rTexture, TerrainTexture gTexture, TerrainTexture bTexture) {
        this.background = background;
        this.RTexture = rTexture;
        this.GTexture = gTexture;
        this.BTexture = bTexture;
    }

    public TerrainTexture getBackground() {
        return background;
    }

    public void setBackground(TerrainTexture background) {
        this.background = background;
    }

    public TerrainTexture getRTexture() {
        return RTexture;
    }

    public void setRTexture(TerrainTexture RTexture) {
        this.RTexture = RTexture;
    }

    public TerrainTexture getGTexture() {
        return GTexture;
    }

    public void setGTexture(TerrainTexture GTexture) {
        this.GTexture = GTexture;
    }

    public TerrainTexture getBTexture() {
        return BTexture;
    }

    public void setBTexture(TerrainTexture BTexture) {
        this.BTexture = BTexture;
    }
}
