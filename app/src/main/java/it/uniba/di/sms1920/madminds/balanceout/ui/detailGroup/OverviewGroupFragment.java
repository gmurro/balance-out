package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

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
    private ArrayList<Movement> movementsToPay;
    private MovementAdapter movementAdapter;
    private ImageView imgCardStatusDebitGroupImageView;
    private TextView subtitleCardStatusDebitGroupTextView;
    private Group group;
    private DatabaseReference movementsReference, usersReference;


    /*viene passato come parametro il gruppo che viene visualizzato nell'activity*/
    public OverviewGroupFragment(Group group) {
        this.group = group;
    }

    public OverviewGroupFragment() {
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

        movementsToPay = new ArrayList<>();

        if(isLogged) {
            movementsReference = FirebaseDatabase.getInstance().getReference().child(Movement.MOVEMENTS).child(group.getIdGroup());
            usersReference = FirebaseDatabase.getInstance().getReference().child(User.USERS);
            /*viene caricato dal db lo stato di debiti/crediti dell'utente all'inteno del gruppo per poter aggiornare la card dello stato*/
            loadStatusData(root);

        }

        /* vengono caricati tutti i movimenti nella recycle view */
        loadMovements();

        /* messaggio di aiuto per comprendere il significato della card relativa a stato debiti/crediti*/
        helpCardGroupImageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        movementsGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        movementsGroupRecyclerView.addItemDecoration(new DividerItemDecorator(getContext().getDrawable(R.drawable.divider)));
        movementsGroupRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /* la lista viene pulita poiche altrimenti ogni volta ce si ricarica la pagina
         *  verrebbero aggiunti gli stessi movimenti */
        movementsToPay.clear();

        if (!isLogged) {
            /*creazione di movimenti di esempio visibili solo quando l'utente non è loggato*/
            movementsToPay.add(new Movement(
                    new User(MainActivity.DEFAULT_ID_USER, "Mario", "Rossi", null, null, null),
                    new User("2", "Giorgio", "Pani", null, null, null),
                    "3.00"
            ));
            movementsToPay.add(new Movement(
                    new User(MainActivity.DEFAULT_ID_USER, "Mario", "Rossi", null, null, null),
                    new User("3", "Luca", "De Giorgio", null, null, null),
                    "6.00"
            ));

            movementAdapter = new MovementAdapter(movementsToPay, isLogged, getActivity(),null, null);
            movementsGroupRecyclerView.setAdapter(movementAdapter);
            overviewGroupSwipeRefresh.setRefreshing(false);
        } else {

            //i movimenti vengono letti e calcolati dal db e messi nell arraylist movementsToPay
            readDataFromDb();
        }

    }


    private void readMovementsDb() {
        //movimenti letti dal db
        final ArrayList<Movement> movementReaded = new ArrayList<>();

        //vengono creati dei movimenti risultanti da quelli presenti sul db che rappresentano le quote che gli utenti devono effettivamente pagare
        movementsToPay = new ArrayList<>();

        movementsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movementReaded.clear();
                movementsToPay.clear();

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


                Log.w("test", movementReaded.toString());


                //calcolo dei movimenti validi
                for(Movement movementDb: movementReaded) {
                    if(!Movement.containsAlreadyMovement(movementsToPay, movementDb)) {
                        movementsToPay.add(movementDb);
                    }
                }

                //se nelle impostazioni del gruppo i movimenti non sono pubblici, elimino i movimenti che non riguardano l'utente
                if(!group.isPublicMovements()) {
                    Iterator<Movement> iter = movementsToPay.iterator();
                    while (iter.hasNext()) {
                        Movement m = iter.next();
                        if (!m.getUidCreditor().equals(mAuth.getUid()) && !m.getUidDebitor().equals(mAuth.getUid())) {
                            iter.remove();
                        }
                    }
                }

                //caricamneto dei dati di debitori e creditori
                for (final Movement m : movementsToPay) {

                    //default per creditore e debitore
                    m.setCreditor(new User("",""," ","",null,null));
                    m.setDebitor(new User("",""," ","",null,null));

                    //lettura dati
                    usersReference.child(m.getUidCreditor()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String uid = (String)dataSnapshot.child(User.UID).getValue();
                            String name = (String)dataSnapshot.child(User.NAME).getValue();
                            String surname = (String)dataSnapshot.child(User.SURNAME).getValue();
                            String picture = (String)dataSnapshot.child(User.PICTURE).getValue();
                            m.setCreditor(new User(uid,name,surname,null,picture,null));

                            movementAdapter = new MovementAdapter(movementsToPay, isLogged, getActivity(), mAuth.getUid(), group.getIdGroup());
                            movementsGroupRecyclerView.setAdapter(movementAdapter);
                            overviewGroupSwipeRefresh.setRefreshing(false);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    usersReference.child(m.getUidDebitor()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String uid = (String)dataSnapshot.child(User.UID).getValue();
                            String name = (String)dataSnapshot.child(User.NAME).getValue();
                            String surname = (String)dataSnapshot.child(User.SURNAME).getValue();
                            String picture = (String)dataSnapshot.child(User.PICTURE).getValue();
                            m.setDebitor(new User(uid,name,surname,null,picture,null));

                            movementAdapter = new MovementAdapter(movementsToPay, isLogged, getActivity(), mAuth.getUid(), group.getIdGroup());
                            movementsGroupRecyclerView.setAdapter(movementAdapter);
                            overviewGroupSwipeRefresh.setRefreshing(false);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                Log.w("test", movementsToPay.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), R.string.error_db, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void loadStatusData(final View root) {
        DatabaseReference reffUser = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid()).child("mygroups").child(group.getIdGroup());

        reffUser.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            int statusDebit = dataSnapshot.child(Group.STATUS_DEBIT_GROUP).getValue(Integer.class);
                            String amountDebit = (String) dataSnapshot.child(Group.AMOUNT_DEBIT).getValue();

                            group.setStatusDebitGroup(statusDebit);
                            group.setAmountDebit(amountDebit);

                            checkStatusGroup(root);
                        } catch(Exception e) {
                            Log.w("test",e.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private ArrayList<Movement>  readDataFromDb() {
        DatabaseReference reffGroup = FirebaseDatabase.getInstance().getReference().child(Group.GROUPS).child(group.getIdGroup());

        //lettura delle imoostazioni del gruppo per sapere se i movimenti sono pubblici o privati e se sono semplificati o no
        reffGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                group.setSemplificationDebts(dataSnapshot.child(Group.SEMPLIFICATION_DEBTS).getValue(Boolean.class));
                group.setPublicMovements(dataSnapshot.child(Group.PUBLIC_MOVEMENTS).getValue(Boolean.class));

                Log.w("test", group.toString());

                readMovementsDb();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return null;
    }

    private void checkStatusGroup(View root) {

        Log.w("test", group.toString());

        /* viene letto l'importo del debito che si ha nel gruppo */
        String status = group.getAmountDebit();

        /* viene modificata la card dello stato in base al debito che si ha */
        if (group.getStatusDebitGroup() > 0) {
            imgCardStatusDebitGroupImageView.setBackgroundResource(R.drawable.credit);
            subtitleCardStatusDebitGroupTextView.setText(root.getResources().getString(R.string.value_status_credit_group) + " " + status + "€.");
        } else if (group.getStatusDebitGroup() < 0) {
            imgCardStatusDebitGroupImageView.setBackgroundResource(R.drawable.debit);
            subtitleCardStatusDebitGroupTextView.setText(root.getResources().getString(R.string.value_status_debit_group) + " " + status + "€.");
        } else {
            imgCardStatusDebitGroupImageView.setBackgroundResource(R.drawable.equal);
            subtitleCardStatusDebitGroupTextView.setText(root.getResources().getString(R.string.status_parity));
        }
    }

}