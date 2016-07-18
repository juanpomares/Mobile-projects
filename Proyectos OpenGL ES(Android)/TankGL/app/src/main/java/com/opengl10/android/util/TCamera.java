package com.opengl10.android.util;


import android.util.Log;

import static android.opengl.Matrix.*;

/**
 * Created by mastermoviles on 7/1/16.
 */
public class TCamera
{
    float[] viewmatrix=new float[16];

    float[] punto_a_mirar;
    float[] situacion_camara;
    float[] up_vector={0, 1, 0};

    float distancia=5;
    private float Rx=0, Ry=0;
    private float MaxRy=-5, MinRy=-70;



    public TCamera()
    {
        punto_a_mirar=new float[3];
        situacion_camara=new float[3];

        for(int i=0; i<3; i++)
        {
            punto_a_mirar[i]=situacion_camara[i]=0;
        }


        calcularPuntoDondeMirar();
        recalculateViewMatrix();
    }

    public float[] getViewmatrix()
    {
        return viewmatrix;
    }

    private void calcularPuntoDondeMirar()
    {
        // Calculate the camera position using the distance and angles
        float radiansRX=(float)Math.toRadians(Rx);
        float radiansRY=(float)Math.toRadians(Ry);

        situacion_camara[0] =(float)( distancia * -Math.sin(radiansRX) * Math.cos(radiansRY));
        situacion_camara[1] =(float)( distancia * -Math.sin(radiansRY));
        situacion_camara[2] =(float)(-distancia * Math.cos(radiansRX) * Math.cos(radiansRY));

        for(int i=0; i<3; i++)
            situacion_camara[i]+=punto_a_mirar[i];

        recalculateViewMatrix();
    }

    public void setDistancia(float distance)
    {
        distancia=Math.max(0.01f, distance);
        calcularPuntoDondeMirar();
    }

    public float getRx(){return Rx;}
    public float getRy(){return Ry;}


    public void setRotacion(float rx, float ry)
    {
        while(rx>360) rx-=360;
        while(rx<0) rx+=360;
        this.Rx=rx;

        this.Ry=Math.max(MinRy, Math.min(ry, MaxRy));
        calcularPuntoDondeMirar();
    }



    public void setPuntoAMirarTarget(float x, float y, float z)
    {
        punto_a_mirar[0]=x;
        punto_a_mirar[1]=y;
        punto_a_mirar[2]=z;

        calcularPuntoDondeMirar();
    }

    public void setPuntoAMirar(float x, float y, float z)
    {
        punto_a_mirar[0]=x;
        punto_a_mirar[1]=y;
        punto_a_mirar[2]=z;

        recalculateViewMatrix();
    }

    public void setPuntoDondeMirar(float x, float y, float z)
    {
        situacion_camara[0]=x;
        situacion_camara[1]=y;
        situacion_camara[2]=z;

        recalculateViewMatrix();
    }

    public void setUpVector(float x, float y, float z)
    {
        up_vector[0]=x;
        up_vector[1]=y;
        up_vector[2]=z;

        recalculateViewMatrix();
    }

    private void recalculateViewMatrix()
    {
        float[] dif=new float[3];

        for(int i=0; i<3; i++)
            dif[i]=situacion_camara[i] - punto_a_mirar[i];

        float[] vz= normalize(dif);
        float[] vx = normalize(crossProduct(up_vector, vz));
        // vy doesn't need to be normalized because it's a cross
        // product of 2 normalized vectors
        float[] vy = crossProduct(vz, vx);

        float[] inverseVieWMatrix=new float[16];

        int k=0;
        for(int i=0; i<3; i++)
        {
            inverseVieWMatrix[k++]=vx[i];
        }
        inverseVieWMatrix[k++]=0;

        for(int i=0; i<3; i++)
        {
            inverseVieWMatrix[k++]=vy[i];
        }
        inverseVieWMatrix[k++]=0;

        for(int i=0; i<3; i++)
        {
            inverseVieWMatrix[k++]=vz[i];
        }
        inverseVieWMatrix[k++]=0;

        for(int i=0; i<3; i++)
        {
            inverseVieWMatrix[k++]=situacion_camara[i];
        }
        inverseVieWMatrix[k++]=1;

        invertM(viewmatrix, 0, inverseVieWMatrix, 0);

        /*Matrix inverseViewMatrix =
                new Matrix(new Vector4(vx, 0),
                new Vector4(vy, 0),
                new Vector4(vz, 0),
                new Vector4(eye, 1));
        return inverseViewMatrix.getInverse();*/

    }

    private float[] normalize(float[] vector)
    {
        float[] dv=new float[3];

        float modulo=0;
        for(int i=0; i<3; i++)
            modulo+=vector[i]*vector[i];

        modulo=(float)Math.sqrt(modulo);


        for(int i=0; i<3; i++)
            dv[i]=vector[i]/modulo;

        return dv;
    }

    private float[] crossProduct(float[] A, float[] B)
    {
        float[] dv=new float[3];

        dv[0]=A[1]*B[2]-A[2]*B[1];
        dv[1]=A[2]*B[0]-A[0]*B[2];
        dv[2]=A[0]*B[1]-A[1]*B[0];

        return dv;
    }

}
