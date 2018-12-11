package de.mobcom.group3.gotrack.Recording;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import de.mobcom.group3.gotrack.MainActivity;

public class Locator {

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

    /*
     * initialize Locator and start Tracking
     * */
    public Locator(MainActivity creator, RecordFragment parent) {
        this.parent = parent;
        this.creator = creator;
        this.instance = this;

        /*
         * initialize Locationlistener
         * */
        int minTime = 1;
        int minDistance = 5;

        locationManager = (LocationManager) creator.getSystemService(Context.LOCATION_SERVICE);


        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

                instance.parent.updateLocation(location);
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


    /*
     * starts GPS Tracking
     * */
    protected void startTracking() {

        /*
         + Check if permissions granted for GPS service and storage access
         + toDo: verify why location and storage permissions aren't requested simultaneously
         */
        // For GPS:
        if (ActivityCompat.checkSelfPermission(creator, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(creator, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else if (ActivityCompat.checkSelfPermission(creator,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(creator, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
}
