package it.uniba.di.sms1920.madminds.balanceout.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.math.BigDecimal;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.helper.MoneyDivider;
import it.uniba.di.sms1920.madminds.balanceout.ui.profile.ProfileFragment;

public class ActivityFragment extends Fragment {

    private FirebaseAuth mAuth;
    private boolean isLogged;
    private boolean isEmailVerified;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();

        /* vengono mostrati 3 layout diversi a seconda se l'utente è loggato o meno e se quando lo è, non ha verificato l'account tramite mail */
        View root;
        if(!isLogged) {
            root = notLoggedActivityFragment(inflater, container);
        } else {
            if (isEmailVerified) {
                root = loggedActivityFragment(inflater, container);
            } else {
                root = notEmailVerificatedActivityFragment(inflater, container);
            }
        }

        return root;
    }


    public View notLoggedActivityFragment (LayoutInflater inflater, final ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_not_logged, container, false);
        MaterialButton login = root.findViewById(R.id.loginReminderButton);
        final BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);

        login.setOnClickListener(new MaterialButton.OnClickListener() {
            @Override
            public void onClick(View v){

                /* Di seguito il passaggio al fragment del login */
                Fragment newFragment = new ProfileFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                /* Sostituisce tutto ciò che è nel main_fragment con questo fragment
                 * aggiunge la transazione al back stack e fa il commit*/
                transaction.replace(R.id.main_fragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                /* modifica la barra di navigazione selezionando l'icona del profilo*/
                bottomNavigationView.setSelectedItemId(R.id.navigation_profile);

            }
        });
        return root;
    }

    public View notEmailVerificatedActivityFragment (LayoutInflater inflater, final ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_not_email_verificated, container, false);
        MaterialButton emailIntentButton = root.findViewById(R.id.emailIntentButton);
        final BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);

        emailIntentButton.setOnClickListener(new MaterialButton.OnClickListener() {
            @Override
            public void onClick(View v){

                /* Intent che apre la casella di posta elettronica */
                Intent intent = Intent.makeMainSelectorActivity(
                        Intent.ACTION_MAIN,
                        Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);       //la posta elettronica viene aperta separatamente rispetto all'app Balance Out
                startActivity(intent);
            }
        });
        return root;
    }

    public View loggedActivityFragment (LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_activity, container, false);

        final Button button = root.findViewById(R.id.buttonExample);
        button.setOnClickListener(  new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            BigDecimal bd = new BigDecimal("10.0");
                                            Toast.makeText(getContext(), String.format("%.2f",bd).replace(",","."),Toast.LENGTH_LONG).show();
                                        }
                                    }

        );
        return root;
    }

    private void verifyLogged() {
        /* firebaseUser contiene l'informazione relativa all'utente se è loggato o meno */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        /* memorizzo in isLogged l'informazione boolean relativa all'utente se è loggato o meno*/
        if(firebaseUser == null) {
            isLogged = false;
            isEmailVerified = false;
        } else {
            isLogged = true;
            isEmailVerified = firebaseUser.isEmailVerified();
        }
    }
}