package com.example.ehsaas.UI.Requests;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ehsaas.R;
import com.example.ehsaas.UI.Chat.ChatActivity;
import com.example.ehsaas.UI.Food.Receiver.AllFoodAndUsedItems;
import com.example.ehsaas.UI.Food.Receiver.FoodDetail;
import com.example.ehsaas.UI.Models.FoodModel;
import com.example.ehsaas.UI.Models.FoodRequestModel;
import com.example.ehsaas.UI.Models.UsedItemsModel;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.notification.APIService;
import com.example.ehsaas.UI.notification.Client;
import com.example.ehsaas.UI.notification.Data;
import com.example.ehsaas.UI.notification.Response;
import com.example.ehsaas.UI.notification.Sender;
import com.example.ehsaas.UI.notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class MyDonationRequests extends AppCompatActivity {


    RecyclerView recyclerView;
    ImageView back;
    ProgressDialog progress;
    FirebaseFirestore db;
    UserPreferences userPreferences;

    ArrayList<FoodRequestModel> requestsModels=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donationequests);

        back = findViewById(R.id.back_btn);
        userPreferences=new UserPreferences(MyDonationRequests.this);

        back.setOnClickListener(v -> finish());
        recyclerView=findViewById(R.id.recyclerview);
        progress = new ProgressDialog(MyDonationRequests.this);
        db = FirebaseFirestore.getInstance();
        getData();

    }


    public void getData()
    {
        progress.setMessage("Getting Data");
        progress.setCancelable(false);
        progress.show();
        db.collection("interested")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progress.dismiss();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            FoodRequestModel foodModel=new FoodRequestModel();
                            foodModel.setId(document.getId());
                            foodModel.setDonationCategory(String.valueOf(data.get("donationCategory")));
                            foodModel.setStatus(String.valueOf(data.get("status")));
                            foodModel.setVia(String.valueOf(data.get("via")));
                            foodModel.setDate(String.valueOf(data.get("date")));
                            DocumentReference documentReference;
                            foodModel.setDonationId(String.valueOf(data.get("donationId")));
                            documentReference = (DocumentReference) data.get("donorId");
                            foodModel.setDonorId(String.valueOf(documentReference.getPath()));
                            documentReference = (DocumentReference) data.get("receiverId");
                            foodModel.setReceiverId(String.valueOf(documentReference.getPath()));
                            if (foodModel.getDonorId().contains(userPreferences.getUserId()) && !foodModel.getStatus().equalsIgnoreCase("completed")
                                    && !foodModel.getStatus().equalsIgnoreCase("declined"))
                            {
                                requestsModels.add(foodModel);
                            }

                        }

                        usedItemsAdapter groupAdapter = new usedItemsAdapter(requestsModels, MyDonationRequests.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyDonationRequests.this);
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
        public usedItemsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_requests_view, parent, false);
            return new usedItemsAdapter.MyViewHolder(item);
        }

        @Override
        public void onBindViewHolder(usedItemsAdapter.MyViewHolder holder, int position) {
            FoodRequestModel user_model=contactModelList.get(position);

            // Receiver Details
            final String[] name_new = new String[1];
            final String[] contactNo = new String[1];
            final String[] img = new String[1];
            final String[] email = new String[1];
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

                            name_new[0] = String.valueOf(data.get("name"));

                            contactNo[0] = String.valueOf(data.get("contactNo"));
                            email[0] = String.valueOf(data.get("email"));

                            img[0] = String.valueOf(data.get("img"));

                            holder.receiver_name.setText(Html.fromHtml("<b>Receiver Name:</b> "+ name_new[0]));
                            holder.recever_contact.setText(Html.fromHtml("<b>Receiver Contact:</b> "+ contactNo[0]));

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


            holder.via_text.setText(Html.fromHtml("<b>Via:</b> "+user_model.getVia()));

            String date=user_model.getDate();
            try {
                String[] s = date.split(" ");
                holder.date.setText(Html.fromHtml("<b>Date:</b> "+s[0]+" "+s[1]+" "+s[2]+" "+s[3]));
            } catch (Exception e) {
                e.printStackTrace();
            }


            String finalDonor = donor;
            String finalDonation = donation;
            holder.accept_btn.setOnClickListener(v -> {

                progress.setMessage("Please wait..");
                progress.setCancelable(false);
                progress.show();

                Map<String, Object> blood_obj = new HashMap<>();

                blood_obj.put("status", "accepted");

                db.collection("interested").document(user_model.getId())
                        .update(blood_obj)
                        .addOnSuccessListener(aVoid -> {
                            sendNotification(finalDonor,name_new[0],userPreferences.getFname()+" has accepted your donation request",contactNo[0],img[0],email[0]);

                        })
                        .addOnFailureListener(e -> {
                            progress.dismiss();
                            Toast.makeText(MyDonationRequests.this, e.toString(), Toast.LENGTH_SHORT).show();
                        });


            });

            holder.reject_btn.setOnClickListener(v -> {
                progress.setMessage("Please wait..");
                progress.setCancelable(false);
                progress.show();
                Map<String, Object> blood_obj = new HashMap<>();

                blood_obj.put("status", "declined");

                db.collection("interested").document(user_model.getId())
                        .update(blood_obj)
                        .addOnSuccessListener(aVoid -> {
                            sendNotificationDelete(finalDonor,name_new[0],userPreferences.getFname()+" has rejected your donation request",contactNo[0],img[0],email[0]);


                        })
                        .addOnFailureListener(e -> {
                            progress.dismiss();
                            Toast.makeText(MyDonationRequests.this, e.toString(), Toast.LENGTH_SHORT).show();
                        });
            });



        }

        @Override
        public int getItemCount() {
            return contactModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView donation_name,receiver_name,recever_contact,via_text,date;
            Button accept_btn,reject_btn;

            public MyViewHolder(View itemView) {
                super(itemView);
                donation_name=itemView.findViewById(R.id.donation_name);
                receiver_name=itemView.findViewById(R.id.receiver_name);
                recever_contact=itemView.findViewById(R.id.recever_contact);
                via_text=itemView.findViewById(R.id.via_text);
                date=itemView.findViewById(R.id.date);
                reject_btn=itemView.findViewById(R.id.reject_btn);
                accept_btn=itemView.findViewById(R.id.accept_btn);

            }

            @Override
            public void onClick(View v) {

            }
        }
    }


    private void sendNotification(final String messageReceiverID, final String messageReceiverName,
                                  final String messageText,String contactNo,String img,String email) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(messageReceiverID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(userPreferences.getUserId(), messageReceiverName + ":" + messageText, "Donation Request Accepted", messageReceiverID, R.drawable.dp);

                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());
                    APIService apiService;

                    //create api service

                    apiService = Client.getRetrofit("https://fcm.googleapis.com/").

                            create(APIService.class);
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    if (response.code() == 200) {
                                        progress.dismiss();
                                        //  Toast.makeText(ChatActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                                        Intent chatIntent=new Intent(MyDonationRequests.this, ChatActivity.class);
                                        chatIntent.putExtra("visit_user_id", messageReceiverID);
                                        chatIntent.putExtra("visit_user_name",messageReceiverName);
                                        chatIntent.putExtra("user_contact", contactNo);
                                        chatIntent.putExtra("visit_image", img);
                                        chatIntent.putExtra("email", email);
                                        startActivity(chatIntent);
                                        finish();

                                    }
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendNotificationDelete(final String messageReceiverID, final String messageReceiverName,
                                  final String messageText,String contactNo,String img,String email) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(messageReceiverID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(userPreferences.getUserId(), messageReceiverName + ":" + messageText, "Donation Request Rejected", messageReceiverID, R.drawable.dp);

                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());
                    APIService apiService;

                    //create api service

                    apiService = Client.getRetrofit("https://fcm.googleapis.com/").

                            create(APIService.class);
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    if (response.code() == 200) {
                                        progress.dismiss();
                                        //  Toast.makeText(ChatActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                                        Toast.makeText(MyDonationRequests.this, "Successfully Rejected..", Toast.LENGTH_SHORT).show();
                                        finish();

                                    }
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}