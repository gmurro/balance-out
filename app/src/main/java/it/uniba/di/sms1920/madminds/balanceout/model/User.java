package it.uniba.di.sms1920.madminds.balanceout.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private String uid;
    private String name;
    private String surname;
    private String email;
    private String picture;

    public User(String uid, String name, String surname, String email, String picture) {
        this.uid = uid;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.picture = picture;
    }

    public User() {
        super();
    }

    public String getUid() {
        return uid;
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

    public void setUid(String uid) {
        this.uid = uid;
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
