package it.uniba.di.sms1920.madminds.balanceout.model;

public class Payer {
    private String idUser;
    private String amount;
    private User user;

    public Payer(String idUser, String amount) {
        this.idUser = idUser;
        this.amount = amount;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Payer{" +
                "idUser='" + idUser + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
