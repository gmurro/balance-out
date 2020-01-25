package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.Group;

public class MembersGroupActivity extends AppCompatActivity {

    Group group;

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
}
