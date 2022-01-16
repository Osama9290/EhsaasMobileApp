package com.example.ehsaas.UI.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Blood.Donor.DonateBlood;
import com.example.ehsaas.UI.Activities.UsedItems.Donor.UsedItemCategory;
import com.example.ehsaas.UI.Food.Donor.food_cat;


public class DonationCategory extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donation_cat, container, false);

        Button food = (Button) view.findViewById(R.id.food_btn);
        Button item = (Button) view.findViewById(R.id.item_btn);
        Button blood = (Button) view.findViewById(R.id.blood_btn);

        food.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, new food_cat());
            ft.addToBackStack(null);
            ft.commit();
        });
        item.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, new UsedItemCategory());
            ft.addToBackStack(null);
            ft.commit();
        });
        blood.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DonateBlood.class);
            startActivity(intent);
        });

        return view;
    }
}