package com.example.ehsaas.UI.SignUpLogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Preferences.UserPreferences;

public class SelectUserType extends AppCompatActivity {


    Button donor,receiver;
    UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);

        receiver=findViewById(R.id.receiver);
        donor=findViewById(R.id.donor);
        userPreferences=new UserPreferences(SelectUserType.this);

//        FirebaseInstallations.getInstance().getToken(false).addOnCompleteListener(new OnCompleteListener<InstallationTokenResult>() {
//            @Override
//            public void onComplete(@NonNull Task<InstallationTokenResult> task) {
//                if(!task.isSuccessful()){
//                    return;
//                }
//                // Get new Instance ID token
//                String token2 = task.getResult().getToken();
//
//                userPreferences.setDevice_token(token2);
//
//            }
//        });




        donor.setOnClickListener(v -> {
            Intent intent=new Intent(SelectUserType.this,SignIn.class);
            intent.putExtra("type","donor");
            startActivity(intent);
        });

        receiver.setOnClickListener(v -> {
            Intent intent=new Intent(SelectUserType.this,SignIn.class);
            intent.putExtra("type","receiver");
            startActivity(intent);
        });

        findViewById(R.id.volunteer).setOnClickListener(v -> {
            Intent intent=new Intent(SelectUserType.this,SignIn.class);
            intent.putExtra("type","volunteer");
            startActivity(intent);
        });
    }
}