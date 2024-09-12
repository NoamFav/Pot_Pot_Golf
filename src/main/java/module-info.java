module com.pot_pot_golf {

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

    exports com.pot_pot_golf.Core;
    exports com.pot_pot_golf.Game;
    exports com.pot_pot_golf.Core.GolfBots;
    exports com.pot_pot_golf.Core.Entity;
    exports com.pot_pot_golf.Core.Utils;
    exports com.pot_pot_golf.Core.Rendering;
    exports com.pot_pot_golf.Core.Entity.Terrain;
    exports com.pot_pot_golf.Core.AWT;
    exports com.pot_pot_golf.Game.GameUtils.GameLogic;
    exports com.pot_pot_golf.Game.GameUtils.FieldManager;
    exports com.pot_pot_golf.Game.GameUtils;
}
