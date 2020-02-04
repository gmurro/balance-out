package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import it.uniba.di.sms1920.madminds.balanceout.MainActivity;
import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.Reminder;

public class AdvancedSettingsGroupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference reffGroup;
    private Group group;
    private SwitchMaterial publicMovementsSettingsGroupSwitch, debtSemplificationSettingsGroupSwitch;
    private TextView titleDeleteGroupTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_settings_group);

        Toolbar toolbar = findViewById(R.id.advancedSettingsGroupToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mAuth = FirebaseAuth.getInstance();

        //inizializzazione delle views
        publicMovementsSettingsGroupSwitch = findViewById(R.id.publicMovementsSettingsGroupSwitch);
        debtSemplificationSettingsGroupSwitch = findViewById(R.id.debtSemplificationSettingsGroupSwitch);
        titleDeleteGroupTextView = findViewById(R.id.titleDeleteGroupTextView);


        group = new Group();
        //vengono letti i dati sul gruppo dall'intent precedente
        group.setIdGroup(getIntent().getStringExtra(Group.ID_GROUP));

        //legge dal db lo stato su cui devono essere impostate le switch
        readSwitchesStatus();

        publicMovementsSettingsGroupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reffGroup.child(Group.PUBLIC_MOVEMENTS).setValue(isChecked);
            }
        });

        debtSemplificationSettingsGroupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reffGroup.child(Group.SEMPLIFICATION_DEBTS).setValue(isChecked);
            }
        });

        titleDeleteGroupTextView.setOnClickListener(new View.OnClickListener() {
            //viene cancellato il gruppo
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(AdvancedSettingsGroupActivity.this)
                        .setTitle(getString(R.string.title_delete_group))
                        .setMessage(getString(R.string.message_delete_group))
                        .setPositiveButton(getString(R.string.title_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteGroup();
                            }
                        })
                        .setNegativeButton(getString(R.string.title_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void deleteGroup() {
        final DatabaseReference reffReminders = FirebaseDatabase.getInstance().getReference().child(Reminder.REMINDERS);

        //viene cancellato il gruppo
        reffGroup.child(Group.ACTIVE).setValue(false);

        //cancello anche i promemoria del gruppo
        reffReminders.child(group.getIdGroup()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot idReminder: dataSnapshot.getChildren()) {

                    String key = idReminder.getKey();
                    String uidCreditor = idReminder.child(Reminder.UID_CREDITOR).getValue(String.class);
                    String uidDebitor = idReminder.child(Reminder.UID_DEBITOR).getValue(String.class);

                    if(uidCreditor.equals(mAuth.getUid()) || uidDebitor.equals(mAuth.getUid())) {
                        reffReminders.child(group.getIdGroup()).child(key).removeValue();
                    }
                }

                setResult(RESULT_OK);
                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readSwitchesStatus() {
        reffGroup = FirebaseDatabase.getInstance().getReference().child(Group.GROUPS).child(group.getIdGroup());
        reffGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                group.setSemplificationDebts(dataSnapshot.child(Group.SEMPLIFICATION_DEBTS).getValue(Boolean.class));
                group.setPublicMovements(dataSnapshot.child(Group.PUBLIC_MOVEMENTS).getValue(Boolean.class));

                publicMovementsSettingsGroupSwitch.setChecked(group.isPublicMovements());
                debtSemplificationSettingsGroupSwitch.setChecked(group.isSemplificationDebts());

                for (DataSnapshot ds : dataSnapshot.child(Group.UID_MEMEBRS).getChildren()) {
                    group.addUidMembers(ds.getValue(String.class));
                }

                Log.w("test", group.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
