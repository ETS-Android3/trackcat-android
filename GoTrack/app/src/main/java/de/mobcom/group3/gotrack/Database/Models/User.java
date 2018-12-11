package de.mobcom.group3.gotrack.Database.Models;

public class User {
    /*
     + private model attributes
     + modifications via getter and setter
     */
    private String firstName;
    private String lastName;
    private boolean isActive;
    private int id;
    private String eMail;
    private byte[] image;

    /**
     * Empty constructor
     */
    public User() { }

    /**
     * Constructor to save user information from database read.
     * @param id of type integer
     * @param firstName of type string
     * @param lastName of type string
     * @param eMail of type string
     * @param image of type byte array
     */
    public User(int id, String firstName, String lastName, int active, String eMail, byte[] image) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.eMail = eMail;
        this.image = image;
        this.setActive(active);
    }

    /**
     * Constructor to create a user to write to the database
     * @param firstName of type string
     * @param lastName of type string
     * @param eMail of type string
     * @param image of type byte array
     */
    public User(String firstName, String lastName, String eMail, byte[] image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.eMail = eMail;
        this.image = image;
    }

    /**
     * Getter for the first name
     * @return value of type string
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter for the fist name
     * @param firstName of type string
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter for the last name
     * @return value of type string
     */
    public String getLastName() { return lastName; }

    /**
     * Setter for the last name
     * @param lastName of type string
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Getter for the id
     * @return value of type integer
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for the id
     * @param id of type integer
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for the e-mail address
     * @return value of type string
     */
    public String getMail() {
        return eMail;
    }

    /**
     * Setter for the e-mail address
     * @param eMail of type string
     */
    public void setMail(String eMail) {
        this.eMail = eMail;
    }

    /**
     * Getter for the user profile image
     * @return value of type byte array
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Setter for the user profile image
     * @param image of type byte array
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     * Getter to decide if user is currently active user or not
     * @return native boolean for logic purposes
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Getter to decide if user is currently active user or not for database storage purposes.
     * Integer value is necessary due to SQLite Database constraint.
     * SQLite does not implement boolean values natively as true or false but only as integer.
     * @return value of type integer
     */
    public int isActiveForDB() {
        if(isActive)
            return 1;
        else
            return 0;
    }

    /**
     * Setter to decide if user is currently active user or not
     * Integer value is necessary due to SQLite Database constraint.
     * SQLite does not implement boolean values natively as true or false but only as integer.
     * @param active int value '1' for true and '0' for false
     */
    public void setActive(int active) {
        isActive = active == 1;
    }
}
