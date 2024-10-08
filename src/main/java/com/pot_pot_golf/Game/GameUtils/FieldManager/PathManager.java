package com.pot_pot_golf.Game.GameUtils.FieldManager;

import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;

/**
 * The path manager class.
 * This class is responsible for managing the path of the game.
 * Stores the path of the game.
 */
public class PathManager {

    // A* pathfinding variables
    private Vector3f startPoint;
    private Vector3f endPoint;
    private List<Vector2i> path;

    public Vector3f getStartPoint() {return startPoint;}
    public void setStartPoint(Vector3f startPoint) {this.startPoint = startPoint;}
    public Vector3f getEndPoint() {return endPoint;}
    public void setEndPoint(Vector3f endPoint) {this.endPoint = endPoint;}
    public List<Vector2i> getPath() {return path;}
    public void setPath(List<Vector2i> path) {this.path = path;}
}
