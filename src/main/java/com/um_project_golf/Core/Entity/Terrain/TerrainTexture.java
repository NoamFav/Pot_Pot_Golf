package com.um_project_golf.Core.Entity.Terrain;

/**
 * The TerrainTexture class is used to store the texture of the terrain.
 */
public class TerrainTexture {

    private int id; // id of the texture

    public TerrainTexture(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}