package com.um_project_golf.Core;

import com.um_project_golf.Core.Utils.Consts;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

/**
 * The window manager class.
 * This class is responsible for creating and managing the window of the game.
 */
public class WindowManager {
    private final String title;

    private int width, height;
    private long window;

    private boolean resized, vSync;

    private final Matrix4f projectionMatrix;

    /**
     * The constructor of the window manager.
     * It initializes the title, width, height and vSync of the window.
     *
     * @param title The title of the window.
     * @param width The width of the window.
     * @param height The height of the window.
     * @param vSync The vSync of the window.
     */
    public WindowManager(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        this.resized = false;
        projectionMatrix = new Matrix4f();
    }

    /**
     * Initializes the window.
     * It creates the window and sets the window hints.
     */
    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

        boolean maximized = false;
        if (width == 0 || height == 0) {
            maximized = true;
            width = 1920;
            height = 1080;
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
        }

        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResized(true);
        });

        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                GLFW.glfwSetWindowShouldClose(window, true);
            }
        });

        if (maximized) {
            GLFW.glfwMaximizeWindow(window);
        } else {
            GLFWVidMode vid_mode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowPos(window, ((vid_mode != null ? vid_mode.width() : 0) - width) / 2, ((vid_mode != null ? vid_mode.height() : 0) - height) / 2);
        }
        
        GLFW.glfwMakeContextCurrent(window);

        if (isvSync()) {
            GLFW.glfwSwapInterval(1);
        }

        GLFW.glfwShowWindow(window);
        GLFW.glfwFocusWindow(window);

        GL.createCapabilities();


        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    /**
     * Updates the window.
     * It swaps the buffers and polls the events.
     */
    public void update() {
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    /**
     * Cleans up the window.
     * It destroys the window and terminates GLFW.
     */
    public void cleanup() {
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    /**
     * Sets the clear color of the window.
     *
     * @param r The red color.
     * @param g The green color.
     * @param b The blue color.
     * @param alpha The alpha color.
     */
    public void setClearColor(float r, float g, float b, float alpha) {
        GL11.glClearColor(r, g, b, alpha);
    }

    /**
     * Checks if a key is pressed.
     *
     * @param keyCode The key code of the key.
     * @return True if the key is pressed, false otherwise.
     */
    public boolean is_keyPressed(int keyCode) {
        return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
    }

    /**
     * Checks if the window should close.
     *
     * @return True if the window should close, false otherwise.
     */
    public boolean windowShouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    /**
     * Gets the title of the window.
     *
     * @return The title of the window.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the window.
     *
     * @param title The title of the window.
     */
    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(window, title);
    }

    /**
     * Sets the resized of the window.
     *
     * @param resized The resized of the window.
     */
    public void setResized(boolean resized) {
        this.resized = resized;
    }

    /**
     * Checks if the window is resized.
     *
     * @return True if the window is resized, false otherwise.
     */
    public boolean isResized() {
        return resized;
    }

    /**
     * Checks if the vSync is enabled.
     *
     * @return True if the vSync is enabled, false otherwise.
     */
    public boolean isvSync() {
        return vSync;
    }

    /**
     * Sets the vSync of the window.
     *
     * @param vSync The vSync of the window.
     */
    public void set_vSync(boolean vSync) {
        this.vSync = vSync;
    }

    /**
     * Gets the width of the window.
     *
     * @return The width of the window.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the window.
     *
     * @return The height of the window.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the window of the window manager.
     *
     * @return The window of the window manager.
     */
    public long getWindow() {
        return window;
    }

    /**
     * Gets the projection matrix of the window manager.
     *
     * @return The projection matrix of the window manager.
     */
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * Updates the projection matrix of the window manager.
     *
     * @return The updated projection matrix.
     */
    public Matrix4f updateProjectionMatrix() {
        float aspectRatio = (float) width / (float) height;
        return projectionMatrix.setPerspective(Consts.FOV, aspectRatio, Consts.Z_NEAR, Consts.Z_FAR);
    }

    /**
     * Updates the projection matrix of the window manager.
     *
     * @param matrix The matrix to update.
     * @return The updated projection matrix.
     */
    public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height) {
        float aspectRatio = (float) width / (float) height;
        return matrix.setPerspective(Consts.FOV, aspectRatio, Consts.Z_NEAR, Consts.Z_FAR);
    }
}
