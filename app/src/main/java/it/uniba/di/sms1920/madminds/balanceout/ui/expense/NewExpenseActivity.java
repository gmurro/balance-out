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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.CircleTrasformation;
import it.uniba.di.sms1920.madminds.balanceout.helper.DividerItemDecorator;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.KeyValueItem;
import it.uniba.di.sms1920.madminds.balanceout.model.User;
import it.uniba.di.sms1920.madminds.balanceout.ui.home.NewGroupActivity;

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
    private TextView dataNewExpenseTextView, titleAddReceiptNewExpenseTextView;
    private Uri filePathReceipt;
    private MaterialButton addExpenseButton;

    /*contatore per contare i gruppi letti*/
    private int i = 0;

    /*variabile che memorizza l'indice dell'array di membri del gruppo in cui è presente l'utente loggato, di default è -1*/
    private int indexLoggedUser = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        Toolbar toolbar = (Toolbar) findViewById(R.id.newExpenseToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        group = new Group();

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

        //vengono caricati i gruppi nello spinner per i gruppi
        loadGroupsSpinner();

        //vengono inizializzati i valori per lo spinner per la divisione
        ArrayList<String> typeDivision = new ArrayList<>();
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

                    }
                }
        );

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

                            /* viene controllato se l'id del gruppo letto è una nuova lettura (in tal caso alreadyRead = -1) o è una modifica di un gruppo gia letto (alreadyRead = id del gruppo)*/
                            int alreadyRead = containsUidGroupKeyValue(groups, dataSnapshot.getValue(Group.class).getIdGroup());
                            if (alreadyRead == -1) {
                                groups.add(new KeyValueItem(dataSnapshot.getValue(Group.class).getIdGroup(), dataSnapshot.getValue(Group.class).getNameGroup()));
                            } else {
                                //viene sostituito il gruppo modificato
                                groups.remove(alreadyRead);
                                groups.add(alreadyRead, new KeyValueItem(dataSnapshot.getValue(Group.class).getIdGroup(), dataSnapshot.getValue(Group.class).getNameGroup()));
                            }

                            i++;

                            /* viene caricato il menu a tendina (spinner) con i gruppi */
                            KeyValueAdapter adapter = new KeyValueAdapter(NewExpenseActivity.this,
                                    android.R.layout.simple_spinner_item, groups);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            groupNewExpenseSpinner.setAdapter(adapter);

                            /* se l'activity è stata aperta dall'inteno del dettaglio gruppo e sono stati letti tutti i gruppi */
                            if (i == myGroups.size() && group.getIdGroup() != null) {
                                KeyValueItem item = new KeyValueItem(group.getIdGroup(), group.getNameGroup());
                                int pos = groups.indexOf(item);
                                //viene selezionato come gruppo, quello in cui si era
                                groupNewExpenseSpinner.setSelection(pos);
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
                        group.setIdGroup(groups.get(pos).getKey());
                        group.setNameGroup(groups.get(pos).getValue());
                        loadMembersGroup(groups.get(pos).getKey());
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

                            int indexChanged = 0;

                            /* viene controllato se l'id dell'utente letto è una nuova lettura (in tal caso alreadyRead = -1) o è una modifica di un utente gia letto (alreadyRead = id dell'utente)*/
                            int alreadyRead = group.containsUidMember(dataSnapshot.getValue(User.class).getUid());
                            if (alreadyRead == -1) {
                                group.getMembers().add(dataSnapshot.getValue(User.class));  //utenti visualizzati tra quelli per la divisione
                                indexChanged = group.getMembers().size()-1;
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
                            if(indexLoggedUser!=-1) {
                                payers.remove(indexLoggedUser);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
        int i=0;
        for (KeyValueItem g: groups) {
            if(g.getKey().equals(idGroup)) {
                return i;
            }
            i++;
        }
        if(i==groups.size()) {
            i=-1;
        }
        return -1;
    }

}
