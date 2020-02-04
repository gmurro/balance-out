package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.CircleTrasformation;
import it.uniba.di.sms1920.madminds.balanceout.model.Movement;
import it.uniba.di.sms1920.madminds.balanceout.model.Reminder;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.shape.OvalShape;

public class MovementAdapter extends RecyclerView.Adapter<MovementAdapter.ViewHolder>{

    List<Movement> movementList;
    Context context;
    Activity activity;
    boolean logged;
    String uidAuth;
    String idGroup;

    public MovementAdapter(List<Movement> movementList, boolean logged, Activity activity, String uidAuth, String idGroup)
    {
        this.logged = logged;
        this.movementList = movementList;
        this.activity = activity;
        this.uidAuth = uidAuth;
        this.idGroup = idGroup;
    }

    @Override
    public MovementAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_movement,parent,false);
        MovementAdapter.ViewHolder viewHolder = new MovementAdapter.ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final MovementAdapter.ViewHolder holder, final int position) {
        final Movement movement = movementList.get(position);


        if(movement.getDebitor().getPicture()!=null) {

            holder.imgDebtorMovementCardImageView.setPadding(9,9,9,9);
            Picasso.get().load(movement.getDebitor().getPicture()).fit().centerInside().transform(new CircleTrasformation()).into(holder.imgDebtorMovementCardImageView);

        }

        if(movement.getCreditor().getPicture()!=null) {
            holder.imgCreditorMovementCardImageView.setPadding(9,9,9,9);
            Picasso.get().load(movement.getCreditor().getPicture()).fit().centerInside().transform(new CircleTrasformation()).into(holder.imgCreditorMovementCardImageView);
        }

        holder.nameCreditorMovementsTextView.setText(movement.getCreditor().getName()+" "+movement.getCreditor().getSurname().substring(0,1)+".");
        holder.nameDebitorMovementsTextView.setText(movement.getDebitor().getName()+" "+movement.getDebitor().getSurname().substring(0,1)+".");

        holder.valueDebtMovementsTextView.setText(movement.getAmount()+"€");

        holder.cardMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!logged) {
                    /* tutorial che spiega ad un utente non loggato che cos'e il movimento*/
                    MaterialShowcaseView showcase = new MaterialShowcaseView.Builder(activity)
                            .setTarget(holder.cardMovement)
                            .setShape(new OvalShape())
                            .setMaskColour(context.getResources().getColor(R.color.primary_dark_opacity))
                            .setDismissText(context.getString(R.string.understand))
                            .setContentText(context.getString(R.string.tutorial_example_movement))
                            .singleUse("tutorialInGroup") // provide a unique ID used to ensure it is only shown once
                            .show();

                } else {
                    //se il movimento riguarda l'utente loggato
                    if(movement.getUidDebitor().equals(uidAuth) || movement.getUidCreditor().equals(uidAuth)) {

                        //rendo i bottoni visibili quando si clicca
                        if (holder.balanceMovementeButton.getVisibility() == View.GONE) {
                            holder.balanceMovementeButton.setVisibility(View.VISIBLE);
                        } else {
                            holder.balanceMovementeButton.setVisibility(View.GONE);
                        }

                        if (holder.rememberMovementButton.getVisibility() == View.GONE) {
                            holder.rememberMovementButton.setVisibility(View.VISIBLE);
                        } else {
                            holder.rememberMovementButton.setVisibility(View.GONE);
                        }
                    }

                }
            }
        });

        //se viene cliccato il bottone per pareggiare il debito
        holder.balanceMovementeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //se viene cliccato il bottone per ricordare il debito
        holder.rememberMovementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //viene scritto sul db il promemoria per mandare la notifica al destinatario

                DatabaseReference reminderRef = FirebaseDatabase.getInstance().getReference().child(Reminder.REMINDERS).child(idGroup);
                String idReminder = reminderRef.push().getKey();

                Reminder reminder = new Reminder(movement.getUidCreditor(), movement.getUidDebitor(), movement.getAmount(), new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()), idGroup);
                reminder.setNameCreditor(movement.getCreditor().getName()+" "+movement.getCreditor().getSurname());
                reminder.setNameDebitor(movement.getDebitor().getName()+" "+movement.getDebitor().getSurname());

                //scrittura su db
                reminderRef.child(idReminder).setValue(reminder.toMap());

                Snackbar.make(holder.rememberMovementButton, context.getString(R.string.title_reminder_sended), Snackbar.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return movementList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imgDebtorMovementCardImageView;
        TextView nameDebitorMovementsTextView;
        TextView valueDebtMovementsTextView;
        ImageView imgCreditorMovementCardImageView;
        TextView nameCreditorMovementsTextView;
        MaterialButton balanceMovementeButton, rememberMovementButton;
        ConstraintLayout cardMovement;

        public ViewHolder(View itemView)
        {
            super(itemView);

            imgDebtorMovementCardImageView = itemView.findViewById(R.id.imgDebtorMovementCardImageView);
            nameDebitorMovementsTextView = itemView.findViewById(R.id.nameDebtorMovementsTextView);
            imgCreditorMovementCardImageView = itemView.findViewById(R.id.imgCreditorMovementCardImageView);
            nameCreditorMovementsTextView = itemView.findViewById(R.id.nameCreditorMovementsTextView);
            valueDebtMovementsTextView = itemView.findViewById(R.id.valueDebtMovementsTextView);
            balanceMovementeButton = itemView.findViewById(R.id.balanceMovementeButton);
            rememberMovementButton = itemView.findViewById(R.id.rememberMovementButton);
            cardMovement = itemView.findViewById(R.id.movementLayout);
        }

    }
}