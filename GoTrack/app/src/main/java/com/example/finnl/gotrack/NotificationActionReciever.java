package com.example.finnl.gotrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationActionReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String msg =intent.getAction();
        if(msg.equalsIgnoreCase("ACTION_PAUSE")){

            MainActivity.getInstance().stopTracking();




        }
    }
}
