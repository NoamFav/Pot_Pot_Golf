package com.um_project_golf.Core.Entity.Terrain;

import com.um_project_golf.Core.Entity.Material;
import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Texture;
import com.um_project_golf.Core.ObjectLoader;
import com.um_project_golf.Core.Utils.Consts;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;

/**
 * The class responsible for the terrain.
 */
public class Terrain {

    private final Vector3f position;
    private final Model model;
    private final TerrainTexture blendMap;
    private final BlendMapTerrain blendMapTerrain;

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

    public Terrain(Vector3f position, ObjectLoader loader, Material material, boolean isWater) {
        this.position = position;
        this.model = generateTerrain(loader, isWater);
        this.model.setMaterial(material);
        this.blendMap = null;
        this.blendMapTerrain = null;
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
        float[][] heightmap = new float[Consts.VERTEX_COUNT][Consts.VERTEX_COUNT];

        for(int i = 0; i < Consts.VERTEX_COUNT; i++){
            for(int j = 0; j < Consts.VERTEX_COUNT; j++){

                float x = j / (Consts.VERTEX_COUNT - 1f) * Consts.SIZE_X;
                float z = i / (Consts.VERTEX_COUNT - 1f) * Consts.SIZE_Z;


                float height = (float) (SimplexNoise.octaveSimplexNoise(x * Consts.scales, z * Consts.scales, 0, Consts.octaves, Consts.persistence) * (Consts.MAX_HEIGHT/2) );
                heightmap[j][i] = height;

                vertices[vertexPointer * 3] = x;
                vertices[vertexPointer * 3 + 1] = isWater ? 0 : height;
                vertices[vertexPointer * 3 + 2] = z;

                calcNormals(j, heightmap, i, height, normals, vertexPointer);

                textureCoords[vertexPointer * 2] = j / (Consts.VERTEX_COUNT - 1f);
                textureCoords[vertexPointer * 2 + 1] = i / (Consts.VERTEX_COUNT - 1f);

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
        System.out.println(heightmap.length + " " + heightmap[0].length);
        SceneManager.setHeightMap(heightmap);

        return loader.loadModel(vertices, textureCoords, normals, indices);
    }

    private static void calcNormals(int j, float[][] heightmap, int i, float height, float[] normals, int vertexPointer) {
        float heightLeft = j > 0 ? heightmap[j - 1][i] : height;
        float heightRight = j < Consts.VERTEX_COUNT - 1 ? heightmap[j + 1][i] : height;
        float heightDown = i > 0 ? heightmap[j][i - 1] : height;
        float heightUp = i < Consts.VERTEX_COUNT - 1 ? heightmap[j][i + 1] : height;

        // Calculate the slopes in the x and z directions
        float slopeX = heightRight - heightLeft;
        float slopeZ = heightUp - heightDown;

        // Calculate the normal
        Vector3f normal = new Vector3f(slopeX, -2.0f, slopeZ);
        normal.normalize();

        normals[vertexPointer * 3] = normal.x;
        normals[vertexPointer * 3 + 1] = normal.y;
        normals[vertexPointer * 3 + 2] = normal.z;
    }

    public float getHeight(float x, float z) {
        double x1 = Math.sin(x / 10) * 10 + Math.cos(z / 10) * 10;
        return (float) (x1);

    }

    public void heightmaptoCSV(float[][] heightmap) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < heightmap.length; i++) {
            for (int j = 0; j < heightmap[i].length; j++) {
                sb.append(heightmap[i][j]);
                sb.append(",");
            }
            sb.append("\n");
        }
        File file = new File("heightmap.csv");
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
