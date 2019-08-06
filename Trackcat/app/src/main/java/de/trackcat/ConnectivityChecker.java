package de.trackcat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityChecker extends BroadcastReceiver {

    public static boolean connected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager mgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mgr.getActiveNetworkInfo();

        connected = networkInfo != null && networkInfo.isConnected();

        try {
            MainActivity.getInstance().networkChange(connected);
        } catch (Exception e) {
        }
    }
}