package it.uniba.di.sms1920.madminds.balanceout.model;

public class Movement {
    private User creditor;
    private User debitor;
    private String amount;

    public Movement(User debitor, User creditor, String amount) {
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

    public String getAmount() {
        return amount;
    }
}
