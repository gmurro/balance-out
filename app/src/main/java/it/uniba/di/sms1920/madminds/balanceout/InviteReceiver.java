package it.uniba.di.sms1920.madminds.balanceout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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


                        if (firebaseUser != null
                                && deepLink != null
                                && deepLink.getBooleanQueryParameter("groupId", false)) {
                            String groupId = deepLink.getQueryParameter("groupId");

                            //Toast.makeText(InviteReceiver.this, groupId, Toast.LENGTH_LONG).show();

                            if(addToGroup(groupId)){
                                Toast.makeText(InviteReceiver.this, "true", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(InviteReceiver.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(InviteReceiver.this, "false", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(InviteReceiver.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            //TODO reindirizzare alla registrazione e avvisare l'utente di cio`
                        }
                    }
                });

    }


    private boolean addToGroup(final String groupId) {
        final boolean[] add = {false};

        dbReff = FirebaseDatabase.getInstance().getReference().child("groups").child(groupId).child("uidMembers");
        dbReffUser = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("mygroups");
        final ArrayList<String> member = new ArrayList<>();

        dbReff.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull final MutableData mutableData) {
                boolean presente = false;
                for(MutableData md : mutableData.getChildren()) {
                    member.add(md.getValue().toString());
                    Log.w("mydebug", member.toString());

                    for(String idMember : member) {
                        if(idMember.equals(firebaseUser.getUid())){
                            presente = true;
                            break;
                        }
                    }



                }

                if(!presente) {
                    mutableData.child(String.valueOf(member.size())).setValue(firebaseUser.getUid());
                } else {
                    Log.w("mydebug", "gia presente");
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(groupId + "/amountDebit", getString(R.string.zero_amount_debit));
                childUpdates.put(groupId + "/statusDebitGroup", 0);

                dbReffUser.updateChildren(childUpdates);

                add[0] = true;
            }
        });

        return add[0];
    }
}
