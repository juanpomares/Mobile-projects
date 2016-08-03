package com.opengl10.android.util;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TextureHelper {
	private static final String TAG = "TextureHelper";
	
	public static int loadTexture(Context context, int resourceId) {
		
		final int[] textureObjectIds = new int[1];
		
		glGenTextures(1, textureObjectIds, 0);
		
		if (textureObjectIds[0] == 0) {
			if (LoggerConfig.ON) {
					Log.w(TAG, "The OpenGL texture couldn't created.");
			}
			return 0;
		}
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		final Bitmap bitmap = BitmapFactory.decodeResource(
				context.getResources(), resourceId, options);
		if (bitmap == null) {
			if (LoggerConfig.ON) {
				Log.w(TAG, "The resource whit ID '" + resourceId + "' couldn't be decoded.");
			}
			glDeleteTextures(1, textureObjectIds, 0);
			return 0;
		}
		
		glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
		
		glGenerateMipmap(GL_TEXTURE_2D);
		
		glBindTexture(GL_TEXTURE_2D, 0);
		
		return textureObjectIds[0];
	}
}
