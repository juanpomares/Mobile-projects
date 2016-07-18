package com.ua.juanchema.mastermoviles.tdam.proyectofinalsockets;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Calendar;

/**
 * Created by Usuario on 24/11/2015.
 */
public class ConversationClass extends AppCompatActivity
{
    /* *************************************************************************** */
    //                             Datos Intent Comunes
    /* *************************************************************************** */
    protected int mFondo;
    protected String mNombreUsuario;
    protected String mPuerto;
    protected LinearLayout layoutConversacion;


    //DATOS MENSAJE PERSONALIZADO
    LinearLayout linearLayoutMensajes;
    Calendar calendario;
    Socket socket;
    boolean ConectionEstablished;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    EditText EditTextMensaje;

    protected void setConnectionEstablislhed(boolean c)
    {
        ConectionEstablished=c;
        ConversationClass.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
            //Si hay conexion activamos el edit text, sino lo desactivamos
                if(ConectionEstablished)
                    EditTextMensaje.setEnabled(true);
                else
                    EditTextMensaje.setEnabled(false);
            }
        });
    }

    protected void CloseSocketInputs()//Cerramos los streams y el socket
    {
        ConectionEstablished=false;
        try {
            if(dataInputStream!=null) dataInputStream.close();
        }catch(Exception e){}
        finally {
            dataInputStream=null;
            try {
                if(dataOutputStream!=null) dataOutputStream.close();
            }catch(Exception e){}
            finally {
                dataOutputStream=null;
                try {
                    if(socket!=null) socket.close();
                }catch(Exception e){}
                finally {
                    socket=null;
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_activity);

        //Obtenemos valores del intent
        //Log.e("DETALLES", "Estamos en conversation class on create, vamos a obtener extras");
        mPuerto = getIntent().getStringExtra("inicio.puerto");
        //Log.e("DETALLES", "Hemos recogido el puerto, que es "+mPuerto);
        mNombreUsuario = getIntent().getStringExtra("inicio.nombre");
        //Log.e("DETALLES", "Hemos recogido el nombre de usuario, que es "+mNombreUsuario);
        mFondo = getIntent().getIntExtra("inicio.fondo", 0);
        //Log.e("DETALLES", "Hemos recogido el fondo, que es "+mFondo);

        layoutConversacion = (LinearLayout) findViewById(R.id.ListaMensajes);
        //Log.e("DETALLES", "Hemos recogido el linear layout y le vamos a asignar el fondo");
        int idFondo = getIdFondo(mFondo);
        //Log.e("DETALLES", "Ahora que la hemos obtenido, que es "+idFondo+", vamos a asignar el fondo");
        ((ImageView) findViewById(R.id.imageView)).setBackgroundResource(idFondo);
        //else
            //Log.e("DETALLES", "layoutConversacion es null");
        //Log.e("DETALLES", "Hemos asignado el fondo con éxito");

        //Inicializamos las variables de instancia
        EditTextMensaje=((EditText)findViewById(R.id.EditTextMensaje));
        calendario=Calendar.getInstance();
        socket=null; dataInputStream=null; dataOutputStream=null;
    }

    private int getIdFondo(int i)
    {
        //Log.e("DETALLES", "Vamos a obtener la id del fondo");
        switch(i)
        {
            case 0:
                return R.drawable.clasicon;
            case 1:
                return R.drawable.azuln;
            case 2:
                return R.drawable.rojon;
        }

        return R.drawable.clasicon;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        layoutConversacion.setBackgroundResource(0);
    }

    protected void changeTitle(String s)
    {
        final String nuevoTitulo = s;
        ConversationClass.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle(nuevoTitulo);
            }
        });
    }

    private String dosCaracteres(int v){return String.format("%02d", v);}

    private void AddMensajeScroll(MiMensajeView v)
    {
        layoutConversacion.addView(v);//Añadimos el nuevo mensaje al linearlayput
        v.getParent().requestChildFocus(v, v);//PAra que se haga scroll hasta el nuevo mensaje añadido
    }

    protected class ShowImage extends Thread//Crea un nuevo mensaje en la interfaz con la imgane enviada/recibida
    {
        private Bitmap imagen;
        private int tipo;

        ShowImage(Bitmap img, int t) { imagen=img; tipo=t;}

        @Override
        public void run()
        {
            ConversationClass.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    MiMensajeView vista = new MiMensajeView(ConversationClass.this);
                    vista.setEnviado(tipo);
                    Calendar calendario = Calendar.getInstance();
                    vista.setHora(dosCaracteres(calendario.get(Calendar.HOUR_OF_DAY)) + ":" + dosCaracteres(calendario.get(Calendar.MINUTE)));
                    vista.setTexto("");
                    vista.setImagen(imagen);
                    AddMensajeScroll(vista);
                }
            });

        }
    }

    protected class ShowMessageSended extends Thread//Añade a la vista el mensaje recibido
    {
        private String msg;

        ShowMessageSended(String message) {       msg = message;      }

        @Override
        public void run()
        {
            ConversationClass.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    MiMensajeView vista = new MiMensajeView(ConversationClass.this);
                    vista.setEnviado(MiMensajeView.PROPIO);
                    Calendar calendario = Calendar.getInstance();
                    vista.setHora(dosCaracteres(calendario.get(Calendar.HOUR_OF_DAY)) + ":" + dosCaracteres(calendario.get(Calendar.MINUTE)));
                    vista.setTexto(msg);
                    vista.setImagen(null);
                    AddMensajeScroll(vista);
                }
            });

        }
    }

    protected class ShowMessageReceived extends Thread//Añade a la interfaz el mensaje enviado
    {
        private String msg;

        ShowMessageReceived(String message) {       msg = message;      }

        @Override
        public void run()
        {
            ConversationClass.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    MiMensajeView vista = new MiMensajeView(ConversationClass.this);
                    vista.setEnviado(MiMensajeView.NOPROPIO);

                    vista.setHora(dosCaracteres(calendario.get(Calendar.HOUR_OF_DAY)) + ":" + dosCaracteres(calendario.get(Calendar.MINUTE)));
                    vista.setTexto(msg);
                    vista.setImagen(null);
                    AddMensajeScroll(vista);
                }
            });

        }
    }

    protected class ShowMessageInfo extends Thread//Añade a la interfaz un mensaje de información
    {
        private String msg;

        ShowMessageInfo(String message) {       msg = message;      }

        @Override
        public void run()
        {
            ConversationClass.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    MiMensajeView vista = new MiMensajeView(ConversationClass.this);
                    vista.setHora(dosCaracteres(calendario.get(Calendar.HOUR_OF_DAY)) + ":" + dosCaracteres(calendario.get(Calendar.MINUTE)));
                    vista.setTexto(msg);
                    vista.setHora("");
                    vista.setEnviado(-1);
                    vista.setImagen(null);
                    AddMensajeScroll(vista);
                }
            });

        }
    }

    protected void HacerFoto()//Crea un intent para obtener una captura de la camara
    {
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1234);
    }

    protected class ReceiveBitmapThread extends Thread
    {
        int tam;
        ReceiveBitmapThread(int t)
        {
            tam=t;
        }

        public void run()
        {
            try {
                byte[] data = new byte[tam];
                dataInputStream.readFully(data, 0, tam); // leemos todos los datos

               /* ByteArrayOutputStream out = new ByteArrayOutputStream();
                int actual = 0;
                int leido = 0;
                InputStream in = socket.getInputStream();

                while ((leido = in.read(data)) != -1)
                {
                    out.write(data, actual, leido);
                    actual += leido;
                    out.flush();


                    Log.d("He leido del bitmap: ", leido+" bytes actual: "+actual );
                }*/

                Bitmap breceived = BitmapFactory.decodeByteArray(data, 0, tam);//Convertimos el bytearray en bitmap
                new ShowImage(breceived, MiMensajeView.NOPROPIO).start();//Se añade a la vista
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
