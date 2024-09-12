#version 400 core

const int MAX_TEXTURES = 7; // Maximum number of textures

in vec2 fragTextureCoord; // Texture coordinates from the vertex shader
in vec3 fragNormal; // Normal vector from the vertex shader
in vec3 fragPos; // Position of the fragment in world space

out vec4 fragColor; // Output fragment color

struct Material { // Material properties
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};


uniform sampler2D textures[MAX_TEXTURES]; // Array of textures
uniform sampler2D blendMap;

uniform vec3 ambientLight;
uniform Material material;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColor(Material material, vec2 textCoords) {
    if (material.hasTexture == 0) { // If the material has no other textures
        vec4 blendMapColor = texture(blendMap, textCoords); // Get the blend map color
        int blendMapSize = textureSize(blendMap, 0).x; // Get the size of the blend map
        vec2 tiledCoords = textCoords * (blendMapSize * 0.15); // Tile the texture coordinates

        // Normalize blend map colors by ensuring they do not exceed 1.0 in total
        float maxColorWeight = max(blendMapColor.r + blendMapColor.g + blendMapColor.b, 1.0);
        blendMapColor.rgb /= maxColorWeight;

        // Recalculate the background texture amount
        float backgroundTextureAmount = 1.0 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
        vec4 backgroundTextureColor = texture(textures[6], tiledCoords) * backgroundTextureAmount;

        // Apply textures with controlled blend factors
        bool isDarkRed = blendMapColor.r > 0.1 && blendMapColor.r < 0.4 && blendMapColor.g < 0.1 && blendMapColor.b < 0.1;
        bool isDarkGreen = blendMapColor.g > 0.1 && blendMapColor.g < 0.4 && blendMapColor.r < 0.1 && blendMapColor.b < 0.1;
        bool isDarkBlue = blendMapColor.b > 0.1 && blendMapColor.b < 0.4 && blendMapColor.r < 0.1 && blendMapColor.g < 0.1;

        float darkRedTextureAmount = max(0.0, blendMapColor.r - 0.05) / 0.35; // Adjust base and range
        float darkGreenTextureAmount = max(0.0, blendMapColor.g - 0.05) / 0.35;
        float darkBlueTextureAmount = max(0.0, blendMapColor.b - 0.05) / 0.35;
        float factor = 1.0;

        vec4 darkRedTextureColor = vec4(0);
        vec4 darkGreenTextureColor = vec4(0);
        vec4 darkBlueTextureColor = vec4(0);
        vec4 rTextureColor = vec4(0);
        vec4 gTextureColor = vec4(0);
        vec4 bTextureColor = vec4(0);

        if (isDarkRed || isDarkGreen || isDarkBlue){
            darkRedTextureColor = texture(textures[3], tiledCoords) * darkRedTextureAmount * factor;
            darkGreenTextureColor = texture(textures[4], tiledCoords) * darkGreenTextureAmount * factor;
            darkBlueTextureColor = texture(textures[5], tiledCoords) * darkBlueTextureAmount * factor;
        } else {
            rTextureColor = texture(textures[0], tiledCoords) * blendMapColor.r;
            gTextureColor = texture(textures[1], tiledCoords) * blendMapColor.g;
            bTextureColor = texture(textures[2], tiledCoords) * blendMapColor.b;
        }

        // Calculate the ambient color with controlled blending
        ambientC = clamp(backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor
        + darkRedTextureColor + darkGreenTextureColor + darkBlueTextureColor, 0.0, 1.0);
        diffuseC = ambientC; // Set the diffuse color to the ambient color
        specularC = ambientC; // Set the specular color to the ambient color
    } else {
        // Set material colors directly if textures are used
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

void main()
{
    setupColor(material, fragTextureCoord); // Setup the color of the fragment

    fragColor = ambientC * vec4(ambientLight, 1); // Calculate the final color of the fragment
}