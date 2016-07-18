package com.opengl4.android;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;

/**
 * Created by Usuario on 04/01/2016.
 */
public class SphereActivity extends OpenGLActivity
{

    private static int minMN=3, maxMN=10;

    private int subdivision;
    private float radio;

    private SeekBar spSubdivision, spradio;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spherelayout);
        glSurfaceView=(GLSurfaceView)findViewById(R.id.SurfaceView);

        super.iniciar_Activity(R.drawable.lines);

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
                renderer.setEsfera(radio, subdivision, subdivision);
            }
        });
    }


    private void obtener_spinners()
    {
        spSubdivision=(SeekBar)findViewById(R.id.sbSubdivisions);
        spradio=(SeekBar)findViewById(R.id.sbRadio);
        spradio.setMax(10);



        spSubdivision.setOnSeekBarChangeListener(this);
        spradio.setOnSeekBarChangeListener(this);
    }

    private void obtener_valores()
    {
        subdivision=(int)(spSubdivision.getProgress()/(100.0f)*maxMN+minMN)*2;
        radio=(spradio.getProgress())/25.f+0.1f;

        Log.d("obtener_valores", ""+subdivision);

    }
}
