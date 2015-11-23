package com.henry.terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.henry.entity.Entity;
import com.henry.models.RawModel;
import com.henry.models.TexturedModel;
import com.henry.objloader.ModelData;
import com.henry.objloader.OBJFileLoader;
import com.henry.renderEngine.Loader;
import com.henry.renderEngine.MasterRenderer;
import com.henry.textures.ModelTexture;
import com.henry.textures.TerrainTexture;
import com.henry.textures.TerrainTexturePack;
import com.henry.toolbox.Maths;

public class Terrain {
	
	private static TexturedModel circleTree;
	private static TexturedModel fern;
	private static TexturedModel tree;
	
	public static int MAX_ENTITIES = 30;
	
	public static void loadModels(Loader loader) {
		ModelData data1 = OBJFileLoader.loadOBJ("lowPolyTree");
		RawModel model = loader.loadToVAO(data1.getVertices(), data1.getTextureCoords(), data1.getNormals(), data1.getIndices());
		
		circleTree = new TexturedModel(model, new ModelTexture(loader.loadTexture("lowPolyTree")));
		ModelTexture texture = circleTree.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(0.8f);
		
		//*************************************************************
		
		ModelData data2 = OBJFileLoader.loadOBJ("tree");
		RawModel second = loader.loadToVAO(data2.getVertices(), data2.getTextureCoords(), data2.getNormals(), data2.getIndices());
		
		tree = new TexturedModel(second, new ModelTexture(loader.loadTexture("tree")));
		ModelTexture texture2 = tree.getTexture();
		texture2.setShineDamper(10);
		texture2.setReflectivity(1f);
		
		//**************************************************************
		
		ModelData data3 = OBJFileLoader.loadOBJ("fern");
		RawModel third = loader.loadToVAO(data3.getVertices(), data3.getTextureCoords(), data3.getNormals(), data3.getIndices());
		
		fern = new TexturedModel(third, new ModelTexture(loader.loadTexture("fern")));
		texture = fern.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(0.5f);
		texture.setHasTransparency(true);
		texture.setUseFakeLighting(true);
		
	}
	
	public static final float SIZE = 800;
	private static final float MAX_HEIGHT = 40;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;
	
	private float x;
	private float z;
	
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	
	private float[][] heights;
	
	private List<Entity> entities = new ArrayList<Entity>();
	
	public Terrain(int gridX, int gridZ, Loader loader, 
			TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMap) {
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.model = generateTerrain(loader, heightMap);
		
		addEntities();
	}
	
	private RawModel generateTerrain(Loader loader, String heightMap){
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(new File("res/images/terrains/" + heightMap + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int VERTEX_COUNT = image.getHeight();
		
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT * 1)];
		int vertexPointer = 0;
		
		for(int i = 0; i < VERTEX_COUNT; i++){
			for(int j = 0;j < VERTEX_COUNT; j++){
				vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
				float height = getHeight(j, i, image);
				heights[j][i] = height;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = calculateNormal(j, i, image);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		
		int pointer = 0;
		for(int gz = 0; gz < VERTEX_COUNT - 1; gz++){
			for(int gx = 0; gx < VERTEX_COUNT - 1; gx++){
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	public void addEntities() {
		Random rand = new Random();
		
		for(int i = 0; i < MAX_ENTITIES; i++) {
			int choice = rand.nextInt(3);
			
			float entityX = rand.nextInt((int) SIZE) + x;
			float entityZ = rand.nextInt((int) SIZE) + z;
			float entityY = getHeightOfTerrain(entityX, entityZ);
			
			switch(choice) {
				case 0 : entities.add(new Entity(fern, new Vector3f(entityX, entityY, entityZ), 0, 0, 0, 1.5f)); break;
				case 1 : entities.add(new Entity(circleTree, new Vector3f(entityX, entityY, entityZ), 0, 0, 0, 1)); break;
				case 2 : entities.add(new Entity(tree, new Vector3f(entityX, entityY, entityZ), 0, 0, 0, 20)); break;
				default : System.err.println("THIS IS IMPOSSIBLE!!!!");
			}
		}
	}
	
	public void render(MasterRenderer renderer) {
		renderer.processTerrain(this);
	}
	
	public void renderEntities(MasterRenderer renderer) {
		for(int i = 0; i < entities.size(); i++) {
			renderer.processEntity(entities.get(i));
		}
	}
	
	private Vector3f calculateNormal(int x, int z, BufferedImage image) {
		float heightL = getHeight(x - 1, z, image);
		float heightR = getHeight(x + 2, z, image);
		float heightD = getHeight(x, z - 1, image);
		float heightU = getHeight(x, z + 1, image);
		
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalise();
		return normal;
	}
	
	private float getHeight(int x, int z, BufferedImage image) {
		if(x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
			return 0;
		}
		
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOR / 2f;
		height /= MAX_PIXEL_COLOR / 2f;
		height *= MAX_HEIGHT;
		
		return height;
	}
	
	public float getHeightOfTerrain(float worldX, float worldZ) {
		float terrainZ = worldZ - z;
		float terrainX = worldX - x;
		float gridSquareSize = SIZE / ((float) heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		
		if(gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
			return 0;
		}
		
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float answer;
		
		if (xCoord <= (1 - zCoord)) {
			answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1), 
							new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), 
							new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, 
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		
		return answer;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

	public TerrainTexture getBlendMap() {
		return blendMap;
	}
	
}