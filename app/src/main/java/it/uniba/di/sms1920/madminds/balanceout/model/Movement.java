package it.uniba.di.sms1920.madminds.balanceout.model;

public class Movement {
    private Utente creditor;
    private Utente debitor;
    private double importo;

    public Movement(Utente creditor, Utente debitor, double importo) {
        this.creditor = creditor;
        this.debitor = debitor;
        this.importo = importo;
    }

    public Utente getCreditor() {
        return creditor;
    }

    public Utente getDebitor() {
        return debitor;
    }

    public double getImporto() {
        return importo;
    }
}
