package com.um_project_golf.Core.Utils;

import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * The utils class.
 * This class is responsible for providing utility methods.
 */
public class Utils {

    /**
     * Stores data in a float buffer.
     *
     * @param data The data to store.
     * @return The float buffer.
     */
    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length); // Allocate memory for the float buffer
        buffer.put(data).flip(); // Put the data into the buffer and flip it
        return buffer; // Return the buffer
    }

    /**
     * Stores data in an int buffer.
     *
     * @param data The data to store.
     * @return The int buffer.
     */
    public static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length); // Allocate memory for the int buffer
        buffer.put(data).flip(); // Put the data into the buffer and flip it
        return buffer; // Return the buffer
    }

    /**
     * Loads a resource.
     *
     * @param fileName The name of the file to load.
     * @return The resource loaded.
     */
    public static String loadResource(String fileName) {
        String result; // The result
        InputStream in = Utils.class.getResourceAsStream(fileName); // Get the input stream
        if (in == null) { // If the input stream is null
            throw new RuntimeException("Resource not found: " + fileName); // Throw a new runtime exception
        }
        try (Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)) { // Try with resources
            result = scanner.useDelimiter("\\A").next(); // Get the result
        }
        return result; // Return the result
    }
}
