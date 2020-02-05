package it.uniba.di.sms1920.madminds.balanceout.ui.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.DividerItemDecorator;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.Reminder;
import it.uniba.di.sms1920.madminds.balanceout.model.User;
import it.uniba.di.sms1920.madminds.balanceout.ui.profile.ProfileFragment;

public class ReminderFragment extends Fragment {
    private FirebaseAuth mAuth;
    private boolean isLogged;
    private boolean isEmailVerified;
    private ArrayList<Reminder> reminders;
    private RecyclerView remindersRecyclerView;
    private SwipeRefreshLayout reminderSwipeRefresh;
    private ReminderAdapter reminderAdapter;
    private TextView messageNoReminderTextView;

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

    public View notEmailVerificatedActivityFragment(LayoutInflater inflater, final ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_not_email_verificated, container, false);
        MaterialButton emailIntentButton = root.findViewById(R.id.emailIntentButton);
        MaterialButton sendEmailVerificationButton = root.findViewById(R.id.sendEmailVerificationButton);
        final BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);

        sendEmailVerificationButton.setOnClickListener(
                new MaterialButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendEmailVerification();
                    }
                });

        emailIntentButton.setOnClickListener(new MaterialButton.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                getActivity().recreate();
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

    private void sendEmailVerification() {

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        //findViewById(R.id.verifyEmailButton).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(),
                                    getString(R.string.verification_email_sent) + " "+user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("pippo", "sendEmailVerification", task.getException());
                            Toast.makeText(getActivity(),
                                    getString(R.string.failed_verification_email),
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    public View loggedReminderFragment(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_reminder, container, false);

        messageNoReminderTextView = root.findViewById(R.id.messageNoReminderTextView);
        reminderSwipeRefresh = root.findViewById(R.id.reminderSwipeRefresh);
        reminders = new ArrayList<>();

        remindersRecyclerView = root.findViewById(R.id.remindersRecyclerView);
        remindersRecyclerView.addItemDecoration(new DividerItemDecorator(getActivity().getDrawable(R.drawable.divider)));


        /* vengono caricati tutti i promemoria nella recycle view */
        loadReminders();

        if(reminders.size()==0) {
            messageNoReminderTextView.setVisibility(View.VISIBLE);
        } else {
            messageNoReminderTextView.setVisibility(View.GONE);
        }

        /* quando viene ricaricata la pagina con uno swipe down, vengono ricaricati tutti i movimenti*/
        reminderSwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadReminders();
                    }
                }
        );

        return root;
    }

    private void loadReminders() {

        DatabaseReference reffUsers = FirebaseDatabase.getInstance().getReference().child(User.USERS).child(mAuth.getUid()).child("mygroups");
        final DatabaseReference reffReminders = FirebaseDatabase.getInstance().getReference().child(Reminder.REMINDERS);
        final DatabaseReference reffGroup = FirebaseDatabase.getInstance().getReference().child(Group.GROUPS);


        final ArrayList<String> myGroups = new ArrayList<>();
        reffUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*lettura dei dati sull'utente per reperire la lista dei gruppi in cui e`*/
                myGroups.clear();
                reminders.clear();

                for (DataSnapshot idGroup : dataSnapshot.getChildren()) {
                    myGroups.add(idGroup.getKey());
                }

                Log.w("letturaGruppo", myGroups.toString());


                for (final String idGroup : myGroups) {

                    Log.w("letturaGruppo", idGroup);

                    reffReminders.child(idGroup).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot idReminder : dataSnapshot.getChildren()) {
                                Reminder reminder = idReminder.getValue(Reminder.class);
                                reminder.setIdReminder(idReminder.getKey());
                                reminder.setNameGroup("");  //default

                                Log.w("test",reminder.toString());

                                //se il promemoria interessa l'utente loggato
                                if (reminder.getUidCreditor().equals(mAuth.getUid()) || reminder.getUidDebitor().equals(mAuth.getUid())) {

                                    /* viene controllato se l'id del promemoria letto è una nuova lettura (in tal caso alreadyRead = -1) o è una modifica di un promemoria gia letto (alreadyRead = id del promemoria)*/
                                    int alreadyRead = Reminder.containsIdReminder(reminders, idReminder.getKey());
                                    if (alreadyRead == -1) {

                                        //aggiunge il promemoria
                                        reminders.add(reminder);
                                    } else {
                                        //viene sostituito il gruppo modificato
                                        reminders.remove(alreadyRead);
                                        reminders.add(alreadyRead, reminder);
                                    }

                                }
                            }

                            //aggiungo il nome del gruppo al promemoria
                            for(final Reminder r: reminders) {
                                reffGroup.child(r.getIdGroup()).child(Group.NAME_GROUP).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        r.setNameGroup(dataSnapshot.getValue(String.class));

                                        reminderAdapter = new ReminderAdapter(reminders, getActivity(), mAuth.getUid());
                                        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        remindersRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                        remindersRecyclerView.setAdapter(reminderAdapter);
                                        reminderSwipeRefresh.setRefreshing(false);

                                        if(reminders.size()==0) {
                                            messageNoReminderTextView.setVisibility(View.VISIBLE);
                                        } else {
                                            messageNoReminderTextView.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getActivity(), R.string.error_db, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        reminderSwipeRefresh.setRefreshing(false);
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