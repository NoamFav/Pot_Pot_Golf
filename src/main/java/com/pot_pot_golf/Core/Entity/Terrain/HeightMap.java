package com.pot_pot_golf.Core.Entity.Terrain;

import com.pot_pot_golf.Core.Entity.SceneManager;
import com.pot_pot_golf.Game.GameUtils.Consts;
import com.pot_pot_golf.Game.GolfGame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

/**
 * The HeightMap class is used to generate a heightmap using simplex noise and store it in a 2D
 * array. The heightmap is then used to calculate the height of the terrain at a given position.
 */
public class HeightMap {

    private static final Logger log = LogManager.getLogger(HeightMap.class); // Log errors

    /** Create a heightmap using simplex noise. */
    public void createHeightMap() {
        float[][] heightmap = new float[Consts.VERTEX_COUNT][Consts.VERTEX_COUNT];
        SimplexNoise.shufflePermutation(); // Shuffle the permutation array for randomness

        for (int i = 0; i < Consts.VERTEX_COUNT; i++) {
            for (int j = 0; j < Consts.VERTEX_COUNT; j++) {
                float x = j / (Consts.VERTEX_COUNT - 1f) * Consts.SIZE_X;
                float z = i / (Consts.VERTEX_COUNT - 1f) * Consts.SIZE_Z;

                float height =
                        (float)
                                (SimplexNoise.octaveSimplexNoise(
                                                x * Consts.SCALE,
                                                z * Consts.SCALE,
                                                0,
                                                Consts.OCTAVES,
                                                Consts.PERSISTENCE,
                                                Consts.AMPLITUDE)
                                        * Consts.MAX_TERRAIN_HEIGHT);
                heightmap[j][i] = height + 1.9f; // Offset height
            }
        }

        SceneManager.setHeightMap(heightmap);

        try (OutputStream outputStream = new FileOutputStream(Consts.HEIGHTMAP_PATH)) {
            createHeightmapImage(heightmap, outputStream);
        } catch (IOException e) {
            log.error("Error writing heightmap to file: {}", e.getMessage());
        }

        float minHeight = Float.MAX_VALUE;
        float maxHeight = Float.MIN_VALUE;

        for (float[] row : heightmap) {
            for (float h : row) {
                minHeight = Math.min(minHeight, h);
                maxHeight = Math.max(maxHeight, h);
            }
        }
        log.info("Min height: {}, Max height: {}", minHeight, maxHeight);
    }

    /**
     * Create an image of the heightmap.
     *
     * @param heightmap The heightmap to create an image of
     * @param outputStream The OutputStream to write the image to
     */
    public void createHeightmapImage(float[][] heightmap, OutputStream outputStream) {
        int width = heightmap.length;
        int height = heightmap[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                float heightValue = heightmap[x][z];
                Color color = determineHeightColor(heightValue);
                image.setRGB(x, z, color.getRGB());
            }
        }

        try {
            ImageIO.write(image, "PNG", outputStream);
        } catch (IOException e) {
            log.error("Error creating heightmap image: {}", e.getMessage());
        }
    }

    /**
     * Determine the color for a height value.
     *
     * @param heightValue The height value
     * @return The corresponding color
     */
    private Color determineHeightColor(float heightValue) {
        if (heightValue < Consts.SAND_HEIGHT) return Color.RED;
        else if (heightValue < 10) return Color.GREEN;
        else if (heightValue < 13) return Color.BLUE;
        else if (heightValue < 17) return new Color(63, 0, 0);
        else if (heightValue < 20) return new Color(0, 63, 0);
        else if (heightValue < 25) return new Color(0, 0, 63);
        else return Color.BLACK;
    }

    /**
     * Get the height of the terrain at a given position.
     *
     * @param position The position
     * @return The height at the position
     */
    public float getHeight(@NotNull Vector3f position) {
        int heightX =
                (int)
                        ((position.x + Consts.SIZE_X / 2)
                                * ((Consts.VERTEX_COUNT - 1) / Consts.SIZE_X));
        int heightZ =
                (int)
                        ((position.z + Consts.SIZE_Z / 2)
                                * ((Consts.VERTEX_COUNT - 1) / Consts.SIZE_Z));

        heightX = Math.max(0, Math.min(heightX, Consts.VERTEX_COUNT - 1));
        heightZ = Math.max(0, Math.min(heightZ, Consts.VERTEX_COUNT - 1));

        return GolfGame.debugMode
                ? Consts.HEIGHT_FUNCTION.apply(position.x, position.z)
                : SceneManager.getHeightMap()[heightX][heightZ];
    }

    /**
     * Save the heightmap to an output stream.
     *
     * @param outputStream The output stream
     */
    public void saveHeightMap(OutputStream outputStream) {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(outputStream))) {
            float[][] heightMap = SceneManager.getHeightMap();
            for (float[] row : heightMap) {
                for (float height : row) {
                    dos.writeFloat(height);
                }
            }
        } catch (IOException e) {
            log.error("Failed to save heightmap: {}", e.getMessage());
        }
    }

    /**
     * Load the heightmap from an input stream.
     *
     * @param inputStream The input stream
     */
    public void loadHeightMap(InputStream inputStream) {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream))) {
            float[][] heightMap = new float[Consts.VERTEX_COUNT][Consts.VERTEX_COUNT];
            for (int i = 0; i < Consts.VERTEX_COUNT; i++) {
                for (int j = 0; j < Consts.VERTEX_COUNT; j++) {
                    heightMap[i][j] = dis.readFloat();
                }
            }
            SceneManager.setHeightMap(heightMap);
        } catch (IOException e) {
            log.error("Failed to load heightmap: {}", e.getMessage());
        }
    }

    public float[][] getHeightMap() {
        return SceneManager.getHeightMap();
    }
}
