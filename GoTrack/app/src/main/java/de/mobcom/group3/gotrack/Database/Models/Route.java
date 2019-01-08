package de.mobcom.group3.gotrack.Database.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import de.mobcom.group3.gotrack.CustomElements.CustomLocation;

public class Route implements Parcelable {
    /*
     + private model attributes
     + modifications via getter and setter
     */
    private int id;
    private int userId;
    private int type;
    private String name;
    private long date;
    private long time;
    private long rideTime;
    private boolean isImported;
    private double distance;
    private ArrayList<CustomLocation> locations;

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
                 int type, long date, int isImported, ArrayList<CustomLocation> locations) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.time = time;
        this.rideTime = rideTime;
        this.distance = distance;
        this.locations =locations;
        this.date = date;
        this.type = type;
        this.setImportedDB(isImported);
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
    public ArrayList<CustomLocation> getLocations() {
        return this.locations;
    }

    /**
     * Setter for the locations
     * @param locations of type array list
     */
    public void setLocations(ArrayList<CustomLocation> locations) {
        this.locations = locations;
        if(!locations.isEmpty())
            this.date = locations.get(0).getTime();
    }

    /**
     * Setter to add a single location to the other route locations
     * @param location of type location
     */
    public void addLocation(CustomLocation location) {
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

    /**
     * Setter for the date
     * @param date of type long
     */
    public void setDate(long date) {
        this.date = date;
    }

    /**
     * Getter for the type
     * @return value of type integer
     */
    public int getType() {
        return type;
    }

    /**
     * Setter for the type
     * @param type of type integer
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Getter for import flag
     * @return value of type boolean
     */
    public boolean isImported() {
        return isImported;
    }

    /**
     * Setter for import flag
     * @param isImported of type integer
     */
    public void setImported(boolean isImported) {
        this.isImported = isImported;
    }

    /**
     * Getter to decide if route is imported or not for database storage purposes.
     * Integer value is necessary due to SQLite Database constraint.
     * SQLite does not implement boolean values natively as true or false but only as integer.
     * @return value of type integer
     */
    public int isImportedDB() {
        if (isImported) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Setter to decide if route is imported or not for database storage purposes.
     * Integer value is necessary due to SQLite Database constraint.
     * SQLite does not implement boolean values natively as true or false but only as integer.
     * @param isImported of type integer
     */
    public void setImportedDB(int isImported) {
        this.isImported = isImported == 1;
    }

    /*
     + Parcelable Stuff
     */
    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        System.out.println("Write to parcel has begun");
        dest.writeInt(this.id);
        dest.writeInt(this.userId);
        dest.writeInt(this.type);
        dest.writeString(this.name);
        dest.writeLong(this.date);
        dest.writeLong(this.time);
        dest.writeLong(this.rideTime);
        dest.writeInt(this.isImportedDB());
        dest.writeDouble(this.distance);
        dest.writeTypedList(this.locations);
        locations.get(0).writeToParcel(dest, 0);
    }

    public Route(Parcel source) {
        this.id = source.readInt();
        this.userId = source.readInt();
        this.type = source.readInt();
        this.name = source.readString();
        this.date = source.readLong();
        this.time = source.readLong();
        this.rideTime = source.readLong();
        this.setImportedDB(source.readInt());
        this.distance = source.readDouble();
        this.locations = source.createTypedArrayList(CustomLocation.CREATOR);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Route createFromParcel(Parcel source) {
            return new Route(source);
        }

        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}