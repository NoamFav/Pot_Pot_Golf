module com.um_project_golf {

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

    exports com.um_project_golf.Core;
    exports com.um_project_golf.Game;
    exports com.um_project_golf.Core.GolfBots;
    exports com.um_project_golf.Core.Entity;
    exports com.um_project_golf.Core.Utils;
    exports com.um_project_golf.Core.Lighting;
    exports com.um_project_golf.Core.Rendering;
    exports com.um_project_golf.Core.Entity.Terrain;
    exports com.um_project_golf.Core.AWT;
    exports com.um_project_golf.Game.GameUtils.GameLogic;
    exports com.um_project_golf.Game.GameUtils.FieldManager;
    exports com.um_project_golf.Game.GameUtils;
}