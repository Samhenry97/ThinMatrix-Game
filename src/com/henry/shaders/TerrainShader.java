package com.henry.shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.henry.entity.Camera;
import com.henry.entity.Light;
import com.henry.toolbox.Maths;

public class TerrainShader extends ShaderProgram {
	
	private static final int MAX_LIGHTS = 4;

	private static final String VERTEX_FILE = "src/com/henry/shaders/terrainVertexShader.glsl";
	private static final String FRAGMENT_FILE = "src/com/henry/shaders/terrainFragmentShader.glsl";
	
	private int locTransformationMatrix;
	private int locProjectionMatrix;
	private int locViewMatrix;
	private int locLightPositions[];
	private int locLightColors[];
	private int locAttenuation[];
	private int locShineDamper;
	private int locReflectivity;
	private int locSkyColor;
	private int locBackgroundTexture;
	private int locRTexture;
	private int locGTexture;
	private int locBTexture;
	private int locBlendMap;

	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		locTransformationMatrix = super.getUniformLocation("transformationMatrix");
		locProjectionMatrix = super.getUniformLocation("projectionMatrix");
		locViewMatrix = super.getUniformLocation("viewMatrix");
		locShineDamper = super.getUniformLocation("shineDamper");
		locReflectivity = super.getUniformLocation("reflectivity");
		locSkyColor = super.getUniformLocation("skyColor");
		locBackgroundTexture = super.getUniformLocation("backgroundTexture");
		locRTexture = super.getUniformLocation("rTexture");
		locGTexture = super.getUniformLocation("gTexture");
		locBTexture = super.getUniformLocation("bTexture");
		locBlendMap = super.getUniformLocation("blendMap");
		
		locLightPositions = new int[MAX_LIGHTS];
		locLightColors = new int[MAX_LIGHTS];
		locAttenuation = new int[MAX_LIGHTS];
		
		for(int i = 0; i < MAX_LIGHTS; i++) {
			locLightPositions[i] = super.getUniformLocation("lightPosition[" + i + "]");
			locLightColors[i] = super.getUniformLocation("lightColor[" + i + "]");
			locAttenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
	}
	
	public void connectTextureUnits() {
		super.loadInt(locBackgroundTexture, 0);
		super.loadInt(locRTexture, 1);
		super.loadInt(locGTexture, 2);
		super.loadInt(locBTexture, 3);
		super.loadInt(locBlendMap, 4);
	}
	
	public void loadSkyColor(float r, float g, float b) {
		super.loadVector(locSkyColor, new Vector3f(r, g, b));
	}
	
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(locShineDamper, damper);
		super.loadFloat(locReflectivity, reflectivity);
	}
	
	public void loadLights(List<Light> lights) {
		for(int i = 0; i < MAX_LIGHTS; i++) {
			if(i < lights.size()) {
				super.loadVector(locLightPositions[i], lights.get(i).getPosition());
				super.loadVector(locLightColors[i], lights.get(i).getColor());
				super.loadVector(locAttenuation[i], lights.get(i).getAttenuation());
			} else {
				super.loadVector(locLightPositions[i], new Vector3f(0, 0, 0));
				super.loadVector(locLightColors[i], new Vector3f(0, 0, 0));
				super.loadVector(locAttenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}
	
	public void loadTransformationMatrix(Matrix4f transformation) {
		super.loadMatrix(locTransformationMatrix, transformation);
	}
	
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(locProjectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(locViewMatrix, viewMatrix);
	}

}