package com.opengl4.android.util;

import static android.opengl.GLES20.*;
import android.util.Log;

public class ShaderHelper {
	private static final String TAG = "ShaderHelper";
	
	public static int compileVertexShader(String shaderCode) {
		return compileShader(GL_VERTEX_SHADER, shaderCode);
	}
	
	public static int compileFragmentShader(String shaderCode) {
		return compileShader(GL_FRAGMENT_SHADER, shaderCode);
	}
	
	private static int compileShader(int type, String shaderCode) {
		int shaderObjectId = glCreateShader(type);
		
		if (shaderObjectId == 0) {
			if (LoggerConfig.ON) {
				Log.w(TAG, "New shader couldn't be created.");
			}
			return 0;
		}
		glShaderSource(shaderObjectId, shaderCode);
		glCompileShader(shaderObjectId);
		
		int[] compileStatus = new int[1];
		glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
		
		if (LoggerConfig.ON) {
			// Print the shader info log to the Android log output.
			Log.v(TAG, "Result of the code compiling:" + "\n" + shaderCode + "\n:"
			+ glGetShaderInfoLog(shaderObjectId));
		}
		if (compileStatus[0] == 0) {
			// If fails, delete the shader object
			glDeleteShader(shaderObjectId);
			if (LoggerConfig.ON) {
				Log.w(TAG, "The shader compilation failed.");
			}
			return 0;
		}

		return shaderObjectId;
	}
	
	public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
		int programObjectId = glCreateProgram();
		if (programObjectId == 0) {
			if (LoggerConfig.ON) {
				Log.w(TAG, "The new program couldn't be created");
			}
			return 0;
		}
		// Adding the shaders
		glAttachShader(programObjectId, vertexShaderId);
		glAttachShader(programObjectId, fragmentShaderId);
		// Linking the shaders
		glLinkProgram(programObjectId);
		// Checking the result
		int[] linkStatus = new int[1];
		glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

		if (LoggerConfig.ON) {
			Log.v(TAG, "Linking program result: :\n"
					+ glGetProgramInfoLog(programObjectId));
		}
		// Checking linking rseult
		if (linkStatus[0] == 0) {
			// If fails, delete the program object
            glDeleteProgram(programObjectId);
			if (LoggerConfig.ON) {
				Log.w(TAG, "Linking of program failed.");
			}
			return 0;
		}
		// All was well, return the identifier
		return programObjectId;
	}
	
	public static boolean validateProgram(int programObjectId) {
		glValidateProgram(programObjectId);
		
		int[] validateStatus = new int[1];
		glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
		
		Log.v(TAG, "Result of the validateion program: " + validateStatus[0]
				+ "\nLog:" + glGetProgramInfoLog(programObjectId));
		return validateStatus[0] != 0;
	}
}
