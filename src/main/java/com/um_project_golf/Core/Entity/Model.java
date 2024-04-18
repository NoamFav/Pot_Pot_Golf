package com.um_project_golf.Core.Entity;

/**
 * The model class.
 * This class is responsible for the models of the game.
 */
public class Model {

    private final int id;
    private final int vertexCount;
    private Texture texture;

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
        this.texture = texture;
    }

    /**
     * The constructor of the model.
     * It initializes the model with the same values of another model.
     *
     * @param model The model to copy.
     */
    public Model(Model model, Texture texture) {
        this.id = model.getId();
        this.vertexCount = model.getVertexCount();
        this.texture = texture;
    }

    public int getId() {
        return id;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Texture getTexture() {
        return texture;
    }

    /**
     * Sets the texture of the model.
     *
     * @param texture The texture to set.
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}