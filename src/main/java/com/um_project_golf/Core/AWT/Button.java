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
    private final float x, y;  // Button position
    private final float width, height;  // Button dimensions
    private String text; // Button text
    private final Runnable action;  // Action to be performed when the button is clicked
    private final long vg;  // NanoVG context
    private final String imagePath;  // Image path for the button
    private final float fontSize;  // Font size for the button text
    private int imgId = -1;  // Store the image ID

    private double scaledMouseX, scaledMouseY;  // Scaled mouse position
    private boolean isPressed = false;


    private final WindowManager window = Launcher.getWindow();

    public Button(float x, float y, float width, float height, String text, float fontSize, Runnable action, long vg, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.fontSize = fontSize;
        this.action = action;
        this.vg = vg;
        this.imagePath = imagePath;
        initImage();  // Load the image once when the Button object is created
    }

    private void initImage() {
        imgId = nvgCreateImage(vg, imagePath, 0);
        if (imgId == 0) {
            System.err.println("Failed to load image: " + imagePath);
        }
    }

    public void render() {
        try (MemoryStack ignored = MemoryStack.stackPush()) {
            // Start new frame for NanoVG
            nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);

            // Load an image to use as texture
            NVGPaint imgPaint = nvgImagePattern(vg, x, y, width, height, 0, imgId, 1, NVGPaint.create());

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
                throw new RuntimeException("Could not add font");
            }

            nvgFontSize(vg, fontSize);
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
        updateMouseScaling();
        processButtonInteraction();
    }

    private void updateMouseScaling() {
        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        GLFW.glfwGetCursorPos(GLFW.glfwGetCurrentContext(), mouseX, mouseY);

        int[] framebufferWidth = new int[1];
        int[] framebufferHeight = new int[1];
        GLFW.glfwGetFramebufferSize(Launcher.getWindow().getWindow(), framebufferWidth, framebufferHeight);

        int[] windowWidth = new int[1], windowHeight = new int[1];
        GLFW.glfwGetWindowSize(Launcher.getWindow().getWindow(), windowWidth, windowHeight);

        double scaleX = framebufferWidth[0] / (double) windowWidth[0];
        // Scaling factors for mouse position
        double scaleY = framebufferHeight[0] / (double) windowHeight[0];

        scaledMouseX = mouseX[0] * scaleX;
        scaledMouseY = mouseY[0] * scaleY;
    }

    private void processButtonInteraction() {
        boolean mouseOver = isMouseOver(scaledMouseX, scaledMouseY);
        boolean mouseButtonDown = GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;

        if (mouseOver && mouseButtonDown) {
            if (!isPressed) {
                action.run();  // Run the action only on the transition to press
                isPressed = true; // Set isPressed to true after the action has run
            }
        } else {
            isPressed = false;  // Reset isPressed when not over the button or button not pressed
        }
    }

    private boolean isMouseOver(double scaledMouseX, double scaledMouseY) {
        return scaledMouseX >= x && scaledMouseX <= x + width && scaledMouseY >= y && scaledMouseY <= y + height;
    }

    public void cleanup() {
        if (imgId > 0) {
            nvgDeleteImage(vg, imgId);
            imgId = -1;
        }
    }

    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }
}