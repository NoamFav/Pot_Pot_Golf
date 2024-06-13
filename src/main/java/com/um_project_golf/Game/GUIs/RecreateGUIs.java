package com.um_project_golf.Game.GUIs;

import com.um_project_golf.Game.FieldManager.*;
import org.jetbrains.annotations.NotNull;

public class RecreateGUIs {
    /**
     * Recreates the GUIs.
     */
    public RecreateGUIs(long vg, @NotNull MainFieldManager context) {

        context.getGuiElementManager().clearMenuButtons();
        context.getGuiElementManager().clearInGameMenuButtons();

        new MenuGUI(vg, context);

        new InGameGUI(vg, context);

        new DefaultGUI(vg, context);

    }
}
