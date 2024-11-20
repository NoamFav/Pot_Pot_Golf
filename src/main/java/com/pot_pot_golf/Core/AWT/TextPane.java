package com.pot_pot_golf.Core.AWT;

import static org.lwjgl.nanovg.NanoVG.*;

import com.pot_pot_golf.Core.WindowManager;
import com.pot_pot_golf.Game.Launcher;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

/** The TextPane class is used to render a text pane on the screen */
public class TextPane {

    private final float x, y; // TextPane position
    private final float width, height; // TextPane dimensions
    private String text; // TextPane text
    private final float fontSize; // Font size for the TextPane text
    private final long vg; // NanoVG context
    private int bgImage; // Background image handle
    private final InputStream fontStream; // InputStream for the font

    private final WindowManager window = Launcher.getWindow();

    /**
     * Create a new TextPane object
     *
     * @param x The x position of the TextPane
     * @param y The y position of the TextPane
     * @param width The width of the TextPane
     * @param height The height of the TextPane
     * @param text The text to display on the TextPane
     * @param fontSize The font size of the text
     * @param vg The NanoVG context
     * @param imagePath The path to the image to use for the TextPane background
     * @param fontStream The InputStream for the font
     */
    public TextPane(
            float x,
            float y,
            float width,
            float height,
            String text,
            float fontSize,
            long vg,
            InputStream imageStream,
            InputStream fontStream) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.fontSize = fontSize;
        this.vg = vg;
        this.fontStream = fontStream;

        // Load the background image from InputStream
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

    /** Render the TextPane */
    public void render() {
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

    /** Cleanup the TextPane */
    public void cleanup() {
        if (bgImage != 0) {
            nvgDeleteImage(vg, bgImage);
            bgImage = -1;
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
