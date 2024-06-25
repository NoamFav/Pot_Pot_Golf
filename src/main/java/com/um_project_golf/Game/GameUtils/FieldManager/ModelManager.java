package com.um_project_golf.Game.GameUtils.FieldManager;

import com.um_project_golf.Core.Entity.Model;

import java.util.List;

/**
 * The model manager class.
 * This class is responsible for managing the models of the game.
 * Stores the models of the game.
 */
public class ModelManager {

    // Models
    private List<Model> botBallModel;
    private List<Model> aiBotBallModel;
    private List<Model> ball2;
    private List<Model> tree;
    private List<Model> tree2;

    public List<Model> getBotBallModel() {return botBallModel;}
    public void setBotBallModel(List<Model> botBallModel) {this.botBallModel = botBallModel;}
    public List<Model> getAiBotBallModel() {return aiBotBallModel;}
    public void setAiBotBallModel(List<Model> aiBotBallModel) {this.aiBotBallModel = aiBotBallModel;}
    public List<Model> getBall2() {return ball2;}
    public void setBall2(List<Model> ball2) {this.ball2 = ball2;}
    public List<Model> getTree() {return tree;}
    public void setTree(List<Model> tree) {this.tree = tree;}
    public List<Model> getTree2() {return tree2;}
    public void setTree2(List<Model> tree2) {this.tree2 = tree2;}
}
