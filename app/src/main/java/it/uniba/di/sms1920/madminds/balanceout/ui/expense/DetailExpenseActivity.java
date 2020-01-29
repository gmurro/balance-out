package it.uniba.di.sms1920.madminds.balanceout.ui.expense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.DividerItemDecorator;
import it.uniba.di.sms1920.madminds.balanceout.model.Expense;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.Payer;
import it.uniba.di.sms1920.madminds.balanceout.model.User;
import it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup.MemberAdapter;
import it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup.MembersGroupActivity;


public class DetailExpenseActivity extends AppCompatActivity {

    private Expense expense;
    private DatabaseReference expenseReference, usersReference, groupReference;
    private StorageReference storageReference;
    private RecyclerView payersDetailExpenseRecyclerView;
    private RecyclerView debitorDivisionDetailExpenseRecyclerView;
    private PayerDetailExpenseAdapter payersAdapter, debitorsAdapter;
    private TextInputEditText descriptionDetailExpenseEditText;
    private TextView dataDetailExpenseTextView;
    private ConstraintLayout viewReceiptConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_expense);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detailExpenseToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        payersDetailExpenseRecyclerView = findViewById(R.id.payersDetailExpenseRecyclerView);
        debitorDivisionDetailExpenseRecyclerView = findViewById(R.id.debitorDivisionDetailExpenseRecyclerView);
        descriptionDetailExpenseEditText = findViewById(R.id.descriptionDetailExpenseEditText);
        dataDetailExpenseTextView = findViewById(R.id.dataDetailExpenseTextView);
        viewReceiptConstraintLayout = findViewById(R.id.viewReceiptConstraintLayout);

        storageReference = FirebaseStorage.getInstance().getReference("receiptsExpenses");
        groupReference = FirebaseDatabase.getInstance().getReference().child(Group.GROUPS).child(expense.getIdGroup()).child(Group.UID_MEMEBRS);
        usersReference = FirebaseDatabase.getInstance().getReference().child(User.USERS);
        expenseReference = FirebaseDatabase.getInstance().getReference().child(Expense.EXPENSES);


        expense = new Expense();
        readExpense();

        //imposto le view con i valori della spesa
        descriptionDetailExpenseEditText.setText(expense.getDescription());
        dataDetailExpenseTextView.setText(expense.getData());
        viewReceiptConstraintLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(expense.getReceipt()==null) {
                            Snackbar.make(findViewById(R.id.viewReceiptConstraintLayout), getString(R.string.title_empty_receipt), Snackbar.LENGTH_LONG).show();
                        } else {
                            AlertDialog.Builder alertadd = new AlertDialog.Builder(DetailExpenseActivity.this);
                            LayoutInflater factory = LayoutInflater.from(DetailExpenseActivity.this);
                            final View view = factory.inflate(R.layout.card_receipt_dialog, null);
                            alertadd.setView(view);
                            alertadd.setNeutralButton("Ok!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dlg, int sumthin) {

                                }
                            });
                            alertadd.show();
                        }
                    }
                }
        );


    }

    private void readExpense() {
        //TODO lettura
        //prova
        ArrayList<Payer> creditors = new ArrayList<>();
        creditors.add(new Payer("2ooPH7ou6kf5e5ge7QceUx3lfCS2", "5.00"));
        ArrayList<Payer> debitors = new ArrayList<>();
        debitors.add(new Payer("2ooPH7ou6kf5e5ge7QceUx3lfCS2", "2.50"));
        debitors.add(new Payer("lFbg6eQe3fQXBBp7eEJdEHodoTD2", "2.50"));
        expense = new Expense("1", creditors, "20/01/2020", Expense.EQUAL_DIVISION, "Pic-nic", null, debitors, "-LziaT3xZZWfoCShT9aZ", Expense.NEVER);


        //vengono letti gli utenti relativi a chi ha pagato la spesa dal db
        for (final Payer p : expense.getPayersExpense()) {
            usersReference.child(p.getIdUser()).addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            expense.getPayersExpense().get(expense.getPayersExpense().indexOf(p)).setUser(dataSnapshot.getValue(User.class));

                            //viene aggiornata la recycle view degli utenti che hanno pagato la spesa
                            payersAdapter = new PayerDetailExpenseAdapter(expense.getPayersExpense(), DetailExpenseActivity.this, getString(R.string.title_paid_single));
                            payersDetailExpenseRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            payersDetailExpenseRecyclerView.addItemDecoration(new DividerItemDecorator(getDrawable(R.drawable.divider)));
                            payersDetailExpenseRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            payersDetailExpenseRecyclerView.setAdapter(payersAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

        //vengono letti gli utenti relativi a chi deve pagare dal db
        for (final Payer p : expense.getPayersDebt()) {
            usersReference.child(p.getIdUser()).addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            expense.getPayersDebt().get(expense.getPayersDebt().indexOf(p)).setUser(dataSnapshot.getValue(User.class));

                            //viene aggiornata la recycle view degli utenti in debito
                            debitorsAdapter = new PayerDetailExpenseAdapter(expense.getPayersDebt(), DetailExpenseActivity.this, getString(R.string.title_must_pay));
                            debitorDivisionDetailExpenseRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            debitorDivisionDetailExpenseRecyclerView.addItemDecoration(new DividerItemDecorator(getDrawable(R.drawable.divider)));
                            debitorDivisionDetailExpenseRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            debitorDivisionDetailExpenseRecyclerView.setAdapter(debitorsAdapter);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }
}
