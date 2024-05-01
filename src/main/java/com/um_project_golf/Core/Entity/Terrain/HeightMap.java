package com.um_project_golf.Core.Entity.Terrain;

import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Utils.Consts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class HeightMap {

    private static final Logger log = LogManager.getLogger(HeightMap.class);

    public void createHeightMap() {

        float[][] heightmap = new float[Consts.VERTEX_COUNT][Consts.VERTEX_COUNT];
        SimplexNoise.shufflePermutation();

        for(int i = 0; i < Consts.VERTEX_COUNT; i++){
            for(int j = 0; j < Consts.VERTEX_COUNT; j++){

                float x = j / (Consts.VERTEX_COUNT - 1f) * Consts.SIZE_X;
                float z = i / (Consts.VERTEX_COUNT - 1f) * Consts.SIZE_Z;

                float height = (float) (SimplexNoise.octaveSimplexNoise(x * Consts.SCALE, z * Consts.SCALE, 0, Consts.OCTAVES, Consts.PERSISTENCE, Consts.AMPLITUDE) * (Consts.MAX_HEIGHT/2));
                heightmap[j][i] = height;
            }
        }
        SceneManager.setHeightMap(heightmap);
        createHeightmapImage(heightmap);
    }

    public void createHeightmapImage(float[][] heightmap) {
        int width = heightmap.length;
        int height = heightmap[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float heightValue = heightmap[x][y];
                Color color;
                float red = 2.5f + (float) (Math.random() * 5);
                float green = 15 + (float) (Math.random() * 5);
                float blue = 20 + (float) (Math.random() * 5);

                if (heightValue < red) {
                    color = Color.RED;
                } else if (heightValue >= red && heightValue < green) {
                    color = Color.GREEN;
                } else if (heightValue >= green && heightValue < blue) {
                    color = Color.BLUE;
                } else {
                    color = Color.BLACK;
                }
                image.setRGB(x, y, color.getRGB());
            }
        }

        try {
            ImageIO.write(image, "PNG", new File("Texture/heightmap.png"));
        } catch (Exception e) {
            log.error("Error creating heightmap image: {}", e.getMessage());
        }
    }

    public float getHeight(Vector3f position) {
        int heightX = (int) ((position.x + Consts.SIZE_X/2) * ((Consts.VERTEX_COUNT-1) / Consts.SIZE_X));
        int heightZ = (int) ((position.z + Consts.SIZE_Z/2) * ((Consts.VERTEX_COUNT-1) / Consts.SIZE_Z));

        // Clamp values to ensure they fall within the heightmap's index range
        heightX = Math.max(0, Math.min(heightX, (Consts.VERTEX_COUNT-1)));
        heightZ = Math.max(0, Math.min(heightZ, (Consts.VERTEX_COUNT-1)));

        //Debug
//        System.out.println("Debug - World Pos: (" + position.x + ", " + position.z +
//                "), Scaled Indices: (" + heightX + ", " + heightZ +
//                "), Height: " + SceneManager.getHeightMap()[heightX][heightZ]);


        return SceneManager.getHeightMap()[heightX][heightZ];
    }
}
