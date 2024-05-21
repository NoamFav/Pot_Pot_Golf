package com.um_project_golf.Core.Utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.BiFunction;
import javafx.util.Pair;

/**
 * The consts class.
 * This class is responsible for the constants of the game.
 * All key settings are stored here.
 * Made to be easily accessible and modifiable.
 */
public class Consts {

    public static final int MAX_TEXTURES = 7;
    public static String Title = "UM Project Golf";

    public static final float REFERENCE_WIDTH = 3840;
    public static final float REFERENCE_HEIGHT = 2160;

    public static final double SCALE = 0.005; // Used for scaling the terrain with simplex noise
    public static final int OCTAVES = 10; // Number of octaves used in simplex noise (higher = more detail)
    public static final double PERSISTENCE = 0.4; // Used to determine how much each octave contributes to the overall shape (higher = more detail)
    public static final double AMPLITUDE = 1; // Used for increasing the height of the terrain (higher = more height)

    public static final long NANOSECOND = 1000000000L;  // 1 second in nanoseconds
    public static final float FRAME_CAP = 5000f; // Maximum frame rate
    public static final float FRAMERATE = 1.0f / FRAME_CAP; // maximum frame rate in seconds

    public static final float JUMP_FORCE = 4; // Force applied when jumping (m/s)

    public static final int MAX_POINT_LIGHTS = 5; // Maximum number of point lights
    public static final int MAX_SPOT_LIGHTS = 5; // Maximum number of spot-lights

    public static final float FOV = (float) Math.toRadians(60); // Field of view (Don't change)
    public static final float Z_NEAR = 0.1f; // Near plane (Used for clipping)
    public static final float Z_FAR = 10000.0f; // Far plane (Used for freeing up resources)
    public static final float SPECULAR_POWER = 10f; // Specular power (Higher = more shiny)

    private static final int POWER = 10; // Power of 2 for the terrain size
    public static final float SIZE_X = (float) Math.pow(2, POWER); // Size of the terrain in the x direction
    public static final float SIZE_Z = (float) Math.pow(2, POWER); // Size of the terrain in the z direction
    public static final int VERTEX_COUNT = (int) Math.pow(2, POWER + 2); // Number of vertices in the terrain (Higher = more detail but also more performance heavy)
    public static final float MAX_HEIGHT = 10000; // Maximum height of the terrain
    public static final float TEXTURE_SCALE = 100f; // Scale of the texture on the terrain

    public static final float PLAYER_HEIGHT = 1.75f; // Height of the player (meters)
    public static final float MOUSE_SENSITIVITY = 0.2f; // Mouse sensitivity (Higher = more sensitive)
    public static final float CAMERA_MOVEMENT_SPEED = 4;  //1.1f for normal speed. Camera movement speed (Higher = faster)

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1f, 1f, 1f, 1f); // Default color of the object
    public static final Vector3f AMBIENT_LIGHT = new Vector3f(0.8f, 0.8f, 0.8f); // Ambient light color (higher = brighter)

    // For examination purposes:
    public static final BiFunction<Float, Float, Float> HEIGHT_FUNCTION = (x, z) -> (float) (Math.sin((x - z) / 7) + 0.5); // Height function for the terrain
    public static final float MAX_TERRAIN_HEIGHT = 10; // Height of the water
    public static final float GRAVITY = 9.81f; // Gravity constant (m/s^2)
    public static final float BALL_MASS = 0.0459f; // Mass of the ball (kg)

    public static final Pair<Float, Float> INITIAL_SPEED = new Pair<>(0f, 0f); // Position of the ball
    public static final Pair<Float, Float> TEE_POSITION = new Pair<>(0f, 0f); // Position of the hole
    public static final Pair<Float, Float> HOLE_POSITION = new Pair<>(0f, 0f); // Position of the hole
    public static final float TARGET_RADIUS = 0.15f; // Radius of the ball (meters)
    public static final float MAX_SPEED = 5f; // Maximum speed of the ball (m/s)

    public static final float KINETIC_FRICTION_GRASS = 0.1f;
    public static final float KINETIC_FRICTION_SAND = 0.2f;
    public static final float STATIC_FRICTION_GRASS = 0.2f;
    public static final float STATIC_FRICTION_SAND = 0.2f;
}