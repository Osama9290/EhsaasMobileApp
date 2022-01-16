package com.example.ehsaas.UI.Activities.UsedItems.Receiver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Food.Receiver.AllFoodAndUsedItems;
import com.example.ehsaas.UI.Food.Receiver.RequestFood;

public class SelectUsedItemReceiver extends AppCompatActivity {


    Button furniture,cloth,book,other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_rec_item_cat);

        other=findViewById(R.id.other);
        furniture=findViewById(R.id.furniture);
        cloth=findViewById(R.id.cloth);
        book=findViewById(R.id.book);
        ImageView back = (ImageView) findViewById(R.id.back_btn);

        other.setOnClickListener(v -> {
            Intent intent = new Intent(SelectUsedItemReceiver.this, RequestUsedItem.class);
            intent.putExtra("type","other");
            intent.putExtra("from","item");
            startActivity(intent);
        });

        furniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectUsedItemReceiver.this, RequestUsedItem.class);
                intent.putExtra("type","furniture");
                intent.putExtra("from","item");
                startActivity(intent);
            }
        });

        cloth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectUsedItemReceiver.this, RequestUsedItem.class);
                intent.putExtra("type","clothes");
                intent.putExtra("from","item");
                startActivity(intent);
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectUsedItemReceiver.this, RequestUsedItem.class);
                intent.putExtra("type","books");
                intent.putExtra("from","item");
                startActivity(intent);
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}