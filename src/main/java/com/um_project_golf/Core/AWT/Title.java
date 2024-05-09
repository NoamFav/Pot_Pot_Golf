package com.um_project_golf.Core.AWT;

import org.lwjgl.nanovg.NVGPaint;

import static org.lwjgl.nanovg.NanoVG.*;

public class Title {

    private final String filePath;
    private final float x, y;
    private final float width, height;
    private final long vg;
    private int imgId = -1;  // Store the image ID

    public Title(String filePath, float x, float y, float width, float height, long vg) {
        this.filePath = filePath;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.vg = vg;
        initImage();  // Load the image once when the Title object is created
    }

    private void initImage() {
        imgId = nvgCreateImage(vg, filePath, 0);
        if (imgId == 0) {
            System.err.println("Failed to load image: " + filePath);
        }
    }

    public void render() {
        if (imgId <= 0) {
            return;  // Do not render if image failed to load
        }

        nvgBeginPath(vg);
        nvgRect(vg, x, y, width, height);

        NVGPaint imgPaint = nvgImagePattern(vg, x, y, width, height, 0, imgId, 1, NVGPaint.create());
        nvgFillPaint(vg, imgPaint);
        nvgFill(vg);
    }

    public void cleanup() {
        if (imgId > 0) {
            nvgDeleteImage(vg, imgId);
            imgId = -1;
        }
    }
}
