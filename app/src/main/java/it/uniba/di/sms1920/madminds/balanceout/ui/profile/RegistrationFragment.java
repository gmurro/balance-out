package it.uniba.di.sms1920.madminds.balanceout.ui.profile;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.uniba.di.sms1920.madminds.balanceout.R;


public class RegistrationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseFunctions mFunctions;


    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

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


    private static final String TAG = "balanceOutTracker";
    private TextInputEditText nameEditText,surnameEditText,emailEditText, passwordEditText, confirmPasswordEditText;
    private TextInputLayout name,surname,email, password, confirmPassword;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleSignIn;
    private View v;
    private static final int RC_SIGN_IN = 9001;
    private Button signIn;
    private CheckBox privacyConfirmCheckBox;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public RegistrationFragment() {
        // Required empty public constructor
    }

    public static RegistrationFragment newInstance(String param1, String param2) {
        RegistrationFragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         v = inflater.inflate(R.layout.fragment_registration, container, false);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]


        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        setProgressDialog();
        mAuth.useAppLanguage();

        email = v.findViewById(R.id.registrationEmailTextInputLayout);
        name = v.findViewById(R.id.registrationNameTextInputLayout);
        surname = v.findViewById(R.id.registrationSurnameTextInputLayout);
        confirmPassword = v.findViewById(R.id.registrationConfirmPasswordTextInputLayout);
        password = v.findViewById(R.id.registrationPasswordTextInputLayout);


        signIn = v.findViewById(R.id.registrationButton);
        googleSignIn = v.findViewById(R.id.registrationGoogleSignInButton);

        privacyConfirmCheckBox = v.findViewById(R.id.privacyConfirmCheckBox);
        nameEditText = v.findViewById(R.id.registrationNameEditText);
        surnameEditText = v.findViewById(R.id.registrationSurnameEditText);
        emailEditText = v.findViewById(R.id.registrationEmailEditText);
        passwordEditText = v.findViewById(R.id.registrationPasswordEditText);
        confirmPasswordEditText = v.findViewById(R.id.registrationConfirmPasswordEditText);

        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                email.setHelperText(getString(R.string.help_registration_email));
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                password.setHelperText(getString(R.string.password_rule));
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                password.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                confirmPassword.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isFieldsError = false;

                if(nameEditText.getText().toString().isEmpty()){
                    isFieldsError = true;
                    nameEditText.setError(getResources().getString(R.string.error_registration_name));

                }
                if(surnameEditText.getText().toString().trim().isEmpty()){
                    isFieldsError = true;
                    surnameEditText.setError(getResources().getString(R.string.error_registration_surname));

                }
                if(emailEditText.getText().toString().trim().isEmpty()){
                    isFieldsError = true;
                    emailEditText.setError(getResources().getString(R.string.error_registration_email));

                }
                if(passwordEditText.getText().toString().trim().isEmpty()){
                    isFieldsError = true;
                    confirmPassword.setPasswordVisibilityToggleEnabled(false);
                    passwordEditText.setError(getResources().getString(R.string.error_registration_passoword));

                }
                if(passwordEditText.getText().toString().trim().isEmpty()){
                    isFieldsError = true;
                    password.setPasswordVisibilityToggleEnabled(false);
                    confirmPasswordEditText.setError(getString(R.string.msg_error_confirm_password));
                }
                if(!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())){
                    isFieldsError = true;
                    password.setPasswordVisibilityToggleEnabled(false);
                    confirmPasswordEditText.setError(getResources().getString(R.string.msg_error_password));
                    
                }
                if(!privacyConfirmCheckBox.isChecked()){
                    isFieldsError = true;
                    Snackbar.make(v, getString(R.string.msg_error_privacy), Snackbar.LENGTH_LONG).show();
                }
                if(!isFieldsError){
                    createAccount(nameEditText.getText().toString(),
                            surnameEditText.getText().toString(),
                            emailEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }

            }
        });

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.show();
                signIn();
            }
        });

        return v;
    }


    private void createAccount(String name,String surname,String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mProgress.show();

        final String nameAccount = name;
        final String surnameAccount = surname;

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            firebaseUser = mAuth.getCurrentUser();

                            //Scrittura del nome e cognome,
                            // successivamente email e password che concretizzano la regitrazione


                            backToProfile();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), getString(R.string.authentication_failed),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        mProgress.dismiss();
                        // [END_EXCLUDE]
                    }
                }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                writeNameSurname(mAuth.getUid(), nameAccount, surnameAccount);
                if(!firebaseUser.isEmailVerified()) {
                    sendEmailVerification();
                }

                mAuth.signOut();
            }
        });
        // [END create_user_with_email]
    }


    private void writeNameSurname (String key, String name, String surname){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(key);

        Log.w("pippo", "name: "+name+" "+surname);
        databaseReference.child("name").setValue(name);
        databaseReference.child("surname").setValue(surname);

    }


    private boolean validateForm() {
        boolean valid = true;

        String email = emailEditText.getText().toString();
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        if (TextUtils.isEmpty(email) ) {
            emailEditText.setError(getString(R.string.error_registration_email));
            valid = false;
        }
        if(!matcher.find()){
            emailEditText.setError(getString(R.string.email_registration_find));
            valid = false;
        }
        else {
            emailEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        Matcher matcherPassword = VALID_PASSWORD_REGEX.matcher(password);
        if (TextUtils.isEmpty(password) ) {
            passwordEditText.setError(getString(R.string.error_registration_passoword));
            valid = false;
        }if(!matcherPassword.find()){
            passwordEditText.setError(getString(R.string.helper_rules_password));
            valid = false;
        }
        else {
            passwordEditText.setError(null);
        }

        return valid;
    }


    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mProgress.dismiss();
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getActivity(), getString(R.string.authentication_success),
                                    Toast.LENGTH_SHORT).show();
                            if(!user.isEmailVerified()) {
                                sendEmailVerification();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), getString(R.string.authentication_failed),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        mProgress.dismiss();
                        // [END_EXCLUDE]
                    }
                }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                backToProfile();
            }
        });
    }

    private void backToProfile() {
        Fragment newFragment = new ProfileFragment();
        // consider using Java coding conventions (upper first char class names!!!)
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(getId(), newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }
    // [END auth_with_google]


    // [START signin]
    private void signIn() {
        mProgress.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]


    private void sendEmailVerification() {
        // Disable button
        //findViewById(R.id.verifyEmailButton).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
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
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(getActivity(),
                                    getString(R.string.failed_verification_email),
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }


    private void setProgressDialog() {
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage(getString(R.string.loading_access)); // Setting Message
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Horizontal
        mProgress.setCancelable(false);
    }
}
