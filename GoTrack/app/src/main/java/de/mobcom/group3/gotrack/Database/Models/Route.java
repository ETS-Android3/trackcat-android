package de.mobcom.group3.gotrack.Database.Models;

import android.location.Location;
import java.util.ArrayList;

public class Route {
    private long id;
    private long userId;
    private String name;
    private double time;
    private double distance;
    private ArrayList<Location> locations;

    public Route() {}

    public Route(long id, long userId, String name, double time, double distance, ArrayList<Location> locations) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.time = time;
        this.distance = distance;
        this.locations =locations;
    }

    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return this.userId;
    }
    public void setUserID(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getTime() {
        return time;
    }
    public void setTime(double time) {
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
}
