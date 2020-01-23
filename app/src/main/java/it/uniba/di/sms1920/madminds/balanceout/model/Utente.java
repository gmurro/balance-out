package it.uniba.di.sms1920.madminds.balanceout.model;

public class Utente {
    private int id;
    private String nome;
    private String cognome;
    private String email;
    private String imgUtente;

    public Utente(int id, String nome, String cognome, String email, String imgUtente) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.imgUtente = imgUtente;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getImgUtente() {
        return imgUtente;
    }
}
