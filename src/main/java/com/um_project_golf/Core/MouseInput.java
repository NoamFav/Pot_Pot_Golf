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
    private final Vector2d previousPos, currentPos; // The previous and current position of the mouse.
    private final Vector2f displayVec; // The displacement vector of the mouse.

    private boolean inWindow = true; // Flag to check if the cursor is in the window.
    private boolean leftButtonPressed = false; // Flag to check if the left button is pressed.
    private boolean rightButtonPressed = false; // Flag to check if the right button is pressed.

    /**
     * The constructor of the mouse input.
     * It initializes the previous position, current position and displacement vector of the mouse.
     */
    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displayVec = new Vector2f();
    }

    /**
     * Initializes the mouse input.
     * It sets the cursor position, window focus, cursor enter, and mouse button callbacks.
     */
    public void init() {
        // Set the cursor position callback to update the current position of the mouse.
        GLFW.glfwSetCursorPosCallback(Launcher.getWindow().getWindow(), (window, xPos, yPos) -> {
            currentPos.x = xPos;
            currentPos.y = yPos;
        });

        // Set the window focus callback to update the inWindow flag.
        GLFW.glfwSetWindowFocusCallback(Launcher.getWindow().getWindow(), (window, focused) -> {
            if (focused) { // If the window is focused, check if the cursor is in the window.
                double[] xPos = new double[1]; // The x position of the cursor.
                double[] yPos = new double[1]; // The y position of the cursor.
                GLFW.glfwGetCursorPos(window, xPos, yPos); // Get the cursor position.
                int[] width = new int[1]; // The width of the window.
                int[] height = new int[1]; // The height of the window.
                GLFW.glfwGetWindowSize(window, width, height); // Get the window size.
                // Check if the cursor is in the window.
                inWindow = (xPos[0] >= 0 && xPos[0] <= width[0] && yPos[0] >= 0 && yPos[0] <= height[0]); // Set the inWindow flag.
            } else {
                inWindow = false; // If the window is not focused, set the inWindow flag to false.
            }
        });

        // Set the cursor enter callback to update the inWindow flag.
        GLFW.glfwSetCursorEnterCallback(Launcher.getWindow().getWindow(), (window, entered) -> inWindow = entered);

        // Set the mouse button callback to update the leftButtonPressed and rightButtonPressed flags.
        // Both flags are set to true when the left and right buttons are pressed, respectively.
        // Allows for handling of mouse button events for the input.
        GLFW.glfwSetMouseButtonCallback(Launcher.getWindow().getWindow(), (window, button, action, mode) -> {
            leftButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS; // Set the leftButtonPressed flag.
            rightButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && action == GLFW.GLFW_PRESS; // Set the rightButtonPressed flag.
        });
    }

    public void input() {
        displayVec.set(0,0); // Reset the displacement vector.
        // If the previous position is valid, and the cursor is in the window.
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double deltaX = currentPos.x - previousPos.x; // The change in x position.
            double deltaY = currentPos.y - previousPos.y; // The change in y position.
            boolean rotateX = deltaX != 0; // Check if the x position has changed.
            boolean rotateY = deltaY != 0; // Check if the y position has changed.
            if (rotateX) { // If the x position has changed, set the x displacement.
                displayVec.y = (float) deltaX; // Set the x displacement.
            }
            if (rotateY) { // If the y position has changed, set the y displacement.
                displayVec.x = (float) deltaY; // Set the y displacement.
            }
        }
        previousPos.x = currentPos.x; // Set the previous x position.
        previousPos.y = currentPos.y; // Set the previous y position.
    }

    /**
     * Gets the displacement vector of the mouse.
     *
     * @return The displacement vector of the mouse.
     */
    public Vector2f getDisplayVec() {
        return displayVec;
    }

    /**
     * Checks if the left button is pressed.
     *
     * @return True if the left button is pressed, false otherwise.
     */
    @SuppressWarnings("unused")
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
