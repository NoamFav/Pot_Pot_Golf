package com.um_project_golf.Core;

import com.um_project_golf.Core.Entity.Material;
import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;
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

    private final int programID;
    private int vertexShaderID, fragmentShaderID;

    private final Map<String, Integer> uniforms;

    /**
     * The constructor of the shader manager.
     * It initializes the program ID and the uniforms.
     *
     * @throws Exception If the shader could not be created.
     */
    public ShaderManager() throws Exception {
        programID = GL20.glCreateProgram();
        if (programID == 0) {
            throw new Exception("Could not create Shader");
        }

        uniforms = new HashMap<>();
    }

    /**
     * Creates a uniform.
     *
     * @param uniformName The name of the uniform.
     * @throws Exception If the uniform could not be created.
     */
    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = GL20.glGetUniformLocation(programID, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform: " + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public void createDirectionalLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    public void createPointLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".constant");
        createUniform(uniformName + ".linear");
        createUniform(uniformName + ".exponent");
    }

    public void createSpotLightUniform(String uniformName) throws Exception {
        createPointLightUniform(uniformName + ".pointLight");
        createUniform(uniformName + ".coneDirection");
        createUniform(uniformName + ".cutoff");
    }

    /**
     * Creates a uniform.
     *
     * @param uniformName The name of the uniform.
     * @throws Exception If the uniform could not be created.
     */
    public void createMaterialUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");
    }

    /**
     * Sets a uniform.
     *
     * @param uniformName The name of the uniform.
     * @param value The value of the uniform.
     */
    public void setUniform(String uniformName, Matrix4f value) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            GL20.glUniformMatrix4fv(uniforms.get(uniformName), false, value.get(stack.mallocFloat(16)));
        }
    }

    /**
     * Sets a uniform.
     *
     * @param uniformName The name of the uniform.
     * @param value The value of the uniform.
     */
    public void setUniform(String uniformName, Vector4f value) {
        GL20.glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    /**
     * Sets a uniform.
     *
     * @param uniformName The name of the uniform.
     * @param value The value of the uniform.
     */
    public void setUniform(String uniformName, Vector3f value) {
        GL20.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    /**
     * Sets a uniform.
     *
     * @param uniformName The name of the uniform.
     * @param value The value of the uniform.
     */
    public void setUniform(String uniformName, boolean value) {
        GL20.glUniform1f(uniforms.get(uniformName), value ? 1 : 0);
    }

    /**
     * Sets a uniform.
     *
     * @param uniformName The name of the uniform.
     * @param value The value of the uniform.
     */
    public void setUniform(String uniformName, int value) {
        GL20.glUniform1i(uniforms.get(uniformName), value);
    }

    /**
     * Sets a uniform.
     *
     * @param uniformName The name of the uniform.
     * @param value The value of the uniform.
     */
    public void setUniform(String uniformName, float value) {
        GL20.glUniform1f(uniforms.get(uniformName), value);
    }

    /**
     * Sets the uniforms of a material.
     *
     * @param uniformName The name of the uniform.
     * @param material The material to set.
     */
    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".ambient", material.getAmbientColor());
        setUniform(uniformName + ".diffuse", material.getDiffuseColor());
        setUniform(uniformName + ".specular", material.getSpecularColor());
        setUniform(uniformName + ".hasTexture", material.hasTexture() ? 1 : 0);
        setUniform(uniformName + ".reflectance", material.getReflectance());
    }

    public void setUniform(String uniformName, PointLight pointLight) {
        setUniform(uniformName + ".color", pointLight.getColor());
        setUniform(uniformName + ".position", pointLight.getPosition());
        setUniform(uniformName + ".intensity", pointLight.getIntensity());
        setUniform(uniformName + ".constant", pointLight.getConstant());
        setUniform(uniformName + ".linear", pointLight.getLinear());
        setUniform(uniformName + ".exponent", pointLight.getExponent());
    }

    public void setUniform(String uniformName, DirectionalLight directionalLight) {
        setUniform(uniformName + ".color", directionalLight.getColor());
        setUniform(uniformName + ".direction", directionalLight.getDirection());
        setUniform(uniformName + ".intensity", directionalLight.getIntensity());
    }

    public void setUniform(String uniformName, SpotLight spotLight) {
        setUniform(uniformName + ".pointLight", spotLight.getPointLight());
        setUniform(uniformName + ".coneDirection", spotLight.getConeDirection());
        setUniform(uniformName + ".cutoff", spotLight.getCutOff());
    }

    /**
     * Creates a vertex shader.
     *
     * @param shaderCode The code of the shader.
     * @throws Exception If the shader could not be created.
     */
    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    /**
     * Creates a fragment shader.
     *
     * @param shaderCode The code of the shader.
     * @throws Exception If the shader could not be created.
     */
    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderID = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
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
        int shaderID = GL20.glCreateShader(type);
        if (shaderID == 0) {
            throw new Exception("Error creating shader. Type: " + type);
        }

        GL20.glShaderSource(shaderID, shaderCode);
        GL20.glCompileShader(shaderID);

        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + GL20.glGetShaderInfoLog(shaderID, 1024));
        }

        GL20.glAttachShader(programID, shaderID);

        return shaderID;
    }

    /**
     * Links the shader.
     *
     * @throws Exception If the shader could not be linked.
     */
    public void link() throws Exception {
        GL20.glLinkProgram(programID);
        if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + GL20.glGetProgramInfoLog(programID, 1024));
        }

        if (vertexShaderID != 0) {
            GL20.glDetachShader(programID, vertexShaderID);
        }

        if (fragmentShaderID != 0) {
            GL20.glDetachShader(programID, fragmentShaderID);
        }

        int vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        GL20.glValidateProgram(programID);
        if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(programID, 1024));
        }

        GL30.glBindVertexArray(0);
    }

    /**
     * Binds the shader.
     */
    public void bind() {
        GL20.glUseProgram(programID);
    }

    /**
     * Unbinds the shader.
     */
    public void unbind() {
        GL20.glUseProgram(0);
    }

    /**
     * Cleans up the shader.
     */
    public void cleanup() {
        unbind();
        if (programID != 0) {
            GL20.glDeleteProgram(programID);
        }
    }
}
