package com.um_project_golf.Core.AWT;

import com.um_project_golf.Core.WindowManager;
import com.um_project_golf.Game.Launcher;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nanovg.NanoVG.*;

public class Button {
    private float x, y;  // Button position
    private float width, height;  // Button dimensions
    private String text; // Button text
    private Runnable action;  // Action to be performed when the button is clicked
    private long vg;  // NanoVG context

    private final WindowManager window = Launcher.getWindow();

    public void createButton(float x, float y, float width, float height, String text, Runnable action, long vg) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.action = action;
        this.vg = vg;
    }

    public void render() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Start new frame for NanoVG
            nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);

            // Load an image to use as texture
            int img = nvgCreateImage(vg, "icon.iconset/icon_512x512@2x.png", 0);
            NVGPaint imgPaint = nvgImagePattern(vg, x, y, width, height, 0, img, 1, NVGPaint.create());

            nvgBeginPath(vg);
            nvgRect(vg, x, y, width, height);

            // Use the image pattern to fill the rectangle
            nvgFillPaint(vg, imgPaint);
            nvgFill(vg);
            // Setting text color
            NVGColor textColor = NVGColor.create();
            textColor.r(1.0f); // White text
            textColor.g(1.0f);
            textColor.b(1.0f);
            textColor.a(1.0f);

            int fontId = nvgCreateFont(vg, "golf", "src/main/resources/fonts/golf.ttf");
            if (fontId == -1) {
                System.out.println("Font loading failed");
            }

            nvgFontSize(vg, 80);
            nvgFontFace(vg, "golf");
            nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
            nvgFillColor(vg, textColor);
            nvgText(vg, x + width / 2, y + height / 2, text);

            // End the frame for NanoVG
            nvgEndFrame(vg);

            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

            // Disable the scissor test in case NanoVG enabled it
            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            // Restore OpenGL states that NanoVG may have changed
            GL11.glEnable(GL11.GL_DEPTH_TEST); // Re-enable depth testing
            GL11.glEnable(GL11.GL_STENCIL_TEST); // Re-enable stencil testing if you use it
            GL11.glCullFace(GL11.GL_BACK); // Restore face culling
        }
    }


    public void update() {
        // Check if the mouse is over the button and the left mouse button is pressed
        if (isMouseOver() && GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS) {
            action.run(); // Run the action
        }
    }

    private boolean isMouseOver() {
        double[] mouseX = new double[1]; // Mouse x position
        double[] mouseY = new double[1]; // Mouse y position
        GLFW.glfwGetCursorPos(GLFW.glfwGetCurrentContext(), mouseX, mouseY); // Get the mouse position

        return mouseX[0] >= x && mouseX[0] <= x + width && mouseY[0] >= y && mouseY[0] <= y + height; // Check if the mouse is over the button
    }
}