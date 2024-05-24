package com.um_project_golf.Core;

import com.um_project_golf.Core.Utils.Consts;
import com.um_project_golf.Game.Launcher;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

/**
 * The engine manager class.
 * This class is responsible for managing the engine of the game.
 */
public class EngineManager {

    private static int fps; // The frames per second

    private boolean isRunning; // Flag to check if the engine is running

    private WindowManager window;
    private MouseInput mouseInput;
    private GLFWErrorCallback errorCallback; // The error callback for GLFW
    private ILogic gameLogic;

    /**
     * Initializes the engine.
     *
     * @throws Exception If the engine fails to initialize.
     */
    private void init() throws Exception {
        // Set the error callback
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = Launcher.getWindow();
        gameLogic = Launcher.getGolfGame();
        mouseInput = new MouseInput();
        window.init();
        gameLogic.init(mouseInput);
        mouseInput.init();
    }

    /**
     * Starts the engine.
     *
     * @throws Exception If the engine fails to start.
     */
    public void start() throws Exception {
        init(); // Initialize the engine
        if (isRunning) {
            return;
        }
        run(); // Run the engine
    }

    /**
     * Runs the engine.
     */
    public void run() {
        // Initialize key variables
        this.isRunning = true;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while (isRunning) { // While the engine is running
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) Consts.NANOSECOND; // Convert to seconds
            frameCounter += passedTime; // Add the passed time to the frame counter

            input(); // Initialize the input section


            while (unprocessedTime > Consts.FRAMERATE) { // If the unprocessed time is greater than the frame rate (60)
                render = true; // Set render to true
                unprocessedTime -= Consts.FRAMERATE; // Subtract the frame rate from the unprocessed time

                if (window.windowShouldClose()) {
                    stop(); // Stop the engine if the window should close (exit)
                }

                if (frameCounter >= Consts.NANOSECOND) { // If the frame counter is greater than or equal to a second
                    setFps(frames); // Set the frames per second
                    window.setTitle(Consts.Title + " FPS: " + frames); // Set the title of the window
                    frames = 0; // Reset the frames
                    frameCounter = 0; // Reset the frame counter
                }
            }
            if (render) { // If render is true
                update(); // Update the game
                render(); // Render the game
                frames++; // Increment the frames
            }
        }
        cleanup(); // Clean up the engine to prevent memory leaks
    }

    /**
     * Stops the engine.
     */
    public void stop() {
        if (!isRunning) {
            return;
        }
        isRunning = false; // Set the engine to not running
    }

    /**
     * Inputs the game.
     */
    public void input() {
        mouseInput.input(); // Set the input for the mouse
        gameLogic.input(); // Set the input for the game
    }

    /**
     * Renders the game.
     */
    public void render() {
        gameLogic.render(); // Render the game
        window.update(); // Update the window
    }

    /**
     * Updates the game.
     */
    public void update() {
        gameLogic.update();
    }

    /**
     * Cleans up the engine.
     * This method is called when the engine is stopped to prevent memory leaks.
     */
    public void cleanup() {
        window.cleanup(); // Clean up the window
        gameLogic.cleanUp(); // Clean up the game
        errorCallback.free(); // Free the error callback
        GLFW.glfwTerminate(); // Terminate GLFW
    }

    /**
     * Gets the frames per second.
     *
     * @return The frames per second.
     */
    public static int getFps() {
        return fps;
    }

    /**
     * Sets the frames per second.
     *
     * @param fps The frames per second.
     */
    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }
}
