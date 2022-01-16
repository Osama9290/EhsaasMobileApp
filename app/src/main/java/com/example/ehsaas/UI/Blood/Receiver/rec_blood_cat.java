package com.example.ehsaas.UI.Blood.Receiver;

import android.app.ProgressDialog;
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
import com.example.ehsaas.UI.Fragments.ReceiverCategory;
import com.example.ehsaas.UI.Models.BloodModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;


public class rec_blood_cat extends Fragment {


    FirebaseFirestore db;
    ProgressDialog progress;
    public static ArrayList<BloodModel> bloodModels=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rec_blood_cat, container, false);

        Button o_pos = (Button) view.findViewById(R.id.O_pos);
        Button o_neg = (Button) view.findViewById(R.id.O_neg);
        Button a_pos = (Button) view.findViewById(R.id.A_pos);
        Button a_neg = (Button) view.findViewById(R.id.A_neg);
        Button b_pos = (Button) view.findViewById(R.id.B_pos);
        Button b_neg = (Button) view.findViewById(R.id.B_neg);
        Button ab_pos = (Button) view.findViewById(R.id.AB_pos);
        Button ab_neg = (Button) view.findViewById(R.id.AB_neg);
        ImageView back = (ImageView) view.findViewById(R.id.back_btn);

        bloodModels=new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        progress = new ProgressDialog(getActivity());
        getData();


        o_pos.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BloodDetail.class);
            intent.putExtra("type","O+");
            startActivity(intent);
        });

        o_neg.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BloodDetail.class);
            intent.putExtra("type","O-");
            startActivity(intent);
        });

        a_pos.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BloodDetail.class);
            intent.putExtra("type","A+");
            startActivity(intent);
        });

        a_neg.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BloodDetail.class);
            intent.putExtra("type","A-");
            startActivity(intent);
        });

        b_pos.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), BloodDetail.class);
            intent.putExtra("type","B+");
            startActivity(intent);
        });

        b_neg.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BloodDetail.class);
            intent.putExtra("type","B-");
            startActivity(intent);
        });

        ab_pos.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BloodDetail.class);
            intent.putExtra("type","AB+");
            startActivity(intent);
        });

        ab_neg.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BloodDetail.class);
            intent.putExtra("type","AB-");
            startActivity(intent);
        });

        back.setOnClickListener(v -> {
           getActivity().onBackPressed();
        });


        return view;
    }

    public void getData()
    {
        progress.setMessage("Getting Data");
        progress.setCancelable(false);
        progress.show();
        db.collection("blood")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progress.dismiss();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            BloodModel bloodModel=new BloodModel();
                            bloodModel.setDonation_id(document.getId());
                            bloodModel.setAge(String.valueOf(data.get("age")));
                            bloodModel.setBloodGroup(String.valueOf(data.get("bloodGroup")));
                            bloodModel.setBloodTransfusion(String.valueOf(data.get("bloodTransfusion")));
                            bloodModel.setCity(String.valueOf(data.get("city")));
                            bloodModel.setCurrentMedication(String.valueOf(data.get("currentMedication")));
                            bloodModel.setDate(String.valueOf(data.get("date")));
                            DocumentReference documentReference= (DocumentReference) data.get("donorId");
                            bloodModel.setDonorId(String.valueOf(documentReference.getPath()));
                            bloodModel.setExpiry(String.valueOf(data.get("expiry")));
                            bloodModel.setIllness(String.valueOf(data.get("illness")));
                            bloodModel.setImg(String.valueOf(data.get("img")));
                            bloodModel.setLastDonated(String.valueOf(data.get("lastDonated")));
                            bloodModel.setProvince(String.valueOf(data.get("province")));
                            bloodModel.setSmoking(String.valueOf(data.get("smoking")));
                            bloodModel.setStatus(String.valueOf(data.get("status")));
                            bloodModel.setVaccination(String.valueOf(data.get("vaccination")));

                            if (bloodModel.getStatus().equalsIgnoreCase("active"))
                            bloodModels.add(bloodModel);

                        }
                    } else {
                        progress.dismiss();
//                            Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

}