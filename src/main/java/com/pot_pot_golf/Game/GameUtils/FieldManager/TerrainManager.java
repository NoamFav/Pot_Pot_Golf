package com.pot_pot_golf.Game.GameUtils.FieldManager;

import com.pot_pot_golf.Core.Entity.Terrain.BlendMapTerrain;
import com.pot_pot_golf.Core.Entity.Terrain.Terrain;

/**
 * The terrain manager class.
 * This class is responsible for managing the terrains of the game.
 * Stores the terrains of the game.
 */
public class TerrainManager {
    // Terrains
    private Terrain terrain;
    private Terrain ocean;
    private BlendMapTerrain blendMapTerrain;
    private BlendMapTerrain blueTerrain;

    public Terrain getTerrain() {return terrain;}
    public void setTerrain(Terrain terrain) {this.terrain = terrain;}
    public Terrain getOcean() {return ocean;}
    public void setOcean(Terrain ocean) {this.ocean = ocean;}
    public BlendMapTerrain getBlendMapTerrain() {return blendMapTerrain;}
    public void setBlendMapTerrain(BlendMapTerrain blendMapTerrain) {this.blendMapTerrain = blendMapTerrain;}
    public BlendMapTerrain getBlueTerrain() {return blueTerrain;}
    public void setBlueTerrain(BlendMapTerrain blueTerrain) {this.blueTerrain = blueTerrain;}
}
