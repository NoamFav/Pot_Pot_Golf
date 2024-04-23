package com.um_project_golf.Core;

import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.Utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.assimp.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The class responsible for loading objects.
 */
public class ObjectLoader {
    private final List<Integer> vaos = new ArrayList<>();
    private final List<Integer> vbos = new ArrayList<>();
    private final List<Integer> textures = new ArrayList<>();

    /**
     * Loads a model from a file.
     *
     * @param path The path to the file.
     * @return The model loaded.
     */
    public Model loadAssimpModel(String path) {
        // Import the file
        AIScene scene = Assimp.aiImportFile(path,
                Assimp.aiProcess_JoinIdenticalVertices |
                        Assimp.aiProcess_Triangulate |
                        Assimp.aiProcess_FixInfacingNormals);

        if (scene == null) {
            throw new RuntimeException("Failed to load model: " + path + "\n" + Assimp.aiGetErrorString());
        }

        // Process the first mesh (for simplicity)
        AIMesh mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(0));
        Model model = processMesh(mesh);

        // Cleanup
        Assimp.aiReleaseImport(scene);

        return model;
    }

    private Model processMesh(AIMesh mesh) {
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        // Vertices
        for (int i = 0; i < mesh.mNumVertices(); i++) {
            AIVector3D vertex = mesh.mVertices().get(i);
            vertices.add(vertex.x());
            vertices.add(vertex.y());
            vertices.add(vertex.z());

            // Normals
            AIVector3D normal = Objects.requireNonNull(mesh.mNormals()).get(i);
            normals.add(normal.x());
            normals.add(normal.y());
            normals.add(normal.z());

            // Texture coordinates (if present)
            if (mesh.mTextureCoords(0) != null) {
                AIVector3D texCoord = Objects.requireNonNull(mesh.mTextureCoords(0)).get(i);
                textures.add(texCoord.x());
                textures.add(1 - texCoord.y()); // Adjust for OpenGL's Y coordinate
            } else {
                textures.add(0.0f); // Default texture coords
                textures.add(0.0f);
            }
        }

        // Indices
        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);
            for (int j = 0; j < face.mNumIndices(); j++) {
                indices.add(face.mIndices().get(j));
            }
        }

        // Convert lists to arrays and create the model
        return loadModel(listToArray(vertices), listToArray(textures), listToArray(normals), listToIntArray(indices));
    }

    private float[] listToArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private int[] listToIntArray(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
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
    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbind();
        return new Model(id, indices.length);
    }

    /**
     * Loads a texture.
     *
     * @param filename The name of the file to load.
     * @return The texture loaded.
     * @throws Exception If the texture fails to load.
     */
    public int loadTexture(String filename) throws Exception {
        int width, height;
        ByteBuffer buffer;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(filename, w, h, c, 4);
            if (buffer == null) {
                throw new Exception("Image file [" + filename + "] not loaded: " + STBImage.stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        int textureID = GL11.glGenTextures();
        textures.add(textureID);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        return textureID;
    }

    /**
     * Creates a VAO.
     *
     * @return The ID of the VAO.
     */
    private int createVAO() {
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }

    /**
     * Stores the indices buffer.
     *
     * @param indices The indices to store.
     */
    private void storeIndicesBuffer(int[] indices) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    /**
     * Stores the data in the attribute list.
     *
     * @param attributeNumber The number of the attribute.
     * @param vertexCount The vertex count.
     * @param data The data to store.
     */
    private void storeDataInAttributeList(int attributeNumber, int vertexCount, float[] data) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    /**
     * Unbinds the VAO.
     */
    private void unbind() {
        GL30.glBindVertexArray(0);
    }

    /**
     * Cleans up the object loader.
     */
    public void cleanUp() {
        for (int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            GL30.glDeleteBuffers(vbo);
        }
        for (int texture : textures) {
            GL11.glDeleteTextures(texture);
        }
    }
}
