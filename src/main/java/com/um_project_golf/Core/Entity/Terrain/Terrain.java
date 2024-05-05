package com.um_project_golf.Core.Entity.Terrain;

import com.um_project_golf.Core.Entity.Material;
import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Texture;
import com.um_project_golf.Core.ObjectLoader;
import com.um_project_golf.Core.Utils.Consts;
import org.joml.Vector3f;

/**
 * The class responsible for the terrain.
 */
public class Terrain {

    private final Vector3f position; // The position of the terrain
    private final Model model; // The model of the terrain
    private final TerrainTexture blendMap; // The blend map of the terrain
    private final BlendMapTerrain blendMapTerrain; // The blend map terrain of the terrain

    /**
     * The constructor of the terrain.
     * It initializes the position, loader and material of the terrain.
     *
     * @param position The position of the terrain.
     * @param loader The loader of the terrain.
     * @param material The material of the terrain.
     */
    public Terrain(Vector3f position, ObjectLoader loader, Material material, BlendMapTerrain blendMapTerrain, TerrainTexture blendMap,boolean isWater) {
        this.position = position;
        this.model = generateTerrain(loader, isWater);
        this.model.setMaterial(material);
        this.blendMap = blendMap;
        this.blendMapTerrain = blendMapTerrain;
    }

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
        this.blendMap = null; // no blend map
        this.blendMapTerrain = null; // no blend map terrain
    }


        /**
         * Generates the terrain.
         *
         * @param loader The loader of the terrain.
         * @return The model of the terrain.
         */
    private Model generateTerrain(ObjectLoader loader, boolean isWater) {
        int count = Consts.VERTEX_COUNT * Consts.VERTEX_COUNT; // The number of vertices
        float[] vertices = new float[count * 3]; // The vertices
        float[] normals = new float[count * 3]; // The normals
        float[] textureCoords = new float[count * 2]; // The texture coordinates
        int[] indices = new int[6 * (Consts.VERTEX_COUNT - 1) * (Consts.VERTEX_COUNT - 1)]; // The indices
        int vertexPointer = 0; // The vertex pointer
        float[][] heightmap = SceneManager.getHeightMap(); // The heightmap
        float scale = Consts.TEXTURE_SCALE; // The scale of the texture

        for(int i = 0; i < Consts.VERTEX_COUNT; i++){ // Loop through the heightmap array
            for(int j = 0; j < Consts.VERTEX_COUNT; j++){ // Loop through the heightmap array

                float x = j / (Consts.VERTEX_COUNT - 1f) * Consts.SIZE_X; // Calculate the x position of the vertex
                float z = i / (Consts.VERTEX_COUNT - 1f) * Consts.SIZE_Z; // Calculate the z position of the vertex
                float height = heightmap[j][i]; // Get the height of the vertex

                vertices[vertexPointer * 3] = x; // Set the x position of the vertex
                vertices[vertexPointer * 3 + 1] = isWater ? 0 : height; // Set the y position of the vertex
                vertices[vertexPointer * 3 + 2] = z; // Set the z position of the vertex

                calcNormals(j, heightmap, i, height, normals, vertexPointer); // Calculate the normals

                textureCoords[vertexPointer * 2] = j / (Consts.VERTEX_COUNT - 1f); // Set the x texture coordinate
                textureCoords[vertexPointer * 2 + 1] = i / (Consts.VERTEX_COUNT - 1f); // Set the y texture coordinate

                vertexPointer++; // Increment the vertex pointer
            }
        }
        int pointer = 0; // The pointer
        for(int gz = 0; gz < Consts.VERTEX_COUNT - 1.0f; gz++){ // Loop through the heightmap array
            for(int gx = 0; gx < Consts.VERTEX_COUNT - 1.0f; gx++){ // Loop through the heightmap array
                int topLeft = (gz * Consts.VERTEX_COUNT) + gx; // Calculate the top left vertex
                int topRight = topLeft + 1; // Calculate the top right vertex
                int bottomLeft = ((gz + 1) * Consts.VERTEX_COUNT) + gx; // Calculate the bottom left vertex
                int bottomRight = bottomLeft + 1; // Calculate the bottom right vertex
                indices[pointer++] = topLeft; // Set the top left index
                indices[pointer++] = bottomLeft; // Set the bottom left index
                indices[pointer++] = topRight; // Set the top right index
                indices[pointer++] = topRight; // Set the top right index
                indices[pointer++] = bottomLeft; // Set the bottom left index
                indices[pointer++] = bottomRight; // Set the bottom right index
            }
        }
        System.out.println(heightmap.length + " " + heightmap[0].length); // Print the size of the heightmap
        SceneManager.setHeightMap(heightmap); // Set the heightmap in the SceneManager

        return loader.loadModel(vertices, textureCoords, normals, indices); // Load the model
    }

    /**
     * Calculate the normals of the terrain.
     *
     * @param j The x position of the vertex.
     * @param heightmap The heightmap of the terrain.
     * @param i The z position of the vertex.
     * @param height The height of the vertex.
     * @param normals The normals of the terrain.
     * @param vertexPointer The vertex pointer.
     */
    private static void calcNormals(int j, float[][] heightmap, int i, float height, float[] normals, int vertexPointer) {
        float heightLeft = j > 0 ? heightmap[j - 1][i] : height; // Get the height of the left vertex
        float heightRight = j < Consts.VERTEX_COUNT - 1 ? heightmap[j + 1][i] : height; // Get the height of the right vertex
        float heightDown = i > 0 ? heightmap[j][i - 1] : height; // Get the height of the down vertex
        float heightUp = i < Consts.VERTEX_COUNT - 1 ? heightmap[j][i + 1] : height; // Get the height of the up vertex

        // Calculate the slopes in the x and z directions
        float slopeX = heightRight - heightLeft; // Calculate the slope in the x direction
        float slopeZ = heightUp - heightDown; // Calculate the slope in the z direction

        // Calculate the normal
        Vector3f normal = new Vector3f(slopeX, -2.0f, slopeZ); // Calculate the normal
        normal.normalize(); // Normalize the normal

        normals[vertexPointer * 3] = normal.x; // Set the x normal
        normals[vertexPointer * 3 + 1] = normal.y; // Set the y normal
        normals[vertexPointer * 3 + 2] = normal.z; // Set the z normal
    }

    /**
     * Get the height of the terrain at a given position.
     * Only used for evaluation.
     *
     * @param x The x position of the terrain.
     * @param z The z position of the terrain.
     * @return The height of the terrain at the given position.
     */
    public float getHeight(float x, float z) {
        double x1 = Math.sin(x / 10) * 10 + Math.cos(z / 10) * 10; // Calculate the height of the terrain
        return (float) (x1); // Return the height of the terrain
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

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    public BlendMapTerrain getBlendMapTerrain() {
        return blendMapTerrain;
    }
}
