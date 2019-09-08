package de.trackcat.Recording;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import de.trackcat.MainActivity;

public class Locator extends Service {

    /* Standard value for requesting permissions */
    private static final int MY_PERMISSIONS_REQUEST = 1;

    private LocationManager locationManager;
    private LocationListener locationListener;

    /* Called when foreground service is started */
    @Override
    public void onCreate() {
        startForeground(12345678, getNotification());
        init();
    }

    public void init() {

        /* Initialize locationListener */
        try {
            locationManager = (LocationManager) MainActivity.getInstance().getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {

                    /* Send message to View */
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = location;
                    RecordFragment.handler.sendMessage(msg);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

            /* Start locating */
            startTracking();
        } catch (Exception e) {
        }
    }

    /* Create notification for foreground service */
    private Notification getNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "channel_01",
                        "My Channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
                Notification.Builder builder;

                builder = new Notification.Builder(getApplicationContext(), "channel_01");

                return builder.build();
            }
        }
        return null;
    }

    /* Starts GPS tracking */
    protected void startTracking() {

        /*
         + checks if permissions granted for GPS service and storage access
         */
        // for GPS:
        if (ActivityCompat.checkSelfPermission(MainActivity.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
            /* for external storage */
        } else if (ActivityCompat.checkSelfPermission(MainActivity.getInstance(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST);
        } else {
            int minDistance = 1;
            int minTime = 10;

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,
                    minDistance, locationListener); // via GPS

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    /* Stops GPS tracking */
    public void stopTracking() {
        try {
            locationManager.removeUpdates(locationListener);
        } catch (Exception e) {
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
