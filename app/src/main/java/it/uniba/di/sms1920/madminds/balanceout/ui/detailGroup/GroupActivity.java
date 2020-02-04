package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import it.uniba.di.sms1920.madminds.balanceout.MainActivity;
import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.Reminder;
import it.uniba.di.sms1920.madminds.balanceout.model.User;
import it.uniba.di.sms1920.madminds.balanceout.ui.expense.NewExpenseActivity;


public class GroupActivity extends AppCompatActivity {

    public final static int GROUP_CANCELLED = 13;
    public final static int EXPENSE_ADDED = 29;
    public final static int EXPENSE_CANCELLED = 36;
    private FirebaseAuth mAuth;
    private DatabaseReference reffGroup;
    private DatabaseReference reffUsers;
    private boolean isLogged;
    private TabGroupAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Group group;
    private Menu menu;
    private ImageView imgGroupToolbar;
    private TextView dateCreationGroupTextView;


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

        group = new Group();

        //vengono letti i dati sul gruppo dall'intent precedente
        group.setIdGroup(getIntent().getStringExtra(Group.ID_GROUP));
        group.setNameGroup(getIntent().getStringExtra(Group.NAME_GROUP));
        group.setImgGroup(getIntent().getStringExtra(Group.IMG_GROUP));
        group.setCreationDataGroup(getIntent().getStringExtra(Group.CREATION_DATA_GROUP));

        //* viene modificata la toolbar con il nome del gruppo *//
        getSupportActionBar().setTitle(group.getNameGroup());

        imgGroupToolbar = findViewById(R.id.imgGroupToolbar);
        dateCreationGroupTextView = findViewById(R.id.dateCreationGroupTextView);
        dateCreationGroupTextView.setText(getString(R.string.title_created_on) + ": " + group.getCreationDataGroup());

        //la data di creazione viene ofuscanta se l'appbar viene alzata
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.groupAppBarLayout);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                dateCreationGroupTextView.setAlpha(1.0f - Math.abs(verticalOffset / (float)
                        appBarLayout.getTotalScrollRange()));
            }
        });

        if (isLogged) {

            //viene visualizzata l'immagine del gruppo nella toolbar
            Picasso.get().load(group.getImgGroup()).fit().centerCrop().into(imgGroupToolbar, new Callback() {
                @Override
                public void onSuccess() {
                    imgGroupToolbar.setAlpha(190);
                    imgGroupToolbar.setBackgroundColor(Color.BLACK);
                }

                @Override
                public void onError(Exception e) {
                }
            });

            reffGroup = FirebaseDatabase.getInstance().getReference().child(Group.GROUPS).child(group.getIdGroup());
            reffUsers = FirebaseDatabase.getInstance().getReference().child(User.USERS);


            reffGroup.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    group.setNameGroup(dataSnapshot.child(Group.NAME_GROUP).getValue(String.class));
                    group.setCreationDataGroup(dataSnapshot.child(Group.CREATION_DATA_GROUP).getValue(String.class));
                    group.setIdGroup(dataSnapshot.child(Group.ID_GROUP).getValue(String.class));
                    group.setIdAdministrator(dataSnapshot.child(Group.ID_ADMINISTRATOR).getValue(String.class));
                    group.setImgGroup(dataSnapshot.child(Group.IMG_GROUP).getValue(String.class));
                    group.setActive((boolean) dataSnapshot.child(Group.ACTIVE).getValue());

                    Log.w("test", "State group: " + group.isActive());
                    //se il gruppo viene eliminato, torna indietro
                    if (!group.isActive()) {
                        setResult(GROUP_CANCELLED);
                        finish();
                    }

                    //vengono modificati gli elementi del layout ad ogni cambiamento
                    dateCreationGroupTextView.setText(getString(R.string.title_created_on) + ": " + group.getCreationDataGroup());
                    Picasso.get().load(group.getImgGroup()).fit().centerCrop().into(imgGroupToolbar);
                    getSupportActionBar().setTitle(group.getNameGroup());

                    for (DataSnapshot ds : dataSnapshot.child(Group.UID_MEMEBRS).getChildren()) {
                        group.addUidMembers(ds.getValue(String.class));
                    }

                    Log.w("pippo", group.toString());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            group.setIdAdministrator(MainActivity.DEFAULT_ID_USER);
            group.setAmountDebit("9.00");
            group.setStatusDebitGroup(-1);
        }


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabGroupAdapter(getSupportFragmentManager());
        adapter.addFragment(new OverviewGroupFragment(group), getString(R.string.title_overview));
        adapter.addFragment(new ExpensesGroupFragment(group), getString(R.string.title_expense));
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
                    startActivityForResult(newExpense, EXPENSE_ADDED);
                }
            }
        });
    }

    private void verifyLogged() {
        /* firebaseUser contiene l'informazione relativa all'utente se è loggato o meno */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        /* memorizzo in isLogged l'informazione boolean relativa all'utente se è loggato o meno*/
        if (firebaseUser == null) {
            isLogged = false;
        } else {
            isLogged = true;
        }
    }

    public void bottomNavigationViewClick() {
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

        if (!group.getIdAdministrator().equals(idUser)) {
            menu.removeItem(R.id.advancedGroupMenuButton);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // gestisce i click sugli elementi della barra delle azioni
        int id = item.getItemId();

        /* Se l'utente non  è loggato non può fare alcuna operazione nel menu */
        if (isLogged == false) {
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

                    //viene controllato se l'utente è in debito e in  tal caso non può lasciare il gruppo
                    if (group.getStatusDebitGroup() != 0) {
                        Snackbar.make(findViewById(R.id.gruopActivityConstraintLayout), getString(R.string.title_error_leave_group_debt), Snackbar.LENGTH_LONG).show();
                    } else
                        //viene controllato se l'utente è l'amministratore e ci sono altri membri oltre lui nel gruppo e in tal caso non può lasciare il gruppo
                        if (group.getIdAdministrator().equals(mAuth.getUid()) && group.getUidMembers().size() > 1) {
                            Snackbar.make(findViewById(R.id.gruopActivityConstraintLayout), getString(R.string.title_error_leave_group_admin), Snackbar.LENGTH_LONG).show();
                        } else {
                            //viene abbandonato il gruppo
                            new MaterialAlertDialogBuilder(GroupActivity.this)
                                    .setTitle(getString(R.string.title_exit_group))
                                    .setMessage(getString(R.string.message_exit_group))
                                    .setPositiveButton(getString(R.string.title_yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            leaveGroup();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.title_no), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    break;
                case R.id.advancedGroupMenuButton:
                    Intent intentAdvanced = new Intent(GroupActivity.this, AdvancedSettingsGroupActivity.class);
                    intentAdvanced.putExtra(Group.ID_GROUP, group.getIdGroup());
                    startActivityForResult(intentAdvanced, GROUP_CANCELLED);
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void leaveGroup() {

        final DatabaseReference reffReminders = FirebaseDatabase.getInstance().getReference().child(Reminder.REMINDERS);

        reffGroup.child(Group.UID_MEMEBRS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.getValue().toString().equals(mAuth.getUid())) {
                        String keyMember = ds.getKey();
                        reffUsers.child(mAuth.getUid()).child(User.MY_GROUPS).child(group.getIdGroup()).removeValue();
                        reffGroup.child(Group.UID_MEMEBRS).child(keyMember).removeValue();

                        //cancello anche i promemoria del gruppo
                        reffReminders.child(group.getIdGroup()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot idReminder: dataSnapshot.getChildren()) {
                                    String key = idReminder.getKey();
                                    String uidCreditor = idReminder.child(Reminder.UID_CREDITOR).getValue(String.class);
                                    String uidDebitor = idReminder.child(Reminder.UID_CREDITOR).getValue(String.class);

                                    if(uidCreditor.equals(mAuth.getUid()) || uidDebitor.equals(mAuth.getUid())) {
                                        reffReminders.child(group.getIdGroup()).child(key).removeValue();
                                    }
                                }
                                setResult(MainActivity.EXIT_GROUP);
                                finish();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case EXPENSE_ADDED:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(findViewById(R.id.viewPager), getString(R.string.expence_added), Snackbar.LENGTH_LONG).show();
                }
                break;
            case EXPENSE_CANCELLED:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(findViewById(R.id.viewPager), getString(R.string.title_expense_cancelled), Snackbar.LENGTH_LONG).show();
                    adapter = new TabGroupAdapter(getSupportFragmentManager());
                    adapter.addFragment(new OverviewGroupFragment(group), getString(R.string.title_overview));
                    adapter.addFragment(new ExpensesGroupFragment(group), getString(R.string.title_expense));
                    viewPager.setAdapter(adapter);
                    tabLayout.setupWithViewPager(viewPager);
                    tabLayout.selectTab(tabLayout.getTabAt(1));
                }
                break;
            case GROUP_CANCELLED:
                if (resultCode == RESULT_OK) {
                    setResult(GROUP_CANCELLED);
                    finish();
                }
        }
    }

}
