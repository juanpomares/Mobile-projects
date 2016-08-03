package com.opengl10.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.rotateM;
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
            //  x, y, R, G, B, A
            //Up
             0.0f, 1.0f, 0.5f, 0.5f, 1.0f, 0.5f,
            -0.1f, 0.8f, 0.0f, 0.0f, 0.8f, 0.5f,
             0.1f, 0.8f, 0.0f, 0.0f, 0.8f, 0.5f,

            //Down
            0.0f,  -1.0f, 0.5f, 0.5f, 1.0f, 0.5f,
            0.1f,  -0.8f, 0.0f, 0.0f, 0.8f, 0.5f,
            -0.1f, -0.8f, 0.0f, 0.0f, 0.8f, 0.5f,


            //Left
            -1.0f,  0.0f, 0.5f, 0.5f, 1.0f, 0.5f,
            -0.8f, -0.1f, 0.0f, 0.0f, 0.8f, 0.5f,
            -0.8f,  0.1f, 0.0f, 0.0f, 0.8f, 0.5f,

            //Right
            1.0f,  0.0f, 0.5f, 0.5f, 1.0f, 0.5f,
            0.8f,  0.1f, 0.0f, 0.0f, 0.8f, 0.5f,
            0.8f, -0.1f, 0.0f, 0.0f, 0.8f, 0.5f,

            //Left Turret
            -1.0f, -0.9f, 0.5f, 1.0f, 0.5f, 0.5f,
            -0.8f, -1.0f, 0.0f, 0.8f, 0.0f, 0.5f,
            -0.8f, -0.8f, 0.0f, 0.8f, 0.0f, 0.5f,

            //Right Turret
            1.0f, -0.9f, 0.5f, 1.0f, 0.5f, 0.5f,
            0.8f, -0.8f, 0.0f, 0.8f, 0.0f, 0.5f,
            0.8f, -1.0f, 0.0f, 0.8f, 0.0f, 0.5f
    };

    private int PointsLength;


    public TTriangles(){}

    private void loadUniforms()
    {
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(renderer.aSimpleColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);
        vertexData.position(0);
        glVertexAttribPointer(renderer.aSimplePositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);

    }


    public void draw()
    {
        loadUniforms();
        glDrawArrays(GL_TRIANGLES, 0, PointsLength);
    }

    public void onSurfaceCreate(OpenGLRenderer renderer)
    {
        this.renderer=renderer;
        PointsLength =tablaVertices.length/(COLOR_COMPONENT_COUNT+POSITION_COMPONENT_COUNT);

        vertexData = ByteBuffer
                .allocateDirect(PointsLength * STRIDE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tablaVertices);

        glEnableVertexAttribArray(renderer.aSimplePositionLocation);
        glEnableVertexAttribArray(renderer.aSimpleColorLocation);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

}
