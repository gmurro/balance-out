package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import it.uniba.di.sms1920.madminds.balanceout.MainActivity;
import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.DividerItemDecorator;
import it.uniba.di.sms1920.madminds.balanceout.model.Expense;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.Payer;
import it.uniba.di.sms1920.madminds.balanceout.model.User;
import it.uniba.di.sms1920.madminds.balanceout.ui.home.GroupAdapter;


public class ExpensesGroupFragment extends Fragment {

    private FirebaseAuth mAuth;
    private boolean isLogged;
    private SwipeRefreshLayout expensesGroupSwipeRefresh;
    private ArrayList<Expense> expenses;
    private RecyclerView expensesGroupRecyclerView;
    private ExpenseAdapter expenseAdapter;
    private Group group;
    private DatabaseReference expenseReference;
    private View root;

    public ExpensesGroupFragment() {
    }

    /*viene passato come parametro il gruppo che viene visualizzato nell'activity*/
    public ExpensesGroupFragment(Group group) {
        this.group = group;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_expenses_group, container, false);


        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();


        expensesGroupSwipeRefresh = root.findViewById(R.id.expensesGroupSwipeRefresh);

        expenses = new ArrayList<>();

        /* vengono caricate tutte le spese nella recycle view */
        loadExpences();

        /* quando viene ricaricata la pagina con uno swipe down, vengono ricaricati tutti i movimenti*/
        expensesGroupSwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadExpences();
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

    private void loadExpences(){
        /* la lista viene pulita poiche altrimenti ogni volta ce si ricarica la pagina
         *  verrebbero aggiunte le stesse spese */
        expenses.clear();

        if (!isLogged) {
            /*creazione di spese di esempio visibili solo quando l'utente non è loggato*/
            ArrayList<Payer> payersExpense = new ArrayList<>();
            payersExpense.add( new Payer( "2", "12.00"));
            expenses.add(new Expense(
                    null,
                    payersExpense,
                    new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()),
                    Expense.EQUAL_DIVISION,
                    "Esempio spesa 1",
                    null,
                    null,
                    null,
                    0
                    ));

            ArrayList<Payer> payersExpense2 = new ArrayList<>();
            payersExpense2.add( new Payer( "3", "15.00"));
            expenses.add(new Expense(
                    null,
                    payersExpense2,
                    new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()),
                    Expense.EQUAL_DIVISION,
                    "Esempio spesa 2",
                    null,
                    null,
                    null,
                    0
            ));

        } else {
            //vengono letti le spese dal db
            expenseReference = FirebaseDatabase.getInstance().getReference().child(Expense.EXPENSES).child(group.getIdGroup());
            expenseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot expense : dataSnapshot.getChildren()) {

                        int alreadyRead = Expense.containsIdExpense(expenses, expense.getValue(Expense.class).getId());
                        if (alreadyRead == -1) {
                            expenses.add(expense.getValue(Expense.class));
                        } else {
                            //viene sostituito il gruppo modificato
                            expenses.remove(alreadyRead);
                            expenses.add(alreadyRead, expense.getValue(Expense.class));
                        }
                        Log.w("letturaSpesa",expense.getValue(Expense.class).toString());
                    }

                    //viene aggiornata la recycle view
                    expensesGroupRecyclerView = root.findViewById(R.id.expensesGroupRecyclerView);
                    expenseAdapter = new ExpenseAdapter(expenses, isLogged, getActivity());
                    expensesGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                    expensesGroupRecyclerView.addItemDecoration(new DividerItemDecorator(getContext().getDrawable(R.drawable.divider)));
                    expensesGroupRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    expensesGroupRecyclerView.setAdapter(expenseAdapter);
                    expensesGroupSwipeRefresh.setRefreshing(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), R.string.error_db, Toast.LENGTH_LONG).show();
                }
            });
        }

        expensesGroupRecyclerView = root.findViewById(R.id.expensesGroupRecyclerView);
        expenseAdapter = new ExpenseAdapter(expenses, isLogged, getActivity());

        expensesGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        expensesGroupRecyclerView.addItemDecoration(new DividerItemDecorator(getContext().getDrawable(R.drawable.divider)));
        expensesGroupRecyclerView.setItemAnimator(new DefaultItemAnimator());
        expensesGroupRecyclerView.setAdapter(expenseAdapter);

        expensesGroupSwipeRefresh.setRefreshing(false);

    }

}