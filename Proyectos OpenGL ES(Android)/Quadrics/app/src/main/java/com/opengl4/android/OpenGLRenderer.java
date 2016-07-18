package com.opengl4.android;

import static android.opengl.GLES20.*;

import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.opengl4.android.util.Color;


public class OpenGLRenderer implements Renderer
{
	// Para paralela
	private static final float TAM = 0.5f;
	// Para perspectiva
	// private static final float TAM = 1.0f;

	public Context context;
	private float rX=-45, rY=20;

	private float[] projectionMatrix = new float[16];
	private float[] modelMatrix=new float[16];
	private float[] MVP=new float[16];

	private Quadrics quadricaVertex, quadricaFragment;
	private Quadrics qnormal;

	private boolean rendernormal, renderobject, rendervertextfragment;

	public OpenGLRenderer(Context context, int textureid)
	{
		rendernormal=renderobject=true;
		rendervertextfragment=false;

		quadricaVertex=qnormal=null;
		this.context = context;

		quadricaVertex = new Quadrics(this, Quadrics.OBJETO_VERTEXLIGHTING,  textureid);
		quadricaVertex.setColor(new Color(1, 1, 1, 1));


		quadricaFragment = new Quadrics(this, Quadrics.OBJETO_FRAGMENTLIGHTING,  textureid);
		quadricaFragment.setColor(new Color(1, 1, 1, 1));

		qnormal = new Quadrics(this, Quadrics.OBJETO_NORMALES, textureid);
		qnormal.setColor(new Color(0.1f, 0.1f, 0.1f, 1));
	}



	public void setRX(float NewRX){rX=NewRX;}
	public void setRY(float NewRY){rY=NewRY;}
	public float getRX(){return rX;}
	public float getRY(){return rY;}

	public void setEsfera(float radio, int n, int m)
	{
		quadricaVertex.Esfera(radio, n, m);
		quadricaFragment.Esfera(radio, n, m);
		qnormal.Esfera(radio, n, m);
	}

	public void setToroide(float radio1, float radio2, int n, int m)
	{
		quadricaVertex.Toroide(radio1, radio2, n, m);
		quadricaFragment.Toroide(radio1, radio2, n, m);
		qnormal.Toroide(radio1, radio2, n, m);
	}
	public void setSuperCuadrica(float radio1, float radio2, float radio3, float s1, float s2, int n, int m)
	{
		quadricaVertex.SuperCuadratica(radio1, radio2, radio3, s1, s2, n, m);
		quadricaFragment.SuperCuadratica(radio1, radio2, radio3, s1, s2, n, m);
		qnormal.SuperCuadratica(radio1, radio2, radio3, s1, s2, n, m);
	}
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
	{
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		QuadricsOnSurfaceCreated(quadricaVertex);
		QuadricsOnSurfaceCreated(quadricaFragment);
		QuadricsOnSurfaceCreated(qnormal);
		handleTouchDrag(0, 0);
	}

	public void setRenderNormal(boolean n){rendernormal=n;}
	public void setRenderObject(boolean n){renderobject=n;}
	public void setRenderVertexLighting(boolean n){rendervertextfragment=n;}

	public void QuadricsOnSurfaceCreated(Quadrics q)
	{
		q.onSurfaceCreated(context);
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height)
	{
		// Establecer el viewport de  OpenGL para ocupar toda la superficie.
		glViewport(0, 0, width, height);
		final float aspectRatio = width > height ?
				(float) width / (float) height :
				(float) height / (float) width;

		if (width > height) {
			// Landscape
			orthoM(projectionMatrix, 0, -aspectRatio*TAM, aspectRatio*TAM, -TAM, TAM, -10.0f, 10.0f);
			//frustumM(projectionMatrix, 0, -aspectRatio*TAM, aspectRatio*TAM, -TAM, TAM, 0.1f, 100.0f);
		} else {
			// Portrait or square
			orthoM(projectionMatrix, 0, -TAM, TAM, -aspectRatio*TAM, aspectRatio*TAM, -10.0f, 10.0f);
			//frustumM(projectionMatrix, 0, -TAM, TAM, -aspectRatio*TAM, aspectRatio*TAM, 0.1f, 100.0f);
		}


		multiplyMM(MVP, 0, projectionMatrix, 0, modelMatrix, 0);
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused)
	{
		// Clear the rendering surface.
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		glEnable(GL_DEPTH_TEST);
		//glEnable(GL_CULL_FACE);
		//glEnable(GL_DITHER);
	//	glLineWidth(2.0f);


		// Clear the rendering surface.
		/////glClear(GL_COLOR_BUFFER_BIT);



		if(renderobject)
		{
			if(rendervertextfragment)
				quadricaVertex.draw(MVP, modelMatrix);
			else
				quadricaFragment.draw(MVP, modelMatrix);
		}
		if(rendernormal)
			qnormal.draw(MVP, modelMatrix);
	}

	public void handleTouchDrag(float normalizedX, float normalizedY)
	{
		setRX(rX-normalizedY*180f);
		setRY(rY+normalizedX*180f);

		setIdentityM(modelMatrix, 0);
		rotateM(modelMatrix, 0, rY, 0f, 1f, 0f);
		rotateM(modelMatrix, 0, rX, 1f, 0f, 0f);

		multiplyMM(MVP, 0, projectionMatrix, 0, modelMatrix, 0);
	}
}