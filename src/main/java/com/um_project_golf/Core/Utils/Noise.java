package com.um_project_golf.Core.Utils;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import java.util.Random;

/**
 * The class responsible for adding noise to the velocity and initial position of the ball.
 */
public class Noise {

    private final Random random = new Random();

    /**
     * Adds noise to a 3D velocity vector
     *
     * @param velocity the original velocity vector
     * @param directionNoiseDegrees the noise to add to the direction in degrees
     * @param magnitudeNoisePercentage the percentage of the magnitude to use as noise level
     * @return a new vector with noise applied
     */
    public @NotNull Vector3f addNoiseToVelocity(@NotNull Vector3f velocity, float directionNoiseDegrees, float magnitudeNoisePercentage) {
        // Convert the degrees to radians.
        float directionNoiseRadians = (float) Math.toRadians(directionNoiseDegrees);
        float magnitudeNoiseDecimal = magnitudeNoisePercentage / 100;

        // Calculate the magnitude and direction on the XZ plane.
        float magnitude = (float) Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        float angle = (float) Math.atan2(velocity.z, velocity.x);

        // Add Gaussian noise to the angle.
        float noisyAngle = angle + (float) random.nextGaussian() * directionNoiseRadians;

        // Add Gaussian noise to the magnitude.
        float magnitudeNoise = magnitude * magnitudeNoiseDecimal;
        float noisyMagnitude = magnitude + (float) random.nextGaussian() * magnitudeNoise;

        // Convert polar coordinates back to Cartesian.
        float noisyX = noisyMagnitude * (float) Math.cos(noisyAngle);
        float noisyZ = noisyMagnitude * (float) Math.sin(noisyAngle);

        return new Vector3f(noisyX, 0, noisyZ);
    }

    /**
     * Adds noise to a 3D position vector
     *
     * @param position the original position vector
     * @param noiseRadius the radius of the noise to add in meters
     * @return a new vector with noise applied
     */
    public @NotNull Vector3f addNoiseToInitialPosition(@NotNull Vector3f position, float noiseRadius) {
        float noisyX = position.x + (float) random.nextGaussian() * noiseRadius;
        float noisyZ = position.z + (float) random.nextGaussian() * noiseRadius;

        return new Vector3f(noisyX, position.y, noisyZ);
    }
}
