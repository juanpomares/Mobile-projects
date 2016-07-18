package com.ua.juanchema.mastermoviles.tdam.proyectofinalsockets;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Usuario on 23/11/2015.
 */
public class ConfigureClient extends AppCompatActivity
{
    /* *************************************************************************** */
    //                     Para evitar que se apague la pantalla
    /* *************************************************************************** */
    protected PowerManager.WakeLock wakelock;

    /* *************************************************************************** */
    //                                  Spinner
    /* *************************************************************************** */
    Spinner mySpinner;
    String[] mFondos = {"Classic CJ", "Azulado CJ", "Rojizo CJ" };
    Integer[] mImages = {R.drawable.miniatura_clasico, R.drawable.miniatura_azul, R.drawable.miniatura_rojo};
    /* *************************************************************************** */
    //                      Variables para el pattern de la IP
    /* *************************************************************************** */
    private Pattern pattern;
    private Matcher matcher;
    private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";


    /* *************************************************************************** */
    //Inicializacion de componentes y otros, empiezan los metodos
    /* *************************************************************************** */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configurar_client_activity);

        setTitle("Configurar Cliente");

        //Establecemos que la app solo pueda estar en vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Para evitar que la pantalla se apague
        final PowerManager pm=(PowerManager)getSystemService(getApplicationContext().POWER_SERVICE);
        this.wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        wakelock.acquire();


        //Establecemos un valor por defecto al spinner y lo inicializamos
        mySpinner =(Spinner) findViewById(R.id.inicio_cliente_spinner_fondo);
        mySpinner.setAdapter(new MyAdapter(ConfigureClient.this, R.layout.customspinner, mFondos));

        //Hacemos que al abrir la actividad, el foco aparezca en el campo de ip
        EditText ipEscrita = (EditText)findViewById(R.id.inicio_cliente_ET_direccionIP);
        ipEscrita.requestFocus();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.wakelock.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakelock.acquire();

        //Al volver a la actividad, hacemos que el foco se quede en ip
        EditText ipEscrita = (EditText)findViewById(R.id.inicio_cliente_ET_direccionIP);
        ipEscrita.requestFocus();

    }


    /* *************************************************************************** */
    //Validacion de datos y carga de la actividad de conversacion
    /* *************************************************************************** */
    public void inicio_clienteAbreConexion(View view)
    {
        //VALIDAMOS IP
        EditText ipEscrita = (EditText)findViewById(R.id.inicio_cliente_ET_direccionIP);
        if(validate(ipEscrita.getText().toString()))
        {
            //VALIDAMOS PUERTO
            EditText puertoEscrito = (EditText)findViewById(R.id.inicio_cliente_ET_Puerto);
            if(PuertoValidator(puertoEscrito.getText().toString()))
            {
                //VALIDAMOS NOMBRE
                EditText nombreEscrito = (EditText)findViewById(R.id.inicio_cliente_ET_nombreUsuario);
                if(NombreValidator(nombreEscrito.getText().toString()))
                {
                    //OBTENEMOS VALOR DE FONDO DEL SPINNER Y LO PASAMOS A INT
                    String text = mySpinner.getSelectedItem().toString();
                    int fondo = 0;
                    if (text.charAt(0) == 'C')
                    {
                        fondo = 0;
                    }
                    else if (text.charAt(0) == 'A')
                    {
                        fondo = 1;
                    }
                    else if (text.charAt(0) == 'R')
                    {
                        fondo = 2;
                    }
                    //LANZAMOS INTENT CON LA INFORMACION NECESARIA
                    Log.e("DETALLES", "Lanzamos el intent");
                    Log.e("DETALLES", "IP:"+ipEscrita.getText().toString()+"\nPuerto: "+puertoEscrito.getText().toString()+"\nNombre Uusuario: "+nombreEscrito.getText().toString()+"\nFondo: "+fondo);
                    Intent abreConversacion = new Intent();
                    abreConversacion.setClass(getApplicationContext(), Client.class);
                    abreConversacion.putExtra("inicio_cliente.ip", ipEscrita.getText().toString());
                    abreConversacion.putExtra("inicio.puerto",puertoEscrito.getText().toString());
                    abreConversacion.putExtra("inicio.nombre",nombreEscrito.getText().toString());
                    abreConversacion.putExtra("inicio.fondo", fondo);
                    startActivity(abreConversacion);


                    //Limpiamos formulario
                    ipEscrita.setText("");
                    puertoEscrito.setText("");
                    nombreEscrito.setText("");
                    mySpinner.setSelection(0);

                }
                else
                {
                    crearTostada("Introduce un nombre de usuario.");
                }
            }
            else
            {
                crearTostada("Puerto no válido.");
            }
        }
        else
        {
            crearTostada("Dirección IP no válida.");
        }
    }

    /* *************************************************************************** */
    // Metodo para crear toast pasandole un mensaje
    /* *************************************************************************** */
    public void crearTostada(String mensaje)
    {
        Toast toastadita = Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT);
        toastadita.show();
    }

    /* *************************************************************************** */
    // Metodo para validar el nombre de usuario (comprueba si se ha escrito algo)
    /* *************************************************************************** */
    public boolean NombreValidator(String nombre)
    {
        return (nombre.length()!=0);
    }

    /* *************************************************************************** */
    // Metodo para validar el puerto (comprueba que sea mayor de 1023 y menor que 65537)
    /* *************************************************************************** */
    public boolean PuertoValidator(String numero)
    {
        if (numero.length() !=0) {
            return (Integer.parseInt(numero) > 1023 && Integer.parseInt(numero) < 65537);
        }
        return false;
    }

    /* *************************************************************************** */
    // Metodo para validar la ip (se usa una expresion regular declarada arriba)
    /* *************************************************************************** */
    public boolean validate(final String ip)
    {
        pattern = Pattern.compile(IPADDRESS_PATTERN);
        matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    /* *************************************************************************** */
    // Clase Adapter personalizada
    /* *************************************************************************** */
    public class MyAdapter extends ArrayAdapter
    {

        public MyAdapter(Context context, int textViewResourceId, String[] objects)
        {
            super(context, textViewResourceId, objects);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent)
        {
            // Inflamos el layout del customspinner
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.customspinner, parent, false);

            //Recogemos el text view de una opcion del spinner
            TextView tvFondo = (TextView) layout.findViewById(R.id.tvFondo);
            // y le asignamos su texto
            tvFondo.setText(mFondos[position]);
            // Le ponemos un color azulito
            tvFondo.setTextColor(Color.rgb(75, 180, 225));

            // Recogemos la image view de la opcion del spinner
            ImageView img = (ImageView) layout.findViewById(R.id.imgFondo);
            // Y le asignamos un valor
            img.setImageResource(mImages[position]);

            return layout;
        }

        //Devuelve una view que se va a pintar en el dropdown del spinner
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }


        //Devuelve la view que se va a pintar pero ya sin ser en el dropdown
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }
    }
}
