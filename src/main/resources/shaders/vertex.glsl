#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;

out vec2 fragTextureCoord;


void main()
{
    gl_Position = vec4(position, 1.0);
    fragTextureCoord = textureCoord;
}