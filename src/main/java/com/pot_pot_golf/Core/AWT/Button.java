package com.pot_pot_golf.Core.AWT;

import com.pot_pot_golf.Core.WindowManager;
import com.pot_pot_golf.Game.GameUtils.Consts;
import com.pot_pot_golf.Game.Launcher;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * The Button class is used to render a button on the screen
 */
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

    /**
     * Create a new Button object
     *
     * @param x         The x position of the button
     * @param y         The y position of the button
     * @param width     The width of the button
     * @param height    The height of the button
     * @param text      The text to display on the button
     * @param fontSize  The font size of the text
     * @param action    The action to perform when the button is clicked
     * @param vg        The NanoVG context
     * @param imagePath The path to the image to use for the button
     */
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

    /**
     * initialize the image for the button
     * This method is called once when the Button object is created
     */
    private void initImage() {
        imgId = nvgCreateImage(vg, imagePath, 0);
        if (imgId == 0) {
            System.err.println("Failed to load image: " + imagePath);
        }
    }

    /**
     * Render the button
     */
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

            int fontId = nvgCreateFont(vg, "golf", Consts.GUI.FONT);
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

    /**
     * Update the button
     */
    public void update() {
        updateMouseScaling();
        processButtonInteraction();
    }

    /**
     * Update the mouse scaling
     */
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

    /**
     * Process the button interaction
     */
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

    /**
     * Check if the mouse is over the button
     *
     * @param scaledMouseX The scaled x position of the mouse
     * @param scaledMouseY The scaled y position of the mouse
     * @return True if the mouse is over the button, false otherwise
     */
    private boolean isMouseOver( double scaledMouseX, double scaledMouseY) {
        return scaledMouseX >= x && scaledMouseX <= x + width && scaledMouseY >= y && scaledMouseY <= y + height;
    }

    /**
     * Clean up the button
     */
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