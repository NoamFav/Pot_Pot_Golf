package com.um_project_golf.Core.Rendering;

import com.um_project_golf.Core.Entity.Material;
import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.Entity.Texture;
import com.um_project_golf.Core.ObjectLoader;
import com.um_project_golf.Core.Utils.Consts;
import org.joml.Vector3f;

/**
 * The class responsible for the terrain.
 */
public class Terrain {

    private final Vector3f position;
    private final Model model;

    /**
     * The constructor of the terrain.
     * It initializes the position, loader and material of the terrain.
     *
     * @param position The position of the terrain.
     * @param loader The loader of the terrain.
     * @param material The material of the terrain.
     */
    public Terrain(Vector3f position, ObjectLoader loader, Material material, boolean isWater) {
        this.position = position;
        this.model = generateTerrain(loader, isWater);
        this.model.setMaterial(material);
    }

    /**
     * Generates the terrain.
     *
     * @param loader The loader of the terrain.
     * @return The model of the terrain.
     */
    private Model generateTerrain(ObjectLoader loader, boolean isWater) {
        int count = Consts.VERTEX_COUNT * Consts.VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (Consts.VERTEX_COUNT - 1) * (Consts.VERTEX_COUNT - 1)];
        int vertexPointer = 0;

        for(int i = 0; i < Consts.VERTEX_COUNT; i++){
            for(int j = 0; j < Consts.VERTEX_COUNT; j++){

                float x = j / (Consts.VERTEX_COUNT - 1f) * Consts.SIZE_X;
                float z = i / (Consts.VERTEX_COUNT - 1f) * Consts.SIZE_Z;
                float height = getHeight(x, z);

                vertices[vertexPointer * 3] = x;
                vertices[vertexPointer * 3 + 1] = isWater ? 0 : height;
                vertices[vertexPointer * 3 + 2] = z;

                normals[vertexPointer * 3] = 0;
                normals[vertexPointer * 3 + 1] = 1;
                normals[vertexPointer * 3 + 2] = 0;

                textureCoords[vertexPointer * 2] = x;
                textureCoords[vertexPointer * 2 + 1] = z;

                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz = 0; gz < Consts.VERTEX_COUNT - 1.0f; gz++){
            for(int gx = 0; gx < Consts.VERTEX_COUNT - 1.0f; gx++){
                int topLeft = (gz * Consts.VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * Consts.VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadModel(vertices, textureCoords, normals, indices);
    }

    public float getHeight(float x, float z) {
        return (float)(10 * Math.sin(x * 0.1) * Math.cos(z * 0.1) + 5 * Math.sin(x * 0.05) * Math.cos(z * 0.05));
    }


    public Model getModel() {
        return model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Material getMaterial() {
        return model.getMaterial();
    }

    public Texture getTexture() {
        return model.getTexture();
    }
}
