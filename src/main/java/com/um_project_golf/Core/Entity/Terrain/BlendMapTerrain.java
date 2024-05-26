package com.um_project_golf.Core.Entity.Terrain;

import java.util.List;

/**
 * The BlendMapTerrain class is used to store the textures of the terrain.
 */
public class BlendMapTerrain {

    List<TerrainTexture> textures; // textures of the terrain

    /**
     * Create a new blend map terrain
     * @param textures The textures of the terrain
     */
    public BlendMapTerrain(List<TerrainTexture> textures) {
        this.textures = textures;
    }

    public List<TerrainTexture> getTextures() {
        return textures;
    }

    @SuppressWarnings("unused")
    public void setTextures(List<TerrainTexture> textures) {
        this.textures = textures;
    }

    @SuppressWarnings("unused")
    public void addTexture(TerrainTexture texture) {
        textures.add(texture);
    }

    @SuppressWarnings("unused")
    public void removeTexture(TerrainTexture texture) {
        textures.remove(texture);
    }
}
