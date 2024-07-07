package com.pot_pot_golf.Core.Entity;

import com.pot_pot_golf.Game.GameUtils.Consts;
import org.joml.Vector4f;

/**
 * The material class.
 * This class is responsible for the materials of the game.
 */
@SuppressWarnings("unused")
public class Material {


    private Vector4f ambientColor; // ambient color of the material (color of the material in the shadow)
    private Vector4f diffuseColor; // diffuse color of the material (color of the material in the light)
    private Vector4f specularColor; // specular color of the material (color of the material in the reflection)
    private float reflectance; // reflectance of the material (how much light is reflected)
    private Texture texture; // texture of the material
    private boolean disableCulling; // disable culling

    /**
     * The constructor of the material.
     * It initializes the colors and the reflectance of the material.
     */
    public Material() {
        this.ambientColor = Consts.DEFAULT_COLOR;
        this.diffuseColor = Consts.DEFAULT_COLOR;
        this.specularColor = Consts.DEFAULT_COLOR;
        this.texture = null; // no texture
        this.reflectance = 0; // no reflectance
        this.disableCulling = false; // enable culling
    }

    /**
     * The constructor of the material.
     * It initializes the color and the reflectance of the material.
     *
     * @param color The color of the material.
     * @param reflectance The reflectance of the material.
     */
    public Material(Vector4f color, float reflectance) {
        this(color, color, color, reflectance, null);
    }

    /**
     * The constructor of the material.
     * It initializes the color, the reflectance, and the texture of the material.
     *
     * @param color The color of the material.
     * @param reflectance The reflectance of the material.
     * @param texture The texture of the material.
     */
    public Material(Vector4f color, float reflectance, Texture texture) {
        this(color, color, color, reflectance, texture);
    }

    /**
     * The constructor of the material.
     * @param texture The texture of the material.
     */
    public Material(Texture texture, float reflectance) {
        this(Consts.DEFAULT_COLOR, Consts.DEFAULT_COLOR, Consts.DEFAULT_COLOR, reflectance, texture); // default color
    }

    /**
     * The constructor of the material.
     * It initializes the colors, the reflectance, and the texture of the material.
     *
     * @param ambientColor The ambient color of the material.
     * @param diffuseColor The diffuse color of the material.
     * @param specularColor The specular color of the material.
     * @param reflectance The reflectance of the material.
     * @param texture The texture of the material.
     */
    public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float reflectance, Texture texture) {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.reflectance = reflectance;
        this.texture = texture;
    }

    public Vector4f getAmbientColor() {
        return ambientColor;
    }

    public void setAmbientColor(Vector4f ambientColor) {
        this.ambientColor = ambientColor;
    }

    public Vector4f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Vector4f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public Vector4f getSpecularColor() {
        return specularColor;
    }

    public void setSpecularColor(Vector4f specularColor) {
        this.specularColor = specularColor;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    /**
     * Checks if the material has a texture.
     *
     * @return True if the material has a texture, false otherwise.
     */
    public boolean hasTexture() {
        return this.texture != null;
    }

    public boolean isDisableCulling() {
        return disableCulling;
    }

    public void setDisableCulling(boolean disableCulling) {
        this.disableCulling = disableCulling;
    }
}
