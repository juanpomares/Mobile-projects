package com.opengl10.android;

import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINE_LOOP;
import static android.opengl.GLES20.GL_LINE_STRIP;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by mastermoviles on 21/12/15.
 */
public class TTriangles
{
    private OpenGLRenderer renderer;
    private static final int BYTES_PER_FLOAT = 4;
    private FloatBuffer vertexData;

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 4;
    // Cálculo del tamaño de los datos (6 floats)
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    float[] tablaVertices =
            {
            // Abanico de triángulos, x, y, R, G, B, A
            //Arriba
             0.0f, 1.0f, 0.5f, 0.5f, 1.0f, 0.5f,
            -0.1f, 0.8f, 0.0f, 0.0f, 0.8f, 0.5f,
             0.1f, 0.8f, 0.0f, 0.0f, 0.8f, 0.5f,

            //Abajo
            0.0f,  -1.0f, 0.5f, 0.5f, 1.0f, 0.5f,
            0.1f,  -0.8f, 0.0f, 0.0f, 0.8f, 0.5f,
            -0.1f, -0.8f, 0.0f, 0.0f, 0.8f, 0.5f,


            //Izquierda
            -1.0f,  0.0f, 0.5f, 0.5f, 1.0f, 0.5f,
            -0.8f, -0.1f, 0.0f, 0.0f, 0.8f, 0.5f,
            -0.8f,  0.1f, 0.0f, 0.0f, 0.8f, 0.5f,

            //Derecha
            1.0f,  0.0f, 0.5f, 0.5f, 1.0f, 0.5f,
            0.8f,  0.1f, 0.0f, 0.0f, 0.8f, 0.5f,
            0.8f, -0.1f, 0.0f, 0.0f, 0.8f, 0.5f,

            //Torreta Izquierda
            -1.0f, -0.9f, 0.5f, 1.0f, 0.5f, 0.5f,
            -0.8f, -1.0f, 0.0f, 0.8f, 0.0f, 0.5f,
            -0.8f, -0.8f, 0.0f, 0.8f, 0.0f, 0.5f,

            //Torreta Derecha
            1.0f, -0.9f, 0.5f, 1.0f, 0.5f, 0.5f,
            0.8f, -0.8f, 0.0f, 0.8f, 0.0f, 0.5f,
            0.8f, -1.0f, 0.0f, 0.8f, 0.0f, 0.5f
    };

    private int cantidad_puntos;


    public TTriangles()
    {

    }

    private void cargar_uniform()
    {
        // Asociamos el vector de colores
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(renderer.aSimpleColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);
        vertexData.position(0);
        //glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
        //false, 0, vertexData);
        glVertexAttribPointer(renderer.aSimplePositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);

    }


    public void draw()
    {
        // Env�a la matriz de proyecci�n multiplicada por modelMatrix al shader
        //glUniformMatrix4fv(renderer.uSimpleMVP, 1, false, modelMatrix, 0);
        cargar_uniform();
        glDrawArrays(GL_TRIANGLES, 0, cantidad_puntos);
    }

    public void onSurfaceCreate(OpenGLRenderer renderer)
    {
        this.renderer=renderer;
        cantidad_puntos=tablaVertices.length/(COLOR_COMPONENT_COUNT+POSITION_COMPONENT_COUNT);

        vertexData = ByteBuffer
                .allocateDirect(cantidad_puntos * STRIDE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tablaVertices);


        // Asociando vértices con su attribute

        // Habilitamos el vector de vértices
        glEnableVertexAttribArray(renderer.aSimplePositionLocation);
        glEnableVertexAttribArray(renderer.aSimpleColorLocation);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

}
