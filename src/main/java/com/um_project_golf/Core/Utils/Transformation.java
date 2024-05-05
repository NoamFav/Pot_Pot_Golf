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
        Matrix4f matrix = new Matrix4f(); // Create a new matrix.
        matrix.identity().translate(entity.getPos()) // Translate the matrix.
                .rotateX((float) Math.toRadians(entity.getRotation().x)) // Rotate the matrix on the x-axis.
                .rotateY((float) Math.toRadians(entity.getRotation().y)) // Rotate the matrix on the y-axis.
                .rotateZ((float) Math.toRadians(entity.getRotation().z)) // Rotate the matrix on the z-axis.
                .scale(entity.getScale()); // Scale the matrix.
        return matrix; // Return the matrix.
    }

    public static Matrix4f createTransformationMatrix(Terrain terrain) {
        Matrix4f matrix = new Matrix4f(); // Create a new matrix.
        matrix.identity().translate(terrain.getPosition()).scale(1); // Translate and scale the matrix.
        return matrix; // Return the matrix.
    }

    /**
     * Creates a view matrix.
     *
     * @param camera The camera to create the view matrix.
     * @return The view matrix.
     */
    public static Matrix4f getViewMatrix(Camera camera) {
       Vector3f cameraPos = camera.getPosition(); // Get the camera position.
       Vector3f rotation = camera.getRotation(); // Get the camera rotation.
       Matrix4f viewMatrix = new Matrix4f(); // Create a new matrix.
       viewMatrix.identity() // Set the matrix to identity.
               .rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0)) // Rotate the matrix on the x-axis.
               .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0)) // Rotate the matrix on the y-axis.
               .rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1)) // Rotate the matrix on the z-axis.
               .translate(-cameraPos.x, -cameraPos.y, -cameraPos.z); // Translate the matrix.
       return viewMatrix; // Return the matrix.
    }
}
