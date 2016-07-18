package com.opengl4.android;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.*;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


    }

    public void crearCuadrica(View v)
    {
        Intent intent=null;

        switch (v.getId())
        {
            case R.id.buttonEsfera:

                intent=new Intent(MenuActivity.this, SphereActivity.class);

                break;

            case R.id.buttonToroide:
                intent=new Intent(MenuActivity.this, ToroideActivity.class);
                //intent.putExtra("Cuadrica", 1);

                break;

            case R.id.buttonSuper:
                intent=new Intent(MenuActivity.this, SuperCuadricActivity.class);
                //intent.putExtra("Cuadrica", 2);

                break;

        }

        if(intent!=null)
            startActivity(intent);
    }

    //@Override
    /*public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
