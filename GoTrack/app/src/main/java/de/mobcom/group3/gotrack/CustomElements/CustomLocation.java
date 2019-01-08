package de.mobcom.group3.gotrack.CustomElements;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * Replacement for android Location Class because of small differences in
 * different api levels
 * needed for examlpe when sending Route from Samsung Galaxy S5 to S9
 * */
public class CustomLocation implements Parcelable {
    private double altitude;
    private double latitude;
    private double longitude;
    private float speed;
    private long time;

    public CustomLocation(Parcel in) {
        altitude = in.readDouble();
        latitude = in.readDouble();
        longitude = in.readDouble();
        speed = in.readFloat();
        time = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(altitude);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeFloat(speed);
        dest.writeLong(time);
    }

    public CustomLocation() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CustomLocation> CREATOR = new Creator<CustomLocation>() {
        @Override
        public CustomLocation createFromParcel(Parcel in) {
            return new CustomLocation(in);
        }

        @Override
        public CustomLocation[] newArray(int size) {
            return new CustomLocation[size];
        }
    };

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
