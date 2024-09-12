package com.pot_pot_golf.Core;

import com.pot_pot_golf.Game.GameUtils.Consts;
import org.jetbrains.annotations.NotNull;
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
    private final String title; // The title of the window.

    private int width, height; // The width and height of the window.
    private long window; // The window of the window manager.

    private boolean firstResize, resized, vSync, antiAliasing; // The resized, vSync and antiAliasing of the window.

    private final Matrix4f projectionMatrix; // The projection matrix of the window manager.

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
        GLFWErrorCallback.createPrint(System.err).set(); // Set the error callback.

        if (!GLFW.glfwInit()) { // If GLFW is unable to initialize
            throw new IllegalStateException("Unable to initialize GLFW"); // Throw an exception.
        }
        GLFW.glfwDefaultWindowHints(); // Set the default window hints.
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // Set the window hint to not visible.
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // Set the window hint to resizable.
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3); // Set the window hint to the major version of the context.
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2); // Set the window hint to the minor version of the context.
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE); // Set the window hint to the OpenGL profile.
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE); // Set the window hint to the OpenGL forward compatibility.

        boolean maximized = false; // Set the maximized to false.
        if (width == 0 || height == 0) { // If the width or height is 0
            maximized = true; // Set the maximized to true.
            // TODO update to get the screen resolution without crashing everything (it's not working)
            width = 3840; // Set the width to 1920.
            height = 2160; // Set the height to 1080.
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE); // Set the window hint to maximized.
        }

        if (antiAliasing) { // If the antiAliasing is enabled
            GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4); // Set the window hint to 4 samples.
        }

        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL); // Create the GLFW window.
        if (window == MemoryUtil.NULL) { // If the window is null
            throw new RuntimeException("Failed to create the GLFW window"); // Throw a runtime exception.
        }

        // Get the video mode of the primary monitor.


        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> { // Set the framebuffer size callback.
            this.width = width; // Set the width to the width.
            this.height = height; // Set the height to the height.
            this.setResized(true); // Set the resized to true.
        });

//        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> { // Set the key callback.
//            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) { // If the key is escape and the action is release
//                GLFW.glfwSetWindowShouldClose(window, true); // Set the window should close to true.
//            }
//        });

        if (maximized) { // If the window is maximized
            GLFW.glfwMaximizeWindow(window); // Maximize the window.
        } else {
            GLFWVidMode vid_mode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()); // Get the video mode.
            GLFW.glfwSetWindowPos(window, ((vid_mode != null ? vid_mode.width() : 0) - width) / 2, ((vid_mode != null ? vid_mode.height() : 0) - height) / 2); // Set the window position.
        }
        
        GLFW.glfwMakeContextCurrent(window); // Make the context current.

        if (isvSync()) { // If the vSync is enabled
            GLFW.glfwSwapInterval(1); // Swap the interval to 1.
        }


        GLFW.glfwShowWindow(window); // Show the window.
        GLFW.glfwFocusWindow(window); // Focus the window.

        GL.createCapabilities(); // Create the capabilities.

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Set the clear color.
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enable the depth test.
        GL11.glEnable(GL11.GL_STENCIL_TEST); // Enable the stencil test.
        GL11.glFrontFace(GL11.GL_CCW); // Set the front face to counter-clockwise.
        GL11.glEnable(GL11.GL_CULL_FACE); // Enable the cull face. (Back face culling)
        GL11.glCullFace(GL11.GL_BACK); // Set the cull face to back. (Back face culling)
    }

    /**
     * Updates the window.
     * It swaps the buffers and polls the events.
     */
    public void update() {
        GLFW.glfwSwapBuffers(window); // Swap the buffers. (Double buffering)
        GLFW.glfwPollEvents(); // Poll the events. (Keyboard, mouse, etc.)
    }

    /**
     * Cleans up the window.
     * It destroys the window and terminates GLFW.
     */
    public void cleanup() {
        GLFW.glfwDestroyWindow(window); // Destroy the window.
        GLFW.glfwTerminate(); // Terminate GLFW. (Free resources)
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
        GL11.glClearColor(r, g, b, alpha); // Set the clear color.
    }

    /**
     * Checks if a key is pressed.
     *
     * @param keyCode The key code of the key.
     * @return True if the key is pressed, false otherwise.
     */
    public boolean is_keyPressed(int keyCode) {
        return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS; // Get the key and check if it is pressed.
    }

    /**
     * Checks if the window should close.
     *
     * @return True if the window should close, false otherwise.
     */
    public boolean windowShouldClose() {
        return GLFW.glfwWindowShouldClose(window); // Check if the window should close.
    }

    /**
     * Gets the title of the window.
     *
     * @return The title of the window.
     */
    @SuppressWarnings("unused")
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
        if (firstResize) {
            firstResize = false;
            return false;
        }
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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    @SuppressWarnings("unused")
    public boolean isAntiAliasing() {
        return antiAliasing;
    }

    public void setAntiAliasing(boolean antiAliasing) {
        this.antiAliasing = antiAliasing;
    }

    /**
     * Updates the projection matrix of the window manager.
     *
     * @return The updated projection matrix.
     */
    public Matrix4f updateProjectionMatrix() {
        float aspectRatio = (float) width / (float) height; // Calculate the aspect ratio.
        return projectionMatrix.setPerspective(Consts.FOV, aspectRatio, Consts.Z_NEAR, Consts.Z_FAR); // Set the perspective.
    }

    /**
     * Updates the projection matrix of the window manager.
     *
     * @param matrix The matrix to update.
     * @return The updated projection matrix.
     */
    @SuppressWarnings("unused")
    public Matrix4f updateProjectionMatrix(@NotNull Matrix4f matrix, int width, int height) {
        float aspectRatio = (float) width / (float) height; // Calculate the aspect ratio.
        return matrix.setPerspective(Consts.FOV, aspectRatio, Consts.Z_NEAR, Consts.Z_FAR); // Set the perspective.
    }

    /**
     * Converts a value from the reference width to the current width.
     * Makes sure the value is scaled correctly.
     * To keep the aspect ratio of the game.
     *
     * @param value The value to convert.
     * @return The converted value.
     */
    public float getWidthConverted(float value) {
        float scaleFactor = width / Consts.REFERENCE_WIDTH;
        return value * scaleFactor;
    }

    /**
     * Converts a value from the reference height to the current height.
     * Makes sure the value is scaled correctly.
     * To keep the aspect ratio of the game.
     *
     * @param value The value to convert.
     * @return The converted value.
     */
    public float getHeightConverted(float value) {
        float scaleFactor = height / Consts.REFERENCE_HEIGHT;
        return value * scaleFactor;
    }

    /**
     * Converts a value to a uniform scale factor.
     * Makes sure the value is scaled correctly.
     * Used for scaling the font.
     *
     * @param value The value to convert.
     * @return The converted value.
     */
    public float getUniformScaleFactorFont(float value) {
        float widthScaleFactor = width / Consts.REFERENCE_WIDTH;
        float heightScaleFactor = height / Consts.REFERENCE_HEIGHT;
        float scaleFactor = Math.min(widthScaleFactor, heightScaleFactor);
        return value * scaleFactor;
    }
}
