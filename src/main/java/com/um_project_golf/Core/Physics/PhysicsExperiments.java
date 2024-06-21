package com.um_project_golf.Core.Physics;

import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Game.GameUtils.Consts;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PhysicsExperiments {

    // Initialize HeightMap and SceneManager
    private static final HeightMap FLATGRASSTERRAIN = createFlatTerrain();
    private static final HeightMap HILLYTERRAIN = createHillyTerrain();
    private static final HeightMap STEEPHILLYTERRAIN = createSteepHillyTerrain();
    private static final HeightMap GENTLEHILLYTERRAIN = createGentleHillyTerrain();
    private static final HeightMap FLATSANDYTERRAIN = createSandyTerrain();
    private static final SceneManager SCENE = new SceneManager(-90);

    // Create CompletePhysicsEngine instances
    private static final CompletePhysicsEngine flatGrassCompleteEngine = new CompletePhysicsEngine(FLATGRASSTERRAIN, SCENE);
    private static final CompletePhysicsEngine hillyCompleteEngine = new CompletePhysicsEngine(HILLYTERRAIN, SCENE);
    private static final CompletePhysicsEngine steepHillyCompleteEngine = new CompletePhysicsEngine(STEEPHILLYTERRAIN, SCENE);
    private static final CompletePhysicsEngine gentleHillyCompleteEngine = new CompletePhysicsEngine(GENTLEHILLYTERRAIN, SCENE);
    private static final CompletePhysicsEngine flatSandyCompleteEngine = new CompletePhysicsEngine(FLATSANDYTERRAIN, SCENE);

    // Create SimplePhysicsEngine instances
    private static final SimplePhysicsEngine flatGrassSimpleEngine = new SimplePhysicsEngine(FLATGRASSTERRAIN, SCENE);
    private static final SimplePhysicsEngine hillySimpleEngine = new SimplePhysicsEngine(HILLYTERRAIN, SCENE);
    private static final SimplePhysicsEngine steepHillySimpleEngine = new SimplePhysicsEngine(STEEPHILLYTERRAIN, SCENE);
    private static final SimplePhysicsEngine gentleHillySimpleEngine = new SimplePhysicsEngine(GENTLEHILLYTERRAIN, SCENE);
    private static final SimplePhysicsEngine flatSandySimpleEngine = new SimplePhysicsEngine(FLATSANDYTERRAIN, SCENE);

    private static final String RESULTS_DIR = "src/main/java/com/um_project_golf/Core/Physics/Experiment_results";

    public static void main(String[] args) {

        // Define initial states
        double[] initialStateFastX = {0.0, 0.0, 10.0, 0.0}; // Fast moving in x-direction
        double[] initialStateSlowX = {0.0, 0.0, 2.0, 0.0}; // Slow moving in x-direction
        double[] initialStateFastZ = {0.0, 0.0, 0.0, 10.0}; // Fast moving in z-direction
        double[] initialStateSlowZ = {0.0, 0.0, 0.0, 2.0}; // Slow moving in z-direction
        double[] initialStateDiagonal = {0.0, 0.0, 5.0, 5.0};  // Diagonal movement

        boolean showAllPositions = true;

        // Run experiments for CompletePhysicsEngine
        // Flat terrain
        runExperiment("Complete Physics Engine - Flat Grass Terrain - Fast x", flatGrassCompleteEngine, initialStateFastX, showAllPositions);
        runExperiment("Complete Physics Engine - Flat Grass Terrain - Slow x", flatGrassCompleteEngine, initialStateSlowX, showAllPositions);
        runExperiment("Complete Physics Engine - Flat Grass Terrain - Fast z", flatGrassCompleteEngine, initialStateFastZ, showAllPositions);
        runExperiment("Complete Physics Engine - Flat Grass Terrain - Slow z", flatGrassCompleteEngine, initialStateSlowZ, showAllPositions);
        runExperiment("Complete Physics Engine - Flat Grass Terrain - Diagonal", flatGrassCompleteEngine, initialStateDiagonal, showAllPositions);

        // Normal Hilly Terrain
        runExperiment("Complete Physics Engine - Hilly Terrain - Fast x", hillyCompleteEngine, initialStateFastX, showAllPositions);
        runExperiment("Complete Physics Engine - Hilly Terrain - Slow x", hillyCompleteEngine, initialStateSlowX, showAllPositions);
        runExperiment("Complete Physics Engine - Hilly Terrain - Fast z", hillyCompleteEngine, initialStateFastZ, showAllPositions);
        runExperiment("Complete Physics Engine - Hilly Terrain - Slow z", hillyCompleteEngine, initialStateSlowZ, showAllPositions);
        runExperiment("Complete Physics Engine - Hilly Terrain - Diagonal", hillyCompleteEngine, initialStateDiagonal, showAllPositions);
        
        // Steep Hilly Terrain
        runExperiment("Complete Physics Engine - Steep Hilly Terrain - Fast x", steepHillyCompleteEngine, initialStateFastX, showAllPositions);
        runExperiment("Complete Physics Engine - Steep Hilly Terrain - Slow x", steepHillyCompleteEngine, initialStateSlowX, showAllPositions);
        runExperiment("Complete Physics Engine - Steep Hilly Terrain - Fast z", steepHillyCompleteEngine, initialStateFastZ, showAllPositions);
        runExperiment("Complete Physics Engine - Steep Hilly Terrain - Slow z", steepHillyCompleteEngine, initialStateSlowZ, showAllPositions);
        runExperiment("Complete Physics Engine - Steep Hilly Terrain - Diagonal", steepHillyCompleteEngine, initialStateDiagonal, showAllPositions);

        // Gentle Hilly Terrain
        runExperiment("Complete Physics Engine - Gentle Hilly Terrain - Fast x", gentleHillyCompleteEngine, initialStateFastX, showAllPositions);
        runExperiment("Complete Physics Engine - Gentle Hilly Terrain - Slow x", gentleHillyCompleteEngine, initialStateSlowX, showAllPositions);
        runExperiment("Complete Physics Engine - Gentle Hilly Terrain - Fast z", gentleHillyCompleteEngine, initialStateFastZ, showAllPositions);
        runExperiment("Complete Physics Engine - Gentle Hilly Terrain - Slow z", gentleHillyCompleteEngine, initialStateSlowZ, showAllPositions);
        runExperiment("Complete Physics Engine - Gentle Hilly Terrain - Diagonal", gentleHillyCompleteEngine, initialStateDiagonal, showAllPositions);

        // Sandy Terrain
        runExperiment("Complete Physics Engine - Sandy Terrain - Fast x", flatSandyCompleteEngine, initialStateFastX, showAllPositions);
        runExperiment("Complete Physics Engine - Sandy Terrain - Slow x", flatSandyCompleteEngine, initialStateSlowX, showAllPositions);
        runExperiment("Complete Physics Engine - Sandy Terrain - Fast z", flatSandyCompleteEngine, initialStateFastZ, showAllPositions);
        runExperiment("Complete Physics Engine - Sandy Terrain - Slow z", flatSandyCompleteEngine, initialStateSlowZ, showAllPositions);
        runExperiment("Complete Physics Engine - Sandy Terrain - Diagonal", flatSandyCompleteEngine, initialStateDiagonal, showAllPositions);

        // Run experiments for SimplePhysicsEngine
        // Flat terrain
        runExperiment("Simple Physics Engine - Flat Grass Terrain - Fast x", flatGrassSimpleEngine, initialStateFastX, showAllPositions);
        runExperiment("Simple Physics Engine - Flat Grass Terrain - Slow x", flatGrassSimpleEngine, initialStateSlowX, showAllPositions);
        runExperiment("Simple Physics Engine - Flat Grass Terrain - Fast z", flatGrassSimpleEngine, initialStateFastZ, showAllPositions);
        runExperiment("Simple Physics Engine - Flat Grass Terrain - Slow z", flatGrassSimpleEngine, initialStateSlowZ, showAllPositions);
        runExperiment("Simple Physics Engine - Flat Grass Terrain - Diagonal", flatGrassSimpleEngine, initialStateDiagonal, showAllPositions);

        // Normal Hilly Terrain
        runExperiment("Simple Physics Engine - Hilly Terrain - Fast x", hillySimpleEngine, initialStateFastX, showAllPositions);
        runExperiment("Simple Physics Engine - Hilly Terrain - Slow x", hillySimpleEngine, initialStateSlowX, showAllPositions);
        runExperiment("Simple Physics Engine - Hilly Terrain - Fast z", hillySimpleEngine, initialStateFastZ, showAllPositions);
        runExperiment("Simple Physics Engine - Hilly Terrain - Slow z", hillySimpleEngine, initialStateSlowZ, showAllPositions);
        runExperiment("Simple Physics Engine - Hilly Terrain - Diagonal", hillySimpleEngine, initialStateDiagonal, showAllPositions);
        
        // Steep Hilly Terrain
        runExperiment("Simple Physics Engine - Steep Hilly Terrain - Fast x", steepHillySimpleEngine, initialStateFastX, showAllPositions);
        runExperiment("Simple Physics Engine - Steep Hilly Terrain - Slow x", steepHillySimpleEngine, initialStateSlowX, showAllPositions);
        runExperiment("Simple Physics Engine - Steep Hilly Terrain - Fast z", steepHillySimpleEngine, initialStateFastZ, showAllPositions);
        runExperiment("Simple Physics Engine - Steep Hilly Terrain - Slow z", steepHillySimpleEngine, initialStateSlowZ, showAllPositions);
        runExperiment("Simple Physics Engine - Steep Hilly Terrain - Diagonal", steepHillySimpleEngine, initialStateDiagonal, showAllPositions);

        // Gentle Hilly Terrain
        runExperiment("Simple Physics Engine - Gentle Hilly Terrain - Fast x", gentleHillySimpleEngine, initialStateFastX, showAllPositions);
        runExperiment("Simple Physics Engine - Gentle Hilly Terrain - Slow x", gentleHillySimpleEngine, initialStateSlowX, showAllPositions);
        runExperiment("Simple Physics Engine - Gentle Hilly Terrain - Fast z", gentleHillySimpleEngine, initialStateFastZ, showAllPositions);
        runExperiment("Simple Physics Engine - Gentle Hilly Terrain - Slow z", gentleHillySimpleEngine, initialStateSlowZ, showAllPositions);
        runExperiment("Simple Physics Engine - Gentle Hilly Terrain - Diagonal", gentleHillySimpleEngine, initialStateDiagonal, showAllPositions);

        // Sandy Terrain
        runExperiment("Simple Physics Engine - Flat Sandy Terrain - Fast x", flatSandySimpleEngine, initialStateFastX, showAllPositions);
        runExperiment("Simple Physics Engine - Flat Sandy Terrain - Slow x", flatSandySimpleEngine, initialStateSlowX, showAllPositions);
        runExperiment("Simple Physics Engine - Flat Sandy Terrain - Fast z", flatSandySimpleEngine, initialStateFastZ, showAllPositions);
        runExperiment("Simple Physics Engine - Flat Sandy Terrain - Slow z", flatSandySimpleEngine, initialStateSlowZ, showAllPositions);
        runExperiment("Simple Physics Engine - Flat Sandy Terrain - Diagonal", flatSandySimpleEngine, initialStateDiagonal, showAllPositions);
    }

    private static void runExperiment(String experimentName, PhysicsEngine engine, double[] initialState, boolean showAllPositions) {
        System.out.println("Running experiment: " + experimentName);
        double stepSize = 0.1; 
        List<Vector3f> trajectory = engine.runRK4(initialState, stepSize);

        // Create a StringBuilder for the current experiment
        StringBuilder resultsTable = new StringBuilder();
        if (showAllPositions) {
            resultsTable.append("Experiment; Time; X; Y; Z\n");
        }
        else {
            resultsTable.append("Experiment; Start x; Start y; Start z; Final x; Final y; Final z\n");
        }

        // Output start and final position
        Vector3f startPosition = trajectory.get(0);
        Vector3f finalPosition = trajectory.get(trajectory.size() - 1);

        System.out.println("Start position: " + startPosition);
        System.out.println("Final position: " + finalPosition);

        if (showAllPositions) {
            for (int i = 0; i < trajectory.size(); i++) {
                Vector3f position = trajectory.get(i);
                double currentTime = i * stepSize;
                resultsTable.append(String.format("%s; %.2f; %.2f; %.2f; %.2f\n", experimentName, currentTime, position.x, position.y, position.z));
            }
        }
        else {
            resultsTable.append(String.format("%s; %.2f; %.2f; %.2f; %.2f; %.2f; %.2f\n",
            experimentName, startPosition.x, startPosition.y, startPosition.z,
            finalPosition.x, finalPosition.y, finalPosition.z));
        }

        // Save the results to a file
        saveResultsToFile(resultsTable.toString(), experimentName.replace(" ", "_") + "_results.csv");
    }

    private static void saveResultsToFile(String data, String fileName) {
        try (FileWriter fileWriter = new FileWriter(RESULTS_DIR + File.separator + fileName)) {
            fileWriter.write(data);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    @Contract(value = " -> new", pure = true)
    private static @NotNull HeightMap createFlatTerrain() {
        // Create a mock of flat terrain above sand level
        return new HeightMap() {
            @Override
            public float getHeight(@NotNull Vector3f position) {
                return Consts.SAND_HEIGHT + 0.1f;
            }
        };
    }

    @Contract(value = " -> new", pure = true)
    private static @NotNull HeightMap createHillyTerrain() {
        // Create a mock of hilly terrain
        return new HeightMap() {
            @Override
            public float getHeight(@NotNull Vector3f position) {
                // Define a hilly height function
                return (float) Math.sin(position.x) * (float) Math.cos(position.z);
            }
        };
    }

    @Contract(value = " -> new", pure = true)
    private static @NotNull HeightMap createSteepHillyTerrain() {
        // Create a mock of hilly terrain
        return new HeightMap() {
            @Override
            public float getHeight(@NotNull Vector3f position) {
                // Define a hilly height function
                return 10 * ((float) Math.sin(position.x) * (float) Math.cos(position.z));
            }
        };
    }

    @Contract(value = " -> new", pure = true)
    private static @NotNull HeightMap createGentleHillyTerrain() {
        // Create a mock of gentle hilly terrain
        return new HeightMap() {
            @Override
            public float getHeight(@NotNull Vector3f position) {
                // Define a gentle hilly height function
                return 0.1f * ((float) Math.sin(position.x) * (float) Math.cos(position.z));
            }
        };
    }    

    @Contract(value = " -> new", pure = true)
    private static @NotNull HeightMap createSandyTerrain() {
        // Create a mock of flat sandy terrain
        return new HeightMap() {
            @Override
            public float getHeight(@NotNull Vector3f position) {
                // Define a flat sandy terrain for simplicity
                return Consts.SAND_HEIGHT - 0.1f; // Only gets recognized as sand when it's below the sand level, so minus 0.01f
            }
        };
    }
}
