package it.uniba.di.sms1920.madminds.balanceout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.MetadateGroup;

public class NewGroupActivity extends AppCompatActivity {

    private Button createGroup;
    private FirebaseAuth mAuth;
    private DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        createGroup = findViewById(R.id.createGroupButton);

        /*inizializzazione firebase*/
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        reff = FirebaseDatabase.getInstance().getReference();

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creaNuovoGruppo();
            }
        });
    }


    private boolean creaNuovoGruppo() {
        final boolean[] success = new boolean[1];
        ArrayList<String> utenti = new ArrayList<>();

        utenti.add(mAuth.getUid());


        ArrayList<String> uidMembers = new ArrayList<>();
        uidMembers.add(mAuth.getUid());

        MetadateGroup metagruppoData = new MetadateGroup(0, "00.00");

        Group g = new Group(null, getString(R.string.example_name_group),
                Calendar.getInstance().getTime(),
                null,
                null,
                utenti,
                mAuth.getUid(),
                0,
                0,
                true
        );


        Map<String, Object> gruppoMap = g.toMap();
        Map<String, Object> metadateMap = metagruppoData.toMap();

        String key = reff.child("groups").push().getKey();
        Map<String, Object> childUpdate = new HashMap<>();

        //scrittura multipla su rami differenti del db
        childUpdate.put("/groups/" + key, gruppoMap);
        childUpdate.put("/users/"+mAuth.getUid()+"/mygroups/"+key, metadateMap);

        reff.updateChildren(childUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                success[0] = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                success[0] = false;
            }
        });


        return success[0];
    }
}
