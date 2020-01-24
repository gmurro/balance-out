package it.uniba.di.sms1920.madminds.balanceout.model;

import java.io.Serializable;
import java.util.*;

public class Group implements Serializable {

    public static final String GROUP="GROUP";

    private String idGroup;
    private String nameGroup;
    private Date creationDataGroup;
    private String imgGroup;
    private ArrayList<User> members;

    /* per ogni account, se esso è in debito col gruppo  statusDebitGroup = -1
    *  se è in pari statusDebitGroup = 0
    *  se deve ricevere un credito statusDebitGroup = 1 */
    private int statusDebitGroup;

    public Group(String idGroup, String nameGroup, Date creationDataGroup, String imgGroup, ArrayList<User> members, int statusDebitGroup) {
        this.idGroup = idGroup;
        this.nameGroup = nameGroup;
        this.creationDataGroup = creationDataGroup;
        this.imgGroup = imgGroup;
        this.members = members;
        this.statusDebitGroup = statusDebitGroup;
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


    public ArrayList<User> getMembers() {
        return members;
    }

    public String getImgGroup() {
        return imgGroup;
    }

    public void setImgGroup(String imgGroup) {
        this.imgGroup = imgGroup;
    }


}
