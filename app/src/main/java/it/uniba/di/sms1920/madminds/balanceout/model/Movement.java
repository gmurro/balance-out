package it.uniba.di.sms1920.madminds.balanceout.model;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    public Movement(String uidCreditor, String uidDebitor, String amount, String idExpense, boolean active) {
        this.uidCreditor = uidCreditor;
        this.uidDebitor = uidDebitor;
        this.amount = amount;
        this.idExpense = idExpense;
        this.active=active;
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

    @Override
    public String toString() {
        return "Movement{" +
                "idMovement='" + idMovement + '\'' +
                ", uidCreditor='" + uidCreditor + '\'' +
                ", uidDebitor='" + uidDebitor + '\'' +
                ", amount='" + amount + '\'' +
                ", creditor=" + creditor +
                ", debitor=" + debitor +
                ", idExpense='" + idExpense + '\'' +
                ", active=" + active +
                '}';
    }

    /* funzione che controlla se l'idMovement è presente nell array movements
       restituisce -1 se non c'e,
       l'indice in cui si trova se c'e
     */
    public static int containsIdMovement(ArrayList<Movement> movements, String idMovement) {
        int i=0;
        for (Movement g: movements) {
            if(g.getIdMovement().equals(idMovement)) {
                return i;
            }
            i++;
        }
        if(i==movements.size()) {
            i=-1;
        }
        return -1;
    }


    public static boolean containsAlreadyMovement(ArrayList<Movement> movements, Movement newMovement) {
        for (Movement m : movements) {

            BigDecimal alreadyPresentAmount = new BigDecimal(m.getAmount());
            BigDecimal newAmount = new BigDecimal(newMovement.getAmount());

            //se gli attori del movimento sono invertiti
            if (m.getUidCreditor().equals(newMovement.getUidDebitor()) && m.getUidDebitor().equals(newMovement.getUidCreditor())) {

                BigDecimal difference;

                //se alreadyPresentAmount > newAmount
                if (alreadyPresentAmount.compareTo(newAmount) > 0) {
                    difference = alreadyPresentAmount.subtract(newAmount);
                    m.setAmount(String.format("%.2f", difference).replace(",", "."));

                } else
                    //se alreadyPresentAmount < newAmount
                    if (alreadyPresentAmount.compareTo(newAmount) < 0) {
                        difference = newAmount.subtract(alreadyPresentAmount);
                        m.setUidCreditor(newMovement.getUidCreditor());
                        m.setUidDebitor(newMovement.getUidDebitor());
                        m.setAmount(String.format("%.2f", difference).replace(",", "."));

                    } else {
                        //se alreadyPresentAmount == newAmount
                        movements.remove(m);
                    }

                    return true;
            }

            //se gli attori del movimento sono gli stessi
            else if (m.getUidCreditor().equals(newMovement.getUidCreditor()) && m.getUidDebitor().equals(newMovement.getUidDebitor())) {
                BigDecimal sum = newAmount.add(alreadyPresentAmount);
                m.setAmount(String.format("%.2f", sum).replace(",", "."));

                return true;
            }
        }

        return false;
    }
}
