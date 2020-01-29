package it.uniba.di.sms1920.madminds.balanceout.ui.expense;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.CircleTrasformation;
import it.uniba.di.sms1920.madminds.balanceout.model.User;
import it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup.ExpenseAdapter;


public class PayerNewExpenseAdapter extends RecyclerView.Adapter<PayerNewExpenseAdapter.ViewHolder> {

    List<User> payerList;
    Context context;
    Activity activity;

    public PayerNewExpenseAdapter(List<User> payerList, Activity activity) {
        this.payerList = payerList;
        this.activity = activity;
    }

    @Override
    public PayerNewExpenseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_payer_new_expense, parent, false);
        PayerNewExpenseAdapter.ViewHolder viewHolder = new PayerNewExpenseAdapter.ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final PayerNewExpenseAdapter.ViewHolder holder, final int position) {
        final User payer = payerList.get(position);

        if (payer.getPicture() != null) {
            holder.imgPayerNewExpenseImageView.setPadding(8, 8, 8, 8);
            Picasso.get().load(payer.getPicture()).fit().centerInside().transform(new CircleTrasformation()).into(holder.imgPayerNewExpenseImageView);
        }

        holder.namePayerNewExpenseTextView.setText(payer.getName() + " " + payer.getSurname().substring(0, 1) + ".");
        holder.uidPayerNewExpenseTextView.setText(payer.getUid());
        holder.selectedPayerNewExpenseCheckBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            holder.valuePaidNewExpenseEditText.setEnabled(true);
                        } else {
                            holder.valuePaidNewExpenseEditText.setEnabled(false);
                            holder.valuePaidNewExpenseEditText.setText("");
                        }
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return payerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox selectedPayerNewExpenseCheckBox;
        ImageView imgPayerNewExpenseImageView;
        TextView namePayerNewExpenseTextView, uidPayerNewExpenseTextView;
        TextInputEditText valuePaidNewExpenseEditText;

        public ViewHolder(View itemView) {
            super(itemView);

            selectedPayerNewExpenseCheckBox = itemView.findViewById(R.id.selectedPayerNewExpenseCheckBox);
            imgPayerNewExpenseImageView = itemView.findViewById(R.id.imgPayerNewExpenseImageView);
            namePayerNewExpenseTextView = itemView.findViewById(R.id.namePayerNewExpenseTextView);
            valuePaidNewExpenseEditText = itemView.findViewById(R.id.valuePaidNewExpenseEditText);
            uidPayerNewExpenseTextView = itemView.findViewById(R.id.uidPayerNewExpenseTextView);
        }

    }

}
