package com.um_project_golf.Core;

import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.Utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * The class responsible for loading objects.
 */
public class ObjectLoader {
    private final List<Integer> vaos = new ArrayList<>();
    private final List<Integer> vbos = new ArrayList<>();
    private final List<Integer> textures = new ArrayList<>();

    /**
     * Loads an OBJ model.
     *
     * @param filename The name of the file to load.
     * @return The model loaded.
     */
    public Model loadOBJModel(String filename) {
        List<String> lines = Utils.readAllLines(filename);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for(String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    vertices.add(new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    ));
                    break;
                case "vt":
                    textures.add(new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    ));
                    break;
                case "vn":
                    normals.add(new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    ));
                    break;
                case "f":
                    processFace(tokens[1], faces);
                    processFace(tokens[2], faces);
                    processFace(tokens[3], faces);
                    break;
                default:
                    break;
            }
        }
        List<Integer> indices = new ArrayList<>();
        float[] verticesArray = new float[vertices.size() * 3];
        int i = 0;
        for (Vector3f vertex : vertices) {
            verticesArray[i * 3] = vertex.x;
            verticesArray[i * 3 + 1] = vertex.y;
            verticesArray[i * 3 + 2] = vertex.z;
            i++;
        }

        float[] textCoordArr = new float[vertices.size() * 2];
        float[] normalArr = new float[vertices.size() * 3];

        for (Vector3i face : faces) {
            processVertex(face.x, face.y, face.z, textures, normals, indices, textCoordArr, normalArr);
        }

        int[] indicesArr = indices.stream().mapToInt(Integer::intValue).toArray();

        return loadModel(verticesArray, textCoordArr, indicesArr);
    }

    /**
     * Processes a vertex.
     *
     * @param pos The position.
     * @param texCoord The texture coordinate.
     * @param normal The normal.
     */
    private static void processVertex(int pos, int texCoord, int normal,
                                      List<Vector2f> textCoordList, List<Vector3f> normalsList, List<Integer> indicesList,
                                      float[] textCoordArr, float[] normalArr) {
        indicesList.add(pos);

        if(texCoord >= 0) {
            Vector2f textCoordVec = textCoordList.get(texCoord);
            textCoordArr[pos * 2] = textCoordVec.x;
            textCoordArr[pos * 2 + 1] = 1 - textCoordVec.y;
        }

        if (normal >= 0) {
            Vector3f normalVec = normalsList.get(normal);
            normalArr[pos * 3] = normalVec.x;
            normalArr[pos * 3 + 1] = normalVec.y;
            normalArr[pos * 3 + 2] = normalVec.z;
        }
    }

    /**
     * Processes a face.
     *
     * @param token The token to process.
     * @param faces The list of faces.
     */
    private static void processFace(String token, List<Vector3i> faces) {
        String[] lineTokens = token.split("/");
        int length = lineTokens.length;
        int pos = -1, coords = -1, normal = -1;
        pos = Integer.parseInt(lineTokens[0]) - 1;
        if (length > 1) {
            String textCoord = lineTokens[1];
            coords = !textCoord.isEmpty() ? Integer.parseInt(textCoord) - 1 : -1;
            if (length > 2) {
                normal = Integer.parseInt(lineTokens[2]) - 1;
            }
        }
        faces.add(new Vector3i(pos, coords, normal));
    }

    /**
     * Loads a model.
     *
     * @param vertices The vertices of the model.
     * @param textureCoords The texture coordinates of the model.
     * @param indices The indices of the model.
     * @return The model loaded.
     */
    public Model loadModel(float[] vertices, float[] textureCoords, int[] indices) {
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        storeDataInAttributeList(1, 2, textureCoords);
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
