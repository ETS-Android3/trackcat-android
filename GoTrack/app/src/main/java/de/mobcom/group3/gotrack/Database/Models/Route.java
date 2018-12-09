package de.mobcom.group3.gotrack.Database.Models;

import android.location.Location;

import java.util.ArrayList;

public class Route {
    private int id;
    private int userId;
    private String name;
    private long time;
    private long rideTime;
    private double distance;
    private ArrayList<Location> locations;

    public Route() {}

    public Route(int id, int userId, String name, long time, long rideTime, double distance, ArrayList<Location> locations) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.time = time;
        this.rideTime = rideTime;
        this.distance = distance;
        this.locations =locations;
    }

    public Route(int userId, String name, long time, long rideTime, double distance, ArrayList<Location> locations) {
        this.userId = userId;
        this.name = name;
        this.time = time;
        this.rideTime = rideTime;
        this.distance = distance;
        this.locations = locations;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserID(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }

    public ArrayList<Location> getLocations() {
        return this.locations;
    }
    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public void addLocation(Location location) {
        if(this.locations == null){
            this.locations = new ArrayList<Location>();
        }
        this.locations.add(location);
    }

    public long getRideTime() {
        return rideTime;
    }

    public void setRideTime(long rideTime) {
        this.rideTime = rideTime;
    }
}
