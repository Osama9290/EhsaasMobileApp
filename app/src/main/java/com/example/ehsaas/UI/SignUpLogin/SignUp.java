package com.example.ehsaas.UI.SignUpLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.Dashboard;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

public class SignUp extends AppCompatActivity {


    private FirebaseAuth mAuth;
    EditText et_email,et_password,et__confirm_password,et_full_name,et_phone,et_cnic;
    FirebaseFirestore db;
    CollectionReference dbdonor;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView signin = findViewById(R.id.signin);
        Button signup =findViewById(R.id.signup);
        ImageView back_btn=findViewById(R.id.back_btn);
        et_password=findViewById(R.id.et_password);
        et_email=findViewById(R.id.et_email);
        et__confirm_password=findViewById(R.id.et__confirm_password);
        et_phone=findViewById(R.id.et_phone);
        et_full_name=findViewById(R.id.et_full_name);
        et_cnic=findViewById(R.id.et_cnic);
        progress = new ProgressDialog(this);
        String type;


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        signin.setOnClickListener(v -> {

            Intent intent=new Intent(SignUp.this, SignIn.class);
            startActivity(intent);
        });
        db = FirebaseFirestore.getInstance();


        dbdonor = db.collection("donor");

        type = getIntent().getStringExtra("type");

        et_cnic.addTextChangedListener(new TextWatcher() {
            int len=0;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String str = et_cnic.getText().toString();

                if((str.length()==5 && len <str.length()) || (str.length()==13 && len <str.length())){
                    et_cnic.append("-");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                String str = et_cnic.getText().toString();
                len = str.length();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        signup.setOnClickListener(v -> {
            if (et_email.getText().toString().isEmpty())
            {
                Toast.makeText(SignUp.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
            }
            else if (et_password.getText().toString().isEmpty())
            {
                Toast.makeText(SignUp.this, "Enter Password", Toast.LENGTH_SHORT).show();
            }
            else if (et_password.getText().toString().length() < 6)
            {
                Toast.makeText(SignUp.this, "Password should be greater than 5 characters", Toast.LENGTH_SHORT).show();
            }
            else if (!et_password.getText().toString().equals(et__confirm_password.getText().toString()))
            {
                Toast.makeText(SignUp.this, "Confirm Password must be matched", Toast.LENGTH_SHORT).show();
            }

            else if (et_cnic.getText().toString().isEmpty())
            {
                Toast.makeText(SignUp.this, "Please Enter Your CNIC ", Toast.LENGTH_SHORT).show();
            }
            else if (et_cnic.getText().toString().length() < 15)
            {
                Toast.makeText(SignUp.this, "Please Enter Valid CNIC ", Toast.LENGTH_SHORT).show();
            }
            else {
                progress.setMessage("Registering");
                progress.setCancelable(false);
                progress.show();
                mAuth.createUserWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString())
                        .addOnCompleteListener(SignUp.this, task -> {
                            if (task.isSuccessful()) {
                                progress.dismiss();
                                String uid = task.getResult().getUser().getUid();

                                SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss zzz", Locale.US);
                                String time = df.format(new Date());
                                Map<String, Object> user = new HashMap<>();
                                user.put("contactNo", et_phone.getText().toString());
                                user.put("email", et_email.getText().toString());
                                user.put("name", et_full_name.getText().toString());
                                user.put("status", "active");
                                user.put("cnic", et_cnic.getText().toString());
                                user.put("createdAt", time);

                                db.collection(type).document(uid)
                                        .set(user)
                                        .addOnSuccessListener(aVoid -> {

                                            FirebaseUser user1 = mAuth.getCurrentUser();
                                            UserPreferences userPreferences=new UserPreferences(SignUp.this);
                                            userPreferences.setUserId(user1.getUid());
                                            userPreferences.setUseremail(user1.getEmail());
                                            userPreferences.createUserLoginSession(user1.getEmail());
                                            userPreferences.setUserType(type);
                                            userPreferences.setFname(et_full_name.getText().toString());
                                            userPreferences.setPhone(et_phone.getText().toString());

                                            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");
                                            Token token= new Token(userPreferences.getDevice_token());
                                            ref.child(user1.getUid()).setValue(token);

                                            Intent intent=new Intent(SignUp.this, Dashboard.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(SignUp.this, e.toString(), Toast.LENGTH_SHORT).show());

                            } else {
                                progress.dismiss();
                                Toast.makeText(SignUp.this, Objects.requireNonNull(task.getException()).toString(),
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
            }

        });

    }
}