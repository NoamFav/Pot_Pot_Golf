package com.um_project_golf.Game.GameUtils.FieldManager;

import com.um_project_golf.Core.Entity.Entity;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class EntitiesManager {
    // EntitiesManager
    private final List<Entity> trees;
    private final List<Float> treeHeights;
    private Entity golfBall;
    private Entity golfBall2;
    private Entity currentBall;
    private Entity botBall;
    private Entity aiBotBall;
    private Entity endFlag;
    private Entity arrowEntity;

    public EntitiesManager() {
        this.trees = new ArrayList<>();
        this.treeHeights = new ArrayList<>();
    }

    public List<Entity> getTrees() {return trees;}
    public void addTree(Entity tree) {this.trees.add(tree);}
    public void clearTrees() {this.trees.clear();}
    public List<Float> getTreeHeights() {return treeHeights;}
    public void addTreeHeight(Float height) {this.treeHeights.add(height);}
    public void clearTreeHeights() {this.treeHeights.clear();}
    public Entity getGolfBall() {return golfBall;}
    public void setGolfBall(Entity golfBall) {this.golfBall = golfBall;}
    public void setGolfBallPosition(Vector3f position) {this.golfBall.setPosition(position);}
    public Entity getGolfBall2() {return golfBall2;}
    public void setGolfBall2(Entity golfBall2) {this.golfBall2 = golfBall2;}
    public void setGolfBall2Position(Vector3f position) {this.golfBall2.setPosition(position);}
    public void updateCurrentBall(boolean isPlayer1Turn) {currentBall = isPlayer1Turn ? golfBall : golfBall2;}
    public Entity getCurrentBall() {return currentBall;}
    public void setCurrentBall(Entity currentBall) {this.currentBall = currentBall;}
    public void setCurrentBallPosition(Vector3f position) {this.currentBall.setPosition(position);}
    public Entity getBotBall() {return botBall;}
    public void setBotBall(Entity botBall) {this.botBall = botBall;}
    public Entity getAiBotBall() {return aiBotBall;}
    public void setAiBotBall(Entity aiBotBall) {this.aiBotBall = aiBotBall;}
    public Entity getEndFlag() {return endFlag;}
    public void setEndFlag(Entity endFlag) {this.endFlag = endFlag;}
    public void setEndFlagPosition(Vector3f position) {this.endFlag.setPosition(position);}
    public Entity getArrowEntity() {return arrowEntity;}
    public void setArrowEntity(Entity arrowEntity) {this.arrowEntity = arrowEntity;}
    public void setArrowEntityPosition(Vector3f position) {this.arrowEntity.setPosition(position);}
    public void setArrowEntityRotation(Vector3f rotation) {this.arrowEntity.setRotation(rotation);}
}
