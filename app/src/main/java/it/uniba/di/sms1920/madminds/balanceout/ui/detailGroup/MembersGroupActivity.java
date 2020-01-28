package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
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

import java.util.ArrayList;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.DividerItemDecorator;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.User;

public class MembersGroupActivity extends AppCompatActivity {

    Group group;
    private ArrayList<User> members;
    private RecyclerView membersGroupRecyclerView;
    private MemberAdapter memberAdapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference reffUsers;
    private String groupId;

    private Uri mInvitationUrl;
    private static final String TAG = "deepLink";

    private String link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_group);

        // Validate that the developer has set the app code.
        //validateAppCode();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();



        Toolbar toolbar = findViewById(R.id.membersGroupToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* viene modificata la toolbar con il nome del gruppo */
        group = (Group) getIntent().getExtras().getSerializable(Group.GROUP);
        getSupportActionBar().setTitle(group.getNameGroup());

        reffUsers = FirebaseDatabase.getInstance().getReference().child("users");

        membersGroupRecyclerView = findViewById(R.id.membersGroupRecyclerView);

        /* vengono caricati i membri del gruppo passati dalla schemata precedente */
        //members = group.getMembers();

        /* vengono caricati tutti i membri nella recycle view */
        loadMembers();

        groupId = group.getIdGroup();
        createLink();

        MaterialButton inviteMemberButton = findViewById(R.id.inviteMemberButton);
        inviteMemberButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareDeepLink(mInvitationUrl.toString());
                    }
                }
        );


        // [END_EXCLUDE]


    }

    private void loadMembers() {

        for(String uidMember : group.getUidMembers()) {
            reffUsers.child(uidMember).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    group.addMember(dataSnapshot.getValue(User.class));

                    memberAdapter = new MemberAdapter(group.getMembers(), MembersGroupActivity.this, group.getIdAdministrator());

                    membersGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    membersGroupRecyclerView.addItemDecoration(new DividerItemDecorator(getDrawable(R.drawable.divider)));
                    membersGroupRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    membersGroupRecyclerView.setAdapter(memberAdapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void shareDeepLink(String deepLink) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.join_message));
        intent.putExtra(Intent.EXTRA_TEXT, deepLink);

        startActivity(intent);
    }


    public void createLink() {
        // [START ddl_referral_create_link]

        link = getString(R.string.base_dynamic_link) + "?groupId=" + groupId;
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix(getString(R.string.base_dynamic_link))
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(getPackageName())
                                .setMinimumVersion(21)
                                .build())
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        mInvitationUrl = shortDynamicLink.getShortLink();

                    }
                });
        // [END ddl_referral_create_link]
    }

}
