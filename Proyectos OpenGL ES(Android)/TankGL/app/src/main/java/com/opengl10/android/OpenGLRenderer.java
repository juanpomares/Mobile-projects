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

	Tanketor tanque;
	//Tobject decoracion[];
	Tobject suelo;
	TTriangles triangulos;
	TCamera camara;

	public OpenGLRenderer(Context context, float distancia_inicial)
	{
		this.context=context;

		//TODO Crear objetos
		tanque=new Tanketor(this);

		triangulos=new TTriangles();

		//decoracion=new Tobject[1];
		/*decoracion[0]=new Tobject(this, R.raw.torus, R.drawable.mono_tex);
		decoracion[0].setPosition(0, 0, -20);
	coracion[1]=new Tobject(this, R.raw.torus, R.drawable.mono_tex);
		decoracion[1].setPosition(0, 0, 20);*/

		suelo=new Tobject(this, R.raw.suelo2, R.drawable.grass);

		camara=new TCamera();
		camara.setPuntoAMirar(0, 0, 0);
		camara.setPuntoDondeMirar(0, 5, -10);

		camara.setDistancia(distancia_inicial);
		camara.setRotacion(0, -20);

		viewMatrix=camara.getViewmatrix();
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
			
		// Comprobamos si soporta texturas en el vertex shader
		glGetIntegerv(GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, maxVertexTextureImageUnits, 0);
		if (LoggerConfig.ON) {
			Log.w(TAG, "Max. Vertex Texture Image Units: "+maxVertexTextureImageUnits[0]);
		}
		// Comprobamos si soporta texturas (en el fragment shader)
		glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, maxTextureImageUnits, 0);
		if (LoggerConfig.ON) {
			Log.w(TAG, "Max. Texture Image Units: "+maxTextureImageUnits[0]);
		}

		int vertexShader, fragmentShader;


		//Cargamos Shader Simple para triangulos
		vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.triangulos_vertex_shader);
		fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.triangulos_fragment_shader);

		// Compilamos los shaders
		vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

		// Enlazamos el programa OpenGL
		programSimple = ShaderHelper.linkProgram(vertexShader, fragmentShader);

		// En depuración validamos el programa OpenGL
		if (LoggerConfig.ON) {	ShaderHelper.validateProgram(programSimple);}
		// Activamos el programa OpenGL
		glUseProgram(programSimple);

		// Capturamos el attribute a_Position
		aSimpleColorLocation = glGetAttribLocation(programSimple, A_COLOR);
		glEnableVertexAttribArray(aSimpleColorLocation);
		aSimplePositionLocation = glGetAttribLocation(programSimple, A_POSITION);
		glEnableVertexAttribArray(aSimplePositionLocation);




		/*Cargamos shader Specular*/
		// Leemos los shaders
		/*if (maxVertexTextureImageUnits[0]>0) {
			// Textura soportada en el vertex shader
			vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.specular_vertex_shader);
			fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.specular_fragment_shader);
		} else
		{*/
			//Log.d("onSurfaceCreated", "Textura no soportada en el VertexShader");
			// Textura no soportada en el vertex shader
			vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.modelos_vertex_shader);
			fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.modelos_fragment_shader);
		//}
		
		// Compilamos los shaders
		vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		
		// Enlazamos el programa OpenGL
		programTextureSpecular = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		
		// En depuraci�n validamos el programa OpenGL
		if (LoggerConfig.ON) {	ShaderHelper.validateProgram(programTextureSpecular);}

		glUseProgram(programTextureSpecular);

		
		// Capturamos los uniforms
		uMVPMatrixLocation = glGetUniformLocation(programTextureSpecular, U_MVPMATRIX);
		uMVMatrixLocation = glGetUniformLocation(programTextureSpecular, U_MVMATRIX);
		uColorLocation = glGetUniformLocation(programTextureSpecular, U_COLOR);
		uTextureUnitLocation = glGetUniformLocation(programTextureSpecular, U_TEXTURE);
		uLightsLocation=glGetUniformLocation(programTextureSpecular, U_LIGHTS);


		// Capturamos los attributes
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
		triangulos.onSurfaceCreate(this);
		tanque.onSurfaceCreated();

		/*for(int i=0; i<decoracion.length; i++)
			decoracion[i].onSurfaceCreated();*/
		suelo.onSurfaceCreated();
	}


	void frustum(float[] m, int offset, float l, float r, float b, float t, float n, float f)
	{
		frustumM(m, offset, l, r, b, t, n, f);
		// Correcci�n del bug de Android
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
		// Establecer el viewport de  OpenGL para ocupar toda la superficie.
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

		calcularProjectionViewMatrix();
	}


	private void calcularProjectionViewMatrix()
	{
		viewMatrix=camara.getViewmatrix();
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



		// Activamos el programa OpenGL
		glUseProgram(programTextureSpecular);


		//glUniform1i(uLightsLocation, 0);
		suelo.draw(projectionviewMatrix);

		glUniform1i(uLightsLocation, 1);
		tanque.draw(projectionviewMatrix);


		//glDisable(GL_CULL_FACE);


		/*for(int i=0; i<decoracion.length; i++)
			decoracion[i].draw(projectionviewMatrix);*/


		/*float[] shadow=getShadowProjectionMatrix(1, 5, 1);
		tanque.draw(shadow);
		decoracion[0].draw(shadow);
		decoracion[1].draw(shadow);*/

		glUseProgram(programSimple);
		glEnable(GL_BLEND);
		triangulos.draw();
		glDisable(GL_BLEND);
		/**/




		//monito.draw(getShadowProjectionMatrix(1, 10, 0));

	}

	private float[] getShadowProjectionMatrix(float x, float y, float z)
	{
		float[] shadow={y, -z, 0, 0, 0, 0, 0, 0, 0, -z, y, 0, 0, -1, 0, y};


		//float[] shadow={-y, x, 0, 0, 0, 0, 0, 0, 0, z, -y, 0, 0, 1, 0, -y};


		//float[] shadow={-y, 0, 0, 0, x, 0, z, 1, 0, 0, -y, 0, 0, 0, 0, -y};


		float[] dv=new float[16];


		multiplyMM(dv, 0, projectionviewMatrix, 0, shadow, 0);

		return dv;

		}

	public boolean CanDrag(float normalizedX, float normalizedY)
	{
		int filax=-1, filay=-1;

		if(normalizedX<=-0.8){								filax=1;}
		else if(normalizedX>=0.8){							filax=3;}
		else if(normalizedX>=-0.1 && normalizedX<=0.1)	{	filax=2;}

		if(normalizedY<=-0.8){								filay=3;}
		else if(normalizedY>=0.8){							filay=1;}
		else if(normalizedY>=-0.1 && normalizedY<=0.1)	{	filay=2;}

		if(filax!=-1 && filay!=-1)
		{
			switch (filax)
			{
				case 1:
					if(filay==2)//Izquierda
					{
						return false;
					}else if(filay==3)//Torreta Izquierda
					{
						return false;
					}
					break;
				case 2:
					if (filay==1) //Adelante
					{
						return false;
					}else if(filay==3)//Atras
					{
						return false;
					}
					break;
				case 3:
					if (filay==2)//Derecha
					{
						return false;
					} else if(filay==3)//Torreta Derecha
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

			float rotar_tanque=10f, pasos_tanque=0.25f;
			int filax=-1, filay=-1;


			if(normalizedX<=-0.8){								filax=1;}
			else if(normalizedX>=0.8){							filax=3;}
			else if(normalizedX>=-0.1 && normalizedX<=0.1)	{	filax=2;}

			if(normalizedY<=-0.8){								filay=3;}
			else if(normalizedY>=0.8){							filay=1;}
			else if(normalizedY>=-0.1 && normalizedY<=0.1)	{	filay=2;}

			if(filax!=-1 && filay!=-1)
			{
				switch (filax)
				{
					case 1:
						if(filay==2)//Izquierda
						{
							tanque.rotar(rotar_tanque);
							rotarCamara(rotar_tanque, 0);
						}else if(filay==3)//Torreta Izquierda
						{
							tanque.rotarTorreta(rotar_tanque);
						}
						break;
					case 2:
						if (filay==1) //Adelante
						{
							tanque.mover(pasos_tanque);
							MirarTanque();
						}else if(filay==3)//Atras
						{
							tanque.mover(-pasos_tanque);
							MirarTanque();
						}

						break;
					case 3:
						if (filay==2)//Derecha
						{
							tanque.rotar(-rotar_tanque);
							rotarCamara(-rotar_tanque, 0);
						} else if(filay==3)//Torreta Derecha
						{
							tanque.rotarTorreta(-rotar_tanque);
						}
						break;
				}
			}else{	Log.w(TAG, "Touch Press ["+normalizedX+", "+normalizedY+"]"); }

		}
	}
	
	public void handleTouchDrag(float normalizedX, float normalizedY)
	{
		rotarCamara(normalizedX*90f, -normalizedY*90f);
		//monito.setRotacion(monito.getrX()-normalizedY*180f, monito.getrY()+ normalizedX*180f);
	}

	private void rotarCamara(float rX, float rY)
	{
		camara.setRotacion(camara.getRx()+ rX, camara.getRy() + rY);
		calcularProjectionViewMatrix();
	}

	private void MirarTanque()
	{
		camara.setPuntoAMirarTarget(tanque.getPositionX(), tanque.getPositionY(), tanque.getPositionZ());
		calcularProjectionViewMatrix();
	}

	public void setZoom(float zoom)
	{
		camara.setDistancia(zoom);
		calcularProjectionViewMatrix();
	}

}