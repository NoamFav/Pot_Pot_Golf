package com.um_project_golf.Core.Utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * The consts class.
 * This class is responsible for the constants of the game.
 */
public class Consts {

    public static String Title = "UM Project Golf";

    public static final double SCALE = 0.005;
    public static final int OCTAVES = 10;
    public static final double PERSISTENCE = 0.4;
    public static final double AMPLITUDE = 1;

    public static final long NANOSECOND = 1000000000L;
    public static final float FRAME_CAP = 5000f;
    public static final float FRAMERATE = 1.0f / FRAME_CAP;
    public static final float GRAVITY = 9.81f;
    public static final float JUMP_FORCE = 4;

    public static final int MAX_POINT_LIGHTS = 5;
    public static final int MAX_SPOT_LIGHTS = 5;

    public static final float FOV = (float) Math.toRadians(60);
    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 10000.0f;
    public static final float SPECULAR_POWER = 10f;

    private static final int POWER = 9;
    public static final float SIZE_X = (float) Math.pow(2, POWER);
    public static final float SIZE_Z = (float) Math.pow(2, POWER);
    public static final int VERTEX_COUNT = (int) Math.pow(2, POWER + 2);
    public static final float MAX_HEIGHT = 100;
    public static final float TEXTURE_SCALE = 100f;

    public static final float PLAYER_HEIGHT = 2.0f;
    public static final float MOUSE_SENSITIVITY = 0.2f;
    public static final float CAMERA_MOVEMENT_SPEED = 4;  //1.1f for normal speed

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    public static final Vector3f AMBIENT_LIGHT = new Vector3f(0.8f, 0.8f, 0.8f);

}