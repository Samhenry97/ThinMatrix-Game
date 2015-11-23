package com.henry.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.henry.entity.Camera;
import com.henry.renderEngine.DisplayManager;
import com.henry.toolbox.Maths;

public class SkyBoxShader extends ShaderProgram{
 
    private static final String VERTEX_FILE = "src/com/henry/skybox/skyboxVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/com/henry/skybox/skyboxFragmentShader.txt";
    
    private static final float ROTATE_SPEED = .5f;
     
    private int locProjectionMatrix;
    private int locViewMatrix;
    private int locFogColor;
    private int locCubeMapDay;
    private int locCubeMapNight;
    private int locBlendFactor;
    
    private float rotation = 0;
     
    public SkyBoxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
     
    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(locProjectionMatrix, matrix);
    }
 
    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;
        
        rotation += ROTATE_SPEED * DisplayManager.getFrameTime();
        Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), matrix, matrix);
        
        super.loadMatrix(locViewMatrix, matrix);
    }
    
    public void loadBlendFactor(float blend) {
    	super.loadFloat(locBlendFactor, blend);
    }
    
    public void connectTexturesUnits() {
    	super.loadInt(locCubeMapDay, 0);
    	super.loadInt(locCubeMapNight, 1);
    }
    
    @Override
    protected void getAllUniformLocations() {
        locProjectionMatrix = super.getUniformLocation("projectionMatrix");
        locViewMatrix = super.getUniformLocation("viewMatrix");
        locFogColor = super.getUniformLocation("fogColor");
        locCubeMapDay = super.getUniformLocation("cubeMapDay");
        locCubeMapNight = super.getUniformLocation("cubeMapNight");
        locBlendFactor = super.getUniformLocation("blendFactor");
    }
    
    public void loadFogColor(float r, float g, float b) {
    	super.loadVector(locFogColor, new Vector3f(r, g, b));
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
 
}