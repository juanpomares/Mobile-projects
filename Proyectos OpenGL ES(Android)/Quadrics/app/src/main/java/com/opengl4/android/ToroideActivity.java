package com.opengl4.android;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

/**
 * Created by Usuario on 04/01/2016.
 */
public class ToroideActivity extends OpenGLActivity
{

    private static int minMN=3, maxMN=20;

    private int subdivision;
    private float mRadius, mRadius2;

    private SeekBar mSKSubdivision, mSKRadius, mSKRadius2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toroidelayout);
        glSurfaceView=(GLSurfaceView)findViewById(R.id.SurfaceView);

        super.iniciar_Activity(R.drawable.donut);

        obtener_spinners();
        crear_figura();
    }

    @Override
    public void crear_figura()
    {
        obtener_valores();
        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run()
            {
                renderer.setToroide(mRadius, mRadius2, subdivision, subdivision);
            }
        });
    }

    private void obtener_spinners()
    {
        mSKSubdivision =(SeekBar)findViewById(R.id.sbSubdivisions);
        mSKSubdivision.setMax(maxMN-minMN+1);

        mSKRadius =(SeekBar)findViewById(R.id.sbRadio);
        mSKRadius.setMax(20);
        mSKRadius2 =(SeekBar)findViewById(R.id.sbRadio2);
        mSKRadius2.setMax(20);

        mSKSubdivision.setOnSeekBarChangeListener(this);
        mSKRadius.setOnSeekBarChangeListener(this);
        mSKRadius2.setOnSeekBarChangeListener(this);
    }

    private void obtener_valores()
    {
        subdivision= mSKSubdivision.getProgress()+minMN;
        Log.d("obtener_valores", "Subdivision: "+subdivision);

        mRadius =(mSKRadius.getProgress())/100.f+0.05f;
        mRadius2 =(mSKRadius2.getProgress())/100.f+0.05f;
    }
}
