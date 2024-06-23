package com.um_project_golf.Core.Utils;

import com.um_project_golf.Core.Entity.Terrain.HeightMap;
import com.um_project_golf.Game.GameUtils.Consts;
import com.um_project_golf.Game.GameUtils.FieldManager.PathManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;

public class StartEndPoint {
    /**
     * Convert distance from meters to vertices.
     */
    public void startEndPointConversion(@NotNull PathManager pathManager, @NotNull HeightMap heightMap) {
        List<Vector2i> path = pathManager.getPath();
        float factor = Consts.VERTEX_COUNT / Consts.SIZE_X;
        Vector3f startPoint = new Vector3f(path.get(0).x, 0, path.get(0).y);
        startPoint.x = (int) (startPoint.x / factor - Consts.SIZE_X / 2);
        startPoint.z = (int) (startPoint.z / factor - Consts.SIZE_Z / 2);
        startPoint.y = heightMap.getHeight(new Vector3f(startPoint.x, 0, startPoint.z));
        pathManager.setStartPoint(startPoint);

        Vector3f endPoint = new Vector3f(path.get(path.size() - 1).x, 0, path.get(path.size() - 1).y);
        endPoint.x = (int) (endPoint.x / factor) - Consts.SIZE_X / 2;
        endPoint.z = (int) (endPoint.z / factor) - Consts.SIZE_Z / 2;
        endPoint.y = heightMap.getHeight(new Vector3f(endPoint.x, 0, endPoint.z));
        pathManager.setEndPoint(endPoint);
    }

}
