package com.opengl10.android.util;


import static android.opengl.Matrix.*;

/**
 * Created by mastermoviles on 7/1/16.
 */
public class TCamera
{
    float[] mViewMatrix =new float[16];

    float[] mLookPoint;
    float[] mPositionCamera;
    float[] mUpVector ={0, 1, 0};

    float mDistance =5;
    private float mRotationX =0, mRotationY =0;
    private float mMaxRotationY =-5, mMinRotationY =-70;



    public TCamera()
    {
        mLookPoint =new float[3];
        mPositionCamera =new float[3];

        for(int i=0; i<3; i++)
        {
            mLookPoint[i]= mPositionCamera[i]=0;
        }


        calculateLookPoint();
        recalculateViewMatrix();
    }

    public float[] getViewmatrix()
    {
        return mViewMatrix;
    }

    private void calculateLookPoint()
    {
        // Calculate the camera position using the distance and angles
        float radiansRX=(float)Math.toRadians(mRotationX);
        float radiansRY=(float)Math.toRadians(mRotationY);

        mPositionCamera[0] =(float)( mDistance * -Math.sin(radiansRX) * Math.cos(radiansRY));
        mPositionCamera[1] =(float)( mDistance * -Math.sin(radiansRY));
        mPositionCamera[2] =(float)(-mDistance * Math.cos(radiansRX) * Math.cos(radiansRY));

        for(int i=0; i<3; i++)
            mPositionCamera[i]+= mLookPoint[i];

        recalculateViewMatrix();
    }

    public void setDistance(float distance)
    {
        mDistance =Math.max(0.01f, distance);
        calculateLookPoint();
    }

    public float getRotationX(){return mRotationX;}
    public float getRotationY(){return mRotationY;}


    public void setCameraRotation(float rx, float ry)
    {
        while(rx>360) rx-=360;
        while(rx<0) rx+=360;
        this.mRotationX =rx;

        this.mRotationY =Math.max(mMinRotationY, Math.min(ry, mMaxRotationY));
        calculateLookPoint();
    }



    public void setTarget(float x, float y, float z)
    {
        mLookPoint[0]=x;
        mLookPoint[1]=y;
        mLookPoint[2]=z;

        calculateLookPoint();
    }

    public void setLookPoint(float x, float y, float z)
    {
        mLookPoint[0]=x;
        mLookPoint[1]=y;
        mLookPoint[2]=z;

        recalculateViewMatrix();
    }

    public void setCameraPosition(float x, float y, float z)
    {
        mPositionCamera[0]=x;
        mPositionCamera[1]=y;
        mPositionCamera[2]=z;

        recalculateViewMatrix();
    }

    public void setUpVector(float x, float y, float z)
    {
        mUpVector[0]=x;
        mUpVector[1]=y;
        mUpVector[2]=z;

        recalculateViewMatrix();
    }

    private void recalculateViewMatrix()
    {
        float[] dif=new float[3];

        for(int i=0; i<3; i++)
            dif[i]= mPositionCamera[i] - mLookPoint[i];

        float[] vz= normalize(dif);
        float[] vx = normalize(crossProduct(mUpVector, vz));
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
            inverseVieWMatrix[k++]= mPositionCamera[i];
        }
        inverseVieWMatrix[k++]=1;

        invertM(mViewMatrix, 0, inverseVieWMatrix, 0);

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

        float length=0;
        for(int i=0; i<3; i++)
            length+=vector[i]*vector[i];

        length=(float)Math.sqrt(length);


        for(int i=0; i<3; i++)
            dv[i]=vector[i]/length;

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
