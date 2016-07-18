package com.opengl4.android;

import com.opengl4.android.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;


public class OpenGLActivity extends Activity implements SeekBar.OnSeekBarChangeListener
{
	protected GLSurfaceView glSurfaceView;
	private boolean rendererSet = false, cambio;

    protected OpenGLRenderer renderer;
    private float HistoricalNormalizedX, HistoricalNormalizedY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_open_gl1);
        super.onCreate(savedInstanceState);

       /* glSurfaceView = new GLSurfaceView(this);
        setContentView(glSurfaceView);*/
    }

    protected void iniciar_Activity(int textureid)
    {
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        //final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));
        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);
            // Para que funcione en el emulador
            glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            // Assign our renderer.

            glSurfaceView.setRenderer(renderer=new OpenGLRenderer(this, textureid));

            rendererSet = true;
            /*Toast.makeText(this, "OpenGL ES 2.0 supported",
                    Toast.LENGTH_LONG).show();*/
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event != null)
                {
                    float normalizedX =   ((event.getX()) / (float) v.getWidth()) * 2 - 1;
                    float normalizedY = -(((event.getY()) / (float) v.getHeight()) * 2 - 1);

                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        final float nX =   normalizedX;
                        final float nY = normalizedY;

                        HistoricalNormalizedX =normalizedX;
                        HistoricalNormalizedY =normalizedY;
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE)
                    {
                        final float normalizedMoveX =   normalizedX- HistoricalNormalizedX;
                        final float normalizedMoveY =	normalizedY- HistoricalNormalizedY;

                        HistoricalNormalizedX =normalizedX;
                        HistoricalNormalizedY =normalizedY;

                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                renderer.handleTouchDrag(normalizedMoveX, normalizedMoveY);
                            }
                        });

                    }else if (event.getAction() == MotionEvent.ACTION_UP)
                    {

                    }

                    return true;
                } else {
                    return false;
                }
            }
        });



        ((CheckBox)findViewById(R.id.cbNormal)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                renderer.setRenderNormal(isChecked);
            }
        });

        ((CheckBox)findViewById(R.id.cbObject)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                renderer.setRenderObject(isChecked);
            }
        });

        ((CheckBox)findViewById(R.id.cbFragment)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                renderer.setRenderVertexLighting(!isChecked);
            }
        });
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	if (rendererSet) {
    		glSurfaceView.onPause();
    	}
    }
    		
    @Override
    protected void onResume() {
    	super.onResume();
    	if (rendererSet) {
    		glSurfaceView.onResume();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.open_gl1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        cambio=true;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        cambio=false;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        if(cambio)
            crear_figura();
    }


    public void crear_figura(){}
}
