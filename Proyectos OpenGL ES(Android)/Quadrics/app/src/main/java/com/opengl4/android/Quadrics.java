package com.opengl4.android;

import android.content.Context;
import android.util.Log;

import com.opengl4.android.util.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;

import static android.opengl.GLES20.*;


import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetIntegerv;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by mastermoviles on 17/11/15.
 */


public class Quadrics
{
    private static final int BYTES_PER_FLOAT = 4;
    private FloatBuffer vertexData;

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int TEXTURE_COMPONENT_COUNT = 2;
    // Cálculo del tamaño de los datos (5 floats)
    private int STRIDE;

    private int numVertices;
    private float[] tablaVertices;
    private LinkedList<Vertice> listaVertice;

    private Color color;



    /*Variables para cuadrica*/
    private int aPositionLocation;
    public int aNormalLocation;
    private int aTextureLocation;
    public int uMVPMatrixLocation;
    public int uMVMatrixLocation;
    public int uColorLocation;
    public int uTextureUnitLocation;

    private static final String U_TEXTURE 	= "u_TextureUnit";
    private static final String U_COLOR     = "u_Color";
    private static final String U_MVPMATRIX = "u_MVPMatrix";
    private static final String U_MVMATRIX 	= "u_MVMatrix";
    private static final String A_POSITION  = "a_Position";
    private static final String A_NORMAL    = "a_Normal";
    private static final String A_TEXTURE   = "a_UV";

    private int program;
    private OpenGLRenderer renderer;

    private int TexturaID;
    int texture;


    private int tipo;
    public static final int OBJETO_NORMALES=0;
    public static final int OBJETO_FRAGMENTLIGHTING=1;
    public static final int OBJETO_VERTEXLIGHTING=2;

    public Quadrics(OpenGLRenderer rend, int tipop, int textura)
    {
        listaVertice=null;
        vertexData=null;
        tablaVertices=null;


        TexturaID=textura;

        renderer=rend;
        STRIDE=POSITION_COMPONENT_COUNT;

        this.tipo=tipop;

        if(!isObjectNormal())
           STRIDE +=(NORMAL_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT);

       STRIDE*=BYTES_PER_FLOAT;

        numVertices=0;
        setColor(new Color());
    }

    public boolean isObjectNormal(){return (tipo==OBJETO_NORMALES);}

    public void setColor(Color c){ color=c;  }

    public class Vertice
    {
        float pX, pY, pZ;
        float nX, nY, nZ;
        float u,v;

        public Vertice()
        {
            pX=pY=pZ=0;
            nX=nY=nZ=0;
            u=v=0;
        }

        private int sgn(float number)
        {
            return number<0?-1:1;
        }

        private float interp_lineal(float ini, float fin, float t)
        {
            return ini*(1.0f-t)+fin*t;
        }

        public Vertice crearVerticeEsfera(int n, int m, float r, float x, float y, float z, int i, int j)
        {
            Vertice v = new Vertice();
            float angulo_ini_m=(float)-Math.PI/2.0f, angulo_fin_m=(float)Math.PI/2.0f;
            float angulo_ini_n=(float)-Math.PI, angulo_fin_n=(float)Math.PI;

            float radians_act_m= interp_lineal (angulo_ini_m, angulo_fin_m, j/(m*1.0f));
            float radians_act_n= interp_lineal (angulo_ini_n, angulo_fin_n, i/(n*1.0f));

            v.pX = (float) (r*Math.cos(radians_act_n)*Math.cos(radians_act_m)+x);
            v.pY = (float) (r*Math.cos(radians_act_n)*Math.sin(radians_act_m)+y);
            v.pZ = (float) (r*Math.sin(radians_act_n)+z);

            float modulo_normal=0;
            float[] punto_final={v.pX, v.pY, v.pZ};
            float[] vector_normal=new float[3];
            float[] punto_origen={x, y, z};

            for(int k=0; k<3; k++)
            {
                vector_normal[k] = punto_final[k] - punto_origen[k];
                modulo_normal+=(vector_normal[k]*vector_normal[k]);
            }

            modulo_normal=(float)Math.sqrt(modulo_normal);

            for(int k=0; k<3; k++){ vector_normal[k] /= modulo_normal; }

            v.nX=vector_normal[0];
            v.nY=vector_normal[1];
            v.nZ=vector_normal[2];

            v.u=i/(n*1.0f);
            v.v=j/(m*1.0f);

            if(v.u>1) v.u=1;
            if(v.u<0) v.u=0;

            if(v.v>1) v.v=1;
            if(v.v<0) v.v=0;

            return v;
        }

        public Vertice crearVerticeToro(int n, int m, float r1, float r2, float x, float y, float z, int i, int j)
        {
            Vertice v = new Vertice();

            float twopi= (float) (2.0f*Math.PI);

            v.pX = (float) ((r1 + r2 * Math.cos(i * twopi / m)) * Math.cos(j * twopi / n))+x;
            v.pY = (float) ((r1 + r2 * Math.cos(i * twopi / m)) * Math.sin(j * twopi / n))+y;
            v.pZ = (float) (r2 * Math.sin(i * twopi / n))+z;

            float modulo_normal=0;
            float[] punto_final={v.pX, v.pY, v.pZ};
            float[] vector_normal=new float[3];
            float[] punto_origen={(float) (x+r1*Math.cos(j*twopi/n)),(float) (y+r1*Math.sin(j*twopi/n)), z};

            for(int k=0; k<3; k++)
            {
                vector_normal[k] = punto_final[k] - punto_origen[k];
                modulo_normal+=(vector_normal[k]*vector_normal[k]);
            }

            modulo_normal=(float)Math.sqrt(modulo_normal);

            for(int k=0; k<3; k++)
            {
                vector_normal[k] /= modulo_normal;

            }

            v.nX=vector_normal[0];
            v.nY=vector_normal[1];
            v.nZ=vector_normal[2];



            v.v=i/(n*1.0f);
            v.u=j/(m*1.0f);
            if(v.u>1) v.u=1;
            if(v.u<0) v.u=0;

            if(v.v>1) v.v=1;
            if(v.v<0) v.v=0;

            return v;
        }

        public Vertice crearVerticesSuperCuadratica(int n, int m, float r1, float r2, float r3, float s1, float s2, float x, float y, float z, int i, int j)
        {
            Vertice v = new Vertice();
            float angulo_ini_m=(float)0.f, angulo_fin_m=(float)Math.PI*2;
            float angulo_ini_n=(float)0.f, angulo_fin_n=(float)Math.PI;

            float radians_act_m= interp_lineal (angulo_ini_m, angulo_fin_m, j/(m*1.0f));
            float radians_act_n= interp_lineal (angulo_ini_n, angulo_fin_n, i/(n*1.0f));

            float cos_v_dv = (float) Math.cos(radians_act_n);
            float cos_u_du = (float) Math.cos(radians_act_m);

            float sin_v_dv = (float) Math.sin(radians_act_n);
            float sin_u_du = (float) Math.sin(radians_act_m);

            v.pX= (float) (r1*sgn(cos_v_dv)*sgn(sin_u_du)*Math.pow(Math.abs(cos_v_dv),s1)*Math.pow(Math.abs(sin_u_du),s2));
            v.pY= (float) (r2*sgn(sin_v_dv)*sgn(sin_u_du)*Math.pow(Math.abs(sin_v_dv), s1)*Math.pow(Math.abs(sin_u_du),s2));
            v.pZ= (float) (r3*sgn(cos_u_du)*Math.pow(Math.abs(cos_u_du), s2));

            float dx_du= (float) (r1*sgn(cos_v_dv)*cos_u_du*Math.pow(Math.abs(cos_v_dv), s1)*Math.pow(Math.abs(sin_u_du), s1 - 1));
            float dy_du= (float) (r2*sgn(sin_v_dv)*cos_u_du*Math.pow(Math.abs(sin_v_dv), s1)*Math.pow(Math.abs(sin_u_du), s1 - 1));
            float dz_du= (float) (-r3*sin_u_du*Math.pow(Math.abs(cos_u_du), s1 - 1));

            float dx_dv= (float) (-r1*sgn(sin_u_du)*Math.pow(Math.abs(sin_u_du), s2)*sin_v_dv*Math.pow(Math.abs(cos_v_dv), s2 - 1));
            float dy_dv= (float) (r2*sgn(sin_u_du)*Math.pow(Math.abs(sin_u_du), s2)*cos_v_dv*Math.pow(Math.abs(sin_v_dv), s2 - 1));

            float[] vector_normal=new float[3];

            vector_normal[0] = -dz_du*dy_dv;
            vector_normal[1] = dx_dv*dz_du;
            vector_normal[2] = dx_du*dy_dv-dx_dv*dy_du;

            float modulo_normal=0;

            for(int k=0; k<3; k++)
            {
                modulo_normal+=(vector_normal[k]*vector_normal[k]);
            }

            modulo_normal=(float)Math.sqrt(modulo_normal);

            if(j>=n/2 || j==0)
                modulo_normal*=-1;

            for(int k=0; k<3; k++)
            {
                vector_normal[k] /= modulo_normal;
            }

            v.nX=vector_normal[0];
            v.nY=vector_normal[1];
            v.nZ=vector_normal[2];

            v.u=i*1.0f/n;
            v.v=j*1.0f/m;

            return v;
        }
    }

    public void AddPointLista(Vertice vertex)
    {
        listaVertice.add(vertex);
        if(isObjectNormal())
        {
            float altura=0.06f;
            Vertice normal=new Vertice();
            normal.pX=vertex.pX+vertex.nX*altura;
            normal.pY=vertex.pY+vertex.nY*altura;
            normal.pZ=vertex.pZ+vertex.nZ*altura;

            listaVertice.add(normal);
        }
    }

    public void Toroide(float r1, float r2, int n, int m)
    {
        Toroide(r1, r2, n, m, 0, 0, 0);
    }

    public void Toroide(float r1, float r2, int n, int m, float orig_x, float orig_y, float orig_z)
    {
        numVertices = 0;
        if (n < 3) n = 3;
        if (m < 3) m = 3;

        if(listaVertice==null)
            listaVertice = new LinkedList<Vertice>();
        listaVertice.clear();
        Vertice actual = new Vertice();

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                AddPointLista(actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i, j));
                AddPointLista(actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i, j + 1));
                AddPointLista(actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i + 1, j + 1));

                AddPointLista(actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i, j));
                AddPointLista(actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i + 1, j + 1));
                AddPointLista(actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i + 1, j));
            }
        }
        CrearTablaVertices();
    }

    public void Esfera(float r, int n, int m)
    {
        Esfera(r, n, m, 0, 0, 0);
    }

    public void Esfera(float r, int n, int m, float orig_x, float orig_y, float orig_z)
    {
        numVertices=0;

        if(n<3) n=3;
        if(m<3) m=3;

        if(listaVertice==null)
            listaVertice = new LinkedList<Vertice>();
        listaVertice.clear();
        Vertice actual=new Vertice();

        for(int i=0; i<m; i++)
        {
            for(int j=0; j<=n; j++)
            {
                if(j!=n)
                {
                    AddPointLista(actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i, j));
                    AddPointLista(actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i, j + 1));
                    AddPointLista(actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i + 1, j + 1));

                    if(j!=0)
                    {
                        AddPointLista(actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i, j));
                        AddPointLista(actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i + 1, j + 1));
                        AddPointLista(actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i + 1, j));
                    }
                }else
                {
                    AddPointLista(actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i, j));
                    AddPointLista(actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i, j + 1));
                    AddPointLista(actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i + 1, j));
                }
            }
        }

        CrearTablaVertices();
    }

    public void SuperCuadratica(float r1, float r2, float r3, float s1, float s2, int n, int m)
    {
        SuperCuadratica(r1, r2, r3, s1, s2, n, m, 0, 0, 0);
    }

    public void SuperCuadratica(float r1, float r2, float r3, float s1, float s2, int n, int m, float orig_x, float orig_y, float orig_z)
    {
        numVertices=0;

        if(n<3) n=3;
        if(m<3) m=3;

        if(listaVertice==null)
            listaVertice = new LinkedList<Vertice>();
        listaVertice.clear();
        Vertice actual=new Vertice();

        for(int i=0; i<m; i++)
        {
            for(int j=0; j<n; j++)
            {
                if(j!=n)
                {
                    AddPointLista(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j));
                    AddPointLista(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j + 1));
                    AddPointLista(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i + 1, j + 1));

                    if(j!=0)
                    {
                        AddPointLista(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j));
                        AddPointLista(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i + 1, j + 1));
                        AddPointLista(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i + 1, j));
                    }
                } else
                {
                    AddPointLista(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j));
                    AddPointLista(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j + 1));
                    AddPointLista(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i + 1, j));
                }
            }
        }
        CrearTablaVertices();
    }

    private void CrearTablaVertices()
    {
        if(tablaVertices!=null)
            tablaVertices=null;

        tablaVertices = new float[(numVertices=this.listaVertice.size())*(POSITION_COMPONENT_COUNT+(isObjectNormal()==false?(NORMAL_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) :0))];
        /*else
        {
            tablaVertices=null;
            tablaVertices = new float[(numVertices=this.listaVertice.size())*(POSITION_COMPONENT_COUNT+(normales==false?(NORMAL_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) :0))];
        }*/


        int desplazamiento=0;

        Vertice actual;

        for (int i=0; i<numVertices; i++)
        {
            actual = listaVertice.get(i);

            tablaVertices[desplazamiento++] = actual.pX;
            tablaVertices[desplazamiento++] = actual.pY;
            tablaVertices[desplazamiento++] = actual.pZ;
            if(!isObjectNormal())
            {
                tablaVertices[desplazamiento++] = actual.nX;
                tablaVertices[desplazamiento++] = actual.nY;
                tablaVertices[desplazamiento++] = actual.nZ;
                tablaVertices[desplazamiento++] = actual.u;
                tablaVertices[desplazamiento++] = actual.v;
            }
        }

        if(vertexData!=null)
        {
            vertexData.clear();
            vertexData = null;
        }

        vertexData = ByteBuffer
            .allocateDirect(tablaVertices.length * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();
        vertexData.clear();
        vertexData.put(tablaVertices);
    }

    public void onSurfaceCreated(Context context)
    {
        String vertexShaderSource="", fragmentShaderSource="";

        switch (tipo)
        {
            case OBJETO_NORMALES:
                vertexShaderSource=TextResourceReader.readTextFileFromResource(context, R.raw.normal_vertex_shader);
                fragmentShaderSource=TextResourceReader.readTextFileFromResource(context, R.raw.normal_fragment_shader);
                break;

            case OBJETO_VERTEXLIGHTING:
                vertexShaderSource=TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
                fragmentShaderSource=TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);
                break;

            case OBJETO_FRAGMENTLIGHTING:
                vertexShaderSource=TextResourceReader.readTextFileFromResource(context, R.raw.fligthing_vertex_shader);
                fragmentShaderSource=TextResourceReader.readTextFileFromResource(context, R.raw.fligthing_fragment_shader);
                break;
        }


        // Compilamos los shaders
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        // En depuración validamos el programa OpenGL
        if (LoggerConfig.ON) {	ShaderHelper.validateProgram(program);	}

        // Activamos el programa OpenGL
        glUseProgram(program);

        uColorLocation = glGetUniformLocation(program, U_COLOR);
        glUniform4f(uColorLocation, color.r, color.g, color.b, color.a);
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVPMATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        if(!isObjectNormal())
        {
            int[]	maxVertexTextureImageUnits = new int[1];
            int[]	maxTextureImageUnits       = new int[1];

            glGetIntegerv(GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, maxVertexTextureImageUnits, 0);
            if (LoggerConfig.ON)
                Log.w("OnSurfaceCreated", "Max. Vertex Texture Image Units: "+maxVertexTextureImageUnits[0]);

            glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, maxTextureImageUnits, 0);
            if (LoggerConfig.ON)
                Log.w("OnSurfaceCreated", "Max. Texture Image Units: "+maxTextureImageUnits[0]);

            if(TexturaID!=-1)
                texture = TextureHelper.loadTexture(renderer.context, TexturaID);

            // Capturamos los uniforms
            uMVMatrixLocation = glGetUniformLocation(program, U_MVMATRIX);
            uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE);

            aNormalLocation = glGetAttribLocation(program, A_NORMAL);
            aTextureLocation = glGetAttribLocation(program, A_TEXTURE);
         }
        cargarBufferShader();
    }

    private void cargarBufferShader()
    {
        glEnableVertexAttribArray(aPositionLocation);
        // Asociando vértices con su attribute
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);

        if(!isObjectNormal())
        {
            glEnableVertexAttribArray(aNormalLocation);
            // Asociando vértices con su attribute
            vertexData.position(POSITION_COMPONENT_COUNT);
            glVertexAttribPointer(aNormalLocation, NORMAL_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);


            glEnableVertexAttribArray(aTextureLocation);
            // Asociando vértices con su attribute
            vertexData.position(POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT);
            glVertexAttribPointer(aTextureLocation, TEXTURE_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        }
    }

    public void draw(float[] MVP, float[] modelMatrix)
    {
        if(vertexData==null)
            return;

        glUseProgram(program);
        cargarBufferShader();

        // Envía la matriz de proyección al shader

        // Env�a la matriz de proyecci�n multiplicada por modelMatrix al shader
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVP, 0);

        if(!isObjectNormal())
        {
            // Env�a la matriz modelMatrix al shader
            glUniformMatrix4fv(uMVMatrixLocation, 1, false, modelMatrix, 0);

            // Pasamos la textura
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture);
            glUniform1f(uTextureUnitLocation, 0);
        }
       //glDrawArrays(GL_TRIANGLES, 0, numVertices);
       glDrawArrays(isObjectNormal() == true ? GL_LINES : GL_TRIANGLES, 0, numVertices);
    }
}
