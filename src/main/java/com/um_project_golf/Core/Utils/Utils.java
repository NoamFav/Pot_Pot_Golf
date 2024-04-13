package com.um_project_golf.Core.Utils;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class Utils {

    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }
}
