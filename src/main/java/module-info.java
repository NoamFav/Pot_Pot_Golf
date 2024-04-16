module com.example.um_project_golf {
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

    opens com.um_project_golf to javafx.fxml;
    exports com.um_project_golf.Core;
    exports com.um_project_golf.Game;
    exports com.um_project_golf.Core.Entity;
    exports com.um_project_golf.Core.Utils;
    opens com.um_project_golf.Core to javafx.fxml;
}