package it.uniba.di.sms1920.madminds.balanceout.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;

import it.uniba.di.sms1920.madminds.balanceout.MainActivity;
import it.uniba.di.sms1920.madminds.balanceout.NewGroupActivity;
import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;

public class HomeFragment extends Fragment {

    private RecyclerView groupsRecyclerView;
    private GroupAdapter groupAdapter;
    private ArrayList<Group> groups;
    private FirebaseAuth mAuth;
    private boolean isLogged;
    private boolean isEmailVerified;

    private ImageView helpCardImageView;
    private SwipeRefreshLayout homeSwipeRefresh;

    private FloatingActionButton homeExpandableFab, createGroupFab, joinGroupFab;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock, text_fab_open, text_fab_close;
    private MaterialCardView descriptionCreateGroupFabTextView, descriptionJoinGroupFabTextView;
    private boolean isOpenFab = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();

        /* vengono mostrati 2 layout diversi a seconda se l'utente ha verificato l'account tramite mail o no*/
        final View root;
        if(!isLogged) {
            root = homeFragment(inflater, container);
        } else {
            if (isEmailVerified) {
                root = homeFragment(inflater, container);
            } else {
                root = notEmailVerificatedHomeFragment(inflater, container);
            }

        }

        return root;
    }

    private View notEmailVerificatedHomeFragment (LayoutInflater inflater, final ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_not_email_verificated, container, false);
        MaterialButton emailIntentButton = root.findViewById(R.id.emailIntentButton);
        final BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);

        emailIntentButton.setOnClickListener(new MaterialButton.OnClickListener() {
            @Override
            public void onClick(View v){

                /* Intent che apre la casella di posta elettronica */
                Intent intent = Intent.makeMainSelectorActivity(
                        Intent.ACTION_MAIN,
                        Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);       //la posta elettronica viene aperta separatamente rispetto all'app Balance Out
                startActivity(intent);
            }
        });
        return root;
    }

    private View homeFragment(LayoutInflater inflater, final ViewGroup container)  {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        /* vengono inizializzate tutte le view nel fragment*/
        inizializeViews(root);

        groups = new ArrayList<>();

        /* vengono caricati tutti i gruppi nella recycle view */
        loadGroups();

        /* messaggio di aiuto per comprendere il significato della card relativa a stato debiti/crediti*/
        helpCardImageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v){
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(getString(R.string.title_help_status_debit))
                        .setMessage(getString(R.string.text_help_status_debit))
                        .setPositiveButton(getString(R.string.understand), null)
                        .show();
            }
        });

        /* listener in ascolto dei clic sui bottone per creare i gruppi o per unirsi a gruppi esistenti */
        homeFabClicked(root);
        createGroupFabClicked();
        joinGroupFabClicked();


        /* quando viene ricaricata la pagina con uno swipe down, vengono ricaricati tutti i gruppi*/
        homeSwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadGroups();
                    }
                }
        );

        return root;
    }

    private void inizializeViews(View root) {
        groupsRecyclerView = root.findViewById(R.id.groupsHomeRecyclerView);
        helpCardImageView = root.findViewById(R.id.helpCardImageView);
        homeSwipeRefresh = root.findViewById(R.id.homeSwipeRefresh);
        homeExpandableFab = root.findViewById(R.id.homeExpandableFab);
        createGroupFab = root.findViewById(R.id.createGroupFab);
        joinGroupFab = root.findViewById(R.id.joinGroupFab);
        descriptionCreateGroupFabTextView = root.findViewById(R.id.descriptionCreateGroupFabTextView);
        descriptionJoinGroupFabTextView = root.findViewById(R.id.descriptionJoinGroupFabTextView);

        /* animazioni per l'espansione del bottone per aggiungere i gruppi */
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_anticlock);
        text_fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.text_fab_open);
        text_fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.text_fab_close);
    }

    private void joinGroupFabClicked() {
        joinGroupFab.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), getString(R.string.title_join_group), Toast.LENGTH_LONG).show();
                //TODO inserire una nuova activity per entare in un gruppo esistente
            }
        });
    }

    private void createGroupFabClicked() {
        createGroupFab.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), getString(R.string.title_create_group), Toast.LENGTH_LONG).show();
                //TODO inserire una nuova activity per creare un nuovo gruppo
                Intent newGroup = new Intent(getActivity(), NewGroupActivity.class);
                startActivity(newGroup);
            }
        });
    }

    private void homeFabClicked(final View root) {
        /* quando viene premuto il bottone pe raggiungere i gruppi */
        homeExpandableFab.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v){
                /* viene visualizzato un messaggio di errore se l'utente non è loggato */
                if(!isLogged) {
                    Snackbar.make(root, getString(R.string.not_logged_message_add_group), Snackbar.LENGTH_LONG).show();
                } else {

                    /* altrimenti il bottone viene esploso (con le animazioni) e vengono visualizzati i bottoni per unirsi e creare un gruppo */
                    if (isOpenFab) {

                        descriptionCreateGroupFabTextView.startAnimation(text_fab_close);
                        descriptionJoinGroupFabTextView.startAnimation(text_fab_close);
                        joinGroupFab.startAnimation(fab_close);
                        createGroupFab.startAnimation(fab_close);
                        homeExpandableFab.startAnimation(fab_anticlock);
                        joinGroupFab.setClickable(false);
                        createGroupFab.setClickable(false);

                        isOpenFab = false;
                    } else {

                        descriptionCreateGroupFabTextView.setVisibility(View.VISIBLE);
                        descriptionJoinGroupFabTextView.setVisibility(View.VISIBLE);
                        descriptionCreateGroupFabTextView.startAnimation(text_fab_open);
                        descriptionJoinGroupFabTextView.startAnimation(text_fab_open);
                        joinGroupFab.setVisibility(View.VISIBLE);
                        createGroupFab.setVisibility(View.VISIBLE);
                        joinGroupFab.startAnimation(fab_open);
                        createGroupFab.startAnimation(fab_open);
                        homeExpandableFab.startAnimation(fab_clock);
                        joinGroupFab.setClickable(true);
                        createGroupFab.setClickable(true);
                        isOpenFab = true;
                    }
                }
            }
        });
    }

    public void loadGroups() {
        /* la lista viene pulita poiche altrimenti ogni volta ce si ricarica la pagina
        *  verrebbero aggiunti gli stessi gruppi */
        groups.clear();

        if(!isLogged) {
            /*creazione di un gruppo di esempio visibile solo quando l'utente non è loggato*/
            groups.add(new Group(null, getString(R.string.example_name_group),
                    Calendar.getInstance().getTime(),
                    null,
                    null,
                    null,
                    MainActivity.DEFAULT_ID_USER,
                    -1,
                    -9.00,
                    true
            ));
        } else {
            //TODO lettura da db dei gruppi
        }

        groupAdapter = new GroupAdapter(groups,isLogged, getActivity());

        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        groupsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        groupsRecyclerView.setAdapter(groupAdapter);

        homeSwipeRefresh.setRefreshing(false);
    }

    private void verifyLogged() {
        /* firebaseUser contiene l'informazione relativa all'utente se è loggato o meno */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        /* memorizzo in isLogged l'informazione boolean relativa all'utente se è loggato o meno*/
        if(firebaseUser == null) {
            isLogged = false;
            isEmailVerified = false;
        } else {
            isLogged = true;
            isEmailVerified = firebaseUser.isEmailVerified();
        }
    }
}