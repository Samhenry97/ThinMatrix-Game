package com.henry.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private final float MAX_PITCH = 90f;
	private final float MIN_PITCH = -10f;
	private final float MIN_PITCH_1ST = -90f;
	private final float MAX_DISTANCE_FROM_PLAYER = 500f;
	private final float MIN_DISTANCE_FROM_PLAYER = 15f;
	
	private int person = 3;
	private float zoomSpeed = 0;
	private float zoomFriction = 0.08f;
	private float zoomDamper = 0.018f;
	
	private float distanceFromPlayer = 50f;
	private float angleAroundPlayer = 0f;
	
	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch = 20f;
	private float yaw;
	private float roll;
	
	private Player player;
	
	public Camera(Player player) {
		this.player = player;
	}
	
	public void move() {
		if(Keyboard.isKeyDown(Keyboard.KEY_F)) {
			if(person == 1) person = 3;
			else if(person == 3) person = 1;
		}
		
		if(person == 3) {
			calculateZoom();
			calculatePitch();
			calculateAngleAroundPlayer();
			
			float hDistance = calculateHDistance();
			float vDistance = calculateVDistance();
			
			calculatePosition(hDistance, vDistance);
			
			yaw = 180 - (player.getRotY() + angleAroundPlayer);
		} else if(person == 1) {
			float theta = player.getRotY();
			float xOffs = (float) (2 * Math.sin(Math.toRadians(theta)));
			float zOffs = (float) (2 * Math.cos(Math.toRadians(theta)));
			
			position.x = player.getPosition().x + xOffs;
			position.z = player.getPosition().z + zOffs;
			position.y = player.getPosition().y + 10;
			
			calculatePitch();
			
			yaw = -player.getRotY() - 180;
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private void calculatePosition(float hDistance, float vDistance) {		
		float theta = player.getRotY() + angleAroundPlayer;
		float xOffs = (float) (hDistance * Math.sin(Math.toRadians(theta)));
		float zOffs = (float) (hDistance * Math.cos(Math.toRadians(theta)));
		
		position.x = player.getPosition().x - xOffs;
		position.z = player.getPosition().z - zOffs;
		position.y = player.getPosition().y + vDistance + 10;
	}
	
	private float calculateHDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateZoom() {
		float zoomAmount = Mouse.getDWheel() * zoomDamper;
		
		if(zoomAmount == 0 && zoomSpeed != 0) {
			zoomAmount = zoomSpeed;
			if(zoomSpeed < 0) {
				zoomSpeed += zoomFriction;
			} else {
				zoomSpeed -= zoomFriction;
			}
			
			if(zoomSpeed < zoomFriction && zoomSpeed > -zoomFriction) {
				zoomSpeed = 0;
			}
		} else if(zoomAmount != 0) {
			zoomSpeed = zoomAmount;
		}
		
		distanceFromPlayer -= zoomAmount;
		
		if(distanceFromPlayer < MIN_DISTANCE_FROM_PLAYER)
			distanceFromPlayer = MIN_DISTANCE_FROM_PLAYER;
		else if(distanceFromPlayer > MAX_DISTANCE_FROM_PLAYER)
			distanceFromPlayer = MAX_DISTANCE_FROM_PLAYER;
	}
	
	private void calculatePitch() {
			float pitchChange = Mouse.getDY() * 0.1f;
			
			pitch -= pitchChange;
			
			if(person == 3 && pitch < MIN_PITCH)
				pitch = MIN_PITCH;
			else if(person == 1 && pitch < MIN_PITCH_1ST)
				pitch = MIN_PITCH_1ST;
			else if(pitch > MAX_PITCH)
				pitch = MAX_PITCH;
	}
	
	private void calculateAngleAroundPlayer() {
		if(Mouse.isButtonDown(0)) {
			float angleChange = Mouse.getDX() * 0.3f;
			
			angleAroundPlayer -= angleChange;
		}
	}
	
}