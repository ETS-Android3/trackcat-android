package de.trackcat.Database.Models;

import java.util.ArrayList;

import de.trackcat.CustomElements.CustomLocation;

/**
 * Model to define an route object
 */
public class Route {
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
    private long timeStamp;
    private boolean isImported;
    private double distance;
    private ArrayList<CustomLocation> locations;

    /**
     * Empty constructor, modifications via getter and setter
     */
    public Route() {
    }

    /**
     * Constructor to save route information from database read.
     *
     * @param id        of type integer
     * @param userId    of type integer
     * @param name      of type string
     * @param time      of type long
     * @param rideTime  of type long
     * @param distance  of type double
     */
    public Route(int id, int userId, String name, long time, long rideTime, double distance,
                 int type, long date, long timeStamp, int isImported) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.time = time;
        this.rideTime = rideTime;
        this.distance = distance;
        this.locations = locations;
        this.timeStamp= timeStamp;
        this.date = date;
        this.type = type;
        this.setImportedDB(isImported);
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
     * Getter for the user id.
     *
     * @return value of type integer
     */
    public int getUserId() {
        return this.userId;
    }

    /**
     * Setter for the user id.
     *
     * @param userId of type integer
     */
    public void setUserID(int userId) {
        this.userId = userId;
    }

    /**
     * Getter for the name of the route.
     *
     * @return value of type string
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name of the route.
     *
     * @param name value of type string
     */
    public void setName(String name) {
        this.name = name;
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
     * Getter for the distance.
     *
     * @return value of type double
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Setter for the distance.
     *
     * @param distance of type double
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

   /**
     * Getter for the locations.
     *
     * @return value of type array list
     */
  /*  public ArrayList<CustomLocation> getLocations() {
        return this.locations;
    }*/

    /**
     * Setter for the locations.
     *
     * @param locations of type array list
     */
   /*  public void setLocations(ArrayList<CustomLocation> locations) {
        this.locations = locations;
        if (!locations.isEmpty())
            this.date = locations.get(0).getTime();
    }*/

    /**
     * Setter to add a single location to the other route locations.
     *
     * @param location of type location
     */
  /*  public void addLocation(CustomLocation location) {
        if (this.locations == null) {
            this.locations = new ArrayList<>();
        }
        this.locations.add(location);
    }*/
    /**
     * Getter for the ride time.
     *
     * @return value of type long
     */
    public long getRideTime() {
        return rideTime;
    }

    /**
     * Setter for the ride time.
     *
     * @param rideTime of type long
     */
    public void setRideTime(long rideTime) {
        this.rideTime = rideTime;
    }

    /**
     * Getter for the date.
     *
     * @return value of type long (millis since Jan. 1, 1970)
     */
    public long getDate() {
        return date;
    }

    /**
     * Setter for the date.
     *
     * @param date of type long
     */
    public void setDate(long date) {
        this.date = date;
    }

    /**
     * Getter for the type.
     *
     * @return value of type integer
     */
    public int getType() {
        return type;
    }

    /**
     * Setter for the type.
     *
     * @param type of type integer
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Getter for the timeStamp.
     *
     * @return value of type long
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Setter for the timeStamp.
     *
     * @param timeStamp of type long
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Getter for import flag.
     *
     * @return value of type boolean
     *
     * <p>
     * Returns true if the route is imported or false if it isn't.
     * </p>
     */
    public boolean isImported() {
        return isImported;
    }

    /**
     * Setter for import flag.
     *
     * @param isImported of type integer
     *
     *                   <p>
     *                   Hand over true to define that the route is imported or false to define that it isn't.
     *                   </p>
     */
    public void setImported(boolean isImported) {
        this.isImported = isImported;
    }

    /**
     * Getter to define if route is imported or not for database storage purposes.
     *
     * @return value of type integer
     *
     * <p>
     * Integer value is necessary due to SQLite Database constraint.
     * SQLite does not implement boolean values natively as true or false but only as integer.
     * </p>
     * <p>
     * Returns "1" if the route is imported or "0" if it isn't.
     * </p>
     */
    public int isImportedDB() {
        if (isImported) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Getter to define if route is imported or not for database storage purposes.
     *
     * @param isImported value of type integer
     *
     *                   <p>
     *                   Integer value is necessary due to SQLite Database constraint.
     *                   SQLite does not implement boolean values natively as true or false but only as integer.
     *                   </p>
     *                   <p>
     *                   Hand over "1" to define that the route is imported or "0" to define that it isn't.
     *                   </p>
     */
    public void setImportedDB(int isImported) {
        this.isImported = isImported == 1;
    }
}