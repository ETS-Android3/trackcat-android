package de.mobcom.group3.gotrack.Recording;

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
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import de.mobcom.group3.gotrack.MainActivity;

public class Locator extends Service {

    // TODO set on 5
    final private int MIN_DISTANCE = 1;
    final private int MIN_TIME = 10;

    // Standardwerte für die Abfrage der Berechtigungen
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private MainActivity creator;
    private RecordFragment parent;
    private Locator instance;

    private LocationManager locationManager;
    private LocationListener locationListener;

    
    @Override
    public void onCreate() {
        startForeground(12345678, getNotification());

        init();
    }

    private void init() {
        this.instance = this;

        /*
         * initialize Locationlistener
         * */
        int minTime = 1;
        int minDistance = 5;

        locationManager = (LocationManager) MainActivity.getInstance().getSystemService(Context.LOCATION_SERVICE);


        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

                // send message to View
                Message msg = new Message();
                msg.what = 2;
                msg.obj = location;
                RecordFragment.handler.sendMessage(msg);

                //instance.parent.updateLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            public void onProviderEnabled(String provider) {

            }

            public void onProviderDisabled(String provider) {


            }

        };

        /* start Locating */
        startTracking();
    }

    private Notification getNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "channel_01",
                        "My Channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
                Notification.Builder builder = null;

                builder = new Notification.Builder(getApplicationContext(), "channel_01");


                return builder.build();
            }
        }
        return null;
    }



    /*
     * initialize Locator and start Tracking
     * */
    public Locator() {
       init();
    }


    /*
     * starts GPS Tracking
     * */
    protected void startTracking() {

        /*
         + Check if permissions granted for GPS service and storage access
         + toDo: verify why location and storage permissions aren't requested simultaneously
         */
        // For GPS:
        if (ActivityCompat.checkSelfPermission(MainActivity.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else if (ActivityCompat.checkSelfPermission(MainActivity.getInstance(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME,
                    MIN_DISTANCE, locationListener); // via GPS
        }
    }

    /*
     * stops GPS Tracking
     * */
    public void stopTracking() {
        locationManager.removeUpdates(locationListener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
