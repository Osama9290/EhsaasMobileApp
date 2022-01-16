package com.example.ehsaas.UI.SignUpLogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.Dashboard;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SignUpVolunteer extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{


    private FirebaseAuth mAuth;
    EditText et_email,et_password,et_age,et_city,et_occupation,et_institute,et_full_name,et_phone;
    FirebaseFirestore db;
    CollectionReference dbdonor;
    ProgressDialog progress;
    Spinner province;
    String province_st;

    String[] provinces = {"Punjab", "Sindh","Balochistan","Khyber Pakhtunkhwa","Azad Kashmir"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_volunteer);

        TextView signin = findViewById(R.id.signin);
        Button signup =findViewById(R.id.signup);
        ImageView back_btn=findViewById(R.id.back_btn);
        et_password=findViewById(R.id.et_password);
        et_email=findViewById(R.id.et_email);
        et_age=findViewById(R.id.et_age);
        province=findViewById(R.id.province);

        et_city=findViewById(R.id.et_city);
        et_occupation=findViewById(R.id.et_occupation);
        et_institute=findViewById(R.id.et_institute);

        et_phone=findViewById(R.id.et_phone);
        et_full_name=findViewById(R.id.et_full_name);
        progress = new ProgressDialog(this);
        String type;

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ArrayAdapter<String> province_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces);
        province_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        province.setAdapter(province_adapter);
        province.setOnItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
        signin.setOnClickListener(v -> {

            Intent intent=new Intent(SignUpVolunteer.this, SignIn.class);
            startActivity(intent);
        });
        db = FirebaseFirestore.getInstance();


        dbdonor = db.collection("donor");

        type = getIntent().getStringExtra("type");


        signup.setOnClickListener(v -> {

            if (et_email.getText().toString().isEmpty())
            {
                Toast.makeText(SignUpVolunteer.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
            }
            else if (et_full_name.getText().toString().isEmpty())
            {
                Toast.makeText(SignUpVolunteer.this, "Enter Name", Toast.LENGTH_SHORT).show();
            }
            else if (et_password.getText().toString().isEmpty())
            {
                Toast.makeText(SignUpVolunteer.this, "Enter Password", Toast.LENGTH_SHORT).show();
            }
            else if (et_password.getText().toString().length() < 6)
            {
                Toast.makeText(SignUpVolunteer.this, "Password should be greater than 5 characters", Toast.LENGTH_SHORT).show();
            }
            else if (et_age.getText().toString().isEmpty())
            {
                Toast.makeText(SignUpVolunteer.this, "Please Enter Age", Toast.LENGTH_SHORT).show();
            }

            else if (et_city.getText().toString().isEmpty())
            {
                Toast.makeText(SignUpVolunteer.this, "Please Enter Your City ", Toast.LENGTH_SHORT).show();
            }

            else if (et_occupation.getText().toString().isEmpty())
            {
                Toast.makeText(SignUpVolunteer.this, "Please Enter Your Occupation ", Toast.LENGTH_SHORT).show();
            }
            else if (et_institute.getText().toString().isEmpty())
            {
                Toast.makeText(SignUpVolunteer.this, "Please Enter Your Institute ", Toast.LENGTH_SHORT).show();
            }

            else {
                progress.setMessage("Registering");
                progress.setCancelable(false);
                progress.show();
                mAuth.createUserWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString())
                        .addOnCompleteListener(SignUpVolunteer.this, task -> {
                            if (task.isSuccessful()) {
                                progress.dismiss();
                                String uid = task.getResult().getUser().getUid();

                                SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss zzz", Locale.US);
                                String time = df.format(new Date());
                                Map<String, Object> user = new HashMap<>();
                                user.put("contactNo", et_phone.getText().toString());
                                user.put("email", et_email.getText().toString());
                                user.put("city", et_city.getText().toString());
                                user.put("deliveryCount", 0);
                                user.put("institution", et_institute.getText().toString());
                                user.put("occupation", et_occupation.getText().toString());
                                user.put("province", province_st);
                                user.put("status", "active");
                                user.put("name", et_full_name.getText().toString());
                                user.put("age", et_age.getText().toString());
                                user.put("createdAt", time);

                                db.collection("volunteer").document(uid)
                                        .set(user)
                                        .addOnSuccessListener(aVoid -> {

                                            FirebaseUser user1 = mAuth.getCurrentUser();
                                            UserPreferences userPreferences=new UserPreferences(SignUpVolunteer.this);
                                            userPreferences.setUserId(user1.getUid());
                                            userPreferences.setUseremail(user1.getEmail());
                                            userPreferences.createUserLoginSession(user1.getEmail());
                                            userPreferences.setUserType("volunteer");
                                            userPreferences.setFname(et_full_name.getText().toString());
                                            userPreferences.setPhone(et_phone.getText().toString());

                                            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");
                                            Token token= new Token(userPreferences.getDevice_token());
                                            ref.child(user1.getUid()).setValue(token);

                                            Intent intent=new Intent(SignUpVolunteer.this, Dashboard.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(SignUpVolunteer.this, e.toString(), Toast.LENGTH_SHORT).show());

                            } else {
                                progress.dismiss();
                                Toast.makeText(SignUpVolunteer.this, Objects.requireNonNull(task.getException()).toString(),
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
            }

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