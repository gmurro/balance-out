package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.DividerItemDecorator;
import it.uniba.di.sms1920.madminds.balanceout.model.Expense;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;
import it.uniba.di.sms1920.madminds.balanceout.model.User;

public class MembersGroupActivity extends AppCompatActivity {

    Group group;
    private ArrayList<User> members;
    private RecyclerView membersGroupRecyclerView;
    private MemberAdapter memberAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
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

        membersGroupRecyclerView = findViewById(R.id.membersGroupRecyclerView);

        /* vengono caricati i membri del gruppo passati dalla schemata precedente */
        members = group.getMembers();

        /* vengono caricati tutti i membri nella recycle view */
        loadMembers();



        MaterialButton inviteMemberButton = findViewById(R.id.inviteMemberButton);
        inviteMemberButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO invita membri
                    }
                }
        );
    }

    private void loadMembers() {
        memberAdapter = new MemberAdapter(members, this, group.getIdAmministrator());

        membersGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        membersGroupRecyclerView.addItemDecoration(new DividerItemDecorator(getDrawable(R.drawable.divider)));
        membersGroupRecyclerView.setItemAnimator(new DefaultItemAnimator());
        membersGroupRecyclerView.setAdapter(memberAdapter);
    }

}
