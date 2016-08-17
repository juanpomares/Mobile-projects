package juanpomares.tfm.matermoviles.connectionwear;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements ButtonListener, JoystickListener
{
    private static final int MAX_MILLIS_BETWEEN_UPDATES =125;

    private GoogleApiClient mApiClient;

    private Node mTelephone =null;

    private boolean mMustNotifyDestroy =true, mSensorsActivated =false;

    private long mLastPositionJoystickSend_Time =0;
    private float mJoystickNX=0, mJoystickNY=0, mJoystickSendNX=0, mJoystickSendNY=0;
    private Timer mTimer=null;

    private String mActualView="None";

    private SensorManager mSensorManager;
    private SensorEventListener mOrientationListener;

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;
    private MessageApi.MessageListener mMessageListener;


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
            float [] mGravityValues;
            float [] mGeoMagneticValues;
            long mLastOrientationSent=0;

            float []R=new float[9];
            float []orientation=new float[3];

            @Override
            public void onSensorChanged(SensorEvent event)
            {
                switch(event.sensor.getType())
                {
                    case Sensor.TYPE_ACCELEROMETER:
                        mGravityValues =event.values;
                        break;

                    case Sensor.TYPE_MAGNETIC_FIELD:
                        mGeoMagneticValues =event.values;
                        break;
                }

                if(System.currentTimeMillis()-mLastOrientationSent< MAX_MILLIS_BETWEEN_UPDATES)
                    return;

                if(mGeoMagneticValues !=null && mGravityValues !=null)
                {
                    if(SensorManager.getRotationMatrix(R, null, mGravityValues, mGeoMagneticValues))
                    {
                        SensorManager.getOrientation(R, orientation);
                        sendMessageChecking(PublicConstants.ORIENTATION_VALUES, orientation[0] + "#" + orientation[1] + "#" + orientation[2]);
                        mLastOrientationSent=System.currentTimeMillis();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {}
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

        stopTimerTask();
        DeactivateSensors();
        Log.d("onStop", "...");

        if(mApiClient.isConnected())
        {
            if(mMustNotifyDestroy)
            {
                mMustNotifyDestroy=false;
                sendMessageWithCloseApp(PublicConstants.DISCONNECTION, "Bye :)");
                Log.d("onStop", "send disconnected wear");
            }

            if (mMessageListener!=null)
            {
                Wearable.MessageApi.removeListener(mApiClient, mMessageListener);
                mMessageListener = null;
            }
            mApiClient.disconnect();
        }
        mTelephone=null;
        AppSharedPreferences.setAppOpen(this, false);
        this.finish();
    }

    private void initGoogleApiClient()
    {
        createConnectionCallbacks();
        createConnectionFailed();


        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .build();

        mApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "...");
    }

    private void createConnectionCallbacks()
    {
        mConnectionCallbacks=new GoogleApiClient.ConnectionCallbacks()
        {
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
                            mMustNotifyDestroy=true;
                            sendMessageUnchecked(PublicConstants.CONNECTION_WEAR, "");
                        }
                    }
                });

                initMessageListener();
                Wearable.MessageApi.addListener(mApiClient, mMessageListener);
            }

            @Override
            public void onConnectionSuspended(int i) {Log.d("onConnectionSuspended", "error: "+i);}

        };
    }

    private void createConnectionFailed()
    {
        mConnectionFailedListener=new GoogleApiClient.OnConnectionFailedListener()
        {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
            {
                Log.d("onConnectionFailed", "Error: "+connectionResult.getErrorCode());
            }
        };
    }


    public void DeactivateSensors()
    {
        if(mSensorsActivated)
        {
            mSensorsActivated = false;
            mSensorManager.unregisterListener(mOrientationListener);
        }
    }

    public void ActiveSensors()
    {
        if(!mSensorsActivated)
        {
            List<Sensor> sensorsAcelerometer = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            List<Sensor> sensorsMagnetic = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
            if (sensorsAcelerometer.size() > 0 && sensorsMagnetic.size()>0)
            {
                mSensorManager.registerListener(mOrientationListener, sensorsAcelerometer.get(0), SensorManager.SENSOR_DELAY_GAME);
                mSensorManager.registerListener(mOrientationListener, sensorsMagnetic.get(0), SensorManager.SENSOR_DELAY_GAME);
                mSensorsActivated = true;
            }
        }
    }

    private void initMessageListener()
    {
        mMessageListener=new MessageApi.MessageListener()
        {

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

                        if(mActualView==sData)
                            return;

                        if(mActualView.contains("Virtual"))//Actual view is Joystick virtual
                        {
                            stopTimerTask();
                        }

                        mActualView=sData;

                        if(sData.contains("Button"))
                        {
                            ViewType nvista;

                            if(sData.contains("Pad"))
                                nvista= ViewType.PadButtonView;
                            else if(sData.contains("Horizontal"))
                                nvista= ViewType.HorizontalButtonView;
                            else
                                nvista= ViewType.VerticalButtonView;



                            setContentView(new ButtonView(MainActivity.this, MainActivity.this, nvista, !sData.contains("without")));
                        }else if(sData.contains("Virtual"))//Joystick virtual
                        {
                            setContentView(new JoystickView(MainActivity.this, MainActivity.this));
                            mTimer=new Timer();
                            mTimer.scheduleAtFixedRate(new SendJoystickNewPosition_Task(), MAX_MILLIS_BETWEEN_UPDATES *4, MAX_MILLIS_BETWEEN_UPDATES *4);

                        }else// TextView
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

                    case PublicConstants.DEACTIVATE_SENSORS:
                        DeactivateSensors();
                        break;

                    case PublicConstants.STOP_ACTIVITY:
                        mMustNotifyDestroy =false;
                        MainActivity.this.finish();
                        break;

                    case PublicConstants.START_ACTIVITY:
                        mMustNotifyDestroy=true;
                        sendMessageUnchecked(PublicConstants.CONNECTION_WEAR, "");
                        break;

                    case PublicConstants.CONNECTION_WELL:
                        if(!mMustNotifyDestroy)
                        {
                            mMustNotifyDestroy = true;
                            TextView tview = ((TextView) findViewById(R.id.text));
                            if (tview == null)
                            {
                                setContentView(R.layout.activity_main);
                                tview = (TextView) findViewById(R.id.text);
                            }
                            tview.setText("Connection established!!");
                        }
                        break;

                    default:
                        Log.d("onMessageReceived", "Default Message");
                        break;
                }
            }

        };
    }

    public void sendMessageChecking(String path, String buffer)
    {
        if(mTelephone !=null)
        {
            (new SendMessage_Thread(path, buffer)).start();
        }
    }

    public void sendMessageUnchecked(String path, String buffer)
    {
        if(mTelephone !=null)
        {
            (new SendMessage_Thread(path, buffer)).run();
        }
    }

    public void sendMessageUnchecked(String path, ByteBuffer buffer)
    {
        if(mTelephone !=null)
        {
            (new SendMessage_Thread(path, buffer)).run();
        }
    }
    public void sendMessageChecking(String path, ByteBuffer buffer)
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
            sendMessageUnchecked(path, buffer);
            Wearable.MessageApi.removeListener(mApiClient, mMessageListener);
            mApiClient.disconnect();
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
        sendMessageChecking(PublicConstants.BUTTON_PRESS, PublicConstants.BUTTONS_NAME[pressed.ordinal()]);
    }

    @Override
    public void onButtonHold(ButtonName released)
    {
        sendMessageChecking(PublicConstants.BUTTON_RELEASE, PublicConstants.BUTTONS_NAME[released.ordinal()]);
    }

    @Override
    public void onPositionChange(float newX, float newY)
    {
        if(newX!=mJoystickNX && newY!=mJoystickNY)
        {
            mJoystickNX=newX;
            mJoystickNY=newY;
        }else
            return;

        long actualTime=System.currentTimeMillis();

        if(Math.abs(newX)+Math.abs(newY)>0.1)
            if(actualTime- mLastPositionJoystickSend_Time < MAX_MILLIS_BETWEEN_UPDATES *3) {   /*Log.d("onPositionChange", "No info sended :)");*/ return;}

      sendJoystickPosition(actualTime);
    }

    private void sendJoystickPosition(long time)
    {
        mLastPositionJoystickSend_Time =time;
        sendMessageUnchecked(PublicConstants.JOYSTICK_VALUES, mJoystickNX+"#"+mJoystickNY);
        mJoystickSendNX=mJoystickNX; mJoystickSendNY=mJoystickNY;
    }

    private void stopTimerTask()
    {
        if(mTimer!=null)
        {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    class SendJoystickNewPosition_Task extends TimerTask
    {
        public void run()
        {
            if (mTelephone==null)
            {
                this.cancel();
                Log.d("SendJoystick", "cancelling task");
            }else
            {
                if (mJoystickSendNX != mJoystickNX && mJoystickSendNY != mJoystickNY)
                {
                    long actualTime = System.currentTimeMillis();

                    if (actualTime - mLastPositionJoystickSend_Time < MAX_MILLIS_BETWEEN_UPDATES * 4) {return; }

                    sendJoystickPosition(actualTime);
                }
            }
        }
    }
}