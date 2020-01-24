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
    private ArrayList<String> uidMembers;
    private String idAmministrator;


    public Group(String idGroup, String nameGroup, Date creationDataGroup, String imgGroup, ArrayList<User> members,ArrayList<String> uidMembers, String idAmministrator) {
        this.idGroup = idGroup;
        this.nameGroup = nameGroup;
        this.creationDataGroup = creationDataGroup;
        this.imgGroup = imgGroup;
        this.members = members;
        this.uidMembers = uidMembers;
        this.idAmministrator = idAmministrator;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public Date getCreationDataGroup() {
        return creationDataGroup;
    }

    public String getIdAmministrator() {
        return idAmministrator;
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
        result.put("idGroup", idGroup);
        result.put("nameGroup", nameGroup);
        result.put("creationDataGroup", creationDataGroup.toString());
        result.put("imgGroup", imgGroup);
        result.put("uidMembers", uidMembers);
        result.put("idAmministrator", idAmministrator);

        return result;
    }
}
