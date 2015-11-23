#version 400 core

in vec2 passTextureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;

out vec4 outColor;

uniform sampler2D textureSampler;
uniform vec3 lightColor[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void) {

	vec3 unitVectorToCamera = normalize(toCameraVector);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for(int i = 0; i < 4; i++) {
		float dist = length(toLightVector[i]);
		float attFactor = attenuation[i].x + attenuation[i].y * dist + attenuation[i].z * dist * dist;
		vec3 unitNormal = normalize(surfaceNormal);
		vec3 unitLightVector = normalize(toLightVector[i]);
		float nDotl = dot(unitNormal, unitLightVector);
		float brightness = max(nDotl, 0.0);
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
		specularFactor = max(specularFactor, 0.0);
		float dampedFactor = pow(specularFactor, shineDamper);
		totalDiffuse += brightness * lightColor[i] / attFactor;
		totalSpecular += dampedFactor * reflectivity * lightColor[i] / attFactor;
	}
	
	totalDiffuse = max(totalDiffuse, 0.2);
	
	vec4 textureColor = texture(textureSampler, passTextureCoords);
	
	if(textureColor.a < 0.5) {
		discard;
	}
	
	outColor = vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0);
	outColor = mix(vec4(skyColor, 1.0), outColor, visibility);
	
}