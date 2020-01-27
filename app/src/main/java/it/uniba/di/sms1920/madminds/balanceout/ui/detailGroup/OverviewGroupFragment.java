package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import it.uniba.di.sms1920.madminds.balanceout.MainActivity;
import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.DividerItemDecorator;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.Movement;
import it.uniba.di.sms1920.madminds.balanceout.model.User;


public class OverviewGroupFragment extends Fragment {
    private FirebaseAuth mAuth;
    private boolean isLogged;
    private SwipeRefreshLayout overviewGroupSwipeRefresh;
    private RecyclerView movementsGroupRecyclerView;
    private ImageView helpCardGroupImageView;
    private ArrayList<Movement> movements;
    private MovementAdapter movementAdapter;
    private ImageView imgCardStatusDebitGroupImageView;
    private TextView subtitleCardStatusDebitGroupTextView;
    private Group group;


    /*viene passato come parametro il gruppo che viene visualizzato nell'activity*/
    public OverviewGroupFragment(Group group) {
        this.group = group;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_overview_group, container, false);

        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();

        movementsGroupRecyclerView = root.findViewById(R.id.movementsGroupRecyclerView);
        helpCardGroupImageView = root.findViewById(R.id.helpCardGroupImageView);
        overviewGroupSwipeRefresh = root.findViewById(R.id.overviewGroupSwipeRefresh);
        imgCardStatusDebitGroupImageView = root.findViewById(R.id.imgCardStatusDebitGroupImageView);
        subtitleCardStatusDebitGroupTextView = root.findViewById(R.id.subtitleCardStatusDebitGroupTextView);

        movements = new ArrayList<>();

        /* vengono caricati tutti i movimenti nella recycle view */
        loadMovements();

        /*viene determinato lo stato di debito/credito nel gruppo*/
        checkStatusGroup(root);

        /* messaggio di aiuto per comprendere il significato della card relativa a stato debiti/crediti*/
        helpCardGroupImageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v){
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(getString(R.string.title_help_status_debit))
                        .setMessage(getString(R.string.text_help_status_debit_group))
                        .setPositiveButton(getString(R.string.understand), null)
                        .show();
            }
        });

        /* quando viene ricaricata la pagina con uno swipe down, vengono ricaricati tutti i movimenti*/
        overviewGroupSwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadMovements();
                        checkStatusGroup(root);
                    }
                }
        );

        return root;
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

    private void loadMovements() {
        /* la lista viene pulita poiche altrimenti ogni volta ce si ricarica la pagina
         *  verrebbero aggiunti gli stessi movimenti */
        movements.clear();

        if (!isLogged) {
            /*creazione di movimenti di esempio visibili solo quando l'utente non è loggato*/
            movements.add(new Movement(
                    new User(MainActivity.DEFAULT_ID_USER, "Mario", "Rossi", null, null),
                    new User("2", "Giorgio", "Pani", null, null),
                    3.00
            ));
            movements.add(new Movement(
                    new User(MainActivity.DEFAULT_ID_USER, "Mario", "Rossi", null, null),
                    new User("3", "Luca", "De Giorgio", null, null),
                    6.00
            ));
        } else {
            //TODO lettura da db dei movimenti
        }

        movementAdapter = new MovementAdapter(movements, isLogged, getActivity());

        movementsGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        movementsGroupRecyclerView.addItemDecoration(new DividerItemDecorator(getContext().getDrawable(R.drawable.divider)));
        movementsGroupRecyclerView.setItemAnimator(new DefaultItemAnimator());
        movementsGroupRecyclerView.setAdapter(movementAdapter);

        overviewGroupSwipeRefresh.setRefreshing(false);

    }

    private void checkStatusGroup(View root) {

        /* viene letto l'importo del debito che si ha nel gruppo */
        double status = group.getAmountDebit();

        /* viene modificata la card dello stato in base al debito che si ha */
        if (group.getStatusDebitGroup() > 0) {
            imgCardStatusDebitGroupImageView.setBackgroundResource(R.drawable.credit);
            subtitleCardStatusDebitGroupTextView.setText(root.getResources().getString(R.string.value_status_credit_group)+" "+status+"€.");
        } else if (group.getStatusDebitGroup() < 0) {
            imgCardStatusDebitGroupImageView.setBackgroundResource(R.drawable.debit);
            subtitleCardStatusDebitGroupTextView.setText(root.getResources().getString(R.string.value_status_debit_group)+" "+status*-1+"€.");
        } else {
            imgCardStatusDebitGroupImageView.setBackgroundResource(R.drawable.equal);
            subtitleCardStatusDebitGroupTextView.setText(root.getResources().getString(R.string.status_parity));
        }
    }

}