package com.example.ehsaas.UI.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.example.ehsaas.R;
import com.example.ehsaas.UI.Food.Donor.DonateFood;
import com.example.ehsaas.UI.Fragments.DonationCategory;
import com.example.ehsaas.UI.Fragments.ReceiverCategory;
import com.example.ehsaas.UI.Fragments.VolunteerPortal;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.Profile.profile;
import com.example.ehsaas.UI.Fragments.search;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Dashboard extends AppCompatActivity {


    UserPreferences userPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        userPreferences = new UserPreferences(Dashboard.this);
        String userId = userPreferences.getUserId();
        if (userPreferences.getUserType().equalsIgnoreCase("donor"))
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DonationCategory()).commit();
        }
        else if (userPreferences.getUserType().equalsIgnoreCase("volunteer"))
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VolunteerPortal()).commit();

        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ReceiverCategory()).commit();
        }

//        try {
//            MimeMessage email = createEmail("toobariaz726@gmail.com", "toobariaz726@gmail.com", "subject", "body");
//
////            createMessageWithEmail(email);
//        } catch (MessagingException | IOException e) {
//            e.printStackTrace();
//        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.home:

                    if (userPreferences.getUserType().equalsIgnoreCase("donor"))
                    {
                        selectedFragment = new DonationCategory();
                    }
                    else if (userPreferences.getUserType().equalsIgnoreCase("volunteer"))
                    {
                        selectedFragment = new VolunteerPortal();
                    }
                    else {
                        selectedFragment = new ReceiverCategory();
                    }

                    break;
                case R.id.search:
                    selectedFragment = new search();
                    break;
                case R.id.profile:
                    selectedFragment = new profile();
                    break;
                case R.id.chats:
                    selectedFragment = new com.example.ehsaas.UI.Fragments.Dashboard();
                    break;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        }
    };


        public static MimeMessage createEmail(String to,
                String from,
                String subject,
                String bodyText)
            throws MessagingException {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage email = new MimeMessage(session);

            email.setFrom(new InternetAddress(from));
            email.addRecipient(javax.mail.Message.RecipientType.TO,
                    new InternetAddress(to));
            email.setSubject(subject);
            email.setText(bodyText);
            return email;
        }

//    public static Message createMessageWithEmail(MimeMessage emailContent)
//            throws MessagingException, IOException {
//        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//        emailContent.writeTo(buffer);
//        byte[] bytes = buffer.toByteArray();
//        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
//        Message message = new Message();
//        message.setRaw(encodedEmail);
//        return message;
//    }
}