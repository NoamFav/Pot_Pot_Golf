package com.um_project_golf.Game.GameUtils.GUIs;

import com.um_project_golf.Core.AWT.Button;
import com.um_project_golf.Core.AWT.Title;
import com.um_project_golf.Core.*;
import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.*;
import com.um_project_golf.Core.GolfBots.AIBot;
import com.um_project_golf.Core.GolfBots.RuleBasedBot;
import com.um_project_golf.Game.GameUtils.Consts;
import com.um_project_golf.Core.Utils.StartEndPoint;
import com.um_project_golf.Core.Utils.TerrainSwitch;
import com.um_project_golf.Game.GameUtils.FieldManager.*;
import com.um_project_golf.Game.Launcher;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static com.um_project_golf.Game.GolfGame.debugMode;

/**
 * The menu GUI class.
 * This class is responsible for creating the menu GUI of the game.
 */
public class MenuGUI {
    private record MenuRunnable(Runnable terrainChanger, Runnable startGame, Runnable sound, Runnable quit, Runnable enableDebugMode) {}

    private final Camera camera;
    private final WindowManager window;
    private final long vg;

    private final HeightMap heightMap;
    private final HeightMapPathfinder pathfinder;
    private final AudioManager audioManager;
    private final SceneManager scene;
    private final BlendMapTerrain blendMapTerrain;
    private final ObjectLoader loader;
    private final MouseInput mouseInputs;
    private final TerrainSwitch terrainSwitch;
    private final StartEndPoint startEndPoint;

    private final GuiElementManager guiElementManager;
    private final GameStateManager gameStateManager;
    private final ModelManager modelManager;
    private final PathManager pathManager;
    private final EntitiesManager entitiesManager;
    private final GameVarManager gameVarManager;

    private final MainFieldManager context;

    /**
     * The constructor of the menu GUI.
     *
     * @param vg      The NanoVG context.
     * @param context The main field manager.
     */
    public MenuGUI(long vg, @NotNull MainFieldManager context) {
        this.vg = vg;
        this.camera = context.getCamera();
        this.window = context.getWindow();
        this.heightMap = context.getHeightMap();
        this.pathfinder = context.getPathfinder();
        this.audioManager = context.getAudioManager();
        this.scene = context.getScene();
        this.blendMapTerrain = context.getTerrainManager().getBlendMapTerrain();
        this.loader = context.getLoader();
        this.mouseInputs = context.getMouseInputs();
        this.terrainSwitch = context.getTerrainSwitch();
        this.startEndPoint = context.getStartEndPoint();
        this.guiElementManager = context.getGuiElementManager();
        this.gameStateManager = context.getGameStateManager();
        this.modelManager = context.getModelManager();
        this.pathManager = context.getPathManager();
        this.entitiesManager = context.getEntitiesManager();
        this.gameVarManager = context.getGameVarManager();

        this.context = context;

        createMenu();
    }

    /**
     * Creates the menu.
     */
    private void createMenu() {

        camera.setPosition(new Vector3f(Consts.SIZE_X / 4, 50, Consts.SIZE_Z / 4));
        camera.setRotation(20, 0, 0);

        float width = window.getWidth();

        MenuRunnable runnable = getRunnable();

        float titleWidth = window.getWidthConverted(1200);
        float titleHeight = window.getHeightConverted(1200);
        float titleX = (window.getWidth() - titleWidth) / 2;
        float titleY = window.getHeightConverted(10);

        Title title = new Title(Consts.GUI.TITLE, titleX, titleY, titleWidth, titleHeight, vg);
        guiElementManager.setTitle(title);

        float heightButton = window.getHeightConverted(300);
        float widthButton = window.getWidthConverted(2000);
        float centerButtonX = (width - widthButton) / 2;
        float centerButtonY = titleHeight + titleY;
        float font = window.getUniformScaleFactorFont(100);
        String imageButton = Consts.GUI.BUTTON_MENU;

        Button startButton = new Button(centerButtonX, centerButtonY, widthButton, heightButton, "Start", font, runnable.startGame(), vg, imageButton);
        guiElementManager.setStartButton(startButton);
        guiElementManager.addMenuButton(startButton);

        Button changeTerrain = new Button(centerButtonX, centerButtonY + heightButton, widthButton, heightButton, "Change Terrain", font, runnable.terrainChanger(), vg, imageButton);
        guiElementManager.addMenuButton(changeTerrain);

        Button soundButton = new Button(window.getWidth() - window.getWidthConverted(450), window.getHeightConverted(20), window.getWidthConverted(400), heightButton, "Sound: " + (gameStateManager.isSoundPlaying() ? "ON" : "OFF"), font, runnable.sound(), vg, imageButton);
        guiElementManager.setSoundButton(soundButton);
        guiElementManager.addMenuButton(soundButton);

        Button exit = new Button(centerButtonX, centerButtonY + heightButton * 2, widthButton, heightButton, "Exit", font, runnable.quit(), vg, imageButton);
        guiElementManager.addMenuButton(exit);

        Button botButton = new Button(window.getWidthConverted(30), window.getHeight() - heightButton * 2, widthButton / 4, heightButton, "Play with bot", font, gameStateManager::switchBot, vg, imageButton);
        guiElementManager.setBotButton(botButton);
        guiElementManager.addMenuButton(botButton);

        Button aiBotButton = new Button(window.getWidthConverted(30), window.getHeight() - heightButton * 3, widthButton / 4, heightButton, "Play with AI", font, gameStateManager::switchAiBot, vg, imageButton);
        guiElementManager.setAiBotButton(aiBotButton);
        guiElementManager.addMenuButton(aiBotButton);

        Button twoPlayerButton = new Button(window.getWidthConverted(30), window.getHeight() - heightButton * 4, widthButton / 4, heightButton, "2 Player", font, gameStateManager::switch2player, vg, imageButton);
        guiElementManager.setTwoPlayerButton(twoPlayerButton);
        guiElementManager.addMenuButton(twoPlayerButton);

        Button debugButton = new Button(window.getWidthConverted(30), window.getHeight() - heightButton, widthButton / 4, heightButton, "Debug Mode", font * 0.7f, runnable.enableDebugMode(), vg, imageButton);
        guiElementManager.setDebugButton(debugButton);
        guiElementManager.addMenuButton(debugButton);
    }

    /**
     * Gets the runnable for the menu.
     *
     * @return The runnable for the menu.
     */
    private @NotNull MenuRunnable getRunnable() {
        Runnable terrainChanger = () -> {
            try {
                entitiesManager.clearTrees();
                System.out.println("Changing terrain");
                heightMap.createHeightMap();
                List<Vector2i> path = pathfinder.getPath(Consts.RADIUS_DOWN, Consts.RADIUS_UP, Consts.SIZE_GREEN);
                pathManager.setPath(path);
                startEndPoint.startEndPointConversion(pathManager, heightMap);

                if (entitiesManager.getBotBall() != null) scene.getEntities().removeIf(entity -> entity.equals(entitiesManager.getBotBall()));
                if (entitiesManager.getAiBotBall() != null) scene.getEntities().removeIf(entity -> entity.equals(entitiesManager.getAiBotBall()));

                TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture(Consts.HEIGHTMAP));
                SimplexNoise.shufflePermutation();
                terrainSwitch.terrainSwitch(blendMapTerrain, modelManager.getTree(), blendMap2);
                entitiesManager.setGolfBallPosition(new Vector3f(pathManager.getStartPoint()));
                if (gameStateManager.is2player()) {
                    entitiesManager.setGolfBall2Position(new Vector3f(pathManager.getStartPoint()));
                }
                entitiesManager.setEndFlagPosition(new Vector3f(pathManager.getEndPoint()));
                gameVarManager.resetNumberOfShots();
                Entity currentBall = entitiesManager.getCurrentBall();
                int numberOfShots = gameVarManager.getNumberOfShots();
                int numberOfShots2 = gameVarManager.getNumberOfShots2();
                guiElementManager.getInfoTextPane().setText("Position: (" + (int) currentBall.getPosition().x + ", " + (int) currentBall.getPosition().z + "). Number of shots: " + (gameStateManager.isPlayer1Turn() ? numberOfShots : numberOfShots2));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        Runnable startGame = () -> {
            System.out.println("Starting game");
            camera.setPosition(new Vector3f(pathManager.getStartPoint()));
            camera.setRotation(0, 0, 0);
            gameStateManager.setCanMove(true);
            gameStateManager.setGuiVisible(false);
            gameStateManager.setOnMenu(false);
            gameStateManager.setGameStarted(true);
            gameStateManager.setPlayer1Turn(true);

            if (entitiesManager.getBotBall() != null) scene.getEntities().removeIf(entity -> entity.equals(entitiesManager.getBotBall()));
            if (entitiesManager.getAiBotBall() != null) scene.getEntities().removeIf(entity -> entity.equals(entitiesManager.getAiBotBall()));

            if (gameStateManager.isBot()) {
                gameVarManager.resetCurrentShotIndexBot();
                createBotBall().run();
            }
            if (gameStateManager.isAiBot()) {
                gameVarManager.resetCurrentShotIndexAI();
                createAiBotBall().run();
            }


            Vector3f start = new Vector3f(pathManager.getStartPoint());
            start.y = heightMap.getHeight(new Vector3f(pathManager.getStartPoint().x, 0, pathManager.getStartPoint().z));

            Vector3f end = new Vector3f(pathManager.getEndPoint());
            end.y = heightMap.getHeight(new Vector3f(pathManager.getEndPoint().x, 0, pathManager.getEndPoint().z));

            if (gameStateManager.is2player()) {
                if (entitiesManager.getGolfBall2() == null) {
                    Entity golfBall2 = new Entity(modelManager.getBall2(), new Vector3f(start), new Vector3f(0, 0, 0), 5);
                    entitiesManager.setGolfBall2(golfBall2);
                    scene.addEntity(golfBall2);
                } else {
                    entitiesManager.setGolfBall2Position(start);
                }
            } else {
                if (entitiesManager.getGolfBall2() != null) {
                    scene.getEntities().removeIf(entity -> entity.equals(entitiesManager.getGolfBall2()));
                    entitiesManager.setGolfBall2(null);
                }
            }

            entitiesManager.setGolfBallPosition(start);
            entitiesManager.setEndFlagPosition(end);
        };

        Runnable sound = () -> {
            if (gameStateManager.isSoundPlaying()) {
                System.out.println("Stopping sound");
                audioManager.stopSound();
                guiElementManager.getSoundButton().setText("Sound: OFF");
                guiElementManager.getSoundButtonInGame().setText("Sound: OFF");
            } else {
                System.out.println("Playing sound");
                audioManager.playSound();
                guiElementManager.getSoundButton().setText("Sound: ON");
                guiElementManager.getSoundButtonInGame().setText("Sound: ON");
            }
            gameStateManager.switchAudio();
        };

        Runnable quit = () -> {
            System.out.println("Quitting game");
            GLFW.glfwSetWindowShouldClose(Launcher.getWindow().getWindow(), true);
        };

        Runnable enableDebugMode = () -> {
            entitiesManager.clearTrees();
            debugMode = !debugMode;
            if (entitiesManager.getGolfBall2() == null && gameStateManager.is2player()) {
                Entity golfBall2 = new Entity(modelManager.getBall2(), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 5);
                entitiesManager.setGolfBall2(golfBall2);
                scene.addEntity(golfBall2);
            }
            if (debugMode) {
                System.out.println("Enabling debug mode");
                try {
                    heightMap.createHeightMap();
                    TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture(Consts.HEIGHTMAP));
                    terrainSwitch.terrainSwitch(blendMapTerrain, modelManager.getTree(), blendMap2);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                if (Consts.USE_PREDEFINED_POSITIONS && debugMode) {
                    Vector3f endPoint = new Vector3f(Consts.HOLE_POSITION.x, heightMap.getHeight(new Vector3f(Consts.HOLE_POSITION.x, 0, Consts.HOLE_POSITION.z)), Consts.HOLE_POSITION.z);
                    Vector3f startPoint = new Vector3f(Consts.TEE_POSITION.x, heightMap.getHeight(new Vector3f(Consts.TEE_POSITION.x, 0, Consts.TEE_POSITION.z)), Consts.TEE_POSITION.z);
                    pathManager.setStartPoint(startPoint);
                    pathManager.setEndPoint(endPoint);

                    Vector2i start = new Vector2i((int) startPoint.x, (int) startPoint.z);
                    start.x = (int) ((start.x + Consts.SIZE_X / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_X));
                    start.y = (int) ((start.y + Consts.SIZE_Z / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_X));

                    Vector2i end = new Vector2i((int) endPoint.x, (int) endPoint.z);
                    end.x = (int) ((end.x + Consts.SIZE_X / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_X));
                    end.y = (int) ((end.y + Consts.SIZE_Z / 2) * (Consts.VERTEX_COUNT / Consts.SIZE_X));

                    System.out.println("Start point: " + start);
                    System.out.println("End point: " + end);

                    List<Vector2i> path = pathfinder.getPathDebug(start, end, Consts.SIZE_GREEN);
                    pathManager.setPath(path);
                    try {
                        TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture(Consts.HEIGHTMAP));
                        terrainSwitch.terrainSwitch(blendMapTerrain, modelManager.getTree(), blendMap2);
                    } catch (Exception ignore) {
                    }
                } else {
                    // Can be changed with the left and right arrow keys
                    Vector3f endPoint = new Vector3f(0, 0, 0);
                    Vector3f startPoint = new Vector3f(0, 0, 0);
                    pathManager.setStartPoint(startPoint);
                    pathManager.setEndPoint(endPoint);
                }
                Vector3f startPoint = new Vector3f(pathManager.getStartPoint());
                Vector3f endPoint = new Vector3f(pathManager.getEndPoint());

                entitiesManager.setGolfBallPosition(new Vector3f(startPoint.x, heightMap.getHeight(new Vector3f(startPoint.x, 0, startPoint.z)), startPoint.z));
                if (gameStateManager.is2player()) {
                    entitiesManager.setGolfBall2Position(new Vector3f(startPoint.x, heightMap.getHeight(new Vector3f(startPoint.x, 0, startPoint.z)), startPoint.z));
                }
                entitiesManager.setEndFlagPosition(new Vector3f(endPoint.x, heightMap.getHeight(new Vector3f(endPoint.x, 0, endPoint.z)), endPoint.z));
            } else {
                System.out.println("Disabling debug mode");
                terrainChanger.run();
            }

            new RecreateGUIs(vg,
                    context
            );
            mouseInputs.init();
        };
        return new MenuRunnable(terrainChanger, startGame, sound, quit, enableDebugMode);
    }

    /**
     * Creates the bot ball.
     * And run the simulation.
     *
     * @return The bot ball.
     */
    @NotNull
    @Contract(pure = true)
    public Runnable createBotBall() {

        return () -> {
            System.out.println("Creating bot ball");
            Entity botBall = new Entity(modelManager.getBotBallModel(), new Vector3f(pathManager.getStartPoint()), new Vector3f(0, 0, 0), 5);
            entitiesManager.setBotBall(botBall);
            scene.addEntity(botBall);

            RuleBasedBot ruleBasedBot = new RuleBasedBot(new Entity(botBall), new Entity(entitiesManager.getEndFlag()), heightMap, Consts.TARGET_RADIUS, scene);
            gameVarManager.setBotPath(ruleBasedBot.findBestShot());

            entitiesManager.setBotBallPosition(new Vector3f(pathManager.getStartPoint()));
        };

    }

    /**
     * Creates the AI bot ball.
     * And run the simulation.
     *
     * @return The AI bot ball.
     */
    @NotNull
    @Contract(pure = true)
    public Runnable createAiBotBall() {

        return () -> {
            System.out.println("Creating AI bot ball");
            Entity aiBotBall = new Entity(modelManager.getAiBotBallModel(), new Vector3f(pathManager.getStartPoint()), new Vector3f(0, 0, 0), 5);
            entitiesManager.setAiBotBall(aiBotBall);
            scene.addEntity(aiBotBall);

            AIBot aiBot = new AIBot(new Entity(aiBotBall), new Entity(entitiesManager.getEndFlag()), heightMap, Consts.TARGET_RADIUS, scene);
            gameVarManager.setAiBotPath(aiBot.startAI());

            entitiesManager.setAiBotBallPosition(new Vector3f(pathManager.getStartPoint()));
        };

    }
}
