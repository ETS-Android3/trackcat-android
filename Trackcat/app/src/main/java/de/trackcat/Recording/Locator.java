package de.trackcat.Recording;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;


import de.trackcat.MainActivity;

public class Locator extends Service {

    // standard value for requesting permissions
    private static final int MY_PERMISSIONS_REQUEST = 1;

    private LocationManager locationManager;
    private LocationListener locationListener;


    /* called when foreground service is started */
    @Override
    public void onCreate() {
        startForeground(12345678, getNotification());

        init();
    }

    /*
     * initialize locator and start tracking
     * called when oreo or higher
     * */
    public Locator() {
        // init();
    }


    public void init() {
        /*
         * initialize locationListener
         * */
        try {
            locationManager = (LocationManager) MainActivity.getInstance().getSystemService(Context.LOCATION_SERVICE);
         /*   if (ActivityCompat.checkSelfPermission(MainActivity.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.addNmeaListener(mNmeaListener);*/


            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // called when a new location is found by the network location provider.

                    // send message to View
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


            /* start locating */
            startTracking();
        } catch (Exception e) {
        }

    }

    private GpsStatus.NmeaListener mNmeaListener = new GpsStatus.NmeaListener() {
        @Override
        public void onNmeaReceived(long timestamp, String nmea) {
            parseNmeaString(nmea);
        }
    };

    private void parseNmeaString(String line) {
        if (line.startsWith("$")) {
            String[] tokens = line.split(",");
            String type = tokens[0];

            // Parse altitude above sea level, Detailed description of NMEA string here http://aprs.gids.nl/nmea/#gga
            if (type.startsWith("$GPGGA")) {
                double lat = 0;
                double lon = 0;
                double altitude = 0;
                double h = 0;
                double geoId = 0;
                double H = 0;
                if (!tokens[2].isEmpty()) {
                    lat = Double.parseDouble(tokens[2]);
                }
                if (!tokens[4].isEmpty()) {
                    lon = Double.parseDouble(tokens[4]);
                }

                if (!tokens[9].isEmpty()) {
                    altitude = Double.parseDouble(tokens[9]);
                }

                if (!tokens[11].isEmpty()) {
                    geoId = Double.parseDouble(tokens[11]);
                }

                //  altitude=h-geoId;


                Log.d("TESTFÜRALTITUDE", "Lat: " + lat + " Lon: " + lon + " Altitude: " + altitude);
            }
        }
    }

    /*
     * create notification for foreground service
     * */
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

    /*
     * starts GPS tracking
     * */
    protected void startTracking() {

        /*
         + checks if permissions granted for GPS service and storage access
         */
        // for GPS:
        if (ActivityCompat.checkSelfPermission(MainActivity.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
            // for external storage
        } else if (ActivityCompat.checkSelfPermission(MainActivity.getInstance(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST);
        } else {
            int minDistance = 1;
            int minTime = 10;

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,
                    minDistance, locationListener); // via GPS

            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    /*
     * stops GPS tracking
     * */
    public void stopTracking() {
        try{
        locationManager.removeUpdates(locationListener);}catch(Exception e){}
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
