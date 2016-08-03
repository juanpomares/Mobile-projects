package com.opengl10.android;

import com.opengl10.android.util.Tobject;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

/**
 * Created by mastermoviles on 7/1/16.
 */
public class Tanketor
{
    private Tobject mTurret, mBody, mCover;
    private Tobject[] mWheels;

    private static int mFramesCount =4;
    private int mActualFrame =0;

    private float mRotationY =0;
    private float mPositionX =0, mPositionY =0, mPositionZ =0;


    public Tanketor(OpenGLRenderer ogl)
    {
        mBody =new Tobject(ogl, R.raw.tank_cover, R.drawable.tank_texture_base);
        mTurret =new Tobject(ogl, R.raw.tank_turret, R.drawable.tank_texture_turret);
        mWheels =new Tobject[mFramesCount];

        mWheels[0]=new Tobject(ogl, R.raw.tank_wheel0, R.drawable.tank_texture_wheel);
        mWheels[1]=new Tobject(ogl, R.raw.tank_wheel1, R.drawable.tank_texture_wheel);
        mWheels[2]=new Tobject(ogl, R.raw.tank_wheel2, R.drawable.tank_texture_wheel);
        mWheels[3]=new Tobject(ogl, R.raw.tank_wheel3, R.drawable.tank_texture_wheel);


        mCover =new Tobject(ogl, R.raw.tank_covering, R.drawable.tank_texture_cover_wheel);
    }

    public float getPositionX(){return mPositionX;}
    public float getPositionY(){return mPositionY;}
    public float getPositionZ(){return mPositionZ;}


    public void onSurfaceCreated()
    {
        mBody.onSurfaceCreated();
        mTurret.onSurfaceCreated();
        mCover.onSurfaceCreated();
        for(int i = 0; i< mFramesCount; i++)
            mWheels[i].onSurfaceCreated();

    }

    public void rotateTurret(float Ary)
    {
        mTurret.setRotation(mTurret.getrX(), mTurret.getrY() + Ary);
    }

    public void rotate(float Ary)
    {
        changeActualFrame(Ary>0?1:-1);
        mRotationY +=Ary;

        while(mRotationY >360) mRotationY -=360;
        while(mRotationY <0) mRotationY +=360;

        mBody.setRotation(mBody.getrX(), mBody.getrY()+Ary);
        mTurret.setRotation(mTurret.getrX(), mTurret.getrY() + Ary);
        mCover.setRotation(mCover.getrX(), mCover.getrY() + Ary);
        for(int i = 0; i< mFramesCount; i++)
            mWheels[i].setRotation(mWheels[i].getrX(), mWheels[i].getrY()+Ary);
    }

    public void draw(float[] projectionViewMatrix)
    {
        mBody.draw(projectionViewMatrix);
        mTurret.draw(projectionViewMatrix);
        mWheels[mActualFrame].draw(projectionViewMatrix);


        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        mCover.draw(projectionViewMatrix);
        glDisable(GL_BLEND);
    }

    public void move(float pasos)
    {
        float radiansRY=(float)Math.toRadians(mRotationY);
        float AZ=(float)(pasos*Math.cos(radiansRY));
        float AX=(float)(pasos*Math.sin(radiansRY));

        mPositionX +=AX;
        mPositionZ +=AZ;


        mBody.move(AX, 0, AZ);
        mTurret.move(AX, 0, AZ);
        mCover.move(AX, 0, AZ);
        for(int i = 0; i< mFramesCount; i++)
            mWheels[i].move(AX, 0, AZ);

        changeActualFrame(pasos>0?1:-1);
    }

    private void changeActualFrame(int dif)
    {
        mActualFrame +=dif;
        if(mActualFrame <0) mActualFrame = mFramesCount -1;
        else if(mActualFrame == mFramesCount) mActualFrame =0;
    }
}
