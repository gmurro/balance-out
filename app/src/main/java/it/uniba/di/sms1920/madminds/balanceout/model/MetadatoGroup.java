package it.uniba.di.sms1920.madminds.balanceout.model;

import java.util.HashMap;
import java.util.Map;

public class MetadatoGroup {
    /* per ogni account, se esso è in debito col gruppo  statusDebitGroup = -1
     *  se è in pari statusDebitGroup = 0
     *  se deve ricevere un credito statusDebitGroup = 1 */
    private int statusDebitGroup;
    private String amountDebit;
    private boolean activated;

    public MetadatoGroup(int statusDebitGroup, String amountDebit, boolean activated) {
        this.statusDebitGroup = statusDebitGroup;
        this.amountDebit = amountDebit;
        this.activated = activated;
    }

    public MetadatoGroup() {
        super();
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("statusDebitGroup", statusDebitGroup);
        result.put("amountDebit", amountDebit);
        result.put("activated", activated);

        return result;
    }

}
