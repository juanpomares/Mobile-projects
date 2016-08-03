package com.opengl10.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;


public class OpenGLActivity extends Activity {
	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;

	private ScaleGestureDetector mScaleDetector;
	private float HistoricalNormalizedX, HistoricalNormalizedY;
	private OpenGLRenderer openGLRenderer;


	private static final float ScaleMin=5f;
	private static final float ScaleInicial=10f;
	private static final float ScaleMax=20f;

	private class ScaleListener	extends ScaleGestureDetector.SimpleOnScaleGestureListener
	{
		//public float scaleanterior;
		public float mScaleFactor=ScaleInicial;

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector)
		{
			return super.onScaleBegin(detector);
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector)
		{
			super.onScaleEnd(detector);
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			mScaleFactor /= detector.getScaleFactor();
			mScaleFactor = Math.max(ScaleMin, Math.min(mScaleFactor, ScaleMax));

			openGLRenderer.setZoom(mScaleFactor);

			return true;
		}
	}

    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
    	//super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_open_gl1);
        super.onCreate(savedInstanceState);

		mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());


		setContentView(R.layout.activity_open_gl2);

		glSurfaceView = (GLSurfaceView)findViewById(R.id.GLView);
        openGLRenderer = new OpenGLRenderer(this, ScaleInicial);
        final ActivityManager activityManager =
        		(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
        		activityManager.getDeviceConfigurationInfo();
        //final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        final boolean supportsEs2 =
        		configurationInfo.reqGlEsVersion >= 0x20000
        		|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
        		&& (Build.FINGERPRINT.startsWith("generic")
        		|| Build.FINGERPRINT.startsWith("unknown")
        		|| Build.MODEL.contains("google_sdk")
        		|| Build.MODEL.contains("Emulator")
        		|| Build.MODEL.contains("Android SDK built for x86")));
        if (supportsEs2) {
        	// Request an OpenGL ES 2.0 compatible context.
        	glSurfaceView.setEGLContextClientVersion(2);


			// Para que funcione en el emulador
        	glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 24, 0);
        	// Asigna nuestro renderer.
        	glSurfaceView.setRenderer(openGLRenderer);
        	rendererSet = true;
        	Toast.makeText(this, "OpenGL ES 2.0 supported",
        			Toast.LENGTH_LONG).show();
        } else {
        	Toast.makeText(this, "This device doesn't supports OpenGL ES 2.0",
        			Toast.LENGTH_LONG).show();
        	return;
        }

        glSurfaceView.setOnTouchListener(new OnTouchListener() {
        	@Override
        	public boolean onTouch(View v, MotionEvent event) {
        	  if (event != null)
			  {
        		// Convert touch coordinates into normalized device
        		// coordinates, keeping in mind that Android's Y
        		// coordinates are inverted.



				  float normalizedX =   ((event.getX()) / (float) v.getWidth()) * 2 - 1;
				  float normalizedY = -(((event.getY()) / (float) v.getHeight()) * 2 - 1);



				  if(event.getPointerCount()>1)
				  {
					  mScaleDetector.onTouchEvent(event);

					  HistoricalNormalizedX = normalizedX;
					  HistoricalNormalizedY = normalizedY;
				  }else {
					  if (event.getAction() == MotionEvent.ACTION_DOWN) {
						  final float nX = normalizedX;
						  final float nY = normalizedY;

						  HistoricalNormalizedX = normalizedX;
						  HistoricalNormalizedY = normalizedY;

						  glSurfaceView.queueEvent(new Runnable() {
							  @Override
							  public void run() {
								  openGLRenderer.handleTouchPress(nX, nY);
							  }
						  });

					  } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
						  final float normalizedMoveX = normalizedX - HistoricalNormalizedX;
						  final float normalizedMoveY = normalizedY - HistoricalNormalizedY;

						  HistoricalNormalizedX = normalizedX;
						  HistoricalNormalizedY = normalizedY;

						  if(openGLRenderer.CanDrag(normalizedX, normalizedY))
						  {
							  glSurfaceView.queueEvent(new Runnable() {
								  @Override
								  public void run() {
									  openGLRenderer.handleTouchDrag(normalizedMoveX, normalizedMoveY);
								  }
							  });

						  }

					  } else if (event.getAction() == MotionEvent.ACTION_UP) {
						 /* final float nX = normalizedX;
						  final float nY = normalizedY;

						  HistoricalNormalizedX = normalizedX;
						  HistoricalNormalizedY = normalizedY;

						  glSurfaceView.queueEvent(new Runnable() {
							  @Override
							  public void run() {
								  openGLRenderer.handleTouchPress(nX, nY);
							  }
						  });
					  */
					  }
				  }
        		return true;
        	  } else {
        		return false;
        	  }
        	}
        });
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	if (rendererSet) {
    		glSurfaceView.onPause();
    	}
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	if (rendererSet) {
    		glSurfaceView.onResume();
    	}
    }

}
