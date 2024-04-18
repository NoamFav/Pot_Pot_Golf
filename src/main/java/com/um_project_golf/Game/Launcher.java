package com.um_project_golf.Game;

import com.um_project_golf.Core.Utils.Consts;
import com.um_project_golf.Core.EngineManager;
import com.um_project_golf.Core.WindowManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;

/**
 * The main class of the game.
 * It is responsible for starting the game.
 */
public class Launcher {

    private static final Logger log = LogManager.getLogger(Launcher.class);
    private static WindowManager window;
    private static GolfGame golfGame;

    /**
     * The main method of the game.
     * It is responsible for starting the game.
     *
     * @param args The arguments passed to the game.
     */
    public static void main(String[] args) {
        System.out.println(Version.getVersion());
        window = new WindowManager(Consts.Title, 0, 0, true);
        golfGame = new GolfGame();
        EngineManager engine = new EngineManager();

        try {
            engine.start();
        } catch (Exception e) {
            log.error(e);
        }
    }

    public static WindowManager getWindow() {
        return window;
    }

    public static GolfGame getGolfGame() {
        return golfGame;
    }
}
