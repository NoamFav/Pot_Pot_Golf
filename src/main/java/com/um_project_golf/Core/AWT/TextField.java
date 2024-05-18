package com.um_project_golf.Core.AWT;

import com.um_project_golf.Core.WindowManager;
import com.um_project_golf.Game.Launcher;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nanovg.NanoVG.*;

public class TextField {
    private final float x, y;  // TextField position
    private final float width, height;  // TextField dimensions
    private final String prompt;  // TextField prompt
    private String text;  // TextField text
    private final float fontSize;  // Font size for the TextField text
    private final long vg;  // NanoVG context
    private final int bgImage;  // Background image handle
    private boolean focused;  // Whether the TextField is focused

    private double scaledMouseX, scaledMouseY;  // Scaled mouse position
    private final WindowManager window = Launcher.getWindow();

    public TextField(float x, float y, float width, float height, String prompt, float fontSize, long vg, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.prompt = prompt;
        this.fontSize = fontSize;
        this.vg = vg;
        this.text = "";  // Initialize text field as empty
        this.focused = false;  // Initialize as not focused

        // Load the background image
        this.bgImage = nvgCreateImage(vg, imagePath, 0);
        if (this.bgImage == 0) {
            throw new RuntimeException("Could not load image " + imagePath);
        }
    }

    public void render() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Start new frame for NanoVG
            nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);

            // Draw background image
            NVGPaint imgPaint = nvgImagePattern(vg, x, y, width, height, 0, bgImage, 1.0f, NVGPaint.create());
            nvgBeginPath(vg);
            nvgRoundedRect(vg, x, y, width, height, 5);
            nvgFillPaint(vg, imgPaint);
            nvgFill(vg);

            // Draw text
            NVGColor textColor = NVGColor.create();
            textColor.r(1.0f);  // White text
            textColor.g(1.0f);
            textColor.b(1.0f);
            textColor.a(1.0f);

            nvgFontSize(vg, fontSize);
            nvgFontFace(vg, "golf");  // Make sure you have a font loaded
            nvgFillColor(vg, textColor);

            String displayText = text.isEmpty() ? prompt : text;
            nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
            nvgText(vg, x + width / 2, y + height / 2, displayText);

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
        processTextFieldInteraction();
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
        double scaleY = framebufferHeight[0] / (double) windowHeight[0];

        scaledMouseX = mouseX[0] * scaleX;
        scaledMouseY = mouseY[0] * scaleY;
    }

    private void processTextFieldInteraction() {
        boolean mouseOver = isMouseOver(scaledMouseX, scaledMouseY);
        boolean mouseButtonDown = GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;

        if (mouseOver && mouseButtonDown) {
            focused = true;
        } else if (!mouseOver && mouseButtonDown) {
            focused = false;
        }
    }

    private boolean isMouseOver(double scaledMouseX, double scaledMouseY) {
        return scaledMouseX >= x && scaledMouseX <= x + width && scaledMouseY >= y && scaledMouseY <= y + height;
    }

    public void handleKeyInput(int key, int action, int mods) {
        if (focused) {
            if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
                if (key == GLFW.GLFW_KEY_BACKSPACE && !text.isEmpty()) {
                    text = text.substring(0, text.length() - 1);
                } else if (key == GLFW.GLFW_KEY_ENTER) {
                    focused = false;  // Unfocused on Enter
                } else {
                    int codePoint = glfwKeyToChar(key, mods);
                    if (codePoint != -1) {
                        text += (char) codePoint;
                    }
                }
            }
        }
    }

    private int glfwKeyToChar(int key, int mods) {
        // Handle printable characters
        if (key >= GLFW.GLFW_KEY_A && key <= GLFW.GLFW_KEY_Z) {
            String keyName = GLFW.glfwGetKeyName(key, 0);
            assert keyName != null;
            char character = keyName.charAt(0);
            if ((mods & GLFW.GLFW_MOD_SHIFT) != 0) {
                character = Character.toUpperCase(character);
            }
            text += character;
        }
        if (key >= GLFW.GLFW_KEY_0 && key <= GLFW.GLFW_KEY_9) {
            return (char) key;
        }
        if (key == GLFW.GLFW_KEY_SPACE) return ' ';

        // Handle special characters based on key and shift state
        if (key == GLFW.GLFW_KEY_SEMICOLON && (mods & GLFW.GLFW_MOD_SHIFT) != 0) return ':';
        if (key == GLFW.GLFW_KEY_SEMICOLON) return ';';
        if (key == GLFW.GLFW_KEY_EQUAL && (mods & GLFW.GLFW_MOD_SHIFT) != 0) return '+';
        if (key == GLFW.GLFW_KEY_EQUAL) return '=';
        if (key == GLFW.GLFW_KEY_COMMA) return ',';
        if (key == GLFW.GLFW_KEY_MINUS) return '-';
        if (key == GLFW.GLFW_KEY_PERIOD) return '.';
        if (key == GLFW.GLFW_KEY_SLASH) return '/';
        if (key == GLFW.GLFW_KEY_GRAVE_ACCENT) return '`';
        if (key == GLFW.GLFW_KEY_LEFT_BRACKET) return '[';
        if (key == GLFW.GLFW_KEY_BACKSLASH) return '\\';
        if (key == GLFW.GLFW_KEY_RIGHT_BRACKET) return ']';
        if (key == GLFW.GLFW_KEY_APOSTROPHE) return '\'';
        return -1; // Non-printable character
    }

    public void cleanup() {
        nvgDeleteImage(vg, bgImage);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void handleMouseClick(float mouseX, float mouseY) {
        focused = isMouseOver(mouseX, mouseY);
    }
}
