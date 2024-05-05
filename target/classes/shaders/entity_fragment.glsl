#version 400 core

const int MAX_POINT_LIGHTS = 5; // maximum number of point lights
const int MAX_SPOT_LIGHTS = 5; // maximum number of spot lights

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

struct DirectionalLight { // struct for directional light properties
    vec3 color;
    vec3 direction;
    float intensity;
};

struct PointLight { // struct for point light properties
    vec3 color;
    vec3 position;
    float intensity;
    float constant;
    float linear;
    float exponent;
};

struct SpotLight { // struct for spot light properties
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

vec4 calcLightColor(vec3 light_color, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal) { // function to calculate light color
    vec4 diffuseColor = vec4(0, 0, 0, 0); // initialize diffuse color
    vec4 specularColor = vec4(0, 0, 0, 0); // initialize specular color

    float diffuseFactor = max(dot(normal, to_light_dir), 0.0); // calculate diffuse factor
    diffuseColor = diffuseC * vec4(light_color, 1) * light_intensity * diffuseFactor; // calculate diffuse color

    vec3 camera_direction = normalize(-position); // calculate camera direction
    vec3 from_light_dir = -to_light_dir; // calculate direction from light
    vec3 reflectedLight = normalize(reflect(from_light_dir, normal)); // calculate reflected light
    float specularFactor = pow(max(dot(camera_direction, reflectedLight), 0.0), specularPower); // calculate specular factor
    specularColor = specularC * light_intensity * specularFactor * material.reflectance * vec4(light_color, 1.0); // calculate specular color

    return (diffuseColor + specularColor); // return light color
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal) { // function to calculate point light
    vec3 to_light_dir = normalize(light.position - position); // calculate direction to light
    vec4 ligh_color = calcLightColor(light.color, light.intensity, position, to_light_dir, normal); // calculate light color with light properties

    float distance = length(light.position - position); // calculate distance to light source
    float attenuationInv = light.constant + light.linear * distance + light.exponent * distance * distance; // calculate attenuation
    return ligh_color / attenuationInv; // return light color
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal) { // function to calculate spot light
    vec3 to_light_dir = normalize(light.pointLight.position - position); // calculate direction to light
    vec3 from_light_dir = -to_light_dir; // calculate direction from light
    float spot_alpha = dot(from_light_dir, normalize(light.coneDirection)); // calculate spot alpha

    vec4 color = vec4(0, 0, 0, 0); // initialize color

    if (spot_alpha > light.cutoff) { // if spot alpha is greater than cutoff, calculate light color
        color = calcPointLight(light.pointLight, position, normal); // calculate light color
        color *= (1 - (1 - spot_alpha) / (1 - light.cutoff)); // apply spot light effect
    }

    return color; // return light color
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) { // function to calculate directional light
    return calcLightColor(light.color, light.intensity, position, normalize(light.direction), normal); // calculate light color
}

void main()
{
    setupColor(material, fragTextureCoord); // setup color values

    vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, fragPos, fragNormal); // calculate directional light

    for(int i = 0; i < MAX_POINT_LIGHTS; i++){ // calculate point lights
        if (pointLights[i].intensity > 0.0) { // if point light intensity is greater than 0, calculate point light
            diffuseSpecularComp += calcPointLight(pointLights[i], fragPos, fragNormal); // calculate point light
        }
    }

    for(int i = 0; i < MAX_SPOT_LIGHTS; i++){ // calculate spot lights
        if (spotLights[i].pointLight.intensity > 0.0) { // if spot light intensity is greater than 0, calculate spot light
            diffuseSpecularComp += calcSpotLight(spotLights[i], fragPos, fragNormal); // calculate spot light
        }
    }

    fragColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp; // calculate final color
}