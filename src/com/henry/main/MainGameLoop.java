package com.henry.main;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.henry.entity.Camera;
import com.henry.entity.Light;
import com.henry.entity.Player;
import com.henry.models.RawModel;
import com.henry.models.TexturedModel;
import com.henry.objloader.ModelData;
import com.henry.objloader.OBJFileLoader;
import com.henry.renderEngine.DisplayManager;
import com.henry.renderEngine.Loader;
import com.henry.renderEngine.MasterRenderer;
import com.henry.terrain.Terrain;
import com.henry.textures.ModelTexture;
import com.henry.textures.TerrainTexture;
import com.henry.textures.TerrainTexturePack;

public class MainGameLoop {
	
	public static Light sun = new Light(new Vector3f(0, 1000000, 0), new Vector3f(1f, 1f, 1f));
	
	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		
		//************* TERRAIN TEXTURE *************//
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTerrain("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTerrain("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTerrain("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTerrain("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture,
				gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTerrain("blendMap"));
		
		//********************************************//
		
		Terrain.loadModels(loader);
		
		ModelData data4 = OBJFileLoader.loadOBJ("player");
		RawModel playerData = loader.loadToVAO(data4.getVertices(), data4.getTextureCoords(), data4.getNormals(), data4.getIndices());
		TexturedModel playerModel = new TexturedModel(playerData, new ModelTexture(loader.loadTexture("playerTexture")));
		
		List<Terrain> terrains = new ArrayList<Terrain>();
		terrains.add(new Terrain(0, -1, loader, texturePack, blendMap, "heightMap"));
		terrains.add(new Terrain(-1, -1, loader, texturePack, blendMap, "heightMap"));
		terrains.add(new Terrain(0, 0, loader, texturePack, blendMap, "heightMap"));
		terrains.add(new Terrain(-1, 0, loader, texturePack, blendMap, "heightMap"));
		
		List<Light> lights = new ArrayList<Light>();
		lights.add(sun);
		
		MasterRenderer renderer = new MasterRenderer(loader);
		
		Player player = new Player(playerModel, new Vector3f(0, 0, -20), 0, 0, 0, 1);
		
		Camera camera = new Camera(player);
		
		Mouse.setGrabbed(true);
		
		while(!Display.isCloseRequested()) {
			
			if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && Mouse.isGrabbed()) {
				Mouse.setGrabbed(false);
			}
			
			if(Display.isActive() && Mouse.isButtonDown(0) && !Mouse.isGrabbed()) {
				Mouse.setGrabbed(true);
			}
			
			while(Keyboard.next()) {
				if(Keyboard.getEventKey() == Keyboard.KEY_M) {
					GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
				} else if(Keyboard.getEventKey() == Keyboard.KEY_N) {
					GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
				}
			}
			
			if(Mouse.isGrabbed()) {
				player.move(terrains);
				camera.move();
			}
			
			renderer.processEntity(player);
			
			for(int i = 0; i < terrains.size(); i++) {
				Terrain terrain = terrains.get(i);
				
				terrain.render(renderer);
				
				if(terrain.equals(player.getCurrentTerrain(terrains))) {
					terrain.renderEntities(renderer);
				}
			}
			
			renderer.render(lights, camera);
			
			DisplayManager.updateDisplay();
			
		}
		
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		
	}

}