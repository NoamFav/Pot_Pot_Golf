package com.pot_pot_golf.Core.AWT;

import static org.lwjgl.nanovg.NanoVG.*;

import org.lwjgl.nanovg.NVGPaint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

/** The Title class is used to render a title image on the screen */
public class Title {

    private final InputStream imageStream; // InputStream for the image
    private final float x, y;
    private final float width, height;
    private final long vg;
    private int imgId = -1; // Store the image ID

    /**
     * Create a new Title object
     *
     * @param imageStream The InputStream of the image to use for the title
     * @param x The x position of the title
     * @param y The y position of the title
     * @param width The width of the title
     * @param height The height of the title
     * @param vg The NanoVG context
     */
    public Title(InputStream imageStream, float x, float y, float width, float height, long vg) {
        this.imageStream = imageStream;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.vg = vg;
        initImage(); // Load the image once when the Title object is created
    }

    /**
     * Initialize the image for the title, This method is called once when the Title object is
     * created
     */
    private void initImage() {
        try {
            // Convert InputStream to ByteBuffer
            ByteBuffer imageBuffer = readStreamToByteBuffer(imageStream);

            // Load the image into NanoVG
            imgId = nvgCreateImageMem(vg, 0, imageBuffer);
            if (imgId == 0) {
                System.err.println("Failed to load image from InputStream.");
            }
        } catch (IOException e) {
            System.err.println("Failed to load image: " + e.getMessage());
        }
    }

    /** Render the Title */
    public void render() {
        if (imgId <= 0) {
            return; // Do not render if image failed to load
        }

        nvgBeginPath(vg);
        nvgRect(vg, x, y, width, height);

        NVGPaint imgPaint =
                nvgImagePattern(vg, x, y, width, height, 0, imgId, 1, NVGPaint.create());
        nvgFillPaint(vg, imgPaint);
        nvgFill(vg);
    }

    /**
     * Read an InputStream into a ByteBuffer
     *
     * @param stream The InputStream to read
     * @return A ByteBuffer containing the data from the InputStream
     * @throws IOException If an I/O error occurs
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

    /** Cleanup the Title object */
    public void cleanup() {
        if (imgId > 0) {
            nvgDeleteImage(vg, imgId);
            imgId = -1;
        }
    }
}
