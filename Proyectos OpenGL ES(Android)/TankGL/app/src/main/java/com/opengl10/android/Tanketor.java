package com.opengl10.android;

import android.content.Context;

import com.opengl10.android.util.Tobject;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glUniform1i;

/**
 * Created by mastermoviles on 7/1/16.
 */
public class Tanketor
{
    private Tobject torreta, cuerpo, cubre;
    private Tobject[] rueda;

    private static int cant_frames=4;
    private int actual=0;

    private float rotacionY=0;
    private float positionX=0, positionY=0, positionZ=0;


    public Tanketor(OpenGLRenderer ogl)
    {
        cuerpo=new Tobject(ogl, R.raw.basetanque, R.drawable.tanquebasetextura);
        torreta=new Tobject(ogl, R.raw.torretatanque, R.drawable.tanquetorretatextura);
        rueda=new Tobject[cant_frames];

        rueda[0]=new Tobject(ogl, R.raw.ruedas0tanque, R.drawable.tanqueruedatextura);
        rueda[1]=new Tobject(ogl, R.raw.ruedas1tanque, R.drawable.tanqueruedatextura);
        rueda[2]=new Tobject(ogl, R.raw.ruedas2tanque, R.drawable.tanqueruedatextura);
        rueda[3]=new Tobject(ogl, R.raw.ruedas3tanque, R.drawable.tanqueruedatextura);


        cubre=new Tobject(ogl, R.raw.cubretanque, R.drawable.tanquecubreruedatextura);
    }

    public float getPositionX(){return positionX;}
    public float getPositionY(){return positionY;}
    public float getPositionZ(){return positionZ;}


    public void onSurfaceCreated()
    {
        cuerpo.onSurfaceCreated();
        torreta.onSurfaceCreated();
        cubre.onSurfaceCreated();
        for(int i=0; i<cant_frames; i++)
            rueda[i].onSurfaceCreated();

    }

    public void rotarTorreta(float Ary)
    {
        torreta.setRotacion(torreta.getrX(), torreta.getrY() + Ary);
    }

    public void rotar(float Ary)
    {
        cambiar_frameActual(Ary>0?1:-1);
        rotacionY+=Ary;

        while(rotacionY>360) rotacionY-=360;
        while(rotacionY<0) rotacionY+=360;

        cuerpo.setRotacion(cuerpo.getrX(), cuerpo.getrY()+Ary);
        torreta.setRotacion(torreta.getrX(), torreta.getrY() + Ary);
        cubre.setRotacion(cubre.getrX(), cubre.getrY() + Ary);
        for(int i=0; i<cant_frames; i++)
            rueda[i].setRotacion(rueda[i].getrX(), rueda[i].getrY()+Ary);
    }

    public void draw(float[] projectionViewMatrix)
    {
        cuerpo.draw(projectionViewMatrix);
        torreta.draw(projectionViewMatrix);
        rueda[actual].draw(projectionViewMatrix);


        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        cubre.draw(projectionViewMatrix);
        glDisable(GL_BLEND);
    }

    public void mover(float pasos)
    {
        float radiansRY=(float)Math.toRadians(rotacionY);
        float AZ=(float)(pasos*Math.cos(radiansRY));
        float AX=(float)(pasos*Math.sin(radiansRY));

        positionX+=AX;
        positionZ+=AZ;


        cuerpo.move(AX, 0, AZ);
        torreta.move(AX, 0, AZ);
        cubre.move(AX, 0, AZ);
        for(int i=0; i<cant_frames; i++)
            rueda[i].move(AX, 0, AZ);

        cambiar_frameActual(pasos>0?1:-1);
    }

    private void cambiar_frameActual(int dif)
    {
        actual+=dif;
        if(actual<0) actual=cant_frames-1;
        else if(actual==cant_frames) actual=0;
    }
}
