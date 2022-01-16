package com.example.ehsaas.UI.Food.Donor;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Food.Donor.DonateFood;


public class food_cat extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_cat, container, false);
        ImageView back = (ImageView) view.findViewById(R.id.back_btn);
        Button sav = (Button) view.findViewById(R.id.savory);
        Button swe = (Button) view.findViewById(R.id.sweet);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        sav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity().getApplicationContext(), DonateFood.class);
                myIntent.putExtra("type","cooked");
                getActivity().startActivity(myIntent);
            }
        });

        swe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity().getApplicationContext(), DonateFood.class);
                myIntent.putExtra("type","uncooked");
                getActivity().startActivity(myIntent);
            }
        });


        return  view;
    }
}