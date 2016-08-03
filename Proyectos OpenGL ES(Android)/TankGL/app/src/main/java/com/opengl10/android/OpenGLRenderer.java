package com.opengl10.android;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.opengl10.android.util.LoggerConfig;
import com.opengl10.android.util.ShaderHelper;
import com.opengl10.android.util.TCamera;
import com.opengl10.android.util.TextResourceReader;
import com.opengl10.android.util.Tobject;


public class OpenGLRenderer implements Renderer
{
	private final Context context;
	private static final String TAG = "OpenGLRenderer";
	
	// Para paralela
	//private static final float TAM = 1.0f;
	// Para perspectiva
	// private static final float TAM = 1.0f;

	public int programTextureSpecular;
	public int programSimple;

	// Nombre de los uniform
	private static final String U_MVPMATRIX = "u_MVPMatrix";
	private static final String U_MVMATRIX 	= "u_MVMatrix";
	private static final String U_COLOR 	= "u_Color";
	private static final String U_TEXTURE 	= "u_TextureUnit";
	private static final String U_LIGHTS	= "u_LightsEnabled";

	// Nombre de los attribute
	private static final String A_POSITION = "a_Position";
	private static final String A_NORMAL   = "a_Normal";
	private static final String A_UV       = "a_UV";
	private static final String A_COLOR    = "a_Color";


	public int aSimpleColorLocation;
	public int aSimplePositionLocation;

	// Handles para los shaders
	public int uMVPMatrixLocation;
	public int uMVMatrixLocation;
	public int uColorLocation;
	public int uTextureUnitLocation;
	public int uLightsLocation;
	public int aSpecularPositionLocation;


	public int aNormalLocation;
	public int aUVLocation;
	

		
	// Matrices de proyecci�n y de vista
	private final float[] projectionMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	private final float[] projectionviewMatrix = new float[16];

	Tanketor mTank;
	Tobject mFloor;
	TTriangles mTriangules;
	TCamera mCamera;

	public OpenGLRenderer(Context context, float distancia_inicial)
	{
		this.context=context;

		mTank =new Tanketor(this);
		mTriangules =new TTriangles();


		mFloor =new Tobject(this, R.raw.floor2, R.drawable.grass);

		mCamera =new TCamera();
		mCamera.setLookPoint(0, 0, 0);
		mCamera.setCameraPosition(0, 5, -10);

		mCamera.setDistance(distancia_inicial);
		mCamera.setCameraRotation(0, -20);

		viewMatrix= mCamera.getViewmatrix();
	}

	public Context getContext(){return context;}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
	{
		String vertexShaderSource;
		String fragmentShaderSource;
			
		glClearColor(0.0f, 0.0f, 0.5f, 0.5f);
		
		int[]	maxVertexTextureImageUnits = new int[1];
		int[]	maxTextureImageUnits       = new int[1];
			
		// Check if it supports textures (vertex shader)
		glGetIntegerv(GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, maxVertexTextureImageUnits, 0);
		if (LoggerConfig.ON) {
			Log.w(TAG, "Max. Vertex Texture Image Units: "+maxVertexTextureImageUnits[0]);
		}
		// Check if it supports textures (fragment shader)
		glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, maxTextureImageUnits, 0);
		if (LoggerConfig.ON) {
			Log.w(TAG, "Max. Texture Image Units: "+maxTextureImageUnits[0]);
		}

		int vertexShader, fragmentShader;


		//Load Simple Triangle Shader
		vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.shader_vertex_triangles);
		fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.shader_fragment_triangles);

		// Compile the shaders
		vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

		// Linking the program OpenGL
		programSimple = ShaderHelper.linkProgram(vertexShader, fragmentShader);

		// Validation of the OpenGL program
		if (LoggerConfig.ON) {	ShaderHelper.validateProgram(programSimple);}
		// Enabling OpenGl program
		glUseProgram(programSimple);


		aSimpleColorLocation = glGetAttribLocation(programSimple, A_COLOR);
		glEnableVertexAttribArray(aSimpleColorLocation);
		aSimplePositionLocation = glGetAttribLocation(programSimple, A_POSITION);
		glEnableVertexAttribArray(aSimplePositionLocation);


		vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.shader_vertex_models);
		fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.shader_fragment_models);

		
		// Compiling the shaders
		vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

		// Linking the program OpenGL
		programTextureSpecular = ShaderHelper.linkProgram(vertexShader, fragmentShader);

		// Validation of the OpenGL program
		if (LoggerConfig.ON) {	ShaderHelper.validateProgram(programTextureSpecular);}

		glUseProgram(programTextureSpecular);


		uMVPMatrixLocation = glGetUniformLocation(programTextureSpecular, U_MVPMATRIX);
		uMVMatrixLocation = glGetUniformLocation(programTextureSpecular, U_MVMATRIX);
		uColorLocation = glGetUniformLocation(programTextureSpecular, U_COLOR);
		uTextureUnitLocation = glGetUniformLocation(programTextureSpecular, U_TEXTURE);
		uLightsLocation=glGetUniformLocation(programTextureSpecular, U_LIGHTS);


		aSpecularPositionLocation = glGetAttribLocation(programTextureSpecular, A_POSITION);
		glEnableVertexAttribArray(aSpecularPositionLocation);
		aNormalLocation = glGetAttribLocation(programTextureSpecular, A_NORMAL);
		glEnableVertexAttribArray(aNormalLocation);
		aUVLocation = glGetAttribLocation(programTextureSpecular, A_UV);
		glEnableVertexAttribArray(aUVLocation);


		NotificarSurfaceCreatedObjects();
	}


	private void NotificarSurfaceCreatedObjects()
	{
		mTriangules.onSurfaceCreate(this);
		mTank.onSurfaceCreated();

		/*for(int i=0; i<decoracion.length; i++)
			decoracion[i].onSurfaceCreated();*/
		mFloor.onSurfaceCreated();
	}


	void frustum(float[] m, int offset, float l, float r, float b, float t, float n, float f)
	{
		frustumM(m, offset, l, r, b, t, n, f);
		// Android Bug fixed
		m[8] /= 2;
	}

	void perspective(float[] m, int offset, float fovy, float aspect, float n, float f)
	{
		final float d = f-n;
		final float angleInRadians = (float) (fovy * Math.PI / 180.0);
		final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));

		m[0] = a/aspect;
		m[1] = 0f;
		m[2] = 0f;
		m[3] = 0f;

		m[4] = 0f;
		m[5] = a;
		m[6] = 0f;
		m[7] = 0f;

		m[8] = 0;
		m[9] = 0;
		m[10] = (n - f) / d;
		m[11] = -1f;

		m[12] = 0f;
		m[13] = 0f;
		m[14] = -2*f*n/d;
		m[15] = 0f;

	}

	void perspective2(float[] m, int offset, float fovy, float aspect, float n, float f)
	{
		float fH, fW;

		fH = (float) Math.tan( fovy / 360 * Math.PI ) * n;
		fW = fH * aspect;
		frustum(m, offset, -fW, fW, -fH, fH, n, f);

	}

	void frustum2(float[] m, int offset, float l, float r, float b, float t, float n, float f)
	{
		float d1 = r-l;
		float d2 = t-b;
		float d3 = f-n;

		m[0] = 2*n/d1;
		m[1] = 0f;
		m[2] = 0f;
		m[3] = 0f;

		m[4] = 0f;
		m[5] = 2*n/d2;
		m[6] = 0f;
		m[7] = 0f;

		m[8] = (r+l)/d1;
		m[9] = (t+b)/d2;
		m[10] = (n-f)/d3;
		m[11] = -1f;

		m[12] = 0f;
		m[13] = 0f;
		m[14] = -2*f*n/d3;
		m[15] = 0f;
	}



	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		// Establishing the OpenGL viewport to fill the surface.
		glViewport(0, 0, width, height);
		final float aspectRatio = width > height ?
				(float) width / (float) height :
				(float) height / (float) width;
		if (width > height) {
				// Landscape
				//orthoM(projectionMatrix, 0, -aspectRatio*TAM, aspectRatio*TAM, -TAM, TAM, -100.0f, 100.0f);
				perspective(projectionMatrix, 0, 45f, aspectRatio, 0.01f, 1000f);
				//frustum(projectionMatrix, 0, -aspectRatio*TAM, aspectRatio*TAM, -TAM, TAM, 1f, 1000.0f);
		} else {
				// Portrait or square
				//orthoM(projectionMatrix, 0, -TAM, TAM, -aspectRatio*TAM, aspectRatio*TAM, -100.0f, 100.0f);
				perspective(projectionMatrix, 0, 45f, 1f/aspectRatio, 0.01f, 1000f);
				//frustum(projectionMatrix, 0, -TAM, TAM, -aspectRatio*TAM, aspectRatio*TAM, 1f, 1000.0f);
		}

		calculateProjectionViewMatrix();
	}


	private void calculateProjectionViewMatrix()
	{
		viewMatrix= mCamera.getViewmatrix();
		multiplyMM(projectionviewMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
	}

	@Override
	public void onDrawFrame(GL10 glUnused)
	{
		// Clear the rendering surface.
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		//glEnable(GL_BLEND);
		//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		//glEnable(GL_DITHER);
		glLineWidth(2.0f);

		/*glClear(GL_DEPTH_BUFFER_BIT);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);*/



		// Enabling OpenGL program
		glUseProgram(programTextureSpecular);


		mFloor.draw(projectionviewMatrix);

		glUniform1i(uLightsLocation, 1);
		mTank.draw(projectionviewMatrix);


		glUseProgram(programSimple);
		glEnable(GL_BLEND);
		mTriangules.draw();
		glDisable(GL_BLEND);

	}



	public boolean CanDrag(float normalizedX, float normalizedY)
	{
		int Row_x=-1, Row_y=-1;

		if(normalizedX<=-0.8){								Row_x=1;}
		else if(normalizedX>=0.8){							Row_x=3;}
		else if(normalizedX>=-0.1 && normalizedX<=0.1)	{	Row_x=2;}

		if(normalizedY<=-0.8){								Row_y=3;}
		else if(normalizedY>=0.8){							Row_y=1;}
		else if(normalizedY>=-0.1 && normalizedY<=0.1)	{	Row_y=2;}

		if(Row_x!=-1 && Row_y!=-1)
		{
			switch (Row_x)
			{
				case 1:
					if(Row_y==2)//Left
					{
						return false;
					}else if(Row_y==3)//Left Turret rotation
					{
						return false;
					}
					break;
				case 2:
					if (Row_y==1) //Forward
					{
						return false;
					}else if(Row_y==3)//Backyard
					{
						return false;
					}
					break;
				case 3:
					if (Row_y==2)//Right
					{
						return false;
					} else if(Row_y==3)//Right Turret rotation
					{
						return false;
					}
					break;
			}
		}
		return true;
	}

	public void handleTouchPress(float normalizedX, float normalizedY)
	{
		if (LoggerConfig.ON)
		{
			float tank_rotation=10f, Tank_steps=0.25f;
			int row_x=-1, row_y=-1;


			if(normalizedX<=-0.8){								row_x=1;}
			else if(normalizedX>=0.8){							row_x=3;}
			else if(normalizedX>=-0.1 && normalizedX<=0.1)	{	row_x=2;}

			if(normalizedY<=-0.8){								row_y=3;}
			else if(normalizedY>=0.8){							row_y=1;}
			else if(normalizedY>=-0.1 && normalizedY<=0.1)	{	row_y=2;}

			if(row_x!=-1 && row_y!=-1)
			{
				switch (row_x)
				{
					case 1:
						if(row_y==2)//Left rotation
						{
							mTank.rotate(tank_rotation);
							CamaraRotation(tank_rotation, 0);
						}else if(row_y==3)//Left turret rotation
						{
							mTank.rotateTurret(tank_rotation);
						}
						break;
					case 2:
						if (row_y==1) //Forward
						{
							mTank.move(Tank_steps);
							LookAtTank();
						}else if(row_y==3)//Backyard
						{
							mTank.move(-Tank_steps);
							LookAtTank();
						}

						break;
					case 3:
						if (row_y==2)//Right rotation
						{
							mTank.rotate(-tank_rotation);
							CamaraRotation(-tank_rotation, 0);
						} else if(row_y==3)//Right turret rotation
						{
							mTank.rotateTurret(-tank_rotation);
						}
						break;
				}
			}else{	Log.w(TAG, "Touch Press ["+normalizedX+", "+normalizedY+"]"); }

		}
	}
	
	public void handleTouchDrag(float normalizedX, float normalizedY)
	{
		CamaraRotation(normalizedX*90f, -normalizedY*90f);
	}

	private void CamaraRotation(float rX, float rY)
	{
		mCamera.setCameraRotation(mCamera.getRotationX()+ rX, mCamera.getRotationY() + rY);
		calculateProjectionViewMatrix();
	}

	private void LookAtTank()
	{
		mCamera.setTarget(mTank.getPositionX(), mTank.getPositionY(), mTank.getPositionZ());
		calculateProjectionViewMatrix();
	}

	public void setZoom(float zoom)
	{
		mCamera.setDistance(zoom);
		calculateProjectionViewMatrix();
	}

}