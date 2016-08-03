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

    public void GoToQuadricActivity(View v)
    {
        Intent intent=null;

        switch (v.getId())
        {
            case R.id.buttonEsfera:
                intent=new Intent(MenuActivity.this, SphereActivity.class);
                break;

            case R.id.buttonToroide:
                intent=new Intent(MenuActivity.this, ToroideActivity.class);
                break;

            case R.id.buttonSuper:
                intent=new Intent(MenuActivity.this, SuperCuadricActivity.class);
                break;

        }

        if(intent!=null)
            startActivity(intent);
    }
}
