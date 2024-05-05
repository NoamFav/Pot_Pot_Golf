package com.um_project_golf.Core.Entity.Terrain;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The TerrainGenerator class is used to generate a terrain using simplex noise and store it in a 2D array.
 * The terrain is then used to calculate the height of the terrain at a given position.
 * Independed from the rest of the project.
 * Used to generate a terrain image and display it in a window.
 * For testing purposes only.
 */
public class TerrainGenerator extends JPanel {
    private final int width = 512;
    private final int height = 512;
    private BufferedImage image;

    public TerrainGenerator() {
        super(true);
        this.setPreferredSize(new Dimension(width, height));
        generateTerrain();
    }

    private void generateTerrain() {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        double scale = 0.05; // Adjust scale to control terrain zoom
        int octaves = 4;
        double persistence = 0.5;

        double minVal = Double.MAX_VALUE;
        double maxVal = Double.MIN_VALUE;
        double[][] heightMap = new double[width][height];

        // Generate height map
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                double y = SimplexNoise.octaveSimplexNoise(x * scale, z * scale, 0, octaves, persistence, 1.0);
                heightMap[x][z] = y;
                if (y > maxVal) maxVal = y;
                if (y < minVal) minVal = y;
            }
        }

        // Normalize and draw the image
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                float value = (float)((heightMap[x][z] - minVal) / (maxVal - minVal));
                int rgb = Color.HSBtoRGB(0, 0, value); // grayscale value
                image.setRGB(x, z, rgb);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("Terrain Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new TerrainGenerator());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}