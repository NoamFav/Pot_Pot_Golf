package com.pot_pot_golf.Core.Utils;

import com.pot_pot_golf.Game.GameUtils.Consts;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class ResourceCloner {

    private static final String RESOURCE_DIR =
            System.getProperty("user.home") + "/.pot_pot_golf/resources/";

    /**
     * Initializes resources by copying them to the home directory if not already present. Updates
     * paths in the Consts class to point to the copied resources.
     */
    public static void initializeResources() throws IOException {
        // List of resources to copy
        List<String> resources =
                List.of(
                        "Texture/Default.png",
                        "Texture/heightmap.png",
                        "SoundTrack/skippy-mr-sunshine-fernweh-goldfish-main-version-02-32-7172.wav",
                        "Models/tree/tree.obj",
                        "Models/Skybox/SkyBox.obj",
                        "Models/Ball/ImageToStl.com_ball.obj",
                        "Models/Arrow/Arrow5.obj",
                        "Models/flag/flag.obj",
                        "Models/Ball/Ball_texture/Golf_Ball.png",
                        "Models/Ball/Ball_texture/Golf_Ball2.png",
                        "Models/Ball/Ball_texture/BallBot.png",
                        "Models/Ball/Ball_texture/BallAIBot.png",
                        "Texture/cartoonSand.jpg",
                        "Texture/cartoonFlowers.jpg",
                        "Texture/cartoonGrass.jpg",
                        "Texture/cartoonWater.jpg",
                        "Texture/title.png",
                        "Texture/buttons.png",
                        "Texture/inGameMenu.png",
                        "fonts/MightySouly-lxggD.ttf");

        // Ensure resource directory exists
        File baseDir = new File(RESOURCE_DIR);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        // Copy resources if not already copied
        for (String resource : resources) {
            copyResourceIfNeeded(resource);
        }

        // Update Consts paths
        Consts.updatePaths(RESOURCE_DIR);
    }

    private static void copyResourceIfNeeded(String resourcePath) throws IOException {
        File targetFile = new File(RESOURCE_DIR + resourcePath);

        // Skip copying if the file already exists
        if (targetFile.exists()) {
            return;
        }

        // Create parent directories if necessary
        targetFile.getParentFile().mkdirs();

        // Copy the resource
        try (InputStream inputStream =
                        ResourceCloner.class.getResourceAsStream("/" + resourcePath);
                OutputStream outputStream = new FileOutputStream(targetFile)) {

            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found in JAR: " + resourcePath);
            }

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}
