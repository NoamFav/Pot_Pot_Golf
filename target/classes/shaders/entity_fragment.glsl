#version 400 core

in vec2 fragTextureCoord; // texture coordinates from vertex shader
in vec3 fragNormal; // normal vector from vertex shader
in vec3 fragPos; // position from vertex shader

out vec4 fragColor; // output color

struct Material { // struct for material properties
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

uniform sampler2D textureSampler;
uniform vec3 ambientLight;
uniform Material material;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColor(Material material, vec2 textCoords) { // function to setup color values
    if (material.hasTexture == 1) { // if material has texture, use texture color
        ambientC = texture(textureSampler, textCoords); // get texture color
        diffuseC = ambientC; // set diffuse color to texture color
        specularC = ambientC; // set specular color to texture color
    } else { // if material does not have texture, use material color
        ambientC = material.ambient; // set ambient color to material color
        diffuseC = material.diffuse; // set diffuse color to material color
        specularC = material.specular; // set specular color to material color
    }
}

void main()
{
    setupColor(material, fragTextureCoord); // setup color values

    fragColor = ambientC * vec4(ambientLight, 1); // calculate final color
}