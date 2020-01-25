package it.uniba.di.sms1920.madminds.balanceout.ui.profile;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import it.uniba.di.sms1920.madminds.balanceout.MainActivity;
import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.CircleTrasformation;
import it.uniba.di.sms1920.madminds.balanceout.model.User;
import it.uniba.di.sms1920.madminds.balanceout.ui.settings.SettingsActivity;

public class ProfileFragment extends Fragment {

    private static final int RC_SIGN_IN = 9001;
    public static final int LOGOUT_ID = 107;

    private static final String TAG = "balanceOutTracker";
    private TextInputEditText emailEditText, passwordEditText;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    private boolean isLogged;
    private boolean isEmailVerified;
    private FirebaseUser firebaseUser;
    private GoogleSignInClient mGoogleSignInClient;

    private View root;
    private TextInputEditText nameProfileTextInputEditText;
    private TextInputEditText surnameProfileEditText;
    private TextInputEditText emailProfileEditText;
    private MaterialButton modifyProfileMaterialButton;
    private MaterialButton saveModifyProfileMaterialButton;

    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuInflater menuInflater = ((MainActivity)getActivity()).getMenuInflater();
        if(isLogged) {
            menuInflater.inflate(R.menu.settings_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.settingsApp:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivityForResult(intent, LOGOUT_ID );
                break;
            case R.id.modifyProfileButton:
                nameProfileTextInputEditText = root.findViewById(R.id.nameProfileEditText);
                surnameProfileEditText = root.findViewById(R.id.surnameProfileEditText);
                emailProfileEditText = root.findViewById(R.id.emailProfileEditText);
                modifyProfileMaterialButton = root.findViewById(R.id.modifyPasswordMaterialButton);
                saveModifyProfileMaterialButton = root.findViewById(R.id.saveModifyProfileMaterialButton);

                nameProfileTextInputEditText.setFocusable(true);
                surnameProfileEditText.setFocusable(true);
                emailProfileEditText.setFocusable(true);

                modifyProfileMaterialButton.setVisibility(View.GONE);
                saveModifyProfileMaterialButton.setVisibility(View.VISIBLE);
        }
        return true;
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        /*inizializzo mAuth e verifico se esiste gia` un istanza di questo utente, ovvero se l'utente
        * e' gia' registrato*/
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity().getApplicationContext(), gso);

        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();


        if(isLogged) {

            View root = getViewAlreadyLogin(inflater, container, firebaseUser);
            return root;

        } else {
            View root = getViewLogin(inflater, container);

            return root;
        }

    }

    private void verifyLogged() {
        /* firebaseUser contiene l'informazione relativa all'utente se è loggato o meno */
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mAuth.useAppLanguage();

        /* memorizzo in isLogged l'informazione boolean relativa all'utente se è loggato o meno*/
        if(firebaseUser == null) {
            isLogged = false;
            isEmailVerified = false;
        } else {
            isLogged = true;
            isEmailVerified = firebaseUser.isEmailVerified();
        }

    }



    private View getViewLogin(@NonNull final LayoutInflater inflater, final ViewGroup container) {
        root = inflater.inflate(R.layout.fragment_login, container, false);

        TextView registrationText = root.findViewById(R.id.notRegisteredTextView);
        TextView lostPasswordText = root.findViewById(R.id.lostPasswordTextView);
        Button login = root.findViewById(R.id.registrationButton);
        SignInButton google = root.findViewById(R.id.googleSignInButton);
        emailEditText = root.findViewById(R.id.registrationEmailEditText);
        passwordEditText = root.findViewById(R.id.registrationPasswordEditText);

        setProgressDialog();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()) {
                    mProgress.show();
                    loginEmail(emailEditText.getText().toString(), passwordEditText.getText().toString());

                }

            }
        });

        registrationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent registration = new Intent(getActivity(), RegistrationActivity.class);
                startActivity(registration);*/

                Fragment newFragment = new RegistrationFragment();
                // consider using Java coding conventions (upper first char class names!!!)
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(container.getId(), newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });


        lostPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordReset("alessio.tart@gmail.com");
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.show();
                signIn();
            }
        });

        return root;
    }


    private void passwordReset(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Email sent",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }

    private View getViewAlreadyLogin(@NonNull LayoutInflater inflater, ViewGroup container, FirebaseUser firebaseUser) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);


        saveModifyProfileMaterialButton = root.findViewById(R.id.saveModifyProfileMaterialButton);
        ActionBar actionBar = getActivity().getActionBar();
        final TextView emailTest, surnameTextView, nameTextView;
        final ImageView profileImagevView;
        Button logout;

        emailTest = root.findViewById(R.id.emailProfileEditText);
        surnameTextView = root.findViewById(R.id.surnameProfileEditText);
        nameTextView = root.findViewById(R.id.nameProfileEditText);
        profileImagevView = root.findViewById(R.id.profileImageView);

        /*
        emailTest.setText(firebaseUser.getEmail());
        surnameTextView.setText(firebaseUser.getDisplayName());
         */


        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                /*
                User user;
                user = dataSnapshot.getValue(User.class);

                surnameProfileEditText.setText(user.getSurname());
                nameProfileTextInputEditText.setText(user.getName());
                emailProfileEditText.setText(user.getEmail());
                user.getPicture();
                */

                //TODO leggere una immagine e caricarla sul db/ dal db

                nameTextView.setText(dataSnapshot.child("name").getValue().toString());
                surnameTextView.setText(dataSnapshot.child("surname").getValue().toString());
                emailTest.setText(dataSnapshot.child("email").getValue().toString());

                String filePath = dataSnapshot.child("picture").getValue().toString();
                //profileImagevView.m
                Picasso.get().load(filePath).fit().centerInside().transform(new CircleTrasformation()).into(profileImagevView);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });




        saveModifyProfileMaterialButton.setOnClickListener(new MaterialButton.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(nameProfileTextInputEditText.getText().toString().isEmpty() || surnameProfileEditText.getText().toString().isEmpty() ||
                    emailProfileEditText.getText().toString().isEmpty() ){
                    Toast.makeText(getActivity(), R.string.title_message_error_empty,
                            Toast.LENGTH_SHORT).show();
                }else{
                    modifyProfileMaterialButton.setVisibility(View.VISIBLE);
                    saveModifyProfileMaterialButton.setVisibility(View.GONE);
                }

            }
        });

        return root;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        switch (requestCode) {
            case RC_SIGN_IN:
                mProgress.dismiss();
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
                break;
            case LOGOUT_ID:
                getActivity().recreate();
            break;
        }
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getActivity(), "Authentication Successfull",
                                    Toast.LENGTH_SHORT).show();
                            if(!user.isEmailVerified()) {
                                sendEmailVerification();
                            }

                            backToProfile();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication Failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        mProgress.dismiss();
                        // [END_EXCLUDE]
                    }
                });

    }



    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]





    private void setProgressDialog() {
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage(getString(R.string.loading_access)); // Setting Message
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Horizontal
        mProgress.setCancelable(false);
    }



    private void loginEmail(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getActivity(), "Authentication successfull",
                                    Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();

                            backToProfile();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();
                        }

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

        // Commit the transaction
        transaction.commit();
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Required.");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }


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
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(getActivity(),
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }



    @Override
    public void onStop() {
        super.onStop();
    }

}