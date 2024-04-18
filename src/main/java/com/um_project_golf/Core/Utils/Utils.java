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

public class Utils {

    private static final Logger log = LogManager.getLogger(Utils.class);

    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

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
