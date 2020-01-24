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

import java.util.List;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.Movement;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.shape.OvalShape;

public class MovementAdapter extends RecyclerView.Adapter<MovementAdapter.ViewHolder>{

    List<Movement> movementList;
    Context context;
    Activity activity;
    boolean logged;

    public MovementAdapter(List<Movement> movementList, boolean logged, Activity activity)
    {
        this.logged = logged;
        this.movementList = movementList;
        this.activity = activity;
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

        if(movement.getDebitor().getImgUser()!=null) {

            //TODO Insert uri image
        }

        if(movement.getCreditor().getImgUser()!=null) {
            //TODO Insert uri image
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
                    //TODO dettaglio bottoni movimento
                }
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
        ConstraintLayout cardMovement;

        public ViewHolder(View itemView)
        {
            super(itemView);

            imgDebtorMovementCardImageView = itemView.findViewById(R.id.imgDebtorMovementCardImageView);
            nameDebitorMovementsTextView = itemView.findViewById(R.id.nameDebtorMovementsTextView);
            imgCreditorMovementCardImageView = itemView.findViewById(R.id.imgCreditorMovementCardImageView);
            nameCreditorMovementsTextView = itemView.findViewById(R.id.nameCreditorMovementsTextView);
            valueDebtMovementsTextView = itemView.findViewById(R.id.valueDebtMovementsTextView);
            cardMovement = itemView.findViewById(R.id.movementLayout);
        }

    }
}