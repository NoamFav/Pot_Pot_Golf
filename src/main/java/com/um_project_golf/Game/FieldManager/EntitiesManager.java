package com.um_project_golf.Game.FieldManager;

import com.um_project_golf.Core.Entity.Entity;

import java.util.ArrayList;
import java.util.List;

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

    public List<Entity> getTrees() {
        return trees;
    }

    public List<Float> getTreeHeights() {
        return treeHeights;
    }

    public Entity getGolfBall() {
        return golfBall;
    }

    public void setGolfBall(Entity golfBall) {
        this.golfBall = golfBall;
    }

    public Entity getGolfBall2() {
        return golfBall2;
    }

    public void setGolfBall2(Entity golfBall2) {
        this.golfBall2 = golfBall2;
    }

    public Entity getCurrentBall() {
        return currentBall;
    }

    public void setCurrentBall(Entity currentBall) {
        this.currentBall = currentBall;
    }

    public Entity getBotBall() {
        return botBall;
    }

    public void setBotBall(Entity botBall) {
        this.botBall = botBall;
    }

    public Entity getAiBotBall() {
        return aiBotBall;
    }

    public void setAiBotBall(Entity aiBotBall) {
        this.aiBotBall = aiBotBall;
    }

    public Entity getEndFlag() {
        return endFlag;
    }

    public void setEndFlag(Entity endFlag) {
        this.endFlag = endFlag;
    }

    public Entity getArrowEntity() {
        return arrowEntity;
    }

    public void setArrowEntity(Entity arrowEntity) {
        this.arrowEntity = arrowEntity;
    }
}
