package com.pot_pot_golf.Core.Utils;

import com.pot_pot_golf.Game.GameUtils.Consts;

import java.io.*;
import java.nio.file.*;
import java.util.Objects;

public class ResourceCloner {

    private static final String RESOURCE_DIR =
            System.getProperty("user.home") + "/.pot_pot_golf/resources/";

    /**
     * Initializes resources by copying them to the home directory if not already present. Updates
     * paths in the Consts class to point to the copied resources.
     */
    public static void initializeResources() {
        try {
            // Get the root path of resources inside the JAR
            Path jarResourceRoot =
                    Paths.get(
                            Objects.requireNonNull(ResourceCloner.class.getResource("/resources/"))
                                    .toURI());

            // Target resource directory
            Path targetResourceRoot = Paths.get(RESOURCE_DIR);

            // Copy resources recursively
            Files.walk(jarResourceRoot)
                    .forEach(
                            sourcePath -> {
                                Path destinationPath =
                                        targetResourceRoot.resolve(
                                                jarResourceRoot.relativize(sourcePath));

                                try {
                                    if (Files.isDirectory(sourcePath)) {
                                        Files.createDirectories(destinationPath);
                                    } else {
                                        Files.copy(
                                                sourcePath,
                                                destinationPath,
                                                StandardCopyOption.REPLACE_EXISTING);
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(
                                            "Failed to copy resource: " + sourcePath, e);
                                }
                            });

            // Update Consts paths
            Consts.updatePaths(RESOURCE_DIR);

        } catch (Exception e) {
            System.err.println("Failed to initialize resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
