package it.uniba.di.sms1920.madminds.balanceout.model;

import java.util.HashMap;
import java.util.Map;

public class Payer {
    private String idUser;
    private String amount;
    private User user;

    public static final String ID_USER = "idUser";
    public static final String AMOUNT = "amount";

    public Payer(String idUser, String amount) {
        this.idUser = idUser;
        this.amount = amount;
    }

    public Payer() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


    @Override
    public String toString() {
        return "Payer{" +
                "idUser='" + idUser + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }



    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        //new SimpleDateFormat("dd/MM/yyyy").format(creationDataGroup)

        result.put(ID_USER, idUser);
        result.put(AMOUNT, AMOUNT);

        return result;
    }
}
