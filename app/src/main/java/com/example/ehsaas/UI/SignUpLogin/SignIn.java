package com.example.ehsaas.UI.SignUpLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.Dashboard;
import com.example.ehsaas.UI.Fragments.ReceiverCategory;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.Profile.forgot_pass;
import com.example.ehsaas.UI.notification.Token;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class SignIn extends AppCompatActivity {


    private FirebaseAuth mAuth;
    EditText et_email,et_password;
    ImageView google_signin;
    GoogleSignInClient mGoogleSignInClient;
    String type;
    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        LinearLayout signup = findViewById(R.id.signup);
        TextView forgot = findViewById(R.id.forgot);
        Button signin = findViewById(R.id.signin_btn);
        ImageView back = findViewById(R.id.back_btn);

        mAuth = FirebaseAuth.getInstance();
        et_password=findViewById(R.id.et_password);
        et_email=findViewById(R.id.et_email);
        google_signin=findViewById(R.id.google_signin);
        progress = new ProgressDialog(this);

        type = getIntent().getStringExtra("type");

        back.setOnClickListener(v -> onBackPressed());

        processRequest();

        signup.setOnClickListener(v -> {
            Intent intent;
            if (type.equalsIgnoreCase("volunteer"))
            {
                intent = new Intent(SignIn.this,SignUpVolunteer.class);
            }
            else {
                intent = new Intent(SignIn.this,SignUp.class);

            }

            intent.putExtra("type",type);
           startActivity(intent);
        });

        google_signin.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 101);
        });
        signin.setOnClickListener(v -> {

            if (et_email.getText().toString().isEmpty())
            {
                Toast.makeText(SignIn.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
            }
            else if (et_password.getText().toString().isEmpty())
            {
                Toast.makeText(SignIn.this, "Enter Password", Toast.LENGTH_SHORT).show();
            }
            else {
                progress.setMessage("Loging");
                progress.setCancelable(false);
                progress.show();
                mAuth.signInWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString())
                        .addOnCompleteListener(SignIn.this, task -> {
                            if (task.isSuccessful()) {
                                progress.dismiss();
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserPreferences userPreferences=new UserPreferences(SignIn.this);
                                userPreferences.setUserId(user.getUid());
                                userPreferences.setUseremail(user.getEmail());
                                userPreferences.createUserLoginSession(user.getEmail());
                                userPreferences.setUserType(type);


                                // Get Info
                                FirebaseFirestore db;
                                db = FirebaseFirestore.getInstance();
                                DocumentReference docRef = db.collection(type).document(user.getUid());
                                docRef.get().addOnCompleteListener(task2 -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task2.getResult();
                                        if (document != null && document.exists()) {
                                            Map<String, Object> data = document.getData();
                                            String name_new = String.valueOf(data.get("name"));
                                            String contactNo = String.valueOf(data.get("contactNo"));
                                            String img = String.valueOf(data.get("img"));
                                            userPreferences.setFname(name_new);
                                            userPreferences.setPhone(contactNo);
                                            userPreferences.setDp(img);

                                        } else {

                                        }
                                    }
                                    else {


                                    }
                                });

                                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");
                                Token token= new Token(userPreferences.getDevice_token());
                                ref.child(user.getUid()).setValue(token);

                                Intent intent=new Intent(SignIn.this, Dashboard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                progress.dismiss();
                                Toast.makeText(SignIn.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
//                                        updateUI(null);
                            }
                        });
            }

        });

        forgot.setOnClickListener(v -> {
            startActivity(new Intent(SignIn.this,ForgotPassword.class));
        });

    }

    private void processRequest()
    {
// Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(SignIn.this, gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
//        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(SignIn.this,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
//
//        else if (type.equals("Facebook"))
//        {
//            // Pass the activity result back to the Facebook SDK
//            callbackManager.onActivityResult(requestCode, resultCode, data);
//        }
//
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(SignIn.this, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();

                        updateUI(user);
                    } else {

                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

        if(user!=null)
        {

            UserPreferences userPreferences=new UserPreferences(SignIn.this);
            userPreferences.setUserId(user.getUid());
            userPreferences.setUseremail(user.getEmail());
            userPreferences.createUserLoginSession(user.getEmail());
            userPreferences.setUserType(type);

            Toast.makeText(SignIn.this,"Successfully SignIn with Google",Toast.LENGTH_LONG)
                    .show();
            Intent intent=new Intent(SignIn.this, Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
           ;

        }

    }
}