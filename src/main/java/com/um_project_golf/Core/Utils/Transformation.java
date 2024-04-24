package com.um_project_golf.Core.Utils;

import com.um_project_golf.Core.Camera;
import com.um_project_golf.Core.Entity.Entity;
import com.um_project_golf.Core.Entity.Terrain.Terrain;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * The transformation class.
 * This class is responsible for the transformations of the game.
 */
public class Transformation {

    /**
     * Creates a transformation matrix.
     *
     * @param entity The entity to create the transformation matrix.
     * @return The transformation matrix.
     */
    public static Matrix4f createTransformationMatrix(Entity entity) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity().translate(entity.getPos()).
                rotateX((float) Math.toRadians(entity.getRotation().x)).
                rotateY((float) Math.toRadians(entity.getRotation().y)).
                rotateZ((float) Math.toRadians(entity.getRotation().z)).
                scale(entity.getScale());
        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Terrain terrain) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity().translate(terrain.getPosition()).scale(1);
        return matrix;
    }

    /**
     * Creates a view matrix.
     *
     * @param camera The camera to create the view matrix.
     * @return The view matrix.
     */
    public static Matrix4f getViewMatrix(Camera camera) {
       Vector3f cameraPos = camera.getPosition();
       Vector3f rotation = camera.getRotation();
       Matrix4f viewMatrix = new Matrix4f();
       viewMatrix.identity()
               .rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
               .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
               .rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1))
               .translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
       return viewMatrix;
    }
}
