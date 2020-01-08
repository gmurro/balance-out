package it.uniba.di.sms1920.madminds.balanceout.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;

import it.uniba.di.sms1920.madminds.balanceout.R;

public class HomeFragment extends Fragment {

    private RecyclerView groupsRecyclerView;
    private GroupAdapter groupAdapter;
    private ArrayList<Group> groups;
    private FirebaseAuth mAuth;
    private boolean isLogged;

    private ImageView helpCardImageView;
    private SwipeRefreshLayout homeSwipeRefresh;

    private FloatingActionButton homeExpandableFab, createGroupFab, joinGroupFab;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock, text_fab_open, text_fab_close;
    private MaterialCardView descriptionCreateGroupFabTextView, descriptionJoinGroupFabTextView;
    private boolean isOpenFab = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_home, container, false);
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

        groups = new ArrayList<>();

        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();

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
            groups.add(new Group(getString(R.string.example_name_group),
                    Calendar.getInstance().getTime(),
                    null,
                    -1
            ));
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
        } else {
            isLogged = true;
        }
    }
}