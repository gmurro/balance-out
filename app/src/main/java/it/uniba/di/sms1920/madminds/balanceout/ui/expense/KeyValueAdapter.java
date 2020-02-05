package it.uniba.di.sms1920.madminds.balanceout.ui.expense;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.KeyValueItem;

public class KeyValueAdapter extends ArrayAdapter<KeyValueItem> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<KeyValueItem> items;
    private final int mResource;

    public KeyValueAdapter(@NonNull Context context, @LayoutRes int resource,
                           @NonNull List objects) {
        super(context, resource, 0, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(mResource, parent, false);
        try {

            KeyValueItem item = items.get(position);
            TextView text = view.findViewById(R.id.text1);
            text.setText(item.getValue());
        }catch (Exception e) {
            Log.w("test",e.toString());
        }
        return view;
    }
}