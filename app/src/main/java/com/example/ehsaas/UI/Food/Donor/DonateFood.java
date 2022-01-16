package com.example.ehsaas.UI.Food.Donor;

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
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.MapsActivity;
import com.example.ehsaas.UI.Activities.congrat;
import com.example.ehsaas.UI.Food.Receiver.FoodDetail;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DonateFood extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{


    private static int RESULT_LOAD_IMAGE = 1000;
    private final static int PERMISSION_CODE = 1001;
    ImageView imageView, back;
    Button button, save;
    FirebaseFirestore db;
    String strFilePath = "";
    UserPreferences userPreferences;
    EditText food_quantity, food_name,city;
    public static EditText location;

    Spinner province;
    String location_st, food_quantity_st, food_name_st,province_st;
    ProgressDialog progress;
    RatingBar rating;
    String donorId;

    String[] provinces = {"Punjab", "Sindh","Balochistan","Khyber Pakhtunkhwa","Azad Kashmir"};


    String type,name,location_new,Quantity,city_new,province_new,img_new,edit;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_food);
        button = (Button) findViewById(R.id.upload);
        save = (Button) findViewById(R.id.Save);
        back = (ImageView) findViewById(R.id.back_btn);
        imageView = (ImageView) findViewById(R.id.imgView);
        food_name = findViewById(R.id.food_name);
        location = findViewById(R.id.location);
        food_quantity = findViewById(R.id.food_quantity);
        province=findViewById(R.id.province);
        city=findViewById(R.id.city);
        rating=findViewById(R.id.rating);



        userPreferences = new UserPreferences(DonateFood.this);

        progress = new ProgressDialog(this);

        db = FirebaseFirestore.getInstance();

        ArrayAdapter<String> province_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces);
        province_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        province.setAdapter(province_adapter);
        province.setOnItemSelectedListener(this);

        type = getIntent().getStringExtra("type");

        edit = getIntent().getStringExtra("edit");

        if (edit != null)
        {

            name = getIntent().getStringExtra("name");
            location_new = getIntent().getStringExtra("location");
            Quantity = getIntent().getStringExtra("Quantity");
            city_new = getIntent().getStringExtra("city");

            donorId = getIntent().getStringExtra("id");
            province_new = getIntent().getStringExtra("province");

            img_new = getIntent().getStringExtra("img");

            food_name.setText(name);
            location.setText(location_new);
            food_quantity.setText(Quantity);
            city.setText(city_new);
            save.setText("Edit");

            int spinnerPosition3 = province_adapter.getPosition(province_new);
            province.setSelection(spinnerPosition3);

            Glide.with(DonateFood.this)
                    .load(img_new)
                    .into(imageView);

        }


        button.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    pickimage();
                }
            } else {
                pickimage();
            }
        });

        back.setOnClickListener(v -> finish());

        save.setOnClickListener(v -> {

            if (food_name.getText().toString().isEmpty()) {
                Toast.makeText(DonateFood.this, "Please Enter Food Name", Toast.LENGTH_SHORT).show();

            }
            else if (food_quantity.getText().toString().isEmpty()) {
                Toast.makeText(DonateFood.this, "Please Enter Food Quantity", Toast.LENGTH_SHORT).show();

            }

            else if (city.getText().toString().isEmpty()) {
                Toast.makeText(DonateFood.this, "Please Enter City", Toast.LENGTH_SHORT).show();

            }
            else if (location.getText().toString().isEmpty()) {
                Toast.makeText(DonateFood.this, "Please Enter Street Address", Toast.LENGTH_SHORT).show();
            }

            else if (strFilePath.equals("")) {
                if (edit != null)
                {
                    food_name_st = food_name.getText().toString();
                    location_st = location.getText().toString();
                    food_quantity_st = food_quantity.getText().toString();
                    SaveDataNew();

                }
                else {
                    Toast.makeText(DonateFood.this, "Please Upload Image", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                food_name_st = food_name.getText().toString();
                location_st = location.getText().toString();
                food_quantity_st = food_quantity.getText().toString();
                if (edit != null)
                {
                    SaveDataNew();
                }
                else {
                    SaveData();
                }

            }

        });


        findViewById(R.id.location_pick).setOnClickListener(v -> {
            Intent intent=new Intent(DonateFood.this, MapsActivity.class);
            intent.putExtra("type","food");
            startActivity(intent);
        });
    }

    private void pickimage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    pickimage();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMAGE) {
            imageView.setImageURI(data.getData());

            Uri data1 = data.getData();
            uploadFileAndSaveToFireBase(data1);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void SaveData() {
        progress.setMessage("Saving Data");
        progress.setCancelable(false);
        progress.show();


        String uid = userPreferences.getUserId();

        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss zzz", Locale.US);
        String time = df.format(new Date());


        Calendar c = Calendar.getInstance();
        try {
            c.setTime(df.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 3);
        String expiry = df.format(c.getTime());

        Map<String, Object> blood_obj = new HashMap<>();
        blood_obj.put("date", time);
        DocumentReference documentReference = db.document("/donor/"+uid);
        blood_obj.put("donorId", documentReference);
        blood_obj.put("expiry", expiry);
        blood_obj.put("category", type);
        blood_obj.put("city", city.getText().toString());
        blood_obj.put("img", strFilePath);
        blood_obj.put("name", food_name_st);
        blood_obj.put("province", province_st);
        blood_obj.put("quantity", food_quantity_st);
        blood_obj.put("status", "active");
        blood_obj.put("streetAddress", location_st);

        db.collection("food").document()
                .set(blood_obj)
                .addOnSuccessListener(aVoid -> {
                    progress.dismiss();
                    Intent intent = new Intent(DonateFood.this, congrat.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progress.dismiss();
                    Toast.makeText(DonateFood.this, e.toString(), Toast.LENGTH_SHORT).show();
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void SaveDataNew() {
        progress.setMessage("Saving Data");
        progress.setCancelable(false);
        progress.show();

        Map<String, Object> blood_obj = new HashMap<>();
        blood_obj.put("city", city.getText().toString());
        if (strFilePath.equalsIgnoreCase(""))
        {
            blood_obj.put("img", img_new);
        }
        else {
            blood_obj.put("img", strFilePath);
        }

        blood_obj.put("name", food_name_st);
        blood_obj.put("province", province_st);
        blood_obj.put("quantity", food_quantity_st);
        blood_obj.put("streetAddress", location_st);

        db.collection("food").document(donorId)
                .update(blood_obj)
                .addOnSuccessListener(aVoid -> {
                    progress.dismiss();
                    Toast.makeText(this, "Successfully Updated Donation :)", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progress.dismiss();
                    Toast.makeText(DonateFood.this, e.toString(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadFileAndSaveToFireBase(Uri pathUri) {

        progress.setMessage("Uploading Image");
        progress.setCancelable(false);
        progress.show();

        StorageReference photoRef = FirebaseStorage.getInstance().getReference().child("images").child(pathUri.getLastPathSegment());
        photoRef.putFile(pathUri).addOnSuccessListener(DonateFood.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {

            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                progress.dismiss();
                Toast.makeText(DonateFood.this, "Upload is done...", Toast.LENGTH_SHORT).show();
                String s = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(uri -> strFilePath = uri.toString());

            }
        }).addOnFailureListener(e -> {
            progress.dismiss();

        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        province_st=provinces[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}