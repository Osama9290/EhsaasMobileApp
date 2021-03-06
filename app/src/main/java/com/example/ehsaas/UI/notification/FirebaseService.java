package com.example.ehsaas.UI.notification;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        if(user != null)
        {
            updateToken(s);
        }
    }

    private void updateToken(String tokenRefresh) {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token= new Token(tokenRefresh);
        ref.child(user.getUid()).setValue(token);

    }
}
