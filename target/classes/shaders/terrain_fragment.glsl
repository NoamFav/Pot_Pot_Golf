#version 400 core

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

in vec2 fragTextureCoord;
in vec3 fragNormal;
in vec3 fragPos;

out vec4 fragColor;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct PointLight {
    vec3 color;
    vec3 position;
    float intensity;
    float constant;
    float linear;
    float exponent;
};

struct SpotLight {
    PointLight pointLight;
    vec3 coneDirection;
    float cutoff;
};

uniform sampler2D textureSampler;
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
    if (material.hasTexture == 1) {
        ambientC = texture(textureSampler, textCoords);
        diffuseC = ambientC;
        specularC = ambientC;
    } else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

vec4 calcLightColor(vec3 light_color, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColor = diffuseC * vec4(light_color, 1) * light_intensity * diffuseFactor;

    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflectedLight = normalize(reflect(from_light_dir, normal));
    float specularFactor = pow(max(dot(camera_direction, reflectedLight), 0.0), specularPower);
    specularColor = specularC * light_intensity * specularFactor * material.reflectance * vec4(light_color, 1.0);

    return (diffuseColor + specularColor);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal) {
    vec3 to_light_dir = normalize(light.position - position);
    vec4 ligh_color = calcLightColor(light.color, light.intensity, position, to_light_dir, normal);

    float distance = length(light.position - position);
    float attenuationInv = light.constant + light.linear * distance + light.exponent * distance * distance;
    return ligh_color / attenuationInv;
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal) {
    vec3 to_light_dir = normalize(light.pointLight.position - position);
    vec3 from_light_dir = -to_light_dir;
    float spot_alpha = dot(from_light_dir, normalize(light.coneDirection));

    vec4 color = vec4(0, 0, 0, 0);

    if (spot_alpha > light.cutoff) {
        color = calcPointLight(light.pointLight, position, normal);
        color *= (1 - (1 - spot_alpha) / (1 - light.cutoff));
    }

    return color;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    vec3 lightDirNorm = normalize(light.direction);
    float lightAngleCosine = dot(lightDirNorm, vec3(0, 1, 0)); // Cosine of angle with the 'up' direction

    vec3 to_light_dir = lightDirNorm;
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);

    // Adjust the zenith factor to be less pronounced
    // Use a narrower range for the smoothstep to reduce the impact at the zenith
    float zenithFactor = smoothstep(0.80, 0.85, abs(lightAngleCosine)); // More subtle transition near vertical

    // Adjust diffuseFactor to subtly increase brightness at zenith
    diffuseFactor = mix(diffuseFactor, 0.9, zenithFactor); // Mix towards a slightly increased brightness at zenith

    vec4 lightColor = vec4(light.color, 1.0) * light.intensity;
    vec4 diffuseColor = diffuseC * lightColor * diffuseFactor;
    vec4 specularColor = vec4(0.0); // Specular calculation remains unchanged for simplicity

    return diffuseColor + specularColor;
}


void main()
{
    setupColor(material, fragTextureCoord);

    vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, fragPos, fragNormal);

    for(int i = 0; i < MAX_POINT_LIGHTS; i++){
        if (pointLights[i].intensity > 0.0) {
            diffuseSpecularComp += calcPointLight(pointLights[i], fragPos, fragNormal);
        }
    }

    for(int i = 0; i < MAX_SPOT_LIGHTS; i++){
        if (spotLights[i].pointLight.intensity > 0.0) {
            diffuseSpecularComp += calcSpotLight(spotLights[i], fragPos, fragNormal);
        }
    }

    fragColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;
}