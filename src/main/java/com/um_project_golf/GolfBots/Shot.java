package com.um_project_golf.GolfBots;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/**
 * Class responsible for access of the velocities of the shots in the AIBot class.
 */

public record Shot(Vector3f velocity) {

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "VelocityX: " + velocity.x + ", VelocityZ: " + velocity.z;
    }
}
