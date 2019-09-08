package de.trackcat;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import de.trackcat.Database.DAO.RecordTempDAO;

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

        /* Get current user */
        currentUser = MainActivity.getInstance().getActiveUser();
        instance = this;
        return START_STICKY;

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        /* Delete all not finished */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ClearCallService.schedule(getApplicationContext(), currentUser);
        } else {
            RecordTempDAO recordTempDAO = new RecordTempDAO(ClosingService.getInstance());
            recordTempDAO.deleteAllNotFinished();
        }
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
