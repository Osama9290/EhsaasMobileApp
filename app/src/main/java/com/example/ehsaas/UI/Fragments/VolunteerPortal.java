package com.example.ehsaas.UI.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.Volunteer.SelectToDeliver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class VolunteerPortal extends Fragment {


    View view;
    UserPreferences userPreferences;
    TextView delivery_count;


    public VolunteerPortal() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_volunteer_portal, container, false);
        delivery_count=view.findViewById(R.id.delivery_count);

        userPreferences=new UserPreferences(getActivity());

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("volunteer").document(userPreferences.getUserId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Map<String, Object> data = document.getData();
                        String deliveryCount = String.valueOf(data.get("deliveryCount"));
                        delivery_count.setText(deliveryCount);
                        userPreferences.setLname(deliveryCount);
                    }
                    else {

                    }
                } else {


                }
            }
        });



        view.findViewById(R.id.deliver_btn).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), SelectToDeliver.class)));

        return view;
    }
}