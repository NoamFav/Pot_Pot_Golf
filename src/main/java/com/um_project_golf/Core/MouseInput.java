package com.um_project_golf.Core;

import com.um_project_golf.Game.Launcher;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

/**
 * The mouse input class.
 * This class is responsible for handling the mouse input of the game.
 */
public class MouseInput {
    private final Vector2d previousPos, currentPos;
    private final Vector2f displVec;

    private boolean inWindow = false;
    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;

    /**
     * The constructor of the mouse input.
     * It initializes the previous position, current position and displacement vector of the mouse.
     */
    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    /**
     * Initializes the mouse input.
     * It sets the cursor position, window focus, cursor enter and mouse button callbacks.
     */
    public void init() {
        GLFW.glfwSetCursorPosCallback(Launcher.getWindow().getWindow(), (window, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        });

        GLFW.glfwSetWindowFocusCallback(Launcher.getWindow().getWindow(), (window, focused) -> {
            if (focused) {
                double[] xPos = new double[1];
                double[] yPos = new double[1];
                GLFW.glfwGetCursorPos(window, xPos, yPos);
                int[] width = new int[1];
                int[] height = new int[1];
                GLFW.glfwGetWindowSize(window, width, height);
                inWindow = (xPos[0] >= 0 && xPos[0] <= width[0] && yPos[0] >= 0 && yPos[0] <= height[0]);
            } else {
                inWindow = false;
            }
        });

        GLFW.glfwSetCursorEnterCallback(Launcher.getWindow().getWindow(), (window, entered) -> inWindow = entered);

        GLFW.glfwSetMouseButtonCallback(Launcher.getWindow().getWindow(), (window, button, action, mode) -> {
            leftButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS;
            rightButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && action == GLFW.GLFW_PRESS;
        });
    }

    public void input() {
        displVec.set(0,0);
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double deltaX = currentPos.x - previousPos.x;
            double deltaY = currentPos.y - previousPos.y;
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;
            if (rotateX) {
                displVec.y = (float) deltaX;
            }
            if (rotateY) {
                displVec.x = (float) deltaY;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    /**
     * Gets the displacement vector of the mouse.
     *
     * @return The displacement vector of the mouse.
     */
    public Vector2f getDisplVec() {
        return displVec;
    }

    /**
     * Checks if the left button is pressed.
     *
     * @return True if the left button is pressed, false otherwise.
     */
    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    /**
     * Checks if the right button is pressed.
     *
     * @return True if the right button is pressed, false otherwise.
     */
    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }
}
