package de.trackcat.CustomElements;

public class CustomFriend {

    private long dateOfRegistration;
    private String firstName;
    private String lastName;
    private byte[] image;
    private long totalDistance;

    public long getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(long dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public long getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(long totalDistance) {
        this.totalDistance = totalDistance;
    }
}
