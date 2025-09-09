#version 400 core

layout(location = 0) in vec3 position; // The position variable has an attribute of 0
layout(location = 1) in vec2 textureCoord; // The texture coordinate variable has an attribute of 1
layout(location = 2) in vec3 normal; // The normal variable has an attribute of 2

out vec2 fragTextureCoord; // The output variable to the fragment shader
out vec3 fragNormal; // The output normal to the fragment shader
out vec3 fragPos; // The output position to the fragment shader

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main()
{
    vec4 worldPos = transformationMatrix * vec4(position, 1.0); // Calculate the world position of the vertex
    gl_Position = projectionMatrix * viewMatrix * worldPos; // Calculate the clip position of the vertex

    fragNormal = normalize(worldPos.xyz); // Calculate the normal of the vertex
    fragPos = worldPos.xyz; // Calculate the position of the vertex
    fragTextureCoord = textureCoord; // Pass the texture coordinate to the fragment shader
}