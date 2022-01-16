package com.example.ehsaas.UI.Blood.Receiver;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.UsedItems.Receiver.RequestUsedItem;
import com.example.ehsaas.UI.Activities.congrat;
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

public class RequestBlood extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {


    private final static int RESULT_LOAD_IMAGE = 1000;
    private final static int PERMISSION_CODE = 1001;
    String[] blood_type = {"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
    String[] provinces = {"Punjab", "Sindh","Balochistan","Khyber Pakhtunkhwa","Azad Kashmir"};
    String[] pick_type = {"Yes", "No"};
    FirebaseFirestore db;
    UserPreferences userPreferences;
    ProgressDialog progress;
    String blood="",province_et_st="", pickup_st="", hblevel_st="", pints="";
    EditText age_cat,patient_name,disease,hb_level,pints_required,
            time_limit,hospital,city_et,attendant_name,contact_no;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_blood);

        age_cat=findViewById(R.id.age_cat);
        patient_name=findViewById(R.id.patient_name);
        disease=findViewById(R.id.disease);
        hb_level=findViewById(R.id.hb_level);
        pints_required=findViewById(R.id.pints_required);
        time_limit=findViewById(R.id.time_limit);
        hospital=findViewById(R.id.hospital);
        city_et=findViewById(R.id.city_et);
        attendant_name=findViewById(R.id.attendant_name);
        contact_no=findViewById(R.id.contact_no);
        Spinner province_et=findViewById(R.id.province_et);
        progress = new ProgressDialog(this);


        Spinner blood = findViewById(R.id.blood_cat);

        Spinner pick_drop=findViewById(R.id.pick_drop);
        Button save = findViewById(R.id.Save);

        ImageView back = findViewById(R.id.back_btn);
        userPreferences=new UserPreferences(RequestBlood.this);

        db = FirebaseFirestore.getInstance();


        ArrayAdapter<String> Blood = new ArrayAdapter(this, android.R.layout.simple_spinner_item, blood_type);
        Blood.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> province = new ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces);
        province.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> pickdrop_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, pick_type);
        pickdrop_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        blood.setOnItemSelectedListener(this);
        province_et.setAdapter(province);
        blood.setAdapter(Blood);
        province_et.setOnItemSelectedListener(this);
        pick_drop.setAdapter(pickdrop_adapter);
        pick_drop.setOnItemSelectedListener(this);

        attendant_name.setText(userPreferences.getFname());
        contact_no.setText(userPreferences.getPhone());


        save.setOnClickListener(v -> {


            if (!hb_level.getText().toString().isEmpty())
            {
                hblevel_st=hb_level.getText().toString();
            }
            if (!pints_required.getText().toString().isEmpty())
            {
                pints=pints_required.getText().toString();
            }

             if (patient_name.getText().toString().isEmpty())
            {
                Toast.makeText(RequestBlood.this, "Please enter patient name", Toast.LENGTH_SHORT).show();
            }

            else if (age_cat.getText().toString().isEmpty())
            {
                Toast.makeText(RequestBlood.this, "Please enter age.", Toast.LENGTH_SHORT).show();
            }
            else if (Integer.parseInt(age_cat.getText().toString()) < 18 ||
                    Integer.parseInt(age_cat.getText().toString()) > 85)
            {
                Toast.makeText(RequestBlood.this, "Age must be between 18 and 85.", Toast.LENGTH_SHORT).show();
            }

            else if (disease.getText().toString().isEmpty())
            {
                Toast.makeText(RequestBlood.this, "Please enter disease name", Toast.LENGTH_SHORT).show();
            }
            else if (time_limit.getText().toString().isEmpty())
            {
                Toast.makeText(RequestBlood.this, "Please enter time limit", Toast.LENGTH_SHORT).show();
            }
            else if (hospital.getText().toString().isEmpty())
            {
                Toast.makeText(RequestBlood.this, "Please enter hospital name", Toast.LENGTH_SHORT).show();
            }
            else if (city_et.getText().toString().isEmpty())
            {
                Toast.makeText(RequestBlood.this, "Please enter city", Toast.LENGTH_SHORT).show();
            }
            else if (attendant_name.getText().toString().isEmpty()) {
                Toast.makeText(RequestBlood.this, "Please Enter Attendant Name", Toast.LENGTH_SHORT).show();
            }
            else if (contact_no.getText().toString().isEmpty()) {
                Toast.makeText(RequestBlood.this, "Please Enter Contact No.", Toast.LENGTH_SHORT).show();
            }

            else {
                SaveData();
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


        Map<String, Object> blood_obj = new HashMap<>();
        blood_obj.put("date", time);
        DocumentReference documentReference = db.document("/receiver/"+uid);
        blood_obj.put("receiverId",  documentReference);
        blood_obj.put("attendantContact", contact_no.getText().toString());
        blood_obj.put("attendantName", attendant_name.getText().toString());
        blood_obj.put("pickDrop", pickup_st);
        blood_obj.put("disease", disease.getText().toString());

        blood_obj.put("hbLevel", hblevel_st);
        blood_obj.put("province", province_et_st);
        blood_obj.put("patientName", patient_name.getText().toString());

        blood_obj.put("status", "active");
        blood_obj.put("pintsRequired", pints_required.getText().toString());
        String s = age_cat.getText().toString();
        blood_obj.put("age", s);
        blood_obj.put("bloodGroup", blood);
        blood_obj.put("timeLimit", time_limit.getText().toString());
        blood_obj.put("city", city_et.getText().toString());



        db.collection("bloodRequest").document()
                .set(blood_obj)
                .addOnSuccessListener(aVoid -> {
                    progress.dismiss();
                    Toast.makeText(this, "Successfully Requested for Blood :)", Toast.LENGTH_SHORT).show();

                    finish();
                })
                .addOnFailureListener(e ->
                        {
                            progress.dismiss();
                            Toast.makeText(RequestBlood.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                );
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