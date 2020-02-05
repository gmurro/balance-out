package it.uniba.di.sms1920.madminds.balanceout.ui.reminder;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import it.uniba.di.sms1920.madminds.balanceout.MainActivity;
import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.Reminder;
import it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup.GroupActivity;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    List<Reminder> reminderList;
    Context context;
    Activity activity;
    String idAuth;

    public ReminderAdapter(List<Reminder> reminderList, Activity activity, String idAuth) {
        this.reminderList = reminderList;
        this.activity = activity;
        this.idAuth = idAuth;
    }

    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_reminder, parent, false);
        ReminderAdapter.ViewHolder viewHolder = new ReminderAdapter.ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ReminderAdapter.ViewHolder holder, final int position) {
        final Reminder reminder = reminderList.get(position);


        holder.nameGroupReminderTextView.setText(context.getString(R.string.title_group) + ": " + reminder.getNameGroup());

        if (reminder.getUidCreditor().equals(idAuth)) {
            holder.textReminderTextView.setText(Html.fromHtml(context.getString(R.string.title_reminder_remember) + " " + reminder.getNameDebitor() + " <b>" + context.getString(R.string.title_remider_have) + "</b> " + reminder.getAmount() + "€"));
        } else {
            holder.textReminderTextView.setText(Html.fromHtml(context.getString(R.string.title_reminder_remember) + " " + reminder.getNameCreditor() + " <b>" + context.getString(R.string.title_reminder_give) + "</b> " + reminder.getAmount() + "€"));
        }

        holder.dateReminderTextView.setText(reminder.getData());

        holder.reminderConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Apre il dettaglio del gruppo*/
                Intent intent = new Intent(context, GroupActivity.class);
                intent.putExtra(Group.ID_GROUP, reminder.getIdGroup());
                intent.putExtra(Group.NAME_GROUP, reminder.getNameGroup());
                intent.putExtra(Group.CREATION_DATA_GROUP, "");
                activity.startActivityForResult(intent, MainActivity.START_FRAGMENT);
            }
        });

        holder.reminderConstraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle(context.getString(R.string.title_delete_reminder))
                        .setMessage(context.getString(R.string.message_delete_reminder))
                        .setPositiveButton(context.getString(R.string.title_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteReminder(reminder);
                            }
                        })
                        .setNegativeButton(context.getString(R.string.title_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameGroupReminderTextView;
        TextView textReminderTextView;
        TextView dateReminderTextView;
        ConstraintLayout reminderConstraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            nameGroupReminderTextView = itemView.findViewById(R.id.nameGroupReminderTextView);
            textReminderTextView = itemView.findViewById(R.id.textReminderTextView);
            dateReminderTextView = itemView.findViewById(R.id.dateReminderTextView);
            reminderConstraintLayout = itemView.findViewById(R.id.reminderConstraintLayout);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                textReminderTextView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            }
        }

    }

    private void deleteReminder(Reminder r) {
        DatabaseReference reffReminders = FirebaseDatabase.getInstance().getReference().child(Reminder.REMINDERS);
        reffReminders.child(r.getIdGroup()).child(r.getIdReminder()).removeValue();
    }

}
