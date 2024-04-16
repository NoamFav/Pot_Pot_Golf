package com.um_project_golf.Core;

import com.um_project_golf.Core.Utils.Consts;
import com.um_project_golf.Game.Launcher;
import com.um_project_golf.MouseInput;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class EngineManager {

    public static final long NANOSECOND = 1000000000L;
    public static final float FRAME_CAP = 5000f;

    private static int fps;
    private static final float framerate = 1.0f / FRAME_CAP;

    private boolean isRunning;

    private WindowManager window;
    private MouseInput mouseInput;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;

    private void init() throws Exception {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = Launcher.getWindow();
        gameLogic = Launcher.getGolfGame();
        mouseInput = new MouseInput();
        window.init();
        gameLogic.init();
        mouseInput.init();
    }

    public void start() throws Exception {
        init();
        if (isRunning) {
            return;
        }
        run();
    }

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

            unprocessedTime += passedTime / (double) NANOSECOND;
            frameCounter += passedTime;

            input();


            while (unprocessedTime > framerate) {
                render = true;
                unprocessedTime -= framerate;

                if (window.windowShouldClose()) {
                    stop();
                }

                if (frameCounter >= NANOSECOND) {
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

    public void stop() {
        if (!isRunning) {
            return;
        }
        isRunning = false;
    }

    public void input() {
        mouseInput.input();
        gameLogic.input();
    }

    public void render() {
        gameLogic.render();
        window.update();
    }

    public void update() {
        gameLogic.update();
    }

    public void cleanup() {
        window.cleanup();
        gameLogic.cleanUp();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }

}
