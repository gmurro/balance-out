package it.uniba.di.sms1920.madminds.balanceout.model;

public class Movement {
    private User creditor;
    private User debitor;
    private double amount;

    public Movement(User debitor, User creditor, double amount) {
        this.creditor = creditor;
        this.debitor = debitor;
        this.amount = amount;
    }

    public User getCreditor() {
        return creditor;
    }

    public User getDebitor() {
        return debitor;
    }

    public double getAmount() {
        return amount;
    }
}
