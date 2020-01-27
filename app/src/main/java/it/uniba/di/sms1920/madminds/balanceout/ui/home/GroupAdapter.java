package it.uniba.di.sms1920.madminds.balanceout.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it.uniba.di.sms1920.madminds.balanceout.helper.CircleTrasformation;
import it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup.GroupActivity;
import it.uniba.di.sms1920.madminds.balanceout.MainActivity;
import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.shape.OvalShape;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder>{

    List<Group> groupList;
    Context context;
    Activity activity;
    boolean logged;

    public GroupAdapter(List<Group> groupList, boolean logged, Activity activity)
    {
        this.logged = logged;
        this.groupList = groupList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_group,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Group group = groupList.get(position);


        if(group.getImgGroup() == null) {
            /* default image for group card */
            holder.imgGroupCardImageView.setBackgroundResource(R.drawable.default_group_img);
        }else {
            //TODO leggere la foto da db

            holder.imgGroupCardImageView.setPadding(9,9,9,9);
            Picasso.get().load(group.getImgGroup()).fit().centerInside().transform(new CircleTrasformation()).into(holder.imgGroupCardImageView);

        }

        holder.titleCardNameGroupTextView.setText(group.getNameGroup());

        /* assegna a subtitleCardStatusDebitGroupTextView la stringa che specifica se si è in debito, in pari o in credito con il gruppo
        *  se statusDebitGroup è rispettivamente -1, 0 o 1 */
        if(group.getStatusDebitGroup()<0) {
            holder.subtitleCardStatusDebitGroupTextView.setText(context.getString(R.string.status_debit_group));
            holder.subtitleCardStatusDebitGroupTextView.setBackgroundResource(R.drawable.my_border_red);
            holder.subtitleCardStatusDebitGroupTextView.setTextColor(context.getColor(R.color.dark_red));
        } else if (group.getStatusDebitGroup()==0) {
            holder.subtitleCardStatusDebitGroupTextView.setText(context.getString(R.string.status_parity_group));
            holder.subtitleCardStatusDebitGroupTextView.setBackgroundResource(R.drawable.my_border_purple);
            holder.subtitleCardStatusDebitGroupTextView.setTextColor(context.getColor(R.color.primary));
        } else {
            holder.subtitleCardStatusDebitGroupTextView.setText(context.getString(R.string.status_credit_group));
            holder.subtitleCardStatusDebitGroupTextView.setBackgroundResource(R.drawable.my_border_green);
            holder.subtitleCardStatusDebitGroupTextView.setTextColor(context.getColor(R.color.dark_green));
        }

        holder.cardGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!logged) {
                    /* tutorial che spiega il motivo per cui per un utente non loggato viene visualizzato il gruppo di esempio */
                    MaterialShowcaseView showcase = new MaterialShowcaseView.Builder(activity)
                            .setTarget(holder.cardGroup)
                            .setShape(new OvalShape())
                            .setMaskColour(context.getResources().getColor(R.color.primary_dark_opacity))
                            .setDismissText(context.getString(R.string.understand))
                            .setContentText(context.getString(R.string.tutorial_example_group))
                            .singleUse("tutorial") // provide a unique ID used to ensure it is only shown once
                            .show();

                    /* se il tutorial è stato chiuso dall'utente, apre l'activity di dettaglio del gruppo */
                    if (!showcase.isAttachedToWindow()) {
                        Intent intent = new Intent(context, GroupActivity.class);
                        intent.putExtra(Group.GROUP, group);
                        activity.startActivityForResult(intent, MainActivity.START_FRAGMENT);
                    }
                } else {

                    /*Apre il dettaglio del gruppo*/
                    Intent intent = new Intent(context, GroupActivity.class);
                    intent.putExtra(Group.GROUP, group);
                    activity.startActivityForResult(intent, MainActivity.START_FRAGMENT);
                }
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