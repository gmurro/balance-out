package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import it.uniba.di.sms1920.madminds.balanceout.R;


public class ExpenseGroupFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_expense_group, container, false);
        // Inflate the layout for this fragment
        return root;
    }

}