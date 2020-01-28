package it.uniba.di.sms1920.madminds.balanceout.model;

public class Payer {
    private User user;
    private String amount;

    public Payer(User user, String amount) {
        this.user = user;
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public String getAmount() {
        return amount;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Payer{" +
                "user=" + user +
                ", amount='" + amount + '\'' +
                '}';
    }
}
