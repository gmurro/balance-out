package it.uniba.di.sms1920.madminds.balanceout.model;

public class Reminder {
    public static final String REMINDERS = "reminders";
    public static final String DATA = "data";
    public static final String UID_CREDITOR = "uidCreditor";
    public static final String UID_DEBITOR = "uidDebitor";
    public static final String AMOUNT = "amount";

    private String uidCreditor;
    private String uidDebitor;
    private String amount;
    private String data;
    private String idGroup;

    public Reminder(String uidCreditor, String uidDebitor, String amount, String data, String idGroup) {
        this.uidCreditor = uidCreditor;
        this.uidDebitor = uidDebitor;
        this.amount = amount;
        this.data = data;
        this.idGroup = idGroup;
    }
}
