package it.uniba.di.sms1920.madminds.balanceout.ui.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import android.util.Log;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.ui.profile.ProfileFragment;

public class ReminderFragment extends Fragment {
    private FirebaseAuth mAuth;
    private boolean isLogged;
    private boolean isEmailVerified;

    private static final String TAG = "balanceOutTracker";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();


        /* vengono mostrati 3 layout diversi a seconda se l'utente è loggato o meno e se quando lo è, non ha verificato l'account tramite mail */
        View root;
        if (!isLogged) {
            root = notLoggedReminderFragment(inflater, container);
        } else {
            if (isEmailVerified) {
                root = loggedReminderFragment(inflater, container);
            } else {
                root = notEmailVerificatedActivityFragment(inflater, container);
            }
        }

        return root;
    }

    public View notLoggedReminderFragment(LayoutInflater inflater, final ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_not_logged, container, false);
        MaterialButton login = root.findViewById(R.id.loginReminderButton);
        final BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);

        login.setOnClickListener(new MaterialButton.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Di seguito il passaggio al fragment del login */
                Fragment newFragment = new ProfileFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                /* Sostituisce tutto ciò che è nel main_fragment con questo fragment
                 * aggiunge la transazione al back stack e fa il commit*/
                transaction.replace(R.id.main_fragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                /* modifica la barra di navigazione selezionando l'icona del profilo*/
                bottomNavigationView.setSelectedItemId(R.id.navigation_profile);

            }
        });
        return root;
    }

    public View notEmailVerificatedActivityFragment (LayoutInflater inflater, final ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_not_email_verificated, container, false);
        MaterialButton emailIntentButton = root.findViewById(R.id.emailIntentButton);
        final BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);

        emailIntentButton.setOnClickListener(new MaterialButton.OnClickListener() {
            @Override
            public void onClick(View v){

                /* Intent che apre la casella di posta elettronica */
                Intent intent = Intent.makeMainSelectorActivity(
                        Intent.ACTION_MAIN,
                        Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);       //la posta elettronica viene aperta separatamente rispetto all'app Balance Out
                startActivity(intent);
            }
        });
        return root;
    }

    public View loggedReminderFragment(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_reminder, container, false);

        Button topicButton = root.findViewById(R.id.topicButton);
        Button tokenbutton = root.findViewById(R.id.tokenButton);
        Button sendButton = root.findViewById(R.id.sendButton);
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("token");



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("MyNotifications", "Mia notifica ora", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            Log.i (TAG, "Notification Channel creato");

        }



        topicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Subscribing to weather topic");
                // [START subscribe_topics]
                FirebaseMessaging.getInstance().subscribeToTopic("Sviluppatori")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                String msg = "Topic weather scritto";
                                if (!task.isSuccessful()) {
                                    msg = "Failed to subscribe to weather topic";
                                }

                                Log.i(TAG, msg);
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                // [END subscribe_topics]
            }
        });



        tokenbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get token
                // [START retrieve_current_token]
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.i(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();

                                db.child("userToken").child(token).child(mAuth.getUid()).setValue("used");


                                // Log and toast
                                String msg = " Token = " + token;
                                Log.i(TAG, msg);
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                // [END retrieve_current_token]
            }
        });



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /* Invio da dispositivo a dispositivo, setMessageId e SENDER_ID ???

                FirebaseMessaging fm = FirebaseMessaging.getInstance();
                fm.send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                        .setMessageId(Integer.toString(messageId))
                        .addData("my_message", "Hello World")
                        .addData("my_action","SAY_HELLO")
                        .build());
                */

            }


        });





        return root;
    }



    private void sendNotification(){

        //our json object will look like

    }


    private void verifyLogged() {
        /* firebaseUser contiene l'informazione relativa all'utente se è loggato o meno */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        /* memorizzo in isLogged l'informazione boolean relativa all'utente se è loggato o meno*/
        if (firebaseUser == null) {
            isLogged = false;
            isEmailVerified = false;
        } else {
            isLogged = true;
            isEmailVerified = firebaseUser.isEmailVerified();
        }
    }
}