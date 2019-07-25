package de.trackcat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import de.trackcat.Database.Models.User;

public class ClosingService extends Service {

    private static ClosingService instance;
    public static ClosingService getInstance(){
        return instance;
    }
    int currentUser = MainActivity.getInstance().getActiveUser();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flag,int startId){
        super.onStartCommand(intent, flag, startId);
        Log.d("GESCHLOSSEN","OFFEN:" + currentUser);
        instance=this;
        return START_STICKY;

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

       GlobalFunctions.deleteAllTempRecord(this, currentUser);

        // Handle application closing
       // fireClosingNotification();

        // Destroy the service
      //  stopSelf();
        Log.d("GESCHLOSSEN","GESCHLOSSEN");
        super.onTaskRemoved(rootIntent);

         stopSelf();
    }

    @Override
    public void onDestroy() {


      //  MainActivity.getInstance().deleteAllTempRecord();
        Log.d("GESCHLOSSEN", "Service Destroyed");
        super.onDestroy();
    }

}
