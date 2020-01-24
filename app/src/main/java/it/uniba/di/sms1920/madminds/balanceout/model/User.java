package it.uniba.di.sms1920.madminds.balanceout.model;

public class User {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String imgUser;

    public User(String id, String name, String surname, String email, String imgUser) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.imgUser = imgUser;
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

    public String getImgUser() {
        return imgUser;
    }
}
