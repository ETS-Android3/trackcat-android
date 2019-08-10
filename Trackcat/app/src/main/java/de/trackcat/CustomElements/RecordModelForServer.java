package de.trackcat.CustomElements;

import java.util.List;

import de.trackcat.Database.Models.Location;

public class RecordModelForServer {

    private int id;
    private int userId;
    private int type;
    private String name;
    private long date;
    private long time;
    private long rideTime;
    private long timeStamp;
    private double distance;
    private List<Location> locations;

    public RecordModelForServer() {
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

    public List<Location> getLocations() {
        return this.locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
        if (!locations.isEmpty())
            this.date = locations.get(0).getTime();
    }

    public long getRideTime() {
        return rideTime;
    }

    public void setRideTime(long rideTime) {
        this.rideTime = rideTime;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}