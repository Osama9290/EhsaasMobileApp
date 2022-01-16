package com.example.ehsaas.UI.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.UsedItems.Receiver.SelectUsedItemReceiver;
import com.example.ehsaas.UI.Blood.Receiver.BloodDetail;
import com.example.ehsaas.UI.Blood.Receiver.RequestBlood;
import com.example.ehsaas.UI.Food.Receiver.AllFoodAndUsedItems;
import com.example.ehsaas.UI.Food.Receiver.RequestFood;
import com.example.ehsaas.UI.Food.Receiver.SelectFoodReceiver;
import com.example.ehsaas.UI.Models.BloodModel;
import com.example.ehsaas.UI.Models.FoodModel;
import com.example.ehsaas.UI.Models.UsedItemsModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class RequestForDonations extends AppCompatActivity {


    RecyclerView recyclerView;
    TextView no_item,donation_text;
    FirebaseFirestore db;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donations);

        recyclerView=findViewById(R.id.recyclerview);
        no_item=findViewById(R.id.no_item);
        Button food = (Button) findViewById(R.id.food_btn);
        Button blood = (Button) findViewById(R.id.blood_btn);
        Button item = (Button) findViewById(R.id.item_btn);
        donation_text=findViewById(R.id.donation_text);
        progress = new ProgressDialog(RequestForDonations.this);

        db = FirebaseFirestore.getInstance();

        donation_text.setText("Request For Donation");

        food.setOnClickListener(v -> requestFood());

        blood.setOnClickListener(v -> requestBlood());

        item.setOnClickListener(v -> requestItems());
    }



    public void requestFood()
    {
        startActivity(new Intent(RequestForDonations.this, SelectFoodReceiver.class));
    }

    public void requestBlood()
    {
        startActivity(new Intent(RequestForDonations.this, RequestBlood.class));
    }

    public void requestItems()
    {
        startActivity(new Intent(RequestForDonations.this, SelectUsedItemReceiver.class));
    }
}