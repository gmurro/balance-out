package it.uniba.di.sms1920.madminds.balanceout.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.ui.profile.ProfileFragment;

public class NotificationsFragment extends Fragment {
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        /* firebaseUser contiene l'informazione relativa all'utente se è loggato o meno */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        /* memorizzo in logged l'informazione boolean relativa all'utente se è loggato o meno*/
        boolean isLogged;
        if(firebaseUser == null) {
            isLogged = false;
        } else {
            isLogged = true;
        }

        /* vengono mostrati due layout diversi a seconda se l'utente è loggato o meno */
        View root;
        if(!isLogged) {
            root = notLoggedNotificationFragment(inflater, container);
        } else {
            root = loggedNotificationFragment(inflater, container);
        }

        return root;
    }

    public View notLoggedNotificationFragment (LayoutInflater inflater, final ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_not_logged, container, false);
        MaterialButton login = root.findViewById(R.id.loginNotificationsButton);
        login.setOnClickListener(new MaterialButton.OnClickListener() {
            @Override
            public void onClick(View v){
                Fragment newFragment = new ProfileFragment();
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
        return root;
    }

    public View loggedNotificationFragment (LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_notifications_logged, container, false);
        return root;
    }
}