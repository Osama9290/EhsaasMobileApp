package com.example.ehsaas.UI.Food.Receiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.UsedItems.Donor.DonateUsedItem;
import com.example.ehsaas.UI.Activities.congrat;
import com.example.ehsaas.UI.Blood.Donor.DonateBlood;
import com.example.ehsaas.UI.Blood.Receiver.BloodDetail;
import com.example.ehsaas.UI.Chat.ChatActivity;
import com.example.ehsaas.UI.Food.Donor.DonateFood;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.Volunteer.SelectToDeliver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FoodDetail extends AppCompatActivity {

    ImageView image;
    TextView name_st,quantity_st,location_st,province_st,city_st,date_st,donor_name,
            conatct_vo,category_tv,tv_description,tv_size,tv_brand,tv_author,tv_edition;

    LinearLayout receiver_side,donor_side,activate_layout;

    Button pickup,delivery,edit,delete,activate;
    UserPreferences userPreferences;
    ProgressDialog progress;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_food_detail);

        ImageView back = findViewById(R.id.back_btn);

        userPreferences=new UserPreferences(FoodDetail.this);
        pickup=findViewById(R.id.pickup);
        delivery=findViewById(R.id.delivery);
        edit=findViewById(R.id.edit);
        delete=findViewById(R.id.delete);
        image=findViewById(R.id.image);
        name_st=findViewById(R.id.name_st);
        quantity_st=findViewById(R.id.quantity_st);
        location_st=findViewById(R.id.location_st);
        province_st=findViewById(R.id.province);
        city_st=findViewById(R.id.city);
        date_st=findViewById(R.id.date);
        donor_name=findViewById(R.id.donor_name);
        conatct_vo=findViewById(R.id.conatct_vo);
        category_tv=findViewById(R.id.category_tv);
        tv_description=findViewById(R.id.tv_description);
        tv_size=findViewById(R.id.tv_size);
        tv_brand=findViewById(R.id.tv_brand);
        tv_author=findViewById(R.id.tv_author);
        tv_edition=findViewById(R.id.tv_edition);
        donor_side=findViewById(R.id.donor_side);
        receiver_side=findViewById(R.id.receiver_side);
        progress = new ProgressDialog(this);
        activate_layout=findViewById(R.id.activate_layout);
        activate=findViewById(R.id.activate);



        String type = getIntent().getStringExtra("type");


        String name = getIntent().getStringExtra("name");
        String id = getIntent().getStringExtra("id");
        String img = getIntent().getStringExtra("img");
        String location = getIntent().getStringExtra("location");
        String quantity = getIntent().getStringExtra("Quantity");
        String status = getIntent().getStringExtra("status");

        String province = getIntent().getStringExtra("province");
        String city = getIntent().getStringExtra("city");
        String date = getIntent().getStringExtra("date");
        String donorId = getIntent().getStringExtra("donorId");
        String category = getIntent().getStringExtra("category");


        String description = getIntent().getStringExtra("description");
        String size = getIntent().getStringExtra("size");
        String brand = getIntent().getStringExtra("brand");
        String author = getIntent().getStringExtra("author");
        String edition = getIntent().getStringExtra("edition");
        String rating = getIntent().getStringExtra("rating");



        String from = getIntent().getStringExtra("from");

        if (type == null)
        {
            donor_side.setVisibility(View.VISIBLE);
            receiver_side.setVisibility(View.GONE);
            activate_layout.setVisibility(View.VISIBLE);

            if (status.equalsIgnoreCase("active"))
            {
                activate.setText("Donated");
                donor_side.setVisibility(View.VISIBLE);
            }
            else {
                activate.setText("Activate");
                donor_side.setVisibility(View.GONE);
            }


            if (status.equalsIgnoreCase("donated"))
            {
                activate.setText("Thanks for your donation :)");
                donor_side.setVisibility(View.GONE);
            }
        }
        else
        {
            receiver_side.setVisibility(View.VISIBLE);
            donor_side.setVisibility(View.GONE);
            activate_layout.setVisibility(View.GONE);

        }

        if (description != null)
        {
            if (!description.equals("null"))
            {
                tv_description.setText(Html.fromHtml("<b>Description:</b> "+description));
            }
            else {
                tv_description.setVisibility(View.GONE);
            }
        }
        else {
            tv_description.setVisibility(View.GONE);
        }


        if (size != null)
        {
            if (!size.equals("null"))
            {
                tv_size.setText(Html.fromHtml("<b>Size:</b> "+size));
            }
            else {
                tv_size.setVisibility(View.GONE);
            }

        }
        else {
            tv_size.setVisibility(View.GONE);
        }

        if (brand != null)
        {
            if (!brand.equals("null"))
            {
                tv_brand.setText(Html.fromHtml("<b>Brand:</b> "+brand));
            }
            else {
                tv_brand.setVisibility(View.GONE);
            }
        }
        else {
            tv_brand.setVisibility(View.GONE);
        }



        if (author != null)
        {
            if (!author.equals("null"))
            {
                tv_author.setText(Html.fromHtml("<b>Author:</b> "+author));
            }
            else {
                tv_author.setVisibility(View.GONE);
            }
        }
        else {
            tv_author.setVisibility(View.GONE);
        }


        if (edition != null)
        {
            if (!edition.equals("null"))
            {
                tv_edition.setText(Html.fromHtml("<b>Edition:</b> "+edition));
            }
            else {
                tv_edition.setVisibility(View.GONE);
            }
        }
        else {
            tv_edition.setVisibility(View.GONE);
        }



        name_st.setText(Html.fromHtml("<b>Name:</b> "+name));
        location_st.setText(Html.fromHtml("<b>Street Address:</b> "+location));
        quantity_st.setText(Html.fromHtml("<b>Quantity Name:</b> "+quantity));
        province_st.setText(Html.fromHtml("<b>Province:</b> "+province));
        city_st.setText(Html.fromHtml("<b>City:</b> "+city));
        category_tv.setText(Html.fromHtml("<b>Category:</b> "+category));
        try {
            String[] s = date.split(" ");

            date_st.setText(Html.fromHtml("<b>Date:</b> "+s[0]+" "+s[1]+" "+s[2]+" "+s[3]));
        } catch (Exception e) {
            e.printStackTrace();
        }

        donor_name.setText(Html.fromHtml("<b>Name:</b> "));
        conatct_vo.setText(Html.fromHtml("<b>Contact No:</b> "));
        String donor= null;
        try {
            String[] split = donorId.split("/");
            donor = split[1];
        } catch (Exception e) {
            e.printStackTrace();
        }

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("donor").document(donor);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Map<String, Object> data = document.getData();
                    String name_new = String.valueOf(data.get("name"));
                    String contactNo = String.valueOf(data.get("contactNo"));

                    donor_name.setText(Html.fromHtml("<b>Name:</b> "+name_new));
                    conatct_vo.setText(Html.fromHtml("<b>Contact No:</b> "+contactNo));


                } else {
                    donor_name.setText(Html.fromHtml("<b>Name:</b> "));
                    conatct_vo.setText(Html.fromHtml("<b>Contact No:</b> "));
                }
            } else {
                donor_name.setText(Html.fromHtml("<b>Name:</b> "));
                conatct_vo.setText(Html.fromHtml("<b>Contact No:</b> "));

            }
        });


        Glide.with(FoodDetail.this)
                .load(img)
                .placeholder(R.drawable.food)
                .into(image);


        back.setOnClickListener(v -> onBackPressed());


        pickup.setOnClickListener(v -> {
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
            documentReference = db.document(donorId);
            blood_obj.put("donorId", documentReference);
            blood_obj.put("donationId", id);
            blood_obj.put("status", "in progress");
            if (category.equalsIgnoreCase("cooked") || category.equalsIgnoreCase("uncooked"))
            {
                blood_obj.put("donationCategory", "food");
            }
            else{
                blood_obj.put("donationCategory", "used items");
            }


            blood_obj.put("via", "pickup");


            db.collection("interested").document()
                    .set(blood_obj)
                    .addOnSuccessListener(aVoid -> {
                        progress.dismiss();
                        Toast.makeText(this, "Successfully Requested for Pickup Item", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progress.dismiss();
                        Toast.makeText(FoodDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                    });

        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progress.setMessage("Removing...");
                progress.setCancelable(false);
                progress.show();

                Map<String, Object> blood_obj = new HashMap<>();
                blood_obj.put("status", "inactive");
                if (category.equalsIgnoreCase("cooked") || category.equalsIgnoreCase("uncooked"))
                {

                    db.collection("food").document(id)
                            .update(blood_obj)
                            .addOnSuccessListener(aVoid -> {
                                progress.dismiss();
                                Toast.makeText(FoodDetail.this, "Donation is removed successfully.", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progress.dismiss();
                                Toast.makeText(FoodDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                            });
                }
                else {
                    db.collection("usedItems").document(id)
                            .update(blood_obj)
                            .addOnSuccessListener(aVoid -> {
                                progress.dismiss();
                                Toast.makeText(FoodDetail.this, "Donation is removed successfully.", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progress.dismiss();
                                Toast.makeText(FoodDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                            });
                }

            }
        });


        delivery.setOnClickListener(v -> {
            progress.setMessage("Requesting for delivery...");
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
            documentReference = db.document(donorId);
            blood_obj.put("donorId", documentReference);
            blood_obj.put("donationId", id);
            blood_obj.put("status", "in progress");
            if (category.equalsIgnoreCase("cooked") || category.equalsIgnoreCase("uncooked"))
            {
                blood_obj.put("donationCategory", "food");
            }
            else{
                blood_obj.put("donationCategory", "used items");
            }
            blood_obj.put("via", "delivery");


            db.collection("interested").document()
                    .set(blood_obj)
                    .addOnSuccessListener(aVoid -> {
                        progress.dismiss();
                        Toast.makeText(this, "Successfully Requested for Deliver Item", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progress.dismiss();
                        Toast.makeText(FoodDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                    });

        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (category.equalsIgnoreCase("cooked") || category.equalsIgnoreCase("uncooked"))
                {
                    intent = new Intent(FoodDetail.this, DonateFood.class);
                    intent.putExtra("edit","edit");
                    intent.putExtra("name",name);
                    intent.putExtra("img",img);
                    intent.putExtra("location",location);
                    intent.putExtra("Quantity",quantity);
                    intent.putExtra("province",province);
                    intent.putExtra("city",city);
                    intent.putExtra("date",date);
                    intent.putExtra("donorId",donorId);
                    intent.putExtra("category",category);
                    intent.putExtra("id",id);
                    intent.putExtra("status",status);
                    startActivity(intent);

                }

                else {
                    intent = new Intent(FoodDetail.this, DonateUsedItem.class);
                    intent.putExtra("edit","edit");
                    intent.putExtra("name",name);
                    intent.putExtra("img",img);
                    intent.putExtra("location",location);
                    intent.putExtra("Quantity",quantity);
                    intent.putExtra("province",province);
                    intent.putExtra("city",city);
                    intent.putExtra("date",date);
                    intent.putExtra("donorId",donorId);
                    intent.putExtra("category",category);
                    intent.putExtra("id",id);
                    intent.putExtra("description",description);
                    intent.putExtra("size",size);
                    intent.putExtra("brand",brand);
                    intent.putExtra("author",author);
                    intent.putExtra("edition",edition);
                    intent.putExtra("status",status);
                    intent.putExtra("rating",rating);
                    intent.putExtra("type",category);
                    startActivity(intent);
                }
                }

        });


        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equalsIgnoreCase("active"))
                {
                        progress.setMessage("Saving Data");
                        progress.setCancelable(false);
                        progress.show();

                        Map<String, Object> blood_obj = new HashMap<>();
                        blood_obj.put("status", "donated");
                        String table="";
                        if (category.equalsIgnoreCase("cooked") || category.equalsIgnoreCase("uncooked"))
                        {
                            table="food";
                        }
                        else{
                            table="usedItems";
                        }


                        String finalTable = table;
                        db.collection(table).document(id)
                                .update(blood_obj)
                                .addOnSuccessListener(aVoid -> {

                                    String uid = userPreferences.getUserId();

                                    SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss zzz", Locale.US);
                                    String time = df.format(new Date());


                                    Map<String, Object> blood_obj2 = new HashMap<>();
                                    blood_obj2.put("date", time);
                                    DocumentReference documentReference = db.document("/donor/"+uid);
                                    blood_obj2.put("donorId", documentReference);
                                    blood_obj2.put("donationCategory", finalTable);
                                    blood_obj2.put("donationId", id);

                                    db.collection("receivedDonations").document()
                                            .set(blood_obj2)
                                            .addOnSuccessListener(aVoid2 -> {

                                                progress.dismiss();
                                                Toast.makeText(FoodDetail.this, "Thanks for your donation :)", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                progress.dismiss();
                                                Toast.makeText(FoodDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    progress.dismiss();
                                    Toast.makeText(FoodDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                                });
                }

                else if (status.equalsIgnoreCase("donated"))
                {
                    Toast.makeText(FoodDetail.this, "Thnaks for your donation :)", Toast.LENGTH_SHORT).show();
                }
                else {

                    progress.setMessage("Activating Donation..");
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
                    if (category.equalsIgnoreCase("cooked") || category.equalsIgnoreCase("uncooked"))
                    {

                        db.collection("food").document(id)
                                .update(blood_obj)
                                .addOnSuccessListener(aVoid -> {
                                    progress.dismiss();
                                    Toast.makeText(FoodDetail.this, "Donation is activated successfully.", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    progress.dismiss();
                                    Toast.makeText(FoodDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                                });
                    }
                    else {
                        db.collection("usedItems").document(id)
                                .update(blood_obj)
                                .addOnSuccessListener(aVoid -> {
                                    progress.dismiss();
                                    Toast.makeText(FoodDetail.this, "Donation is activated successfully.", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    progress.dismiss();
                                    Toast.makeText(FoodDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                                });
                    }
                }
            }
        });

        if (from != null)
        {
            activate_layout.setVisibility(View.GONE);
            donor_side.setVisibility(View.GONE);
            receiver_side.setVisibility(View.GONE);
        }



    }


}