package de.trackcat.Database.Models;

/**
 * Model to define an user object
 */
public class User {
    /*
     + private model attributes
     + modifications via getter and setter
     */
    private String firstName;
    private String lastName;
    private boolean isActive;
    private boolean darkThemeActive;
    private boolean hintsActive;
    private int id;
    private String eMail;
    private String password;
    private float weight;
    private float size;
    private int gender;
    private long dateOfBirth;
    private long dateOfRegistration;
    private long lastLogin;
    private long timeStamp;
    private int idUsers;
    private boolean isSynchronized;
    private byte[] image;

    /**
     * Empty constructor, modifications via getter and setter
     */
    public User() {
    }

    /**
     * Constructor to save user information from database read.
     *
     * @param id        of type integer
     * @param firstName of type string
     * @param lastName  of type string
     * @param eMail     of type string
     * @param password     of type string
     * @param weight    of type float
     * @param size    of type float
     * @param gender    of type integer
     * @param dateOfBirth    of type long
     * @param dateOfRegistration    of type long
     * @param lastLogin    of type long
     * @param timeStamp    of type long
     * @param idUsers    of type int
     * @param isSynchronized     of type integer
     */
    public User(int id, String firstName, String lastName, int active, int hintsActive,
                int themeActive, String eMail, String password ,float weight, float size, int gender, long dateOfBirth,
                long dateOfRegistration, long lastLogin, long timeStamp, int idUsers, byte[] image, int isSynchronized) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.eMail = eMail;
        this.password = password;
        this.weight = weight;
        this.size = size;
        this.gender=gender;
        this.dateOfBirth = dateOfBirth;
        this.dateOfRegistration = dateOfRegistration;
        this.lastLogin = lastLogin;
        this.timeStamp = timeStamp;
        this.idUsers = idUsers;
        this.image = image;
        this.setIsSynchronizedDB(isSynchronized);
        this.setActiveDB(active);
        this.setDarkThemeActiveDB(themeActive);
        this.setHintsActiveDB(hintsActive);
    }

    /**
     * Constructor to create a user to write to the database.
     *
     * @param firstName of type string
     * @param lastName  of type string
     * @param eMail     of type string
     * @param image     of type byte array
     */
    public User(String firstName, String lastName, String eMail, byte[] image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.eMail = eMail;
        this.image = image;
    }

    /**
     * Getter for the first name.
     *
     * @return value of type string
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter for the fist name.
     *
     * @param firstName of type string
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter for the last name.
     *
     * @return value of type string
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter for the last name
     *
     * @param lastName of type string
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Getter for the id..
     *
     * @return value of type integer
     */
    public int getId() {
        return id;
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
     * Getter for the e-mail address.
     *
     * @return value of type string
     */
    public String getMail() {
        return eMail;
    }

    /**
     * Setter for the e-mail address.
     *
     * @param eMail of type string
     */
    public void setMail(String eMail) {
        this.eMail = eMail;
    }

    /**
     * Getter for the password.
     *
     * @return value of type string
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for the password.
     *
     * @param password of type string
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for the weight.
     *
     * @return value of type float
     */
    public float getWeight() {
        return weight;
    }

    /**
     * Setter for the weight.
     *
     * @param weight of type float
     */
    public void setWeight(float weight) {
        this.weight = weight;
    }

    /**
     * Getter for the size.
     *
     * @return value of type float
     */
    public float getSize() {
        return size;
    }

    /**
     * Setter for the size.
     *
     * @param size of type float
     */
    public void setSize(float size) {
        this.size = size;
    }

    /**
     * Getter for the gender.
     *
     * @return value of type integer
     */
    public int getGender() {
        return gender;
    }


    /**
     * Setter for the gender.
     *
     * @param gender of type integer
     */
    public void setGender(int gender) {
        this.gender = gender;
    }


    /**
     * Getter for isSynchronized.
     *
     * @return value of type boolean
     */
    public boolean getSynchronized() {
        return isSynchronized;
    }

    /**
     * Getter to define isSynchronized.
     *
     * @return value of type integer
     *
     * <p>
     * Integer value is necessary due to SQLite Database constraint. SQLite does not implement
     * boolean values natively as true or false but only as integer.
     * </p>
     * <p>
     * Returns "1" if the user is currently the active one or "0" if it isn't.
     * </p>
     */
    public int isSynchronizedDB() {
        if (isSynchronized)
            return 1;
        else
            return 0;
    }

    /**
     * Setter for the gender.
     *
     * @param isSynchronized of type boolean
     */
    public void isSynchronised(boolean isSynchronized) {
        this.isSynchronized = isSynchronized;
    }

    /**
     * Setter to define gender.
     *
     * @param isSynchronized of type integer
     *
     *               <p>
     *               Integer value is necessary due to SQLite Database constraint. SQLite does not implement
     *               boolean values natively as true or false but only as integer.
     *               </p>
     *               <p>
     *               Hand over "1" to define that the user is currently the active one or "0"
     *               to define that it isn't.
     *               </p>
     */
    public void setIsSynchronizedDB(int isSynchronized) {
        this.isSynchronized = isSynchronized == 1;
    }

    /**
     * Getter for the dateOfBirth.
     *
     * @return value of type long
     */
    public long getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Setter for the dateOfBirth.
     *
     * @param dateOfBirth of type long
     */
    public void setDateOfBirth(long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Getter for the dateOfRegistration.
     *
     * @return value of type long
     */
    public long getDateOfRegistration() {
        return dateOfRegistration;
    }

    /**
     * Setter for the dateOfRegistration.
     *
     * @param dateOfRegistration of type long
     */
    public void setDateOfRegistration(long dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    /**
     * Getter for the lastLogin.
     *
     * @return value of type long
     */
    public long getLastLogin() {
        return lastLogin;
    }

    /**
     * Setter for the lastLogin.
     *
     * @param lastLogin of type long
     */
    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * Getter for the timeStamp.
     *
     * @return value of type long
     */
    public float getTimeStamp() {
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
     * Getter for the idUsers.
     *
     * @return value of type int
     */
    public float getIdUsers() {
        return idUsers;
    }

    /**
     * Setter for the idUsers.
     *
     * @param idUsers of type long
     */
    public void setIdUsers(int idUsers) {
        this.idUsers = idUsers;
    }

    /**
     * Getter for the user profile image.
     *
     * @return value of type byte array
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Setter for the user profile image.
     *
     * @param image of type byte array
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     * Getter to define if user is currently active user or not.
     *
     * @return native boolean for logic purposes
     *
     * <p>
     * Returns true if the user is currently the active one or false if it isn't.
     * </p>
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Getter to define if user is currently active user or not for database storage purposes.
     *
     * @return value of type integer
     *
     * <p>
     * Integer value is necessary due to SQLite Database constraint. SQLite does not implement
     * boolean values natively as true or false but only as integer.
     * </p>
     * <p>
     * Returns "1" if the user is currently the active one or "0" if it isn't.
     * </p>
     */
    public int isActiveDB() {
        if (isActive)
            return 1;
        else
            return 0;
    }

    /**
     * Setter to decide if user is currently active user or not for UI purposes.
     *
     * @param active boolean value
     *
     *               <p>
     *               Hand over true to define that the user is currently the active one or false
     *               to define that it isn't.
     *               </p>
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Setter to define if user is currently active user or not for database storage purposes.
     *
     * @param active of type integer
     *
     *               <p>
     *               Integer value is necessary due to SQLite Database constraint. SQLite does not implement
     *               boolean values natively as true or false but only as integer.
     *               </p>
     *               <p>
     *               Hand over "1" to define that the user is currently the active one or "0"
     *               to define that it isn't.
     *               </p>
     */
    public void setActiveDB(int active) {
        this.isActive = active == 1;
    }

    /**
     * Getter to define if user has currently darkTheme activated or not.
     *
     * @return native boolean for logic purposes
     *
     * <p>
     * Returns true if the darkTheme activated or false if it isn't.
     * </p>
     */
    public boolean isDarkThemeActive() {
        return darkThemeActive;
    }

    /**
     * Getter to define if user has currently darkTheme activated or not for database storage
     * purposes.
     *
     * @return value of type integer
     *
     * <p>
     * Integer value is necessary due to SQLite Database constraint. SQLite does not implement
     * boolean values natively as true or false but only as integer.
     * </p>
     * <p>
     * Returns "1" if darTheme is activated or "0" if it isn't.
     * </p>
     */
    public int isDarkThemeActiveDB() {
        if (darkThemeActive) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Setter to define if user has currently darkTheme activated or not for UI purposes.
     *
     * @param isDarkTheme boolean value
     *
     *                    <p>
     *                    Hand over true to define that the user has currently darkTheme activated or false
     *                    to define that it isn't.
     *                    </p>
     */
    public void setDarkThemeActive(boolean isDarkTheme) {
        this.darkThemeActive = isDarkTheme;
    }

    /**
     * Setter to define if user has currently darkTheme activated or not for database storage
     * purposes.
     *
     * @param theme of type integer
     *
     *              <p>
     *              Integer value is necessary due to SQLite Database constraint. SQLite does not implement
     *              boolean values natively as true or false but only as integer.
     *              </p>
     *              <p>
     *              Hand over "1" to define that the user has currently the darkTheme activated or "0"
     *              to define that it isn't.
     *              </p>
     */
    public void setDarkThemeActiveDB(int theme) {
        this.darkThemeActive = theme == 1;
    }

    /**
     * Getter to define if user has currently hints activated or not.
     *
     * @return native boolean for logic purposes
     *
     * <p>
     * Returns true if the user hints activated or false if they aren't.
     * </p>
     */
    public boolean isHintsActive() {
        return hintsActive;
    }

    /**
     * Getter to define if user has currently hints activated or not for database storage
     * purposes.
     *
     * @return value of type integer
     *
     * <p>
     * Integer value is necessary due to SQLite Database constraint. SQLite does not implement
     * boolean values natively as true or false but only as integer.
     * </p>
     * <p>
     * Returns "1" if the user hints activated or "0" if they aren't.
     * </p>
     */
    public int isHintsActiveDB() {
        if (hintsActive) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Setter to define if user has currently hints activated or not for UI purposes.
     *
     * @param hintsActive boolean value
     *
     *                    <p>
     *                    Hand over true to define that the user hints are activated or false
     *                    to define that they aren't.
     *                    </p>
     */
    public void setHintsActive(boolean hintsActive) {
        this.hintsActive = hintsActive;
    }

    /**
     * Setter to define if user has currently hints activated or not for database storage
     * purposes.
     *
     * @param hint of type integer
     *
     *             <p>
     *             Integer value is necessary due to SQLite Database constraint. SQLite does not implement
     *             boolean values natively as true or false but only as integer.
     *             </p>
     *             <p>
     *             Hand over "1" to define that user hints activated or "0"
     *             to define that they aren't.
     *             </p>
     */
    public void setHintsActiveDB(int hint) {
        this.hintsActive = hint == 1;
    }
}