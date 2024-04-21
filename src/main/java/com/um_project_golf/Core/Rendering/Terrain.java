package com.um_project_golf.Core.Rendering;

import com.um_project_golf.Core.Entity.Material;
import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.Entity.Texture;
import com.um_project_golf.Core.ObjectLoader;
import org.joml.Vector3f;

public class Terrain {

    private static final float SIZE = 800;
    private static final int VERTEX_COUNT = 128;

    private final Vector3f position;
    private final Model model;

    public Terrain(Vector3f position, ObjectLoader loader, Material material) {
        this.position = position;
        this.model = generateTerrain(loader);
        this.model.setMaterial(material);
    }

    private Model generateTerrain(ObjectLoader loader) {
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;

        for(int i = 0; i < VERTEX_COUNT; i++){
            for(int j = 0; j < VERTEX_COUNT; j++){
                vertices[vertexPointer * 3] = j / ( VERTEX_COUNT - 1f) * SIZE;
                vertices[vertexPointer * 3 + 1] = 0; // Height of the terrain
                vertices[vertexPointer * 3 + 2] = i / ( VERTEX_COUNT - 1f) * SIZE;
                normals[vertexPointer * 3] = 0;
                normals[vertexPointer * 3 + 1] = 1;
                normals[vertexPointer * 3 + 2] = 0;
                textureCoords[vertexPointer * 2] = j / ( VERTEX_COUNT - 1f) * SIZE;
                textureCoords[vertexPointer * 2 + 1] = i / ( VERTEX_COUNT - 1f) * SIZE;
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz = 0; gz < VERTEX_COUNT - 1.0f; gz++){
            for(int gx = 0; gx < VERTEX_COUNT - 1.0f; gx++){
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
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
