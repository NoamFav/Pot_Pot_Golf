package com.pot_pot_golf.Game.GameUtils.GameLogic;

import com.pot_pot_golf.Core.Entity.Entity;
import com.pot_pot_golf.Core.Entity.SceneManager;
import com.pot_pot_golf.Game.GameUtils.Consts;
import com.pot_pot_golf.Game.GameUtils.FieldManager.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

/**
 * The update manager class.
 * This class is responsible for updating the game each frame.
 */
public class UpdateManager {

    private final SceneManager scene;

    private final EntitiesManager entitiesManager;
    private final GameVarManager gameVarManager;
    private final PathManager pathManager;
    private final GuiElementManager guiElementManager;
    private final GameStateManager gameStateManager;

    /**
     * The constructor of the update manager.
     * It initializes the update manager.
     *
     * @param context The main field manager.
     */
    public UpdateManager(@NotNull MainFieldManager context) {
        this.scene = context.getScene();

        this.entitiesManager = context.getEntitiesManager();
        this.gameVarManager = context.getGameVarManager();
        this.pathManager = context.getPathManager();
        this.guiElementManager = context.getGuiElementManager();
        this.gameStateManager = context.getGameStateManager();
    }

    /**
     * Make the day and night cycle.
     * Not used for now.
     */
    public void daytimeCycle() {
        scene.increaseSpotAngle(0.01f);
        if (scene.getSpotAngle() > 4) {
            scene.setSpotInc(-1);
        } else if (scene.getSpotAngle() < -4) {
            scene.setSpotInc(1);
        }

        scene.increaseLightAngle(1.1f);
        scene.setLightAngle(65);
        scene.getDirectionalLight().setIntensity(0.5f);

//        if (scene.getLightAngle() > 90) {
//            scene.getDirectionalLight().setIntensity(0);
//            if (scene.getLightAngle() >= 360)
//                scene.setLightAngle(-90);
//        } else if (scene.getLightAngle() <= -80 || scene.getLightAngle() >= 80) {
//            float factor = 1 - (Math.abs(scene.getLightAngle()) - 80) / 10.0f;
//            scene.getDirectionalLight().setIntensity(factor);
//            scene.getDirectionalLight().getColor().x = Math.max(factor, 0.9f);
//            scene.getDirectionalLight().getColor().z = Math.max(factor, 0.5f);
//        } else {
//            scene.getDirectionalLight().setIntensity(1);
//            scene.getDirectionalLight().getColor().x = 1;
//            scene.getDirectionalLight().getColor().z = 1;
//            scene.getDirectionalLight().getColor().y = 1;
//        }
//
//        double angle = Math.toRadians(scene.getLightAngle());
//        scene.getDirectionalLight().getDirection().x = (float) Math.sin(angle);
//        scene.getDirectionalLight().getDirection().y = (float) Math.cos(angle);
    }

    /**
     * Update the tree animations.
     * Used for end game animation.
     */
    public void updateTreeAnimations() {
        if (entitiesManager.getTrees().isEmpty() || gameVarManager.isTreeAnimationIdle()) {
            return;
        }

        gameVarManager.incrementTreeAnimationTime(0.1f); // Adjust the time increment as needed

        // Total duration of the animation (5 seconds up and 5 seconds down)
        float treeAnimationDuration = 10f;
        float t = gameVarManager.getTreeAnimationTime() / (treeAnimationDuration / 2);
        if (t > 1f) t = 1f;

        for (int i = 0; i < entitiesManager.getTrees().size(); i++) {
            Entity tree = entitiesManager.getTrees().get(i);
            float baseHeight = entitiesManager.getTreeHeights().get(i);
            float treeHeightOffset = 10f;

            if (gameVarManager.isTreeAnimationGoingUp()) {
                if (gameVarManager.getTreeAnimationTime() <= treeAnimationDuration / 2) {
                    float newY = baseHeight + treeHeightOffset * t;
                    float newRotation = -90 + 180 * t;
                    tree.setPosition(tree.getPosition().x, newY, tree.getPosition().z);
                    tree.setRotation(newRotation, tree.getRotation().y, tree.getRotation().z);
                } else {
                    gameVarManager.setTreeAnimationGoingDown();
                    gameVarManager.resetTreeAnimationTime();
                }
            } else if(gameVarManager.isTreeAnimationGoingDown()) {
                if (gameVarManager.getTreeAnimationTime() <= treeAnimationDuration / 2) {
                    float newY = baseHeight + treeHeightOffset * (1 - t);
                    float newRotation = 90 + 180f * t;
                    tree.setPosition(tree.getPosition().x, newY, tree.getPosition().z);
                    tree.setRotation(newRotation, tree.getRotation().y, tree.getRotation().z);
                } else {
                    gameVarManager.setTreeAnimationIdle();}
            }
        }
    }

    /**
     * Animate the ball.
     * Used for the ball's movement.
     * Makes the ball move to the end point.
     */
    public void animateBall() {
        if (gameStateManager.isAnimating()) {
            float timeStep = 0.1f;
            gameVarManager.incrementAnimationTimeAccumulator(timeStep);

            if (gameVarManager.getAnimationTimeAccumulator() >= timeStep) {
                gameVarManager.decrementAnimationTimeAccumulator(timeStep);
                List<Vector3f> ballPositions = gameVarManager.getBallPositions();

                if (gameVarManager.getCurrentPositionIndex() < ballPositions.size()) {
                    Vector3f nextPosition = ballPositions.get(gameVarManager.getCurrentPositionIndex());

                    if (nextPosition == ballPositions.get(ballPositions.size() - 1)) {
                        float isInHoleThreshold = Consts.TARGET_RADIUS;
                        Vector3f endPoint = new Vector3f(pathManager.getEndPoint());
                        if (nextPosition.x <= endPoint.x + isInHoleThreshold && nextPosition.x >= endPoint.x - isInHoleThreshold) {
                            if (nextPosition.z <= endPoint.z + isInHoleThreshold && nextPosition.z >= endPoint.z - isInHoleThreshold) {
                                int numberOfShots = gameVarManager.getNumberOfShots();
                                int numberOfShots2 = gameVarManager.getNumberOfShots2();
                                int shot = gameStateManager.isPlayer1Turn() ? numberOfShots : numberOfShots2;
                                System.out.println("Ball reached the end point!");
                                System.out.println("You took " + shot + " shots to reach the end point!");
                                System.out.println(endPoint);
                                if (gameStateManager.is2player()) gameStateManager.setPlayer1Won(gameStateManager.isPlayer1Turn());
                                guiElementManager.getCurrentPlayer().setText("Player " + (gameStateManager.isPlayer1Won() ? "1" : "2") + " wins!");
                                System.out.println("Player " + (gameStateManager.isPlayer1Won() ? "1" : "2") + " wins!");
                                guiElementManager.getWarningTextPane().setText("You Win! In " + shot + " shots!");
                                gameVarManager.setTreeAnimationGoingUp();
                                gameVarManager.resetTreeAnimationTime();
                            }
                        }
                    }

                    gameVarManager.getBallCollisionDetector().checkCollisionBall(nextPosition);
                    if (nextPosition.y <= -0.1) { // Ball in water
                        entitiesManager.setCurrentBallPosition(gameVarManager.getShotStartPosition());
                        gameStateManager.setAnimating(false);
                        updateBallMultiplayer();
                        guiElementManager.getWarningTextPane().setText("Ploof! Ball in water! Resetting to last shot position.");
                    } else {
                        entitiesManager.setCurrentBallPosition(nextPosition);
                        gameVarManager.incrementCurrentPositionIndex();}
                } else {
                    gameStateManager.setAnimating(false); // Animation completed
                    updateBallMultiplayer();
                }
            }
        } else {
            updateDirectionalArrow();
        }
    }


    /**
     * Animate the bot ball.
     * Used for the bot's movement.
     * Makes the bot ball move to the end point.
     */
    public void animateBotBall() {

        float timeStep = 0.1f;
        gameVarManager.incrementAnimationTimeAccumulatorBot(timeStep);

        if (gameVarManager.getAnimationTimeAccumulatorBot() >= timeStep) {
            gameVarManager.decrementAnimationTimeAccumulatorBot(timeStep);

            List<Vector3f> ballPositions;
            if (gameVarManager.getCurrentShotIndexBot() < gameVarManager.getBotPath().size()) {ballPositions = gameVarManager.getBotPath().get(gameVarManager.getCurrentShotIndexBot());}
            else { gameStateManager.setBotAnimating(false); return;}

            if (gameVarManager.getCurrentPositionIndexBot() < ballPositions.size()) {
                Vector3f nextPosition = ballPositions.get(gameVarManager.getCurrentPositionIndexBot());

                if (nextPosition == ballPositions.get(ballPositions.size() - 1)) {
                    float isInHoleThreshold = Consts.TARGET_RADIUS;
                    Vector3f endPoint = new Vector3f(pathManager.getEndPoint());
                    if (nextPosition.x <= endPoint.x + isInHoleThreshold && nextPosition.x >= endPoint.x - isInHoleThreshold) {
                        if (nextPosition.z <= endPoint.z + isInHoleThreshold && nextPosition.z >= endPoint.z - isInHoleThreshold) {
                            System.out.println("Bot Ball reached the end point!");
                            System.out.println(endPoint);
                            gameVarManager.setTreeAnimationGoingUp();
                            gameVarManager.resetTreeAnimationTime();
                        }
                    }
                }
                gameVarManager.getBallCollisionDetector().checkCollisionBall(nextPosition);
                entitiesManager.getBotBall().setPosition(nextPosition);
                gameVarManager.incrementCurrentPositionIndexBot();
            } else {
                gameVarManager.resetAnimationTimeAccumulatorBot();
                gameVarManager.resetCurrentPositionIndexBot();
                gameVarManager.incrementCurrentShotIndexBot();
                gameStateManager.setBotAnimating(false);
            }
        }
    }

    public void animateAIBall() {
        float timeStep = 0.1f;
        gameVarManager.incrementAnimationTimeAccumulatorAI(timeStep);

        if (gameVarManager.getAnimationTimeAccumulatorAI() >= timeStep) {
            gameVarManager.decrementAnimationTimeAccumulatorAI(timeStep);

            List<Vector3f> ballPositions;
            if (gameVarManager.getCurrentShotIndexAI() < gameVarManager.getAiBotPath().size()) { ballPositions = gameVarManager.getAiBotPath().get(gameVarManager.getCurrentShotIndexAI());}
            else { gameStateManager.setAiBotAnimating(false); return;}

            if (gameVarManager.getCurrentPositionIndexAI() < ballPositions.size()) {
                Vector3f nextPosition = ballPositions.get(gameVarManager.getCurrentPositionIndexAI());

                if (nextPosition == ballPositions.get(ballPositions.size() - 1)) {
                    float isInHoleThreshold = Consts.TARGET_RADIUS;
                    Vector3f endPoint = new Vector3f(pathManager.getEndPoint());
                    if (nextPosition.x <= endPoint.x + isInHoleThreshold && nextPosition.x >= endPoint.x - isInHoleThreshold) {
                        if (nextPosition.z <= endPoint.z + isInHoleThreshold && nextPosition.z >= endPoint.z - isInHoleThreshold) {
                            System.out.println("AI Ball reached the end point!");
                            System.out.println(endPoint);
                            gameVarManager.setTreeAnimationGoingUp();
                            gameVarManager.resetTreeAnimationTime();
                        }
                    }
                }
                gameVarManager.getBallCollisionDetector().checkCollisionBall(nextPosition);
                entitiesManager.getAiBotBall().setPosition(nextPosition);
                gameVarManager.incrementCurrentPositionIndexAI();
            } else {
                gameVarManager.resetAnimationTimeAccumulatorAI();
                gameVarManager.resetCurrentPositionIndexAI();
                gameVarManager.incrementCurrentShotIndexAI();
                gameStateManager.setAiBotAnimating(false);
            }
        }
    }

    /**
     * Update the ball for multiplayer.
     * Switches the player's turn.
     * Used for multiplayer.
     */
    private void updateBallMultiplayer() {
        if (gameStateManager.is2player()) {
            gameStateManager.switchPlayer1Turn();
            boolean isPlayer1Turn = gameStateManager.isPlayer1Turn();
            entitiesManager.updateCurrentBall(isPlayer1Turn);
            System.out.println("Player " + (isPlayer1Turn ? "1's" : "2's") + " turn");
            guiElementManager.getCurrentPlayer().setText("Player " + (isPlayer1Turn ? "1's" : "2's") + " turn");
            Entity currentBall = entitiesManager.getCurrentBall();
            int numberOfShots = gameVarManager.getNumberOfShots();
            int numberOfShots2 = gameVarManager.getNumberOfShots2();
            guiElementManager.getInfoTextPane().setText("Position: (" + (int) currentBall.getPosition().x + ", " + (int) currentBall.getPosition().z + "). Number of shots: " + (isPlayer1Turn ? numberOfShots : numberOfShots2));
        }
    }

    /**
     * Update the Directional Arrow.
     * Used for the ball's direction.
     */
    private void updateDirectionalArrow() {
        String vx = guiElementManager.getVxTextField().getText().replaceAll("[a-zA-Z]", "");
        String vz = guiElementManager.getVzTextField().getText().replaceAll("[a-zA-Z]", "");

        if (vx.isEmpty() || vz.isEmpty()) return;
        if (vx.equals("Enter vx") || vz.equals("Enter vz")) return;
        if (isNotValidFloat(vx) || isNotValidFloat(vz)) return;

        float vxValue = Float.parseFloat(vx);
        float vzValue = Float.parseFloat(vz);

        Vector3f position = entitiesManager.getCurrentBall().getPosition();
        Vector3f rotation = entitiesManager.getArrowEntity().getRotation();

        // Calculate the direction vector from vx and vz
        Vector3f direction = new Vector3f(vxValue, 0, vzValue).normalize();

        // Compute the y rotation based on the direction vector
        float yRotation = (float) Math.toDegrees(Math.atan2(direction.x, direction.z));

        // Update the arrow entity's position and rotation
        entitiesManager.setArrowEntityPosition(new Vector3f(position.x, position.y + .5f, position.z));
        entitiesManager.setArrowEntityRotation(new Vector3f(rotation.x, yRotation - 90, rotation.z));
    }

    /**
     * Checks if the string is a valid float.
     *
     * @param str The string to check.
     * @return True if the string is not a valid float, false otherwise.
     */
    private boolean isNotValidFloat(String str) {
        try {
            Float.parseFloat(str);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
