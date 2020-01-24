package it.uniba.di.sms1920.madminds.balanceout.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Group implements Serializable {

    public static final String GROUP="GROUP";

    private String idGroup;
    private String nameGroup;
    private Date creationDataGroup;
    private String imgGroup;
    private ArrayList<User> members;
    private ArrayList<String> uidMembers;
    private String idAmministrator;

    /* per ogni account, se esso è in debito col gruppo  statusDebitGroup = -1
     *  se è in pari statusDebitGroup = 0
     *  se deve ricevere un credito statusDebitGroup = 1 */
    private int statusDebitGroup;
    private double amountDebit;
    private boolean activated;


    public Group(String idGroup,
                 String nameGroup,
                 Date creationDataGroup,
                 String imgGroup,
                 ArrayList<User> members,
                 ArrayList<String> uidMembers,
                 String idAmministrator,
                 int statusDebitGroup,
                 double amountDebit,
                 boolean activated) {
        this.idGroup = idGroup;
        this.nameGroup = nameGroup;
        this.creationDataGroup = creationDataGroup;
        this.imgGroup = imgGroup;
        this.members = members;
        this.uidMembers = uidMembers;
        this.idAmministrator = idAmministrator;
        this.statusDebitGroup = statusDebitGroup;
        this.amountDebit = amountDebit;
        this.activated = activated;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public int getStatusDebitGroup() {
        return statusDebitGroup;
    }

    public Date getCreationDataGroup() {
        return creationDataGroup;
    }

    public String getIdAmministrator() {
        return idAmministrator;
    }

    public double getAmountDebit() {
        return amountDebit;
    }

    public boolean isActivated() {
        return activated;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public String getImgGroup() {
        return imgGroup;
    }

    public void setImgGroup(String imgGroup) {
        this.imgGroup = imgGroup;
    }

    public ArrayList<String> getUidMembers() {
        return uidMembers;
    }

    public void setUidMembers(ArrayList<String> uidMembers) {
        this.uidMembers = uidMembers;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        /*formattazione della data in dd/mm/aa*/
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        result.put("idGroup", idGroup);
        result.put("nameGroup", nameGroup);
        result.put("creationDataGroup", new SimpleDateFormat("dd/MM/yyyy").format(creationDataGroup));
        result.put("imgGroup", imgGroup);
        result.put("members", members);
        result.put("uidMembers", uidMembers);
        result.put("idAmministrator", idAmministrator);
        result.put("statusDebitGroup", statusDebitGroup);
        result.put("amountDebit", amountDebit);
        result.put("activated", activated);

        return result;
    }

}