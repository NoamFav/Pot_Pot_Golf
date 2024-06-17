module com.um_project_golf {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires exp4j;
    requires org.lwjgl.glfw;
    requires org.lwjgl.opengl;
    requires org.joml;
    requires org.apache.logging.log4j;
    requires org.lwjgl.stb;
    requires org.lwjgl.assimp;
    requires lwjgl3.awt;
    requires java.desktop;
    requires org.lwjgl.nanovg;
    requires org.lwjgl.openal;
    requires org.jetbrains.annotations;

    opens com.um_project_golf to javafx.fxml;
    opens com.um_project_golf.Core to javafx.fxml;
    opens com.um_project_golf.Phase1 to javafx.graphics;
    exports com.um_project_golf.Core;
    exports com.um_project_golf.Game;
    exports com.um_project_golf.Core.GolfBots;
    exports com.um_project_golf.Phase1;
    exports com.um_project_golf.Core.Entity;
    exports com.um_project_golf.Core.Utils;
    exports com.um_project_golf.Core.Lighting;
    exports com.um_project_golf.Core.Rendering;
    exports com.um_project_golf.Core.Entity.Terrain;
    exports com.um_project_golf.Core.AWT;
    opens com.um_project_golf.Core.Utils to javafx.fxml;
    exports com.um_project_golf.Game.GameUtils.GameLogic;
    opens com.um_project_golf.Game.GameUtils.GameLogic to javafx.fxml;
    exports com.um_project_golf.Game.GameUtils.FieldManager;
    opens com.um_project_golf.Game.GameUtils.FieldManager to javafx.fxml;
    exports com.um_project_golf.Game.GameUtils;
    opens com.um_project_golf.Game.GameUtils to javafx.fxml;
}