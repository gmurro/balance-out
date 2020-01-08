package it.uniba.di.sms1920.madminds.balanceout;

import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import it.uniba.di.sms1920.madminds.balanceout.ui.activity.ActivityFragment;
import it.uniba.di.sms1920.madminds.balanceout.ui.home.HomeFragment;
import it.uniba.di.sms1920.madminds.balanceout.ui.notifications.NotificationsFragment;
import it.uniba.di.sms1920.madminds.balanceout.ui.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    public static final int START_FRAGMENT=0;
    public static final int START_HOME=1;
    public static final int START_NOTIFICATIONS=2;
    public static final int START_ACTIVITY=3;
    public static final int START_PROFILE=4;
    private BottomNavigationView navView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.homePageToolbar);
        setSupportActionBar(myToolbar);

        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_activity, R.id.navigation_notifications, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.main_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.settingsApp:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.START_FRAGMENT) {

            switch (resultCode){
                case START_HOME:
                    break;
                case START_ACTIVITY:
                    View viewActivity = navView.findViewById(R.id.navigation_activity);
                    viewActivity.performClick();
                    break;
                case START_NOTIFICATIONS:
                    View viewNotifications = navView.findViewById(R.id.navigation_notifications);
                    viewNotifications.performClick();
                    break;
                case START_PROFILE:
                    View viewProfile = navView.findViewById(R.id.navigation_profile);
                    viewProfile.performClick();
                    break;
            }

        }

    }

}
