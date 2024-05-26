package com.um_project_golf.Core.Entity.Terrain;

import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Utils.Consts;
import com.um_project_golf.Game.GolfGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * The HeightMap class is used to generate a heightmap using simplex noise and store it in a 2D array.
 * The heightmap is then used to calculate the height of the terrain at a given position.
 */
public class HeightMap {

    private static final Logger log = LogManager.getLogger(HeightMap.class); // Log errors

    /**
     * Create a heightmap using simplex noise
     */
    public void createHeightMap() {

        float[][] heightmap = new float[Consts.VERTEX_COUNT][Consts.VERTEX_COUNT]; // Create a 2D array to store the heightmap
        SimplexNoise.shufflePermutation(); // Shuffle the permutation array to create a random noise pattern

        for(int i = 0; i < Consts.VERTEX_COUNT; i++){ // Loop through the heightmap array
            for(int j = 0; j < Consts.VERTEX_COUNT; j++){ // Loop through the heightmap array

                float x = j / (Consts.VERTEX_COUNT - 1f) * Consts.SIZE_X; // Calculate the x position of the vertex
                float z = i / (Consts.VERTEX_COUNT - 1f) * Consts.SIZE_Z; // Calculate the z position of the vertex

                // Calculate the height of the vertex using simplex noise
                float height = (float) (SimplexNoise.octaveSimplexNoise(x * Consts.SCALE, z * Consts.SCALE, 0, Consts.OCTAVES, Consts.PERSISTENCE, Consts.AMPLITUDE) * Consts.MAX_TERRAIN_HEIGHT);
                heightmap[j][i] = height + 1.9f; // Set the height of the vertex in the heightmap array
            }
        }
        SceneManager.setHeightMap(heightmap); // Set the heightmap in the SceneManager
        createHeightmapImage(heightmap); // Create an image of the heightmap

        float minHeight = Float.MAX_VALUE;
        float maxHeight = Float.MIN_VALUE;

        for (int i = 0; i < Consts.VERTEX_COUNT; i++) {
            for (int j = 0; j < Consts.VERTEX_COUNT; j++) {
                if (heightmap[i][j] < minHeight) {
                    minHeight = heightmap[i][j];
                }
                if (heightmap[i][j] > maxHeight) {
                    maxHeight = heightmap[i][j];
                }
            }
        }
        System.out.println("Min height: " + minHeight + ", Max height: " + maxHeight);
    }

    /**
     * Create an image of the heightmap
     * @param heightmap The heightmap to create an image of
     */
    public void createHeightmapImage(float[] @NotNull [] heightmap) {
        int width = heightmap.length; // Get the width of the heightmap
        int height = heightmap[0].length; // Get the height of the heightmap
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // Create a new image



        for (int x = 0; x < width; x++) { // Loop through the heightmap
            for (int z = 0; z < height; z++) { // Loop through the heightmap
                float heightValue;

                float hx = x / (Consts.VERTEX_COUNT - 1f) * Consts.SIZE_X; // Calculate the x position of the vertex
                float hz = z / (Consts.VERTEX_COUNT - 1f) * Consts.SIZE_Z;

                if (GolfGame.debugMode) {
                    heightValue = Terrain.getHeight(hx, hz); // Calculate the height of the vertex
                } else {
                    heightValue = heightmap[x][z]; // Calculate the height of the vertex
                }

                // Get the height value of the vertex
                Color color; // Create a new color
                float sand = .5f;// (float) (Math.random() * 1.5f); // Generate a random red value
                float grass = 10; //(float) (Math.random() * 1.5f); // Generate a random green value
                float fairway = 11; //+ (float) (Math.random() * 2.5f); // Generate a random blue value
                float dryGrass = 17 + (float) (Math.random() * 2.5f); // Generate a random blue value
                float mold = 20 + (float) (Math.random() * 2.5f); // Generate a random blue value
                float rock = 25 + (float) (Math.random() * 2.5f); // Generate a random blue value
                if (heightValue < sand) { // If the height value is less than the sand value
                    color = Color.RED; // Set the color to red
                } else if (heightValue < grass) { // If the height value is less than the grass value
                    color = Color.GREEN; // Set the color to blue
                } else if (heightValue < fairway) { // If the height value is less than the fairway value
                    color = Color.BLUE; // Set the color to green
                } else if (heightValue < dryGrass) { // If the height value is less than the dry grass value
                    color = new Color(63, 0, 0); // Set the color to yellow
                } else if (heightValue < mold) { // If the height value is less than the rock value
                    color = new Color(0, 63, 0); // Set the color to cyan
                } else if (heightValue < rock){ // If the height value is greater than the rock value
                    color = new Color(0, 0, 63); // Set the color to magenta
                } else {
                    color = Color.BLACK; // Set the color to white
                }


                image.setRGB(x, z, color.getRGB()); // Set the color of the pixel in the image
            }
        }

        try { // Try to write the image to a file
            ImageIO.write(image, "PNG", new File("Texture/heightmap.png"));
        } catch (Exception e) { // Catch any exceptions
            log.error("Error creating heightmap image: {}", e.getMessage()); // Log the error
        }
    }

    /**
     * Get the height of the terrain at a given position
     * @param position The position to get the height of
     * @return The height of the terrain at the given position
     */
    public float getHeight(@NotNull Vector3f position) {
        int heightX = (int) ((position.x + Consts.SIZE_X/2) * ((Consts.VERTEX_COUNT-1) / Consts.SIZE_X)); // Calculate the x index of the heightmap
        int heightZ = (int) ((position.z + Consts.SIZE_Z/2) * ((Consts.VERTEX_COUNT-1) / Consts.SIZE_Z)); // Calculate the z index of the heightmap

        // Clamp values to ensure they fall within the heightmap's index range
        heightX = Math.max(0, Math.min(heightX, (Consts.VERTEX_COUNT-1))); // Clamp the x index
        heightZ = Math.max(0, Math.min(heightZ, (Consts.VERTEX_COUNT-1))); // Clamp the z index

        //Debug
//        System.out.println("Debug - World Pos: (" + position.x + ", " + position.z +
//                "), Scaled Indices: (" + heightX + ", " + heightZ +
//                "), Height: " + SceneManager.getHeightMap()[heightX][heightZ]);

        float height;
        if (GolfGame.debugMode) {
            height = Terrain.getHeight(position.x, position.z); // Calculate the height of the vertex
        } else {
            height = SceneManager.getHeightMap()[heightX][heightZ]; // Calculate the height of the vertex
        }

        return height; // Return the height of the vertex
    }
}
