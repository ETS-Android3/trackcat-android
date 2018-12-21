package de.mobcom.group3.gotrack.Database.Models;

import android.location.Location;
import android.net.http.SslCertificate;

import java.util.ArrayList;

public class Route {
    /*
     + private model attributes
     + modifications via getter and setter
     */
    private int id;
    private int userId;
    private String name;
    private long date;
    private long time;
    private long rideTime;
    private double distance;
    private ArrayList<Location> locations;

    /**
     * empty constructor
     */
    public Route() {}

    /**
     * Constructor to save route information from database read.
     * @param id of type integer
     * @param userId of type integer
     * @param name of type string
     * @param time of type long
     * @param rideTime of type long
     * @param distance of type double
     * @param locations of type array list
     */
    public Route(int id, int userId, String name, long time, long rideTime, double distance,
                 ArrayList<Location> locations) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.time = time;
        this.rideTime = rideTime;
        this.distance = distance;
        this.locations =locations;
        this.date = System.currentTimeMillis();
    }

    /**
     * Constructor to create a route to write to the database
     * @param userId of type integer
     * @param name of type string
     * @param time of type long
     * @param rideTime of type long
     * @param distance of type double
     * @param locations of type array list
     */
    public Route(int userId, String name, long time, long rideTime, double distance, ArrayList<Location> locations) {
        this.userId = userId;
        this.name = name;
        this.time = time;
        this.rideTime = rideTime;
        this.distance = distance;
        this.locations = locations;
        this.date = System.currentTimeMillis();
    }

    /**
     * Getter for the id
     * @return value of type integer
     */
    public int getId() {
        return this.id;
    }

    /**
     * Setter for the id
     * @param id of type integer
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for the user id
     * @return value of type integer
     */
    public int getUserId() {
        return this.userId;
    }

    /**
     * Setter for the user id
     * @param userId of type integer
     */
    public void setUserID(int userId) {
        this.userId = userId;
    }

    /**
     * Getter for the name of the route
     * @return value of type string
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name of the route
     * @param name value of type string
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the time
     * @return value of type long
     */
    public long getTime() {
        return time;
    }

    /**
     * Setter for the time
     * @param time of type long
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Getter for the distance
     * @return value of type double
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Setter for the distance
     * @param distance of type double
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Getter for the locations
     * @return value of type array list
     */
    public ArrayList<Location> getLocations() {
        return this.locations;
    }

    /**
     * Setter for the locations
     * @param locations of type array list
     */
    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
        if(!locations.isEmpty())
            this.date = locations.get(0).getTime();
    }

    /**
     * Setter to add a single location to the other route locations
     * @param location of type location
     */
    public void addLocation(Location location) {
        if(this.locations == null){
            this.locations = new ArrayList<>();
        }
        this.locations.add(location);
    }

    /**
     * Getter for the ride time
     * @return value of type long
     */
    public long getRideTime() {
        return rideTime;
    }

    /**
     * Setter for the ride time
     * @param rideTime of type long
     */
    public void setRideTime(long rideTime) {
        this.rideTime = rideTime;
    }

    /**
     * Getter for the date
     * @return value of type long (millis since Jan. 1, 1970)
     */
    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
