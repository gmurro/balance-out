package it.uniba.di.sms1920.madminds.balanceout.ui.expense;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.CircleTrasformation;
import it.uniba.di.sms1920.madminds.balanceout.model.Payer;
import it.uniba.di.sms1920.madminds.balanceout.model.User;

public class PayerDetailExpenseAdapter extends RecyclerView.Adapter<PayerDetailExpenseAdapter.ViewHolder> {

    List<Payer> creditorList;
    Context context;
    Activity activity;
    String message;

    public PayerDetailExpenseAdapter(List<Payer> creditorList, Activity activity, String message) {
        this.creditorList = creditorList;
        this.activity = activity;
        this.message = message;
    }

    @Override
    public PayerDetailExpenseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_payer_detail_expense, parent, false);
        PayerDetailExpenseAdapter.ViewHolder viewHolder = new PayerDetailExpenseAdapter.ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final PayerDetailExpenseAdapter.ViewHolder holder, final int position) {
        final Payer creditor = creditorList.get(position);

        if (creditor.getUser() != null) {
            if (creditor.getUser().getPicture() != null) {
                holder.imgPayerDetailExpenseImageView.setPadding(8, 8, 8, 8);
                Picasso.get().load(creditor.getUser().getPicture()).fit().centerInside().transform(new CircleTrasformation()).into(holder.imgPayerDetailExpenseImageView);
            }

            holder.namePayerDetailExpenseTextView.setText(creditor.getUser().getName() + " " + creditor.getUser().getSurname().substring(0, 1) + ". " + message);
            holder.amountDetailExpenseTextView.setText(creditor.getAmount());
        }
    }

    @Override
    public int getItemCount() {
        return creditorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPayerDetailExpenseImageView;
        TextView namePayerDetailExpenseTextView, amountDetailExpenseTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            imgPayerDetailExpenseImageView = itemView.findViewById(R.id.imgPayerDetailExpenseImageView);
            namePayerDetailExpenseTextView = itemView.findViewById(R.id.namePayerDetailExpenseTextView);
            amountDetailExpenseTextView = itemView.findViewById(R.id.amountDetailExpenseTextView);
        }

    }

}

