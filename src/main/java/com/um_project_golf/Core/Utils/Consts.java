package com.um_project_golf.Core.Utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * The consts class.
 * This class is responsible for the constants of the game.
 */
public class Consts {

    public static String Title = "UM Project Golf";

    public static final int MAX_POINT_LIGHTS = 5;
    public static final int MAX_SPOT_LIGHTS = 5;

    public static final float FOV = (float) Math.toRadians(60);
    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 10000.0f;
    public static final float SPECULAR_POWER = 10f;

    public static final float SIZE_X = 2000;
    public static final float SIZE_Z = 2000;
    public static final int VERTEX_COUNT = 4096;
    public static final float MAX_HEIGHT = 10;

    public static final float MOUSE_SENSITIVITY = 0.2f;
    public static final float CAMERA_MOVEMENT_SPEED = 1f;

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    public static final Vector3f AMBIENT_LIGHT = new Vector3f(0.5f, 0.5f, 0.5f);

}