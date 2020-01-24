package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import it.uniba.di.sms1920.madminds.balanceout.MainActivity;
import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.DividerItemDecorator;
import it.uniba.di.sms1920.madminds.balanceout.model.Expense;
import it.uniba.di.sms1920.madminds.balanceout.model.Movement;
import it.uniba.di.sms1920.madminds.balanceout.model.Payer;
import it.uniba.di.sms1920.madminds.balanceout.model.User;


public class ExpensesGroupFragment extends Fragment {

    private FirebaseAuth mAuth;
    private boolean isLogged;
    private SwipeRefreshLayout expensesGroupSwipeRefresh;
    private ArrayList<Expense> expenses;
    private RecyclerView expensesGroupRecyclerView;
    private ExpenseAdapter expenseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_expenses_group, container, false);

        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();

        expensesGroupRecyclerView = root.findViewById(R.id.expensesGroupRecyclerView);
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
            payersExpense.add( new Payer( new User("2", "Giorgio", "Pani", null, null), 12.00));
            expenses.add(new Expense(
                    null,
                    payersExpense,
                    Calendar.getInstance().getTime(),
                    12.00,
                    Expense.EQUAL_DIVISION,
                    "Esempio spesa 1",
                    null,
                    null
                    ));

            ArrayList<Payer> payersExpense2 = new ArrayList<>();
            payersExpense2.add( new Payer( new User("3", "Luca", "De Giorgio", null, null), 15.00));
            expenses.add(new Expense(
                    null,
                    payersExpense2,
                    Calendar.getInstance().getTime(),
                    15.00,
                    Expense.EQUAL_DIVISION,
                    "Esempio spesa 2",
                    null,
                    null
            ));

        } else {
            //TODO lettura da db delle spese
        }

        expenseAdapter = new ExpenseAdapter(expenses, isLogged, getActivity());

        expensesGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        expensesGroupRecyclerView.addItemDecoration(new DividerItemDecorator(getContext().getDrawable(R.drawable.divider)));
        expensesGroupRecyclerView.setItemAnimator(new DefaultItemAnimator());
        expensesGroupRecyclerView.setAdapter(expenseAdapter);

        expensesGroupSwipeRefresh.setRefreshing(false);

    }

}