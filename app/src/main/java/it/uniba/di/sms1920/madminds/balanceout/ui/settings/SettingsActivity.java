package it.uniba.di.sms1920.madminds.balanceout.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.ui.profile.ProfileFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Toolbar toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.settingsFragment, new MySettingsFragment())
                .commit();

    }



    public static class MySettingsFragment extends PreferenceFragmentCompat {
        private FirebaseAuth mAuth;
        private Context context;
        private Preference infoSettings;
        private DatabaseReference databaseReference;
        private String userToken = "";
        private final String LOGOUT = "Logout";
        private static final String TAG = "balanceOutTracker";


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);

            databaseReference = FirebaseDatabase.getInstance().getReference().child("token").child("userToken");
            getToken();

            Preference notificationSetting = findPreference("notificationsSetting");
            notificationSetting.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent intent = new Intent();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("app_package", context.getPackageName());
                        intent.putExtra("app_uid", context.getApplicationInfo().uid);
                    } else {
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                    }
                    startActivity(intent);
                    return true;
                }
            });

            Preference logoutProfile = findPreference("logout");
            logoutProfile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    databaseReference.child(userToken).removeValue();

                    Toast.makeText(getActivity(), "Logout eseguito",
                            Toast.LENGTH_SHORT).show();

                    getActivity().setResult(ProfileFragment.LOGOUT_ID);
                    getActivity().finish();
                    return true;
                }
            });

            infoSettings = findPreference("infoSettings");
            infoSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                    builder.setView(R.layout.dialog_info_settings)
                            .setPositiveButton(R.string.agree_settings, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();

                    return true;
                }
            });

        }


        private void getToken(){
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.i(TAG, "error set token 'null'", task.getException());
                                return;
                            }

                            userToken = task.getResult().getToken();
                            Log.i(TAG, "token attuale "+ userToken);
                        }
                    });
        }

        @Override
        public void onAttach(Context activity) {
            super.onAttach(activity);
            context = activity;
        }



    }

}
