package de.trackcat;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import de.trackcat.Database.DAO.RecordTempDAO;
import de.trackcat.Database.Models.User;

public class ClosingService extends Service {

    private static ClosingService instance;

    public static ClosingService getInstance() {
        return instance;
    }

    int currentUser;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        super.onStartCommand(intent, flag, startId);
        currentUser = MainActivity.getInstance().getActiveUser();
        Log.d("GESCHLOSSEN", "START currentUser:" + currentUser);
        instance = this;
        return START_STICKY;

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ClearCallService.schedule(getApplicationContext(), currentUser);
            Log.d("GESCHLOSSEN", "TASK REMOVED größer Lollipop: " + currentUser + "");
        } else {
            Log.d("GESCHLOSSEN", "kleiner Lollipop: " + currentUser + "");
            RecordTempDAO recordTempDAO = new RecordTempDAO(ClosingService.getInstance());
            recordTempDAO.deleteAllNotFinished();
        }


        // Handle application closing
        // fireClosingNotification();

        // Destroy the service
        //  stopSelf();
        Log.d("GESCHLOSSEN", "GESCHLOSSEN");
        super.onTaskRemoved(rootIntent);

        // stopSelf();
    }

    @Override
    public void onDestroy() {


        //  MainActivity.getInstance().deleteAllTempRecord();
        Log.d("GESCHLOSSEN", "ON DESTROY");
        super.onDestroy();
    }

}
