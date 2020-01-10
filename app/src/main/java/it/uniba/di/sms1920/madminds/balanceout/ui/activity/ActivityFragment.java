package it.uniba.di.sms1920.madminds.balanceout.ui.activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.ui.profile.ProfileFragment;

public class ActivityFragment extends Fragment {

    private FirebaseAuth mAuth;
    private boolean isLogged;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();

        /* vengono mostrati due layout diversi a seconda se l'utente è loggato o meno */
        View root;
        if(!isLogged) {
            root = notLoggedActivityFragment(inflater, container);
        } else {
            root = loggedActivityFragment(inflater, container);
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

    public View loggedActivityFragment (LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_activity, container, false);
        return root;
    }

    private void verifyLogged() {
        /* firebaseUser contiene l'informazione relativa all'utente se è loggato o meno */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        /* memorizzo in isLogged l'informazione boolean relativa all'utente se è loggato o meno*/
        if(firebaseUser == null) {
            isLogged = false;
        } else {
            isLogged = true;
        }
    }
}