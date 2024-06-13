package com.um_project_golf.Game.GUIs;

import com.um_project_golf.Core.*;
import com.um_project_golf.Core.Entity.SceneManager;
import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Core.Entity.Terrain.HeightMapPathfinder;
import com.um_project_golf.Core.Utils.StartEndPoint;
import com.um_project_golf.Core.Utils.TerrainSwitch;
import com.um_project_golf.Game.FieldManager.*;

public class RecreateGUIs {
    /**
     * Recreates the GUIs.
     */
    public RecreateGUIs(GuiElementManager guiElementManager, Camera camera,
                         WindowManager window, long vg, GameStateManager gameStateManager,
                         ModelManager modelManager, PathManager pathManager,
                         EntitiesManager entitiesManager, GameVarManager gameVarManager,
                         HeightMap heightMap, HeightMapPathfinder pathfinder, AudioManager audioManager,
                         SceneManager scene, TerrainManager terrainManager, ObjectLoader loader,
                         MouseInput mouseInputs, TerrainSwitch terrainSwitch,
                         StartEndPoint startEndPoint) {
        guiElementManager.clearMenuButtons();
        guiElementManager.clearInGameMenuButtons();

        new MenuGUI(camera, window, vg, guiElementManager,
                gameStateManager, modelManager, pathManager, terrainManager, entitiesManager,
                gameVarManager, heightMap, pathfinder, audioManager, scene,
                terrainManager.getBlendMapTerrain(), loader, mouseInputs,
                terrainSwitch, startEndPoint);

        new InGameGUI(vg, window, camera, audioManager,
                guiElementManager, gameStateManager, gameVarManager,
                entitiesManager);

        new DefaultGUI(window, vg, heightMap, scene,
                gameStateManager, entitiesManager, gameVarManager, guiElementManager);

    }
}
