package com.henry.shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.henry.entity.Camera;
import com.henry.entity.Light;
import com.henry.toolbox.Maths;

public class StaticShader extends ShaderProgram {
	
	private static final int MAX_LIGHTS = 4;
	
	private static final String VERTEX_FILE = "src/com/henry/shaders/vertexShader.glsl";
	private static final String FRAGMENT_FILE = "src/com/henry/shaders/fragmentShader.glsl";
	
	private int locTransformationMatrix;
	private int locProjectionMatrix;
	private int locViewMatrix;
	private int locLightPositions[];
	private int locLightColors[];
	private int locAttenuation[];
	private int locShineDamper;
	private int locReflectivity;
	private int locUseFakeLighting;
	private int locSkyColor;
	private int locNumberOfRows;
	private int locOffset;

	public StaticShader() {
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
		locUseFakeLighting = super.getUniformLocation("useFakeLighting");
		locSkyColor = super.getUniformLocation("skyColor");
		locNumberOfRows = super.getUniformLocation("numberOfRows");
		locOffset = super.getUniformLocation("offset");
		
		locLightPositions = new int[MAX_LIGHTS];
		locLightColors = new int[MAX_LIGHTS];
		locAttenuation = new int[MAX_LIGHTS];
		
		for(int i = 0; i < MAX_LIGHTS; i++) {
			locLightPositions[i] = super.getUniformLocation("lightPosition[" + i + "]");
			locLightColors[i] = super.getUniformLocation("lightColor[" + i + "]");
			locAttenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
	}
	
	public void loadNumberOfRows(int numberOfRows) {
		super.loadFloat(locNumberOfRows, numberOfRows);
	}
	
	public void loadOffset(float x, float y) {
		super.load2DVector(locOffset, new Vector2f(x, y));
	}
	
	public void loadSkyColor(float r, float g, float b) {
		super.loadVector(locSkyColor, new Vector3f(r, g, b));
	}
	
	public void loadFakeLightingVariable(boolean useFake) {
		super.loadBoolean(locUseFakeLighting, useFake);
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