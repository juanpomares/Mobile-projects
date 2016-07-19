package com.example.juan_.pruebainterfaces;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
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
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener
{
    private enum ListenerTipo{Joystick, ButtonPress, ButtonRelease, Gyroscope};

    private LinkedHashMap<String, String> mJoystickListeners;
    private LinkedHashMap<String, String> mButtonPressListeners;
    private LinkedHashMap<String, String> mButtonReleaseListeners;
    private LinkedHashMap<String, String> mGyroscopeListeners;

    public float mJoystickValues[]={0,0};
    public String mButtonPressed ="Ninguno";
    public float mGyroscopeValues[]={0,0,0};
    public String mActualView =null;
    public int mConnectedWearable=-1;
    public boolean mSensorActivated=false;
    private boolean mReconnectWearable=false;
    private boolean WearableApiListener=false;

    private String mWearableID="";

    private GoogleApiClient mApiClient=null;
    private MessageApi.MessageListener mMessageListener=null;

    private TextView joystickvalues[];
    private TextView giroscopiovalues[];
    private TextView botonpulsado;

    private Spinner spin_vistas;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        inicializeManagerListeners();
        initGoogleApiClient();

        setContentView(R.layout.activity_main);

        botonpulsado=(TextView)findViewById(R.id.PulsadoBoton);

        giroscopiovalues=new TextView[3];
        giroscopiovalues[0]=(TextView)findViewById(R.id.girX);
        giroscopiovalues[1]=(TextView)findViewById(R.id.girY);
        giroscopiovalues[2]=(TextView)findViewById(R.id.girZ);

        joystickvalues=new TextView[2];
        joystickvalues[0]=(TextView)findViewById(R.id.joy_posx);
        joystickvalues[1]=(TextView)findViewById(R.id.joy_posy);

        spin_vistas=(Spinner)findViewById(R.id.spinner);

        spin_vistas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if(view!=null && (view instanceof TextView))
                {
                    String ninterface = (((TextView) view).getText().toString());
                    changeInterface(ninterface);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ActualizarDatos();
    }

    private void changeInterface(String newinterface)
    {
        if(mActualView!=newinterface)
        {
            mActualView=newinterface;
            if(newinterface.contains("Texto"))
            {
                newinterface=(new Random().nextInt(50))+": Cambiada la vista";

            }

            sendMessage(PublicConstants.CHANGE_LAYOUT, newinterface);
            if(newinterface.contains("botón"))
            {
                findViewById(R.id.posicionjoystick).setVisibility(View.GONE);
                findViewById(R.id.joy_posx).setVisibility(View.GONE);
                findViewById(R.id.joy_posy).setVisibility(View.GONE);

                findViewById(R.id.botonpulsado).setVisibility(View.VISIBLE);
                findViewById(R.id.PulsadoBoton).setVisibility(View.VISIBLE);
            }else
                if(newinterface.contains("virtual"))//Joystick virtual
                {
                    findViewById(R.id.posicionjoystick).setVisibility(View.VISIBLE);
                    findViewById(R.id.joy_posx).setVisibility(View.VISIBLE);
                    findViewById(R.id.joy_posy).setVisibility(View.VISIBLE);

                    findViewById(R.id.botonpulsado).setVisibility(View.GONE);
                    findViewById(R.id.PulsadoBoton).setVisibility(View.GONE);
                }else// vista con texto simple
                {
                    findViewById(R.id.posicionjoystick).setVisibility(View.GONE);
                    findViewById(R.id.joy_posx).setVisibility(View.GONE);
                    findViewById(R.id.joy_posy).setVisibility(View.GONE);

                    findViewById(R.id.botonpulsado).setVisibility(View.GONE);
                    findViewById(R.id.PulsadoBoton).setVisibility(View.GONE);
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

    private void ActualizarDatos()
    {
        botonpulsado.setText(mButtonPressed);

        for(int i=0; i<3; i++)
            giroscopiovalues[i].setText(mGyroscopeValues[i]+"");

        for(int i=0; i<2; i++)
            joystickvalues[i].setText(mJoystickValues[i]+"");
    }

    private void getMWearable()
    {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
        List<Node> list=nodes.getNodes();

        if(list.size()>0)
            mWearableID=list.get(0).getId();
    }

    private void sendMessage( final String path, final String text ) {
        AddTextInterface("sendMessage P:"+path+" T:"+text);
        if (mConnectedWearable!=0)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (mWearableID == "")
                        getMWearable();

                    if (mWearableID == "") {
                        AddTextInterface("No hay wearables conectados :0");
                    } else {
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                                mApiClient, mWearableID, path, text.getBytes()).await();

                        if (!result.getStatus().isSuccess())
                            Log.d("SendMessage", "No se ha enviado correctamente");

                        if (text.equals("disconnect"))
                            DisconnectWear();
                    }
                }
            }).start();
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
    private void AddTextInterface(String txt, boolean toast)
    {
        Log.d("AddTextInterface", txt);
        if(toast)
            Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }

    private void AddTextInterface(String txt)
    {
        AddTextInterface(txt, false);
    }


    private void ReConnection()
    {
        if(mReconnectWearable)
        {
            AddTextInterface("Entro al reconnection");
            if (mSensorActivated)
                sendMessage(PublicConstants.ACTIVE_SENSORS, "Orientation");

            if (mActualView != null)
                sendMessage(PublicConstants.CHANGE_LAYOUT, mActualView);

            mReconnectWearable = false;
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {
        String path = messageEvent.getPath();

        byte[] Bdata = messageEvent.getData();
        String data=new String(Bdata);

        Log.d("MessageReceived", " P: "+path+" D: "+data);

        if (path.equalsIgnoreCase(PublicConstants.BUTTON_PRESS))
        {
            mButtonPressed=data;
            AddTextInterface("Se ha apretado el boton: " + data);
            notifyListener(ListenerTipo.ButtonPress);
            ActualizarDatos();
        }else if (path.equalsIgnoreCase(PublicConstants.BUTTON_RELEASE))
        {
            AddTextInterface("Se ha soltado el boton: " + data);
            notifyListener(ListenerTipo.ButtonRelease, data);
            if(mButtonPressed.equalsIgnoreCase(data))
                mButtonPressed="Ninguno";

            ActualizarDatos();
        }else if (path.equalsIgnoreCase(PublicConstants.GYROSCOPE_VALUES))
        {
            String[] trozos=data.split("#");

            if(trozos.length>2)
                for(int i=0; i<3; i++)
                    mGyroscopeValues[i]=(float)Math.toDegrees(Float.parseFloat(trozos[i]));

            notifyListener(ListenerTipo.Gyroscope);
            ActualizarDatos();
        }else if (path.equalsIgnoreCase(PublicConstants.JOYSTICK_VALUES))
        {
            String[] trozos=data.split("#");
            if(trozos.length>1)
            for(int i=0; i<2; i++)
                mJoystickValues[i]=Float.parseFloat(trozos[i]);

            notifyListener(ListenerTipo.Joystick);
            ActualizarDatos();
        }else if (path.equalsIgnoreCase(PublicConstants.DISCONNECTION)) {
            mConnectedWearable = 0;
            AddTextInterface("Se ha desconectado el reloj :)", true);
        }else if (path.equalsIgnoreCase(PublicConstants.CONNECTION_WEAR))
         {
            mWearableID = messageEvent.getSourceNodeId();
            mConnectedWearable = 1;

            sendMessage("Okay", "jeje");

            AddTextInterface("Se ha conectado el reloj :)", true);

            if(mReconnectWearable)
            {
                ReConnection();
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        if(mConnectedWearable==1)
            sendMessage(PublicConstants.STOP_ACTIVITY, "");
        super.onDestroy();
    }

    public void AbrirApp(View v)
    {

        mReconnectWearable=true;
        sendMessage(PublicConstants.START_ACTIVITY, "");
    }
    public void CerrarApp(View v)
    {
        mReconnectWearable=true;
        sendMessage(PublicConstants.STOP_ACTIVITY, "");
    }
    public void ActiveSensor(View v)
    {
        if(!mSensorActivated)
        {
            mSensorActivated = true;
            sendMessage(PublicConstants.ACTIVE_SENSORS, "");

            for(int i=0; i<3; i++)
                giroscopiovalues[i].setVisibility(View.VISIBLE);

        }
    }
    public void DeActiveSensor(View v)
    {
        if(mSensorActivated)
        {
            mSensorActivated = false;
            sendMessage(PublicConstants.DEACTIVE_SENSORS, "");
            for(int i=0; i<3; i++)
                giroscopiovalues[i].setVisibility(View.GONE);
        }
    }

    /*Listeners para Unity*/
    protected void inicializeManagerListeners()
    {
        mJoystickListeners =new LinkedHashMap<String, String>();
        mButtonPressListeners =new LinkedHashMap<String, String>();
        mButtonReleaseListeners =new LinkedHashMap<String, String>();
        mGyroscopeListeners =new LinkedHashMap<String, String>();
    }

    public void addJoystickListener(String object, String function){ addListener(mJoystickListeners, object, function);}
    public void addButtonPressListener(String object, String function){ addListener(mButtonPressListeners, object, function);}
    public void addButtonReleaseListener(String object, String function){ addListener(mButtonReleaseListeners, object, function);}
    public void addGyroscopeListener(String object, String function){ addListener(mGyroscopeListeners, object, function);}

    public void removeJoystickListener(String object, String metodo){ removeListener(mJoystickListeners, object);}
    public void removeButtonPressListener(String object, String metodo){ removeListener(mButtonPressListeners, object);}
    public void removeButtonReleaseListener(String object, String metodo){ removeListener(mButtonReleaseListeners, object);}
    public void removeGyroscopeListener(String object, String metodo){ removeListener(mGyroscopeListeners, object);}

    public void removeListeners(String object)
    {
        mJoystickListeners.remove(object);
        mButtonPressListeners.remove(object);
        mButtonReleaseListeners.remove(object);
        mGyroscopeListeners.remove(object);
    }

    public void removeAllListeners()
    {
        mJoystickListeners.clear();
        mButtonPressListeners.clear(); mButtonReleaseListeners.clear();
        mGyroscopeListeners.clear();
    }

    private void addListener(LinkedHashMap<String, String> mapa, String object, String function){ mapa.put(object, function);  }
    private void removeListener(LinkedHashMap<String, String> mapa, String object){ mapa.remove(object);  }

    private LinkedHashMap<String, String> getMapaByTipo(ListenerTipo tipo)
    {
        if(tipo==ListenerTipo.ButtonPress)          return mButtonPressListeners;
        else if(tipo==ListenerTipo.ButtonRelease)   return mButtonReleaseListeners;
        else if(tipo==ListenerTipo.Gyroscope)       return mGyroscopeListeners;
        else                                        return mJoystickListeners;
    }

    private void notifyListener(ListenerTipo tipo) {  notifyListener(tipo, "");  }
    private void notifyListener(ListenerTipo tipo, String data)
    {
        LinkedHashMap<String, String> _map=getMapaByTipo(tipo);

        Iterator<String> _objects=_map.keySet().iterator();
        while(_objects.hasNext())
        {
            String _object=_objects.next();
            String _method=_map.get(_object);

            if(tipo==ListenerTipo.ButtonPress)
                Log.d("notifyListener", _object+": "+_method+"("+mButtonPressed+")");
            else if(tipo==ListenerTipo.ButtonRelease)
                Log.d("notifyListener", _object+": "+_method+"("+data+")");
            else    if(tipo==ListenerTipo.Gyroscope)
                Log.d("notifyListener", _object+": "+_method);
            else if(tipo==ListenerTipo.Joystick)
                Log.d("notifyListener", _object+": "+_method);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Wearable.MessageApi.addListener(mApiClient, this);
        WearableApiListener=true;
        sendMessage(PublicConstants.START_ACTIVITY, "");
        AddTextInterface("Enviado START_ACTIVITY command");
    }

    @Override
    public void onConnectionSuspended(int i) {AddTextInterface("Connection suspended");}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {AddTextInterface("Connection failed");}
}