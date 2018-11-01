package com.example.finnl.gotrack.Statistics;

import android.location.Location;

import com.example.finnl.gotrack.MainActivity;

public class KmCounter {
    private float mAmount;
    private MainActivity creator;
    private Location oldLocation;


    public KmCounter(MainActivity creator) {
        this.creator = creator;
    }

    // add Distance between old and new Location to amount
    public void addKm(Location newLocation) {
        if (oldLocation == null) {
            oldLocation = newLocation;
        } else {
            float newDisctance = oldLocation.distanceTo(newLocation);
            mAmount = mAmount + newDisctance;
            oldLocation = newLocation;
        }
    }

    public double getAmount() {
        return mAmount;
    }
}
