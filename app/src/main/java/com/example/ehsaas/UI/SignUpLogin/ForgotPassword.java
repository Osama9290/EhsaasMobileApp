package com.example.ehsaas.UI.SignUpLogin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    private EditText email;
    private ImageView iv_back;
    private TextView tv_send;
    UserPreferences user_preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        user_preferences = new UserPreferences(this);




        initializeViews();

        iv_back.setOnClickListener(v -> onBackPressed());

        tv_send.setOnClickListener(v -> validate());
    }

    private void initializeViews() {
        email=findViewById(R.id.email);
        iv_back=findViewById(R.id.back_btn);
        tv_send=findViewById(R.id.send_btn);

    }

    private void validate() {

       if( email.getText().toString().length() == 0 )
        {
            Toast.makeText(this, "Email is required.", Toast.LENGTH_SHORT).show();
            email.requestFocus();

        }
        else  if (!isValid(email.getText().toString()))
        {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            email.requestFocus();

        }
        else {
            callAPI();
        }
    }

    private void callAPI() {

        FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            popup_exit("An email has been sent to you at "+email.getText().toString()+ " to reset your password. Please check spam.");

                        }
                    }
                });


    }

    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public void popup_exit(final String position) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.popup_message, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(ForgotPassword.this);

        final TextView tv_ok = alertLayout.findViewById(R.id.tv_ok);
        final TextView tv_logo = alertLayout.findViewById(R.id.tv_logo);
        tv_logo.setText(position);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();
        dialog.show();

        tv_ok.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

    }
}