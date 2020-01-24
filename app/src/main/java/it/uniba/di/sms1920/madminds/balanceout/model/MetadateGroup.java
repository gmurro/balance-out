package it.uniba.di.sms1920.madminds.balanceout.model;

import java.util.HashMap;
import java.util.Map;

public class MetadateGroup {

    private String nameGroup;
    /* per ogni account, se esso è in debito col gruppo  statusDebitGroup = -1
     *  se è in pari statusDebitGroup = 0
     *  se deve ricevere un credito statusDebitGroup = 1 */
    private int statusDebitGroup;
    private String amountDebit;
    private boolean activated;
    private String imgGroup;

    public MetadateGroup(String nameGroup, int statusDebitGroup, String amountDebit, boolean activated) {
        this.nameGroup = nameGroup;
        this.statusDebitGroup = statusDebitGroup;
        this.amountDebit = amountDebit;
        this.activated = activated;
    }

    public MetadateGroup() {
        super();
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nameGroup", nameGroup);
        result.put("statusDebitGroup", statusDebitGroup);
        result.put("amountDebit", amountDebit);
        result.put("activated", activated);

        return result;
    }

}
