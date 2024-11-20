package com.pot_pot_golf.Core.AWT;

import static org.lwjgl.nanovg.NanoVG.*;

import com.pot_pot_golf.Core.WindowManager;
import com.pot_pot_golf.Game.Launcher;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

/** The TextField class is used to render a text field on the screen */
public class TextField {
    private final float x, y; // TextField position
    private final float width, height; // TextField dimensions
    private final String prompt; // TextField prompt
    private String text; // TextField text
    private final float fontSize; // Font size for the TextField text
    private final long vg; // NanoVG context
    private final int bgImage; // Background image handle
    private final InputStream fontStream; // InputStream for the font
    private boolean focused; // Whether the TextField is focused

    private double scaledMouseX, scaledMouseY; // Scaled mouse position
    private final WindowManager window = Launcher.getWindow(); // Window manager

    /**
     * Create a new TextField object
     *
     * @param x The x position of the TextField
     * @param y The y position of the TextField
     * @param width The width of the TextField
     * @param height The height of the TextField
     * @param prompt The prompt to display when the TextField is empty
     * @param fontSize The font size of the text
     * @param vg The NanoVG context
     * @param imagePath The path to the image to use for the TextField background
     * @param fontStream The InputStream for the font
     */
    public TextField(
            float x,
            float y,
            float width,
            float height,
            String prompt,
            float fontSize,
            long vg,
            InputStream imageStream,
            InputStream fontStream) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.prompt = prompt;
        this.fontSize = fontSize;
        this.vg = vg;
        this.text = ""; // Initialize text field as empty
        this.focused = false; // Initialize as not focused
        this.fontStream = fontStream;

        // Load the background image
        try {
            ByteBuffer imageBuffer = readStreamToByteBuffer(imageStream);
            this.bgImage = nvgCreateImageMem(vg, 0, imageBuffer);
            if (this.bgImage == 0) {
                throw new RuntimeException("Failed to load background image from InputStream.");
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error reading background image InputStream: " + e.getMessage(), e);
        }
    }

    /** Render the TextField */
    public void render() {
        try (MemoryStack ignored = MemoryStack.stackPush()) {
            // Start new frame for NanoVG
            nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);

            // Draw background image
            NVGPaint imgPaint =
                    nvgImagePattern(vg, x, y, width, height, 0, bgImage, 1.0f, NVGPaint.create());
            nvgBeginPath(vg);
            nvgRoundedRect(vg, x, y, width, height, 5);
            nvgFillPaint(vg, imgPaint);
            nvgFill(vg);

            // Draw text
            NVGColor textColor = NVGColor.create();
            textColor.r(1.0f); // White text
            textColor.g(1.0f);
            textColor.b(1.0f);
            textColor.a(1.0f);

            int fontId = loadFont(vg, "golf", fontStream);
            if (fontId == -1) {
                throw new RuntimeException("Could not add font.");
            }

            nvgFontSize(vg, fontSize);
            nvgFontFace(vg, "golf");
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

    /**
     * Load a font using NanoVG from an InputStream.
     *
     * @param vg The NanoVG context.
     * @param fontName The name of the font.
     * @param fontStream The InputStream for the font file.
     * @return The font ID or -1 if the font could not be loaded.
     */
    private int loadFont(long vg, String fontName, InputStream fontStream) {
        try {
            // Convert InputStream to ByteBuffer
            ByteBuffer fontBuffer = readStreamToByteBuffer(fontStream);

            // Use nvgCreateFontMem with correct arguments
            return nvgCreateFontMem(vg, fontName, fontBuffer, true); // true = NanoVG frees memory
        } catch (IOException e) {
            System.err.println("Failed to load font: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Read an InputStream into a ByteBuffer.
     *
     * @param stream The InputStream to read.
     * @return A ByteBuffer containing the data from the InputStream.
     * @throws IOException If an I/O error occurs.
     */
    private ByteBuffer readStreamToByteBuffer(InputStream stream) throws IOException {
        ByteBuffer buffer;
        try (stream) {
            buffer = ByteBuffer.allocate(stream.available());
            Channels.newChannel(stream).read(buffer);
            buffer.flip();
        }
        return buffer;
    }

    /** Update the TextField */
    public void update() {
        updateMouseScaling();
        processTextFieldInteraction();
    }

    /** Update the mouse scaling */
    private void updateMouseScaling() {
        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        GLFW.glfwGetCursorPos(GLFW.glfwGetCurrentContext(), mouseX, mouseY);

        int[] framebufferWidth = new int[1];
        int[] framebufferHeight = new int[1];
        GLFW.glfwGetFramebufferSize(
                Launcher.getWindow().getWindow(), framebufferWidth, framebufferHeight);

        int[] windowWidth = new int[1], windowHeight = new int[1];
        GLFW.glfwGetWindowSize(Launcher.getWindow().getWindow(), windowWidth, windowHeight);

        double scaleX = framebufferWidth[0] / (double) windowWidth[0];
        double scaleY = framebufferHeight[0] / (double) windowHeight[0];

        scaledMouseX = mouseX[0] * scaleX;
        scaledMouseY = mouseY[0] * scaleY;
    }

    /**
     * Process TextField interaction If the mouse is over the TextField and the left mouse button is
     * pressed, focus the TextField
     */
    private void processTextFieldInteraction() {
        boolean mouseOver = isMouseOver(scaledMouseX, scaledMouseY);
        boolean mouseButtonDown =
                GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MOUSE_BUTTON_1)
                        == GLFW.GLFW_PRESS;

        if (mouseOver && mouseButtonDown) {
            focused = true;
        } else if (!mouseOver && mouseButtonDown) {
            focused = false;
        }
    }

    /**
     * Check if the mouse is over the TextField
     *
     * @param scaledMouseX The scaled x position of the mouse
     * @param scaledMouseY The scaled y position of the mouse
     * @return True if the mouse is over the TextField, false otherwise
     */
    private boolean isMouseOver(double scaledMouseX, double scaledMouseY) {
        return scaledMouseX >= x
                && scaledMouseX <= x + width
                && scaledMouseY >= y
                && scaledMouseY <= y + height;
    }

    /**
     * Handle key input for the TextField
     *
     * @param key The key code
     * @param action The key action
     * @param mods The key mods
     */
    public void handleKeyInput(int key, int action, int mods) {
        if (focused) {
            if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
                if (key == GLFW.GLFW_KEY_BACKSPACE && !text.isEmpty()) {
                    text = text.substring(0, text.length() - 1);
                } else if (key == GLFW.GLFW_KEY_ENTER) {
                    focused = false; // Unfocused on Enter
                } else {
                    int codePoint = glfwKeyToChar(key, mods);
                    if (codePoint != -1) {
                        text += (char) codePoint;
                    }
                }
            }
        }
    }

    /**
     * Convert GLFW key code to character Handles printable characters To be improved for more key
     * codes and support for different keyboard layouts
     *
     * @param key The key code
     * @param mods The key mods
     * @return The character corresponding to the key code
     */
    private int glfwKeyToChar(int key, int mods) {
        // Handle printable characters
        if (key >= GLFW.GLFW_KEY_A && key <= GLFW.GLFW_KEY_Z) {
            String keyName = GLFW.glfwGetKeyName(key, 0);
            if (keyName != null) {
                char character = keyName.charAt(0);
                if ((mods & GLFW.GLFW_MOD_SHIFT) != 0) {
                    character = Character.toUpperCase(character);
                } else {
                    character = Character.toLowerCase(character);
                }
                return character;
            }
        } else if (key >= GLFW.GLFW_KEY_0 && key <= GLFW.GLFW_KEY_9) {
            return (char) ('0' + (key - GLFW.GLFW_KEY_0));
        } else if (key == GLFW.GLFW_KEY_MINUS) {
            return '-';
        } else if (key == GLFW.GLFW_KEY_PERIOD) {
            return '.';
        }
        return -1; // Non-printable character
    }

    /** Clean up the TextField */
    public void cleanup() {
        nvgDeleteImage(vg, bgImage);
    }

    public String getText() {
        return this.text;
    }

    public void handleMouseClick(float mouseX, float mouseY) {
        focused = isMouseOver(mouseX, mouseY);
    }
}
