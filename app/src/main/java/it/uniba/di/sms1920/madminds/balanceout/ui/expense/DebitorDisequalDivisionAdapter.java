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

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.util.List;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.CircleTrasformation;
import it.uniba.di.sms1920.madminds.balanceout.model.User;

public class DebitorDisequalDivisionAdapter extends RecyclerView.Adapter<DebitorDisequalDivisionAdapter.ViewHolder>{

    List<User> debitorList;
    Context context;
    Activity activity;

    public DebitorDisequalDivisionAdapter(List<User> debitorList, Activity activity)
    {
        this.debitorList = debitorList;
        this.activity = activity;
    }

    @Override
    public DebitorDisequalDivisionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_debitor_disequal_division,parent,false);
        DebitorDisequalDivisionAdapter.ViewHolder viewHolder = new DebitorDisequalDivisionAdapter.ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final DebitorDisequalDivisionAdapter.ViewHolder holder, final int position) {
        final User debitor = debitorList.get(position);

        if(debitor.getPicture()!=null) {
            holder.imgDebitorByPersonNewExpenseImageView.setPadding(8,8,8,8);
            Picasso.get().load(debitor.getPicture()).fit().centerInside().transform(new CircleTrasformation()).into(holder.imgDebitorByPersonNewExpenseImageView);
        }

        holder.nameDebitorByPersonNewExpenseTextView.setText(debitor.getName()+" "+debitor.getSurname().substring(0,1)+".");

    }

    @Override
    public int getItemCount() {
        return debitorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imgDebitorByPersonNewExpenseImageView;
        TextView nameDebitorByPersonNewExpenseTextView;
        TextInputEditText valueDebtByPersonNewExpenseEditText;

        public ViewHolder(View itemView)
        {
            super(itemView);

            imgDebitorByPersonNewExpenseImageView = itemView.findViewById(R.id.imgDebitorByPersonNewExpenseImageView);
            nameDebitorByPersonNewExpenseTextView = itemView.findViewById(R.id.nameDebitorByPersonNewExpenseTextView);
            valueDebtByPersonNewExpenseEditText = itemView.findViewById(R.id.valueDebtByPersonNewExpenseEditText);
        }

    }

}

