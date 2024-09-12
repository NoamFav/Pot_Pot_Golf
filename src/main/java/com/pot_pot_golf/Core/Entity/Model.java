package com.pot_pot_golf.Core.Entity;

/**
 * The model class.
 * This class is responsible for the models of the game.
 */
public class Model {

    private final int id; // The id of the model.
    private final int vertexCount; // The vertex count of the model.
    private Material material; // The material of the model.

    /**
     * The constructor of the model.
     * It initializes the id and vertex count of the model.
     *
     * @param id The id of the model.
     * @param vertexCount The vertex count of the model.
     */
    public Model(int id, int vertexCount) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material();
    }

    public int getId() {
        return id;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Texture getTexture() {
        return material.getTexture();
    }

    /**
     * Sets the texture of the model.
     *
     * @param texture The texture to set.
     */
    public void setTexture(Texture texture) {
        this.material.setTexture(texture);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}