package com.pot_pot_golf.Game;

import com.pot_pot_golf.Core.EngineManager;
import com.pot_pot_golf.Core.Utils.ResourceCloner;
import com.pot_pot_golf.Core.WindowManager;
import com.pot_pot_golf.Game.GameUtils.Consts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;

import java.io.IOException;

/** The main class of the game. It is responsible for starting the game. */
public class Launcher {

    private static final Logger log =
            LogManager.getLogger(Launcher.class); // Logger for the Launcher class
    private static WindowManager window; // The window manager of the game
    private static GolfGame golfGame; // The game itself

    /**
     * The main method of the game. It is responsible for starting the game.
     *
     * @param args The arguments passed to the game.
     */
    public static void main(String[] args) {
        System.out.println(Version.getVersion()); // Print the version of LWJGL

        try {
            ResourceCloner.initializeResources();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        window = new WindowManager(Consts.Title, 0, 0, true); // Create the window manager
        golfGame = new GolfGame(); // Create the game
        EngineManager engine = new EngineManager(); // Create the engine manager

        try { // Try to start the engine
            engine.start();
        } catch (Exception e) { // If an exception is thrown, print the stack trace
            // log.error("An error occurred while starting the engine: {}", e.getMessage());
            // log.error("An error occurred while starting the engine: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }

    public static GolfGame getGolfGame() {
        return golfGame;
    }
}
