package com.example.ehsaas.UI.Food.Receiver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Models.FoodModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;


public class FoodSelectCategory extends Fragment {

    FirebaseFirestore db;
    Button rec_savory,sweet;
    ProgressDialog progress;
    public static ArrayList<FoodModel> foodModels=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rec_food_cat, container, false);

        sweet=view.findViewById(R.id.sweet);
        rec_savory=view.findViewById(R.id.rec_savory);
        progress = new ProgressDialog(getActivity());
        ImageView back = (ImageView) view.findViewById(R.id.back_btn);

        back.setOnClickListener(v -> getActivity().onBackPressed());

        rec_savory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AllFoodAndUsedItems.class);
            intent.putExtra("type","cooked");
            startActivity(intent);
        });

        sweet.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AllFoodAndUsedItems.class);
            intent.putExtra("type","uncooked");
            startActivity(intent);
        });

        foodModels =new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        getData();

        return  view;
    }

    public void getData()
    {
        progress.setMessage("Getting Data");
        progress.setCancelable(false);
        progress.show();
        db.collection("food")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progress.dismiss();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            FoodModel foodModel=new FoodModel();
                            String id = document.getId();
                            foodModel.setDonation_id(document.getId());
                            foodModel.setCity(String.valueOf(data.get("city")));
                            foodModel.setDate(String.valueOf(data.get("date")));
                            DocumentReference documentReference= (DocumentReference) data.get("donorId");
                            foodModel.setDonorId(String.valueOf(documentReference.getPath()));
                            foodModel.setExpiry(String.valueOf(data.get("expiry")));
                            foodModel.setImg(String.valueOf(data.get("img")));
                            foodModel.setProvince(String.valueOf(data.get("province")));
                            foodModel.setStatus(String.valueOf(data.get("status")));

                            foodModel.setCategory(String.valueOf(data.get("category")));
                            foodModel.setName(String.valueOf(data.get("name")));
                            foodModel.setQuantity(String.valueOf(data.get("quantity")));
                            foodModel.setStreetAddress(String.valueOf(data.get("streetAddress")));

                            if (foodModel.getStatus().equalsIgnoreCase("active"))
                            foodModels.add(foodModel);

                        }
                    } else {
                        progress.dismiss();
//                            Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }
}