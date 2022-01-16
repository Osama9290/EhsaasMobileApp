package com.example.ehsaas.UI.Blood.Donor;

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

import com.bumptech.glide.Glide;
import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.congrat;
import com.example.ehsaas.UI.Food.Donor.DonateFood;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DonateBlood extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {


    private final static int RESULT_LOAD_IMAGE = 1000;
    private final static int PERMISSION_CODE = 1001;
    String[] blood_type = {"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
    String[] smoke = {"No", "Yes"};
    String[] transfusion = {"Yes", "No"};
    String[] provinces = {"Punjab", "Sindh","Balochistan","Khyber Pakhtunkhwa","Azad Kashmir"};
    FirebaseFirestore db;
    UserPreferences userPreferences;
    String strFilePath="";
    ProgressDialog progress;
    ImageView imgView;
    String blood="", gender_st ="",blood_tran="",last_don_text_st="",illness_cat_st="",
            medication_cat_st="",vaccination_et_st="",province_et_st="";
    EditText age_cat,last_don_text,illness_cat,medication_cat,vaccination_et,city_et;

    String edit,age_cat_new,last_don_text_new,illness_cat_new,
            donorId,medication_cat_new,vaccination_et_new,city_et_new,img_new,
            bloodGroup_new,city_new,blood_trans_new,smoke_new,province_new;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_blood);

        age_cat=findViewById(R.id.age_cat);
        last_don_text=findViewById(R.id.last_don_text);
        illness_cat=findViewById(R.id.illness_cat);
        medication_cat=findViewById(R.id.medication_cat);
        vaccination_et=findViewById(R.id.vaccination_et);
        city_et=findViewById(R.id.city_et);
        Spinner province_et=findViewById(R.id.province_et);
        progress = new ProgressDialog(this);
        imgView=findViewById(R.id.imgView);


        Spinner blood = findViewById(R.id.blood_cat);
        Spinner smoke_et = findViewById(R.id.smoke_et);
        Spinner blood_trans = findViewById(R.id.blood_trans_cat);
        Button save = findViewById(R.id.Save);
        Button upload=findViewById(R.id.upload);
        ImageView back = findViewById(R.id.back_btn);
        userPreferences=new UserPreferences(DonateBlood.this);

        blood.setOnItemSelectedListener(this);
        smoke_et.setOnItemSelectedListener(this);
        blood_trans.setOnItemSelectedListener(this);

        db = FirebaseFirestore.getInstance();

        upload.setOnClickListener(v -> {
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

        ArrayAdapter<String> Blood = new ArrayAdapter(this, android.R.layout.simple_spinner_item, blood_type);
        Blood.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> Gender = new ArrayAdapter(this, android.R.layout.simple_spinner_item, smoke);
        Gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> Blood_trans = new ArrayAdapter(this, android.R.layout.simple_spinner_item, transfusion);
        Blood_trans.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> province = new ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces);
        province.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        province_et.setAdapter(province);
        blood.setAdapter(Blood);
        smoke_et.setAdapter(Gender);
        blood_trans.setAdapter(Blood_trans);
        province_et.setOnItemSelectedListener(this);


        edit = getIntent().getStringExtra("edit");

        if (edit != null)
        {
            age_cat_new = getIntent().getStringExtra("age_cat_new");
            last_don_text_new = getIntent().getStringExtra("last_don_text_new");
            illness_cat_new = getIntent().getStringExtra("illness_cat_new");
            medication_cat_new = getIntent().getStringExtra("medication_cat_new");
            vaccination_et_new = getIntent().getStringExtra("vaccination_et_new");
            city_et_new = getIntent().getStringExtra("city_et_new");
            donorId = getIntent().getStringExtra("id");
            img_new = getIntent().getStringExtra("img");


            bloodGroup_new = getIntent().getStringExtra("bloodGroup_new");
            city_new = getIntent().getStringExtra("city");
            blood_trans_new = getIntent().getStringExtra("blood_trans_new");
            smoke_new = getIntent().getStringExtra("smoke_new");
            donorId = getIntent().getStringExtra("id");
            province_new = getIntent().getStringExtra("province");


            age_cat.setText(age_cat_new);
            last_don_text.setText(last_don_text_new);
            illness_cat.setText(illness_cat_new);
            medication_cat.setText(medication_cat_new);
            vaccination_et.setText(vaccination_et_new);
            city_et.setText(city_et_new);


            int spinnerPosition = Blood.getPosition(bloodGroup_new);
            blood.setSelection(spinnerPosition);


            int spinnerPosition2 = Blood_trans.getPosition(blood_trans_new);
            blood_trans.setSelection(spinnerPosition2);


            int spinnerPosition3 = province.getPosition(province_new);
            province_et.setSelection(spinnerPosition3);

            int spinnerPosition4 = Gender.getPosition(smoke_new);
            smoke_et.setSelection(spinnerPosition4);

            Glide.with(DonateBlood.this)
                    .load(img_new)
                    .into(imgView);


        }

        save.setOnClickListener(v -> {



            if (!last_don_text.getText().toString().isEmpty())
            {
                last_don_text_st=last_don_text.getText().toString();
            }
            if (!illness_cat.getText().toString().isEmpty())
            {
                illness_cat_st=illness_cat.getText().toString();
            }
             if (!medication_cat.getText().toString().isEmpty())
            {
                medication_cat_st=medication_cat.getText().toString();
            }
             if (!vaccination_et.getText().toString().isEmpty())
            {
                vaccination_et_st=vaccination_et.getText().toString();
            }


            if (age_cat.getText().toString().isEmpty())
            {
                Toast.makeText(DonateBlood.this, "Please enter age.", Toast.LENGTH_SHORT).show();
            }
            else if (Integer.parseInt(age_cat.getText().toString()) < 18 ||
                    Integer.parseInt(age_cat.getText().toString()) > 65)
            {
                Toast.makeText(DonateBlood.this, "Age must be between 18 and 65.", Toast.LENGTH_SHORT).show();
            }
            else if (city_et.getText().toString().isEmpty())
            {
                Toast.makeText(DonateBlood.this, "Please enter city", Toast.LENGTH_SHORT).show();
            }
            else if (strFilePath.equals("")) {
                if (edit != null)
                {
                    SaveDataNew();
                }
                else {
                    Toast.makeText(DonateBlood.this, "Please Upload Image", Toast.LENGTH_SHORT).show();

                }
            }
            else {
                if (edit != null)
                {
                    SaveDataNew();
                }
                else {
                    SaveData();
                }

            }



        });

        back.setOnClickListener(v -> {
            finish();
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
        blood_obj.put("illness", illness_cat_st);

        blood_obj.put("img", strFilePath);
        blood_obj.put("lastDonated", last_don_text_st);
        blood_obj.put("province", province_et_st);
        blood_obj.put("smoking", gender_st);

        blood_obj.put("status", "active");
        blood_obj.put("vaccination", vaccination_et_st);
        String s = age_cat.getText().toString();
        blood_obj.put("age", s);
        blood_obj.put("bloodGroup", blood);
//        blood_obj.put("gender", gender_st);

        blood_obj.put("bloodTransfusion", blood_tran);
        blood_obj.put("city", city_et.getText().toString());
        blood_obj.put("currentMedication", medication_cat_st);



        db.collection("blood").document()
                .set(blood_obj)
                .addOnSuccessListener(aVoid -> {
                    progress.dismiss();
                    Intent intent = new Intent(DonateBlood.this, congrat.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        {
                            progress.dismiss();
                            Toast.makeText(DonateBlood.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                );
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void SaveDataNew() {

        progress.setMessage("Saving Data");
        progress.setCancelable(false);
        progress.show();


        Map<String, Object> blood_obj = new HashMap<>();
        blood_obj.put("illness", illness_cat_st);

        if (strFilePath.equalsIgnoreCase(""))
        {
            blood_obj.put("img", img_new);
        }
        else {
            blood_obj.put("img", strFilePath);
        }
        blood_obj.put("lastDonated", last_don_text_st);
        blood_obj.put("province", province_et_st);
        blood_obj.put("smoking", gender_st);

        blood_obj.put("vaccination", vaccination_et_st);
        String s = age_cat.getText().toString();
        blood_obj.put("age", s);
        blood_obj.put("bloodGroup", blood);

        blood_obj.put("bloodTransfusion", blood_tran);
        blood_obj.put("city", city_et.getText().toString());
        blood_obj.put("currentMedication", medication_cat_st);


        db.collection("blood").document(donorId)
                .update(blood_obj)
                .addOnSuccessListener(aVoid -> {
                    progress.dismiss();
                    Toast.makeText(this, "Successfully Updated Donation :)", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        {
                            progress.dismiss();
                            Toast.makeText(DonateBlood.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                );
    }


    private void uploadFileAndSaveToFireBase(Uri pathUri) {

        progress.setMessage("Uploading Image");
        progress.setCancelable(false);
        progress.show();
        Date currentTime = Calendar.getInstance().getTime();
        long time = currentTime.getTime();

        StorageReference photoRef = FirebaseStorage.getInstance().getReference().child("images").child(time+".jpg");
        photoRef.putFile(pathUri).addOnSuccessListener(DonateBlood.this, taskSnapshot -> {

            progress.dismiss();
            Toast.makeText(DonateBlood.this,"Upload is done...",Toast.LENGTH_SHORT).show();
            String s = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
            result.addOnSuccessListener(uri -> strFilePath = uri.toString());

        }).addOnFailureListener(e -> {
            progress.dismiss();
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.blood_cat:
                blood=blood_type[position];
                break;

            case R.id.province_et:
                province_et_st=provinces[position];
                break;

            case R.id.smoke_et:
                gender_st = smoke[position];
                break;

            case R.id.blood_trans_cat:
                blood_tran=transfusion[position];
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
            imgView.setImageURI(data.getData());

            Uri data1 = data.getData();
            uploadFileAndSaveToFireBase(data1);
        }
    }
}