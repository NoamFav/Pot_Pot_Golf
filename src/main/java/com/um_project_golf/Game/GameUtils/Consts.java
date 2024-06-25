package com.um_project_golf.Game.GameUtils;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.BiFunction;

/**
 * The consts class.
 * This class is responsible for the constants of the game.
 * All key settings are stored here.
 * Made to be easily accessible and modifiable.
 */
public class Consts {

    public static String Title = "UM Project Golf"; // Title of the game

    public static final float REFERENCE_WIDTH = 3840f; // Reference width for the game (don't change)
    public static final float REFERENCE_HEIGHT = 2160f; // Reference height for the game (don't change)

    /* If too much lag, reduce the power to 8 (256 meters, and 1024 vertices).
    Or reduce the number of vertices to either power + 1 or power + 0.
    (less detail but more performance).
    You can also reduce the number of trees to reduce the density of the trees.
    And reduce lag in the process. */
    public static final int POWER = 9; // Power of 2 for the terrain size
    public static final float SIZE_X = (float) Math.pow(2, POWER); // Size of the terrain in the x direction
    public static final float SIZE_Z = (float) Math.pow(2, POWER); // Size of the terrain in the z direction
    public static final int VERTEX_COUNT = (int) Math.pow(2, POWER + 2); // Number of vertices in the terrain (Higher = more detail but also more performance heavy)
    public static final int NUMBER_OF_TREES = (int) (SIZE_X / 2); // Density of the trees (higher = more trees) reduces for performance
    public static final float TREE_SIZE = 1.5f; // Size of the trees at the trunk
    public static final float MAX_HEIGHT = SIZE_X / 2f; // Maximum height of the terrain,
    // doesn't have to influence the actual height of the terrain just the maximum height reachable by the player

    public static final int MAX_TEXTURES = 7; // Maximum number of textures (don't change)

    // Configuration for the terrain generation
    // Change at your own risk (maybe unstable)
    // The recommended values are the default values.
    // Scale = 0.005, Octaves = 11, Persistence = 0.4, Amplitude = 1
    public static final double SCALE = 0.005; // Used for scaling the terrain with simplex noise
    public static final int OCTAVES = 11; // Number of octaves used in simplex noise (higher = more detail)
    public static final double PERSISTENCE = 0.4; // Used to determine how much each octave contributes to the overall shape (higher = more detail)
    public static final double AMPLITUDE = 1; // Used for increasing the height of the terrain (higher = more height)

    public static final long NANOSECOND = 1000000000L;  // 1 second in nanoseconds (don't change)
    public static final float FRAME_CAP = 5000f; // Maximum frame rate (reduce for capping the frame rate)
    public static final float FRAMERATE = 1.0f / FRAME_CAP; // maximum frame rate in seconds

    // Configuration for the lighting
    // Unused for now (not really useful in a golf game)
    public static final int MAX_POINT_LIGHTS = 5; // Maximum number of point lights
    public static final int MAX_SPOT_LIGHTS = 5; // Maximum number of spot-lights

    // Configuration for the camera
    // You can change the FOV to make the game more immersive
    // Or change the Z_NEAR and Z_FAR to make the game more performance heavy or light
    public static final float FOV = (float) Math.toRadians(60); // Field of view (Don't change)
    public static final float Z_NEAR = 0.1f; // Near plane (Used for clipping)
    public static final float Z_FAR = 10000.0f; // Far plane (Used for freeing up resources)
    public static final float SPECULAR_POWER = 10f; // Specular power (Higher = more shiny)

    /* Configuration for the player
    Recommended values is the default values.
    Player Height = 1.75 meters
    Mouse Sensitivity = 0.2
    Camera Movement Speed = 1.1 (normal speed for a human, but really slow for a game) */
    public static final float PLAYER_HEIGHT = 1.75f; // Height of the player (meters)
    public static final float MOUSE_SENSITIVITY = 0.2f; // Mouse sensitivity (Higher = more sensitive)
    public static final float CAMERA_MOVEMENT_SPEED = 3f;  // camera movement speed (Higher = faster)

    // Make the scene darker or brighter with the ambient light
    public static final Vector4f DEFAULT_COLOR = new Vector4f(1f, 1f, 1f, 1f); // Default color of the object (don't change)
    public static final Vector3f AMBIENT_LIGHT = new Vector3f(0.8f, 0.8f, 0.8f); // Ambient light color (higher = brighter)

    // For examination purposes:

    // The color of the terrain is defined by the height of the terrain.
    public static final BiFunction<Float, Float, Float> HEIGHT_FUNCTION = (x, z) -> (float) (0.5 * Math.sin((x + z) / 10) + 1); // Height function for the terrain

    /* When running the game with a function, make sure the height of it is mostly superior to the sand height.
    Otherwise, the sand will be too present on the terrain.
    And the A* algorithm will not be able to find a path to the hole.
    As it sees the sand as a wall.
    (You wouldn't want to play golf in the sand, would you?) */
    public static final float SAND_HEIGHT = 0.2f; // Height of the sand (meters)

    public static final float MAX_TERRAIN_HEIGHT = 10f; // Height of the terrain (meters)
    /* The max height is only a scaling factor for the simplex noise.
    It is not the actual height of the terrain has simplex doesn't have to be 1 at any point.
    The terrains use octaves simplex generations, so the height is a sum of multiple simplex noise functions.
    This means that the actual height will be lower than the max height.
    From test, the max height tends to be around +/- 0.8,
    so the actual height would be +/- 8 meters when left untouched.
    Furthermore, for reducing water bodies,
    simplifying the A* algorithm, the height is scaled to 10 meters.
    By this: height < - 2 ? height - 2 : height + 2;
    The height is scaled to +/- 10 meters.
    Consider that it may be in fact superior to 10 meters by a couple centimeters. */

    // Configuration for the ball physics
    // Gravity is set to the real-world value, but can be changed for a more arcade feel.
    // The mass of the ball is the standard mass of a golf ball.
    // If the ball is too slippery, increase its mass.
    public static final float GRAVITY = 9.81f; // Gravity constant (m/s^2)
    public static final float BALL_MASS = 0.0459f; // Mass of the ball (kg)

    // The Initial velocity of the ball is defined by the textfield in the GUI.
    // The Tee position is defined by pressing the left key,
    // The Hole position is defined by pressing the right key.
    // Only available in the debug mode and not in the release mode,
    // The path between the tee and the hole is calculated by the A* algorithm.
    // So be sure to have a valid path between the tee and the hole.
    // Otherwise, the ball may not be able to reach the hole.
    // (water bodies)
    // You don't have to worry about y, it is calculated by the terrain at render time.
    public static final Vector3f TEE_POSITION = new Vector3f(-3f, 0f, 0f); // Position of the ball
    public static final Vector3f HOLE_POSITION = new Vector3f(4f, 0f, 1f); // Position of the hole
    public static final boolean WANT_TREE = true; // Want trees on the terrain
    public static final boolean USE_PREDEFINED_POSITIONS = true; // Use predefined positions for the tee and the hole
    public static final float TARGET_RADIUS = 0.05f; // Radius of the hole (meters)
    // Harder to hit the ball with a smaller radius

    // Change the radius to make the game easier or harder.
    // Beware of not making the radius bigger than half of the size of the terrain.
    // Otherwise, the A* algorithm will not find a path to the hole.
    // Nor a valid end point most of the time.
    // Recommended values are the standard Par values.
    // Par 3: 115-230 meters (need size terrain of 512)
    // Par 4: 230-410 meters (need size terrain of 1024)
    // Par 5: 410-550 meters (need size terrain of 1024)
    // Par 6: 550-700 meters (need size terrain of 2048)
    public static final int RADIUS_DOWN = 5; // Minimum distance from the hole
    public static final int RADIUS_UP = 10; // Maximum distance from the hole
    public static final int SIZE_GREEN = 10; // Size of the green (meters)

    // (Recommend higher when using long distances)
    // For the connoisseurs
    // Here are the average speeds of the golf clubs for a more realistic experience:
    // Driver = 71 m/s to 76 m/s (257 km/h to 274 km/h)
    // 3 Wood = 64.8 m/s to 69.1 m/s (233 km/h to 249 km/h)
    // 5 Iron = 58.1 m/s to 62.4 m/s (210 km/h to 225 km/h)
    // Pitching Wedge = 38 m/s to 45 m/s (137 km/h to 162 km/h)
    public static final float MAX_SPEED = 5f; // Maximum speed of the ball (m/s)

    // Bot configuration
    public static final float BOT_SENSITIVITY = 0.1f; // Sensitivity of the bots (higher = more sensitive)
    public static final int MAX_SHOTS = 20; // Maximum number of shots for the Rule-based bot

    // Error factor introduced in the bots to make them more human-like
    public static final float ERROR_DIRECTION_DEGREES = 5f; // Error in the direction of the bots (degrees)
    public static final float ERROR_MAGNITUDE_PERCENTAGE = 7f; // Error in the magnitude of the bots (percentage)
    public static final float ERROR_POSITION_RADIUS = 0.2f; // Error in the position of the bots (meters)
    public static final boolean WANT_ERROR = true; // Want error in the bots

    // The requirement of friction is tiny,
    // so on the simplex noise terrain, the ball will have trouble climbing the hills.
    // Use the recommended values for the best experience.
    // And the motions should be more realistic with the recommended values.
    // Furthermore, you can also modify the maximum height of the terrain to make the terrain flatter.
    // Inherently, giving the friction values below more impact.
    public static final float KINETIC_FRICTION_GRASS = 0.08f; // Friction of the grass (recommended: 0.2)
    public static final float STATIC_FRICTION_GRASS = 0.2f; // Friction of the grass (recommended: 0.4)

    public static final float KINETIC_FRICTION_SAND = 0.2f; // Friction of the sand (recommended: 0.4)
    public static final float STATIC_FRICTION_SAND = 0.4f; // Friction of the sand (recommended: 0.8)


    //Resources for the game
    //Do not change, except if you have full knowledge of the path you want to change
    public static final String DEFAULT_TEXTURE = "src/main/resources/Texture/Default.png";

    public static final String BACKGROUND_MUSIC = "src/main/resources/SoundTrack/skippy-mr-sunshine-fernweh-goldfish-main-version-02-32-7172.wav";

    /**
     * The class for the models of the game.
     * All the model paths are stored here.
     */
    public static class OBJ {
        public static final String MAIN_TREE = "src/main/resources/Models/tree/tree.obj";
        public static final String SECONDARY_TREE = "src/main/resources/Models/tree-2/tree4 - Copy.obj";
        public static final String THIRD_TREE = "src/main/resources/Models/tree-3/tree3-mybloodsweatandtears.obj";
        public static final String SKYBOX = "src/main/resources/Models/Skybox/SkyBox.obj";
        public static final String BALL = "src/main/resources/Models/Ball/ImageToStl.com_ball.obj";
        public static final String ARROW = "src/main/resources/Models/Arrow/Arrow5.obj";
        public static final String FLAG = "src/main/resources/Models/flag/flag.obj";
    }

    /**
     * The class for the textures of the game.
     * All the texture paths are stored here.
     */
    public static class BallTexture {
        public static final String BALL1 = "src/main/resources/Models/Ball/Ball_texture/Golf_Ball.png";
        public static final String BALL2 = "src/main/resources/Models/Ball/Ball_texture/Golf_Ball2.png";
        public static final String BALL_BOT = "src/main/resources/Models/Ball/Ball_texture/BallBot.png";
        public static final String BALL_AI_BOT = "src/main/resources/Models/Ball/Ball_texture/BallAIBot.png";
    }

    /**
     * The class for the textures of the game.
     * All the texture paths are stored here.
     */
    public static class TerrainTexture {
        public static final String SAND = "src/main/resources/Texture/cartoonSand.jpg";
        public static final String GRASS = "src/main/resources/Texture/cartoonFlowers.jpg";
        public static final String FAIRWAY = "src/main/resources/Texture/cartoonGrass.jpg";
        public static final String WATER = "src/main/resources/Texture/cartoonWater.jpg";
    }

    public static final String HEIGHTMAP = "src/main/resources/Texture/heightmap.png";

    /**
     * The class for the GUI
     * All the GUI paths are stored here.
     */
    public static class GUI {
        public static final String TITLE = "src/main/resources/Texture/title.png";
        public static final String BUTTON_MENU = "src/main/resources/Texture/buttons.png";
        public static final String BUTTON_IN_GAME_MENU = "src/main/resources/Texture/inGameMenu.png";
        public static final String FONT = "src/main/resources/fonts/MightySouly-lxggD.ttf";
    }
}