package com.um_project_golf.Core.AWT;

import com.um_project_golf.Core.WindowManager;
import com.um_project_golf.Game.Launcher;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.nanovg.NanoVG.*;

public class TextPane {

    private final float x, y;  // TextPane position
    private final float width, height;  // TextPane dimensions
    private String text;  // TextPane text
    private final float fontSize;  // Font size for the TextPane text
    private final long vg;  // NanoVG context
    private int bgImage;  // Background image handle

    private final WindowManager window = Launcher.getWindow();

    public TextPane(float x, float y, float width, float height, String text, float fontSize, long vg, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.fontSize = fontSize;
        this.vg = vg;

        // Load the background image
        this.bgImage = nvgCreateImage(vg, imagePath, 0);
        if (this.bgImage == 0) {
            throw new RuntimeException("Could not load image " + imagePath);
        }
    }

    public void render() {
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

        int fontId = nvgCreateFont(vg, "golf", "src/main/resources/fonts/golf.ttf");
        if (fontId == -1) {
            throw new RuntimeException("Could not add font");
        }

        nvgFontSize(vg, fontSize);
        nvgFontFace(vg, "golf");  // Make sure you have a font loaded
        nvgFillColor(vg, textColor);

        String displayText = text;

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
