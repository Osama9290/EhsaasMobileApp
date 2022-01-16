package com.example.ehsaas.UI.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.Profile.MyDonations;
import com.example.ehsaas.UI.Profile.RequestForDonations;
import com.example.ehsaas.UI.Requests.GeneralDonationRequests;
import com.example.ehsaas.UI.Requests.MyDonationRequests;
import com.example.ehsaas.UI.Volunteer.CompletedDeliver;
import com.example.ehsaas.UI.Volunteer.InProgressDeliver;
import com.example.ehsaas.UI.notification.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Dashboard extends Fragment {

    RelativeLayout my_donations,my_donation_requests,general_donation
            ,request_donation,progress_deliveries,completed_deliveries;
    UserPreferences userPreferences;
    LinearLayout receiver_layout,donor_layout,volunteer_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);



        my_donations=view.findViewById(R.id.my_donations);
        my_donation_requests=view.findViewById(R.id.my_donation_requests);
        general_donation=view.findViewById(R.id.general_donation);
        donor_layout=view.findViewById(R.id.donor_layout);
        receiver_layout=view.findViewById(R.id.receiver_layout);
        request_donation=view.findViewById(R.id.request_donation);
        progress_deliveries=view.findViewById(R.id.progress_deliveries);
        completed_deliveries=view.findViewById(R.id.completed_deliveries);
        volunteer_layout=view.findViewById(R.id.volunteer_layout);

        userPreferences=new UserPreferences(getActivity());

        if (userPreferences.getUserType().equalsIgnoreCase("donor"))
        {
            receiver_layout.setVisibility(View.GONE);
            donor_layout.setVisibility(View.VISIBLE);
            volunteer_layout.setVisibility(View.GONE);
        }
        else if (userPreferences.getUserType().equalsIgnoreCase("volunteer"))
        {
            receiver_layout.setVisibility(View.GONE);
            donor_layout.setVisibility(View.GONE);
            volunteer_layout.setVisibility(View.VISIBLE);
        }
        else {
            receiver_layout.setVisibility(View.VISIBLE);
            donor_layout.setVisibility(View.GONE);
            volunteer_layout.setVisibility(View.GONE);

        }

        progress_deliveries.setOnClickListener(v -> {

            Intent intent=new Intent(getActivity(), InProgressDeliver.class);
            startActivity(intent);

        });

        completed_deliveries.setOnClickListener(v -> {

            Intent intent=new Intent(getActivity(), CompletedDeliver.class);
            startActivity(intent);

        });

        my_donations.setOnClickListener(v -> {

                   Intent intent=new Intent(getActivity(), MyDonations.class);
                   startActivity(intent);

        });

        my_donation_requests.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MyDonationRequests.class));
        });

        general_donation.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), GeneralDonationRequests.class));
        });

        request_donation.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RequestForDonations.class));
        });

        return  view;
    }
}