package com.example.ehsaas.UI.Profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.Dashboard;
import com.example.ehsaas.UI.Food.Donor.DonateFood;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.Profile.profile;
import com.example.ehsaas.UI.SignUpLogin.SignUp;
import com.example.ehsaas.UI.notification.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class edit_profile extends Fragment {

    EditText et_email,et_name,et_contact;
    UserPreferences userPreferences;
    CircleImageView prfile_picture;

    private static int RESULT_LOAD_IMAGE = 1000;
    private final static int PERMISSION_CODE = 1001;

    String strFilePath = "";
    ProgressDialog progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_edit_profile, container, false);

        ImageView back = view.findViewById(R.id.back_btn);

        et_name=view.findViewById(R.id.et_name);
        et_email=view.findViewById(R.id.et_email);
        et_contact=view.findViewById(R.id.et_contact);
        userPreferences=new UserPreferences(getActivity());
        prfile_picture=view.findViewById(R.id.prfile_picture);

        et_email.setText(userPreferences.getUseremail());
        progress = new ProgressDialog(getActivity());

        et_name.setText(userPreferences.getFname());
        et_contact.setText(userPreferences.getPhone());

        Glide.with(getActivity())
                .load(userPreferences.getDp())
                .placeholder(R.drawable.user)
                .into(prfile_picture);

        Button save = view.findViewById(R.id.save);

        back.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, new profile());
            ft.commit();
        });

        save.setOnClickListener(v -> {
            SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss zzz", Locale.US);
            String time = df.format(new Date());
            Map<String, Object> user = new HashMap<>();
            user.put("contactNo", et_contact.getText().toString());
            user.put("name", et_name.getText().toString());
            user.put("createdAt", time);
            if (!strFilePath.equalsIgnoreCase(""))
            {
                user.put("img", strFilePath);
            }
            else {
                user.put("img", userPreferences.getDp());
            }


            FirebaseFirestore db;
            db = FirebaseFirestore.getInstance();
            db.collection(userPreferences.getUserType()).document(userPreferences.getUserId())
                    .update(user)
                    .addOnSuccessListener(aVoid -> {

                        userPreferences.setFname(et_name.getText().toString());
                        userPreferences.setPhone(et_contact.getText().toString());
                        userPreferences.setDp(strFilePath);
                        Toast.makeText(getActivity(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();

                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show());

        });


        prfile_picture.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    pickimage();
                }
            } else {
                pickimage();
            }
        });



        return  view;
    }



    private void pickimage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
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
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMAGE) {
            prfile_picture.setImageURI(data.getData());

            Uri data1 = data.getData();
            uploadFileAndSaveToFireBase(data1);
        }
    }

    private void uploadFileAndSaveToFireBase(Uri pathUri) {

        progress.setMessage("Uploading Image");
        progress.setCancelable(false);
        progress.show();

        StorageReference photoRef = FirebaseStorage.getInstance().getReference().child("images").child(pathUri.getLastPathSegment());
        photoRef.putFile(pathUri).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {

            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                progress.dismiss();
                Toast.makeText(getActivity(), "Upload is done...", Toast.LENGTH_SHORT).show();
                String s = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(uri -> strFilePath = uri.toString());

            }
        }).addOnFailureListener(e -> {
            progress.dismiss();

        });
    }
}