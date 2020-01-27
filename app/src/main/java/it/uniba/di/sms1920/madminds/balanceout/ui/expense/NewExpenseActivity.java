package it.uniba.di.sms1920.madminds.balanceout.ui.expense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.KeyValueItem;
import it.uniba.di.sms1920.madminds.balanceout.model.User;

public class NewExpenseActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Spinner groupNewExpenseSpinner;
    Group group;

    /*contatore per contare i dati letti*/
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

        //vengono caricati i gruppi nello spinner
        loadGroupsSpinner();


    }

    private void loadGroupsSpinner() {

        final DatabaseReference reffUsers = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid()).child("mygroups");
        final DatabaseReference reffGruops = FirebaseDatabase.getInstance().getReference().child("groups");

        final ArrayList<String> myGroups = new ArrayList<>();
        final ArrayList<KeyValueItem> groups = new ArrayList<>();

        reffUsers.addListenerForSingleValueEvent(new ValueEventListener() {
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

                    reffGruops.child(idGroup).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            groups.add(new KeyValueItem(dataSnapshot.getValue(Group.class).getIdGroup(), dataSnapshot.getValue(Group.class).getNameGroup()));

                            i++;

                            KeyValueAdapter adapter = new KeyValueAdapter(NewExpenseActivity.this,
                                    android.R.layout.simple_spinner_item, groups);

                            //KeyValueAdapter adapter = new KeyValueAdapter(NewExpenseActivity.this, android.R.layout.simple_spinner_item, android.R.id.text1, groups);
                            // Create an ArrayAdapter using the string array and a default spinner layout
                            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewExpenseActivity.this, android.R.layout.simple_spinner_item, myGroups);
                            // Specify the layout to use when the list of choices appears
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            // Apply the adapter to the spinner
                            groupNewExpenseSpinner.setAdapter(adapter);

                            /* se l'activity è stata aperta dall'inteno del dettaglio gruppo */
                            if (i == myGroups.size() && group.getIdGroup() != null) {
                                KeyValueItem item = new KeyValueItem(group.getIdGroup(), group.getNameGroup());
                                int pos = groups.indexOf(item);
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
                        Toast.makeText(NewExpenseActivity.this,groups.get(pos).getKey()+" "+groups.get(pos).getValue(),Toast.LENGTH_LONG).show();
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
        final ArrayList<User> members = new ArrayList<>();

        reffGroup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*lettura dei dati sull'utente per reperire la lista dei gruppi in cui e`*/
                uidMembers.clear();
                members.clear();

                for(DataSnapshot id : dataSnapshot.getChildren()) {
                    uidMembers.add((String) id.getValue());
                }

                for(String id: uidMembers) {


                    reffMembers.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            members.add(dataSnapshot.getValue(User.class));

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
