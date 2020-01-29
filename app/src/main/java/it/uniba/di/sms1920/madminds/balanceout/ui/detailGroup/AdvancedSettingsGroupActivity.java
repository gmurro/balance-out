package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;

public class AdvancedSettingsGroupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference reffGroup;
    private Group group;
    private SwitchMaterial publicMovementsSettingsGroupSwitch, debtSemplificationSettingsGroupSwitch;

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


        //inizializzazione delle views
        publicMovementsSettingsGroupSwitch = findViewById(R.id.publicMovementsSettingsGroupSwitch);
        debtSemplificationSettingsGroupSwitch = findViewById(R.id.debtSemplificationSettingsGroupSwitch);


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
