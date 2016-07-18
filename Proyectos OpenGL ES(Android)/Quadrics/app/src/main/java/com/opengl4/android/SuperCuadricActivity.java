package com.opengl4.android;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

/**
 * Created by Usuario on 04/01/2016.
 */
public class SuperCuadricActivity extends OpenGLActivity
{
    private static int minMN=2, maxMN=20;

    private int subdivision;
    private float radio, radio2, radio3, s1, s2;

    private SeekBar spSubdivision, spradio, spradio2, spradio3, sps1, sps2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        s1=s2=0.5f;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.supercuadriclayout);
        glSurfaceView=(GLSurfaceView)findViewById(R.id.SurfaceView);

        super.iniciar_Activity(R.drawable.brick);


        obtener_spinners();
        crear_figura();

        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.handleTouchDrag(0, -1);
            }
        });

    }

    @Override
    public void crear_figura()
    {
        obtener_valores();
        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.setSuperCuadrica(radio, radio2, radio3, s1, s2, subdivision, subdivision);
            }
        });
    }


    private void obtener_spinners()
    {
        spSubdivision=(SeekBar)findViewById(R.id.sbSubdivisions);
        spSubdivision.setMax(maxMN-minMN+1);
        spradio=(SeekBar)findViewById(R.id.sbRadio);
        spradio.setMax(50);
        spradio2=(SeekBar)findViewById(R.id.sbRadio2);
        spradio2.setMax(50);
        spradio3=(SeekBar)findViewById(R.id.sbRadio3);
        spradio3.setMax(50);


        sps1=(SeekBar)findViewById(R.id.sbs1);
        sps1.setMax(35);
        sps2=(SeekBar)findViewById(R.id.sbs2);
        sps2.setMax(35);

        spSubdivision.setOnSeekBarChangeListener(this);
        spradio.setOnSeekBarChangeListener(this);
        spradio2.setOnSeekBarChangeListener(this);
        spradio3.setOnSeekBarChangeListener(this);
        sps1.setOnSeekBarChangeListener(this);
        sps2.setOnSeekBarChangeListener(this);

    }

    private void obtener_valores()
    {
        subdivision=spSubdivision.getProgress()+minMN;
        radio=(spradio.getProgress())/100.f+0.025f;
        radio2=(spradio2.getProgress())/100.f+0.025f;
        radio3=(spradio3.getProgress())/100.f+0.025f;
        s1=(sps1.getProgress())/10.0f;
        s2=(sps2.getProgress())/10.0f;
    }
}
