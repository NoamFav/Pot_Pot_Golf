package com.pot_pot_golf.Game.GameUtils.FieldManager;

/**
 * The game state manager class.
 * This class is responsible for managing the game state of the game.
 * Stores the game state variables.
 */
@SuppressWarnings("unused")
public class GameStateManager {
    // Game state variables
    private boolean canMove;
    private boolean isGuiVisible;
    private boolean isOnMenu;
    private boolean gameStarted;
    private boolean isSoundPlaying;
    private boolean isAnimating;
    private boolean isBot;
    private boolean isAiBot;
    private boolean is2player;
    private boolean isPlayer1Turn;
    private boolean player1Won;
    private boolean hasStartPoint;
    private boolean tKeyWasPressed;

    private boolean isAiBotAnimating;
    private boolean isBotAnimating;

    /**
     * The constructor of the game state manager.
     * It initializes the game state variables to their default values.
     */
    public GameStateManager() {
        canMove = false;
        isGuiVisible = true;
        isOnMenu = true;
        gameStarted = false;
        isSoundPlaying = false;
        hasStartPoint = false;
        tKeyWasPressed = false;
    }

    // Getters and setters
    public boolean canMove() {return canMove;}
    public void setCanMove(boolean canMove) {this.canMove = canMove;}
    public boolean isGuiVisible() {return isGuiVisible;}
    public void setGuiVisible(boolean guiVisible) {isGuiVisible = guiVisible;}
    public boolean isOnMenu() {return isOnMenu;}
    public void setOnMenu(boolean onMenu) {isOnMenu = onMenu;}
    public boolean isGameStarted() {return gameStarted;}
    public void setGameStarted(boolean gameStarted) {this.gameStarted = gameStarted;}
    public boolean isSoundPlaying() {return isSoundPlaying;}
    public void setSoundPlaying(boolean soundPlaying) {isSoundPlaying = soundPlaying;}
    public boolean isAnimating() {return isAnimating;}
    public void setAnimating(boolean animating) {isAnimating = animating;}
    public boolean isBot() {return isBot;}
    public void setBot(boolean bot) {isBot = bot;}
    public void switchBot() {isBot = !isBot;}
    public void switchAiBot() {isAiBot = !isAiBot;}
    public void switch2player() {is2player = !is2player;}
    public void switchPlayer1Turn() {isPlayer1Turn = !isPlayer1Turn;}
    public void switchAudio() {isSoundPlaying = !isSoundPlaying;}
    public boolean isAiBot() {return isAiBot;}
    public void setAiBot(boolean aiBot) {isAiBot = aiBot;}
    public boolean is2player() {return is2player;}
    public void set2player(boolean is2player) {this.is2player = is2player;}
    public boolean isPlayer1Turn() {return isPlayer1Turn;}
    public void setPlayer1Turn(boolean player1Turn) {isPlayer1Turn = player1Turn;}
    public boolean isPlayer1Won() {return player1Won;}
    public void setPlayer1Won(boolean player1Won) {this.player1Won = player1Won;}
    public boolean hasStartPoint() {return hasStartPoint;}
    public void setHasStartPoint(boolean hasStartPoint) {this.hasStartPoint = hasStartPoint;}
    public boolean istKeyWasPressed() {return tKeyWasPressed;}
    public void settKeyWasPressed(boolean tKeyWasPressed) {this.tKeyWasPressed = tKeyWasPressed;}
    public boolean isBotAnimating() {return isBotAnimating;}
    public void setBotAnimating(boolean botAnimating) {isBotAnimating = botAnimating;}
    public boolean isAiBotAnimating() {return isAiBotAnimating;}
    public void setAiBotAnimating(boolean aiBotAnimating) {isAiBotAnimating = aiBotAnimating;}
}
