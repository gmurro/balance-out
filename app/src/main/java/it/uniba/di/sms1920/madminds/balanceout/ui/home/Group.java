package it.uniba.di.sms1920.madminds.balanceout.ui.home;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.*;

public class Group implements Serializable {

    public static final String GROUP="GROUP";

    private int idGroup;
    private String nameGroup;
    private Date creationDataGroup;
    private String imgGroup;

    /* per ogni account, se esso è in debito col gruppo  statusDebitGroup = -1
    *  se è in pari statusDebitGroup = 0
    *  se deve ricevere un credito statusDebitGroup = 1 */
    private int statusDebitGroup;


    public Group(String nameGroup, Date creationDataGroup, String imgGroup, int statusDebitGroup) {
        this.nameGroup = nameGroup;
        this.creationDataGroup = creationDataGroup;
        this.imgGroup = imgGroup;
        this.statusDebitGroup = statusDebitGroup;
    }

    public Group(int idGroup, String nameGroup, Date creationDataGroup, String imgGroup, int statusDebitGroup) {
        this.idGroup = idGroup;
        this.nameGroup = nameGroup;
        this.creationDataGroup = creationDataGroup;
        this.imgGroup = imgGroup;
        this.statusDebitGroup = statusDebitGroup;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }
    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }

    public int getStatusDebitGroup() {
        return statusDebitGroup;
    }

    public void setStatusDebitGroup(int statusDebitGroup) {
        this.statusDebitGroup = statusDebitGroup;
    }

    public Date getCreationDataGroup() {
        return creationDataGroup;
    }

    public void setCreationDataGroup(Date creationDataGroup) {
        this.creationDataGroup = creationDataGroup;
    }

    public String getImgGroup() {
        return imgGroup;
    }

    public void setImgGroup(String imgGroup) {
        this.imgGroup = imgGroup;
    }


}
