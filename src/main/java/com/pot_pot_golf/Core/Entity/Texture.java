package com.pot_pot_golf.Core.Entity;

/**
 * The texture class.
 * This class is responsible for the textures of the game.
 */
public class Texture {

    private int id; // The id of the texture.

    /**
     * The constructor of the texture.
     * It initializes the id of the texture.
     *
     * @param id The id of the texture.
     */
    public Texture(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {this.id = id;}
}