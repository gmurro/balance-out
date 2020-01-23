package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.Movement;


public class OverviewGroupFragment extends Fragment {
    private FirebaseAuth mAuth;
    private boolean isLogged;
    private SwipeRefreshLayout overviewGroupSwipeRefresh;
    private RecyclerView movementsGroupRecyclerView;
    private ImageView helpCardGroupImageView;
    private ArrayList<Movement> movements;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_overview_group, container, false);

        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();

        movementsGroupRecyclerView = root.findViewById(R.id.movementsGroupRecyclerView);
        helpCardGroupImageView = root.findViewById(R.id.helpCardGroupImageView);
        overviewGroupSwipeRefresh = root.findViewById(R.id.overviewGroupSwipeRefresh);



        /* quando viene ricaricata la pagina con uno swipe down, vengono ricaricati tutti i gruppi*/
        overviewGroupSwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadMovements();
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
        } else {
            isLogged = true;
        }
    }

    private void loadMovements(){

    }

}