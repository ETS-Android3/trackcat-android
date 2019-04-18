package de.trackcat.CustomElements;

/*
 * Replacement for Android.Location class because of incompatibilities across
 * different api levels
 * needed for example when sending route from Samsung Galaxy S5 to S9
 * */
public class CustomLocation {
    private double altitude;
    private double latitude;
    private double longitude;
    private float speed;
    private long time;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
