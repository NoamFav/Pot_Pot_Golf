package com.um_project_golf.Core;

import com.um_project_golf.Core.Entity.Material;
import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
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
     * Creates a directional light uniform.
     *
     * @param uniformName The name of the uniform.
     * @throws Exception If the uniform could not be created.
     */
    public void createDirectionalLightUniform(String uniformName) throws Exception {
        // For the uniform of the directional light
        createUniform(uniformName + ".color"); // Create the color uniform
        createUniform(uniformName + ".direction"); // Create the direction uniform
        createUniform(uniformName + ".intensity"); // Create the intensity uniform
    }

    /**
     * Creates a point light uniform.
     *
     * @param uniformName The name of the uniform.
     * @throws Exception If the uniform could not be created.
     */
    public void createPointLightUniform(String uniformName) throws Exception {
        // For the uniform of the point light
        createUniform(uniformName + ".color"); // Create the color uniform
        createUniform(uniformName + ".position"); // Create the position uniform
        createUniform(uniformName + ".intensity"); // Create the intensity uniform
        createUniform(uniformName + ".constant"); // Create the constant uniform
        createUniform(uniformName + ".linear"); // Create the linear uniform
        createUniform(uniformName + ".exponent"); // Create the exponent uniform
    }

    /**
     * Creates a point light list uniform.
     *
     * @param uniformName The name of the uniform.
     * @param size The size of the uniform.
     * @throws Exception If the uniform could not be created.
     */
    public void createPointLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) { // For each point light
            createPointLightUniform(uniformName + "[" + i + "]"); // Create the point light uniform
        }
    }

    /**
     * Creates a spotlight uniform.
     *
     * @param uniformName The name of the uniform.
     * @throws Exception If the uniform could not be created.
     */
    public void createSpotLightUniform(String uniformName) throws Exception {
        // For the uniform of the spotlight
        createPointLightUniform(uniformName + ".pointLight"); // Create the point light uniform
        createUniform(uniformName + ".coneDirection"); // Create the cone direction uniform
        createUniform(uniformName + ".cutoff"); // Create the cutoff uniform
    }

    /**
     * Creates a spotlight list uniform.
     *
     * @param uniformName The name of the uniform.
     * @param size The size of the uniform.
     * @throws Exception If the uniform could not be created.
     */
    public void createSpotLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) { // For each spotlight
            createSpotLightUniform(uniformName + "[" + i + "]"); // Create the spotlight uniform
        }
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
    @SuppressWarnings("unused")
    public void setUniform(String uniformName, boolean value) {
        GL20.glUniform1f(uniforms.get(uniformName), value ? 1 : 0); // Set the uniform
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
     * Sets the uniforms of a point light.
     *
     * @param uniformName The name of the uniform.
     * @param pointLight The point light to set.
     */
    public void setUniform(String uniformName, @NotNull PointLight pointLight) {
        // For the uniform of the point light
        setUniform(uniformName + ".color", pointLight.getColor()); // Set the color uniform
        setUniform(uniformName + ".position", pointLight.getPosition()); // Set the position uniform
        setUniform(uniformName + ".intensity", pointLight.getIntensity()); // Set the intensity uniform
        setUniform(uniformName + ".constant", pointLight.getConstant()); // Set the constant uniform
        setUniform(uniformName + ".linear", pointLight.getLinear()); // Set the linear uniform
        setUniform(uniformName + ".exponent", pointLight.getExponent()); // Set the exponent uniform
    }

    /**
     * Sets the uniforms of a direction Light.
     *
     * @param uniformName The name of the uniform.
     * @param directionalLight The directional light to set.
     */
    public void setUniform(String uniformName, @NotNull DirectionalLight directionalLight) {
        // For the uniform of the directional light
        setUniform(uniformName + ".color", directionalLight.getColor()); // Set the color uniform
        setUniform(uniformName + ".direction", directionalLight.getDirection()); // Set the direction uniform
        setUniform(uniformName + ".intensity", directionalLight.getIntensity()); // Set the intensity uniform
    }

    /**
     * Sets the uniforms of a spotlight.
     *
     * @param uniformName The name of the uniform.
     * @param spotLight The spotlight to set.
     */
    public void setUniform(String uniformName, @NotNull SpotLight spotLight) {
        // For the uniform of the spotlight
        setUniform(uniformName + ".pointLight", spotLight.getPointLight()); // Set the point light uniform
        setUniform(uniformName + ".coneDirection", spotLight.getConeDirection()); // Set the cone direction uniform
        setUniform(uniformName + ".cutoff", spotLight.getCutOff()); // Set the cutoff uniform
    }

    /**
     * Sets the uniforms of a point light list.
     * @param uniformName The name of the uniform.
     * @param pointLights The point lights to set.
     */
    @SuppressWarnings("unused")
    public void setUniform(String uniformName, PointLight[] pointLights) {
        // For the uniform of the point light list
        int numLights = pointLights != null ? pointLights.length : 0; // Get the number of point lights
        setUniform(uniformName + ".numLights", numLights); // Set the number of point lights
        for (int i = 0; i < numLights; i++) { // For each point light
            setUniform(uniformName, pointLights[i], i); // Set the point light uniform
        }
    }

    /**
     * Sets the uniforms of a point light list.
     * @param uniformName The name of the uniform.
     * @param pointLight The point light to set.
     * @param pos The position of the point light.
     */
    public void setUniform(String uniformName, PointLight pointLight, int pos) {
        setUniform(uniformName + "[" + pos + "]", pointLight); // Set the point light uniform
    }

    /**
     * Sets the uniforms of a spotlight list.
     * @param uniformName The name of the uniform.
     * @param spotLights The spotlights to set.
     */
    @SuppressWarnings("unused")
    public void setUniform(String uniformName, SpotLight[] spotLights) {
        // For the uniform of the spotlight list
        int numLights = spotLights != null ? spotLights.length : 0; // Get the number of spotlights
        setUniform(uniformName + ".numLights", numLights); // Set the number of spotlights
        for (int i = 0; i < numLights; i++) { // For each spotlight
            setUniform(uniformName, spotLights[i], i); // Set the spotlight uniform
        }
    }

    /**
     * Sets the uniforms of a spotlight.
     * @param uniformName The name of the uniform.
     * @param spotLight The spotlight to set.
     * @param pos The position of the spotlight.
     */
    public void setUniform(String uniformName, SpotLight spotLight, int pos) {
        setUniform(uniformName + "[" + pos + "]", spotLight); // Set the spotlight uniform
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
