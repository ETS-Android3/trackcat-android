package com.example.finnl.gotrack.Recording;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.example.finnl.gotrack.MainActivity;

public class Locator {

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

        // check Permission
        if (ActivityCompat.checkSelfPermission(creator,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(creator,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED)

        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, locationListener); // via GPS
    }

    /*
     * stops GPS Tracking
     * */
    public void stopTracking() {
        locationManager.removeUpdates(locationListener);
    }
}
