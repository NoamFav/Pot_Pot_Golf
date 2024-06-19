package com.um_project_golf.Game.GameUtils.FieldManager;

import com.um_project_golf.Core.AWT.Button;
import com.um_project_golf.Core.AWT.TextField;
import com.um_project_golf.Core.AWT.TextPane;
import com.um_project_golf.Core.AWT.Title;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.um_project_golf.Game.GolfGame.debugMode;

@SuppressWarnings("unused")
public class GuiElementManager {
    private Title title;
    private final List<Button> menuButtons;
    private final List<Button> inGameMenuButtons;

    // Menu GUI elements
    private Button startButton;
    private Button debugButton;
    private Button twoPlayerButton;
    private Button aiBotButton;
    private Button botButton;
    private Button soundButton;
    private Button soundButtonInGame;

    // In-game GUI elements
    private Button applyButton;
    private TextField vxTextField;
    private TextField vzTextField;
    private TextPane vxTextPane;
    private TextPane vzTextPane;
    private TextPane infoTextPane;
    private TextPane warningTextPane;
    private TextPane currentPlayer;

    public GuiElementManager() {
        this.menuButtons = new ArrayList<>();
        this.inGameMenuButtons = new ArrayList<>();
    }

    public void update(@NotNull GameStateManager gameState) {
        // Update GUI elements
        if (gameState.isGameStarted()) {
            vxTextField.update();
            vzTextField.update();
            applyButton.update();
        }

        if (gameState.isGuiVisible()) {
            if (gameState.isOnMenu()) {
                for (Button button : menuButtons) {
                    button.update();
                }
            } else {
                for (Button button : inGameMenuButtons) {
                    button.update();
                }
            }
        }
    }

    public void render(@NotNull GameStateManager gameState) {
        // Render your UI elements like menuButtons
        if (gameState.isGuiVisible()) {
            if (gameState.isOnMenu()) {
                for (Button button : menuButtons) {
                    if (!Objects.equals(button.getText(), "Change Terrain")) {
                        button.render();
                    } else {
                        if (!debugMode) {
                            button.render();
                        }
                    }
                }
                title.render();
            } else {
                for (Button button : inGameMenuButtons) {
                    button.render();
                }
            }
        }

        if (gameState.isGameStarted()) {
            warningTextPane.render();
            infoTextPane.render();
            applyButton.render();
            vxTextField.render();
            vzTextField.render();
            vxTextPane.render();
            vzTextPane.render();
            currentPlayer.render();
        }
    }

    public void cleanup() {
        if (title != null) {
            title.cleanup();  // Clean up the title resources
        }
        for (Button button : menuButtons) {
            button.cleanup();
        }
        for (Button button : inGameMenuButtons) {
            button.cleanup();
        }

        warningTextPane.cleanup();
        infoTextPane.cleanup();
        applyButton.cleanup();
        vxTextField.cleanup();
        vzTextField.cleanup();
        vxTextPane.cleanup();
        vzTextPane.cleanup();
        currentPlayer.cleanup();
    }

    /**
     * Update the text fields.
     * Used for On/Off buttons.
     */
    public void updateTextFields(@NotNull GameStateManager gameState) {
        boolean isBot = gameState.isBot();
        boolean isAiBot = gameState.isAiBot();
        boolean is2player = gameState.is2player();

        if (isBot && isAiBot) {
            startButton.setText("Start with Ai Bot and Bot (really long)");
        } else if (isBot) {
            startButton.setText("Start with Bot (long)");
        } else if (isAiBot) {
            startButton.setText("Start with Ai Bot (kinda long)");
        } else {
            startButton.setText("Start");
        }

        twoPlayerButton.setText(is2player ? "2 Player: On" : "2 Player: Off");
        botButton.setText(isBot ? "Bot: On" : "Bot: Off");
        aiBotButton.setText(isAiBot ? "AI Bot: On" : "AI Bot: Off");
        debugButton.setText(debugMode ? "Debug: On" : "Debug: Off");
    }

    public Title getTitle() {return title;}
    public void setTitle(Title title) {this.title = title;}
    public List<Button> getMenuButtons() {return menuButtons;}
    public List<Button> getInGameMenuButtons() {return inGameMenuButtons;}
    public void addMenuButton(Button button) {menuButtons.add(button);}
    public void addInGameMenuButton(Button button) {inGameMenuButtons.add(button);}
    public void clearMenuButtons() {menuButtons.clear();}
    public void clearInGameMenuButtons() {inGameMenuButtons.clear();}
    public Button getStartButton() {return startButton;}
    public void setStartButton(Button startButton) {this.startButton = startButton;}
    public Button getDebugButton() {return debugButton;}
    public void setDebugButton(Button debugButton) {this.debugButton = debugButton;}
    public Button getTwoPlayerButton() {return twoPlayerButton;}
    public void setTwoPlayerButton(Button twoPlayerButton) {this.twoPlayerButton = twoPlayerButton;}
    public Button getAiBotButton() {return aiBotButton;}
    public void setAiBotButton(Button aiBotButton) {this.aiBotButton = aiBotButton;}
    public Button getBotButton() {return botButton;}
    public void setBotButton(Button botButton) {this.botButton = botButton;}
    public Button getSoundButton() {return soundButton;}
    public void setSoundButton(Button soundButton) {this.soundButton = soundButton;}
    public Button getSoundButtonInGame() {return soundButtonInGame;}
    public void setSoundButtonInGame(Button soundButtonInGame) {this.soundButtonInGame = soundButtonInGame;}
    public Button getApplyButton() {return applyButton;}
    public void setApplyButton(Button applyButton) {this.applyButton = applyButton;}
    public TextField getVxTextField() {return vxTextField;}
    public void setVxTextField(TextField vxTextField) {this.vxTextField = vxTextField;}
    public TextField getVzTextField() {return vzTextField;}
    public void setVzTextField(TextField vzTextField) {this.vzTextField = vzTextField;}
    public TextPane getVxTextPane() {return vxTextPane;}
    public void setVxTextPane(TextPane vxTextPane) {this.vxTextPane = vxTextPane;}
    public TextPane getVzTextPane() {return vzTextPane;}
    public void setVzTextPane(TextPane vzTextPane) {this.vzTextPane = vzTextPane;}
    public TextPane getInfoTextPane() {return infoTextPane;}
    public void setInfoTextPane(TextPane infoTextPane) {this.infoTextPane = infoTextPane;}
    public TextPane getWarningTextPane() {return warningTextPane;}
    public void setWarningTextPane(TextPane warningTextPane) {this.warningTextPane = warningTextPane;}
    public TextPane getCurrentPlayer() {return currentPlayer;}
    public void setCurrentPlayer(TextPane currentPlayer) {this.currentPlayer = currentPlayer;}
}
