package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import it.uniba.di.sms1920.madminds.balanceout.MainActivity;
import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.Expense;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.Payer;
import it.uniba.di.sms1920.madminds.balanceout.ui.expense.DetailExpenseActivity;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.shape.OvalShape;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder>{

    List<Expense> expenseList;
    Context context;
    Activity activity;
    boolean logged;

    public ExpenseAdapter(List<Expense> expenseList, boolean logged, Activity activity)
    {
        this.logged = logged;
        this.expenseList = expenseList;
        this.activity = activity;
    }

    @Override
    public ExpenseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_expense,parent,false);
        ExpenseAdapter.ViewHolder viewHolder = new ExpenseAdapter.ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ExpenseAdapter.ViewHolder holder, final int position) {
        final Expense expense = expenseList.get(position);

        holder.descriptionExpenseGroupCardTextView.setText(expense.getDescription());

        /*viene calcolato qunato è stata pagata la spesa in totale*/
        double amount=0;
        for(Payer p: expense.getPayersExpense()){
            amount += Double.valueOf(p.getAmount());
        }

        holder.payerExpenseGroupCardTextView.setText(context.getString(R.string.title_amount_expense_is)+" "+String.format("%.2f", amount)+" €");

        holder.dateCardExpenseGroupTextView.setText(expense.getData());

        holder.expenseGroupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!logged) {
                    /* tutorial che spiega ad un utente non loggato che cos'e una spesa*/
                    MaterialShowcaseView showcase = new MaterialShowcaseView.Builder(activity)
                            .setTarget(holder.expenseGroupCard)
                            .setShape(new OvalShape())
                            .setMaskColour(context.getResources().getColor(R.color.primary_dark_opacity))
                            .setDismissText(context.getString(R.string.understand))
                            .setContentText(context.getString(R.string.tutorial_example_expense))
                            .singleUse("tutorialInExpense") // provide a unique ID used to ensure it is only shown once
                            .show();

                } else {

                    Intent intent = new Intent(context, DetailExpenseActivity.class);
                    intent.putExtra(Expense.ID, expense.getId());
                    intent.putExtra(Expense.ID_GROUP, expense.getIdGroup());
                    activity.startActivityForResult(intent, GroupActivity.EXPENSE_CANCELLED);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView descriptionExpenseGroupCardTextView;
        TextView payerExpenseGroupCardTextView;
        TextView dateCardExpenseGroupTextView;
        MaterialCardView expenseGroupCard;

        public ViewHolder(View itemView)
        {
            super(itemView);

            descriptionExpenseGroupCardTextView = itemView.findViewById(R.id.descriptionExpenseGroupCardTextView);
            payerExpenseGroupCardTextView = itemView.findViewById(R.id.payerExpenseGroupCardTextView);
            dateCardExpenseGroupTextView = itemView.findViewById(R.id.dateCardExpenseGroupTextView);
            expenseGroupCard = itemView.findViewById(R.id.expenseGroupCard);
        }

    }

}
