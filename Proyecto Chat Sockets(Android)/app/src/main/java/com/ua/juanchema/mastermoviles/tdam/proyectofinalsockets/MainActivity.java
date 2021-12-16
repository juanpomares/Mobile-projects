package com.ua.juanchema.mastermoviles.tdam.proyectofinalsockets;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.PowerManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    /* *************************************************************************** */
    //                     Para evitar que se apague la pantalla
    /* *************************************************************************** */
    protected PowerManager.WakeLock wakelock;


    /* *************************************************************************** */
    //Inicializacion de componentes y otros, empiezan los metodos
    /* *************************************************************************** */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Establecemos que la app solo pueda estar en vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Para evitar que la pantalla se apague
        final PowerManager pm=(PowerManager)getSystemService(getApplicationContext().POWER_SERVICE);
        this.wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        wakelock.acquire();
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

    }

    public void AbrirConfCliente(View v)
    {
        startActivity(new Intent(MainActivity.this, ConfigureClient.class));
    }

    public void AbrirConfServidor(View v)
    {
        startActivity(new Intent(MainActivity.this, ConfigureServer.class));
    }
}
