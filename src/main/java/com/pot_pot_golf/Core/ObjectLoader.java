package com.pot_pot_golf.Core;

import com.pot_pot_golf.Core.Entity.Model;
import com.pot_pot_golf.Core.Entity.Texture;
import com.pot_pot_golf.Core.Utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.assimp.*;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** The class responsible for loading objects. */
public class ObjectLoader {

    private final List<Integer> vaos = new ArrayList<>(); // List of VAOs (Vertex Array Objects)
    private final List<Integer> vbos = new ArrayList<>(); // List of VBOs (Vertex Buffer Objects)
    private final List<Integer> textures = new ArrayList<>(); // List of textures

    /**
     * Loads a model from a file.
     *
     * @param inputStream The InputStream of the model file.
     * @return The list of models.
     */
    public List<Model> loadAssimpModel(InputStream inputStream) throws Exception {
        // Save the InputStream to a temporary file with the correct extension
        String tempPath = Utils.saveInputStreamToTempFile(inputStream, ".obj");

        // Log the path for debugging
        System.out.println("Loading model from temporary file: " + tempPath);

        // Load the model using Assimp
        AIScene scene =
                Assimp.aiImportFile(
                        tempPath,
                        Assimp.aiProcess_JoinIdenticalVertices
                                | Assimp.aiProcess_Triangulate
                                | Assimp.aiProcess_FixInfacingNormals);

        if (scene == null) {
            throw new RuntimeException(
                    "Failed to load model: " + tempPath + "\n" + Assimp.aiGetErrorString());
        }

        List<Model> models = new ArrayList<>();

        for (int i = 0; i < scene.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(i));
            List<Float> vertices = new ArrayList<>();
            List<Float> textureCoords = new ArrayList<>();
            List<Float> normals = new ArrayList<>();
            List<Integer> indices = new ArrayList<>();
            processMesh(mesh, vertices, textureCoords, normals, indices);

            Model model =
                    loadModel(
                            listToArray(vertices),
                            listToArray(textureCoords),
                            listToArray(normals),
                            listToIntArray(indices));
            models.add(model);
        }

        // Cleanup the temporary file
        Files.deleteIfExists(Path.of(tempPath));

        return models;
    }

    /**
     * Loads material textures.
     *
     * @param material The AIMaterial to load textures from.
     * @return The texture.
     * @throws Exception If the texture fails to load.
     */
    private @Nullable Texture loadMaterialTexture(AIMaterial material) throws Exception {
        AIString texturePath = AIString.calloc();
        Assimp.aiGetMaterialTexture(
                material,
                Assimp.aiTextureType_DIFFUSE,
                0,
                texturePath,
                (IntBuffer) null,
                null,
                null,
                null,
                null,
                null);
        String textureFilePath = texturePath.dataString();
        texturePath.free();

        if (textureFilePath.isEmpty()) {
            return null;
        }

        // Load the texture as an InputStream
        InputStream textureStream =
                getClass().getClassLoader().getResourceAsStream(textureFilePath);
        if (textureStream == null) {
            throw new Exception("Texture file not found: " + textureFilePath);
        }

        int textureID = loadTexture(textureStream);
        return new Texture(textureID);
    }

    /**
     * Processes a mesh and appends its data to the provided lists.
     *
     * @param mesh The mesh to process.
     * @param vertices The list to append vertices to.
     * @param textures The list to append texture coordinates to.
     * @param normals The list to append normals to.
     * @param indices The list to append indices to.
     */
    private void processMesh(
            @NotNull AIMesh mesh,
            List<Float> vertices,
            List<Float> textures,
            List<Float> normals,
            List<Integer> indices) {
        for (int i = 0; i < mesh.mNumVertices(); i++) { // Loop through the vertices
            AIVector3D vertex = mesh.mVertices().get(i); // Get the vertex
            vertices.add(vertex.x()); // Add the x-coordinate of the vertex
            vertices.add(vertex.y()); // Add the y-coordinate of the vertex
            vertices.add(vertex.z()); // Add the z-coordinate of the vertex

            AIVector3D normal = Objects.requireNonNull(mesh.mNormals()).get(i); // Get the normal
            normals.add(normal.x()); // Add the x-coordinate of the normal
            normals.add(normal.y()); // Add the y-coordinate of the normal
            normals.add(normal.z()); // Add the z-coordinate of the normal

            if (mesh.mTextureCoords(0) != null) { // Check if the mesh has texture coordinates
                AIVector3D texCoord =
                        Objects.requireNonNull(mesh.mTextureCoords(0))
                                .get(i); // Get the texture coordinate
                textures.add(texCoord.x()); // Add the x-coordinate of the texture coordinate
                textures.add(1 - texCoord.y()); // Invert the y-coordinate because OpenGL uses a
                // different coordinate system
            } else {
                textures.add(
                        0.0f); // If the mesh does not have texture coordinates, add default texture
                // coordinates
                textures.add(0.0f);
            }
        }

        for (int i = 0; i < mesh.mNumFaces(); i++) { // Loop through the faces
            AIFace face = mesh.mFaces().get(i); // Get the face
            for (int j = 0; j < face.mNumIndices(); j++) { // Loop through the indices
                indices.add(face.mIndices().get(j)); // Add the index with the current offset
            }
        }
    }

    /**
     * Converts a list to an array.
     *
     * @param list The list of materials.
     * @return The material processed.
     */
    private float @NotNull [] listToArray(@NotNull List<Float> list) {
        float[] array = new float[list.size()]; // Create a new array
        for (int i = 0; i < list.size(); i++) { // Loop through the list
            array[i] = list.get(i); // Add the element to the array
        }
        return array;
    }

    /**
     * Converts a list to an array.
     *
     * @param list The list of materials.
     * @return The material processed.
     */
    private int @NotNull [] listToIntArray(@NotNull List<Integer> list) {
        int[] array = new int[list.size()]; // Create a new array
        for (int i = 0; i < list.size(); i++) { // Loop through the list
            array[i] = list.get(i); // Add the element to the array
        }
        return array;
    }

    /**
     * Loads a model.
     *
     * @param vertices The vertices of the model.
     * @param textureCoords The texture coordinates of the model.
     * @param indices The indices of the model.
     * @return The model loaded.
     */
    public Model loadModel(
            float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        int id = createVAO(); // Create a new VAO (Vertex Array Object)
        storeIndicesBuffer(indices); // Store the indices buffer in the vbos (Vertex Buffer Objects)
        storeDataInAttributeList(0, 3, vertices); // Store the vertices in the attribute list
        storeDataInAttributeList(
                1, 2, textureCoords); // Store the texture coordinates in the attribute list
        storeDataInAttributeList(2, 3, normals); // Store the normals in the attribute list
        unbind(); // Unbind the VAO (Vertex Array Object)
        return new Model(id, indices.length); // Return the model
    }

    /**
     * Loads a texture.
     *
     * @param inputStream The InputStream of the texture file.
     * @return The texture loaded.
     * @throws Exception If the texture fails to load.
     */
    public int loadTexture(InputStream inputStream) throws Exception {
        int width, height; // The width and height of the texture
        ByteBuffer buffer; // The buffer of the texture
        try (MemoryStack stack = MemoryStack.stackPush()) { // Push the memory stack
            IntBuffer w = stack.mallocInt(1); // The width of the texture
            IntBuffer h = stack.mallocInt(1); // The height of the texture
            IntBuffer c = stack.mallocInt(1); // The number of components of the texture

            // Load the image using STBImage from InputStream
            buffer = Utils.loadImageToByteBuffer(inputStream, w, h, c);
            if (buffer == null) { // Check if the image failed to load
                throw new Exception("Image not loaded: " + STBImage.stbi_failure_reason());
            }

            width = w.get(); // Get the width of the texture
            height = h.get(); // Get the height of the texture
        }

        int textureID = GL11.glGenTextures(); // Generate a new texture ID
        this.textures.add(textureID); // Add the texture ID to the list of textures
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); // Bind the texture
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(
                GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                buffer); // Set the texture image

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D); // Generate mipmaps
        STBImage.stbi_image_free(buffer); // Free the image buffer
        return textureID; // Return the texture ID
    }

    /**
     * Creates a VAO.
     *
     * @return The ID of the VAO.
     */
    private int createVAO() {
        int id = GL30.glGenVertexArrays(); // Generate a new VAO (Vertex Array Object)
        vaos.add(id); // Add the VAO to the list of VAOs
        GL30.glBindVertexArray(id); // Bind the VAO
        return id;
    }

    /**
     * Stores the indices buffer.
     *
     * @param indices The indices to store.
     */
    private void storeIndicesBuffer(int[] indices) {
        int vbo = GL15.glGenBuffers(); // Generate a new VBO (Vertex Buffer Object)
        vbos.add(vbo); // Add the VBO to the list of VBOs
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo); // Bind the VBO
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices); // Store the data in the buffer
        GL15.glBufferData(
                GL15.GL_ELEMENT_ARRAY_BUFFER,
                buffer,
                GL15.GL_STATIC_DRAW); // Set the data in the buffer
    }

    /**
     * Stores the data in the attribute list.
     *
     * @param attributeNumber The number of the attribute.
     * @param vertexCount The vertex count.
     * @param data The data to store.
     */
    private void storeDataInAttributeList(int attributeNumber, int vertexCount, float[] data) {
        int vbo = GL15.glGenBuffers(); // Generate a new VBO (Vertex Buffer Object)
        vbos.add(vbo); // Add the VBO to the list of VBOs
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); // Bind the VBO
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data); // Store the data in the buffer
        GL15.glBufferData(
                GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); // Set the data in the buffer
        GL20.glVertexAttribPointer(
                attributeNumber,
                vertexCount,
                GL11.GL_FLOAT,
                false,
                0,
                0); // Set the vertex attribute pointer
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // Unbind the VBO
    }

    /** Unbinds the VAO. */
    private void unbind() {
        GL30.glBindVertexArray(0); // unbind the VAO by binding to 0
    }

    /** Cleans up the object loader. */
    public void cleanUp() {
        for (int vao : vaos) { // Loop through the VAOs
            GL30.glDeleteVertexArrays(vao); // Delete the VAO
        }
        for (int vbo : vbos) { // Loop through the VBOs
            GL15.glDeleteBuffers(vbo); // Delete the VBO
        }
        for (int texture : textures) { // Loop through the textures
            GL11.glDeleteTextures(texture); // Delete the texture
        }
    }
}
