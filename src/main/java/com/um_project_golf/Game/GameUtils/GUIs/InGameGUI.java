package com.um_project_golf.Game.GameUtils.GUIs;

import com.um_project_golf.Core.AWT.Button;
import com.um_project_golf.Core.AudioManager;
import com.um_project_golf.Core.Camera;
import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Game.GameUtils.Consts;
import com.um_project_golf.Core.WindowManager;
import com.um_project_golf.Game.GameUtils.FieldManager.*;
import com.um_project_golf.Game.Launcher;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class InGameGUI {

    // Records for storing the Runnables for the buttons
    private record InGameMenuRunnable(Runnable resume, Runnable backToMenu, Runnable sound, Runnable quit) {}

    private final long vg;
    private final WindowManager window;
    private final Camera camera;
    private final AudioManager audioManager;

    private final GuiElementManager guiElementManager;
    private final GameStateManager gameState;
    private final GameVarManager gameVarManager;
    private final EntitiesManager entitiesManager;

    public InGameGUI(long vg, @NotNull MainFieldManager context) {
        this.vg = vg;
        this.window = Launcher.getWindow();
        this.camera = context.getCamera();
        this.audioManager = context.getAudioManager();
        this.guiElementManager = context.getGuiElementManager();
        this.gameState = context.getGameStateManager();
        this.gameVarManager = context.getGameVarManager();
        this.entitiesManager = context.getEntitiesManager();

        createInGameMenu();
    }

    /**
     * Creates the in-game menu.
     */
    private void createInGameMenu() {

        InGameMenuRunnable runnable = getInGameMenuRunnable();

        float heightButton = window.getHeightConverted(300);
        float widthButton = window.getWidthConverted(2000);
        float centerButtonX = (window.getWidth() - widthButton) / 2;
        float centerButtonY = (window.getHeight() - heightButton * 3) / 2;
        float font = window.getHeightConverted(100);
        String button = Consts.GUI.BUTTON_IN_GAME_MENU;

        Button resumeButton = new Button(centerButtonX, centerButtonY, widthButton, heightButton, "Resume", font, runnable.resume(), vg, button);
        guiElementManager.addInGameMenuButton(resumeButton);

        Button backToMenuButton = new Button(centerButtonX, centerButtonY + heightButton, widthButton, heightButton, "Back to Menu", font, runnable.backToMenu(), vg, button);
        guiElementManager.addInGameMenuButton(backToMenuButton);

        Button soundButtonInGame = new Button(centerButtonX, centerButtonY + heightButton * 2, widthButton, heightButton, "Sound: " + (gameState.isSoundPlaying() ? "ON" : "OFF"), font, runnable.sound(), vg, button);
        guiElementManager.setSoundButtonInGame(soundButtonInGame);
        guiElementManager.addInGameMenuButton(soundButtonInGame);

        Button exitButton = new Button(centerButtonX, centerButtonY + heightButton * 3, widthButton, heightButton, "Exit", font, runnable.quit(), vg, button);
        guiElementManager.addInGameMenuButton(exitButton);
    }

    /**
     * Gets the runnable for the in-game menu.
     *
     * @return The runnable for the in-game menu.
     */
    private @NotNull InGameMenuRunnable getInGameMenuRunnable() {
        Runnable resume = () -> {
            System.out.println("Resuming game");
            gameState.setCanMove(true);
            gameState.setGuiVisible(false);
        };

        Runnable backToMenu = () -> {
            System.out.println("Returning to menu");
            camera.setPosition(new Vector3f(Consts.SIZE_X / 4, 50, Consts.SIZE_Z / 4));
            camera.setRotation(20, 0, 0);
            gameState.setCanMove(false);
            gameState.setGuiVisible(true);
            gameState.setOnMenu(true);
            gameState.setGameStarted(false);
            gameVarManager.resetNumberOfShots();
            int numberOfShots = gameVarManager.getNumberOfShots();
            int numberOfShots2 = gameVarManager.getNumberOfShots2();
            Entity currentBall = entitiesManager.getCurrentBall();
            guiElementManager.getInfoTextPane().setText("Position: (" + (int) currentBall.getPosition().x + ", " + (int) currentBall.getPosition().z + "). Number of shots: " + (gameState.isPlayer1Turn() ? numberOfShots : numberOfShots2));
        };

        Runnable sound = () -> {
            if (gameState.isSoundPlaying()) {
                System.out.println("Stopping sound");
                audioManager.stopSound();
                guiElementManager.getSoundButtonInGame().setText("Sound: OFF");
                guiElementManager.getSoundButton().setText("Sound: OFF");
            } else {
                System.out.println("Playing sound");
                audioManager.playSound();
                guiElementManager.getSoundButtonInGame().setText("Sound: ON");
                guiElementManager.getSoundButton().setText("Sound: ON");
            }
            gameState.switchAudio();
        };

        Runnable quit = () -> {
            System.out.println("Quitting game");
            GLFW.glfwSetWindowShouldClose(Launcher.getWindow().getWindow(), true);
        };
        return new InGameMenuRunnable(resume, backToMenu, sound, quit);
    }
}
