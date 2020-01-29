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

public class DebitorEqualDivisionAdapter extends RecyclerView.Adapter<DebitorEqualDivisionAdapter.ViewHolder>{

    List<User> debitorList;
    Context context;
    Activity activity;

    public DebitorEqualDivisionAdapter(List<User> debitorList, Activity activity)
    {
        this.debitorList = debitorList;
        this.activity = activity;
    }

    @Override
    public DebitorEqualDivisionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_debitor_equal_division,parent,false);
        DebitorEqualDivisionAdapter.ViewHolder viewHolder = new DebitorEqualDivisionAdapter.ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final DebitorEqualDivisionAdapter.ViewHolder holder, final int position) {
        final User debitor = debitorList.get(position);

        if(debitor.getPicture()!=null) {
            holder.imgDebitorEqualNewExpenseImageView.setPadding(8,8,8,8);
            Picasso.get().load(debitor.getPicture()).fit().centerInside().transform(new CircleTrasformation()).into(holder.imgDebitorEqualNewExpenseImageView);
        }

        holder.nameDebitorEqualNewExpenseTextView.setText(debitor.getName()+" "+debitor.getSurname().substring(0,1)+".");
        holder.uidDebitorEqualNewExpenseTextView.setText(debitor.getUid());
    }

    @Override
    public int getItemCount() {
        return debitorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imgDebitorEqualNewExpenseImageView;
        TextView nameDebitorEqualNewExpenseTextView,uidDebitorEqualNewExpenseTextView;
        CheckBox selectedDebitorEqualNewExpenseCheckBox;

        public ViewHolder(View itemView)
        {
            super(itemView);

            imgDebitorEqualNewExpenseImageView = itemView.findViewById(R.id.imgDebitorEqualNewExpenseImageView);
            nameDebitorEqualNewExpenseTextView = itemView.findViewById(R.id.nameDebitorEqualNewExpenseTextView);
            uidDebitorEqualNewExpenseTextView = itemView.findViewById(R.id.uidDebitorEqualNewExpenseTextView);
            selectedDebitorEqualNewExpenseCheckBox = itemView.findViewById(R.id.selectedDebitorEqualNewExpenseCheckBox);
        }

    }

}

