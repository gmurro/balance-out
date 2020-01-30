package it.uniba.di.sms1920.madminds.balanceout.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.uniba.di.sms1920.madminds.balanceout.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText oldPasswordTextInputEdit;
    private TextInputEditText newPasswordTextInputEdit;
    private TextInputEditText confirmPasswordTextInputEdit;
    private TextInputLayout oldPasswordTextInputLayout;
    private TextInputLayout newPasswordTextInputLayout;
    private TextInputLayout confirmPasswordTextInputLayout;
    private MaterialButton sendNewPasswordMaterialButton;
    private static final String TAG = "balanceOutTracker";
    private String password;
    private String newPassword;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    public static final Pattern VALID_PASSWORD_REGEX =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$");
    /*^                 # start-of-string
    (?=.*[0-9])       # a digit must occur at least once
    (?=.*[a-z])       # a lower case letter must occur at least once
    (?=.*[A-Z])       # an upper case letter must occur at least once
    (?=.*[!@#$%^&+=])  # a special character must occur at least once
    (?=\S+$)          # no whitespace allowed in the entire string
    .{8,}             # anything, at least eight places though
$                 # end-of-string*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = findViewById(R.id.changePasswordToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        oldPasswordTextInputEdit = findViewById(R.id.oldPasswordEditText);
        newPasswordTextInputEdit = findViewById(R.id.newPasswordEditText);
        confirmPasswordTextInputEdit = findViewById(R.id.confirmNewPasswordEditText);
        oldPasswordTextInputLayout = findViewById(R.id.oldPasswordTextInputLayout);
        newPasswordTextInputLayout = findViewById(R.id.newPasswordTextInputLayout);
        confirmPasswordTextInputLayout = findViewById(R.id.confirmNewPasswordTextInputLayout);
        sendNewPasswordMaterialButton = findViewById(R.id.sendNewPasswordMaterialButton);

        oldPasswordTextInputEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                oldPasswordTextInputLayout.setHelperText(getString(R.string.helper_old_password));
            }
        });

        newPasswordTextInputEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                newPasswordTextInputLayout.setHelperText(getString(R.string.password_rule));
            }
        });

        confirmPasswordTextInputEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                confirmPasswordTextInputLayout.setHelperText(getString(R.string.helper_confirm_password));
            }
        });

        sendNewPasswordMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                password = newPasswordTextInputEdit.getText().toString();
                boolean isFieldsError = false;
                Matcher matcherPassword = VALID_PASSWORD_REGEX.matcher(password);

                
                if(oldPasswordTextInputEdit.getText().toString().trim().isEmpty()){
                    isFieldsError = true;
                    oldPasswordTextInputEdit.setError(getString(R.string.msg_error_old_password));

                }
                if(newPasswordTextInputEdit.getText().toString().trim().isEmpty()){
                    isFieldsError = true;
                    newPasswordTextInputEdit.setError(getString(R.string.msg_error_password));

                }
                if(!matcherPassword.find()){
                    isFieldsError = true;
                    confirmPasswordTextInputEdit.setError(getString(R.string.msg_error_confirm_password));
                }
                if(confirmPasswordTextInputEdit.getText().toString().trim().isEmpty()){
                    isFieldsError = true;
                    confirmPasswordTextInputEdit.setError(getText(R.string.msg_error_confirm_password));

                }
                if(confirmPasswordTextInputEdit.getText().toString().equals(newPasswordTextInputEdit.getText().toString())){
                    isFieldsError = true;
                    confirmPasswordTextInputEdit.setError(getString(R.string.msg_error_password));

                }
                if(!isFieldsError){
                    newPassword = confirmPasswordTextInputEdit.getText().toString();
                    firebaseUser.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User password updated.");
                            }
                        }
                    });
                }
            }
        });


    }




}
