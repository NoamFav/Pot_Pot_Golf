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
                        // Fonts
                        "fonts/MightySouly-lxggD.ttf",

                        // Icons
                        "icons/linux-icons.png",
                        "icons/mac-icons.icns",
                        "icons/win-icons.ico",

                        // Models
                        "Models/Arrow/Arrow5.mtl",
                        "Models/Arrow/Arrow5.obj",
                        "Models/Arrow/Arrow5Albedo.png",
                        "Models/Arrow/Arrow5AO.png",
                        "Models/Arrow/Arrow5Metal.png",
                        "Models/Arrow/Arrow5Normal.png",
                        "Models/Arrow/Arrow5Rough.png",
                        "Models/Arrow/Arrow5UVW.png",
                        "Models/Arrow/Map__55_Normal_Bump.tga",
                        "Models/Ball/Ball.blend",
                        "Models/Ball/Ball.blend1",
                        "Models/Ball/Ball_texture/ballAIBot.png",
                        "Models/Ball/Ball_texture/BallBot.png",
                        "Models/Ball/Ball_texture/Golf_Ball.png",
                        "Models/Ball/Ball_texture/Golf_Ball2.png",
                        "Models/Ball/ImageToStl.com_ball.obj",
                        "Models/flag/flag.mtl",
                        "Models/flag/flag.obj",
                        "Models/flag/flag.png",
                        "Models/flag/WOOD05L2.png",
                        "Models/flag/WOOD05LB.JPG",
                        "Models/flag/wood2.png",
                        "Models/Skybox/DayLight.mtl",
                        "Models/Skybox/DayLight.png",
                        "Models/Skybox/SkyBox.obj",
                        "Models/tree/tree.jpg",
                        "Models/tree/tree.mtl",
                        "Models/tree/tree.obj",
                        "Models/tree-2/tree4 - Copy.obj",
                        "Models/tree-2/tree4-Copy.mtl",
                        "Models/tree-2/tree4UVMap.png",
                        "Models/tree-3/tree3-mybloodsweatandtears.mtl",
                        "Models/tree-3/tree3-mybloodsweatandtears.obj",
                        "Models/tree-3/tree3colours(1).png",
                        "Models/windmill/LowPolyMill.mtl",
                        "Models/windmill/LowPolyMill.obj",
                        "Models/windmill/windmilluvcolour.png",

                        // Shaders
                        "shaders/entity_fragment.glsl",
                        "shaders/entity_vertex.glsl",
                        "shaders/terrain_fragment.glsl",
                        "shaders/terrain_vertex.glsl",

                        // SoundTrack
                        "SoundTrack/skippy-mr-sunshine-fernweh-goldfish-main-version-02-32-7172.wav",

                        // Texture
                        "Texture/buttons.png",
                        "Texture/cartoonFlowers.jpg",
                        "Texture/cartoonGrass.jpg",
                        "Texture/cartoonSand.jpg",
                        "Texture/cartoonWater.jpg",
                        "Texture/Default.png",
                        "Texture/heightmap.png",
                        "Texture/inGameMenu.png",
                        "Texture/title.png");

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
