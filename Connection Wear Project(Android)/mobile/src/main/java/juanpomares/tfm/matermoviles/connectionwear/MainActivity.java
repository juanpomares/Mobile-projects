package juanpomares.tfm.matermoviles.connectionwear;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;


import java.nio.ByteBuffer;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener
{
    public float mJoystickValues[]={0,0};
    public String mButtonPressed ="None";
    public float mOrientationValues[]={0,0,0};
    public String mActualView ="";
    public int mConnectedWearable=-1;
    public boolean mSensorActivated=false;
    //private boolean mReconnectWearable=false;
    private boolean WearableApiListener=false;

    private String mWearableID="";
    private GoogleApiClient mApiClient=null;

    private TextView mJoystickTViews[];
    private TextView mOrientationTViews[];
    private TextView mButtonPressedTView;
    private TextView mConnectedWearTView;
    private Spinner mSpinnerViews;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initGoogleApiClient();

        setContentView(R.layout.activity_main);

        mButtonPressedTView =(TextView)findViewById(R.id.PressedButton);
        mConnectedWearTView =(TextView)findViewById(R.id.ConnectedWear);

        mOrientationTViews =new TextView[3];
        mOrientationTViews[0]=(TextView)findViewById(R.id.girX);
        mOrientationTViews[1]=(TextView)findViewById(R.id.girY);
        mOrientationTViews[2]=(TextView)findViewById(R.id.girZ);

        mJoystickTViews =new TextView[2];
        mJoystickTViews[0]=(TextView)findViewById(R.id.joy_posx);
        mJoystickTViews[1]=(TextView)findViewById(R.id.joy_posy);

        mSpinnerViews =(Spinner)findViewById(R.id.spinner);

        mSpinnerViews.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if(view!=null && (view instanceof TextView))
                {
                    String newInterface = (((TextView) view).getText().toString());

                    if(newInterface.contains("TextView"))
                    {
                        newInterface=((TextView) findViewById(R.id.etTexto)).getText().toString();
                    }
                    changeInterface(newInterface);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        RefreshValues();
    }

    public void SendText(View v)
    {
        changeInterface(((TextView) findViewById(R.id.etTexto)).getText().toString());
    }

    private void changeInterface(String newinterface)
    {
        if(mActualView!=newinterface)
        {
			mJoystickValues[0]=mJoystickValues[0]=0;
			
            mActualView=newinterface;

            sendMessageChecking(PublicConstants.CHANGE_LAYOUT, newinterface);
            if(newinterface.contains("Button"))
            {
                findViewById(R.id.posicionjoystick).setVisibility(View.GONE);
                findViewById(R.id.joy_posx).setVisibility(View.GONE);
                findViewById(R.id.joy_posy).setVisibility(View.GONE);

                findViewById(R.id.buttonpressed).setVisibility(View.VISIBLE);
                findViewById(R.id.PressedButton).setVisibility(View.VISIBLE);

                findViewById(R.id.etTexto).setVisibility(View.GONE);
                findViewById(R.id.buttonEnviar).setVisibility(View.GONE);
                findViewById(R.id.etTexto).setEnabled(false);

            }else
            if(newinterface.contains("Virtual"))//Joystick virtual
            {
                findViewById(R.id.posicionjoystick).setVisibility(View.VISIBLE);
                findViewById(R.id.joy_posx).setVisibility(View.VISIBLE);
                findViewById(R.id.joy_posy).setVisibility(View.VISIBLE);

                findViewById(R.id.buttonpressed).setVisibility(View.GONE);
                findViewById(R.id.PressedButton).setVisibility(View.GONE);

                findViewById(R.id.etTexto).setVisibility(View.GONE);
                findViewById(R.id.buttonEnviar).setVisibility(View.GONE);
                findViewById(R.id.etTexto).setEnabled(false);
            }else// vista con texto simple
            {
                findViewById(R.id.posicionjoystick).setVisibility(View.GONE);
                findViewById(R.id.joy_posx).setVisibility(View.GONE);
                findViewById(R.id.joy_posy).setVisibility(View.GONE);

                findViewById(R.id.buttonpressed).setVisibility(View.GONE);
                findViewById(R.id.PressedButton).setVisibility(View.GONE);

                findViewById(R.id.etTexto).setVisibility(View.VISIBLE);
                findViewById(R.id.buttonEnviar).setVisibility(View.VISIBLE);
                findViewById(R.id.etTexto).setEnabled(true);
                findViewById(R.id.etTexto).requestFocus();
            }
        }
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

    private void RefreshValues()
    {
        mButtonPressedTView.setText(mButtonPressed);

        for(int i=0; i<3; i++)
        {
            String Val="Yaw";
            switch(i)
            {
                case 1:
                    Val="Pitch";
                    break;
                case 2:
                    Val="Roll";
                    break;
            }

            mOrientationTViews[i].setText(Val+": "+ mOrientationValues[i] + "");
        }


        for(int i=0; i<2; i++)
            mJoystickTViews[i].setText(((i==0)?"X":"Y")+": "+mJoystickValues[i]+"");
    }

    private void getMWearable()
    {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
        List<Node> list=nodes.getNodes();

        if(list.size()>0)
            mWearableID=list.get(0).getId();
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
            if (mWearableID == "") {
                AddTextInterface("No smartwatch Connected!! :0");
            } else
            {
                Log.d("MessageSend", "P: " + Path + " " + new String(Buff.array()));
                PendingResultSend = Wearable.MessageApi.sendMessage(mApiClient, mWearableID, Path, Buff.array());
            }
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
            if (mWearableID == "")
                getMWearable();
				
			if (mConnectedWearable != 1 && !Path.equals(PublicConstants.START_ACTIVITY) && !Path.equals(PublicConstants.STOP_ACTIVITY))
            {
                OpenWearableApp(null);
            }else
            {
				super.run();

				if(PendingResultSend!=null)
				{
					MessageApi.SendMessageResult result = PendingResultSend.await();
					if (!result.getStatus().isSuccess())
						Log.e("sendMessage", "ERROR: failed to send Message: " + result.getStatus());
				}else
					AddTextInterface("No smartwatch connected", Toast.LENGTH_LONG);
			}
        }
    }


    private void sendMessageUnchecked(String path, String text )
    {
        if (mConnectedWearable!=0 || path.equals(PublicConstants.START_ACTIVITY))
        {
            AddTextInterface("sendMessage P:"+path+" T:"+text);
            new SendMessage_Thread(path, text).run();
        }
    }

    private void sendMessageChecking(String path, String text )
    {
        if (mConnectedWearable!=0 || path.equals(PublicConstants.START_ACTIVITY))
        {
            AddTextInterface("sendMessage P:"+path+" T:"+text);
            new SendMessageThreadAwait(path, text).start();
        }
    }

    private void DisconnectWear()
    {
        if(mApiClient.isConnected())
        {
            if(WearableApiListener)
            {
                Wearable.MessageApi.removeListener(mApiClient, this);
                WearableApiListener=false;
            }
            mApiClient.disconnect();
        }
    }

    private void AddTextInterface(final String txt, final int lenght)
    {
        Log.d("AddTextInterface", txt);
        if(lenght!=-1)
        {
            MainActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run() { Toast.makeText(MainActivity.this, txt, lenght).show(); }
            });
        }
    }

    private void AddTextInterface(String txt)
    {
        AddTextInterface(txt, -1);
    }

    private void ReConnection()
    {
        /*if(mReconnectWearable)
        {*/
            if (mSensorActivated)
                sendMessageUnchecked(PublicConstants.ACTIVE_SENSORS, "Orientation");

            if (mActualView != null)
                sendMessageUnchecked(PublicConstants.CHANGE_LAYOUT, mActualView);

        /*    mReconnectWearable = false;
        }*/
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {
        String path = messageEvent.getPath();

        byte[] Bdata = messageEvent.getData();
        String data=new String(Bdata);

        Log.d("MessageReceived", " P: "+path+" D: "+data);

        switch(path)
        {
            case PublicConstants.BUTTON_PRESS:
                mButtonPressed=data;
                AddTextInterface("Button Pressed: " + data);
                RefreshValues();
                break;

            case PublicConstants.BUTTON_RELEASE:
                AddTextInterface("Button Released: " + data);
                if(mButtonPressed.equalsIgnoreCase(data))
                    mButtonPressed="None";

                RefreshValues();
                break;

            case PublicConstants.ORIENTATION_VALUES:
                String[] _pieces=data.split("#");

                if(_pieces.length>2)
                    for(int i=0; i<3; i++)
                        mOrientationValues[i]=(float)Math.toDegrees(Float.parseFloat(_pieces[i]));

                RefreshValues();
                break;

            case PublicConstants.JOYSTICK_VALUES:
                String[] _piecs=data.split("#");
                if(_piecs.length>1)
                    for(int i=0; i<2; i++)
                        mJoystickValues[i]=Float.parseFloat(_piecs[i]);

                RefreshValues();
                break;

            case PublicConstants.DISCONNECTION:
                mConnectedWearable = -1;
                AddTextInterface("SmartWatch disconnected!! :(", Toast.LENGTH_LONG);
                mConnectedWearTView.setText("FALSE");
                break;

            case PublicConstants.CONNECTION_WEAR:
                if(mConnectedWearable!=1)
                {
					mWearableID = messageEvent.getSourceNodeId();
					mConnectedWearable = 1;
					mConnectedWearTView.setText("TRUE");

					sendMessageChecking(PublicConstants.CONNECTION_WELL, "jeje ;)");
					AddTextInterface("SmartWatch connected!! :)", Toast.LENGTH_LONG);

					ReConnection();
				}
                break;

            default:
                Log.d("onMessageReceived", "Default :0");
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        if(mConnectedWearable==1)
            sendMessageUnchecked(PublicConstants.STOP_ACTIVITY, "");

        DisconnectWear();
        super.onDestroy();
    }

    public void OpenWearableApp(View v)
    {
        sendMessageChecking(PublicConstants.START_ACTIVITY, "");
    }

    public void CloseWearableApp(View v)
    {
        sendMessageChecking(PublicConstants.STOP_ACTIVITY, "");
        mConnectedWearable=-1;
        mConnectedWearTView.setText("FALSE");
    }

    public void ActiveSensor(View v)
    {
        if(!mSensorActivated)
        {
            mSensorActivated = true;
            sendMessageChecking(PublicConstants.ACTIVE_SENSORS, "");

            for(int i=0; i<3; i++)
                mOrientationTViews[i].setVisibility(View.VISIBLE);
        }
    }

    public void DeActiveSensor(View v)
    {
        if(mSensorActivated)
        {
			mOrientationValues[0]=mOrientationValues[1]=mOrientationValues[2]=0;
            mSensorActivated = false;
            
			sendMessageChecking(PublicConstants.DEACTIVATE_SENSORS, "");
            for(int i=0; i<3; i++)
                mOrientationTViews[i].setVisibility(View.GONE);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Wearable.MessageApi.addListener(mApiClient, this);
        WearableApiListener=true;
        sendMessageChecking(PublicConstants.START_ACTIVITY, "");
        AddTextInterface("Sended START_ACTIVITY command");
    }

    @Override
    public void onConnectionSuspended(int i) {AddTextInterface("Connection suspended");}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        AddTextInterface("Connection failed: "+connectionResult.toString(), Toast.LENGTH_LONG);
    }
}