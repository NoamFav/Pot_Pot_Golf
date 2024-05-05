package com.um_project_golf.Core.Entity;

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

    /**
     * The constructor of the model.
     * It initializes the id, vertex count, and texture of the model.
     *
     * @param id The id of the model.
     * @param vertexCount The vertex count of the model.
     * @param texture The texture of the model.
     */
    public Model(int id, int vertexCount, Texture texture) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material();
        this.material.setTexture(texture);
    }

    /**
     * The constructor of the model.
     * It initializes the model with the same values of another model.
     *
     * @param model The model to copy.
     * @param texture The texture of the model.
     */
    public Model(Model model, Texture texture) {
        this.id = model.getId();
        this.vertexCount = model.getVertexCount();
        this.material = model.getMaterial();
        this.material.setTexture(texture);
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

    /**
     * Sets the texture and reflectance of the model.
     *
     * @param texture The texture to set.
     * @param reflectance The reflectance to set.
     */
    public void setTexture(Texture texture, float reflectance) {
        this.material.setTexture(texture); // Set the texture.
        this.material.setReflectance(reflectance); // Set the reflectance.
    }
}