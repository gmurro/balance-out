package it.uniba.di.sms1920.madminds.balanceout.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.uniba.di.sms1920.madminds.balanceout.MainActivity;
import it.uniba.di.sms1920.madminds.balanceout.R;

public class ChangePasswordActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.changePasswordToolbar);
        setSupportActionBar(myToolbar);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_activity, R.id.navigation_notifications, R.id.navigation_profile)
                .build();


    }




}
