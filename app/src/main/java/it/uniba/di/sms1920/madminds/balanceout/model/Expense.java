package it.uniba.di.sms1920.madminds.balanceout.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Expense {

    public static final int EQUAL_DIVISION=0;
    public static final int PERSON_DIVISION=1;
    public static final int NEVER = 0;
    public static final int DAYLY = 1;
    public static final int WEEKLY = 2;
    public static final int MONTHLY = 3;
    public static final String EXPENSES = "expenses";
    public static final String ID = "id";
    //public static final String

    private String id;
    private ArrayList<Payer> payersExpense;
    private String data;
    private int typeDivision;
    private String description;
    private String receipt;
    private ArrayList<Payer> payersDebt;
    private String idGroup;
    private int repetition;

    public Expense(String id, ArrayList<Payer> payersExpense, String data, int typeDivision, String description, String receipt, ArrayList<Payer> payersDebt, String idGroup, int repetition) {
        this.id = id;
        this.payersExpense = payersExpense;
        this.data = data;
        this.typeDivision = typeDivision;
        this.description = description;
        this.receipt = receipt;
        this.payersDebt = payersDebt;
        this.idGroup = idGroup;
        this.repetition = repetition;
    }

    public Expense(){
        super();
        payersDebt = new ArrayList<>();
        payersExpense = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public ArrayList<Payer> getPayersExpense() {
        return payersExpense;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public String getData() {
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

    public int getRepetition() {
        return repetition;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

/*
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        //new SimpleDateFormat("dd/MM/yyyy").format(creationDataGroup)

        result.put(ID_GROUP, idGroup);
        result.put(NAME_GROUP, nameGroup);
        result.put(CREATION_DATA_GROUP, creationDataGroup);
        result.put(IMG_GROUP, imgGroup);
        result.put(MEMBERS, members);
        result.put(UID_MEMEBRS, uidMembers);
        result.put(ID_ADMINISTRATOR, idAdministrator);
        result.put(STATUS_DEBIT_GROUP, statusDebitGroup);
        result.put(AMOUNT_DEBIT, amountDebit);
        result.put(ACTIVE, active);
        result.put(SEMPLIFICATION_DEBTS, semplificationDebts);
        result.put(PUBLIC_MOVEMENTS, publicMovements);

        return result;
    }*/
}
