package com.example.ehsaas.UI.Volunteer;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Chat.ChatActivity;
import com.example.ehsaas.UI.Models.FoodRequestModel;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InProgressDeliver extends AppCompatActivity {


    RecyclerView recyclerView;
    ImageView back;
    ProgressDialog progress;
    FirebaseFirestore db;
    TextView delivery_text;
    UserPreferences userPreferences;

    ArrayList<FoodRequestModel> requestsModels=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donationequests);

        back = findViewById(R.id.back_btn);
        userPreferences=new UserPreferences(InProgressDeliver.this);

        back.setOnClickListener(v -> finish());
        recyclerView=findViewById(R.id.recyclerview);
        delivery_text=findViewById(R.id.delivery_text);
        delivery_text.setText("In Progress Deliveries");
        progress = new ProgressDialog(InProgressDeliver.this);
        db = FirebaseFirestore.getInstance();
        getData();

    }


    public void getData()
    {
        progress.setMessage("Getting Data");
        progress.setCancelable(false);
        progress.show();
        db.collection("delivery")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progress.dismiss();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            FoodRequestModel foodModel=new FoodRequestModel();
                            foodModel.setId(document.getId());
                            foodModel.setDonationCategory(String.valueOf(data.get("donationCategory")));
                            foodModel.setDate(String.valueOf(data.get("date")));
                            foodModel.setStatus(String.valueOf(data.get("status")));
                            DocumentReference documentReference;
                            foodModel.setDonationId(String.valueOf(data.get("donationId")));
                            documentReference = (DocumentReference) data.get("donorId");
                            foodModel.setDonorId(String.valueOf(documentReference.getPath()));
                            documentReference = (DocumentReference) data.get("receiverId");
                            foodModel.setReceiverId(String.valueOf(documentReference.getPath()));
                            documentReference = (DocumentReference) data.get("volunteerId");
                            foodModel.setVolunteerId(String.valueOf(documentReference.getPath()));
                            if (!foodModel.getStatus().equalsIgnoreCase("completed"))
                            {
                                if (foodModel.getVolunteerId().contains(userPreferences.getUserId()))
                                {
                                    requestsModels.add(foodModel);
                                }

                            }

                        }

                        usedItemsAdapter groupAdapter = new usedItemsAdapter(requestsModels, InProgressDeliver.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(InProgressDeliver.this);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(groupAdapter);


                    } else {
                        progress.dismiss();
                    }
                });


    }


    public class usedItemsAdapter extends RecyclerView.Adapter<usedItemsAdapter.MyViewHolder> {
        ArrayList<FoodRequestModel> contactModelList;
        Context context;


        public usedItemsAdapter(ArrayList<FoodRequestModel> contactModelList, Context context) {
            this.contactModelList = contactModelList;
            this.context = context;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_view, parent, false);
            return new MyViewHolder(item);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            FoodRequestModel user_model=contactModelList.get(position);

            // Receiver Details

            String donor= null;
            try {
                String[] split = user_model.getReceiverId().split("/");
                donor = split[1];
            } catch (Exception e) {
                e.printStackTrace();
            }
            FirebaseFirestore db;
            db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("receiver").document(donor);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Map<String, Object> data = document.getData();
                            String name_new = String.valueOf(data.get("name"));
                            String contactNo = String.valueOf(data.get("contactNo"));

                            holder.receiver_name.setText(Html.fromHtml("<b>Receiver Name:</b> "+name_new));
                            holder.recever_contact.setText(Html.fromHtml("<b>Receiver Contact:</b> "+contactNo));

                        }
                        else {
                            holder.receiver_name.setText(Html.fromHtml("<b>Receiver Name:</b> "));
                            holder.recever_contact.setText(Html.fromHtml("<b>Receiver Contact:</b> "));
                        }
                    } else {
                        holder.receiver_name.setText(Html.fromHtml("<b>Receiver Name:</b> "));
                        holder.recever_contact.setText(Html.fromHtml("<b>Receiver Contact:</b> "));

                    }
                }
            });



            //Donor Details

            String donor2= null;
            try {
                String[] split = user_model.getDonorId().split("/");
                donor2 = split[1];
            } catch (Exception e) {
                e.printStackTrace();
            }

            docRef = db.collection("donor").document(donor2);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Map<String, Object> data = document.getData();
                            String name_new = String.valueOf(data.get("name"));
                            String contactNo = String.valueOf(data.get("contactNo"));

                            holder.donor_name.setText(Html.fromHtml("<b>Donor Name:</b> "+name_new));
                            holder.donor_contact.setText(Html.fromHtml("<b>Donor Contact:</b> "+contactNo));

                        }
                        else {
                            holder.receiver_name.setText(Html.fromHtml("<b>Receiver Name:</b> "));
                            holder.recever_contact.setText(Html.fromHtml("<b>Receiver Contact:</b> "));
                        }
                    } else {
                        holder.receiver_name.setText(Html.fromHtml("<b>Receiver Name:</b> "));
                        holder.recever_contact.setText(Html.fromHtml("<b>Receiver Contact:</b> "));

                    }
                }
            });

            // Donation Details
            String donation= null;
            try {
                String[] split = user_model.getDonationId().split("/");
                donation = user_model.getDonationId();
            } catch (Exception e) {
                e.printStackTrace();
            }
            DocumentReference docRefDonation;
            if (user_model.getDonationCategory().equalsIgnoreCase("food"))
            {
                docRefDonation = db.collection("food").document(donation);
            }
            else if (user_model.getDonationCategory().equalsIgnoreCase("blood"))
            {
                docRefDonation = db.collection("blood").document(donation);
            }
            else {
                docRefDonation = db.collection("usedItems").document(donation);
            }

            docRefDonation.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Map<String, Object> data = document.getData();
                            String name_new = String.valueOf(data.get("name"));
                            String quantity = String.valueOf(data.get("quantity"));
                            String streetAddress = String.valueOf(data.get("streetAddress"));
                            String city = String.valueOf(data.get("city"));
                            String category = String.valueOf(data.get("category"));

                            holder.quantity.setVisibility(View.GONE);

                            holder.street_address.setVisibility(View.GONE);

                            holder.city.setVisibility(View.GONE);
                            holder.category.setText(Html.fromHtml("<b>Category:</b> "+category));





                            String bloodGroup = String.valueOf(data.get("bloodGroup"));

                            if (user_model.getDonationCategory().equalsIgnoreCase("blood"))
                            {
                                holder.donation_name.setText(Html.fromHtml("<b>Blood Group:</b> "+bloodGroup));
                            }
                            else {
                                holder.donation_name.setText(Html.fromHtml("<b>Donation Name:</b> "+name_new));
                            }


                        }
                        else {
                            holder.donation_name.setText(Html.fromHtml("<b>Donation Name:</b> "));

                        }
                    }
                    else {
                        holder.donation_name.setText(Html.fromHtml("<b>Donation Name:</b> "));
                    }
                }
            });



            String date=user_model.getDate();
            try {
                String[] s = date.split(" ");
                holder.request_made.setText(Html.fromHtml("<b>Date:</b> "+s[0]+" "+s[1]+" "+s[2]+" "+s[3]));
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.accept_btn.setText("Delivered");

            holder.accept_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progress.setMessage("Please wait...");
                    progress.setCancelable(false);
                    progress.show();

                    String uid = userPreferences.getUserId();

                    SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss zzz", Locale.US);
                    String time = df.format(new Date());


                    Map<String, Object> blood_obj = new HashMap<>();
                    blood_obj.put("status", "completed");


                    db.collection("delivery").document(user_model.getId())
                            .update(blood_obj)
                            .addOnSuccessListener(aVoid -> {

                                Map<String, Object> user = new HashMap<>();
                                String lname = userPreferences.getLname();

                                int i = Integer.parseInt(lname) + 1;
                                user.put("deliveryCount",String.valueOf(i));


                                FirebaseFirestore db;
                                db = FirebaseFirestore.getInstance();
                                db.collection("volunteer").document(userPreferences.getUserId())
                                        .update(user)
                                        .addOnSuccessListener(aVoid2 -> {

                                            progress.dismiss();
                                            Toast.makeText(InProgressDeliver.this, "Thanks for Deliver the Donation :)", Toast.LENGTH_SHORT).show();
                                            finish();


                                        })


                                        .addOnFailureListener(e -> {
                                            progress.dismiss();
                                            Toast.makeText(InProgressDeliver.this, e.toString(), Toast.LENGTH_SHORT).show();
                                        });


                            })
                            .addOnFailureListener(e -> {
                                progress.dismiss();
                                Toast.makeText(InProgressDeliver.this, e.toString(), Toast.LENGTH_SHORT).show();
                            });
                }
            });


        }

        @Override
        public int getItemCount() {
            return contactModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            Button accept_btn;

            TextView receiver_name,recever_contact,request_made,donation_name,quantity,
                    street_address,city,category,donor_name,donor_contact;


            public MyViewHolder(View itemView) {
                super(itemView);
                donation_name=itemView.findViewById(R.id.donation_name);
                receiver_name=itemView.findViewById(R.id.receiver_name);
                recever_contact=itemView.findViewById(R.id.recever_contact);
                request_made=itemView.findViewById(R.id.request_made);
                quantity=itemView.findViewById(R.id.quantity);
                accept_btn=itemView.findViewById(R.id.accept_btn);

                street_address=itemView.findViewById(R.id.street_address);
                city=itemView.findViewById(R.id.city);
                category=itemView.findViewById(R.id.category);
                donor_name=itemView.findViewById(R.id.donor_name);
                donor_contact=itemView.findViewById(R.id.donor_contact);

            }

            @Override
            public void onClick(View v) {

            }
        }
    }
}