package com.um_project_golf.Game.FieldManager;

import com.um_project_golf.Core.Utils.AnimationState;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class GameVarManager {

    // Game logic variables
    private AnimationState treeAnimationState = AnimationState.IDLE;
    private float treeAnimationTime = 0f;
    private int numberOfShots;
    private int numberOfShots2;
    private int currentPositionIndex;
    private float animationTimeAccumulator;
    private Vector3f shotStartPosition;

    // Positions of the balls for Animation purposes
    private List<Vector3f> ballPositions = new ArrayList<>();

    // Path for the bots, not used currently as a problem with movement issue and threading issues.
    @SuppressWarnings("unused")
    private List<List<Vector3f>> botPath; // Path followed by the bot
    @SuppressWarnings("unused")
    private List<List<Vector3f>> aiBotPath; // Path followed by AI bot

    public boolean isTreeAnimationIdle() {return treeAnimationState == AnimationState.IDLE;}
    public boolean isTreeAnimationGoingUp() {return treeAnimationState == AnimationState.GOING_UP;}
    public boolean isTreeAnimationGoingDown() {return treeAnimationState == AnimationState.GOING_DOWN;}
    public void setTreeAnimationGoingUp() {treeAnimationState = AnimationState.GOING_UP;}
    public void setTreeAnimationGoingDown() {treeAnimationState = AnimationState.GOING_DOWN;}
    public void setTreeAnimationIdle() {treeAnimationState = AnimationState.IDLE;}
    public float getTreeAnimationTime() {return treeAnimationTime;}
    public void setTreeAnimationTime(float treeAnimationTime) {this.treeAnimationTime = treeAnimationTime;}
    public void resetTreeAnimationTime() {treeAnimationTime = 0f;}
    public void incrementTreeAnimationTime(float delta) {treeAnimationTime += delta;}
    public int getNumberOfShots() {return numberOfShots;}
    public void setNumberOfShots(int numberOfShots) {this.numberOfShots = numberOfShots;}
    public void incrementNumberOfShots() {numberOfShots++;}
    public int getNumberOfShots2() {return numberOfShots2;}
    public void setNumberOfShots2(int numberOfShots2) {this.numberOfShots2 = numberOfShots2;}
    public void incrementNumberOfShots2() {numberOfShots2++;}
    public void resetNumberOfShots() {numberOfShots = 0;numberOfShots2 = 0;}
    public int getCurrentPositionIndex() {return currentPositionIndex;}
    public void setCurrentPositionIndex(int currentPositionIndex) {this.currentPositionIndex = currentPositionIndex;}
    public void incrementCurrentPositionIndex() {currentPositionIndex++;}
    public void resetCurrentPositionIndex() {currentPositionIndex = 0;}
    public float getAnimationTimeAccumulator() {return animationTimeAccumulator;}
    public void setAnimationTimeAccumulator(float animationTimeAccumulator) {this.animationTimeAccumulator = animationTimeAccumulator;}
    public void incrementAnimationTimeAccumulator(float delta) {animationTimeAccumulator += delta;}
    public void decrementAnimationTimeAccumulator(float delta) {animationTimeAccumulator -= delta;}
    public void resetAnimationTimeAccumulator() {animationTimeAccumulator = 0f;}
    public Vector3f getShotStartPosition() {return shotStartPosition;}
    public void setShotStartPosition(Vector3f shotStartPosition) {this.shotStartPosition = shotStartPosition;}
    public List<Vector3f> getBallPositions() {return ballPositions;}
    public void setBallPositions(List<Vector3f> ballPositions) {this.ballPositions = ballPositions;}
    public List<List<Vector3f>> getAiBotPath() {return aiBotPath;}
    public void setAiBotPath(List<List<Vector3f>> aiBotPath) {this.aiBotPath = aiBotPath;}
    public List<List<Vector3f>> getBotPath() {return botPath;}
    public void setBotPath(List<List<Vector3f>> botPath) {this.botPath = botPath;}
}
