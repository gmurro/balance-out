package it.uniba.di.sms1920.madminds.balanceout.ui.expense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.DividerItemDecorator;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.KeyValueItem;
import it.uniba.di.sms1920.madminds.balanceout.model.User;

public class NewExpenseActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Spinner groupNewExpenseSpinner, typeDivisionNewExpenseSpinner;
    private Group group;
    private RecyclerView payerNewExpenseRecyclerView;
    private RecyclerView debitorEqualDivisionNewExpenseRecyclerView;
    private RecyclerView debitorDisequalDivisionNewExpenseRecyclerView;
    private ArrayList<User> members;
    private PayerNewExpenseAdapter payersAdapter;
    private DebitorEqualDivisionAdapter equalDivisionAdapter;
    private DebitorDisequalDivisionAdapter disequalDivisionAdapter;

    /*contatore per contare i gruppi letti*/
    private int i=0;

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
        if(getIntent().hasExtra(Group.GROUP)) {
            group = (Group) getIntent().getExtras().getSerializable(Group.GROUP);
        }

        /* viene avvalorato l'utente loggato al momento */
        mAuth = FirebaseAuth.getInstance();

        groupNewExpenseSpinner = findViewById(R.id.groupNewExpenseSpinner);
        typeDivisionNewExpenseSpinner = findViewById(R.id.typeDivisionNewExpenseSpinner);
        payerNewExpenseRecyclerView = findViewById(R.id.payerNewExpenseRecyclerView);
        debitorEqualDivisionNewExpenseRecyclerView = findViewById(R.id.debitorEqualDivisionNewExpenseRecyclerView);
        debitorDisequalDivisionNewExpenseRecyclerView = findViewById(R.id.debitorDisequalDivisionNewExpenseRecyclerView);

        ArrayList<String> typeDivision = new ArrayList<>();
        typeDivision.add(getString(R.string.type_division_equal));
        typeDivision.add(getString(R.string.type_division_disequal));

        ArrayAdapter<String> adapterTypeDivision = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,typeDivision);
        adapterTypeDivision.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeDivisionNewExpenseSpinner.setAdapter(adapterTypeDivision);

        typeDivisionNewExpenseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
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

        //vengono caricati i gruppi nello spinner
        loadGroupsSpinner();

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

                for(DataSnapshot idGroup : dataSnapshot.getChildren()) {
                    myGroups.add(idGroup.getKey());
                }

                Log.w("letturaGruppo", myGroups.toString());

                for(String idGroup: myGroups) {

                    Log.w("letturaGruppo", reffGruops.toString());
                    Log.w("letturaGruppo", idGroup);

                    reffGruops.child(idGroup).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            groups.add(new KeyValueItem(dataSnapshot.getValue(Group.class).getIdGroup(), dataSnapshot.getValue(Group.class).getNameGroup()));

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


                groupNewExpenseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
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

                for(DataSnapshot id : dataSnapshot.getChildren()) {
                    uidMembers.add((String) id.getValue());
                }

                for(String id: uidMembers) {

                    Log.i("readUidMember", id);

                    reffMembers.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            group.getMembers().add(dataSnapshot.getValue(User.class));  //utenti visulaizzati tra quelli per la divisione

                            //verifico che l'utente non sia se stesso
                            if(!dataSnapshot.getValue(User.class).getUid().equals(mAuth.getUid())) {
                                payers.add(dataSnapshot.getValue(User.class));               //membri del gruppo tranne se stesso
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

}
