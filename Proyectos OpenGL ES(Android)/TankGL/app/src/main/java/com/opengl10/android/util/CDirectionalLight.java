package com.opengl10.android.util;


public class CDirectionalLight {
	public float[] direction      = {0.0f, 0.0f, 1f};
	public float[] halfplane      = {0f, -3f, 6f};
	public float[] ambientColor   = {0.2f, 0.2f, 0.2f, 1.0f};
	public float[] diffuseColor   = {0.8f, 0.8f, 0.8f, 1.0f};
	public float[] specularColor  = {1.0f, 1.0f, 1.0f, 1.0f};
}
