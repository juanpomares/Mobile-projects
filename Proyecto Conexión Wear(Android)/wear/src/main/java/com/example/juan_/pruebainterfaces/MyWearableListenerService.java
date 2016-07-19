package com.example.juan_.pruebainterfaces;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MyWearableListenerService extends WearableListenerService
{
    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {
        Log.d("Listener", "MessageReceived");

        if(!AppSharedPreferences.getAppOpen(getApplicationContext()))
        {
            /*if (messageEvent.getPath().contains(PublicConstants.START_ACTIVITY))
            {*/
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            //}
        }else

            Log.d("Listener", "Abierta ya jeje");
    }
}
