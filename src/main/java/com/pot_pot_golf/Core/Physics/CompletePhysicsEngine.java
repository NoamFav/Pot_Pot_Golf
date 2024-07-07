package com.pot_pot_golf.Core.Physics;

import com.pot_pot_golf.Core.Entity.SceneManager;
import com.pot_pot_golf.Core.Entity.Terrain.HeightMap;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/**
 * Class responsible for the physics engine of the game.
 * Uses a more complete physical description.
 */
public class CompletePhysicsEngine extends PhysicsEngine {

    /**
     * Create a new physics engine with a more complete physical description.
     *
     * @param heightMap The height map
     */
    public CompletePhysicsEngine(HeightMap heightMap, SceneManager scene) {
        super(heightMap, scene);
    }

    /**
     * Calculate the equations of motion for the ball.
     * @param t  The time
     * @param x The state vector of the ball where x = [x, z, vx, vz]
     * @return The derivatives of the state vector as an array where dxdt = [vx, vz, ax, az]
     */
    @Override
    protected  double @NotNull [] equationsOfMotion(double t, double @NotNull [] x) {
        double[] dxdt = new double[4];
        double vx = x[2];
        double vz = x[3];
        double magnitudeVelocity = Math.sqrt(vx * vx + vz * vz);

        double dh_dxValue = dh_dxCentredDifferenceMap(x[0], x[1]);
        double dh_dzValue = dh_dzCentredDifferenceMap(x[0], x[1]);

        double kineticFriction;
        double staticFriction;

        assert heightMap != null;
        if (heightMap.getHeight(new Vector3f((float) x[0], 0, (float) x[1])) < sandLevel) {
            kineticFriction = muK_sand;
            staticFriction = muS_sand;
        } else {
            kineticFriction = muK_grass;
            staticFriction = muS_grass;
        }

        // Simplify small value adjustments
        dh_dxValue = Math.abs(dh_dxValue) < 0.00001 ? 0 : dh_dxValue;
        dh_dzValue = Math.abs(dh_dzValue) < 0.00001 ? 0 : dh_dzValue;

        double denominatorHeight = 1 + dh_dxValue * dh_dxValue + dh_dzValue * dh_dzValue;

        if (magnitudeVelocity >= VELOCITY_THRESHOLD) {
            // Moving 
            dxdt[0] = vx;
            dxdt[1] = vz;

            double denominatorVelocity = Math.sqrt(vx * vx + vz * vz + Math.pow(dh_dxValue * vx + dh_dzValue * vz, 2));

            // Calculate acceleration in x-direction
            dxdt[2] = - (g * dh_dxValue) / denominatorHeight - (kineticFriction * g / Math.sqrt(denominatorHeight)) * (vx / denominatorVelocity);
            // Calculate acceleration in z-direction
            dxdt[3] = - (g * dh_dzValue) / denominatorHeight - (kineticFriction * g / Math.sqrt(denominatorHeight)) * (vz / denominatorVelocity);

        } else { // if the ball is at rest
            vx = 0;
            vz = 0;
            dxdt[0] = vx;
            dxdt[1] = vz;

            if (dh_dxValue != 0 || dh_dzValue != 0) { // if the ball is on a slope
                double dh2 = Math.sqrt(dh_dxValue * dh_dxValue + dh_dzValue * dh_dzValue);

                if (staticFriction <= dh2) { // if the friction force does not overcome the downhill force,
                    // the ball will continue to slide
                    dxdt[2] = - (g * dh_dxValue) / denominatorHeight - (kineticFriction * g / Math.sqrt(denominatorHeight)) * (dh_dxValue / dh2);
                    dxdt[3] = - (g * dh_dzValue) / denominatorHeight - (kineticFriction * g / Math.sqrt(denominatorHeight)) * (dh_dzValue / dh2);
                }
            } else {
                dxdt[2] = 0;
                dxdt[3] = 0;
            }
        }

        return dxdt;
    }
}