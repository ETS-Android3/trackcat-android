package de.mobcom.group3.gotrack.Database.Models;

public class User {
    private String name;
    private int id;
    private String eMail;
    private String theme;
    private byte[] image;

    public User() {
    }

    public User(int id, String name, String eMail, String theme, byte[] image) {
        this.id = id;
        this.name = name;
        this.eMail = eMail;
        this.theme = theme;
        this.image = image;
    }

    public User(String name, String eMail, String theme, byte[] image) {
        this.name = name;
        this.eMail = eMail;
        this.theme = theme;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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


    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
