package de.mobcom.group3.gotrack.Database.Models;

public class User {
    private String firstName;
    private String lastName;
    private boolean isActive;
    private int id;
    private String eMail;
    private byte[] image;

    public User() {
    }

    public User(int id, String firstName, String lastName, String eMail, byte[] image) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.eMail = eMail;
        this.image = image;
    }

    public User(String firstName, String lastName, String eMail, byte[] image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.eMail = eMail;
        this.image = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMail() {
        return eMail;
    }

    public void setMail(String eMail) {
        this.eMail = eMail;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(int active) {
        isActive = active == 1;
    }
}
