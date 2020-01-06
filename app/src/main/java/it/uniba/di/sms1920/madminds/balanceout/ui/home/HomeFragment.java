package it.uniba.di.sms1920.madminds.balanceout.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import it.uniba.di.sms1920.madminds.balanceout.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView groupsRecyclerView;
    private GroupAdapter groupAdapter;
    private ArrayList<Group> groups;

    /* se l'utente si è precedentemente loggato al sistema, allora isLogged = true,
       altrimenti isLogged = false */
    private boolean isLogged = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        /*
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */

        groups = new ArrayList<>();

        if(!isLogged) {
            /*creazione di un gruppo di esempio visibile solo quando l'utente non è loggato*/
            groups.add(new Group(getString(R.string.example_name_group),
                    Calendar.getInstance().getTime(),
                    null,
                    -1
                    ));

        }
        groupAdapter = new GroupAdapter(groups);

        groupsRecyclerView = (RecyclerView) root.findViewById(R.id.groupsHomeRecyclerView);
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        groupsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        groupsRecyclerView.setAdapter(groupAdapter);

        return root;
    }
}