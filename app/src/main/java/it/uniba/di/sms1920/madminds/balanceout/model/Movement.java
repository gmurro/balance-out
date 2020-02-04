package it.uniba.di.sms1920.madminds.balanceout.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Movement implements Serializable {
    public static final String ID_MOVEMENT_BALANCED ="idMovementBalanced";
    public static final String MOVEMENTS = "movements";
    public static final String ID_MOVEMENT = "idMovement";
    public static final String UID_CREDITOR = "uidCreditor";
    public static final String UID_DEBITOR = "uidDebitor";
    public static final String AMOUNT = "amount";
    public static final String ACTIVE = "active";
    public static final String ID_EXPENSE = "idExpense";

    private String idMovement;
    private String uidCreditor;
    private String uidDebitor;
    private String amount;
    private User creditor;
    private User debitor;
    private String idExpense;
    private boolean active;

    public Movement() {
    }

    public Movement(User debitor, User creditor, String amount) {
        this.creditor = creditor;
        this.debitor = debitor;
        this.amount = amount;
    }

    public Movement(String uidCreditor, String uidDebitor, String amount, String idExpense, boolean active) {
        this.uidCreditor = uidCreditor;
        this.uidDebitor = uidDebitor;
        this.amount = amount;
        this.idExpense = idExpense;
        this.active=active;
    }

    public String getIdMovement() {
        return idMovement;
    }

    public void setIdMovement(String idMovement) {
        this.idMovement = idMovement;
    }

    public String getUidCreditor() {
        return uidCreditor;
    }

    public void setUidCreditor(String uidCreditor) {
        this.uidCreditor = uidCreditor;
    }

    public String getUidDebitor() {
        return uidDebitor;
    }

    public void setUidDebitor(String uidDebitor) {
        this.uidDebitor = uidDebitor;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCreditor(User creditor) {
        this.creditor = creditor;
    }

    public void setDebitor(User debitor) {
        this.debitor = debitor;
    }

    public String getIdExpense() {
        return idExpense;
    }

    public void setIdExpense(String idExpense) {
        this.idExpense = idExpense;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put(ID_MOVEMENT, idMovement);
        result.put(UID_CREDITOR, uidCreditor);
        result.put(UID_DEBITOR, uidDebitor);
        result.put(AMOUNT, amount);
        result.put(ACTIVE, active);
        result.put(ID_EXPENSE, idExpense);

        return result;
    }

    @Override
    public String toString() {
        return "Movement{" +
                "idMovement='" + idMovement + '\'' +
                ", uidCreditor='" + uidCreditor + '\'' +
                ", uidDebitor='" + uidDebitor + '\'' +
                ", amount='" + amount + '\'' +
                ", creditor=" + creditor +
                ", debitor=" + debitor +
                ", idExpense='" + idExpense + '\'' +
                ", active=" + active +
                '}';
    }

    /* funzione che controlla se l'idMovement è presente nell array movements
       restituisce -1 se non c'e,
       l'indice in cui si trova se c'e
     */
    public static int containsIdMovement(ArrayList<Movement> movements, String idMovement) {
        int i=0;
        for (Movement g: movements) {
            if(g.getIdMovement().equals(idMovement)) {
                return i;
            }
            i++;
        }
        if(i==movements.size()) {
            i=-1;
        }
        return -1;
    }


    public static boolean containsAlreadyMovement(ArrayList<Movement> movements, Movement newMovement) {
        for (Movement m : movements) {

            BigDecimal alreadyPresentAmount = new BigDecimal(m.getAmount());
            BigDecimal newAmount = new BigDecimal(newMovement.getAmount());

            //se gli attori del movimento sono invertiti
            if (m.getUidCreditor().equals(newMovement.getUidDebitor()) && m.getUidDebitor().equals(newMovement.getUidCreditor())) {

                BigDecimal difference;

                //se alreadyPresentAmount > newAmount
                if (alreadyPresentAmount.compareTo(newAmount) > 0) {
                    difference = alreadyPresentAmount.subtract(newAmount);
                    m.setAmount(String.format("%.2f", difference).replace(",", "."));

                } else
                    //se alreadyPresentAmount < newAmount
                    if (alreadyPresentAmount.compareTo(newAmount) < 0) {
                        difference = newAmount.subtract(alreadyPresentAmount);
                        m.setUidCreditor(newMovement.getUidCreditor());
                        m.setUidDebitor(newMovement.getUidDebitor());
                        m.setAmount(String.format("%.2f", difference).replace(",", "."));

                    } else {
                        //se alreadyPresentAmount == newAmount
                        movements.remove(m);
                    }

                    return true;
            }

            //se gli attori del movimento sono gli stessi
            else if (m.getUidCreditor().equals(newMovement.getUidCreditor()) && m.getUidDebitor().equals(newMovement.getUidDebitor())) {
                BigDecimal sum = newAmount.add(alreadyPresentAmount);
                m.setAmount(String.format("%.2f", sum).replace(",", "."));

                return true;
            }
        }

        return false;
    }


    public static void recalculateMovementsGroup(final String idGroup, final String idAuth) {

        //array con tutti i movimenti presenti sul database
        final ArrayList<Movement> movementReaded = new ArrayList<>();

        DatabaseReference movementsReference = FirebaseDatabase.getInstance().getReference().child(Movement.MOVEMENTS).child(idGroup);
        movementsReference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        /*lettura della lista di movimenti dal db*/
                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                            Movement m = data.getValue(Movement.class);

                            /* viene controllato se l'id del movimento letto è una nuova lettura (in tal caso alreadyRead = -1) o è una modifica di un movimento gia letto (alreadyRead = id del movimento)*/
                            int alreadyRead = Movement.containsIdMovement(movementReaded, m.getIdMovement());
                            if (alreadyRead == -1) {

                                //se il movimento è attivo lo aggiunge
                                if (m.isActive()) {
                                    movementReaded.add(m);
                                }

                            } else {
                                //viene sostituito il movimento modificato
                                movementReaded.remove(alreadyRead);
                                if (m.isActive()) {
                                    movementReaded.add(alreadyRead, m);
                                }
                            }
                        }


                        //modifica dei movimenti all'interno del gruppo
                        writeMovementsGroup(movementReaded, idGroup, idAuth);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
    }

    private static void writeMovementsGroup(ArrayList<Movement> movements, String idGroup, String idAuth) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Log.w("test", "ALL-MOVEMENTS: "+movements.toString());
        //vengono creati dei movimenti risultanti da quelli presenti sul db che rappresentano le quote che gli utenti devono effettivamente pagare
        ArrayList<Movement> movementsToPay = new ArrayList<>();

        //calcolo dei movimenti validi
        for (Movement movementDb : movements) {
            if (!Movement.containsAlreadyMovement(movementsToPay, movementDb)) {
                movementsToPay.add(movementDb);
            }
        }

        //calcellazione del ramo listMovement
        databaseReference.child(Group.GROUPS).child(idGroup).child(Group.LIST_MOVEMENTS).removeValue();

        for (Movement m : movementsToPay) {
            final String key = databaseReference.child(Group.GROUPS).child(idGroup).child(Group.LIST_MOVEMENTS).push().getKey();
            //scrittura su db all interno del gruppo
            databaseReference.child(Group.GROUPS).child(idGroup).child(Group.LIST_MOVEMENTS).child(key).setValue(m.toMap());
        }

        Log.w("test3",movementsToPay.toString());

        //ricalcolo dello stato di debiti dei membri del gruppo
        calculateDebts(movementsToPay, idGroup, idAuth);
    }

    //funzione per calcolare lo stato dei debiti di ciascun utente nel gruppo, con scrittura nel db sul ramo di ciascun utente
    private static void calculateDebts(ArrayList<Movement> movements, final String idGroup, String idAuth) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        //mappa che ha come chiave l'id degli utenti e come valore il loro stato nel gruppo
        HashMap<String, MetadateGroup> usersStatusGroup = new HashMap<>();

        //algoritmo per il calcolo dello stato dei debiti
        for(Movement m: movements) {
            //se nella mappa è gia presente l'utente creditore
            if( usersStatusGroup.containsKey(m.getUidCreditor()) ){

                //leggo lo la quantità del debito e lo rendo positivo o negativo a seconda dello stato
                BigDecimal amount = new BigDecimal(usersStatusGroup.get(m.getUidCreditor()).getAmountDebit());
                amount = amount.multiply( new BigDecimal(usersStatusGroup.get(m.getUidCreditor()).getStatusDebitGroup()));
                //sommo la somma che il creditore deve ricevere
                amount = amount.add(new BigDecimal(m.getAmount()));

                MetadateGroup metadate;
                if(amount.compareTo(BigDecimal.ZERO)>0) {
                    metadate = new MetadateGroup(MetadateGroup.STATUS_CREDIT, String.format("%.2f",amount).replace(",","."), idGroup);
                } else if (amount.compareTo(BigDecimal.ZERO)<0) {
                    amount = amount.multiply( new BigDecimal("-1"));
                    metadate = new MetadateGroup(MetadateGroup.STATUS_DEBT, String.format("%.2f",amount).replace(",","."), idGroup);
                } else {
                    metadate = new MetadateGroup(MetadateGroup.STATUS_PARITY, String.format("%.2f",amount).replace(",","."), idGroup);
                }
                usersStatusGroup.put(m.getUidCreditor(), metadate);
                Log.w("test2","creditor ("+m.getUidCreditor()+") :"+metadate);
            } else {
                //aggiungo il creditore alla mappa con il proprio debito
                MetadateGroup metadate = new MetadateGroup(MetadateGroup.STATUS_CREDIT, m.getAmount(), idGroup);
                usersStatusGroup.put(m.getUidCreditor(), metadate);
                Log.w("test2","creditor ("+m.getUidCreditor()+") :"+metadate);
            }



            //se nella mappa è gia presente l'utente debitore
            if( usersStatusGroup.containsKey(m.getUidDebitor()) ){

                //leggo lo la quantità del debito e lo rendo positivo o negativo a seconda dello stato
                BigDecimal amount = new BigDecimal(usersStatusGroup.get(m.getUidDebitor()).getAmountDebit());
                amount = amount.multiply( new BigDecimal(usersStatusGroup.get(m.getUidDebitor()).getStatusDebitGroup()));
                //sommo la somma che il creditore deve ricevere
                amount = amount.subtract(new BigDecimal(m.getAmount()));

                MetadateGroup metadate;
                if(amount.compareTo(BigDecimal.ZERO)>0) {
                    metadate = new MetadateGroup(MetadateGroup.STATUS_CREDIT, String.format("%.2f",amount).replace(",","."), idGroup);
                } else if (amount.compareTo(BigDecimal.ZERO)<0) {
                    amount = amount.multiply( new BigDecimal("-1"));
                    metadate = new MetadateGroup(MetadateGroup.STATUS_DEBT, String.format("%.2f",amount).replace(",","."), idGroup);
                } else {
                    metadate = new MetadateGroup(MetadateGroup.STATUS_PARITY, String.format("%.2f",amount).replace(",","."), idGroup);
                }
                usersStatusGroup.put(m.getUidDebitor(), metadate);
                Log.w("test2","debitor ("+m.getUidDebitor()+") :"+metadate);
            } else {
                //aggiungo il creditore alla mappa con il proprio debito
                MetadateGroup metadate = new MetadateGroup(MetadateGroup.STATUS_DEBT, m.getAmount(), idGroup);
                usersStatusGroup.put(m.getUidDebitor(), metadate);
                Log.w("test2","debitor ("+m.getUidDebitor()+") :"+metadate);
            }
        }

        Log.w("test4",usersStatusGroup.toString());


        //scrittura dello stato degli utenti nel gruppo sul db
        for (Map.Entry<String,MetadateGroup> entry : usersStatusGroup.entrySet()){
            String amountDebt = entry.getValue().getAmountDebit();
            int status = entry.getValue().getStatusDebitGroup();
            databaseReference.child(User.USERS).child(entry.getKey()).child(User.MY_GROUPS).child(idGroup).child(MetadateGroup.AMOUNT_DEBIT).setValue(amountDebt);
            databaseReference.child(User.USERS).child(entry.getKey()).child(User.MY_GROUPS).child(idGroup).child(MetadateGroup.STATUS_DEBIT_GROUP).setValue(status);
        }

        //se non ci sono piu movimenti devo azzerare i debiti per tutti i membri del gruppo
        if(usersStatusGroup.size()==0) {
            databaseReference.child(Group.GROUPS).child(idGroup).child(Group.UID_MEMEBRS).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                String idUser = user.getValue(String.class);
                                //viene azzerato il debito
                                databaseReference.child(User.USERS).child(idUser).child(User.MY_GROUPS).child(idGroup).child(MetadateGroup.AMOUNT_DEBIT).setValue("0.00");
                                databaseReference.child(User.USERS).child(idUser).child(User.MY_GROUPS).child(idGroup).child(MetadateGroup.STATUS_DEBIT_GROUP).setValue(0);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        } else
        //se non ci sono piu debiti che mi riguardano devo azzerare i miei debiti
        if(!usersStatusGroup.containsKey(idAuth)) {
            databaseReference.child(User.USERS).child(idAuth).child(User.MY_GROUPS).child(idGroup).child(MetadateGroup.AMOUNT_DEBIT).setValue("0.00");
            databaseReference.child(User.USERS).child(idAuth).child(User.MY_GROUPS).child(idGroup).child(MetadateGroup.STATUS_DEBIT_GROUP).setValue(0);
        }
    }
}
