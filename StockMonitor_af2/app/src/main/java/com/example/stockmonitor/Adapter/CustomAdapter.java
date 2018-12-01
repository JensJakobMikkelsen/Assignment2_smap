package com.example.stockmonitor.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.stockmonitor.Bookmodel_for_service.ListModel;
import com.example.stockmonitor.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<ListModel> {

    //View updateView;
    //ViewGroup updateParent;

    public CustomAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        //updateView = convertView;
        //updateParent = parent;

        ListModel user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_stock, parent, false);
        }


        // Lookup view for data population
        TextView name = convertView.findViewById(R.id.stock_name);
        TextView price = convertView.findViewById(R.id.stock_price);
        TextView c_price = convertView.findViewById(R.id.stock_c);

        // Populate the data into the template view using the data object
        name.setText(user.getName());
        price.setText(user.getPrice());
        c_price.setText(user.getC_price());
        // Return the completed view to render on screen
        return convertView;
    }

    public void updateAll(String price, View convertView, ViewGroup parent)
    {
        /*
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_stock, parent, false);
        }
        */


        TextView price_ = convertView.findViewById(R.id.stock_price);
        price_.setText(price);
    }
}