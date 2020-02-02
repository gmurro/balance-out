package it.uniba.di.sms1920.madminds.balanceout.model;

import java.util.HashMap;
import java.util.Map;

public class Movement {
    public static final String MOVEMENTS = "movements";
    public static final String ID_MOVEMENT = "idMovement";
    public static final String UID_CREDITOR = "uidCreditor";
    public static final String UID_DEBITOR = "uidDebitor";
    public static final String AMOUNT = "amount";
    public static final String ACTIVE = "active";
    public static final String ID_EXPENSE = "idExpense";

    private String idMovement;
    private String uidCreditor;
    private String uidDebitor;
    private String amount;
    private User creditor;
    private User debitor;
    private String idExpense;
    private boolean active;

    public Movement() {
    }

    public Movement(User debitor, User creditor, String amount) {
        this.creditor = creditor;
        this.debitor = debitor;
        this.amount = amount;
    }

    public Movement(String uidCreditor, String uidDebitor, String amount, String idExpense) {
        this.uidCreditor = uidCreditor;
        this.uidDebitor = uidDebitor;
        this.amount = amount;
        this.idExpense = idExpense;
    }

    public String getIdMovement() {
        return idMovement;
    }

    public void setIdMovement(String idMovement) {
        this.idMovement = idMovement;
    }

    public String getUidCreditor() {
        return uidCreditor;
    }

    public void setUidCreditor(String uidCreditor) {
        this.uidCreditor = uidCreditor;
    }

    public String getUidDebitor() {
        return uidDebitor;
    }

    public void setUidDebitor(String uidDebitor) {
        this.uidDebitor = uidDebitor;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCreditor(User creditor) {
        this.creditor = creditor;
    }

    public void setDebitor(User debitor) {
        this.debitor = debitor;
    }

    public String getIdExpense() {
        return idExpense;
    }

    public void setIdExpense(String idExpense) {
        this.idExpense = idExpense;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public User getCreditor() {
        return creditor;
    }

    public User getDebitor() {
        return debitor;
    }

    public String getAmount() {
        return amount;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put(ID_MOVEMENT, idMovement);
        result.put(UID_CREDITOR, uidCreditor);
        result.put(UID_DEBITOR, uidDebitor);
        result.put(AMOUNT, amount);
        result.put(ACTIVE, active);
        result.put(ID_EXPENSE, idExpense);

        return result;
    }
}
