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
import com.example.ehsaas.UI.Chat.InboxActivity;
import com.example.ehsaas.UI.Food.Receiver.FoodDetail;
import com.example.ehsaas.UI.Models.FoodRequestModel;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.Requests.GeneralDonationRequests;
import com.example.ehsaas.UI.Requests.MyDonationRequests;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class SelectToDeliver extends AppCompatActivity {


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
        userPreferences=new UserPreferences(SelectToDeliver.this);

        back.setOnClickListener(v -> finish());
        recyclerView=findViewById(R.id.recyclerview);
        delivery_text=findViewById(R.id.delivery_text);
        delivery_text.setText("Donations To Be Delivered");
        progress = new ProgressDialog(SelectToDeliver.this);
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
                            foodModel.setDonationCategory(String.valueOf(data.get("donationCategory")));
                            foodModel.setVia(String.valueOf(data.get("via")));
                            foodModel.setDate(String.valueOf(data.get("date")));
                            foodModel.setId(document.getId());
                            foodModel.setStatus(String.valueOf(data.get("status")));
                            DocumentReference documentReference;
                            foodModel.setDonationId(String.valueOf(data.get("donationId")));
                            documentReference = (DocumentReference) data.get("donorId");
                            foodModel.setDonorId(String.valueOf(documentReference.getPath()));
                            documentReference = (DocumentReference) data.get("receiverId");
                            foodModel.setReceiverId(String.valueOf(documentReference.getPath()));
                            if (foodModel.getVia().equalsIgnoreCase("delivery") && foodModel.getStatus().equalsIgnoreCase("accepted"))
                            {
                                requestsModels.add(foodModel);
                            }

                        }

                        usedItemsAdapter groupAdapter = new usedItemsAdapter(requestsModels, SelectToDeliver.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SelectToDeliver.this);
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

            String receiver= null;
            final String[] name_new_receiver = new String[1];
            final String[] contactNo_receiver = new String[1];
            final String[] img_receiver = new String[1];
            final String[] email_receiver = new String[1];
            try {
                String[] split = user_model.getReceiverId().split("/");
                receiver = split[1];
            } catch (Exception e) {
                e.printStackTrace();
            }
            FirebaseFirestore db;
            db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("receiver").document(receiver);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Map<String, Object> data = document.getData();

                            name_new_receiver[0] = String.valueOf(data.get("name"));
                            img_receiver[0] = String.valueOf(data.get("img"));
                            email_receiver[0] = String.valueOf(data.get("email"));

                            contactNo_receiver[0] = String.valueOf(data.get("contactNo"));

                            holder.receiver_name.setText(Html.fromHtml("<b>Receiver Name:</b> "+name_new_receiver[0]));
                            holder.recever_contact.setText(Html.fromHtml("<b>Receiver Contact:</b> "+contactNo_receiver[0]));

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
            final String[] name_new = new String[1];
            final String[] contactNo = new String[1];
            final String[] img = new String[1];
            final String[] email = new String[1];
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

                            name_new[0] = String.valueOf(data.get("name"));

                            contactNo[0] = String.valueOf(data.get("contactNo"));
                            email[0] = String.valueOf(data.get("email"));

                            img[0] = String.valueOf(data.get("img"));


                            holder.donor_name.setText(Html.fromHtml("<b>Donor Name:</b> "+name_new[0]));
                            holder.donor_contact.setText(Html.fromHtml("<b>Donor Contact:</b> "+contactNo[0]));

                        }
                        else {
                            holder.donor_name.setText(Html.fromHtml("<b>Donor Name:</b> "));
                            holder.donor_contact.setText(Html.fromHtml("<b>Donor Contact:</b> "));
                        }
                    } else {
                        holder.donor_name.setText(Html.fromHtml("<b>Donor Name:</b> "));
                        holder.donor_contact.setText(Html.fromHtml("<b>Donor Contact:</b> "));

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

                            holder.quantity.setText(Html.fromHtml("<b>Quantity:</b> "+quantity));

                            holder.street_address.setText(Html.fromHtml("<b>Street Address:</b> "+streetAddress));

                            holder.city.setText(Html.fromHtml("<b>City:</b> "+city));
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
                holder.request_made.setText(Html.fromHtml("<b>Request Made:</b> "+s[0]+" "+s[1]+" "+s[2]+" "+s[3]));
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.accept_btn.setText("Deliver");

            String finalDonor = donor2;
            String finalReceiver = receiver;
            holder.accept_btn.setOnClickListener(v -> {
                progress.setMessage("Sending Notifications to Donor and Receiver...");
                progress.setCancelable(false);
                progress.show();

                Map<String, Object> blood_obj2 = new HashMap<>();

                blood_obj2.put("status", "completed");

                db.collection("interested").document(user_model.getId())
                        .update(blood_obj2)
                        .addOnSuccessListener(aVoid4 -> {
                            String uid = userPreferences.getUserId();

                            SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss zzz", Locale.US);
                            String time = df.format(new Date());


                            Map<String, Object> blood_obj = new HashMap<>();
                            blood_obj.put("date", time);
                            DocumentReference documentReference;
                            documentReference = db.document(user_model.getReceiverId());
                            blood_obj.put("receiverId", documentReference);
                            documentReference = db.document(user_model.getDonorId());
                            blood_obj.put("donorId", documentReference);
                            blood_obj.put("donationId", user_model.getDonationId());

                            documentReference = db.document( "/volunteer/"+uid);
                            blood_obj.put("volunteerId",documentReference);
                            blood_obj.put("status", "in progress");
                            blood_obj.put("donationCategory", user_model.getDonationCategory());


                            db.collection("delivery").document()
                                    .set(blood_obj)
                                    .addOnSuccessListener(aVoid -> {
                                        sendNotification(finalDonor,name_new[0],userPreferences.getFname()+" has accepted your donation to deliver",contactNo[0],img[0],email[0]
                                                ,finalReceiver,name_new_receiver[0],contactNo_receiver[0],img_receiver[0],email_receiver[0]);

                                    })
                                    .addOnFailureListener(e -> {
                                        progress.dismiss();
                                        Toast.makeText(SelectToDeliver.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            progress.dismiss();
                            Toast.makeText(SelectToDeliver.this, e.toString(), Toast.LENGTH_SHORT).show();
                        });


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



    private void sendNotification(final String messageReceiverID, final String messageReceiverName,
                                  final String messageText, String contactNo, String img, String email, String finalReceiver, String s, String s1, String s2, String s3) {
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
                                        //  Toast.makeText(ChatActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();

                                        sendNotificationReceiver(messageReceiverID,messageReceiverName,userPreferences.getFname()+" has accepted your donation to deliver",contactNo,img,email
                                                ,finalReceiver,s,s1,s2,s3);



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

    private void sendNotificationReceiver(final String messageReceiverID, final String messageReceiverName,
                                          final String messageText, String contactNo, String img, String email, String finalReceiver, String s, String s1, String s2, String s3) {
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

                                        createContact(finalReceiver,s3,messageReceiverID,messageReceiverName,contactNo,img,email);


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

    public void createContact(String messageReceiverID, String receiverEmail, String receiverID, String messageReceiverName, String contactNo, String img, String email)
    {
        UserPreferences userPreferences=new UserPreferences(SelectToDeliver.this);

        DatabaseReference ContactsRef;
        ContactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts");

        ContactsRef.child(userPreferences.getUserId()).child(messageReceiverID)
                .child("Contacts").setValue(receiverEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            ContactsRef.child(messageReceiverID).child(userPreferences.getUserId())
                                    .child("Contacts").setValue(userPreferences.getUseremail())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                progress.dismiss();
                                                //  Toast.makeText(ChatActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                                                Intent chatIntent=new Intent(SelectToDeliver.this, ChatActivity.class);
                                                chatIntent.putExtra("visit_user_id", receiverID);
                                                chatIntent.putExtra("visit_user_name",messageReceiverName);
                                                chatIntent.putExtra("user_contact", contactNo);
                                                chatIntent.putExtra("visit_image", img);
                                                chatIntent.putExtra("email", email);
                                                startActivity(chatIntent);
                                                finish();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

}