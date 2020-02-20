package it.uniba.di.sms1920.madminds.balanceout.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import it.uniba.di.sms1920.madminds.balanceout.MainActivity;
import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.MetadateGroup;
import it.uniba.di.sms1920.madminds.balanceout.ui.expense.NewExpenseActivity;
import it.uniba.di.sms1920.madminds.balanceout.ui.joinGroup.SenderBtActivity;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private RecyclerView groupsRecyclerView;
    private LinearLayout noItemsLayout;
    private GroupAdapter groupAdapter;
    private ArrayList<Group> groups;
    //gruppi in cui l'utente e` presente
    private ArrayList<MetadateGroup> myGroups;
    private FirebaseAuth mAuth;
    private boolean isLogged;
    private boolean isEmailVerified;

    private ProgressDialog mProgress;
    private int i = 0; //variabile usatata per contare i gruppi letti inizialmente

    //database references
    private DatabaseReference reffUsers;
    private DatabaseReference reffGruops;

    private TextView titleCardStatusDebitTextView, subtitleCardStatusDebitTextView;
    private ImageView helpCardImageView, imgCardStatusDebitImageView;
    private SwipeRefreshLayout homeSwipeRefresh;
    private View root;

    private FloatingActionButton homeExpandableFab, createGroupFab, newExpenseHomeFab, joinGroupFab;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock, text_fab_open, text_fab_close;
    private MaterialCardView descriptionCreateGroupFabTextView, descriptionNewExpenseFabTextView, descriptionJoinGroupFabTextView;
    private boolean isOpenFab = false;

    private String link;
    private Uri mInvitationUrl;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();

        /* vengono mostrati 2 layout diversi a seconda se l'utente ha verificato l'account tramite mail o no*/
        final View root;
        if (!isLogged) {
            root = homeFragment(inflater, container);
        } else {
            if (isEmailVerified) {
                root = homeFragment(inflater, container);
            } else {
                root = notEmailVerificatedHomeFragment(inflater, container);
            }

        }

        return root;
    }

    private View notEmailVerificatedHomeFragment(LayoutInflater inflater, final ViewGroup container) {
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

    private View homeFragment(LayoutInflater inflater, final ViewGroup container) {
        root = inflater.inflate(R.layout.fragment_home, container, false);

        /* vengono inizializzate tutte le view nel fragment*/
        inizializeViews(root);


        groups = new ArrayList<>();
        myGroups = new ArrayList<>();

        if (isLogged) {
            i = 0;
            setProgressDialog();
            mProgress.show();
        }

        /* vengono caricati tutti i gruppi nella recycle view */
        loadGroups();

        /* messaggio di aiuto per comprendere il significato della card relativa a stato debiti/crediti*/
        helpCardImageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(getString(R.string.title_help_status_debit))
                        .setMessage(getString(R.string.text_help_status_debit))
                        .setPositiveButton(getString(R.string.understand), null)
                        .show();
            }
        });

        /* listener in ascolto dei clic sui bottone per creare i gruppi o per unirsi a gruppi esistenti */
        homeFabClicked(root);
        createGroupFabClicked();
        newExpenseHomeFabClicked();
        joinGroupFabClicked();


        /* quando viene ricaricata la pagina con uno swipe down, vengono ricaricati tutti i gruppi*/
        homeSwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadGroups();
                    }
                }
        );

        return root;
    }

    private void inizializeViews(View root) {
        imgCardStatusDebitImageView = root.findViewById(R.id.imgCardStatusDebitImageView);
        titleCardStatusDebitTextView = root.findViewById(R.id.titleCardStatusDebitTextView);
        subtitleCardStatusDebitTextView = root.findViewById(R.id.subtitleCardStatusDebitTextView);
        groupsRecyclerView = root.findViewById(R.id.groupsHomeRecyclerView);
        helpCardImageView = root.findViewById(R.id.helpCardImageView);
        homeSwipeRefresh = root.findViewById(R.id.homeSwipeRefresh);
        homeExpandableFab = root.findViewById(R.id.homeExpandableFab);
        createGroupFab = root.findViewById(R.id.createGroupFab);
        joinGroupFab = root.findViewById(R.id.joinGroupFab);
        newExpenseHomeFab = root.findViewById(R.id.newExpenseHomeFab);
        descriptionJoinGroupFabTextView = root.findViewById(R.id.descriptionJoinGroupFabTextView);
        descriptionCreateGroupFabTextView = root.findViewById(R.id.descriptionCreateGroupFabTextView);
        descriptionNewExpenseFabTextView = root.findViewById(R.id.descriptionNewExpenseHomeFabTextView);
        noItemsLayout = root.findViewById(R.id.noGroupsLayout);
        noItemsLayout.setVisibility(View.GONE);

        /* animazioni per l'espansione del bottone per aggiungere i gruppi */
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_anticlock);
        text_fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.text_fab_open);
        text_fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.text_fab_close);
    }

    private void newExpenseHomeFabClicked() {

        newExpenseHomeFab.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myGroups.size() == 0) {
                    //se non ci sono gruppi non fa aggiungere spese
                    Snackbar.make(root.findViewById(R.id.statusDebitCard), getString(R.string.title_error_new_expense_no_groups), Snackbar.LENGTH_LONG).show();
                } else {
                    Intent newExpense = new Intent(getActivity(), NewExpenseActivity.class);
                    startActivity(newExpense);
                }
            }
        });
    }

    private void createGroupFabClicked() {
        createGroupFab.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newGroup = new Intent(getActivity(), NewGroupActivity.class);
                startActivityForResult(newGroup, MainActivity.GROUP_CREATED);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.GROUP_CREATED && resultCode == RESULT_OK) {
            Log.w("pippo", "tornato bene da creazione gruppo");
            String result = data.getStringExtra(Group.ID_GROUP);
            createLink(result);
        }
    }

    private void shareDeepLink(String deepLink) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.join_message));
        intent.putExtra(Intent.EXTRA_TEXT, deepLink);

        startActivity(intent);

    }


    public void createLink(String groupId) {
        // [START ddl_referral_create_link]

        link = getString(R.string.base_dynamic_link) + "?groupId=" + groupId;
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix(getString(R.string.base_dynamic_link))
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("it.uniba.di.sms1920.madminds.balanceout")
                                .setMinimumVersion(21)
                                .build())
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        mInvitationUrl = shortDynamicLink.getShortLink();
                        shareDeepLink(mInvitationUrl.toString());
                    }
                });
        // [END ddl_referral_create_link]
    }

    private void joinGroupFabClicked() {
        joinGroupFab.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent joinGroup = new Intent(getActivity(), SenderBtActivity.class);
                //joinGroup.putExtra(Group.ID_GROUP, "null");
                startActivity(joinGroup);
            }
        });
    }


    private void homeFabClicked(final View root) {
        /* quando viene premuto il bottone pe raggiungere i gruppi */
        homeExpandableFab.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* viene visualizzato un messaggio di errore se l'utente non è loggato */
                if (!isLogged) {
                    Snackbar.make(root, getString(R.string.not_logged_message_add_group), Snackbar.LENGTH_LONG).show();
                } else {

                    /* altrimenti il bottone viene esploso (con le animazioni) e vengono visualizzati i bottoni per unirsi e creare un gruppo */
                    if (isOpenFab) {

                        descriptionCreateGroupFabTextView.startAnimation(text_fab_close);
                        descriptionNewExpenseFabTextView.startAnimation(text_fab_close);
                        descriptionJoinGroupFabTextView.startAnimation(text_fab_close);
                        joinGroupFab.startAnimation(fab_clock);
                        newExpenseHomeFab.startAnimation(fab_close);
                        createGroupFab.startAnimation(fab_close);
                        homeExpandableFab.startAnimation(fab_anticlock);
                        newExpenseHomeFab.setClickable(false);
                        createGroupFab.setClickable(false);
                        joinGroupFab.setClickable(false);
                        joinGroupFab.setVisibility(View.GONE);
                        newExpenseHomeFab.setVisibility(View.GONE);
                        createGroupFab.setVisibility(View.GONE);

                        isOpenFab = false;
                    } else {

                        descriptionCreateGroupFabTextView.setVisibility(View.VISIBLE);
                        descriptionNewExpenseFabTextView.setVisibility(View.VISIBLE);
                        descriptionJoinGroupFabTextView.setVisibility(View.VISIBLE);
                        descriptionCreateGroupFabTextView.startAnimation(text_fab_open);
                        descriptionNewExpenseFabTextView.startAnimation(text_fab_open);
                        descriptionJoinGroupFabTextView.startAnimation(text_fab_open);
                        newExpenseHomeFab.setVisibility(View.VISIBLE);
                        createGroupFab.setVisibility(View.VISIBLE);
                        joinGroupFab.setVisibility(View.VISIBLE);
                        newExpenseHomeFab.startAnimation(fab_open);
                        createGroupFab.startAnimation(fab_open);
                        joinGroupFab.startAnimation(fab_open);
                        homeExpandableFab.startAnimation(fab_clock);
                        newExpenseHomeFab.setClickable(true);
                        createGroupFab.setClickable(true);
                        joinGroupFab.setClickable(true);
                        isOpenFab = true;
                    }
                }
            }
        });
    }

    public void loadGroups() {

        /* la lista viene pulita poiche altrimenti ogni volta ce si ricarica la pagina
         *  verrebbero aggiunti gli stessi gruppi */
        groups.clear();

        if (!isLogged) {
            /*creazione di un gruppo di esempio visibile solo quando l'utente non è loggato*/
            groups.add(new Group(null, getString(R.string.example_name_group),
                    new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()),
                    null,
                    null,
                    null,
                    MainActivity.DEFAULT_ID_USER,
                    -1,
                    "9.00",
                    true,
                    false,
                    false
            ));

            //viene modificata la card dello stato nei gruppi in base ai debiti
            checkStatusGroups();

            groupAdapter = new GroupAdapter(groups, isLogged, getActivity());
            groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            groupsRecyclerView.setItemAnimator(new DefaultItemAnimator());
            groupsRecyclerView.setAdapter(groupAdapter);


        } else {
            reffUsers = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid()).child("mygroups");
            reffGruops = FirebaseDatabase.getInstance().getReference().child("groups");

            reffUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    /*lettura dei dati sull'utente per reperire la lista dei gruppi in cui e`*/
                    myGroups.clear();
                    groups.clear();

                    for (DataSnapshot idGroup : dataSnapshot.getChildren()) {
                        int status = idGroup.child(MetadateGroup.STATUS_DEBIT_GROUP).getValue(Integer.class);
                        String amount = idGroup.child(MetadateGroup.AMOUNT_DEBIT).getValue(String.class);
                        String id = idGroup.getKey();
                        myGroups.add(
                                new MetadateGroup(status
                                        , amount
                                        , id
                                ));
                    }

                    Log.w("test4", myGroups.toString());

                    //se non ci sono elementi, viene mostrato un messaggio
                    if (myGroups.size() == 0) {
                        mProgress.dismiss();
                        groupsRecyclerView.setVisibility(View.GONE);
                        noItemsLayout.setVisibility(View.VISIBLE);
                    } else {
                        groupsRecyclerView.setVisibility(View.VISIBLE);
                        noItemsLayout.setVisibility(View.GONE);
                    }


                    for (MetadateGroup metadateGroup : myGroups) {

                        final String idGroup = metadateGroup.getIdGroup();
                        final String amountDebt = metadateGroup.getAmountDebit();
                        final int statusDebt = metadateGroup.getStatusDebitGroup();


                        Log.w("test", myGroups.toString());
                        Log.w("letturaGruppo", idGroup);
                        Log.w("letturaGruppo", reffGruops.child(idGroup).toString());

                        reffGruops.child(idGroup).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                try {
                                    Log.w("letturaGruppo", dataSnapshot.toString());
                                    String idGroup = (String) dataSnapshot.child(Group.ID_GROUP).getValue();
                                    boolean active = (boolean) dataSnapshot.child(Group.ACTIVE).getValue();
                                    String creationDataGroup = (String) dataSnapshot.child(Group.CREATION_DATA_GROUP).getValue();
                                    String imgGroup = (String) dataSnapshot.child(Group.IMG_GROUP).getValue();
                                    String nameGroup = (String) dataSnapshot.child(Group.NAME_GROUP).getValue();

                                    Group group = new Group(idGroup, nameGroup, creationDataGroup, imgGroup, null, null, statusDebt, amountDebt, active, false, false);
                                    group.setStatusDebitGroup(statusDebt);
                                    group.setAmountDebit(amountDebt);
                                    Log.w("test", group.toString());



                                    /* viene controllato se l'id del gruppo letto è una nuova lettura (in tal caso alreadyRead = -1) o è una modifica di un gruppo gia letto (alreadyRead = id del gruppo)*/
                                    int alreadyRead = Group.containsUidGroup(groups, idGroup);
                                    if (alreadyRead == -1) {

                                        //se il gruppo è attivo lo aggiunge
                                        if (active) {
                                            groups.add(group);
                                        }
                                        i++;
                                        if (i == myGroups.size()) {
                                            mProgress.dismiss();
                                        }
                                    } else {
                                        //viene sostituito il gruppo modificato
                                        groups.remove(alreadyRead);
                                        if (active) {
                                            groups.add(alreadyRead, group);
                                        }
                                    }

                                    Log.w("letturaGruppo", groups.toString());

                                    //viene modificata la card dello stato nei gruppi in base ai debiti
                                    checkStatusGroups();

                                    groupAdapter = new GroupAdapter(groups, isLogged, getActivity());
                                    groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    groupsRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                    groupsRecyclerView.setAdapter(groupAdapter);
                                } catch (NullPointerException e) {
                                    //lettura errata
                                    Log.w("letturaGruppo", e.toString());
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
                    Toast.makeText(getActivity(), R.string.error_db, Toast.LENGTH_LONG).show();
                }
            });
        }
        homeSwipeRefresh.setRefreshing(false);
    }


    private void verifyLogged() {
        /* firebaseUser contiene l'informazione relativa all'utente se è loggato o meno */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        /* memorizzo in isLogged l'informazione boolean relativa all'utente se è loggato o meno*/
        if (firebaseUser == null) {
            Log.w("pippo", "mAuth: " + mAuth.getUid());
            isLogged = false;
            isEmailVerified = false;
        } else {
            Log.w("pippo", "mAuth: " + mAuth.getUid() + "firebaseUser: " + firebaseUser + " emailVerif: " + firebaseUser.isEmailVerified() + "");
            isLogged = true;
            isEmailVerified = firebaseUser.isEmailVerified();
        }
    }

    private void setProgressDialog() {
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage(getString(R.string.title_loading)); // Setting Message
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Horizontal
        mProgress.setCancelable(false);
    }

    private void checkStatusGroups() {

        int countGroupsDebt = 0;
        int countGroupsCredit = 0;

        /* viene calcolato l'importo del debito che si ha nel gruppo */
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Group group : groups) {

            try {
                BigDecimal amount = BigDecimal.ZERO;
                if (group.getStatusDebitGroup() > 0) {
                    amount = new BigDecimal(group.getAmountDebit());
                    countGroupsCredit++;
                } else if (group.getStatusDebitGroup() < 0) {
                    amount = new BigDecimal(group.getAmountDebit());
                    amount = amount.multiply(new BigDecimal("-1"));
                    countGroupsDebt++;
                }
                totalAmount = totalAmount.add(amount);

            } catch (NumberFormatException e) {
                Log.w("test", "exception at " + group.toString());
            }

        }

        /* viene modificata la card dello stato in base al debito che si ha */
        StringBuilder messageStatusCard = new StringBuilder();
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {

            //viene scritto in quanti gruppi si è in credito ed eventualmente anche quelli in cui si è in debito se ci sono
            messageStatusCard.append(getActivity().getString(R.string.title_you_are_in_credit) + " ");
            messageStatusCard.append(getActivity().getResources().getQuantityString(R.plurals.number_groups, countGroupsCredit, countGroupsCredit));
            if (countGroupsDebt > 0) {
                messageStatusCard.append(" " + getActivity().getString(R.string.and) + " ");
                messageStatusCard.append(getActivity().getString(R.string.title_you_are_in_debt).toLowerCase() + " ");
                messageStatusCard.append(getActivity().getResources().getQuantityString(R.plurals.number_groups, countGroupsDebt, countGroupsDebt));
            }
            messageStatusCard.append(".\n");
            messageStatusCard.append(getActivity().getString(R.string.title_general_status) + " +" + totalAmount + "€.");

            imgCardStatusDebitImageView.setBackgroundResource(R.drawable.credit);
            subtitleCardStatusDebitTextView.setText(messageStatusCard.toString());
        } else if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {

            //viene scritto in quanti gruppi si è in debito ed eventualmente anche quelli in cui si è in credito se ci sono
            messageStatusCard.append(getActivity().getString(R.string.title_you_are_in_debt) + " ");
            messageStatusCard.append(getActivity().getResources().getQuantityString(R.plurals.number_groups, countGroupsDebt, countGroupsDebt));
            if (countGroupsCredit > 0) {
                messageStatusCard.append(" " + getActivity().getString(R.string.and) + " ");
                messageStatusCard.append(getActivity().getString(R.string.title_you_are_in_credit).toLowerCase() + " ");
                messageStatusCard.append(getActivity().getResources().getQuantityString(R.plurals.number_groups, countGroupsCredit, countGroupsCredit));
            }
            messageStatusCard.append(".\n");
            messageStatusCard.append(getActivity().getString(R.string.title_general_status) + " " + totalAmount + "€.");


            imgCardStatusDebitImageView.setBackgroundResource(R.drawable.debit);
            subtitleCardStatusDebitTextView.setText(messageStatusCard);
        } else {
            //viene scritto in quanti gruppi si è in debito ed eventualmente anche quelli in cui si è in credito se ci sono
            messageStatusCard.append(getActivity().getString(R.string.title_you_are_in_debt) + " ");
            messageStatusCard.append(getActivity().getResources().getQuantityString(R.plurals.number_groups, countGroupsDebt, countGroupsDebt));
            if (countGroupsCredit > 0) {
                messageStatusCard.append(" " + getActivity().getString(R.string.and) + " ");
                messageStatusCard.append(getActivity().getString(R.string.title_you_are_in_credit).toLowerCase() + " ");
                messageStatusCard.append(getActivity().getResources().getQuantityString(R.plurals.number_groups, countGroupsCredit, countGroupsCredit));
            }
            messageStatusCard.append(".\n");
            messageStatusCard.append(getActivity().getString(R.string.status_parity));

            imgCardStatusDebitImageView.setBackgroundResource(R.drawable.equal);
            subtitleCardStatusDebitTextView.setText(messageStatusCard);
        }
    }
}