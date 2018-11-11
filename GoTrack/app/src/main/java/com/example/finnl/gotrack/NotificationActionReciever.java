package com.example.finnl.gotrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        /*
         * Recieves Message when pause Button in Notification is clicked
         * */
        String msg = intent.getAction();
        if (msg != null && msg.equalsIgnoreCase("ACTION_PAUSE")) {
            MainActivity.getInstance().stopTracking();
        }
    }
}
