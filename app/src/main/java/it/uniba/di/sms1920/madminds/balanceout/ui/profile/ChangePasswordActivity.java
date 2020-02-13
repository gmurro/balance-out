package it.uniba.di.sms1920.madminds.balanceout.ui.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
    private ConstraintLayout layout;
    private String password;
    private String oldPassword;
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
        layout = (ConstraintLayout) findViewById(R.id.changePasswordConstraintLayout);

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

        oldPasswordTextInputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldPasswordTextInputLayout.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newPasswordTextInputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                newPasswordTextInputLayout.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPasswordTextInputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                confirmPasswordTextInputLayout.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendNewPasswordMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                password = newPasswordTextInputEdit.getText().toString();
                oldPassword = oldPasswordTextInputEdit.getText().toString();
                boolean isFieldsError = false;
                Matcher matcherPassword = VALID_PASSWORD_REGEX.matcher(password);

                if(oldPasswordTextInputEdit.getText().toString().trim().isEmpty()){
                    isFieldsError = true;
                    oldPasswordTextInputLayout.setPasswordVisibilityToggleEnabled(false);
                    oldPasswordTextInputEdit.setError(getString(R.string.msg_error_old_password));
                }
                if(newPasswordTextInputEdit.getText().toString().trim().isEmpty()){
                    isFieldsError = true;
                    newPasswordTextInputLayout.setPasswordVisibilityToggleEnabled(false);
                    newPasswordTextInputEdit.setError(getString(R.string.error_registration_passoword));

                }
                if(!matcherPassword.find()){
                    isFieldsError = true;
                    newPasswordTextInputLayout.setPasswordVisibilityToggleEnabled(false);
                    newPasswordTextInputEdit.setError(getString(R.string.helper_rules_password));

                }
                if(!confirmPasswordTextInputEdit.getText().toString().equals(newPasswordTextInputEdit.getText().toString())){
                    isFieldsError = true;
                    confirmPasswordTextInputLayout.setPasswordVisibilityToggleEnabled(false);
                    confirmPasswordTextInputEdit.setError(getString(R.string.msg_error_password));

                }
                if(confirmPasswordTextInputEdit.getText().toString().trim().isEmpty()){
                    isFieldsError = true;
                    confirmPasswordTextInputLayout.setPasswordVisibilityToggleEnabled(false);
                    confirmPasswordTextInputEdit.setError(getText(R.string.msg_error_confirm_password));

                }
                if(!confirmPasswordTextInputEdit.getText().toString().equals(newPasswordTextInputEdit.getText().toString())){
                    isFieldsError = true;
                    confirmPasswordTextInputEdit.setError(getString(R.string.msg_error_password));

                }
                if(!isFieldsError){
                    newPassword = confirmPasswordTextInputEdit.getText().toString();

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(firebaseUser.getEmail(), oldPassword);

                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "User re-authenticated.");



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
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            oldPasswordTextInputEdit.setError(getString(R.string.msg_error_old_password_new_password));
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar.make(layout, getString(R.string.msg_change_password_ok), Snackbar.LENGTH_LONG).show();
                            finish();
                        }
                    });








                }
            }
        });


    }




}
