package com.um_project_golf.GolfBots;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record Shot(Vector3f velocity) {
    // Constructor

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "VelocityX: " + velocity.x + ", VelocityZ: " + velocity.z;
    }
}
