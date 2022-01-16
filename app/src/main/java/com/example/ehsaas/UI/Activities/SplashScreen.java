package com.example.ehsaas.UI.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.SignUpLogin.SelectUserType;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN=2500;

    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo;
    UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        image = findViewById(R.id.logo);
        logo = findViewById(R.id.text);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        userPreferences=new UserPreferences(SplashScreen.this);

        new Handler().postDelayed(() -> {

            if (userPreferences.checkLogin())
            {
                Intent intent = new Intent(SplashScreen.this, Dashboard.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(SplashScreen.this, SelectUserType.class);
                startActivity(intent);
                finish();
            }

        },SPLASH_SCREEN);




    }

}