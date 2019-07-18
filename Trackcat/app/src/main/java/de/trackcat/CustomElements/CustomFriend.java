package de.trackcat.CustomElements;

public class CustomFriend {

    private long dateOfRegistration;
    private String firstName;
    private String lastName;
    private byte[] image;
    private int state;

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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
