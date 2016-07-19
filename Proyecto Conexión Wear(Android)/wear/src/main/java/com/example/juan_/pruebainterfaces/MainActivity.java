package com.example.juan_.pruebainterfaces;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.util.List;

public class MainActivity extends Activity implements ButtonListener, JoystickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {
    private TextView mTextView;

    GoogleApiClient mApiClient;

    Node mTelephone =null;

    boolean mMustNotifyDestroy =true;
    boolean mSensorsActived =false;
 //   boolean mDestroyonStop =true;
    boolean mWearableMessageApi=false;

    long MAX_MILIS_BETWEENUPDATES=125;

    SensorManager mSensorManager;
    SensorEventListener mOrientationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("onCreate", "entro");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        setContentView(R.layout.activity_text);
        ((TextView)findViewById(R.id.text)).setText("Establecida conexión correctamente");

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mOrientationListener=new SensorEventListener()
        {
            float []mGravity;
            float []mGeoMagentic;
            long mLastOrientationSent=0;

            float []R=new float[9];
            float []orientation=new float[3];

            @Override
            public void onSensorChanged(SensorEvent event)
            {
                switch(event.sensor.getType())
                {
                    case Sensor.TYPE_ACCELEROMETER:
                        mGravity=event.values;
                        break;

                    case Sensor.TYPE_MAGNETIC_FIELD:
                        mGeoMagentic=event.values;
                        break;
                }

                if(System.currentTimeMillis()-mLastOrientationSent<MAX_MILIS_BETWEENUPDATES)
                    return;

                if(mGeoMagentic!=null && mGravity!=null)
                {
                    if(SensorManager.getRotationMatrix(R, null, mGravity, mGeoMagentic))
                    {
                        SensorManager.getOrientation(R, orientation);
                        sendMessage("GyroscopeValues", orientation[0] + "#" + orientation[1] + "#" + orientation[2]);
                        mLastOrientationSent=System.currentTimeMillis();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };


        if(AppSharedPreferences.getAppOpen(this))
        {
            Log.d("Error", "App ya abierta");
        }else
            AppSharedPreferences.setAppOpen(this, true);

    }


    @Override
    protected void onResume() {
        super.onResume();
        initGoogleApiClient();
    }


    @Override
    protected void onStop() {
        super.onStop();

        DeactiveSensors();
        Log.d("onStop", "entro");
        if(mMustNotifyDestroy && mWearableMessageApi && mApiClient.isConnected())
        {
            mMustNotifyDestroy=false;
            sendMessage(PublicConstants.DISCONNECTION, "Adios :)");
        }else {

            if (mWearableMessageApi) {
                Wearable.MessageApi.removeListener(mApiClient, this);
                mWearableMessageApi = false;
            }
            mApiClient.disconnect();
        }

        AppSharedPreferences.setAppOpen(this, false);


        MainActivity.this.finish();
    }

    private void initGoogleApiClient()
    {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        //Obtener el movil
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>()
        {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result)
            {
                List<Node> nodes=result.getNodes();

                if(nodes.size()>0)
                    mTelephone = nodes.get(0);

                if (mTelephone == null)
                {
                    mMustNotifyDestroy=false;
                    MainActivity.this.finish();
                }
                else
                    sendMessage(PublicConstants.CONNECTION_WEAR, "");
            }
        });
        Wearable.MessageApi.addListener(mApiClient, this);
        mWearableMessageApi=true;
    }

    @Override
    public void onConnectionSuspended(int i) {Log.d("onConnectionSuspended", "error: "+i);}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.d("onConnectionFailed", "error: "+connectionResult.getErrorCode());
    }

    public void DeactiveSensors()
    {
        if(mSensorsActived)
        {
            mSensorsActived = false;
            mSensorManager.unregisterListener(mOrientationListener);
        }
    }

    public void ActiveSensors()
    {
        if(!mSensorsActived)
        {
            List<Sensor> sensorsAcelerometer = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            List<Sensor> sensorsMagnetic = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
            if (sensorsAcelerometer.size() > 0 && sensorsMagnetic.size()>0)
            {
                mSensorManager.registerListener(mOrientationListener, sensorsAcelerometer.get(0), SensorManager.SENSOR_DELAY_GAME);
                mSensorManager.registerListener(mOrientationListener, sensorsMagnetic.get(0), SensorManager.SENSOR_DELAY_GAME);
                mSensorsActived = true;
            }
        }
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {
        String path = messageEvent.getPath();
        byte[] data = messageEvent.getData();
        String sData=new String(data);
        Log.d("onMessageReceived", "P: " + path + " D: " + sData);


        switch (path)
        {
            case PublicConstants.STOP_ACTIVITY:
                mMustNotifyDestroy =false;
                this.finish();
                break;

            case PublicConstants.CHANGE_LAYOUT:


                if(sData.contains("botón"))
                {
                    TipoVista nvista;

                    if(sData.contains("Pad"))
                        nvista=TipoVista.VistaButtonPad;
                    else if(sData.contains("Horizontal"))
                        nvista=TipoVista.VistaButtonHorizontal;
                    else
                        nvista=TipoVista.VistaButtonVertical;



                    setContentView(new VistaButton(this, this, nvista, sData.contains("con")));
                }else if(sData.contains("virtual"))//Joystick virtual
                {
                    setContentView(new VistaJoystick(this, this));
                }else// vista con texto simple
                {

                    setContentView(R.layout.activity_text);
                    ((TextView)findViewById(R.id.text)).setText(sData);
                }
                break;

            case PublicConstants.ACTIVE_SENSORS:
                ActiveSensors();
                break;

            case PublicConstants.DEACTIVE_SENSORS:
                DeactiveSensors();
                break;

           /* case PublicConstants.RESTART_ACTIVITY:
            {

                mDestroyonStop =false;
                break;
            }*/
        }
    }




    public void sendMessage(String path, String buffer)
    {
        ByteBuffer bf=ByteBuffer.allocate(buffer.length());
        bf.put(buffer.getBytes());

        sendMessage(path, bf);
    }

    public void sendMessage(String path, ByteBuffer buffer)
    {
        if(mTelephone !=null)
        {
            final String SMpath=path;
            final ByteBuffer SMBuffer=buffer;
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, mTelephone.getId(), SMpath, SMBuffer.array()).await();
                    if (!result.getStatus().isSuccess())
                        Log.e("sendMessage", "ERROR: failed to send Message: " + result.getStatus());
                     else
                        if(SMpath.equals(PublicConstants.DISCONNECTION))
                        {
                            Wearable.MessageApi.removeListener(mApiClient, MainActivity.this);
                            mWearableMessageApi=false;
                            mApiClient.disconnect();
                        }


                }
            }).start();
        }else
            Log.d("sendMessage", "mTelephone es null :0");
    }



    @Override
    public void onButtonPress(NombreBoton pulsado)
    {
        sendMessage(PublicConstants.BUTTON_PRESS, PublicConstants.BUTTONS_NAME[pulsado.ordinal()]);

    }

    @Override
    public void onButtonHold(NombreBoton soltado) {

        sendMessage(PublicConstants.BUTTON_RELEASE, PublicConstants.BUTTONS_NAME[soltado.ordinal()]);
    }

    @Override
    public void onPositionChange(float newX, float newY)
    {
        sendMessage(PublicConstants.JOYSTICK_VALUES, newX+"#"+newY);

    }
}
