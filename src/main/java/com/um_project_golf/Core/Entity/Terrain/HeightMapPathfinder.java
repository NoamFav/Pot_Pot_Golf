package com.um_project_golf.Core.Entity.Terrain;

import com.um_project_golf.Game.GameUtils.Consts;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * The class responsible for finding a path on the heightmap.
 */
public class HeightMapPathfinder {
    private int width;
    private int height;
    private int SCALE = (int) (Consts.VERTEX_COUNT / Consts.SIZE_X); // Compute the scale based on the size of the terrain
    private int CIRCLE_RADIUS; // Radius of the circle around the start point
    @SuppressWarnings("FieldCanBeLocal") private int SEARCH_RADIUS = 20 * SCALE; // Radius of the search area around a point
    private int BORDER_OFFSET = (int) (Consts.SIZE_X/3 * SCALE); // Offset from the border of the heightmap
    private int PATH_CIRCLE_RADIUS; // Radius of the circle around the end point

    private double[][] costMap;

    /**
     * Generate a path on the heightmap from a random start point to a random end point
     *
     * @param radiusDown The minimum radius of the circle around the start point
     * @param radiusUp   The maximum radius of the circle around the start point
     * @param radiusEnd  The radius of the circle around the end point
     * @return The path as a list of points
     */
    public List<Vector2i> getPath(int radiusDown, int radiusUp, int radiusEnd) {
        SCALE = (int) (Consts.VERTEX_COUNT / Consts.SIZE_X);
        BORDER_OFFSET = (int) (Consts.SIZE_X/3 * SCALE);
        SEARCH_RADIUS = 20 * SCALE;
        int count = 0;
        CIRCLE_RADIUS = (radiusDown + (int) (Math.random() * (radiusUp - radiusDown))) * SCALE;
        PATH_CIRCLE_RADIUS = radiusEnd * SCALE;

        List<Vector2i> path = null;
        int attempts = 0;
        boolean pathFound = false;
        try {
            BufferedImage heightMapImage = ImageIO.read(new File(Consts.HEIGHTMAP));
            width = heightMapImage.getWidth();
            height = heightMapImage.getHeight();
            costMap = generateCostMap(heightMapImage);

            while (attempts < 1000 && !pathFound) {
                Vector2i startPoint = findValidStartPoint();
                Vector2i endPoint = findValidEndPointOnCircle(startPoint);

                if (endPoint != null) {
                    path = findPathAStart(startPoint, endPoint);
                    pathFound = !path.isEmpty();
                }

                attempts++;
            }

            if (pathFound) {
                System.out.println("Path found!" + " #" + count + " Start: " + path.get(0).x + ", " + path.get(0).y + " End: " + path.get(path.size() - 1).x + ", " + path.get(path.size() - 1).y);

                // Draw the path on the heightmap image
                drawPathOnImage(heightMapImage, path);
                // Save the modified image
                ImageIO.write(heightMapImage, "png", new File(Consts.HEIGHTMAP));
            } else {
                System.out.println("Failed to find a valid path after 1000 attempts.");
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return path;
    }

    /**
     * Generate a path on the heightmap from a given start point to a given end point
     * This method is used for debugging purposes when the start and end points are known
     *
     * @param start    The start point
     * @param end      The end point
     * @param radiusEnd The radius of the circle around the end point
     * @return The path as a list of points
     */
    public List<Vector2i> getPathDebug(@NotNull Vector2i start, @NotNull Vector2i end, int radiusEnd) {
        int count = 0;
        CIRCLE_RADIUS = (int) (Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(end.y - start.y, 2)) * SCALE);
        PATH_CIRCLE_RADIUS = radiusEnd * SCALE;

        List<Vector2i> path = null;
        int attempts = 0;
        boolean pathFound = false;
        try {
            BufferedImage heightMapImage = ImageIO.read(new File(Consts.HEIGHTMAP));
            width = heightMapImage.getWidth();
            height = heightMapImage.getHeight();
            costMap = generateCostMap(heightMapImage);

            while (attempts < 1000 && !pathFound) {
                path = findPathAStart(start, end);
                pathFound = !path.isEmpty();

                attempts++;
            }

            if (pathFound) {
                System.out.println("Path found!" + " #" + count + " Start: " + path.get(0).x + ", " + path.get(0).y + " End: " + path.get(path.size() - 1).x + ", " + path.get(path.size() - 1).y);

                // Draw the path on the heightmap image
                drawPathOnImage(heightMapImage, path);
                // Save the modified image
                ImageIO.write(heightMapImage, "png", new File(Consts.HEIGHTMAP));
            } else {
                System.out.println("Failed to find a valid path after 1000 attempts.");
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return path;
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
            drawCircleOnImage(image, point.x, point.y, PATH_CIRCLE_RADIUS, rgbBlue);
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
    private void drawCircleOnImage(@NotNull BufferedImage image, int centerX, int centerY, int radius, int rgb) {
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
            int x = BORDER_OFFSET + rand.nextInt(width - 2 * BORDER_OFFSET);
            int y = BORDER_OFFSET + rand.nextInt(height - 2 * BORDER_OFFSET);
            point = new Vector2i(x, y);
        } while (!isValidStartPoint(point));
        return point;
    }

    /**
     * Check if a point is a valid start point
     *
     * @param point The point to check
     * @return True if the point is a valid start point, false otherwise
     */
    @Contract(pure = true)
    private boolean isValidStartPoint(@NotNull Vector2i point) {
        return point.x >= BORDER_OFFSET && point.x < width - BORDER_OFFSET &&
                point.y >= BORDER_OFFSET && point.y < height - BORDER_OFFSET &&
                costMap[point.x][point.y] < Double.POSITIVE_INFINITY;
    }

    /**
     * Find a valid end point on the circle around the start point
     *
     * @param center The center of the circle
     * @return The end point
     */
    private @Nullable Vector2i findValidEndPointOnCircle(Vector2i center) {
        Random rand = new Random();
        for (int attempts = 0; attempts < 360; attempts++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            int x = center.x + (int) (CIRCLE_RADIUS * Math.cos(angle));
            int y = center.y + (int) (CIRCLE_RADIUS * Math.sin(angle));
            Vector2i point = new Vector2i(x, y);
            if (isValidEndPoint(point)) {
                return point;
            }
        }
        return null; // No valid endpoint found
    }

    /**
     * Check if a point is a valid end point
     *
     * @param point The point to check
     * @return True if the point is a valid end point, false otherwise
     */
    @Contract(pure = true)
    private boolean isValidEndPoint(@NotNull Vector2i point) {
        if (point.x < 0 || point.x >= width || point.y < 0 || point.y >= height) {
            return false;
        }
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
            for (int dy = -SEARCH_RADIUS; dy <= SEARCH_RADIUS; dy++) {
                int nx = point.x + dx;
                int ny = point.y + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (costMap[nx][ny] == Double.POSITIVE_INFINITY) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Find a path from the start point to the end point using A* algorithm
     *
     * @param start The start point
     * @param end   The end point
     * @return The path as a list of points
     */
    public List<Vector2i> findPathAStart(Vector2i start, Vector2i end) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        boolean[][] closedSet = new boolean[width][height];
        openSet.add(new Node(start, 0, heuristic(start, end), null));

        int attempts = 0;
        while (!openSet.isEmpty() && attempts < 500000) {
            Node current = openSet.poll();
            if (current.point.x == end.x && current.point.y == end.y) {
                return reconstructPath(current);
            }
            closedSet[current.point.x][current.point.y] = true;

            for (Vector2i neighbor : getNeighbors(current.point)) {
                if (closedSet[neighbor.x][neighbor.y]) {
                    continue;
                }
                double tentativeCost = current.cost + costMap[neighbor.x][neighbor.y];
                Node neighborNode = new Node(neighbor, tentativeCost, tentativeCost + heuristic(neighbor, end), current);
                openSet.add(neighborNode);
            }
            attempts++;
        }
        return new ArrayList<>(); // return an empty path if no path found
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

    /**
     * Node class for the A* algorithm
     * Nested class to keep the Node class private
     */
    static class Node implements Comparable<Node> {
        Vector2i point;
        double cost;
        double priority;
        Node parent;

        /**
         * Create a new Node object
         *
         * @param point    The point
         * @param cost     The cost
         * @param priority The priority
         * @param parent   The parent node
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
