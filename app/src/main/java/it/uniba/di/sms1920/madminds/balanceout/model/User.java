package it.uniba.di.sms1920.madminds.balanceout.model;

public class User {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String picture;

    public User(String id, String name, String surname, String email, String picture) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.picture = picture;
    }

    public User() {
        super();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture() {
        return picture;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
