package it.uniba.di.sms1920.madminds.balanceout.ui.home;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.di.sms1920.madminds.balanceout.R;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder>{

    List<Group> groupList;
    Context context;

    public GroupAdapter(List<Group> groupList)
    {
        this.groupList = groupList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_group,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Group group = groupList.get(position);

        if(group.getImgGroup()==null) {
            /* default image for group card */
            //holder.imgGroupCardImageView.setBackgroundResource(R.drawable.default_group_img);
        }else {
            //TODO Insert uri image
        }
        holder.titleCardNameGroupTextView.setText(group.getNameGroup());

        /* assegna a subtitleCardStatusDebitGroupTextView la stringa che specifica se si è in debito, in pari o in credito con il gruppo
        *  se statusDebitGroup è rispettivamente -1, 0 o 1 */
        if(group.getStatusDebitGroup()<0) {
            holder.subtitleCardStatusDebitGroupTextView.setText(context.getString(R.string.status_debit_group));
        } else if (group.getStatusDebitGroup()==0) {
            holder.subtitleCardStatusDebitGroupTextView.setText(context.getString(R.string.status_parity_group));
        } else {
            holder.subtitleCardStatusDebitGroupTextView.setText(context.getString(R.string.status_credit_group));
        }

        holder.cardGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,""+group.getNameGroup(),Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imgGroupCardImageView;
        TextView titleCardNameGroupTextView;
        TextView subtitleCardStatusDebitGroupTextView;
        CardView cardGroup;

        public ViewHolder(View itemView)
        {
            super(itemView);

            imgGroupCardImageView = (ImageView) itemView.findViewById(R.id.imgGroupCardImageView);
            titleCardNameGroupTextView = (TextView)itemView.findViewById(R.id.titleCardNameGroupTextView);
            subtitleCardStatusDebitGroupTextView = (TextView)itemView.findViewById(R.id.subtitleCardStatusDebitGroupTextView);
            cardGroup = (CardView)itemView.findViewById(R.id.groupCard);
        }

    }
}