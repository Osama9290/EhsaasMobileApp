package com.example.ehsaas.UI.Activities.UsedItems.Receiver;

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
import com.example.ehsaas.UI.Food.Receiver.AllFoodAndUsedItems;
import com.example.ehsaas.UI.Models.UsedItemsModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;


public class UsedItemShowCategory extends Fragment {

    FirebaseFirestore db;
    Button furniture,cloth,book,other;
    ProgressDialog progress;
    public static ArrayList<UsedItemsModel> usedItemsModels=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_rec_item_cat, container, false);

        other=view.findViewById(R.id.other);
        furniture=view.findViewById(R.id.furniture);
        cloth=view.findViewById(R.id.cloth);
        book=view.findViewById(R.id.book);
        progress = new ProgressDialog(getActivity());


        ImageView back = view.findViewById(R.id.back_btn);


        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AllFoodAndUsedItems.class);
                intent.putExtra("type","other");
                intent.putExtra("from","item");
                startActivity(intent);
            }
        });

        furniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AllFoodAndUsedItems.class);
                intent.putExtra("type","furniture");
                intent.putExtra("from","item");
                startActivity(intent);
            }
        });

        cloth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AllFoodAndUsedItems.class);
                intent.putExtra("type","clothes");
                intent.putExtra("from","item");
                startActivity(intent);
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AllFoodAndUsedItems.class);
                intent.putExtra("type","books");
                intent.putExtra("from","item");
                startActivity(intent);
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        usedItemsModels =new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        getData();

        return  view;
    }

    public void getData()
    {
        progress.setMessage("Getting Data");
        progress.setCancelable(false);
        progress.show();
        db.collection("usedItems")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progress.dismiss();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            UsedItemsModel usedItemsModel=new UsedItemsModel();

                            usedItemsModel.setDonation_id(document.getId());
                            usedItemsModel.setAuthor(String.valueOf(data.get("author")));
                            usedItemsModel.setCity(String.valueOf(data.get("city")));
                            usedItemsModel.setDate(String.valueOf(data.get("date")));
                            DocumentReference documentReference= (DocumentReference) data.get("donorId");
                            usedItemsModel.setDonorId(String.valueOf(documentReference.getPath()));
                            usedItemsModel.setExpiry(String.valueOf(data.get("expiry")));
                            usedItemsModel.setImg(String.valueOf(data.get("img")));
                            usedItemsModel.setProvince(String.valueOf(data.get("province")));
                            usedItemsModel.setStatus(String.valueOf(data.get("status")));

                            usedItemsModel.setCategory(String.valueOf(data.get("category")));
                            usedItemsModel.setName(String.valueOf(data.get("name")));
                            usedItemsModel.setQuantity(String.valueOf(data.get("quantity")));
                            usedItemsModel.setRating(String.valueOf(data.get("rating")));
                            usedItemsModel.setStreetAddress(String.valueOf(data.get("streetAddress")));


                            usedItemsModel.setDescription(String.valueOf(data.get("description")));
                            usedItemsModel.setSize(String.valueOf(data.get("size")));
                            usedItemsModel.setBrand(String.valueOf(data.get("brand")));
                            usedItemsModel.setAuthor(String.valueOf(data.get("author")));
                            usedItemsModel.setEdition(String.valueOf(data.get("edition")));

                            if (usedItemsModel.getStatus().equalsIgnoreCase("active"))
                            usedItemsModels.add(usedItemsModel);

                        }
                    } else {
                        progress.dismiss();
//                            Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }
}