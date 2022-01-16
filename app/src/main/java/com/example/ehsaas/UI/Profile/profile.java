package com.example.ehsaas.UI.Profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ehsaas.R;
import com.example.ehsaas.UI.Chat.InboxActivity;
import com.example.ehsaas.UI.Food.Donor.DonateFood;
import com.example.ehsaas.UI.Fragments.contact_us;
import com.example.ehsaas.UI.Preferences.UserPreferences;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends Fragment {


    ImageView edit_profile;
    TextView user_email,logout_text,donor_text;
    UserPreferences userPreferences;
    CircleImageView prfile_picture;
    RelativeLayout about_layout,logout_layout,switch_layout,chat_layout;
    String[] acc_cat = {"Account", "My Donation", "Edit Profile"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        edit_profile=view.findViewById(R.id.edit_profile);
        user_email=view.findViewById(R.id.user_email);
        logout_text=view.findViewById(R.id.logout_text);
        userPreferences=new UserPreferences(getActivity());
        switch_layout=view.findViewById(R.id.switch_layout);
        about_layout=view.findViewById(R.id.about_layout);
        logout_layout=view.findViewById(R.id.logout_layout);
        donor_text=view.findViewById(R.id.donor_text);
        chat_layout=view.findViewById(R.id.chat_layout);
        prfile_picture=view.findViewById(R.id.prfile_picture);

        Glide.with(getActivity())
                .load(userPreferences.getDp())
                .placeholder(R.drawable.user)
                .into(prfile_picture);



        if (userPreferences.getUseremail() != null){
            if (!userPreferences.getUseremail().equals(""))
            {
                user_email.setText(userPreferences.getUseremail());
            }
            else {
                user_email.setText("No User Added");
            }

        }
        else {
            user_email.setText("No User Added");
        }


        if (userPreferences.getUserType().equalsIgnoreCase("donor"))
        {
            donor_text.setText("Switch to Receiver");
        }
        else if (userPreferences.getUserType().equalsIgnoreCase("volunteer"))
        {
            donor_text.setText("Switch to Donor or Receiver");
        }
        else {
            donor_text.setText("Switch to Donor");
        }

        about_layout.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment=new about_us();
            ft.addToBackStack(null);
            ft.replace(R.id.fragment_profile, fragment);
            ft.commit();
        });

        logout_layout.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Successfully Logout", Toast.LENGTH_SHORT).show();
            userPreferences.logoutUser();
        });


        chat_layout.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), InboxActivity.class));
        });

        switch_layout.setOnClickListener(v -> {
            userPreferences.logoutUser();
        });



        edit_profile.setOnClickListener(v -> {
            if (userPreferences.getUseremail() != null) {
                if (!userPreferences.getUseremail().equals("")) {

                    FragmentManager fs = getParentFragmentManager();
                    FragmentTransaction fa = fs.beginTransaction();
                    fa.replace(R.id.fragment_profile, new edit_profile());
                    fa.commit();
                }
                else {
                    Toast.makeText(getActivity(), "Signup or Login to edit profile", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getActivity(), "Signup or Login to edit profile", Toast.LENGTH_SHORT).show();

            }

        });


        return view;
    }
}
