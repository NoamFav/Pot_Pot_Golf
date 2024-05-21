#version 400 core

const int MAX_POINT_LIGHTS = 5; // Maximum number of point lights
const int MAX_SPOT_LIGHTS = 5; // Maximum number of spot lights
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

struct DirectionalLight { // Directional light properties
    vec3 color;
    vec3 direction;
    float intensity;
};

struct PointLight { // Point light properties
    vec3 color;
    vec3 position;
    float intensity;
    float constant;
    float linear;
    float exponent;
};

struct SpotLight { // Spot light properties
    PointLight pointLight;
    vec3 coneDirection;
    float cutoff;
};

uniform sampler2D textures[MAX_TEXTURES]; // Array of textures
uniform sampler2D blendMap;

uniform vec3 ambientLight;
uniform Material material;
uniform float specularPower;
uniform DirectionalLight directionalLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColor(Material material, vec2 textCoords) {
    if (material.hasTexture == 0) { // If the material has no other textures
        vec4 blendMapColor = texture(blendMap, textCoords); // Get the blend map color
        vec2 tiledCoords = textCoords * 100.0; // Tile the texture coordinates

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


vec4 calcLightColor(vec3 light_color, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal) { // Calculate the color of the light
    vec4 diffuseColor = vec4(0, 0, 0, 0); // Initialize the diffuse color
    vec4 specularColor = vec4(0, 0, 0, 0); // Initialize the specular color

    float diffuseFactor = max(dot(normal, to_light_dir), 0.0); // Calculate the diffuse factor
    diffuseColor = diffuseC * vec4(light_color, 1) * light_intensity * diffuseFactor; // Calculate the diffuse color

    vec3 camera_direction = normalize(-position); // Calculate the camera direction
    vec3 from_light_dir = -to_light_dir; // Calculate the direction from the light
    vec3 reflectedLight = normalize(reflect(from_light_dir, normal)); // Calculate the reflected light
    float specularFactor = pow(max(dot(camera_direction, reflectedLight), 0.0), specularPower); // Calculate the specular factor
    specularColor = specularC * light_intensity * specularFactor * material.reflectance * vec4(light_color, 1.0); // Calculate the specular color

    return (diffuseColor + specularColor); // Return the light color
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal) { // Calculate the color of the point light
    vec3 to_light_dir = normalize(light.position - position); // Calculate the direction to the light
    vec4 ligh_color = calcLightColor(light.color, light.intensity, position, to_light_dir, normal); // Calculate the color of the light

    float distance = length(light.position - position); // Calculate the distance to the light
    float attenuationInv = light.constant + light.linear * distance + light.exponent * distance * distance; // Calculate the inverse attenuation
    return ligh_color / attenuationInv; // Return the light color
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal) { // Calculate the color of the spot light
    vec3 to_light_dir = normalize(light.pointLight.position - position); // Calculate the direction to the light
    vec3 from_light_dir = -to_light_dir; // Calculate the direction from the light
    float spot_alpha = dot(from_light_dir, normalize(light.coneDirection)); // Calculate the spot alpha

    vec4 color = vec4(0, 0, 0, 0); // Initialize the color

    if (spot_alpha > light.cutoff) { // If the spot alpha is greater than the cutoff
        color = calcPointLight(light.pointLight, position, normal); // Calculate the color of the light
        color *= (1 - (1 - spot_alpha) / (1 - light.cutoff)); // Apply the spot light effect
    }

    return color; // Return the color
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) { // Calculate the color of the directional light
    return calcLightColor(light.color, light.intensity, position, normalize(light.direction), normal); // Return the color of the light
}

void main()
{
    setupColor(material, fragTextureCoord); // Setup the color of the fragment

    vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, fragPos, fragNormal); // Calculate the color of the directional light

    for(int i = 0; i < MAX_POINT_LIGHTS; i++){ // For each point light
        if (pointLights[i].intensity > 0.0) { // If the light intensity is greater than 0
            diffuseSpecularComp += calcPointLight(pointLights[i], fragPos, fragNormal); // Calculate the color of the light
        }
    }

    for(int i = 0; i < MAX_SPOT_LIGHTS; i++){ // For each spot light
        if (spotLights[i].pointLight.intensity > 0.0) { // If the light intensity is greater than 0
            diffuseSpecularComp += calcSpotLight(spotLights[i], fragPos, fragNormal); // Calculate the color of the light
        }
    }

    fragColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp; // Calculate the final color of the fragment
}