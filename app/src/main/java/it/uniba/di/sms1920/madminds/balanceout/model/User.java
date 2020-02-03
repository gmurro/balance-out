package it.uniba.di.sms1920.madminds.balanceout.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class User implements Serializable {
    public static final String USERS = "users";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String EMAIL = "email";
    public static final String PICTURE = "picture";
    public static final String MY_GROUPS = "mygroups";
    public static final String UID = "uid";

    private String uid;
    private String name;
    private String surname;
    private String email;
    private String picture;
    private ArrayList<MetadateGroup> metadateGroups;

    public User(String uid, String name, String surname, String email, String picture, ArrayList<MetadateGroup> metadateGroups) {
        this.uid = uid;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.picture = picture;
        this.metadateGroups = metadateGroups;
    }

    public User() {
        super();
        metadateGroups = new ArrayList<>();
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

    public boolean addMetadateGroups(MetadateGroup metadateGroup) {
        return this.metadateGroups.add(metadateGroup);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uid, user.uid) &&
                Objects.equals(name, user.name) &&
                Objects.equals(surname, user.surname) &&
                Objects.equals(email, user.email) &&
                Objects.equals(picture, user.picture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, name, surname, email, picture);
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", picture='" + picture + '\'' +
                ", metadateGroups=" + metadateGroups +
                '}';
    }
}
