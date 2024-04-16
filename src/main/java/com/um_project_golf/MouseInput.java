package com.um_project_golf;

import com.um_project_golf.Game.Launcher;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class MouseInput {
    private final Vector2d previousPos, currentPos;
    private final Vector2f displVec;

    private boolean inWindow = false;
    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;

    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    public void init() {
        GLFW.glfwSetCursorPosCallback(Launcher.getWindow().getWindow(), (window, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        });

        GLFW.glfwSetCursorEnterCallback(Launcher.getWindow().getWindow(), (window, entered) -> {
            inWindow = entered;
        });

        GLFW.glfwSetMouseButtonCallback(Launcher.getWindow().getWindow(), (window, button, action, mode) -> {
            leftButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
            rightButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
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
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }
}
