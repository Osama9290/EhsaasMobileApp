package com.example.ehsaas.UI.Fragments;

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
import com.example.ehsaas.UI.Activities.UsedItems.Receiver.UsedItemShowCategory;
import com.example.ehsaas.UI.Blood.Receiver.rec_blood_cat;
import com.example.ehsaas.UI.Food.Receiver.FoodSelectCategory;


public class ReceiverCategory extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_receive_cat, container, false);
        ImageView back = view.findViewById(R.id.back_btn);
        Button food = view.findViewById(R.id.food_btn);
        Button blood = view.findViewById(R.id.blood_btn);
        Button item = view.findViewById(R.id.item_btn);

        food.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, new FoodSelectCategory());
            ft.addToBackStack(null);
            ft.commit();
        });

        blood.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, new rec_blood_cat());
            ft.addToBackStack(null);
            ft.commit();
        });

        item.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, new UsedItemShowCategory());
            ft.addToBackStack(null);
            ft.commit();
        });

        back.setOnClickListener(v -> getActivity().onBackPressed());

        return view;
    }
}