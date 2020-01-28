package it.uniba.di.sms1920.madminds.balanceout.model;

import java.util.ArrayList;
import java.util.Date;

public class Expense {

    public static final int EQUAL_DIVISION=0;
    public static final int PERSON_DIVISION=1;

    private String id;
    private ArrayList<Payer> payersExpense;
    private Date data;
    private int typeDivision;
    private String description;
    private String receipt;
    private ArrayList<Payer> payersDebt;

    public Expense(String id, ArrayList<Payer> payersExpense, Date data, int typeDivision, String description, String receipt, ArrayList<Payer> payersDebt) {
        this.id = id;
        this.payersExpense = payersExpense;
        this.data = data;
        this.typeDivision = typeDivision;
        this.description = description;
        this.receipt = receipt;
        this.payersDebt = payersDebt;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Payer> getPayersExpense() {
        return payersExpense;
    }

    public Date getData() {
        return data;
    }

    public int getTypeDivision() {
        return typeDivision;
    }

    public String getDescription() {
        return description;
    }

    public String getReceipt() {
        return receipt;
    }

    public ArrayList<Payer> getPayersDebt() {
        return payersDebt;
    }
}
