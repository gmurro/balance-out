package it.uniba.di.sms1920.madminds.balanceout.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MetadateGroup {

    /* per ogni account, se esso è in debito col gruppo  statusDebitGroup = -1
     *  se è in pari statusDebitGroup = 0
     *  se deve ricevere un credito statusDebitGroup = 1 */
    private int statusDebitGroup;
    private String amountDebit;

    public MetadateGroup(int statusDebitGroup, String amountDebit) {
        this.statusDebitGroup = statusDebitGroup;
        this.amountDebit = amountDebit;
    }

    /*
    public MetadateGroup(int statusDebitGroup, String amountDebit, String idGroup) {
        this.statusDebitGroup = statusDebitGroup;
        this.amountDebit = amountDebit;
        this.idGroup = idGroup;
    }*/

    public MetadateGroup() {
        super();
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("statusDebitGroup", statusDebitGroup);
        result.put("amountDebit", amountDebit);
        //result.put("idGroup", idGroup);

        return result;
    }

    public int getStatusDebitGroup() {
        return statusDebitGroup;
    }

    public void setStatusDebitGroup(int statusDebitGroup) {
        this.statusDebitGroup = statusDebitGroup;
    }

    public String getAmountDebit() {
        return amountDebit;
    }

    public void setAmountDebit(String amountDebit) {
        this.amountDebit = amountDebit;
    }
}
