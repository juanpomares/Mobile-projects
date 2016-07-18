package com.opengl10.android.util;


import android.content.Context;
import android.opengl.Matrix.*;

import com.opengl10.android.OpenGLRenderer;

import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by mastermoviles on 21/12/15.
 */
public class Tobject
{
    Resource3DSReader obj3DS;
    int texturaResource;
    int texture;

    OpenGLRenderer renderer;
    private final float[] modelMatrix = new float[16];
    private final float[] MVP = new float[16];
    // Rotaci�n alrededor de los ejes
    private float rX=0, rY=0;
    private float[] position={0,0,0};



    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int UV_COMPONENT_COUNT = 2;
    // C�lculo del tama�o de los datos (3+3+2 = 8 floats)
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT + UV_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    public Tobject(OpenGLRenderer oglr, int Obj3ds, int Texture)
    {
        renderer=oglr;
        // Lee un archivo 3DS desde un recurso
        obj3DS = new Resource3DSReader();
        obj3DS.read3DSFromResource(renderer.getContext(), Obj3ds);
        texturaResource=Texture;
        calcularModelMatrix();
    }


    public void onSurfaceCreated()
    {
        texture = TextureHelper.loadTexture(renderer.getContext(), texturaResource);
    }

    public float getrX(){return rX;}
    public float getrY(){return rY;}


    public float getPosition(int axis)
    {
        return position[axis];
    }

    public void move(float x, float y, float z)
    {
        setPosition(position[0]+x, position[1]+y, position[2]+z);
    }

    public void setPosition(float x, float y, float z)
    {
        position[0]=x;
        position[1]=y;
        position[2]=z;

        calcularModelMatrix();
    }


    public void setRotacion(float rx, float ry)
    {
        this.rX=rx;
        this.rY=ry;

        calcularModelMatrix();
    }

    private void calcularModelMatrix()
    {
        float[] rotationMatrix=new float[16];
        setIdentityM(rotationMatrix, 0);
        rotateM(rotationMatrix, 0, rY, 0f, 1f, 0f);
        rotateM(rotationMatrix, 0, rX, 1f, 0f, 0f);

        float[] translationMatrix=new float[16];
        setIdentityM(translationMatrix, 0);
        translateM(translationMatrix, 0, position[0], position[1], position[2]);

        multiplyMM(modelMatrix, 0, translationMatrix, 0, rotationMatrix, 0);

        //multiplyMM(modelMatrix, 0, rotationMatrix, 0, translationMatrix, 0);
        //translateM(modelMatrix, 0, position[0], position[1], position[2]);
    }


    public void draw(float[] projectionMatrix)
    {
        multiplyMM(MVP, 0, projectionMatrix, 0, modelMatrix, 0);
        //System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);


        // Env�a la matriz de proyecci�n multiplicada por modelMatrix al shader
        glUniformMatrix4fv(renderer.uMVPMatrixLocation, 1, false, MVP, 0);
        // Env�a la matriz modelMatrix al shader
        glUniformMatrix4fv(renderer.uMVMatrixLocation, 1, false, modelMatrix, 0);
        // Actualizamos el color (Marr�n)
        //glUniform4f(uColorLocation, 0.78f, 0.49f, 0.12f, 1.0f);
        glUniform4f(renderer.uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);

        // Pasamos la textura
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        glUniform1f(renderer.uTextureUnitLocation, 0);

        // Dibujamos el objeto
        for (int i=0; i<obj3DS.numMeshes; i++)
        {
            // Asociando v�rtices con su attribute
            obj3DS.dataBuffer[i].position(0);
            glVertexAttribPointer(renderer.aSpecularPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                    false, STRIDE, obj3DS.dataBuffer[i]);

            // Asociamos el vector de normales
            obj3DS.dataBuffer[i].position(POSITION_COMPONENT_COUNT);
            glVertexAttribPointer(renderer.aNormalLocation, NORMAL_COMPONENT_COUNT, GL_FLOAT,
                    false, STRIDE, obj3DS.dataBuffer[i]);

            // Asociamos el vector de UVs
            obj3DS.dataBuffer[i].position(POSITION_COMPONENT_COUNT+NORMAL_COMPONENT_COUNT);
            glVertexAttribPointer(renderer.aUVLocation, NORMAL_COMPONENT_COUNT, GL_FLOAT,
                    false, STRIDE, obj3DS.dataBuffer[i]);
            glDrawArrays(GL_TRIANGLES, 0, obj3DS.numVertices[i]);
        }
    }
}
