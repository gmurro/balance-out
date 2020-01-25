package it.uniba.di.sms1920.madminds.balanceout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class InviteReceiver extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference dbReff;
    private DatabaseReference dbReffUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_receiver);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null) {

        }

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        //Toast.makeText(InviteReceiver.this, "success", Toast.LENGTH_LONG).show();
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        //
                        // If the user isn't signed in and the pending Dynamic Link is
                        // an invitation, sign in the user anonymously, and record the
                        // referrer's UID.
                        //
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null
                                && deepLink != null
                                && deepLink.getBooleanQueryParameter("idUser", false)) {
                            String referrerUid = deepLink.getQueryParameter("idUser");
                            Toast.makeText(InviteReceiver.this, referrerUid, Toast.LENGTH_LONG).show();
                        }
                    }
                });


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        //
                        // If the user isn't signed in and the pending Dynamic Link is
                        // an invitation, sign in the user anonymously, and record the
                        // referrer's UID.
                        //

                        if (firebaseUser != null
                                && deepLink != null
                                && deepLink.getBooleanQueryParameter("groupId", false)) {
                            String groupId = deepLink.getQueryParameter("groupId");

                            Toast.makeText(InviteReceiver.this, groupId, Toast.LENGTH_LONG).show();

                            addToGroup(groupId);
                        } else {
                            //TODO reindirizzare alla registrazione e avvisare l'utente di cio`
                        }
                    }
                });

    }


    private boolean addToGroup(String groupId) {
        dbReff = FirebaseDatabase.getInstance().getReference().child("groups").child(groupId).child("uidMembers");
        dbReffUser = FirebaseDatabase.getInstance().getReference().child("users").child("mygroups");
        final boolean[] add = {false};
        dbReff.push().setValue(firebaseUser.getUid());
        //dbReffUser.push(groupId).setValue("ok");
        ValueEventListener memerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(!ds.getValue().toString().equals(firebaseUser.getUid())) {
                        add[0] = true;
                    }
                }

                if(add[0]) {
                    dbReff.push().setValue(firebaseUser.getUid());
                    Toast.makeText(InviteReceiver.this, "all done", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        dbReff.addValueEventListener(memerListener);

        return add[0];
    }
}
