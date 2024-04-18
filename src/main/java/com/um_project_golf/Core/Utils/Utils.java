package com.um_project_golf.Core.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * The utils class.
 * This class is responsible for providing utility methods.
 */
public class Utils {

    private static final Logger log = LogManager.getLogger(Utils.class);

    /**
     * Stores data in a float buffer.
     *
     * @param data The data to store.
     * @return The float buffer.
     */
    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    /**
     * Stores data in an int buffer.
     *
     * @param data The data to store.
     * @return The int buffer.
     */
    public static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    /**
     * Loads a resource.
     *
     * @param fileName The name of the file to load.
     * @return The resource loaded.
     */
    public static String loadResource(String fileName) {
        String result;
        InputStream in = Utils.class.getResourceAsStream(fileName);
        if (in == null) {
            throw new RuntimeException("Resource not found: " + fileName);
        }
        try (Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    /**
     * Reads all lines from a file.
     *
     * @param fileName The name of the file to read.
     * @return The lines read.
     */
    public static List<String> readAllLines(String fileName) {
        List<String> list = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Class.forName(Utils.class.getName()).getResourceAsStream(fileName))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("An error occurred", e);
        }
        return list;
    }
}
