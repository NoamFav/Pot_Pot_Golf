package com.um_project_golf.Core.AWT;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Button {
    private float x, y;  // Button position
    private float width, height;  // Button dimensions
    private String text;
    private Runnable action;  // Action to be performed when the button is clicked

    public Button(float x, float y, float width, float height, String text, Runnable action) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.action = action;
    }

    public void render() {
        GL11.glColor3f(1.0f, 0.0f, 0.0f); // Red color for visibility
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + width, y);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x, y + height);
        GL11.glEnd();
    }

    public void update() {
        if (isMouseOver() && GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS) {
            action.run();
        }
    }

    private boolean isMouseOver() {
        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        GLFW.glfwGetCursorPos(GLFW.glfwGetCurrentContext(), mouseX, mouseY);

        return mouseX[0] >= x && mouseX[0] <= x + width && mouseY[0] >= y && mouseY[0] <= y + height;
    }
}
