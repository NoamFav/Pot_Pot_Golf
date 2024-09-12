package com.pot_pot_golf.Core;

import com.pot_pot_golf.Core.Entity.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

/**
 * The shader manager class.
 * This class is responsible for creating and managing the shaders of the game.
 */
public class ShaderManager {

    private final int programID; // The ID of the program
    private int vertexShaderID, fragmentShaderID; // The IDs of the shaders

    private final Map<String, Integer> uniforms; // The map of uniforms

    /**
     * The constructor of the shader manager.
     * It initializes the program ID and the uniforms.
     *
     * @throws Exception If the shader could not be created.
     */
    public ShaderManager() throws Exception {
        programID = GL20.glCreateProgram(); // Create the program
        if (programID == 0) { // If the program could not be created
            throw new Exception("Could not create Shader"); // Throw an exception
        }

        uniforms = new HashMap<>(); // Initialize the uniforms
    }

    /**
     * Creates a uniform.
     *
     * @param uniformName The name of the uniform.
     * @throws Exception If the uniform could not be created.
     */
    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = GL20.glGetUniformLocation(programID, uniformName); // Get the location of the uniform
        if (uniformLocation < 0) { // If the location is invalid
            throw new Exception("Could not find uniform: " + uniformName); // Throw an exception
        }
        uniforms.put(uniformName, uniformLocation); // Add the uniform to the map of uniforms
    }

    /**
     * Creates a uniform.
     *
     * @param uniformName The name of the uniform.
     * @throws Exception If the uniform could not be created.
     */
    public void createMaterialUniform(String uniformName) throws Exception {
        // For the uniform of the material
        createUniform(uniformName + ".ambient"); // Create the ambient uniform
        createUniform(uniformName + ".diffuse"); // Create the diffuse uniform
        createUniform(uniformName + ".specular"); // Create the specular uniform
        createUniform(uniformName + ".hasTexture"); // Create the has texture uniform
        createUniform(uniformName + ".reflectance"); // Create the reflectance uniform
    }

    /**
     * Sets a uniform.
     *
     * @param uniformName The name of the uniform.
     * @param value The value of the uniform.
     */
    public void setUniform(String uniformName, @NotNull Matrix4f value) {
        try(MemoryStack stack = MemoryStack.stackPush()) { // Try with resources
            GL20.glUniformMatrix4fv(uniforms.get(uniformName), false, value.get(stack.mallocFloat(16))); // Set the uniform
        }
    }

    /**
     * Sets a uniform.
     *
     * @param uniformName The name of the uniform.
     * @param value The value of the uniform.
     */
    public void setUniform(String uniformName, @NotNull Vector4f value) {
        GL20.glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w); // Set the uniform
    }

    /**
     * Sets a uniform.
     *
     * @param uniformName The name of the uniform.
     * @param value The value of the uniform.
     */
    public void setUniform(String uniformName, @NotNull Vector3f value) {
        GL20.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z); // Set the uniform
    }

    /**
     * Sets a uniform.
     *
     * @param uniformName The name of the uniform.
     * @param value The value of the uniform.
     */
    public void setUniform(String uniformName, int value) {
        GL20.glUniform1i(uniforms.get(uniformName), value); // Set the uniform
    }

    /**
     * Sets a uniform.
     *
     * @param uniformName The name of the uniform.
     * @param value The value of the uniform.
     */
    public void setUniform(String uniformName, float value) {
        GL20.glUniform1f(uniforms.get(uniformName), value); // Set the uniform
    }

    /**
     * Sets the uniforms of a material.
     *
     * @param uniformName The name of the uniform.
     * @param material The material to set.
     */
    public void setUniform(String uniformName, @NotNull Material material) {
        // For the uniform of the material
        setUniform(uniformName + ".ambient", material.getAmbientColor()); // Set the ambient color uniform
        setUniform(uniformName + ".diffuse", material.getDiffuseColor()); // Set the diffuse color uniform
        setUniform(uniformName + ".specular", material.getSpecularColor()); // Set the specular color uniform
        setUniform(uniformName + ".hasTexture", material.hasTexture() ? 1 : 0); // Set the hasTexture uniform
        setUniform(uniformName + ".reflectance", material.getReflectance()); // Set the reflectance uniform
    }

    /**
     * Creates a vertex shader.
     *
     * @param shaderCode The code of the shader.
     * @throws Exception If the shader could not be created.
     */
    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER); // Create the vertex shader
    }

    /**
     * Creates a fragment shader.
     *
     * @param shaderCode The code of the shader.
     * @throws Exception If the shader could not be created.
     */
    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderID = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER); // Create the fragment shader
    }

    /**
     * Creates a shader.
     *
     * @param shaderCode The code of the shader.
     * @param type The type of the shader.
     * @return The ID of the shader.
     * @throws Exception If the shader could not be created.
     */
    public int createShader(String shaderCode, int type) throws Exception {
        int shaderID = GL20.glCreateShader(type); // Create the shader
        if (shaderID == 0) { // If the shader could not be created
            throw new Exception("Error creating shader. Type: " + type); // Throw an exception
        }

        GL20.glShaderSource(shaderID, shaderCode); // Set the source of the shader
        GL20.glCompileShader(shaderID); // Compile the shader

        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0) { // If the shader could not be compiled
            throw new Exception("Error compiling Shader code: " + GL20.glGetShaderInfoLog(shaderID, 1024)); // Throw an exception
        }

        GL20.glAttachShader(programID, shaderID); // Attach the shader to the program

        return shaderID;
    }

    /**
     * Links the shader.
     *
     * @throws Exception If the shader could not be linked.
     */
    public void link() throws Exception {
        GL20.glLinkProgram(programID); // Link the program
        if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0) { // If the program could not be linked
            throw new Exception("Error linking Shader code: " + GL20.glGetProgramInfoLog(programID, 1024)); // Throw an exception
        }

        if (vertexShaderID != 0) { // If the vertex shader is not null
            GL20.glDetachShader(programID, vertexShaderID); // Detach the vertex shader
        }

        if (fragmentShaderID != 0) { // If the fragment shader is not null
            GL20.glDetachShader(programID, fragmentShaderID); // Detach the fragment shader
        }

        int vaoID = GL30.glGenVertexArrays(); // Generate a vertex array object
        GL30.glBindVertexArray(vaoID); // Bind the vertex array object

        GL20.glValidateProgram(programID); // Validate the program
        if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0) { // If the program could not be validated
            System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(programID, 1024)); // Print a warning
        }

        GL30.glBindVertexArray(0); // Unbind the vertex array object
    }

    /**
     * Binds the shader.
     */
    public void bind() {
        GL20.glUseProgram(programID); // Use the program to render
    }

    /**
     * Unbinds the shader.
     */
    public void unbind() {
        GL20.glUseProgram(0); // Stop using the program to render the scene
    }

    /**
     * Cleans up the shader.
     */
    public void cleanup() {
        unbind(); // Unbind the shader
        if (programID != 0) { // If the program is not null
            GL20.glDeleteProgram(programID); // Delete the program
        }
    }
}
