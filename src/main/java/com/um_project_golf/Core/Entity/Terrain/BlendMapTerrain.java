package com.um_project_golf.Core.Entity.Terrain;

public class BlendMapTerrain {

    TerrainTexture background, RTexture, GTexture, BTexture;

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
