package com.henry.renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	private static final int WIDTH = 1920;
	private static final int HEIGHT = 1080;
	private static final int FRAME_CAP = 120;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static void createDisplay() {
		
		ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setFullscreen(true);
			Display.create(new PixelFormat(), attribs);
			Display.setTitle("3D Game");
			
			Keyboard.create();
			Mouse.create();
		} catch(LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		
		lastFrameTime = getCurrentTime();
		
	}
	
	public static void updateDisplay() {
		
		Display.sync(FRAME_CAP);
		Display.update();
		
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
		
	}
	
	public static float getFrameTime() {
		return delta;
	}
	
	public static void closeDisplay() {
		
		Display.destroy();
		Keyboard.destroy();
		Mouse.destroy();
		
	}
	
	private static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
	
}