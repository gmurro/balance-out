package it.uniba.di.sms1920.madminds.balanceout.ui.home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import it.uniba.di.sms1920.madminds.balanceout.R;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.shape.OvalShape;

public class HomeFragment extends Fragment {

    private RecyclerView groupsRecyclerView;
    private GroupAdapter groupAdapter;
    private ArrayList<Group> groups;
    private FirebaseAuth mAuth;

    private ImageView helpCardImageView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        groupsRecyclerView = root.findViewById(R.id.groupsHomeRecyclerView);
        helpCardImageView = root.findViewById(R.id.helpCardImageView);
        groups = new ArrayList<>();

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



        /* messaggio di aiuto per comprendere il significato della card relativa a stato debiti/crediti*/
        helpCardImageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v){
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(getString(R.string.title_help_status_debit))
                        .setMessage(getString(R.string.text_help_status_debit))
                        .setPositiveButton(getString(R.string.understand), null)
                        .show();
            }
        });



        if(!isLogged) {

            /*creazione di un gruppo di esempio visibile solo quando l'utente non è loggato*/
            groups.add(new Group(getString(R.string.example_name_group),
                    Calendar.getInstance().getTime(),
                    null,
                    -1
                    ));


        }

        groupAdapter = new GroupAdapter(groups,isLogged, getActivity());

        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        groupsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        groupsRecyclerView.setAdapter(groupAdapter);

        return root;
    }
}