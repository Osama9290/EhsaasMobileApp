package com.example.ehsaas.UI.Food.Receiver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ehsaas.R;

public class SelectFoodReceiver extends AppCompatActivity {


    Button rec_savory,sweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_rec_food_cat);


        sweet=findViewById(R.id.sweet);
        rec_savory=findViewById(R.id.rec_savory);
        ImageView back = (ImageView) findViewById(R.id.back_btn);

        back.setOnClickListener(v -> onBackPressed());

        rec_savory.setOnClickListener(v -> {
            Intent intent = new Intent(SelectFoodReceiver.this, RequestFood.class);
            intent.putExtra("type","cooked");
            startActivity(intent);
        });

        sweet.setOnClickListener(v -> {
            Intent intent = new Intent(SelectFoodReceiver.this, RequestFood.class);
            intent.putExtra("type","uncooked");
            startActivity(intent);
        });

    }
}