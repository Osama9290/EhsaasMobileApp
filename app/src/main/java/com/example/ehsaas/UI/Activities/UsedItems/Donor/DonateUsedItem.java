package com.example.ehsaas.UI.Activities.UsedItems.Donor;

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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.MapsActivity;
import com.example.ehsaas.UI.Activities.congrat;
import com.example.ehsaas.UI.Food.Donor.DonateFood;
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

public class DonateUsedItem extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{
    private final static int RESULT_LOAD_IMAGE = 1000;
    private final static int PERMISSION_CODE = 1001;
    ImageView imageView, back;
    Button button, save;
    String strFilePath="";
    ProgressDialog progress;


    FirebaseFirestore db;
    UserPreferences userPreferences;
    EditText food_quantity,food_name,city,
            furniture_description,cloth_size,cloth_brand,book_author,book_edition,other_category;
    public static EditText location;
    TextView type_text;
    Spinner province;
    RatingBar rating;

    String[] provinces = {"Punjab", "Sindh","Balochistan","Khyber Pakhtunkhwa","Azad Kashmir"};

    String location_st,food_quantity_st,food_name_st,type,province_st="";

    LinearLayout furniture_layout,cloth_layout,book_layout,other_layout;
    String name,location_new,Quantity,city_new,province_new,img_new,edit,
            donorId,description,size,brand,author,edition,rating_new,category_new;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_item);
        button = findViewById(R.id.upload);
        back = findViewById(R.id.back_btn);
        save = findViewById(R.id.Save);
        imageView = findViewById(R.id.imgView);
        progress = new ProgressDialog(this);
        other_layout=findViewById(R.id.other_layout);
        furniture_layout=findViewById(R.id.furniture_layout);
        cloth_layout=findViewById(R.id.cloth_layout);
        book_layout=findViewById(R.id.book_layout);

        other_category=findViewById(R.id.other_category);
        furniture_description=findViewById(R.id.furniture_description);
        cloth_size=findViewById(R.id.cloth_size);
        cloth_brand=findViewById(R.id.cloth_brand);
        book_author=findViewById(R.id.book_author);
        book_edition=findViewById(R.id.book_edition);


        food_name=findViewById(R.id.food_name);
        location=findViewById(R.id.location);
        food_quantity=findViewById(R.id.food_quantity);

        type_text=findViewById(R.id.type_text);
        userPreferences=new UserPreferences(DonateUsedItem.this);

        province=findViewById(R.id.province);
        city=findViewById(R.id.city);
        rating=findViewById(R.id.rating);

        rating.setOnRatingBarChangeListener((ratingBar, v, b) -> {
//           int numStars = ratingBar.getNumStars();
        });

        ArrayAdapter<String> province_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces);
        province_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        province.setAdapter(province_adapter);
        province.setOnItemSelectedListener(this);

        db = FirebaseFirestore.getInstance();


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


            city_new = getIntent().getStringExtra("city");

            description = getIntent().getStringExtra("description");
            size = getIntent().getStringExtra("size");
            brand = getIntent().getStringExtra("brand");


            author = getIntent().getStringExtra("author");
            edition = getIntent().getStringExtra("edition");
            rating_new = getIntent().getStringExtra("rating");

            category_new = getIntent().getStringExtra("category");

            int spinnerPosition3 = province_adapter.getPosition(province_new);
            province.setSelection(spinnerPosition3);


            furniture_description.setText(description);
            cloth_size.setText(size);
            cloth_brand.setText(brand);
            book_author.setText(author);
            book_edition.setText(edition);
            rating.setRating(Float.parseFloat(rating_new));

            food_name.setText(name);
            location.setText(location_new);
            other_category.setText(category_new);
            food_quantity.setText(Quantity);
            city.setText(city_new);
            save.setText("Edit");

            Glide.with(DonateUsedItem.this)
                    .load(img_new)
                    .into(imageView);

        }

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
        else
        {
            other_layout.setVisibility(View.VISIBLE);
            cloth_layout.setVisibility(View.GONE);
            furniture_layout.setVisibility(View.GONE);
            book_layout.setVisibility(View.GONE);
        }

        type_text.setText("Donate "+type);

        button.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                    String[] permissions ={Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions,PERMISSION_CODE);
                }else{
                    pickimage();
                }
            }else {
                pickimage();
            }
        });

        save.setOnClickListener(v -> {

            if (food_name.getText().toString().isEmpty()) {
                Toast.makeText(DonateUsedItem.this, "Please Enter Food Name", Toast.LENGTH_SHORT).show();

            }
            else if (food_quantity.getText().toString().isEmpty()) {
                Toast.makeText(DonateUsedItem.this, "Please Enter Food Quantity", Toast.LENGTH_SHORT).show();

            }

            else if (city.getText().toString().isEmpty()) {
                Toast.makeText(DonateUsedItem.this, "Please Enter City", Toast.LENGTH_SHORT).show();

            }
            else if (location.getText().toString().isEmpty()) {
                Toast.makeText(DonateUsedItem.this, "Please Enter Street Address", Toast.LENGTH_SHORT).show();
            }
            else if (rating.getRating() == 0.0) {
                Toast.makeText(DonateUsedItem.this, "Please Rate Quality Of Product", Toast.LENGTH_SHORT).show();
            }
            else if (edit == null)
            {
               if (strFilePath.equals("")) {
                Toast.makeText(DonateUsedItem.this, "Please Upload Image", Toast.LENGTH_SHORT).show();
               }
               else {
                   if (type.equalsIgnoreCase("clothes"))
                   {
                       if (cloth_size.getText().toString().isEmpty())
                       {
                           Toast.makeText(DonateUsedItem.this, "Please Enter Cloth Size", Toast.LENGTH_SHORT).show();
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
                       if (edit != null)
                       {
                           SaveDataNew();
                       }
                       else {
                           SaveData();
                       }
//                    }
                   }
                   else if (type.equalsIgnoreCase("books"))
                   {
                       if (book_author.getText().toString().isEmpty())
                       {
                           Toast.makeText(DonateUsedItem.this, "Please Enter Author of Book", Toast.LENGTH_SHORT).show();
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
                   }
                   else
                   {
                       if (other_category.getText().toString().isEmpty())
                       {
                           Toast.makeText(DonateUsedItem.this, "Please Enter Category", Toast.LENGTH_SHORT).show();
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
                   }
               }
            }

            else {
                if (type.equalsIgnoreCase("clothes"))
                {
                    if (cloth_size.getText().toString().isEmpty())
                    {
                        Toast.makeText(DonateUsedItem.this, "Please Enter Cloth Size", Toast.LENGTH_SHORT).show();
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
                    if (edit != null)
                    {
                        SaveDataNew();
                    }
                    else {
                        SaveData();
                    }
//                    }
                }
                else if (type.equalsIgnoreCase("books"))
                {
                    if (book_author.getText().toString().isEmpty())
                    {
                        Toast.makeText(DonateUsedItem.this, "Please Enter Author of Book", Toast.LENGTH_SHORT).show();
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
                }
                else
                {
                    if (other_category.getText().toString().isEmpty())
                    {
                        Toast.makeText(DonateUsedItem.this, "Please Enter Category", Toast.LENGTH_SHORT).show();
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
                }

            }
        });

        back.setOnClickListener(v -> {
            onBackPressed();
        });

        findViewById(R.id.location_pick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DonateUsedItem.this, MapsActivity.class);
                intent.putExtra("type","usedItem");
                startActivity(intent);
            }
        });
    }

    private  void pickimage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,RESULT_LOAD_IMAGE);
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
        c.add(Calendar.DATE, 30);
        String expiry = df.format(c.getTime());

        Map<String, Object> blood_obj = new HashMap<>();
        blood_obj.put("date", time);
        DocumentReference documentReference = db.document("/donor/"+uid);
        blood_obj.put("donorId",  documentReference);
        blood_obj.put("expiry", expiry);
        blood_obj.put("category", type);
        blood_obj.put("city", city.getText().toString());
        blood_obj.put("img", strFilePath);
        blood_obj.put("name", food_name_st);
        blood_obj.put("province", province_st);
        blood_obj.put("quantity", food_quantity_st);
        blood_obj.put("status", "active");
        blood_obj.put("streetAddress", location_st);
        blood_obj.put("rating", rating.getRating());

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
        else
        {
            blood_obj.put("category", other_category.getText().toString());
        }


        db.collection("usedItems").document()
                .set(blood_obj)
                .addOnSuccessListener(aVoid -> {
                    progress.dismiss();
                    Intent intent = new Intent(DonateUsedItem.this, congrat.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progress.dismiss();
                    Toast.makeText(DonateUsedItem.this, e.toString(), Toast.LENGTH_SHORT).show();
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void SaveDataNew() {
        progress.setMessage("Saving Data");
        progress.setCancelable(false);
        progress.show();


        String uid = userPreferences.getUserId();



        Map<String, Object> blood_obj = new HashMap<>();
        blood_obj.put("category", type);
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
        blood_obj.put("rating", rating.getRating());

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
        else
        {
            blood_obj.put("category", other_category.getText().toString());
        }


        db.collection("usedItems").document(donorId)
                .update(blood_obj)
                .addOnSuccessListener(aVoid -> {
                    progress.dismiss();
                    Toast.makeText(this, "Successfully Updated Donation :)", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progress.dismiss();
                    Toast.makeText(DonateUsedItem.this, e.toString(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadFileAndSaveToFireBase(Uri pathUri) {

        progress.setMessage("Uploading Image");
        progress.setCancelable(false);
        progress.show();
        Date currentTime = Calendar.getInstance().getTime();
        long time = currentTime.getTime();

        StorageReference photoRef = FirebaseStorage.getInstance().getReference().child("images").child(time+".jpg");
        photoRef.putFile(pathUri).addOnSuccessListener(DonateUsedItem.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {

            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                progress.dismiss();
                Toast.makeText(DonateUsedItem.this,"Upload is done...",Toast.LENGTH_SHORT).show();
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