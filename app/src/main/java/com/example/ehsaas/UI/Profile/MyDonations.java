package com.example.ehsaas.UI.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Blood.Receiver.BloodDetail;
import com.example.ehsaas.UI.Food.Receiver.AllFoodAndUsedItems;
import com.example.ehsaas.UI.Models.BloodModel;
import com.example.ehsaas.UI.Models.FoodModel;
import com.example.ehsaas.UI.Models.UsedItemsModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MyDonations extends AppCompatActivity {


    RecyclerView recyclerView;
    TextView no_item;
    FirebaseFirestore db;
    ProgressDialog progress;
    public static ArrayList<FoodModel> foodModels=new ArrayList<>();
    public static ArrayList<BloodModel> bloodModels=new ArrayList<>();
    public static ArrayList<UsedItemsModel> usedItemsModels=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donations);

        recyclerView=findViewById(R.id.recyclerview);
        no_item=findViewById(R.id.no_item);
        Button food = (Button) findViewById(R.id.food_btn);
        Button blood = (Button) findViewById(R.id.blood_btn);
        Button item = (Button) findViewById(R.id.item_btn);
        progress = new ProgressDialog(MyDonations.this);

        db = FirebaseFirestore.getInstance();

        food.setOnClickListener(v -> getDataFood());

        blood.setOnClickListener(v -> getDataBlood());

        item.setOnClickListener(v -> getDataItems());
    }



    public void getDataFood()
    {
        foodModels=new ArrayList<>();
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
                            foodModels.add(foodModel);

                        }

                        Intent intent = new Intent(MyDonations.this, AllFoodAndUsedItems.class);
                        startActivity(intent);
                    } else {
                        progress.dismiss();
//                            Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    public void getDataBlood()
    {
        bloodModels=new ArrayList<>();
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
                            String id = document.getId();
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
                            bloodModels.add(bloodModel);

                        }

                        Intent intent = new Intent(MyDonations.this, BloodDetail.class);
                        startActivity(intent);


                    } else {
                        progress.dismiss();
//                            Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    public void getDataItems()
    {
        usedItemsModels=new ArrayList<>();
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
                            String id = document.getId();
                            usedItemsModel.setDonation_id(document.getId());
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
                            usedItemsModels.add(usedItemsModel);

                        }

                        Intent intent = new Intent(MyDonations.this, AllFoodAndUsedItems.class);
                        intent.putExtra("from","item");
                        startActivity(intent);
                    } else {
                        progress.dismiss();
//                            Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }
}