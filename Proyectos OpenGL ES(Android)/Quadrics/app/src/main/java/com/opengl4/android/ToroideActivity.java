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
    private float radio, radio2;

    private SeekBar spSubdivision, spradio, spradio2;

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
                renderer.setToroide(radio, radio2, subdivision, subdivision);
            }
        });
    }

    private void obtener_spinners()
    {
        spSubdivision=(SeekBar)findViewById(R.id.sbSubdivisions);
        spSubdivision.setMax(maxMN-minMN+1);

        spradio=(SeekBar)findViewById(R.id.sbRadio);
        spradio.setMax(20);
        spradio2=(SeekBar)findViewById(R.id.sbRadio2);
        spradio2.setMax(20);

        spSubdivision.setOnSeekBarChangeListener(this);
        spradio.setOnSeekBarChangeListener(this);
        spradio2.setOnSeekBarChangeListener(this);
    }

    private void obtener_valores()
    {
        subdivision=spSubdivision.getProgress()+minMN;
        Log.d("obtener_valores", "Subdivision: "+subdivision);

        radio=(spradio.getProgress())/100.f+0.05f;
        radio2=(spradio2.getProgress())/100.f+0.05f;
    }
}
