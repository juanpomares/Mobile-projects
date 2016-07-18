package com.ua.juanchema.mastermoviles.tdam.proyectofinalsockets;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Usuario on 23/11/2015.
 */
public class Client extends ConversationClass
{
    /* *************************************************************************** */
    //                     Para evitar que se apague la pantalla
    /* *************************************************************************** */
    protected PowerManager.WakeLock wakelock;
    /* *************************************************************************** */
    //                             Datos Intent
    /* *************************************************************************** */
    private String mIp;

    String ServerName;


    //Hilos de ejecucion
    ClientFirstConnection clientConnecting;
    GetMessagesThread hearMessages;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Establecemos que la app solo pueda estar en vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Para evitar que la pantalla se apague
        final PowerManager pm=(PowerManager)getSystemService(getApplicationContext().POWER_SERVICE);
        this.wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        wakelock.acquire();

        //Obtenemos valores del intent
        mIp = getIntent().getStringExtra("inicio_cliente.ip");
        //Log.e("DETALLES client solo: ", "IP:" + mIp);

        ServerName="";
        setConnectionEstablislhed(false);

        (clientConnecting = new ClientFirstConnection()).start();//Abrimos el hilo para conectarnos al servidor
    }


    @Override
    protected void onResume() {
        super.onResume();
        wakelock.acquire();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {//Para crear el action menu
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_conversation_cliente, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.MenuSendArchivo:

                if(ConectionEstablished)
                    HacerFoto();
                else
                    Toast.makeText(getApplicationContext(), "Hace falta estar conectado para enviar archivos.",
                            Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    private class GetMessagesThread extends Thread
    {
        public boolean executing;
        Socket socket;
        private String line;

        GetMessagesThread(Socket s){socket=s;}

        public void run()
        {
            executing=true;
            //Log.d("Executing", "RunGetMessages");

            try {
                dataInputStream = new DataInputStream(socket.getInputStream());//Creamos el inputstream
            }catch(Exception e){ Log.d("Error DatainputStream", "jejeje"); e.printStackTrace();}

            while(executing)
            {
                //Log.d("Executing", "RunGetMessages");

                line="";
                line=ObtenerCadena();//Obtenemos la cadena
                //Log.d("Cadena_recibida", line);
                if(line!="" && line.length()!=0)
                    ProcesarCadena();//Procesamos la cadena
            }
        }

        private void ProcesarCadena()
        {
            String[] trozos=line.split(TipoMensaje.delimitador);
            if(trozos.length>1)
            {
                Log.d("Trozos", ""+trozos.length);
                switch (Integer.parseInt(trozos[0]))
                {
                    case TipoMensaje.NombreServer://Obtenemos el nombre del servidor
                    {
                        ServerName=trozos[1];
                    }break;

                    case TipoMensaje.RespuestaConexion:
                    {
                        clientConnecting.respuesta=Integer.parseInt(trozos[1]);//Notificamos al hilo de conexion la respuesta del servidor
                    }break;

                    case TipoMensaje.MensajeNormal:
                    {
                        new ShowMessageReceived(trozos[1]).start();//Mostramos la imagen en la interfaz
                    }break;

                    case TipoMensaje.Desconexion:
                    {
                        new ShowMessageInfo("Se ha desconectado "+trozos[1]+" :(").run();//Añadimos el mensaje a la interfaz, que se ha desconectado

                        //Cerramos el hilo de escucha, así como los sockets y streams
                        CerrarHiloEscucha();
                        CloseSocketInputs();
                    }break;

                    case TipoMensaje.EnvioImagen:
                    {

                        Log.d("Voy a recibir un bitmap", "jejejeje");

                        new ReceiveBitmapThread(Integer.parseInt(trozos[1])).run();//Abrimos el hilo de recepcion de imagen
                    }break;

                    default:
                        break;
                }
            }
        }

        private String ObtenerCadena()
        {
            String cadena="";

            try {
                cadena=dataInputStream.readUTF();//Leemo la cadena del stream de datos
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            return cadena;
        }
    }

    public void EnviarMensaje(View v)
    {
        if(ConectionEstablished)
        {
            String jeje=EditTextMensaje.getText().toString();//Obtenemos la cadena

            if(jeje!="" && jeje.length()>0)//Comprobamos que la cadena no sea vacia
            {
                new SocketSendMessageThread(TipoMensaje.MensajeNormal+TipoMensaje.delimitador+jeje, true).start();//Creamos un hilo para el envio del nuevo mensaje
                EditTextMensaje.setText("");
            }
        }
    }

    private class SocketSendMessageThread extends Thread
    {
        private boolean mostrar_mensaje_enviado;
        private String msg;

        SocketSendMessageThread(String message, boolean show_msg)
        {
            msg=message;
            mostrar_mensaje_enviado=show_msg;
        }

        @Override
        public void run()
        {
            try
            {
                dataOutputStream.writeUTF(msg);//Enviamos el mensaje
                //dataOutputStream.close();
                if(mostrar_mensaje_enviado)
                {
                    String[] trozos=msg.split(TipoMensaje.delimitador);
                    if(trozos.length>1)
                    {
                        switch (Integer.parseInt(trozos[0]))
                        {
                            case TipoMensaje.MensajeNormal:
                            {
                                new ShowMessageSended(trozos[1]).start();//Añadimos el mensaje a la interfaz
                            }break;
                        }
                    }
                }
            }catch (IOException e)
            {
                e.printStackTrace();
                //message += "¡Algo fue mal! " + e.toString() + "\n";
            }
        }
    }

    private class ClientFirstConnection extends Thread
    {
        int respuesta;
        public void run()
        {
            changeTitle("Esperando Servidor...");
            respuesta=-1;
            try
            {
                new ShowMessageInfo("Creando el socket...").start();//Mostramos un mensaje para indicar que estamos creando el socket
                socket = new Socket(mIp, Integer.parseInt(mPuerto));//Creamos el socket


                new ShowMessageInfo("Conectando con el servidor: "+mIp+":"+mPuerto+"...").start();//Mostramos por la interfaz que nos hemos conectado al servidor

                (hearMessages=new GetMessagesThread(socket)).start();//Creamos el hilol de escucha de mensajes
                dataOutputStream= new DataOutputStream(socket.getOutputStream());//Iniciamos el dataoutputstream

                while(ServerName.length()<1){}//Esperamos a que el servidor nos envie su nombre

                //Añadimos un mensaje a la interfaz indicando que estamos pidiendo autorización en el servidor
                new ShowMessageInfo("Pidiendo autorización para entrar al servidor: '"+ServerName+"'").start();

                //Le enviamos al servidor nuestro nombre, para que pueda aceptarnos o rechazarnos jejeje
                SocketSendMessageThread sendNameUser;
                sendNameUser=new SocketSendMessageThread(TipoMensaje.NombreUser+TipoMensaje.delimitador+mNombreUsuario, true);
                sendNameUser.start();

                while(respuesta==-1){}//Esperamos la respuesta


                if(respuesta==1)
                {//Si nos han aceptado, mostramos un mensaje afirmativo, en caso contrario, el mensaje será negativo :(
                    changeTitle(ServerName);
                    new ShowMessageInfo("Has entrado al servidor: '"+ServerName+"', ya puedes hablar todo lo que quieras jeje").start();
                    setConnectionEstablislhed(true);
                }else
                {
                    changeTitle("RECHAZADO HIJOPUTA");
                    setConnectionEstablislhed(false);
                    new ShowMessageInfo("Te han denegado la entrada al servidor: '"+ServerName+"'").start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        this.wakelock.release();
        if(ConectionEstablished)//Si hay conexion, avisamos al usuario que nos vamos a desconectar, para que pueda cerrar su socket y DataStream
            (new SocketSendMessageThread(TipoMensaje.Desconexion+TipoMensaje.delimitador+mNombreUsuario, false)).run();

        //Cerramos hilo de escuchar mensajes y el socket
        CerrarHiloEscucha();
        CloseSocketInputs();
    }

    private void CerrarHiloEscucha()
    {
        if(hearMessages!=null)
        {
            hearMessages.executing=false;
            hearMessages.interrupt();
            hearMessages=null;
        }
    }


    private class SocketSendBitmapThread extends Thread
    {
        private Bitmap b;

        SocketSendBitmapThread(Bitmap bitmap)
        {
            b=bitmap;
        }

        @Override
        public void run()
        {
            try
            {
                //Obtenemos el array de bytes de la imagen
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                int tam;
                if((tam=byteArray.length)>0)
                {
                    //Notificamos al servidor que va a recibir un imagen
                    new SocketSendMessageThread(TipoMensaje.EnvioImagen+TipoMensaje.delimitador+tam, false).run();

                    //Enviamos la imagen al servidor
                    dataOutputStream.write(byteArray, 0, tam);

                    /*OutputStream out=socket.getOutputStream();
                    int pasado=0;
                    int paso;
                    while(pasado<tam)
                    {
                        paso=tam-pasado;
                        if(paso>1024)
                            paso=1024;
                        out.write(byteArray, pasado, paso);
                        Log.d("Paso Bytes", "Paso: "+paso+" he pasado: "+pasado+" total: "+tam);
                        out.flush();
                        pasado+=paso;
                    }*/


                    new ShowImage(b, MiMensajeView.PROPIO).start();//Añadimos la imagen enviada a la interfaz

                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                //message += "¡Algo fue mal! " + e.toString() + "\n";
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1234)
        {
            if(data!=null && data.getExtras()!=null && data.getExtras().get("data")!=null)//Comprobamos que todo ha ido bien
            {
                Bitmap thumbnail=(Bitmap) data.getExtras().get("data");//Obtenemos la miniatura capturada

                if (ConectionEstablished)//Si hay conexion
                    new SocketSendBitmapThread(thumbnail).start();//Creamos un hilo para enviar la imagen
            }
        }
    }

}
