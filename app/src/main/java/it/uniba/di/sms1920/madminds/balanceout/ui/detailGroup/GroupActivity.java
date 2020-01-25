package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.uniba.di.sms1920.madminds.balanceout.MainActivity;
import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;

public class GroupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private boolean isLogged;
    private TabGroupAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Group group;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.groupToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* viene modificata la toolbar con il nome del gruppo */
        group = (Group) getIntent().getExtras().getSerializable(Group.GROUP);
        getSupportActionBar().setTitle(group.getNameGroup());
        getSupportActionBar().setSubtitle(getString(R.string.title_created_on)+" "+group.getCreationDataGroup().toString());

        //TODO AVVALORARE GRUPPO CON DATI DB

        /* funzione che contiene un listener in ascolto per i click sulla bottom navigation view */
        bottomNavigationViewClick();

        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabGroupAdapter(getSupportFragmentManager());
        adapter.addFragment(new OverviewGroupFragment(group), getString(R.string.title_overview));
        adapter.addFragment(new ExpensesGroupFragment(), getString(R.string.title_expense));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addNewExpenseFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLogged) {
                    Snackbar.make(view, getString(R.string.not_logged_message_add_expense), Snackbar.LENGTH_LONG).show();
                } else {
                    //TODO inserire una nuova spesa
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
        if(!group.getIdAmministrator().equals(idUser)) {
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
                    intent.putExtra(Group.GROUP,group);
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
