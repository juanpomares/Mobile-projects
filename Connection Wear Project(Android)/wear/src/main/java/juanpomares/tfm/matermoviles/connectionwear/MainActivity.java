package juanpomares.tfm.matermoviles.connectionwear;

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

public class MainActivity extends Activity implements ButtonListener, JoystickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener
{
    GoogleApiClient mApiClient;

    Node mTelephone =null;

    boolean mMustNotifyDestroy =true;
    boolean mSensorsActived =false;
    boolean mWearableMessageApi=false;

    long MAX_MILIS_BETWEENUPDATES=125;

    long last_joystick_send=0;
    float mJoystickNX=0, mJoystickNY=0;

    String AcutalView="None";

    SensorManager mSensorManager;
    SensorEventListener mOrientationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d("onCreateWear", "Creating...");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

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
                        sendMessageAsync(PublicConstants.GYROSCOPE_VALUES, orientation[0] + "#" + orientation[1] + "#" + orientation[2]);
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
            Log.d("Error", "App opened yet!!");
        }else
            AppSharedPreferences.setAppOpen(this, true);

    }


    @Override
    protected void onResume() {
        super.onResume();
        initGoogleApiClient();
    }


    @Override
    protected void onStop()
    {
        super.onStop();

        DeactiveSensors();
        Log.d("onStop", "...");
        if(mMustNotifyDestroy && mWearableMessageApi && mApiClient.isConnected())
        {
            mMustNotifyDestroy=false;
            sendMessageWithCloseApp(PublicConstants.DISCONNECTION, "Bye :)");
            Log.d("onStop", "send disconnected wear");
        }else {
            Log.d("onStop", "else send disconnected wear");

            if (mWearableMessageApi)
            {
                Wearable.MessageApi.removeListener(mApiClient, this);
                mWearableMessageApi = false;
            }
            mApiClient.disconnect();
        }

        AppSharedPreferences.setAppOpen(this, false);
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
        //Get the node of the phone
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
                {
                    sendMessageAsync(PublicConstants.CONNECTION_WEAR, "");
                }
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
            case PublicConstants.CHANGE_LAYOUT:

                //Todo Change to UniqueStringsIDs
                if(sData.contains("Button"))
                {
                    ViewType nvista;

                    if(sData.contains("Pad"))
                        nvista= ViewType.VistaButtonPad;
                    else if(sData.contains("Horizontal"))
                        nvista= ViewType.VistaButtonHorizontal;
                    else
                        nvista= ViewType.VistaButtonVertical;



                    setContentView(new VistaButton(this, this, nvista, !sData.contains("without")));
                }else if(sData.contains("Virtual"))//Joystick virtual
                {
                    setContentView(new VistaJoystick(this, this));
                }else// vista con texto simple
                {
                    TextView tview=(TextView) findViewById(R.id.text);
                    if(tview==null)
                    {
                        setContentView(R.layout.activity_main);
                        tview=(TextView) findViewById(R.id.text);
                    }

                    tview.setText(sData.length()>1?sData:"Connection established!!");
                }
                break;

            case PublicConstants.ACTIVE_SENSORS:
                ActiveSensors();
                break;

            case PublicConstants.DEACTIVE_SENSORS:
                DeactiveSensors();
                break;

            case PublicConstants.STOP_ACTIVITY:
                mMustNotifyDestroy =false;
                this.finish();
                break;

            case PublicConstants.START_ACTIVITY:
                sendMessageAsync(PublicConstants.CONNECTION_WEAR, "");
                break;

            default:
                TextView aux=((TextView)findViewById(R.id.text));
                if(aux!=null)
                    aux.setText("Connection established!!");
                Log.d("onMessageReceived", "Default Message");
                break;
        }
    }


    public void sendMessageAsync(String path, String buffer)
    {
        if(mTelephone !=null)
        {
            (new SendMessage_Thread(path, buffer)).start();
        }else
            Log.d("sendMessageAsync", "mTelepohone is Null");
    }

    public void sendMessageSync(String path, String buffer)
    {
        if(mTelephone !=null)
        {
            (new SendMessage_Thread(path, buffer)).run();
        }
    }


    public void sendMessageSync(String path, ByteBuffer buffer)
    {
        if(mTelephone !=null)
        {
            (new SendMessage_Thread(path, buffer)).run();
        }
    }
    public void sendMessageAsync(String path, ByteBuffer buffer)
    {
        if(mTelephone !=null)
        {
            (new SendMessage_Thread(path, buffer)).start();
        }
    }


    public void sendMessageWithCloseApp(String path, String buffer)
    {
        if(mTelephone !=null)
        {
            (new SendMessage_Thread(path, buffer)).run();

            Wearable.MessageApi.removeListener(mApiClient, MainActivity.this);
            mWearableMessageApi=false;
            mApiClient.disconnect();
            this.finish();
        }
    }

    private class SendMessage_Thread extends Thread
    {
        protected String Path;
        protected ByteBuffer Buff;
        protected PendingResult<MessageApi.SendMessageResult> PendingResultSend;

        SendMessage_Thread(String pth, ByteBuffer bff)
        {
            Path=pth;
            Buff=bff;
        }

        SendMessage_Thread(String pth, String buffer)
        {
            Path=pth;
            Buff=ByteBuffer.allocate(buffer.length());
            Buff.put(buffer.getBytes());
        }

        public void run()
        {
            Log.d("MessageSend", "P: "+Path+" "+new String(Buff.array()));

            PendingResultSend=Wearable.MessageApi.sendMessage(mApiClient, mTelephone.getId(), Path, Buff.array());
        }
    }

    private class SendMessageThreadAwait extends SendMessage_Thread
    {
        SendMessageThreadAwait(String pth, ByteBuffer bff){  super(pth, bff); }
        SendMessageThreadAwait(String pth, String buffer)
        {
            super(pth, buffer);
        }


        public void run()
        {
            super.run();

            MessageApi.SendMessageResult result =PendingResultSend.await();
            if (!result.getStatus().isSuccess())
                Log.e("sendMessage", "ERROR: failed to send Message: " + result.getStatus());
        }
    }

    @Override
    public void onButtonPress(ButtonName pressed)
    {
        sendMessageAsync(PublicConstants.BUTTON_PRESS, PublicConstants.BUTTONS_NAME[pressed.ordinal()]);
    }

    @Override
    public void onButtonHold(ButtonName released) {

        sendMessageAsync(PublicConstants.BUTTON_RELEASE, PublicConstants.BUTTONS_NAME[released.ordinal()]);
    }

    @Override
    public void onPositionChange(float newX, float newY)
    {
        if(newX!=mJoystickNX && newY!=mJoystickNY)
        {
            mJoystickNX=newX;
            mJoystickNY=newX;
        }else
            return;

        if(newX+newY>0.1)
            if(System.currentTimeMillis()-last_joystick_send<MAX_MILIS_BETWEENUPDATES*2)
            {   /*Log.d("onPositionChange", "No envio info :)");*/ return;}

        last_joystick_send=System.currentTimeMillis();
        sendMessageSync(PublicConstants.JOYSTICK_VALUES, newX+"#"+newY);

    }
}
