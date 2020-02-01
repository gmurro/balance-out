package it.uniba.di.sms1920.madminds.balanceout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup.GroupActivity;

public class MainActivity extends AppCompatActivity {

    public static final int EXIT_GROUP=29;
    public static final int START_FRAGMENT=0;
    public static final int START_HOME=1;
    public static final int START_NOTIFICATIONS=2;
    public static final int START_ACTIVITY=3;
    public static final int START_PROFILE=4;
    public static final String USER = "User" ;
    public static final String ID_USER = "IdUser" ;
    public static final String DEFAULT_ID_USER = "0000" ;
    private BottomNavigationView navView;


    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        builder.setTitle(R.string.msg_exit)
                .setPositiveButton(R.string.dialog_exit_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.dialog_exit_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

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
                case EXIT_GROUP:
                    View viewAHome = navView.findViewById(R.id.navigation_home);
                    viewAHome.performClick();
                    Snackbar.make(viewAHome, getString(R.string.title_exit_group_done), Snackbar.LENGTH_LONG).show();
                case GroupActivity.GROUP_CANCELLED:
                    Snackbar.make(navView.findViewById(R.id.navigation_home), getString(R.string.title_cancelled_group), Snackbar.LENGTH_LONG).show();

            }

        }
    }

}
