package com.ua.juanchema.mastermoviles.tdam.proyectofinalsockets;

import android.content.DialogInterface;
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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by Usuario on 23/11/2015.
 */
public class Server extends ConversationClass
{

    /* *************************************************************************** */
    //                     Para evitar que se apague la pantalla
    /* *************************************************************************** */
    protected PowerManager.WakeLock wakelock;
    /* *************************************************************************** */
    //                             Datos Intent Propios
    /* *************************************************************************** */
    private Boolean mAutoaceptar;



    //Hilo para esperar clientes en el chat
    WaitingClientThread HiloEspera;
    //Hilo para escuchar los mensajes que le lleguen por el socket
    GetMessagesThread HiloEscucha;

    String UserName;
    ServerSocket serverSocket;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Establecemos que la app solo pueda estar en vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Para evitar que la pantalla se apague
        final PowerManager pm=(PowerManager)getSystemService(getApplicationContext().POWER_SERVICE);
        this.wakelock=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");//pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        wakelock.acquire();

        //Obtenemos valores del intent
        mAutoaceptar = getIntent().getBooleanExtra("inicio_servidor.autoaceptar",false);
        //Log.e("DETALLES server solo: ","Autoaceptar? " + mAutoaceptar);

        //Inicializamos las variables
        UserName=""; serverSocket=null;

        //Iniciamos el hilo de espera de clientes
        (HiloEspera=new WaitingClientThread()).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakelock.acquire();
    }


    public void Crear_Dialogo_Aceptar_Usuario(String nombre_usuario)
    {
        //Si esta el autoaceptado activado no preguntamos, aceptamos la conexion directamente
        if(mAutoaceptar)
            HiloEspera.aceptar_conexion = 1;
        else
        {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("¿ Aceptas a '"+nombre_usuario+"' a entrar en el chat?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    //Notificamos al hilo que se ha aceptado
                    HiloEspera.aceptar_conexion = 1;

                    Toast toast1 =Toast.makeText(getApplicationContext(), "Aceptada conexion", Toast.LENGTH_SHORT);
                    toast1.show();

                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    //Notificamos al hilo que se ha rechazado
                    HiloEspera.aceptar_conexion = 0;

                    Toast toast1 =Toast.makeText(getApplicationContext(), "Cancelada conexion", Toast.LENGTH_SHORT);
                    toast1.show();
                }
            });
            dialogo1.show();
        }
    }

    public void EnviarMensaje(View v)
    {
        if(ConectionEstablished)//Si hay conexion
        {
            String jeje=EditTextMensaje.getText().toString();

            if(jeje!="" && jeje.length()>0)//Comprobamos si hay texto introducido
            {
                //Creamos un hilo para enviar el mensaje y borramos la entrada del usuario de la interfaz
                new SocketSendMessageThread(TipoMensaje.MensajeNormal+TipoMensaje.delimitador+jeje, true).start();
                EditTextMensaje.setText("");
            }
        }else
        {//Si no hay conexion mostramos un mensaje de error
            Toast.makeText(this, "Debes estar conectado para poder enviar mensajes", Toast.LENGTH_LONG).show();
        }
    }

    private class SocketSendMessageThread extends Thread
    {
        private boolean mostrar_mensaje_enviado;//Variable usada para saber si se debe introducir o no el mensaje en la interfaz
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
                dataOutputStream.writeUTF(msg);//Escribimos la cadena por el outputstream

                if(mostrar_mensaje_enviado)//si se desea mostrar el mensaje
                {
                    String[] trozos=msg.split(TipoMensaje.delimitador);
                    if(trozos.length>1)
                    {
                        switch (Integer.parseInt(trozos[0]))
                        {
                            case TipoMensaje.MensajeNormal:
                            {
                                //Creamos un hilo para mostrar el mensaje
                                new ShowMessageSended(trozos[1]).start();
                            }break;
                        }
                    }
                }
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //Para añadir los action items(iconos de la barra jeje)
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_conversation_server, menu);
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
                    Toast.makeText(getApplicationContext(), "No estás conectado, no puedes enviar archivos",
                            Toast.LENGTH_SHORT).show();
                return true;
            case R.id.MenuExpulsarUsuario:
                if(ConectionEstablished)
                {
                    //Notificacoms al usuario que se le ha expulsado del servidor
                    new SocketSendMessageThread(TipoMensaje.Desconexion+TipoMensaje.delimitador+mNombreUsuario, false).run();
                    ReiniciarConexion();//Reiniciamos la conexion para que el servidor, se quede esperando nuevo cliente
                }else
                {
                    Toast.makeText(getApplicationContext(), "No estás conectado jeje",
                            Toast.LENGTH_SHORT).show();
                }

                return true;
        }

        return false;
    }

    //Hilo prinicpal de escucha de mensajes
    private class GetMessagesThread extends Thread
    {
        public boolean executing;
        Socket socket;
        private String line;


        GetMessagesThread(Socket s){socket=s;}

        public void run()
        {
            executing=true;

            while(executing)
            {
                line="";
                line=ObtenerCadena();//Obtenemos la cadena del buffer
                if(line!="" && line.length()!=0)//Comprobamos que esa cadena tenga contenido
                    ProcesarCadena();//Procesamos la cadena recibida
            }
        }

        private void ProcesarCadena()
        {
            String[] trozos=line.split(TipoMensaje.delimitador);//Dividimos la cadena para saber que tipo de mensaje es
            if(trozos.length>1)
            {
                //Log.d("Trozos", ""+trozos.length);
                switch (Integer.parseInt(trozos[0]))
                {
                    case TipoMensaje.NombreUser://Si es el nombre de usuario notificamos al hilo de espera de conexion que ya tenemos el nombre de usuario
                    {
                        HiloEspera.UserName=trozos[1];
                    }
                    break;

                    case TipoMensaje.MensajeNormal://Si es un mensaje normal, creamos un hilo para añadirlo a la interfaz
                    {
                        new ShowMessageReceived(trozos[1]).start();
                    }break;

                    case TipoMensaje.EnvioImagen://Si es un fichero, añadimos un hilo que maneje la recepcion del mismo
                    {
                        new ReceiveBitmapThread(Integer.parseInt(trozos[1])).run();
                    }break;

                    case TipoMensaje.Desconexion://Si el usuario se desconecta, mostrmoas en la pantalla que se ha desconectado y reinicimaos la conexión, para esperar nuevos usuarios
                    {
                        new ShowMessageInfo("Se ha desconectado "+trozos[1]+" :(").run();
                        ReiniciarConexion();

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
                cadena=dataInputStream.readUTF();//Leemos del datainputStream una cadena UTF

            }catch(Exception e)
            {
                e.printStackTrace();
            }
            return cadena;
        }
    }


    //Metodo que cierra el hilo de escucha de mensajes
    private void CerrarHiloEscucha()
    {
        if(HiloEscucha!=null)
        {
            HiloEscucha.executing=false;
            HiloEscucha.interrupt();
            HiloEscucha=null;
        }
    }

    private void ReiniciarConexion()
    {
        CerrarHiloEscucha();
        CloseSocketInputs();
        (HiloEspera=new WaitingClientThread()).start();//iniciamos el hilo de espera de usuarios
    }

    //Aqui obtenemos la IP de nuestro terminal
    private String getIpAddress()
    {
        String ip = "";
        try
        {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements())
                {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress())
                    {
                        ip += "IP de Servidor: " + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e)
        {
            e.printStackTrace();
            ip += "¡Algo fue mal! " + e.toString() + "\n";
        }

        return ip;
    }

    private class WaitingClientThread extends Thread
    {
        public boolean esperando_nuevos_clientes;

        public int aceptar_conexion;
        String UserName;

        public void run()
        {
            changeTitle("Esperando Usuario...");
            esperando_nuevos_clientes=true;
            try
            {
                //Abrimos el socket
                serverSocket = new ServerSocket(Integer.parseInt(mPuerto));

                //Mostramos un mensaje para indicar que estamos esperando en la direccion ip y el puerto...
                new ShowMessageInfo("Creado el servidor\n Dirección: "+getIpAddress()+"\nPuerto: "+serverSocket.getLocalPort()).start();


                //Bucle para dejar al servidor a la escucha de clientes
                while (esperando_nuevos_clientes)
                {
                    //Creamos un socket que esta a la espera de una conexion de cliente
                    Socket socket = serverSocket.accept();


                    //Una vez hay conexion con un cliente, creamos los streams de salida/entrada
                    try {
                        dataInputStream = new DataInputStream(socket.getInputStream());
                        dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    }catch(Exception e){ e.printStackTrace();}


                    //Iniciamos el hilo para la escucha y procesado de mensajes
                    (HiloEscucha=new GetMessagesThread(socket)).start();

                    //Creamos un mensaje en la interfaz indicando que hemos encontrado un nuevo usuario
                    new ShowMessageInfo("Se ha encontrado un nuevo usuario, esperando nombre de usuario...").start();

                    //Enviamos al usuario el nombre del servidor
                    SocketSendMessageThread sendNameServer;
                    sendNameServer=new SocketSendMessageThread(TipoMensaje.NombreServer+TipoMensaje.delimitador+mNombreUsuario, false);
                    sendNameServer.run();

                    UserName="";//Esperamos a que el usuario nos envie su nombre
                    while(UserName.length()<1){}

                    changeTitle(UserName);//Ponemos el nombre de usuario como titulo

                    aceptar_conexion=-1;

                    Server.this.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Crear_Dialogo_Aceptar_Usuario(UserName);
                        }
                    });

                    //Esperamos que el usuario acepte la conexion
                    while(aceptar_conexion==-1);


                    if(aceptar_conexion==1)
                    {
                        //Mostramos un mensaje indicando que hemos aceptado al usuario
                        new ShowMessageInfo("Has aceptado a '"+UserName+"', ya puedes hablar con él jeje").start();

                        //Notificamos al usuario que se ha aceptado la conexion
                        SocketSendMessageThread enviarmensaje;
                        enviarmensaje=new SocketSendMessageThread(TipoMensaje.RespuestaConexion+TipoMensaje.delimitador+"1", true);
                        enviarmensaje.run();

                        esperando_nuevos_clientes=false;
                        setConnectionEstablislhed(true);
                    }else
                    {
                        changeTitle(UserName+" rechazado");

                        //Mostramos un mensaje indicando que hemos rechazado al usuario
                        new ShowMessageInfo("Has rechazado a '"+UserName+"' :(\n Esperando nuevos usuarios...").start();

                        //Notificamos al usuario que se ha rechazado la conexion
                        SocketSendMessageThread enviarmensaje;
                        enviarmensaje=new SocketSendMessageThread(TipoMensaje.RespuestaConexion+TipoMensaje.delimitador+"0", true);
                        enviarmensaje.run();

                        setConnectionEstablislhed(false);

                        //Cerramos el hilo de escucha y los streams, porque cuando haya un nuevo usuario se volveran a crear
                        CerrarHiloEscucha();
                        SuperCloseSocketInputs();
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
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
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();//Obtenemos un array de bytes de la imagen para poder enviarlo

                int tam;
                if((tam=byteArray.length)>0)
                {

                    //Enviamos un mensaje al usuario para decirle que va a recibir un archivo, asi como el tamaño del mismo
                    new SocketSendMessageThread(TipoMensaje.EnvioImagen+TipoMensaje.delimitador+tam, false).run();

                    //Escribimos por el stream todos los datos del fichero
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
                    new ShowImage(b, MiMensajeView.PROPIO).start();//Añadimos la imagen enviada a al interfaz
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                //message += "¡Algo fue mal! " + e.toString() + "\n";
            }
        }
    }

    protected  void SuperCloseSocketInputs()
    {
        super.CloseSocketInputs();
    }

    @Override
    protected void CloseSocketInputs()
    {
        SuperCloseSocketInputs();//Cerramos los streams y el socket
            try {
                serverSocket.close();//cerramos el serversocket
            } catch (Exception e) {
            } finally {
                serverSocket = null;
            }

    }

    @Override
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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        this.wakelock.release();

        if(ConectionEstablished)//Si hay conexion, avisamos al usuario que nos vamos a desconectar, para que pueda cerrar su socket y DataStream
            new SocketSendMessageThread(TipoMensaje.Desconexion + TipoMensaje.delimitador + mNombreUsuario, false).run();

        CerrarHiloEscucha();
        CloseSocketInputs();
    }

}
