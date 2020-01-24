package it.uniba.di.sms1920.madminds.balanceout.model;

public class Payer {
    private User user;
    private double amount;

    public Payer(User user, double amount) {
        this.user = user;
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public double getAmount() {
        return amount;
    }
}
