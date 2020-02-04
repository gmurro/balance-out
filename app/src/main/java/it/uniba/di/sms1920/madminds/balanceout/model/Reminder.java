package it.uniba.di.sms1920.madminds.balanceout.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Reminder {
    public static final String REMINDERS = "reminders";
    public static final String ID_GROUP = "idGroup";
    public static final String DATA = "data";
    public static final String UID_CREDITOR = "uidCreditor";
    public static final String UID_DEBITOR = "uidDebitor";
    public static final String NAME_CREDITOR = "nameCreditor";
    public static final String NAME_DEBITOR = "nameDebitor";
    public static final String AMOUNT = "amount";

    private String idReminder;
    private String uidCreditor;
    private String uidDebitor;
    private String amount;
    private String data;
    private String idGroup;

    private String nameGroup;
    private String nameCreditor;
    private String nameDebitor;

    public Reminder(String uidCreditor, String uidDebitor, String amount, String data, String idGroup) {
        this.uidCreditor = uidCreditor;
        this.uidDebitor = uidDebitor;
        this.amount = amount;
        this.data = data;
        this.idGroup = idGroup;
    }


    public Reminder() {
    }

    public String getIdReminder() {
        return idReminder;
    }

    public void setIdReminder(String idReminder) {
        this.idReminder = idReminder;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }

    public String getNameCreditor() {
        return nameCreditor;
    }

    public void setNameCreditor(String nameCreditor) {
        this.nameCreditor = nameCreditor;
    }

    public String getNameDebitor() {
        return nameDebitor;
    }

    public void setNameDebitor(String nameDebitor) {
        this.nameDebitor = nameDebitor;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put(ID_GROUP, idGroup);
        result.put(UID_CREDITOR, uidCreditor);
        result.put(UID_DEBITOR, uidDebitor);
        result.put(NAME_CREDITOR, nameCreditor);
        result.put(NAME_DEBITOR, nameDebitor);
        result.put(AMOUNT, amount);
        result.put(DATA, data);

        return result;
    }

    /* funzione che controlla se l'idReminder è presente nell array reminders
       restituisce -1 se non c'e,
       l'indice in cui si trova se c'e
     */
    public static int containsIdReminder(ArrayList<Reminder> reminders, String idReminder) {
        int i=0;
        for (Reminder r: reminders) {
            if(r.getIdReminder().equals(idReminder)) {
                return i;
            }
            i++;
        }
        if(i==reminders.size()) {
            i=-1;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "idReminder='" + idReminder + '\'' +
                ", uidCreditor='" + uidCreditor + '\'' +
                ", uidDebitor='" + uidDebitor + '\'' +
                ", amount='" + amount + '\'' +
                ", data='" + data + '\'' +
                ", idGroup='" + idGroup + '\'' +
                ", nameGroup='" + nameGroup + '\'' +
                ", nameCreditor='" + nameCreditor + '\'' +
                ", nameDebitor='" + nameDebitor + '\'' +
                '}';
    }
}
