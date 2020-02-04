package it.uniba.di.sms1920.madminds.balanceout.ui.expense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.CircleTrasformation;
import it.uniba.di.sms1920.madminds.balanceout.helper.DividerItemDecorator;
import it.uniba.di.sms1920.madminds.balanceout.helper.MoneyDivider;
import it.uniba.di.sms1920.madminds.balanceout.model.Expense;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.KeyValueItem;
import it.uniba.di.sms1920.madminds.balanceout.model.MetadateGroup;
import it.uniba.di.sms1920.madminds.balanceout.model.Movement;
import it.uniba.di.sms1920.madminds.balanceout.model.Payer;
import it.uniba.di.sms1920.madminds.balanceout.model.User;
import it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup.MovementAdapter;

public class NewExpenseActivity extends AppCompatActivity {

    public final int RESULT_LOAD_IMAGE = 21;
    public final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 11;
    private FirebaseAuth mAuth;
    private Spinner groupNewExpenseSpinner, typeDivisionNewExpenseSpinner;
    private Group group;
    private RecyclerView payerNewExpenseRecyclerView;
    private RecyclerView debitorEqualDivisionNewExpenseRecyclerView;
    private RecyclerView debitorDisequalDivisionNewExpenseRecyclerView;
    private PayerNewExpenseAdapter payersAdapter;
    private DebitorEqualDivisionAdapter equalDivisionAdapter;
    private DebitorDisequalDivisionAdapter disequalDivisionAdapter;
    private ImageView imgMePayerNewExpenseImageView, imgAddReceiptNewExpenseImageView;
    private ConstraintLayout dateNewExpenseConstraintLayout, addReceiptConstraintLayout;
    private TextView dataNewExpenseTextView, titleAddReceiptNewExpenseTextView, errorDivisionNewExpenseTextView;

    private MaterialButton addExpenseButton;
    private TextInputEditText descriptionNewExpenseEditText, valueMePaidNewExpenseEditText;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Uri filePathReceipt;
    private int typeDivisionSelected;
    private ArrayList<Payer> creditors;
    private ArrayList<Payer> debitors;
    private ArrayList<Movement> movementsToPay;

    /*contatore per contare i gruppi letti*/
    private int i = 0;

    /*variabile che memorizza l'indice dell'array di membri del gruppo in cui è presente l'utente loggato, di default è -1*/
    private int indexLoggedUser = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        Toolbar toolbar = findViewById(R.id.newExpenseToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        group = new Group();
        creditors = new ArrayList<>();
        debitors = new ArrayList<>();

        /* viene letto il gruppo in cui vi si era precedentemente*/
        if (getIntent().hasExtra(Group.GROUP)) {
            group = (Group) getIntent().getExtras().getSerializable(Group.GROUP);
        }

        /* viene avvalorato l'utente loggato al momento */
        mAuth = FirebaseAuth.getInstance();

        groupNewExpenseSpinner = findViewById(R.id.groupNewExpenseSpinner);
        typeDivisionNewExpenseSpinner = findViewById(R.id.typeDivisionNewExpenseSpinner);
        payerNewExpenseRecyclerView = findViewById(R.id.payerNewExpenseRecyclerView);
        debitorEqualDivisionNewExpenseRecyclerView = findViewById(R.id.debitorEqualDivisionNewExpenseRecyclerView);
        debitorDisequalDivisionNewExpenseRecyclerView = findViewById(R.id.debitorDisequalDivisionNewExpenseRecyclerView);
        imgMePayerNewExpenseImageView = findViewById(R.id.imgMePayerNewExpenseImageView);
        dateNewExpenseConstraintLayout = findViewById(R.id.dateNewExpenseConstraintLayout);
        addReceiptConstraintLayout = findViewById(R.id.addReceiptConstraintLayout);
        dataNewExpenseTextView = findViewById(R.id.dataNewExpenseTextView);
        imgAddReceiptNewExpenseImageView = findViewById(R.id.imgAddReceiptNewExpenseImageView);
        titleAddReceiptNewExpenseTextView = findViewById(R.id.titleAddReceiptNewExpenseTextView);
        addExpenseButton = findViewById(R.id.addExpenseButton);
        descriptionNewExpenseEditText = findViewById(R.id.descriptionNewExpenseEditText);
        valueMePaidNewExpenseEditText = findViewById(R.id.valueMePaidNewExpenseEditText);
        errorDivisionNewExpenseTextView = findViewById(R.id.errorDivisionNewExpenseTextView);

        //vengono caricati i gruppi nello spinner per i gruppi
        loadGroupsSpinner();

        //la data della spesa viene impostata a quella odierna
        dataNewExpenseTextView.setText(new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTimeInMillis()));

        //vengono inizializzati i valori per lo spinner per la divisione
        final ArrayList<String> typeDivision = new ArrayList<>();
        typeDivision.add(getString(R.string.type_division_equal));
        typeDivision.add(getString(R.string.type_division_disequal));

        ArrayAdapter<String> adapterTypeDivision = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeDivision);
        adapterTypeDivision.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeDivisionNewExpenseSpinner.setAdapter(adapterTypeDivision);

        typeDivisionNewExpenseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                switch (pos) {
                    case 0:
                        debitorEqualDivisionNewExpenseRecyclerView.setVisibility(View.VISIBLE);
                        debitorDisequalDivisionNewExpenseRecyclerView.setVisibility(View.GONE);
                        break;
                    case 1:
                        debitorEqualDivisionNewExpenseRecyclerView.setVisibility(View.GONE);
                        debitorDisequalDivisionNewExpenseRecyclerView.setVisibility(View.VISIBLE);
                        break;
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dateNewExpenseConstraintLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                        builder.setTitleText(R.string.title_data_expense);
                        builder.setSelection(Calendar.getInstance().getTimeInMillis());
                        MaterialDatePicker<Long> picker = builder.build();
                        picker.show(getSupportFragmentManager(), picker.toString());
                        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                            @Override
                            public void onPositiveButtonClick(Long selection) {
                                dataNewExpenseTextView.setText(DateFormat.format("dd/MM/yyyy", new Date(selection)).toString());
                            }
                        });
                    }
                }
        );


        /*quando viene cliccata la foto, si puo caricare un immagine dalla galleria*/
        addReceiptConstraintLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Devo chiedere il permesso di poter leggere dallo storage per caricare una foto e la leggo
                        checkPermissionReadExternalStorage();
                    }
                }
        );

        /*quando viene premuto il bottone per aggiungere la spesa devo controllare la consistenza dei dati inseriti */
        addExpenseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean error = controlFields();
                        if (!error) {

                            storageReference = FirebaseStorage.getInstance().getReference("receiptsExpenses");
                            databaseReference = FirebaseDatabase.getInstance().getReference();
                            final String idExpense = databaseReference.child(group.getIdGroup()).push().getKey();

                            final Expense e = new Expense(
                                    null,
                                    creditors,
                                    dataNewExpenseTextView.getText().toString(),
                                    typeDivisionSelected,
                                    descriptionNewExpenseEditText.getText().toString(),
                                    null,
                                    debitors,
                                    group.getIdGroup(),
                                    0,
                                    true
                            );

                            //viene creato un array contenete i debitori per i movimenti
                            ArrayList<Payer> debitorsMovement = new ArrayList<>();
                            for (Payer debitor : debitors) {

                                //viene controllato se un debitore è anche creditore e in tal caso viene memorizzato quanto ha pagato nella spesa
                                String amountPaidString = isDebitorAlsoCreditor(debitor.getIdUser(), creditors);
                                if (amountPaidString == null) {
                                    debitorsMovement.add(debitor);
                                } else {
                                    //se è anche creditore
                                    BigDecimal amountPaid = new BigDecimal(amountPaidString);
                                    BigDecimal debt = new BigDecimal(debitor.getAmount());

                                    //se ha un debito maggiore di quanto ha pagato per la spesa viene aggiunto alla lista dei debitori nei movimenti
                                    if (debt.compareTo(amountPaid) == 1) {
                                        BigDecimal valueDebt = debt.subtract(amountPaid);
                                        debitorsMovement.add(new Payer(debitor.getIdUser(), String.format("%.2f", valueDebt).replace(",", ".")));
                                    }
                                }
                            }

                            //viene creato un array contenete i creditori per i movimenti
                            ArrayList<Payer> creditorsMovement = new ArrayList<>();
                            for (Payer creditor : creditors) {
                                //viene controllato se un creditore è anche debitore e in tal caso viene memorizzato quanto ha di debito
                                String debtString = isCreditoreAlsoDebitor(creditor.getIdUser(), debitors);
                                if (debtString == null) {
                                    creditorsMovement.add(creditor);
                                } else {
                                    //se è anche debitore
                                    BigDecimal debt = new BigDecimal(debtString);
                                    BigDecimal amountPaid = new BigDecimal(creditor.getAmount());

                                    //se ha un credito maggiore del suo debito viene aggiunto alla lista dei creditori nei movimenti
                                    if (amountPaid.compareTo(debt) == 1) {
                                        BigDecimal valuePaid = amountPaid.subtract(debt);
                                        creditorsMovement.add(new Payer(creditor.getIdUser(), String.format("%.2f", valuePaid).replace(",", ".")));
                                    }
                                }
                            }

                            Log.w("debug", "debitors: " + debitorsMovement.toString());
                            Log.w("debug", "creditors: " + creditorsMovement.toString());

                            //algoritmo per il calcolo dei movimenti
                            final ArrayList<Movement> movements = new ArrayList<>();
                            int i = 0;
                            int j = 0;
                            BigDecimal amountToHaveCreditor = BigDecimal.ZERO;
                            BigDecimal amountToGiveDebitor = BigDecimal.ZERO;
                            while (i < creditorsMovement.size()) {
                                Payer creditor = creditorsMovement.get(i);
                                amountToHaveCreditor = new BigDecimal(creditor.getAmount());

                                //finche amountToHaveCreditor è > di zero, cioè fin quando il creditore ha ancora qualcosa da avere
                                while (amountToHaveCreditor.compareTo(BigDecimal.ZERO) > 0) {

                                    Payer debitor = debitorsMovement.get(j);
                                    amountToGiveDebitor = new BigDecimal(debitor.getAmount());

                                    BigDecimal difference = amountToHaveCreditor.subtract(amountToGiveDebitor);
                                    Log.w("debug", "diff: " + difference + " i:" + i + " j:" + j);
                                    //se amountToHaveCreditor - amountToGiveDebitor è <= 0, cioè se il debitore ha pagato quanto doveva avere il creditore
                                    if (difference.compareTo(BigDecimal.ZERO) < 0) {
                                        Movement m = new Movement(creditor.getIdUser(), debitor.getIdUser(), creditor.getAmount(), idExpense, true);
                                        movements.add(m);
                                        amountToHaveCreditor = BigDecimal.ZERO;

                                        BigDecimal newDebt = amountToGiveDebitor.subtract(amountToHaveCreditor);
                                        debitor.setAmount(String.format("%.2f", newDebt).replace(",", "."));
                                    } else {
                                        //se amountToHaveCreditor - amountToGiveDebitor è > 0

                                        amountToHaveCreditor = difference;
                                        Movement m = new Movement(creditor.getIdUser(), debitor.getIdUser(), debitor.getAmount(), idExpense, true);
                                        movements.add(m);
                                        j++;

                                    }
                                    Log.w("debug", amountToHaveCreditor + " movements: " + movements.toString());
                                }
                                i++;

                            }

                            //array con tutti i movimenti presenti sul database
                            final ArrayList<Movement> movementReaded = new ArrayList<>();
                            //vengono letti i movimenti già presenti sul db
                            databaseReference.child(Movement.MOVEMENTS).child(group.getIdGroup()).addListenerForSingleValueEvent(
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

                                            //vengono accorpati i due array
                                            movementReaded.addAll(movements);

                                            //i dati calcolati vengono scritti sul database
                                            writeOnDb(e, idExpense, movements,movementReaded);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    }
                            );

                        }
                    }
                }
        );

    }


    private void writeOnDb(Expense e, final String key, final ArrayList<Movement> movements, final ArrayList<Movement> movementsReaded) {

        e.setId(key);
        Map<String, Object> childUpdate = new HashMap<>();
        //scrittura su rami multipli
        databaseReference.child(Expense.EXPENSES).child(group.getIdGroup()).child(key).setValue(e.toMap());
        //childUpdate.put(key, e.toMap());
        childUpdate.put(key + "/" + Expense.PAYERS_EXPENSE, creditors);
        childUpdate.put(key + "/" + Expense.PAYERS_DEBT, debitors);

        databaseReference.child(Expense.EXPENSES).child(group.getIdGroup()).updateChildren(childUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.w("test", "uri: " + filePathReceipt);
                if (filePathReceipt != null) {
                    fileUpdater(key);
                }

                //scrittura dei movimenti
                for (Movement m : movements) {
                    addMovements(m);
                }

                //calcolo della risultante dei movimenti da scrivere all interno del gruppo
                writeMovementsGroup(movementsReaded);

                //calcolo dello stato di debiti dei membri del gruppo
                calculateDebts(movementsToPay);

                setResult(RESULT_OK);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(findViewById(R.id.groupNewExpenseSpinner), getString(R.string.expence_not_added), Snackbar.LENGTH_LONG);
            }
        });

    }


    //funzione per calcolare lo stato dei debiti di ciascun utente nel gruppo, con scrittura nel db sul ramo di ciascun utente
    private void calculateDebts(ArrayList<Movement> movements) {
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
                    metadate = new MetadateGroup(MetadateGroup.STATUS_CREDIT, String.format("%.2f",amount).replace(",","."), group.getIdGroup());
                } else if (amount.compareTo(BigDecimal.ZERO)<0) {
                    amount = amount.multiply( new BigDecimal("-1"));
                    metadate = new MetadateGroup(MetadateGroup.STATUS_DEBT, String.format("%.2f",amount).replace(",","."), group.getIdGroup());
                } else {
                    metadate = new MetadateGroup(MetadateGroup.STATUS_PARITY, String.format("%.2f",amount).replace(",","."), group.getIdGroup());
                }
                usersStatusGroup.put(m.getUidCreditor(), metadate);
                Log.w("test2","creditor ("+m.getUidCreditor()+") :"+metadate);
            } else {
                //aggiungo il creditore alla mappa con il proprio debito
                MetadateGroup metadate = new MetadateGroup(MetadateGroup.STATUS_CREDIT, m.getAmount(), group.getIdGroup());
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
                    metadate = new MetadateGroup(MetadateGroup.STATUS_CREDIT, String.format("%.2f",amount).replace(",","."), group.getIdGroup());
                } else if (amount.compareTo(BigDecimal.ZERO)<0) {
                    amount = amount.multiply( new BigDecimal("-1"));
                    metadate = new MetadateGroup(MetadateGroup.STATUS_DEBT, String.format("%.2f",amount).replace(",","."), group.getIdGroup());
                } else {
                    metadate = new MetadateGroup(MetadateGroup.STATUS_PARITY, String.format("%.2f",amount).replace(",","."), group.getIdGroup());
                }
                usersStatusGroup.put(m.getUidDebitor(), metadate);
                Log.w("test2","debitor ("+m.getUidDebitor()+") :"+metadate);
            } else {
                //aggiungo il creditore alla mappa con il proprio debito
                MetadateGroup metadate = new MetadateGroup(MetadateGroup.STATUS_DEBT, m.getAmount(), group.getIdGroup());
                usersStatusGroup.put(m.getUidDebitor(), metadate);
                Log.w("test2","debitor ("+m.getUidDebitor()+") :"+metadate);
            }
        }

        Log.w("test2",usersStatusGroup.toString());


        //scrittura dello stato degli utenti nel gruppo sul db
        for (Map.Entry<String,MetadateGroup> entry : usersStatusGroup.entrySet()){
            String amountDebt = entry.getValue().getAmountDebit();
            int status = entry.getValue().getStatusDebitGroup();
            databaseReference.child(User.USERS).child(entry.getKey()).child(User.MY_GROUPS).child(group.getIdGroup()).child(MetadateGroup.AMOUNT_DEBIT).setValue(amountDebt);
            databaseReference.child(User.USERS).child(entry.getKey()).child(User.MY_GROUPS).child(group.getIdGroup()).child(MetadateGroup.STATUS_DEBIT_GROUP).setValue(status);
        }

        //se non ci sono piu movimenti devo azzerare i debiti per tutti i membri del gruppo
        if(usersStatusGroup.size()==0) {
            databaseReference.child(Group.GROUPS).child(group.getIdGroup()).child(Group.UID_MEMEBRS).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot user : dataSnapshot.getChildren()) {
                                String idUser = user.getValue(String.class);

                                //viene azzerato il debito
                                databaseReference.child(User.USERS).child(idUser).child(User.MY_GROUPS).child(group.getIdGroup()).child(MetadateGroup.AMOUNT_DEBIT).setValue("0.00");
                                databaseReference.child(User.USERS).child(idUser).child(User.MY_GROUPS).child(group.getIdGroup()).child(MetadateGroup.STATUS_DEBIT_GROUP).setValue(0);
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

    }


    private void addMovements(Movement m) {

        final String key = databaseReference.child(Movement.MOVEMENTS).child(group.getIdGroup()).push().getKey();
        m.setIdMovement(key);

        //scrittura su db
        databaseReference.child(Movement.MOVEMENTS).child(group.getIdGroup()).child(key).setValue(m.toMap());
    }

    private void writeMovementsGroup(ArrayList<Movement> movements) {

        Log.w("test", "ALL-MOVEMENTS: "+movements.toString());
        //vengono creati dei movimenti risultanti da quelli presenti sul db che rappresentano le quote che gli utenti devono effettivamente pagare
        movementsToPay = new ArrayList<>();

        //calcolo dei movimenti validi
        for (Movement movementDb : movements) {
            if (!Movement.containsAlreadyMovement(movementsToPay, movementDb)) {
                movementsToPay.add(movementDb);
            }
        }

        //calcellazione del ramo listMovement
        databaseReference.child(Group.GROUPS).child(group.getIdGroup()).child(Group.LIST_MOVEMENTS).removeValue();

        for (Movement m : movementsToPay) {
            final String key = databaseReference.child(Group.GROUPS).child(group.getIdGroup()).child(Group.LIST_MOVEMENTS).push().getKey();
            //scrittura su db all interno del gruppo
            databaseReference.child(Group.GROUPS).child(group.getIdGroup()).child(Group.LIST_MOVEMENTS).child(key).setValue(m.toMap());

        }
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));

    }

    private void fileUpdater(final String idExpense) {

        final StorageReference ref = storageReference.child(idExpense + "." + getExtension(filePathReceipt));


        ref.putFile(filePathReceipt)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //Scrittura della posizione della foto nello storage
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                databaseReference.child(Expense.EXPENSES).child(group.getIdGroup()).child(idExpense).child(Expense.RECEIPT).setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //scrittura fatta correttamente
                                        Log.w("test", "scrittura avvenuta con successo");
                                    }

                                });


                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {


                    }
                });


    }

    private boolean controlFields() {

        creditors.clear();
        debitors.clear();
        errorDivisionNewExpenseTextView.setVisibility(View.GONE);

        boolean invalidFields = false;
        if (descriptionNewExpenseEditText.getText().toString().trim().isEmpty()) {
            descriptionNewExpenseEditText.setError(getString(R.string.title_insert_description));
            invalidFields = true;
        }
        if (dataNewExpenseTextView.getText().equals(getString(R.string.title_data_expense))) {
            Snackbar.make(findViewById(R.id.dateNewExpenseConstraintLayout), getString(R.string.title_insert_data_expense), Snackbar.LENGTH_LONG).show();
            invalidFields = true;
        }
        if (valueMePaidNewExpenseEditText.getText().toString().trim().isEmpty() || valueMePaidNewExpenseEditText.getText().toString().equals(".")) {
            valueMePaidNewExpenseEditText.setError(getString(R.string.title_insert_amount_expense));
            invalidFields = true;
        }
        //se il campo relativo al pagamento in corrispondenza di un membro selezionato ha più di 2 cifre decimali, viene segnalato un errore
        if (!isMaxTwoDecimalPlaces(valueMePaidNewExpenseEditText.getText().toString())) {
            valueMePaidNewExpenseEditText.setError(getString(R.string.title_error_decimal_places));
            invalidFields = true;
        }

        if (!invalidFields) {

            //viene convertito il valore inserito in un double
            double valuePaidUserLogged = Double.parseDouble(valueMePaidNewExpenseEditText.getText().toString());
            //variabile che contiene la somma dei pagamenti fatti per la spesa da ciascun membro (impostata inizialmente al pagamneto fatto dall'utente loggato)
            double amountPayment = valuePaidUserLogged;

            //viene aggiunto l'utente loggato con l'importo della spesa all'array creditors
            Payer loggedUser = new Payer(group.getMembers().get(indexLoggedUser).getUid(), String.format("%.2f", valuePaidUserLogged).replace(",", "."));
            creditors.add(loggedUser);

            //vengono aggiunti tutti gli utenti selezionati con l'importo della spesa all'array creditors
            for (int i = 0; i < group.getMembers().size() - 1; i++) {
                View view = payerNewExpenseRecyclerView.getChildAt(i);
                CheckBox selectedPayerNewExpenseCheckBox = view.findViewById(R.id.selectedPayerNewExpenseCheckBox);
                TextView uidPayerNewExpenseTextView = view.findViewById(R.id.uidPayerNewExpenseTextView);
                TextInputEditText valuePaidNewExpenseEditText = view.findViewById(R.id.valuePaidNewExpenseEditText);

                if (selectedPayerNewExpenseCheckBox.isChecked()) {

                    //se il campo relativo al pagamento in corrispondenza di un membro selzionato è vuoto, viene segnalato un errore
                    if (valuePaidNewExpenseEditText.getText().toString().trim().isEmpty() || valuePaidNewExpenseEditText.getText().toString().equals(".")) {
                        valuePaidNewExpenseEditText.setError(getString(R.string.title_insert_amount_expense));
                        invalidFields = true;
                        return invalidFields;
                    } else
                        //se il campo relativo al pagamento in corrispondenza di un membro selezionato ha più di 2 cifre decimali, viene segnalato un errore
                        if (!isMaxTwoDecimalPlaces(valuePaidNewExpenseEditText.getText().toString())) {
                            valuePaidNewExpenseEditText.setError(getString(R.string.title_error_decimal_places));
                            invalidFields = true;
                            return invalidFields;
                        }

                    //viene convertito il valore inserito in un double
                    double valuePaid = Double.parseDouble(valuePaidNewExpenseEditText.getText().toString());
                    amountPayment += valuePaid;

                    Payer p = new Payer(uidPayerNewExpenseTextView.getText().toString(), String.format("%.2f", valuePaid).replace(",", "."));
                    creditors.add(p);
                }
            }

            //se la divisione è in parti uguali
            if (typeDivisionNewExpenseSpinner.getSelectedItem().toString().equals(getString(R.string.type_division_equal))) {
                //viene salvato il tipo di divisione usata
                typeDivisionSelected = Expense.EQUAL_DIVISION;

                //vengono aggiunti tutti gli utenti selezionati con l'importo da dividere, nell'array debitors
                for (int i = 0; i < group.getMembers().size(); i++) {
                    View view = debitorEqualDivisionNewExpenseRecyclerView.getChildAt(i);
                    CheckBox selectedDebitorEqualNewExpenseCheckBox = view.findViewById(R.id.selectedDebitorEqualNewExpenseCheckBox);
                    TextView uidDebitorEqualNewExpenseTextView = view.findViewById(R.id.uidDebitorEqualNewExpenseTextView);

                    if (selectedDebitorEqualNewExpenseCheckBox.isChecked()) {
                        Payer p = new Payer(uidDebitorEqualNewExpenseTextView.getText().toString(), "");
                        debitors.add(p);
                    }
                }

                //viene diviso l'importo in modo equo
                debitors = MoneyDivider.equalDivision(debitors, amountPayment, mAuth.getUid());

            }
            //se la visisione è per persona
            else {
                //viene salvato il tipo di divisione usata
                typeDivisionSelected = Expense.PERSON_DIVISION;

                //variabile che conta il valore dei debiti di ciascun membro del gruppo
                double amountDebts = 0.0;

                //vengono aggiunti tutti gli utenti selezionati con l'importo da dividere, nell'array debitors
                for (int i = 0; i < group.getMembers().size(); i++) {
                    View view = debitorDisequalDivisionNewExpenseRecyclerView.getChildAt(i);
                    CheckBox selectedDebitorByPersonNewExpenseCheckBox = view.findViewById(R.id.selectedDebitorByPersonNewExpenseCheckBox);
                    TextView uidDebitorByPersonNewExpenseTextView = view.findViewById(R.id.uidDebitorByPersonNewExpenseTextView);
                    TextView valueDebtByPersonNewExpenseEditText = view.findViewById(R.id.valueDebtByPersonNewExpenseEditText);

                    if (selectedDebitorByPersonNewExpenseCheckBox.isChecked()) {

                        //se il campo relativo al debito in corrispondenza di un membro selzionato è vuoto, viene segnalato un errore
                        if (valueDebtByPersonNewExpenseEditText.getText().toString().trim().isEmpty() || valueDebtByPersonNewExpenseEditText.getText().toString().equals(".")) {
                            valueDebtByPersonNewExpenseEditText.setError(getString(R.string.title_insert_amount_debt));
                            invalidFields = true;
                            return invalidFields;
                        }

                        //viene convertito il valore inserito in un double
                        double valueDebt = Double.valueOf(valueDebtByPersonNewExpenseEditText.getText().toString());
                        amountDebts += valueDebt;
                        Payer p = new Payer(uidDebitorByPersonNewExpenseTextView.getText().toString(), String.format("%.2f", valueDebt).replace(",", "."));
                        debitors.add(p);
                    }
                }

                //se la somma dei debiti segnata è diversa da quanto è stata pagata la spesa, viene segnalato un errore
                if (amountDebts != amountPayment) {
                    errorDivisionNewExpenseTextView.setVisibility(View.VISIBLE);
                    invalidFields = true;
                    return invalidFields;
                }
            }

            //se non è stato selzionato nessun debitore, viene segnalato un errore
            if (debitors.size() == 0) {
                Snackbar.make(findViewById(R.id.dateNewExpenseConstraintLayout), getString(R.string.title_select_almost_debitor), Snackbar.LENGTH_LONG).show();
                invalidFields = true;
                return invalidFields;
            }

        }
        return invalidFields;
    }

    private void loadGroupsSpinner() {

        final DatabaseReference reffUsers = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid()).child("mygroups");
        final DatabaseReference reffGruops = FirebaseDatabase.getInstance().getReference().child("groups");

        final ArrayList<String> myGroups = new ArrayList<>();
        final ArrayList<KeyValueItem> groups = new ArrayList<>();

        reffUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*lettura dei dati sull'utente per reperire la lista dei gruppi in cui e`*/
                groups.clear();
                myGroups.clear();

                for (DataSnapshot idGroup : dataSnapshot.getChildren()) {
                    myGroups.add(idGroup.getKey());
                }

                Log.w("letturaGruppo", myGroups.toString());

                for (String idGroup : myGroups) {

                    Log.w("letturaGruppo", reffGruops.toString());
                    Log.w("letturaGruppo", idGroup);

                    reffGruops.child(idGroup).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            try {
                                String idGroup = (String) dataSnapshot.child(Group.ID_GROUP).getValue();
                                String nameGroup = (String) dataSnapshot.child(Group.NAME_GROUP).getValue();
                                boolean active = (boolean) dataSnapshot.child(Group.ACTIVE).getValue();

                                /* viene controllato se l'id del gruppo letto è una nuova lettura (in tal caso alreadyRead = -1) o è una modifica di un gruppo gia letto (alreadyRead = id del gruppo)*/
                                int alreadyRead = containsUidGroupKeyValue(groups, idGroup);
                                if (alreadyRead == -1) {

                                    //se il gruppo è attivo lo aggiunge
                                    if (active) {
                                        groups.add(new KeyValueItem(idGroup, nameGroup));
                                    }

                                } else {
                                    //viene sostituito il gruppo modificato
                                    groups.remove(alreadyRead);
                                    if (active) {
                                        groups.add(alreadyRead, new KeyValueItem(idGroup, nameGroup));
                                    }
                                }

                                i++;

                                /* viene caricato il menu a tendina (spinner) con i gruppi */
                                KeyValueAdapter adapter = new KeyValueAdapter(NewExpenseActivity.this,
                                        R.layout.card_groups_spinner, groups);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                groupNewExpenseSpinner.setAdapter(adapter);

                                /* se l'activity è stata aperta dall'inteno del dettaglio gruppo e sono stati letti tutti i gruppi */
                                if (i == myGroups.size() && group.getIdGroup() != null) {
                                    KeyValueItem item = new KeyValueItem(group.getIdGroup(), group.getNameGroup());
                                    int pos = groups.indexOf(item);
                                    //viene selezionato come gruppo, quello in cui si era
                                    groupNewExpenseSpinner.setSelection(pos);
                                }
                            } catch (NullPointerException e) {
                                //errore nella lettura
                                Log.w("debug", e.toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), R.string.error_db, Toast.LENGTH_LONG).show();
                        }
                    });
                }


                groupNewExpenseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        group = new Group();

                        try {
                            group.setIdGroup(groups.get(pos).getKey());
                            group.setNameGroup(groups.get(pos).getValue());
                            loadMembersGroup(groups.get(pos).getKey());
                        }catch (Exception e) {
                            Log.w("test",e.toString());
                        }
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), R.string.error_db, Toast.LENGTH_LONG).show();
            }
        });

    }


    private void loadMembersGroup(String idGroup) {
        final DatabaseReference reffGroup = FirebaseDatabase.getInstance().getReference().child("groups").child(idGroup).child("uidMembers");
        final DatabaseReference reffMembers = FirebaseDatabase.getInstance().getReference().child("users");

        final ArrayList<String> uidMembers = new ArrayList<>();
        final ArrayList<User> payers = new ArrayList<>(); //lista utilizzata per visualizzare i membri del gruppo tranne se stesso

        reffGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                /*lettura dei dati sull'utente per reperire la lista dei gruppi in cui e`*/
                uidMembers.clear();
                group.getMembers().clear();
                payers.clear();

                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    uidMembers.add((String) id.getValue());
                }

                for (String id : uidMembers) {

                    Log.i("readUidMember", id);

                    reffMembers.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            try {
                                int indexChanged = 0;

                                /* viene controllato se l'id dell'utente letto è una nuova lettura (in tal caso alreadyRead = -1) o è una modifica di un utente gia letto (alreadyRead = id dell'utente)*/
                                int alreadyRead = group.containsUidMember(dataSnapshot.getValue(User.class).getUid());
                                if (alreadyRead == -1) {
                                    group.getMembers().add(dataSnapshot.getValue(User.class));  //utenti visualizzati tra quelli per la divisione
                                    indexChanged = group.getMembers().size() - 1;
                                } else {
                                    //viene sostituito l'utente modificato
                                    group.getMembers().remove(alreadyRead);
                                    group.getMembers().add(alreadyRead, dataSnapshot.getValue(User.class));
                                    indexChanged = alreadyRead;
                                }

                                //verifico se l'utente letto sia l'utente attualmente loggato
                                if (dataSnapshot.getValue(User.class).getUid().equals(mAuth.getUid())) {
                                    indexLoggedUser = indexChanged;
                                    if (dataSnapshot.getValue(User.class).getPicture() != null) {
                                        imgMePayerNewExpenseImageView.setPadding(8, 8, 8, 8);
                                        Picasso.get().load(dataSnapshot.getValue(User.class).getPicture()).fit().centerInside().transform(new CircleTrasformation()).into(imgMePayerNewExpenseImageView);
                                    }
                                }

                                //payers sara un vettore uguale a quello dei membri senza l'utente attualmente loggato
                                payers.clear();
                                payers.addAll(group.getMembers());

                                try {
                                    if (indexLoggedUser != -1) {
                                        payers.remove(indexLoggedUser);
                                    }
                                } catch (Exception e) {
                                    Log.w("test", e.toString());
                                }

                                payersAdapter = new PayerNewExpenseAdapter(payers, NewExpenseActivity.this);
                                payerNewExpenseRecyclerView.setLayoutManager(new LinearLayoutManager(NewExpenseActivity.this));
                                payerNewExpenseRecyclerView.addItemDecoration(new DividerItemDecorator(getDrawable(R.drawable.divider)));
                                payerNewExpenseRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                payerNewExpenseRecyclerView.setAdapter(payersAdapter);

                                equalDivisionAdapter = new DebitorEqualDivisionAdapter(group.getMembers(), NewExpenseActivity.this);
                                debitorEqualDivisionNewExpenseRecyclerView.setLayoutManager(new LinearLayoutManager(NewExpenseActivity.this));
                                debitorEqualDivisionNewExpenseRecyclerView.addItemDecoration(new DividerItemDecorator(getDrawable(R.drawable.divider)));
                                debitorEqualDivisionNewExpenseRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                debitorEqualDivisionNewExpenseRecyclerView.setAdapter(equalDivisionAdapter);

                                disequalDivisionAdapter = new DebitorDisequalDivisionAdapter(group.getMembers(), NewExpenseActivity.this);
                                debitorDisequalDivisionNewExpenseRecyclerView.setLayoutManager(new LinearLayoutManager(NewExpenseActivity.this));
                                debitorDisequalDivisionNewExpenseRecyclerView.addItemDecoration(new DividerItemDecorator(getDrawable(R.drawable.divider)));
                                debitorDisequalDivisionNewExpenseRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                debitorDisequalDivisionNewExpenseRecyclerView.setAdapter(disequalDivisionAdapter);
                            } catch (NullPointerException e) {
                                //errore lettura
                                Log.w("debug", e.toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), R.string.error_db, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), R.string.error_db, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void checkPermissionReadExternalStorage() {

        if (ContextCompat.checkSelfPermission(NewExpenseActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(NewExpenseActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(NewExpenseActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            /* Apro la galleria per selezionare la foto */
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*viene caricata l'immagine scelta dalla galleria nell image view*/
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            filePathReceipt = data.getData();
            imgAddReceiptNewExpenseImageView.setPadding(8, 8, 8, 8);
            Picasso.get().load(filePathReceipt).fit().centerInside().transform(new CircleTrasformation()).into(imgAddReceiptNewExpenseImageView);
            titleAddReceiptNewExpenseTextView.setText(getString(R.string.title_receipt_added));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    /* Apro la galleria per selezionare la foto */
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                } else {
                    Toast.makeText(NewExpenseActivity.this, "E'necessario dare il permesso per poter caricare la foto", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private int containsUidGroupKeyValue(ArrayList<KeyValueItem> groups, String idGroup) {
        int i = 0;
        for (KeyValueItem g : groups) {
            if (g.getKey().equals(idGroup)) {
                return i;
            }
            i++;
        }
        if (i == groups.size()) {
            i = -1;
        }
        return -1;
    }

    //controlla se una stringa contenente un numero ha piu di 2 nuemri dopo il punto
    private boolean isMaxTwoDecimalPlaces(String decimal) {
        if (decimal.contains(".")) {
            String decimalPart = decimal.substring(decimal.lastIndexOf(".") + 1);
            if (decimalPart.length() > 2) {
                return false;
            }
        }
        return true;
    }

    //controlla se un debitore è anche creditore e restituisce quanto ha pagato nella spesa oppure null se non lo è
    private String isDebitorAlsoCreditor(String uidDebitor, ArrayList<Payer> creditors) {
        for (Payer p : creditors) {
            if (p.getIdUser().equals(uidDebitor)) {
                return p.getAmount();
            }
        }
        return null;
    }

    //controlla se un creditore è anche debitore e restituisce quanto ha di debito oppure null se non lo è
    private String isCreditoreAlsoDebitor(String uidCreditor, ArrayList<Payer> debitors) {
        for (Payer p : debitors) {
            if (p.getIdUser().equals(uidCreditor)) {
                return p.getAmount();
            }
        }
        return null;
    }


}
