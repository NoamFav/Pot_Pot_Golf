package com.um_project_golf.Core.GolfBots;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/**
 * Class responsible for access of the velocities of the shots in the AIBot class.
 * Record class that stores the velocity of the shot.
 * @param velocity the velocity of the shot
 */
public record Shot(Vector3f velocity) {

    /**
     * Method that returns the velocity of the shot.
     *
     * @return the velocity of the shot
     */
    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "VelocityX: " + velocity.x + ", VelocityZ: " + velocity.z;
    }
}
