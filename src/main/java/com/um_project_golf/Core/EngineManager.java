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

    private static int fps;


    private boolean isRunning;

    private WindowManager window;
    private MouseInput mouseInput;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;

    /**
     * Initializes the engine.
     *
     * @throws Exception If the engine fails to initialize.
     */
    private void init() throws Exception {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = Launcher.getWindow();
        gameLogic = Launcher.getGolfGame();
        mouseInput = new MouseInput();
        window.init();
        gameLogic.init();
        mouseInput.init();
    }

    /**
     * Starts the engine.
     *
     * @throws Exception If the engine fails to start.
     */
    public void start() throws Exception {
        init();
        if (isRunning) {
            return;
        }
        run();
    }

    /**
     * Runs the engine.
     */
    public void run() {
        this.isRunning = true;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while (isRunning) {
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) Consts.NANOSECOND;
            frameCounter += passedTime;

            input();


            while (unprocessedTime > Consts.framerate) {
                render = true;
                unprocessedTime -= Consts.framerate;

                if (window.windowShouldClose()) {
                    stop();
                }

                if (frameCounter >= Consts.NANOSECOND) {
                    setFps(frames);
                    window.setTitle(Consts.Title + " FPS: " + frames);
                    frames = 0;
                    frameCounter = 0;
                }
            }
            if (render) {
                update();
                render();
                frames++;
            }
        }
        cleanup();
    }

    /**
     * Stops the engine.
     */
    public void stop() {
        if (!isRunning) {
            return;
        }
        isRunning = false;
    }

    /**
     * Inputs the game.
     */
    public void input() {
        mouseInput.input();
        gameLogic.input();
    }

    /**
     * Renders the game.
     */
    public void render() {
        gameLogic.render();
        window.update();
    }

    /**
     * Updates the game.
     */
    public void update() {
        gameLogic.update(mouseInput);
    }

    /**
     * Cleans up the engine.
     */
    public void cleanup() {
        window.cleanup();
        gameLogic.cleanUp();
        errorCallback.free();
        GLFW.glfwTerminate();
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
