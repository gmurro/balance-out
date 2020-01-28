package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.uniba.di.sms1920.madminds.balanceout.MainActivity;
import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.ui.expense.NewExpenseActivity;
import it.uniba.di.sms1920.madminds.balanceout.ui.home.GroupAdapter;


public class GroupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference reffGroup;
    private DatabaseReference reffUsers;
    private boolean isLogged;
    private TabGroupAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Group group;
    private String groupName;
    private String dataCreation;
    private String idGroup;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Toolbar toolbar = findViewById(R.id.groupToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        /* funzione che contiene un listener in ascolto per i click sulla bottom navigation view */
        bottomNavigationViewClick();

        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();

        if(isLogged) {
            idGroup = getIntent().getStringExtra(GroupAdapter.ID_GROUP);
            group = new Group();

            reffGroup = FirebaseDatabase.getInstance().getReference().child(Group.GROUPS).child(idGroup);
            reffUsers = FirebaseDatabase.getInstance().getReference().child("users");

            getSupportActionBar().setTitle(group.getNameGroup());
            getSupportActionBar().setSubtitle(getString(R.string.title_created_on)+" "+group.getCreationDataGroup());

            //TODO AVVALORARE GRUPPO CON DATI DB

            reffGroup.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    group.setNameGroup(dataSnapshot.child("nameGroup").getValue(String.class));
                    group.setCreationDataGroup(dataSnapshot.child("creationDataGroup").getValue(String.class));
                    group.setIdGroup(dataSnapshot.child("idGroup").getValue(String.class));
                    group.setIdAdministrator(dataSnapshot.child("idAmministrator").getValue(String.class));

                    for (DataSnapshot ds : dataSnapshot.child("uidMembers").getChildren()) {
                        group.addUidMembers(ds.getValue(String.class));
                    }

                    Log.w("pippo", group.toString());

                    //* viene modificata la toolbar con il nome del gruppo *//*

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //LETTURA DATI COMPLETA DI GRUPPO E UTENTI
            /*reffGroup.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    group = dataSnapshot.getValue(Group.class);



                    Log.w("pippo", group.toString());
                for(String s : group.getUidMembers()) {

                    reffUsers.child(s).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            group.addMember(dataSnapshot.getValue(User.class));

                            Log.w("pippo", group.toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/
        } else {
            group = new Group();
            group.setIdAdministrator(MainActivity.DEFAULT_ID_USER);
        }


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabGroupAdapter(getSupportFragmentManager());
        adapter.addFragment(new OverviewGroupFragment(group), getString(R.string.title_overview));
        adapter.addFragment(new ExpensesGroupFragment(), getString(R.string.title_expense));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);



        FloatingActionButton fab = findViewById(R.id.addNewExpenseFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLogged) {
                    Snackbar.make(view, getString(R.string.not_logged_message_add_expense), Snackbar.LENGTH_LONG).show();
                } else {
                    Intent newExpense = new Intent(GroupActivity.this, NewExpenseActivity.class);
                    newExpense.putExtra(Group.GROUP, group);
                    startActivity(newExpense);
                }
            }
        });
    }

    private void verifyLogged() {
        /* firebaseUser contiene l'informazione relativa all'utente se è loggato o meno */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        /* memorizzo in isLogged l'informazione boolean relativa all'utente se è loggato o meno*/
        if(firebaseUser == null) {
            isLogged = false;
        } else {
            isLogged = true;
        }
    }

    public void bottomNavigationViewClick(){
        BottomNavigationView navView = findViewById(R.id.navViewGroup);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        setResult(MainActivity.START_HOME);
                        finish();
                        break;

                    case R.id.navigation_notifications:
                        setResult(MainActivity.START_NOTIFICATIONS);
                        finish();
                        break;

                    case R.id.navigation_activity:
                        setResult(MainActivity.START_ACTIVITY);
                        finish();
                        break;
                    case R.id.navigation_profile:
                        setResult(MainActivity.START_PROFILE);
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate del menu; questo aggiunge elementi alla barra delle azioni se è presente.
        getMenuInflater().inflate(R.menu.group_menu, menu);
        this.menu = menu;

        /*controllo se l'id user loggato è quello dell'ammistratore del gruppo, altrimenti non visualizzo nel menu l impostazioni avanzate*/
        String idUser = isLogged == false ? MainActivity.DEFAULT_ID_USER : mAuth.getCurrentUser().getUid();
        if(!group.getIdAdministrator().equals(idUser)) {
            menu.removeItem(R.id.advancedGroupMenuButton);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // gestisce i click sugli elementi della barra delle azioni
        int id = item.getItemId();

        /* Se l'utente non  è loggato non può fare alcuna operazione nel menu */
        if(isLogged==false){
            Snackbar.make(findViewById(R.id.gruopActivityConstraintLayout), getString(R.string.not_logged_message_menu), Snackbar.LENGTH_LONG).show();
        } else {

            /*altrimenti viene selezionata l'opzione scelta*/
            switch (id) {
                case R.id.membersGroupMenuButton:
                    Intent intent = new Intent(GroupActivity.this, MembersGroupActivity.class);
                    intent.putExtra(Group.GROUP, group);
                    startActivity(intent);
                    break;
                case R.id.editGroupMenuButton:
                    //TODO activity per modificare il gruppo
                    break;
                case R.id.exitGroupMenuButton:
                    //TODO uscire dal gruppo nel db e controllo se e in debito
                    break;
                case R.id.advancedGroupMenuButton:
                    //TODO activity impostazioni avanzate
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
