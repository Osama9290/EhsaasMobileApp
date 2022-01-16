package com.example.ehsaas.UI.Activities.UsedItems.Donor;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.UsedItems.Donor.DonateUsedItem;
import com.example.ehsaas.UI.Fragments.DonationCategory;


public class UsedItemCategory extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_cat, container, false);

        Button clothes = view.findViewById(R.id.cloth);
        Button furni = view.findViewById(R.id.furniture);
        Button books = view.findViewById(R.id.book);
        Button others = view.findViewById(R.id.other);
        ImageView back = view.findViewById(R.id.back_btn);

        clothes.setOnClickListener(v -> {
            Intent myIntent = new Intent(getActivity().getApplicationContext(), DonateUsedItem.class);
            myIntent.putExtra("type","clothes");
            getActivity().startActivity(myIntent);
        });

        furni.setOnClickListener(v -> {
            Intent myIntent = new Intent(getActivity().getApplicationContext(), DonateUsedItem.class);
            myIntent.putExtra("type","furniture");
            getActivity().startActivity(myIntent);
        });

        books.setOnClickListener(v -> {
            Intent myIntent = new Intent(getActivity().getApplicationContext(), DonateUsedItem.class);
            myIntent.putExtra("type","books");
            getActivity().startActivity(myIntent);
        });

        others.setOnClickListener(v -> {
            Intent myIntent = new Intent(getActivity().getApplicationContext(), DonateUsedItem.class);
            myIntent.putExtra("type","other");
            getActivity().startActivity(myIntent);
        });

        back.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, new DonationCategory());
            ft.commit();
        });

        return  view;
    }
}