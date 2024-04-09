package com.example.um_project_golf.Main;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Main {
    private long window;
    private float cameraY = 2.0f;
    private float cameraX = 0.0f;

    public void run() {
        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
    }

    private void init() {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        window = glfwCreateWindow(800, 600, "Red Cube", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();

        glEnable(GL_CULL_FACE);

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    case GLFW_KEY_UP:
                        cameraY += 0.1f;
                        break;
                    case GLFW_KEY_DOWN:
                        cameraY -= 0.1f;
                        break;
                    case GLFW_KEY_LEFT:
                        cameraX -= 0.1f;
                        break;
                    case GLFW_KEY_RIGHT:
                        cameraX += 0.1f;
                        break;
                }
            }
        });

        // Set up perspective projection
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glFrustum(-1.0, 1.0, -1.0, 1.0, 1.5, 20.0);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    private void loop() {
        float angle = 0.0f; // Initialize rotation angle

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glColor3f(1.0f, 0.0f, 0.0f);

            // Set up camera position and orientation
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glFrustum(-1.0, 1.0, -1.0, 1.0, 1.0, 100.0);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            // Manually set camera transformation
            float[] viewMatrix = calculateViewMatrix(cameraX, cameraY, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
            applyViewMatrix(viewMatrix);

            // Draw a 3D cube
            glPushMatrix();
            glTranslatef(0.0f, 0.0f, 2.0f); // Adjust cube position
            glRotatef(angle, 0.0f, 1.0f, 0.0f); // Rotate the cube

            glBegin(GL_QUADS);

            // Front face - Red
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(-1f, -1f, 1f);
            glVertex3f(1f, -1f, 1f);
            glVertex3f(1f, 1f, 1f);
            glVertex3f(-1f, 1f, 1f);

            // Back face - Green
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f(-1f, -1f, -1f);
            glVertex3f(-1f, 1f, -1f);
            glVertex3f(1f, 1f, -1f);
            glVertex3f(1f, -1f, -1f);

            // Left face - Blue
            glColor3f(0.0f, 0.0f, 1.0f);
            glVertex3f(-1f, -1f, -1f);
            glVertex3f(-1f, -1f, 1f);
            glVertex3f(-1f, 1f, 1f);
            glVertex3f(-1f, 1f, -1f);

            // Right face - Yellow
            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f(1f, -1f, 1f);
            glVertex3f(1f, -1f, -1f);
            glVertex3f(1f, 1f, -1f);
            glVertex3f(1f, 1f, 1f);

            // Top face - Cyan
            glColor3f(0.0f, 1.0f, 1.0f);
            glVertex3f(-1f, 1f, 1f);
            glVertex3f(1f, 1f, 1f);
            glVertex3f(1f, 1f, -1f);
            glVertex3f(-1f, 1f, -1f);

            // Bottom face - Magenta
            glColor3f(1.0f, 0.0f, 1.0f);
            glVertex3f(-1f, -1f, -1f);
            glVertex3f(1f, -1f, -1f);
            glVertex3f(1f, -1f, 1f);
            glVertex3f(-1f, -1f, 1f);

            glEnd();
            glPopMatrix();

            glfwSwapBuffers(window);

            glfwPollEvents();

            angle += 5f; // Increment the rotation angle
        }
    }

    private float[] calculateViewMatrix(float cameraX, float cameraY, float cameraZ,
                                        float lookAtX, float lookAtY, float lookAtZ,
                                        float upX, float upY, float upZ) {
        float[] forward = { lookAtX - cameraX, lookAtY - cameraY, lookAtZ - cameraZ };
        normalize(forward);

        float[] side = crossProduct(forward, new float[]{ upX, upY, upZ });
        normalize(side);

        float[] up = crossProduct(side, forward);

        float[] viewMatrix = new float[16];
        viewMatrix[0] = side[0];
        viewMatrix[4] = side[1];
        viewMatrix[8] = side[2];
        viewMatrix[12] = 0.0f;

        viewMatrix[1] = up[0];
        viewMatrix[5] = up[1];
        viewMatrix[9] = up[2];
        viewMatrix[13] = 0.0f;

        viewMatrix[2] = -forward[0];
        viewMatrix[6] = -forward[1];
        viewMatrix[10] = -forward[2];
        viewMatrix[14] = 0.0f;

        viewMatrix[3] = 0.0f;
        viewMatrix[7] = 0.0f;
        viewMatrix[11] = 0.0f;
        viewMatrix[15] = 1.0f;

        viewMatrix[12] = -dotProduct(side, new float[]{ cameraX, cameraY, cameraZ });
        viewMatrix[13] = -dotProduct(up, new float[]{ cameraX, cameraY, cameraZ });
        viewMatrix[14] = dotProduct(forward, new float[]{ cameraX, cameraY, cameraZ });

        return viewMatrix;
    }

    private void applyViewMatrix(float[] viewMatrix) {
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(viewMatrix.length);
        matrixBuffer.put(viewMatrix);
        matrixBuffer.flip();
        glMultMatrixf(matrixBuffer);
    }

    private void normalize(float[] vector) {
        float length = (float) Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
        vector[0] /= length;
        vector[1] /= length;
        vector[2] /= length;
    }

    private float[] crossProduct(float[] a, float[] b) {
        return new float[]{
                a[1] * b[2] - a[2] * b[1],
                a[2] * b[0] - a[0] * b[2],
                a[0] * b[1] - a[1] * b[0]
        };
    }

    private float dotProduct(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    public static void main(String[] args) {
        new Main().run();
    }
}