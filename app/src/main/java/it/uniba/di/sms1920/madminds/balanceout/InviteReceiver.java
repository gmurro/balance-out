package it.uniba.di.sms1920.madminds.balanceout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms1920.madminds.balanceout.model.User;

public class InviteReceiver extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference dbReff;
    private DatabaseReference dbReffUser;
    private ArrayList<String> member;
    private boolean isPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_receiver);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        member = new ArrayList<>();

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

                            dbReffUser = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child(User.MY_GROUPS);
                            dbReff = FirebaseDatabase.getInstance().getReference().child("groups").child(groupId).child("uidMembers");

                            //viene aggiunto l'utente al gruppo
                            addToGroup(groupId);

                        } else {
                            //l'utente non è loggato
                            Toast.makeText(getApplicationContext(),getString(R.string.title_log_to_enter_group),Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(InviteReceiver.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

    }


    private void addToGroup(final String groupId) {

        dbReff.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                isPresent = false;
                String lastKey = null;

                if (mutableData.getValue() == null) {
                    return Transaction.success(mutableData);
                }

                Log.w("mydebug", "dentro transaction");
                Log.w("mydebug", mutableData.toString());

                for(MutableData md : mutableData.getChildren()) {
                    member.add(md.getValue(String.class));
                    lastKey = md.getKey();
                    if (md.getValue(String.class).equals(firebaseUser.getUid())) {
                        isPresent = true;
                        break;
                    }
                    Log.w("mydebug", member.toString());
                }

                int count = Integer.valueOf(lastKey) + 1;
                Log.w("mydebug", lastKey);
                Log.w("mydebug", isPresent+"");


                if(!isPresent) {
                    mutableData.child(String.valueOf(count)).setValue(firebaseUser.getUid());

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(groupId + "/amountDebit", getString(R.string.zero_amount_debit));
                    childUpdates.put(groupId + "/statusDebitGroup", 0);
                    dbReffUser.updateChildren(childUpdates);

                    Log.w("mydebug", "member ADDED");

                } else {
                    Log.w("mydebug", "gia presente");
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                Log.w("mydebug", "onComplete");

                //viene visualizzato un messaggio a seconda se l'utente era già presente nel gruppo o meno
                if(isPresent) {
                    Toast.makeText(InviteReceiver.this, getString(R.string.title_already_in_group), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(InviteReceiver.this, getString(R.string.title_entered_group), Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent(InviteReceiver.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }




}
