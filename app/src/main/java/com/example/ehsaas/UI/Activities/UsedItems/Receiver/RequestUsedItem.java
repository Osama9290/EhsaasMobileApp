package com.example.ehsaas.UI.Activities.UsedItems.Receiver;

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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.MapsActivity;
import com.example.ehsaas.UI.Activities.congrat;
import com.example.ehsaas.UI.Food.Receiver.RequestFood;
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

public class RequestUsedItem extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{
    private final static int RESULT_LOAD_IMAGE = 1000;
    private final static int PERMISSION_CODE = 1001;
    ImageView back;
    Button  save;
    ProgressDialog progress;


    FirebaseFirestore db;
    UserPreferences userPreferences;
    EditText food_quantity,food_name,city,
            furniture_description,cloth_size,cloth_brand,book_author,book_edition,other_category,attendant_name,contact_no;
    public static EditText location;
    TextView type_text;
    Spinner pick_drop,province;

    String[] pick_type = {"Yes", "No"},provinces = {"Punjab", "Sindh","Balochistan","Khyber Pakhtunkhwa","Azad Kashmir"};

    String location_st,food_quantity_st,food_name_st,type,province_st="", pickup_st="";

    LinearLayout furniture_layout,cloth_layout,book_layout,other_layout;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_item);

        back = findViewById(R.id.back_btn);
        save = findViewById(R.id.Save);
        progress = new ProgressDialog(this);
        other_layout=findViewById(R.id.other_layout);
        furniture_layout=findViewById(R.id.furniture_layout);
        cloth_layout=findViewById(R.id.cloth_layout);
        book_layout=findViewById(R.id.book_layout);
        attendant_name=findViewById(R.id.attendant_name);
        contact_no=findViewById(R.id.contact_no);

        other_category=findViewById(R.id.other_category);
        furniture_description=findViewById(R.id.furniture_description);
        cloth_size=findViewById(R.id.cloth_size);
        cloth_brand=findViewById(R.id.cloth_brand);
        book_author=findViewById(R.id.book_author);
        book_edition=findViewById(R.id.book_edition);
        pick_drop=findViewById(R.id.pick_drop);


        food_name=findViewById(R.id.food_name);
        location=findViewById(R.id.location);
        food_quantity=findViewById(R.id.food_quantity);

        type_text=findViewById(R.id.type_text);
        userPreferences=new UserPreferences(RequestUsedItem.this);

        province=findViewById(R.id.province);
        city=findViewById(R.id.city);
        ArrayAdapter<String> province_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces);
        province_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> pickdrop_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, pick_type);
        pickdrop_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        province.setAdapter(province_adapter);
        province.setOnItemSelectedListener(this);
        pick_drop.setAdapter(pickdrop_adapter);
        pick_drop.setOnItemSelectedListener(this);

        db = FirebaseFirestore.getInstance();


        type = getIntent().getStringExtra("type");

        if (type.equalsIgnoreCase("clothes"))
        {
            other_layout.setVisibility(View.GONE);
            cloth_layout.setVisibility(View.VISIBLE);
            furniture_layout.setVisibility(View.GONE);
            book_layout.setVisibility(View.GONE);
        }
        else if (type.equalsIgnoreCase("furniture"))
        {
            other_layout.setVisibility(View.GONE);
            cloth_layout.setVisibility(View.GONE);
            furniture_layout.setVisibility(View.VISIBLE);
            book_layout.setVisibility(View.GONE);
        }
        else if (type.equalsIgnoreCase("books"))
        {
            other_layout.setVisibility(View.GONE);
            cloth_layout.setVisibility(View.GONE);
            furniture_layout.setVisibility(View.GONE);
            book_layout.setVisibility(View.VISIBLE);
        }
        else if (type.equalsIgnoreCase("other"))
        {
            other_layout.setVisibility(View.VISIBLE);
            cloth_layout.setVisibility(View.GONE);
            furniture_layout.setVisibility(View.GONE);
            book_layout.setVisibility(View.GONE);
        }

        type_text.setText("Request for "+type);

        attendant_name.setText(userPreferences.getFname());
        contact_no.setText(userPreferences.getPhone());

        save.setOnClickListener(v -> {

            if (food_name.getText().toString().isEmpty()) {
                Toast.makeText(RequestUsedItem.this, "Please Enter Food Name", Toast.LENGTH_SHORT).show();

            }
            else if (food_quantity.getText().toString().isEmpty()) {
                Toast.makeText(RequestUsedItem.this, "Please Enter Food Quantity", Toast.LENGTH_SHORT).show();

            }

            else if (city.getText().toString().isEmpty()) {
                Toast.makeText(RequestUsedItem.this, "Please Enter City", Toast.LENGTH_SHORT).show();

            }
//            else if (location.getText().toString().isEmpty()) {
//                Toast.makeText(RequestUsedItem.this, "Please Enter Street Address", Toast.LENGTH_SHORT).show();
//            }
            else if (attendant_name.getText().toString().isEmpty()) {
                Toast.makeText(RequestUsedItem.this, "Please Enter Attendant Name", Toast.LENGTH_SHORT).show();
            }
            else if (contact_no.getText().toString().isEmpty()) {
                Toast.makeText(RequestUsedItem.this, "Please Enter Contact No.", Toast.LENGTH_SHORT).show();
            }

            else {
                if (type.equalsIgnoreCase("clothes"))
                {
                    if (cloth_size.getText().toString().isEmpty())
                    {
                        Toast.makeText(RequestUsedItem.this, "Please Enter Cloth Size", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        food_name_st = food_name.getText().toString();
                        location_st = location.getText().toString();
                        food_quantity_st = food_quantity.getText().toString();
                        SaveData();
                    }
                }
                else if (type.equalsIgnoreCase("furniture"))
                {
//                    if (furniture_description.getText().toString().isEmpty())
//                    {
//                        Toast.makeText(DonateUsedItem.this, "Please Enter Description", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
                        food_name_st = food_name.getText().toString();
                        location_st = location.getText().toString();
                        food_quantity_st = food_quantity.getText().toString();
                        SaveData();
//                    }
                }
                else if (type.equalsIgnoreCase("books"))
                {
                    if (book_author.getText().toString().isEmpty())
                    {
                        Toast.makeText(RequestUsedItem.this, "Please Enter Author of Book", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        food_name_st = food_name.getText().toString();
                        location_st = location.getText().toString();
                        food_quantity_st = food_quantity.getText().toString();
                        SaveData();
                    }
                }
                else if (type.equalsIgnoreCase("other"))
                {
                    if (other_category.getText().toString().isEmpty())
                    {
                        Toast.makeText(RequestUsedItem.this, "Please Enter Category", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        food_name_st = food_name.getText().toString();
                        location_st = location.getText().toString();
                        food_quantity_st = food_quantity.getText().toString();
                        SaveData();
                    }
                }

            }
        });

        back.setOnClickListener(v -> {
            onBackPressed();
        });

        findViewById(R.id.location_pick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RequestUsedItem.this, MapsActivity.class);
                intent.putExtra("type","usedItem");
                startActivity(intent);
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
        blood_obj.put("name", food_name_st);
        blood_obj.put("province", province_st);
        blood_obj.put("quantity", food_quantity_st);
        blood_obj.put("status", "active");
        blood_obj.put("attendantContact", contact_no.getText().toString());
        blood_obj.put("attendantName", attendant_name.getText().toString());
        blood_obj.put("pickDrop", pickup_st);


        if (type.equalsIgnoreCase("clothes"))
        {
            blood_obj.put("size", cloth_size.getText().toString());
            blood_obj.put("brand", cloth_brand.getText().toString());
        }
        else if (type.equalsIgnoreCase("furniture"))
        {
            blood_obj.put("description", furniture_description.getText().toString());
        }
        else if (type.equalsIgnoreCase("books"))
        {
            blood_obj.put("author", book_author.getText().toString());
            blood_obj.put("edition", book_edition.getText().toString());
        }
        else if (type.equalsIgnoreCase("other"))
        {
            blood_obj.put("category", other_category.getText().toString());
        }


        db.collection("usedItemRequest").document()
                .set(blood_obj)
                .addOnSuccessListener(aVoid -> {
                    progress.dismiss();
                    Toast.makeText(this, "Successfully Requested for Item :)", Toast.LENGTH_SHORT).show();

                    finish();
                })
                .addOnFailureListener(e -> {
                    progress.dismiss();
                    Toast.makeText(RequestUsedItem.this, e.toString(), Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.province:
                province_st=provinces[position];
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