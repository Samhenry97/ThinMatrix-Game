package com.henry.entity;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.henry.models.TexturedModel;
import com.henry.renderEngine.DisplayManager;
import com.henry.terrain.Terrain;

public class Player extends Entity {
	
	private static final float RUN_SPEED = 50;
	private static final float WALK_SPEED = 20;
	private static final float GRAVITY = -100;
	private static final float JUMP_POWER = 50;
	private static final float STRAFE_SPEED = 20;
	
	private float currentSpeed = 0;
	private float sideSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upSpeed = 0;
	private boolean inAir = false;

	public Player(TexturedModel model, Vector3f position, float rotX,
			float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	public void move(List<Terrain> terrains) {
		Terrain terrain = getCurrentTerrain(terrains);
		
		checkInputs();
		
		increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTime(), 0);
		
		float distance = currentSpeed * DisplayManager.getFrameTime();
		float strafe = sideSpeed * DisplayManager.getFrameTime();
		
		float dx = (float) (distance * Math.sin(Math.toRadians(getRotY())) + strafe * Math.sin(Math.toRadians(getRotY() - 90)));
		float dz = (float) (distance * Math.cos(Math.toRadians(getRotY())) + strafe * Math.cos(Math.toRadians(getRotY() - 90)));
		
		
		
		upSpeed += GRAVITY * DisplayManager.getFrameTime();
		
		increasePosition(dx, upSpeed * DisplayManager.getFrameTime(), dz);
		
		float terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
		
		if(getPosition().y < terrainHeight) {
			upSpeed = 0;
			inAir = false;
			getPosition().y = terrainHeight;
		}
	}
	
	public Terrain getCurrentTerrain(List<Terrain> terrains) {
		Terrain terrain = terrains.get(0);
		
		//Test for which terrain he's on
		int terrainX = (int) Math.ceil(getPosition().x / Terrain.SIZE);
		int terrainZ = (int) Math.ceil(getPosition().z / Terrain.SIZE);
		
		for(int i = 0; i < terrains.size(); i++) {
			Terrain test = terrains.get(i);
			
			int testTerrainX = (int) Math.ceil(test.getX() / Terrain.SIZE) + 1;
			int testTerrainZ = (int) Math.ceil(test.getZ() / Terrain.SIZE) + 1;
			
			if(terrainX == testTerrainX && terrainZ == testTerrainZ) {
				terrain = test;
				break;
			}
		}
		
		return terrain;
	}
	
	private void jump() {
		if(!inAir) {
			upSpeed = JUMP_POWER;
			inAir = true;
		}
	}
	
	private void checkInputs() {		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				currentSpeed = RUN_SPEED;
			} else {
				currentSpeed = WALK_SPEED;
			}
		} else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				currentSpeed = -RUN_SPEED;
			} else {
				currentSpeed = -WALK_SPEED;
			}
		} else {
			if(currentSpeed > 0) {
				currentSpeed -= 1;
			} else if(currentSpeed < 0) {
				currentSpeed += 1;
			}
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			sideSpeed = -STRAFE_SPEED;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			sideSpeed = STRAFE_SPEED;
		} else {
			sideSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
		
		setRotY(getRotY() - (Mouse.getDX() * 0.1f));
	}

}