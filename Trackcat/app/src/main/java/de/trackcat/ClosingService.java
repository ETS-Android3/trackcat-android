package de.trackcat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class ClosingService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        // Handle application closing
       // fireClosingNotification();

        // Destroy the service
      //  stopSelf();
        Log.d("GESCHLOSSEN","GESCHLOSSEN");

         stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("GESCHLOSSEN", "Service Destroyed");
    }

}
