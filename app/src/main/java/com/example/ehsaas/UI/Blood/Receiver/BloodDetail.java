package com.example.ehsaas.UI.Blood.Receiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.congrat;
import com.example.ehsaas.UI.Blood.Donor.DonateBlood;
import com.example.ehsaas.UI.Chat.ChatActivity;
import com.example.ehsaas.UI.Food.Receiver.FoodDetail;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.Models.BloodModel;
import com.example.ehsaas.UI.Profile.MyDonations;
import com.example.ehsaas.UI.Requests.MyDonationRequests;
import com.example.ehsaas.UI.Volunteer.SelectToDeliver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BloodDetail extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView no_item,category_txt;
    String type = null;
    UserPreferences userPreferences;
    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_blood_deltail);

        ImageView back = findViewById(R.id.back_btn);
        recyclerView=findViewById(R.id.recyclerview);
        no_item=findViewById(R.id.no_item);
        category_txt=findViewById(R.id.category_txt);
        userPreferences = new UserPreferences(BloodDetail.this);
        progress = new ProgressDialog(this);

        back.setOnClickListener(v -> onBackPressed());


        try {
            type = getIntent().getExtras().getString("type");
        } catch (Exception e) {
            e.printStackTrace();
        }
        category_txt.setText(type);

        ArrayList<BloodModel> bloodModels;
        if (type == null)
        {
            category_txt.setText("My Donations");
            bloodModels = MyDonations.bloodModels;
        }
        else {
            bloodModels = rec_blood_cat.bloodModels;
        }


        ArrayList<BloodModel> bloodModels_filtered = new ArrayList<>();


        UserPreferences userPreferences=new UserPreferences(BloodDetail.this);


        if (type == null)
        {
            for (int i=0 ;i<bloodModels.size() ; i++)
            {
                if (bloodModels.get(i).getDonorId().contains(userPreferences.getUserId()))
                {
                    bloodModels_filtered.add(bloodModels.get(i));
                }
            }
        }
        else {
            for (int i=0 ;i<bloodModels.size() ; i++)
            {
                if (bloodModels.get(i).getBloodGroup().equals(type))
                {
                    bloodModels_filtered.add(bloodModels.get(i));
                }
            }
        }

        if (bloodModels_filtered.size() == 0)
        {
            no_item.setVisibility(View.VISIBLE);
        }
        else {
            no_item.setVisibility(View.GONE);
        }


        BloodAdapter groupAdapter = new BloodAdapter(bloodModels_filtered, BloodDetail.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(BloodDetail.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(groupAdapter);
    }


    public class BloodAdapter extends RecyclerView.Adapter<BloodAdapter.MyViewHolder> {
        ArrayList<BloodModel> contactModelList;
        Context context;


        public BloodAdapter(ArrayList<BloodModel> contactModelList, Context context) {
            this.contactModelList = contactModelList;
            this.context = context;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.blood_item_view, parent, false);
            return new MyViewHolder(item);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            BloodModel user_model=contactModelList.get(position);

            if (type == null)
            {
                holder.donor_side.setVisibility(View.VISIBLE);
                holder.interest.setVisibility(View.GONE);
                holder.activate.setVisibility(View.VISIBLE);

                if (user_model.getStatus().equalsIgnoreCase("active"))
                {
                    holder.activate.setText("Donated");
                    holder.donor_side.setVisibility(View.VISIBLE);
                }
                else {
                    holder.activate.setText("Activate");
                    holder.donor_side.setVisibility(View.GONE);
                }


                if (user_model.getStatus().equalsIgnoreCase("donated"))
                {
                    holder.activate.setText("Thanks for your donation :)");
                    holder.donor_side.setVisibility(View.GONE);
                }
            }
            else {
                holder.interest.setVisibility(View.VISIBLE);
                holder.donor_side.setVisibility(View.GONE);
                holder.activate.setVisibility(View.GONE);
            }




            Glide.with(context)
                    .load(user_model.getImg())
                    .placeholder(R.drawable.blood_test)
                    .into(holder.image);

            holder.blood_group.setText(Html.fromHtml("<b>Blood Group:</b> "+user_model.getBloodGroup()));
//            holder.gender_txt.setText(user_model.getG());
            holder.age_txt.setText(Html.fromHtml("<b>Age:</b> "+user_model.getAge()));
            holder.last_donation.setText(Html.fromHtml("<b>Last Donation:</b> "+user_model.getLastDonated()));
            holder.illnes_txt.setText(Html.fromHtml("<b>Illness:</b> "+user_model.getIllness()));
            holder.mediation_txt.setText(Html.fromHtml("<b>Current Medication:</b> "+user_model.getCurrentMedication()));
            holder.vaccination_txt.setText(Html.fromHtml("<b>Vaccination:</b> "+user_model.getVaccination()));
            holder.blood_transformation.setText(Html.fromHtml("<b>Blood Transfusion:</b> "+user_model.getBloodTransfusion()));

            holder.donor_name.setText(Html.fromHtml("<b>Name:</b> "));
            holder.donor_contact.setText(Html.fromHtml("<b>Contact No:</b> "));

            holder.blood_smoking.setText(Html.fromHtml("<b>Smoking:</b> "+user_model.getSmoking()));
            holder.province.setText(Html.fromHtml("<b>Province</b> "+user_model.getProvince()));
            holder.city.setText(Html.fromHtml("<b>City:</b> "+user_model.getCity()));

            try {
                String[] s = user_model.getDate().split(" ");

                holder.date.setText(Html.fromHtml("<b>Date:</b> "+s[0]+" "+s[1]+" "+s[2]+" "+s[3]));
            } catch (Exception e) {
                e.printStackTrace();
            }



            FirebaseFirestore db;
            db = FirebaseFirestore.getInstance();

            String donor= null;
            try {
                String[] split = user_model.getDonorId().split("/");
                donor = split[1];
            } catch (Exception e) {
                e.printStackTrace();
            }

            DocumentReference docRef = db.collection("donor").document(donor);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Map<String, Object> data = document.getData();
                            String name_new = String.valueOf(data.get("name"));
                            String contactNo = String.valueOf(data.get("contactNo"));

                            holder.donor_name.setText(Html.fromHtml("<b>Name:</b> "+name_new));
                            holder.donor_contact.setText(Html.fromHtml("<b>Contact No:</b> "+contactNo));

                        } else {
                            holder.donor_name.setText(Html.fromHtml("<b>Name:</b> "));
                            holder.donor_contact.setText(Html.fromHtml("<b>Contact No:</b> "));
                        }
                    } else {
                        holder.donor_name.setText(Html.fromHtml("<b>Name:</b> "));
                        holder.donor_contact.setText(Html.fromHtml("<b>Contact No:</b> "));

                    }
                }
            });


            String finalDonor1 = donor;
            holder.interest.setOnClickListener(v -> {
                progress.setMessage("Requesting for pickup...");
                progress.setCancelable(false);
                progress.show();

                String uid = userPreferences.getUserId();

                SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss zzz", Locale.US);
                String time = df.format(new Date());


                Map<String, Object> blood_obj = new HashMap<>();
                blood_obj.put("date", time);
                DocumentReference documentReference;
                documentReference = db.document("/receiver/"+uid);
                blood_obj.put("receiverId", documentReference);
                documentReference = db.document("/donor/"+finalDonor1);
                blood_obj.put("donorId", documentReference);
                blood_obj.put("donationId", user_model.getDonation_id());
                blood_obj.put("status", "in progress");

                blood_obj.put("donationCategory", "blood");
                blood_obj.put("via", "pickup");


                db.collection("interested").document()
                        .set(blood_obj)
                        .addOnSuccessListener(aVoid -> {
                            progress.dismiss();
                            Toast.makeText(BloodDetail.this, "Successfully Requested for Pickup blood", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            progress.dismiss();
                            Toast.makeText(BloodDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                        });

            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progress.setMessage("Removing..");
                    progress.setCancelable(false);
                    progress.show();
                    Map<String, Object> blood_obj = new HashMap<>();
                    blood_obj.put("status", "inactive");

                        db.collection("blood").document(user_model.getDonation_id())
                                .update(blood_obj)
                                .addOnSuccessListener(aVoid -> {
                                    progress.dismiss();
                                    Toast.makeText(BloodDetail.this, "Donation is removed successfully.", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    progress.dismiss();
                                    Toast.makeText(BloodDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                                });
                    }
            });


            holder.activate.setOnClickListener(v -> {
                if (user_model.getStatus().equalsIgnoreCase("active"))
                {
                    progress.setMessage("Saving Data");
                    progress.setCancelable(false);
                    progress.show();

                    Map<String, Object> blood_obj = new HashMap<>();
                    blood_obj.put("status", "donated");

                    db.collection("blood").document(user_model.getDonation_id())
                            .update(blood_obj)
                            .addOnSuccessListener(aVoid -> {

                                String uid = userPreferences.getUserId();

                                SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss zzz", Locale.US);
                                String time = df.format(new Date());


                                Map<String, Object> blood_obj2 = new HashMap<>();
                                blood_obj2.put("date", time);
                                DocumentReference documentReference = db.document("/donor/"+uid);
                                blood_obj2.put("donorId", documentReference);
                                blood_obj2.put("donationCategory", "blood");
                                blood_obj2.put("donationId", user_model.getDonation_id());

                                db.collection("receivedDonations").document()
                                        .set(blood_obj2)
                                        .addOnSuccessListener(aVoid2 -> {

                                            progress.dismiss();
                                            Toast.makeText(BloodDetail.this, "Thanks for your donation :)", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            progress.dismiss();
                                            Toast.makeText(BloodDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                progress.dismiss();
                                Toast.makeText(BloodDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                            });
                }

                else if (user_model.getStatus().equalsIgnoreCase("donated"))
                {
                    Toast.makeText(BloodDetail.this, "Thnaks for your donation :)", Toast.LENGTH_SHORT).show();
                }
                else {

                    progress.setMessage("Activating Donation...");
                    progress.setCancelable(false);
                    progress.show();

                    Map<String, Object> blood_obj = new HashMap<>();

                    SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss zzz", Locale.US);
                    String time = df.format(new Date());


                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(df.parse(time));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c.add(Calendar.DATE, 3);
                    String expiry = df.format(c.getTime());
                    blood_obj.put("status", "active");
                    blood_obj.put("date", time);
                    blood_obj.put("expiry", expiry);
                        db.collection("blood").document(user_model.getDonation_id())
                                .update(blood_obj)
                                .addOnSuccessListener(aVoid -> {
                                    progress.dismiss();
                                    Toast.makeText(BloodDetail.this, "Donation is activated successfully.", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    progress.dismiss();
                                    Toast.makeText(BloodDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                                });

                }
            });

            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(BloodDetail.this,DonateBlood.class);
                    intent.putExtra("edit","edit");
                    intent.putExtra("age_cat_new",user_model.getAge());
                    intent.putExtra("last_don_text_new",user_model.getLastDonated());
                    intent.putExtra("illness_cat_new",user_model.getIllness());
                    intent.putExtra("medication_cat_new",user_model.getCurrentMedication());
                    intent.putExtra("vaccination_et_new",user_model.getVaccination());
                    intent.putExtra("city_et_new",user_model.getCity());
                    intent.putExtra("id",user_model.getDonation_id());
                    intent.putExtra("img",user_model.getImg());

                    intent.putExtra("bloodGroup_new",user_model.getBloodGroup());
                    intent.putExtra("city",user_model.getCity());
                    intent.putExtra("blood_trans_new",user_model.getBloodTransfusion());
                    intent.putExtra("smoke_new",user_model.getSmoking());
                    intent.putExtra("province",user_model.getProvince());
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return contactModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView blood_group,gender_txt,age_txt,last_donation,illnes_txt,
                    mediation_txt,vaccination_txt,blood_transformation,donor_name,donor_contact,
                    blood_smoking,province,city,date;
            LinearLayout donor_side,activate_layout;
            Button edit,delete,interest,activate;

            ImageView image;

            public MyViewHolder(View itemView) {
                super(itemView);
                donor_side=itemView.findViewById(R.id.donor_side);
                delete=itemView.findViewById(R.id.delete);
                edit=itemView.findViewById(R.id.edit);
                interest=itemView.findViewById(R.id.interest);
                blood_group=itemView.findViewById(R.id.blood_group);
                gender_txt=itemView.findViewById(R.id.gender_txt);
                age_txt=itemView.findViewById(R.id.age_txt);
                last_donation=itemView.findViewById(R.id.last_donation);
                illnes_txt=itemView.findViewById(R.id.illnes_txt);
                mediation_txt=itemView.findViewById(R.id.mediation_txt);
                vaccination_txt=itemView.findViewById(R.id.vaccination_txt);
                blood_transformation=itemView.findViewById(R.id.blood_transformation);
                donor_contact=itemView.findViewById(R.id.donor_contact);
                donor_name=itemView.findViewById(R.id.donor_name);
                image=itemView.findViewById(R.id.image);
                activate=itemView.findViewById(R.id.activate);
                blood_smoking=itemView.findViewById(R.id.blood_smoking);
                province=itemView.findViewById(R.id.province);
                date=itemView.findViewById(R.id.date);
                city=itemView.findViewById(R.id.city);
                activate_layout=itemView.findViewById(R.id.activate_layout);


            }

            @Override
            public void onClick(View v) {

            }
        }
    }

}