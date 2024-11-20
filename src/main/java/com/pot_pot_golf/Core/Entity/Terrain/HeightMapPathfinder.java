package com.pot_pot_golf.Core.Entity.Terrain;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import javax.imageio.ImageIO;

/** The class responsible for finding a path on the heightmap. */
public class HeightMapPathfinder {
    private static final int MAX_ATTEMPTS = 1000; // Max attempts to find a path
    private static final int A_STAR_LIMIT = 500000; // Max iterations for A* algorithm
    private static final int FULL_CIRCLE_DEGREES = 360; // Full circle for angle calculations

    private static final Color SAND_COLOR = Color.RED;
    private static final Color GRASS_COLOR = Color.GREEN;
    private static final Color WATER_COLOR = Color.BLUE;

    private int width;
    private int height;
    private final int scale;
    private int circleRadius;
    private int pathCircleRadius;
    private final int searchRadius;
    private final int borderOffset;

    private double[][] costMap;

    public HeightMapPathfinder() {
        this.scale = 10; // Adjust scale as necessary
        this.searchRadius = 20 * scale;
        this.borderOffset = 50 * scale;
    }

    /**
     * Generate a path on the heightmap from a random start point to a random end point
     *
     * @param radiusDown The minimum radius of the circle around the start point
     * @param radiusUp The maximum radius of the circle around the start point
     * @param radiusEnd The radius of the circle around the end point
     * @return The path as a list of points
     */
    public List<Vector2i> getPath(
            InputStream heightMapStream, int radiusDown, int radiusUp, int radiusEnd) {
        circleRadius = (radiusDown + (int) (Math.random() * (radiusUp - radiusDown))) * scale;
        pathCircleRadius = radiusEnd * scale;

        List<Vector2i> path = null;
        boolean pathFound = false;

        try {
            BufferedImage heightMapImage = loadHeightMap(heightMapStream);
            initializeCostMap(heightMapImage);

            for (int attempts = 0; attempts < MAX_ATTEMPTS && !pathFound; attempts++) {
                Vector2i startPoint = findValidStartPoint();
                Vector2i endPoint = findValidEndPointOnCircle(startPoint);

                if (endPoint != null) {
                    path = findPathAStart(startPoint, endPoint);
                    pathFound = !path.isEmpty();
                }
            }

            if (pathFound) {
                System.out.println(
                        "Path found from " + path.get(0) + " to " + path.get(path.size() - 1));
            } else {
                System.err.println(
                        "Failed to find a valid path after " + MAX_ATTEMPTS + " attempts.");
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return path != null ? path : new ArrayList<>();
    }

    /**
     * Load the heightmap from an InputStream.
     *
     * @param heightMapStream The InputStream for the heightmap.
     * @return A BufferedImage representation of the heightmap.
     * @throws IOException If an I/O error occurs while reading the stream.
     */
    private BufferedImage loadHeightMap(InputStream heightMapStream) throws IOException {
        return ImageIO.read(heightMapStream);
    }

    private void initializeCostMap(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.costMap = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color pixelColor = new Color(image.getRGB(x, y));
                if (pixelColor.equals(SAND_COLOR)) {
                    costMap[x][y] = Double.POSITIVE_INFINITY;
                } else if (pixelColor.equals(GRASS_COLOR)) {
                    costMap[x][y] = 1.0;
                } else if (pixelColor.equals(WATER_COLOR)) {
                    costMap[x][y] = 2.0;
                } else {
                    costMap[x][y] = 1.5; // Default cost
                }
            }
        }
    }

    /**
     * Generate a path on the heightmap from a given start point to a given end point This method is
     * used for debugging purposes when the start and end points are known
     *
     * @param start The start point
     * @param end The end point
     * @param radiusEnd The radius of the circle around the end point
     * @return The path as a list of points
     */
    public List<Vector2i> getPathDebug(
            @NotNull Vector2i start,
            @NotNull Vector2i end,
            int radiusEnd,
            InputStream heightMapStream) {
        int count = 0;
        circleRadius =
                (int)
                        (Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(end.y - start.y, 2))
                                * scale);
        pathCircleRadius = radiusEnd * scale;

        List<Vector2i> path = null;
        int attempts = 0;
        boolean pathFound = false;
        try {
            // Load the heightmap from InputStream
            BufferedImage heightMapImage = loadHeightMap(heightMapStream);
            width = heightMapImage.getWidth();
            height = heightMapImage.getHeight();
            costMap = generateCostMap(heightMapImage);

            while (attempts < 1000 && !pathFound) {
                path = findPathAStart(start, end);
                pathFound = !path.isEmpty();

                attempts++;
            }

            if (pathFound) {
                System.out.println(
                        "Path found!"
                                + " #"
                                + count
                                + " Start: "
                                + path.get(0).x
                                + ", "
                                + path.get(0).y
                                + " End: "
                                + path.get(path.size() - 1).x
                                + ", "
                                + path.get(path.size() - 1).y);

                // Draw the path on the heightmap image
                drawPathOnImage(heightMapImage, path);
                // Save the modified image (Optional: remove this if saving is unnecessary)
                // ImageIO.write(heightMapImage, "png", new File(Consts.HEIGHTMAP));
            } else {
                System.out.println("Failed to find a valid path after 1000 attempts.");
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return path != null ? path : new ArrayList<>();
    }
    /**
     * Draw the path on the heightmap image
     *
     * @param image The heightmap image
     * @param path The path to draw
     */

    private void drawPathOnImage(BufferedImage image, @NotNull List<Vector2i> path) {
        int rgbBlue = new java.awt.Color(0, 0, 255).getRGB(); // Color blue in RGB
        for (Vector2i point : path) {
            drawCircleOnImage(image, point.x, point.y, pathCircleRadius, rgbBlue);
        }
    }

    /**
     * Draw a circle on the heightmap image
     *
     * @param image The heightmap image
     * @param centerX The x coordinate of the center of the circle
     * @param centerY The y coordinate of the center of the circle
     * @param radius The radius of the circle
     * @param rgb The color of the circle in RGB
     */
    private void drawCircleOnImage(
            @NotNull BufferedImage image, int centerX, int centerY, int radius, int rgb) {
        int startX = Math.max(centerX - radius, 0);
        int endX = Math.min(centerX + radius, image.getWidth() - 1);
        int startY = Math.max(centerY - radius, 0);
        int endY = Math.min(centerY + radius, image.getHeight() - 1);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (image.getRGB(x, y) == Color.RED.getRGB()) {
                    continue; // Skip if the pixel is Red (sand)
                }
                int dx = x - centerX;
                int dy = y - centerY;
                if (dx * dx + dy * dy <= radius * radius) {
                    image.setRGB(x, y, rgb);
                }
            }
        }
    }

    /**
     * Generate a cost map from the heightmap image
     *
     * @param image The heightmap image
     * @return The cost map
     */
    private double[] @NotNull [] generateCostMap(BufferedImage image) {
        double[][] costMap = new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                java.awt.Color color = new java.awt.Color(rgb);
                if (color.equals(java.awt.Color.RED)) {
                    costMap[x][y] = Double.POSITIVE_INFINITY;
                } else if (color.equals(java.awt.Color.GREEN)) {
                    costMap[x][y] = 1.0;
                } else if (color.equals(java.awt.Color.BLUE)) {
                    costMap[x][y] = 2.0;
                } else {
                    costMap[x][y] = 1.0; // default cost for other colors
                }
            }
        }
        return costMap;
    }

    /**
     * Find a valid start point on the heightmap
     *
     * @return The start point
     */
    private Vector2i findValidStartPoint() {
        Random rand = new Random();
        Vector2i point;
        do {
            int x = borderOffset + rand.nextInt(width - 2 * borderOffset);
            int y = borderOffset + rand.nextInt(height - 2 * borderOffset);
            point = new Vector2i(x, y);
        } while (!isValidStartPoint(point));
        return point;
    }

    private boolean isValidStartPoint(@NotNull Vector2i point) {
        return point.x >= borderOffset
                && point.x < width - borderOffset
                && point.y >= borderOffset
                && point.y < height - borderOffset
                && costMap[point.x][point.y] < Double.POSITIVE_INFINITY;
    }

    /**
     * Find a valid end point on the circle around the start point
     *
     * @param center The center of the circle
     * @return The end point
     */
    private @Nullable Vector2i findValidEndPointOnCircle(Vector2i center) {
        Random rand = new Random();
        for (int i = 0; i < FULL_CIRCLE_DEGREES; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            int x = center.x + (int) (circleRadius * Math.cos(angle));
            int y = center.y + (int) (circleRadius * Math.sin(angle));

            Vector2i point = new Vector2i(x, y);
            if (isValidEndPoint(point)) {
                return point;
            }
        }
        return null;
    }

    /**
     * Check if a point is a valid end point
     *
     * @param point The point to check
     * @return True if the point is a valid end point, false otherwise
     */
    @Contract(pure = true)
    private boolean isValidEndPoint(@NotNull Vector2i point) {
        return point.x >= 0
                && point.x < width
                && point.y >= 0
                && point.y < height
                && costMap[point.x][point.y] < Double.POSITIVE_INFINITY;
    }

    public List<Vector2i> findPathAStart(Vector2i start, Vector2i end) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        boolean[][] closedSet = new boolean[width][height];
        openSet.add(new Node(start, 0, heuristic(start, end), null));

        for (int i = 0; i < A_STAR_LIMIT; i++) {
            if (openSet.isEmpty()) break;

            Node current = openSet.poll();
            if (current.point.equals(end)) {
                return reconstructPath(current);
            }

            closedSet[current.point.x][current.point.y] = true;
            for (Vector2i neighbor : getNeighbors(current.point)) {
                if (!closedSet[neighbor.x][neighbor.y]) {
                    double newCost = current.cost + costMap[neighbor.x][neighbor.y];
                    openSet.add(
                            new Node(
                                    neighbor,
                                    newCost,
                                    newCost + heuristic(neighbor, end),
                                    current));
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Calculate the heuristic cost between two points
     *
     * @param a The first point
     * @param b The second point
     * @return The heuristic cost
     */
    private double heuristic(@NotNull Vector2i a, @NotNull Vector2i b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    /**
     * Get the neighbors of a point
     *
     * @param point The point
     * @return The neighbors of the point
     */
    private @NotNull List<Vector2i> getNeighbors(@NotNull Vector2i point) {
        List<Vector2i> neighbors = new ArrayList<>();
        if (point.x > 0) neighbors.add(new Vector2i(point.x - 1, point.y));
        if (point.x < width - 1) neighbors.add(new Vector2i(point.x + 1, point.y));
        if (point.y > 0) neighbors.add(new Vector2i(point.x, point.y - 1));
        if (point.y < height - 1) neighbors.add(new Vector2i(point.x, point.y + 1));
        return neighbors;
    }

    /**
     * Reconstruct the path from the end node to the start node
     *
     * @param node The end node
     * @return The path as a list of points
     */
    private @NotNull List<Vector2i> reconstructPath(Node node) {
        List<Vector2i> path = new ArrayList<>();
        while (node != null) {
            path.add(node.point);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    /** Node class for the A* algorithm Nested class to keep the Node class private */
    static class Node implements Comparable<Node> {
        Vector2i point;
        double cost;
        double priority;
        Node parent;

        /**
         * Create a new Node object
         *
         * @param point The point
         * @param cost The cost
         * @param priority The priority
         * @param parent The parent node
         */
        Node(Vector2i point, double cost, double priority, Node parent) {
            this.point = point;
            this.cost = cost;
            this.priority = priority;
            this.parent = parent;
        }

        /**
         * Compare two nodes based on their priority
         *
         * @param other The other node
         * @return The result of the comparison
         */
        @Override
        public int compareTo(@NotNull Node other) {
            return Double.compare(this.priority, other.priority);
        }
    }
}
