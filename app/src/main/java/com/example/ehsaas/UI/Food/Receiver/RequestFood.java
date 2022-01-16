package com.example.ehsaas.UI.Food.Receiver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.congrat;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RequestFood extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{


    private static int RESULT_LOAD_IMAGE = 1000;
    private final static int PERMISSION_CODE = 1001;
    ImageView  back;
    Button  save;
    FirebaseFirestore db;
    UserPreferences userPreferences;
    EditText location, food_quantity, food_name,city,attendant_name,contact_no;
    Spinner pick_drop,province;
    String pickup_st="",provinve_st="";

    String[] pick_type = {"Yes", "No"},provinces = {"Punjab", "Sindh","Balochistan","Khyber Pakhtunkhwa","Azad Kashmir"};
    String location_st, food_quantity_st, food_name_st;
    ProgressDialog progress;

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_food);

        save = (Button) findViewById(R.id.Save);
        back = (ImageView) findViewById(R.id.back_btn);
        food_name = findViewById(R.id.food_name);
        location = findViewById(R.id.location);
        food_quantity = findViewById(R.id.food_quantity);
        province=findViewById(R.id.province);
        city=findViewById(R.id.city);
        attendant_name=findViewById(R.id.attendant_name);
        contact_no=findViewById(R.id.contact_no);
        pick_drop=findViewById(R.id.pick_drop);
        province=findViewById(R.id.province);


        ArrayAdapter<String> province_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces);
        province_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> pickdrop_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, pick_type);
        pickdrop_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        province.setAdapter(province_adapter);
        province.setOnItemSelectedListener(this);
        pick_drop.setAdapter(pickdrop_adapter);
        pick_drop.setOnItemSelectedListener(this);


        userPreferences = new UserPreferences(RequestFood.this);

        progress = new ProgressDialog(this);

        db = FirebaseFirestore.getInstance();


        type = getIntent().getStringExtra("type");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        attendant_name.setText(userPreferences.getFname());
        contact_no.setText(userPreferences.getPhone());

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {


                if (food_name.getText().toString().isEmpty()) {
                    Toast.makeText(RequestFood.this, "Please Enter Food Name", Toast.LENGTH_SHORT).show();

                }
                else if (food_quantity.getText().toString().isEmpty()) {
                    Toast.makeText(RequestFood.this, "Please Enter Food Quantity", Toast.LENGTH_SHORT).show();

                }
//                else if (province.getText().toString().isEmpty()) {
//                    Toast.makeText(RequestFood.this, "Please Enter Province", Toast.LENGTH_SHORT).show();
//
//                }
                else if (city.getText().toString().isEmpty()) {
                    Toast.makeText(RequestFood.this, "Please Enter City", Toast.LENGTH_SHORT).show();

                }
//                else if (location.getText().toString().isEmpty()) {
//                    Toast.makeText(RequestFood.this, "Please Enter Street Address", Toast.LENGTH_SHORT).show();
//                }
                else if (attendant_name.getText().toString().isEmpty()) {
                    Toast.makeText(RequestFood.this, "Please Enter Attendant Name", Toast.LENGTH_SHORT).show();
                }

                else if (contact_no.getText().toString().isEmpty()) {
                    Toast.makeText(RequestFood.this, "Please Enter Contact No.", Toast.LENGTH_SHORT).show();
                }

                else {
                    food_name_st = food_name.getText().toString();
                    location_st = location.getText().toString();
                    food_quantity_st = food_quantity.getText().toString();
                    SaveData();
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void SaveData() {
        progress.setMessage("Saving Data");
        progress.setCancelable(false);
        progress.show();

        String uid = userPreferences.getUserId();

        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss zzz", Locale.US);

        String time = df.format(new Date());


        Map<String, Object> blood_obj = new HashMap<>();
        blood_obj.put("date", time);
        DocumentReference documentReference = db.document("/receiver/"+uid);
        blood_obj.put("receiverId",  documentReference);
        blood_obj.put("category", type);
        blood_obj.put("city", city.getText().toString());
        blood_obj.put("foodName", food_name_st);
        blood_obj.put("pickDrop", pickup_st);
        blood_obj.put("province", provinve_st);
        blood_obj.put("quantity", food_quantity_st);
        blood_obj.put("status", "active");
        blood_obj.put("attendantContact", contact_no.getText().toString());
        blood_obj.put("attendantName", attendant_name.getText().toString());

        db.collection("foodRequest").document()
                .set(blood_obj)
                .addOnSuccessListener(aVoid -> {
                    progress.dismiss();
                    Toast.makeText(this, "Successfully Requested for Food :)", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progress.dismiss();
                    Toast.makeText(RequestFood.this, e.toString(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.province:
                provinve_st=provinces[position];
                break;

            case R.id.pick_drop:
                pickup_st =pick_type[position];
                break;

            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}