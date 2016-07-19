package com.example.juan_.pruebainterfaces;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MyInitialActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppSharedPreferences.setAppOpen(getApplicationContext(), false);

        Log.d("InitialActivity", "EntroNuevo4");
        setContentView(new TextView(this));
        Toast.makeText(this, "Esta aplicación debe abrirse desde el teléfono", Toast.LENGTH_LONG).show();
        this.finish();
    }

}
