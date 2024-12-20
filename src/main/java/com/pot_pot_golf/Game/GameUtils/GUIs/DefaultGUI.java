package com.pot_pot_golf.Game.GameUtils.GUIs;

import com.pot_pot_golf.Core.AWT.Button;
import com.pot_pot_golf.Core.AWT.TextField;
import com.pot_pot_golf.Core.AWT.TextPane;
import com.pot_pot_golf.Core.Entity.Entity;
import com.pot_pot_golf.Core.Entity.SceneManager;
import com.pot_pot_golf.Core.Entity.Terrain.HeightMap;
import com.pot_pot_golf.Core.Physics.CompletePhysicsEngine;
import com.pot_pot_golf.Core.Physics.PhysicsEngine;
import com.pot_pot_golf.Core.Utils.BallCollisionDetector;
import com.pot_pot_golf.Core.WindowManager;
import com.pot_pot_golf.Game.GameUtils.Consts;
import com.pot_pot_golf.Game.GameUtils.FieldManager.*;
import com.pot_pot_golf.Game.Launcher;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.InputStream;

/**
 * The default GUI class. This class is responsible for creating the default GUI of the game. Stores
 * the default GUI of the game.
 */
public class DefaultGUI {

    private final WindowManager window;
    private final long vg;
    private final HeightMap heightMap;
    private final SceneManager scene;

    private final GameStateManager gameStateManager;
    private final EntitiesManager entitiesManager;
    private final GameVarManager gameVarManager;
    private final GuiElementManager guiElementManager;

    /**
     * The constructor of the default GUI.
     *
     * @param vg The NanoVG context.
     * @param context The main field manager.
     */
    public DefaultGUI(long vg, @NotNull MainFieldManager context) {
        this.window = Launcher.getWindow();
        this.vg = vg;
        this.heightMap = context.getHeightMap();
        this.scene = context.getScene();
        this.gameStateManager = context.getGameStateManager();
        this.entitiesManager = context.getEntitiesManager();
        this.gameVarManager = context.getGameVarManager();
        this.guiElementManager = context.getGuiElementManager();

        createDefaultGui();
    }

    /** Create the gui when in game. */
    private void createDefaultGui() {
        float width = window.getWidthConverted(1000);
        float height = window.getHeightConverted(300);
        float x = window.getWidthConverted(10);
        float y = window.getHeightConverted(10);
        float font = window.getUniformScaleFactorFont(70);
        float textFieldFont = window.getUniformScaleFactorFont(50);
        boolean isPlayer1Turn = gameStateManager.isPlayer1Turn();
        InputStream imageButton = Consts.GUI.BUTTON_MENU;
        Entity currentBall = entitiesManager.getCurrentBall();
        int numberOfShots = gameVarManager.getNumberOfShots();
        int numberOfShots2 = gameVarManager.getNumberOfShots2();

        TextPane currentPlayer =
                new TextPane(
                        x,
                        y,
                        width,
                        height / 2,
                        "Player 1's turn",
                        font,
                        vg,
                        imageButton,
                        Consts.GUI.FONT);
        TextPane infoTextPane =
                new TextPane(
                        x,
                        y + height / 2,
                        width,
                        height / 2,
                        "Position: ("
                                + (int) currentBall.getPosition().x
                                + ", "
                                + (int) currentBall.getPosition().z
                                + "). Number of shots: "
                                + (isPlayer1Turn ? numberOfShots : numberOfShots2),
                        textFieldFont,
                        vg,
                        imageButton,
                        Consts.GUI.FONT);
        TextPane warningTextPane =
                new TextPane(
                        x,
                        y + height,
                        width,
                        height / 2,
                        "",
                        textFieldFont * .8f,
                        vg,
                        imageButton,
                        Consts.GUI.FONT);
        BallCollisionDetector ballCollisionDetector = new BallCollisionDetector(heightMap, scene);
        gameVarManager.setBallCollisionDetector(ballCollisionDetector);
        guiElementManager.setCurrentPlayer(currentPlayer);
        guiElementManager.setInfoTextPane(infoTextPane);
        guiElementManager.setWarningTextPane(warningTextPane);

        // Creating text-fields and text panes for entering the velocities
        TextPane vxTextPane =
                new TextPane(
                        x * 4,
                        y * 30 + height / 2,
                        width / 5,
                        height / 2,
                        "vx: ",
                        font,
                        vg,
                        imageButton,
                        Consts.GUI.FONT);
        TextField vxTextField =
                new TextField(
                        x * 25,
                        y * 30 + height / 2,
                        width / 3,
                        height / 2,
                        "Enter vx",
                        textFieldFont,
                        vg,
                        imageButton,
                        Consts.GUI.FONT);
        guiElementManager.setVxTextPane(vxTextPane);
        guiElementManager.setVxTextField(vxTextField);

        TextPane vzTextPane =
                new TextPane(
                        x * 4,
                        y * 30 + height,
                        width / 5,
                        height / 2,
                        "vz: ",
                        font,
                        vg,
                        imageButton,
                        Consts.GUI.FONT);
        TextField vzTextField =
                new TextField(
                        x * 25,
                        y * 30 + height,
                        width / 3,
                        height / 2,
                        "Enter vz",
                        textFieldFont,
                        vg,
                        imageButton,
                        Consts.GUI.FONT);
        guiElementManager.setVzTextPane(vzTextPane);
        guiElementManager.setVzTextField(vzTextField);

        setUpCallbacks();

        Button applyButton =
                new Button(
                        x,
                        y * 30 + height + height / 2,
                        3 * width / 5,
                        2 * height / 3,
                        "Apply Velocity",
                        font,
                        runPhysics(),
                        vg,
                        imageButton,
                        Consts.GUI.FONT);
        guiElementManager.setApplyButton(applyButton);
    }

    /** Sets up the callbacks for the text fields. */
    private void setUpCallbacks() {
        GLFW.glfwSetKeyCallback(
                window.getWindow(),
                (window, key, scancode, action, mods) -> {
                    guiElementManager.getVxTextField().handleKeyInput(key, action, mods);
                    guiElementManager.getVzTextField().handleKeyInput(key, action, mods);
                });

        GLFW.glfwSetMouseButtonCallback(
                window.getWindow(),
                (window, button, action, mods) -> {
                    if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
                        double[] xPos = new double[1];
                        double[] yPos = new double[1];
                        GLFW.glfwGetCursorPos(window, xPos, yPos);
                        guiElementManager
                                .getVxTextField()
                                .handleMouseClick((float) xPos[0], (float) yPos[0]);
                        guiElementManager
                                .getVzTextField()
                                .handleMouseClick((float) xPos[0], (float) yPos[0]);
                    }
                });
    }

    /**
     * Creates the physics runnable.
     *
     * @return The physics runnable.
     */
    @NotNull
    @Contract(pure = true)
    private Runnable runPhysics() {
        PhysicsEngine engine = new CompletePhysicsEngine(heightMap, scene);

        return () -> {
            if (gameStateManager.isAnimating()) {
                return; // Exit if already animating
            }
            try {
                guiElementManager.getWarningTextPane().setText("");
                // Remove any non-numeric characters from the text fields if present
                // (backup in case of threading issues)
                double vx =
                        Double.parseDouble(
                                guiElementManager
                                        .getVxTextField()
                                        .getText()
                                        .replaceAll("[a-zA-Z]", ""));
                double vz =
                        Double.parseDouble(
                                guiElementManager
                                        .getVzTextField()
                                        .getText()
                                        .replaceAll("[a-zA-Z]", ""));
                System.out.println("Applying physics with vx: " + vx + ", vz: " + vz);

                if (Math.abs(vx) > Consts.MAX_SPEED || Math.abs(vz) > Consts.MAX_SPEED) {
                    guiElementManager
                            .getWarningTextPane()
                            .setText("Speed too high: max " + Consts.MAX_SPEED + " m/s");
                } else {
                    Entity currentBall = entitiesManager.getCurrentBall();
                    double[] initialState = {
                        currentBall.getPosition().x, currentBall.getPosition().z, vx, vz
                    }; // initialState = [x, z, vx, vz]
                    double h = 0.1; // Time step
                    gameVarManager.setBallPositions(
                            engine.runRK4(initialState, h)); // Run the simulation

                    gameVarManager.resetCurrentPositionIndex();
                    gameStateManager.setAnimating(true);
                    gameVarManager.resetAnimationTimeAccumulator();
                    if (gameStateManager.isPlayer1Turn()) {
                        gameVarManager.incrementNumberOfShots();
                    } else {
                        gameVarManager.incrementNumberOfShots2();
                    }
                    gameVarManager.setShotStartPosition(
                            new Vector3f(
                                    currentBall.getPosition())); // Store the start position of the
                    // shot
                    int numberOfShots = gameVarManager.getNumberOfShots();
                    int numberOfShots2 = gameVarManager.getNumberOfShots2();
                    guiElementManager
                            .getInfoTextPane()
                            .setText(
                                    "Position: ("
                                            + (int) currentBall.getPosition().x
                                            + ", "
                                            + (int) currentBall.getPosition().z
                                            + "). Number of shots: "
                                            + (gameStateManager.isPlayer1Turn()
                                                    ? numberOfShots
                                                    : numberOfShots2));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
