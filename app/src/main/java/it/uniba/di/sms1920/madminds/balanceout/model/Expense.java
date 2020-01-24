package it.uniba.di.sms1920.madminds.balanceout.model;

import java.util.ArrayList;
import java.util.Date;

public class Expense {

    public static final int EQUAL_DIVISION=0;
    public static final int PERSON_DIVISION=0;

    private String id;
    private ArrayList<User> creditors;
    private Date data;
    private double amount;
    private int typeDivision;
    private String descrizione;
    private ArrayList<String> receipts;
    private ArrayList<Payer> payers;

    public Expense(String id, ArrayList<User> creditors, Date data, double amount, int typeDivision, String descrizione, ArrayList<String> receipts, ArrayList<Payer> payers) {
        this.id = id;
        this.creditors = creditors;
        this.data = data;
        this.amount = amount;
        this.typeDivision = typeDivision;
        this.descrizione = descrizione;
        this.receipts = receipts;
        this.payers = payers;
    }

    public String getId() {
        return id;
    }

    public ArrayList<User> getCreditors() {
        return creditors;
    }

    public Date getData() {
        return data;
    }

    public double getAmount() {
        return amount;
    }

    public int getTypeDivision() {
        return typeDivision;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public ArrayList<String> getReceipts() {
        return receipts;
    }

    public ArrayList<Payer> getPayers() {
        return payers;
    }
}
