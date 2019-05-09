package de.trackcat.Database.Models;

import java.util.ArrayList;

import de.trackcat.CustomElements.CustomLocation;

/**
 * Model to define an route object
 */
public class Location {
    /*
     + private model attributes
     + modifications via getter and setter
     */
    private int id;
    private double latitude;
    private double longitude;
    private double altitude;
    private long time;
    private float speed;
    private int recordId;

    /**
     * Empty constructor, modifications via getter and setter
     */
    public Location() {
    }

    /**
     * Constructor to save route information from database read.
     *
     * @param id        of type integer
     * @param recordId    of type integer
     * @param latitude      of type double
     * @param longitude      of type double
     * @param altitude  of type double
     * @param time  of type long
     * @param speed of type float
     */
    public Location(int id, int recordId, double latitude,  double longitude, double altitude,long time,
                    float speed) {
        this.id = id;
        this.recordId = recordId;
        this.latitude = latitude;
        this.time = time;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;

    }

    /**
     * Getter for the id.
     *
     * @return value of type integer
     */
    public int getId() {
        return this.id;
    }

    /**
     * Setter for the id.
     *
     * @param id of type integer
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for the record id.
     *
     * @return value of type integer
     */
    public int getRecordId() {
        return this.recordId;
    }

    /**
     * Setter for the user id.
     *
     * @param recordId of type integer
     */
    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    /**
     * Getter for the latitude.
     *
     * @return value of type double
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Setter for the latitude.
     *
     * @param latitude value of type double
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Getter for the longitude.
     *
     * @return value of type double
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Setter for the longitude.
     *
     * @param longitude value of type double
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Getter for the altitude.
     *
     * @return value of type double
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * Setter for the altitude.
     *
     * @param altitude value of type double
     */
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    /**
     * Getter for the time.
     *
     * @return value of type long
     */
    public long getTime() {
        return time;
    }

    /**
     * Setter for the time.
     *
     * @param time of type long
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Getter for the speed.
     *
     * @return value of type float
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Setter for the speed.
     *
     * @param speed of type float
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }
}