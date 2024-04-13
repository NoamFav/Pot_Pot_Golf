package com.um_project_golf.Core;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class WindowManager {
    public static final float FOV = (float) Math.toRadians(60);
    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 1000.0f;

    private final String title;

    private int width, height;
    private long window;

    private boolean resized, vSync;

    private final Matrix4f projectionMatrix;

    public WindowManager(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        this.resized = false;
        projectionMatrix = new Matrix4f();
    }

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
            width = 1280;
            height = 920;
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

        GL.createCapabilities();

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public void update() {
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    public void cleanup() {
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public void setClearColor(float r, float g, float b, float alpha) {
        GL11.glClearColor(r, g, b, alpha);
    }
    public boolean is_keyPressed(int keyCode) {
        return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
    }
    public boolean windowShouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(window, title);
    }
    public void setResized(boolean resized) {
        this.resized = resized;
    }
    public boolean isResized() {
        return resized;
    }
    public boolean isvSync() {
        return vSync;
    }
    public void set_vSync(boolean vSync) {
        this.vSync = vSync;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public long getWindow() {
        return window;
    }
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f updateProjectionMatrix() {
        float aspectRatio = (float) width / (float) height;
        return projectionMatrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height) {
        float aspectRatio = (float) width / (float) height;
        return matrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }
}
