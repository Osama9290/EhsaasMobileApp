package com.example.ehsaas.UI.Requests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.example.ehsaas.R;
import com.example.ehsaas.UI.Chat.ChatActivity;
import com.example.ehsaas.UI.Models.FoodRequestModel;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class GeneralDonationRequests extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView back;
    ProgressDialog progress;
    FirebaseFirestore db;
    TextView blood, food, usedItems;
    UserPreferences userPreferences;

    ArrayList<FoodRequestModel> requestsModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_donation_requests);

        back = findViewById(R.id.back_btn);
        usedItems = findViewById(R.id.usedItems);
        food = findViewById(R.id.food);
        blood = findViewById(R.id.blood);
        userPreferences=new UserPreferences(GeneralDonationRequests.this);

        back.setOnClickListener(v -> finish());
        recyclerView = findViewById(R.id.recyclerview);
        progress = new ProgressDialog(GeneralDonationRequests.this);
        db = FirebaseFirestore.getInstance();

        getDataBlood();

        blood.setOnClickListener(v -> {

            findViewById(R.id.blood_view).setVisibility(View.VISIBLE);
            findViewById(R.id.food_view).setVisibility(View.GONE);
            findViewById(R.id.usedItems_view).setVisibility(View.GONE);

            blood.setTextColor(Color.parseColor("#2BB8C1"));
            food.setTextColor(Color.parseColor("#BAC1C1"));
            usedItems.setTextColor(Color.parseColor("#BAC1C1"));

            requestsModels = new ArrayList<>();
            getDataBlood();
        });

        food.setOnClickListener(v -> {

            findViewById(R.id.blood_view).setVisibility(View.GONE);
            findViewById(R.id.food_view).setVisibility(View.VISIBLE);
            findViewById(R.id.usedItems_view).setVisibility(View.GONE);

            blood.setTextColor(Color.parseColor("#BAC1C1"));
            food.setTextColor(Color.parseColor("#2BB8C1"));
            usedItems.setTextColor(Color.parseColor("#BAC1C1"));
            requestsModels = new ArrayList<>();
            getDataFood();
        });

        usedItems.setOnClickListener(v -> {
            findViewById(R.id.blood_view).setVisibility(View.GONE);
            findViewById(R.id.food_view).setVisibility(View.GONE);
            findViewById(R.id.usedItems_view).setVisibility(View.VISIBLE);

            blood.setTextColor(Color.parseColor("#BAC1C1"));
            food.setTextColor(Color.parseColor("#BAC1C1"));
            usedItems.setTextColor(Color.parseColor("#2BB8C1"));
            requestsModels = new ArrayList<>();
            getDataUsedItems();
        });
    }


    public void getDataBlood() {
        progress.setMessage("Getting Data");
        progress.setCancelable(false);
        progress.show();
        db.collection("bloodRequest")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progress.dismiss();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            FoodRequestModel foodModel = new FoodRequestModel();
                            foodModel.setDonationId(document.getId());
                            foodModel.setFoodName(String.valueOf(data.get("foodName")));
                            foodModel.setBloodGroup(String.valueOf(data.get("bloodGroup")));
                            foodModel.setName(String.valueOf(data.get("name")));
                            foodModel.setAttendantName(String.valueOf(data.get("attendantName")));
                            foodModel.setAttendantContact(String.valueOf(data.get("attendantContact")));
                            foodModel.setPickDrop(String.valueOf(data.get("pickDrop")));
                            foodModel.setDate(String.valueOf(data.get("date")));
                            foodModel.setStatus(String.valueOf(data.get("status")));


                            foodModel.setCity(String.valueOf(data.get("city")));
                            foodModel.setStreetAddress(String.valueOf(data.get("streetAddress")));
                            foodModel.setProvince(String.valueOf(data.get("province")));
                            foodModel.setDisease(String.valueOf(data.get("disease")));

                            foodModel.setHbLevel(String.valueOf(data.get("hbLevel")));
                            foodModel.setHospital(String.valueOf(data.get("hospital")));
                            foodModel.setPatientName(String.valueOf(data.get("patientName")));
                            foodModel.setPintsRequired(String.valueOf(data.get("pintsRequired")));
                            foodModel.setTimeLimit(String.valueOf(data.get("timeLimit")));
                            foodModel.setAge(String.valueOf(data.get("age")));
                            DocumentReference documentReference;
                            documentReference = (DocumentReference) data.get("receiverId");
                            foodModel.setReceiverId(String.valueOf(documentReference.getPath()));

                            if(foodModel.getStatus().equalsIgnoreCase("active"))
                            requestsModels.add(foodModel);

                        }

                        usedItemsAdapter groupAdapter = new usedItemsAdapter("blood", requestsModels, GeneralDonationRequests.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(GeneralDonationRequests.this);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(groupAdapter);
                    } else {
                        progress.dismiss();
                    }
                });
    }

    public void getDataFood() {
        progress.setMessage("Getting Data");
        progress.setCancelable(false);
        progress.show();
        db.collection("foodRequest")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progress.dismiss();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            FoodRequestModel foodModel = new FoodRequestModel();
                            foodModel.setDonationId(document.getId());
                            foodModel.setFoodName(String.valueOf(data.get("foodName")));
                            foodModel.setAttendantName(String.valueOf(data.get("attendantName")));
                            foodModel.setAttendantContact(String.valueOf(data.get("attendantContact")));
                            foodModel.setPickDrop(String.valueOf(data.get("pickDrop")));
                            foodModel.setDate(String.valueOf(data.get("date")));
                            foodModel.setQuantity(String.valueOf(data.get("quantity")));
                            foodModel.setCategory(String.valueOf(data.get("category")));

                            foodModel.setCity(String.valueOf(data.get("city")));
                            foodModel.setStatus(String.valueOf(data.get("status")));
                            foodModel.setProvince(String.valueOf(data.get("province")));
                            foodModel.setStreetAddress(String.valueOf(data.get("streetAddress")));

                            DocumentReference documentReference;
                            documentReference = (DocumentReference) data.get("receiverId");
                            foodModel.setReceiverId(String.valueOf(documentReference.getPath()));

                            if(foodModel.getStatus().equalsIgnoreCase("active"))
                            requestsModels.add(foodModel);

                        }

                        usedItemsAdapter groupAdapter = new usedItemsAdapter("food", requestsModels, GeneralDonationRequests.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(GeneralDonationRequests.this);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(groupAdapter);
                    } else {
                        progress.dismiss();
                    }
                });
    }

    public void getDataUsedItems() {
        progress.setMessage("Getting Data");
        progress.setCancelable(false);
        progress.show();
        db.collection("usedItemRequest")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progress.dismiss();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            FoodRequestModel foodModel = new FoodRequestModel();
                            foodModel.setDonationId(document.getId());
                            foodModel.setName(String.valueOf(data.get("name")));
                            foodModel.setAttendantName(String.valueOf(data.get("attendantName")));
                            foodModel.setAttendantContact(String.valueOf(data.get("attendantContact")));
                            foodModel.setPickDrop(String.valueOf(data.get("pickDrop")));
                            foodModel.setDate(String.valueOf(data.get("date")));
                            foodModel.setStatus(String.valueOf(data.get("status")));


                            foodModel.setQuantity(String.valueOf(data.get("quantity")));
                            foodModel.setCategory(String.valueOf(data.get("category")));

                            foodModel.setCity(String.valueOf(data.get("city")));
                            foodModel.setProvince(String.valueOf(data.get("province")));
                            foodModel.setStreetAddress(String.valueOf(data.get("streetAddress")));

                            foodModel.setDescription(String.valueOf(data.get("description")));
                            foodModel.setSize(String.valueOf(data.get("size")));
                            foodModel.setBrand(String.valueOf(data.get("brand")));
                            foodModel.setAuthor(String.valueOf(data.get("author")));
                            foodModel.setEdition(String.valueOf(data.get("edition")));

                            DocumentReference documentReference;
                            documentReference = (DocumentReference) data.get("receiverId");
                            foodModel.setReceiverId(String.valueOf(documentReference.getPath()));

                            if(foodModel.getStatus().equalsIgnoreCase("active"))
                            requestsModels.add(foodModel);
                        }

                        usedItemsAdapter groupAdapter = new usedItemsAdapter("used", requestsModels, GeneralDonationRequests.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(GeneralDonationRequests.this);
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
        String type;


        public usedItemsAdapter(String type, ArrayList<FoodRequestModel> contactModelList, Context context) {
            this.contactModelList = contactModelList;
            this.context = context;
            this.type = type;
        }


        @Override
        public usedItemsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.general_requests_view, parent, false);
            return new usedItemsAdapter.MyViewHolder(item);
        }

        @Override
        public void onBindViewHolder(usedItemsAdapter.MyViewHolder holder, int position) {
            FoodRequestModel user_model = contactModelList.get(position);


            String date=user_model.getDate();
            try {
                String[] s = date.split(" ");
                holder.date_food.setText(Html.fromHtml("<b>Date:</b> "+s[0]+" "+s[1]+" "+s[2]+" "+s[3]));
                holder.date_usedItems.setText(Html.fromHtml("<b>Date:</b> "+s[0]+" "+s[1]+" "+s[2]+" "+s[3]));
                holder.date_blood.setText(Html.fromHtml("<b>Date:</b> "+s[0]+" "+s[1]+" "+s[2]+" "+s[3]));

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (type.equalsIgnoreCase("blood")) {
                holder.blood_layout.setVisibility(View.VISIBLE);
                holder.food_layout.setVisibility(View.GONE);
                holder.usedItems_layout.setVisibility(View.GONE);

                holder.donation_name_blood.setText(Html.fromHtml("<b>Blood Group:</b> " + user_model.getBloodGroup()));
                holder.quantity_blood.setText(Html.fromHtml("<b>Address:</b> " + user_model.getStreetAddress()));
                holder.patient_name_blood.setText(Html.fromHtml("<b>Patient Name:</b> " + user_model.getPatientName()));
                holder.age_blood.setText(Html.fromHtml("<b>Age:</b> " + user_model.getAge()));
                holder.disease_blood.setText(Html.fromHtml("<b>Disease:</b> " + user_model.getDisease()));
                holder.hb_blood.setText(Html.fromHtml("<b>HB Level:</b> " + user_model.getHbLevel()));
                holder.pints_blood.setText(Html.fromHtml("<b>Pints Required:</b> " + user_model.getPintsRequired()));
                holder.time_limit_blood.setText(Html.fromHtml("<b>Time Limit:</b> " + user_model.getTimeLimit()));
                holder.hospital_blood.setText(Html.fromHtml("<b>Hospital:</b> " + user_model.getHospital()));
                holder.province_blood.setText(Html.fromHtml("<b>Province:</b> " + user_model.getProvince()));
                holder.city_blood.setText(Html.fromHtml("<b>City:</b> " + user_model.getCity()));
                holder.pickDrop_blood.setText(Html.fromHtml("<b>Pick Drop:</b> " + user_model.getPickDrop()));
                holder.attendant_name_blood.setText(Html.fromHtml("<b>Attendant Name:</b> " + user_model.getAttendantName()));
                holder.attandant_contact_blood.setText(Html.fromHtml("<b>Attendant Contact:</b> " + user_model.getAttendantContact()));


            }
            else if (type.equalsIgnoreCase("food")) {

                holder.blood_layout.setVisibility(View.GONE);
                holder.food_layout.setVisibility(View.VISIBLE);
                holder.usedItems_layout.setVisibility(View.GONE);

                holder.donation_name_food.setText(Html.fromHtml("<b>Donation Name:</b> " + user_model.getFoodName()));
                holder.quantity_food.setText(Html.fromHtml("<b>Quantity:</b> " + user_model.getQuantity()));
                holder.category_food.setText(Html.fromHtml("<b>Category:</b> " + user_model.getCategory()));
                holder.address_food.setText(Html.fromHtml("<b>Address:</b> " + user_model.getStreetAddress()));
                holder.province_food.setText(Html.fromHtml("<b>Province:</b> " + user_model.getProvince()));
                holder.city_food.setText(Html.fromHtml("<b>City:</b> " + user_model.getCity()));
                holder.attendant_name_food.setText(Html.fromHtml("<b>Attendant Name:</b> " + user_model.getAttendantName()));
                holder.attandant_contact_food.setText(Html.fromHtml("<b>Attendant Contact:</b> " + user_model.getAttendantContact()));
                holder.pickdrop_food.setText(Html.fromHtml("<b>Pick Drop:</b> " + user_model.getPickDrop()));


            }
            else {
                holder.blood_layout.setVisibility(View.GONE);
                holder.food_layout.setVisibility(View.GONE);
                holder.usedItems_layout.setVisibility(View.VISIBLE);

                holder.donation_name_usedItems.setText(Html.fromHtml("<b>Donation Name:</b> " + user_model.getName()));
                holder.quantity_usedItems.setText(Html.fromHtml("<b>Quantity:</b> " + user_model.getQuantity()));
                holder.category_usedItems.setText(Html.fromHtml("<b>Category:</b> " + user_model.getCategory()));
                holder.address_usedItems.setText(Html.fromHtml("<b>Address:</b> " + user_model.getStreetAddress()));
                holder.province_usedItems.setText(Html.fromHtml("<b>Province:</b> " + user_model.getProvince()));
                holder.city_usedItems.setText(Html.fromHtml("<b>City:</b> " + user_model.getCity()));
                holder.attendant_name_usedItems.setText(Html.fromHtml("<b>Attendant Name:</b> " + user_model.getAttendantName()));
                holder.attandant_contact_usedItems.setText(Html.fromHtml("<b>Attendant Contact:</b> " + user_model.getAttendantContact()));
                holder.pickdrop_usedItems.setText(Html.fromHtml("<b>Pick Drop:</b> " + user_model.getPickDrop()));

                String description,size,brand,author,edition;
                if (user_model.getDescription() == null)
                {
                    description="N/A";
                }
                else {
                    description=user_model.getDescription();
                }

                if (user_model.getSize() == "null")
                {
                    size="N/A";
                }
                else {
                    size=user_model.getSize();
                }

                if (user_model.getBrand() == "null")
                {
                    brand="N/A";
                }
                else {
                    brand=user_model.getBrand();
                }

                if (user_model.getAuthor() == "null")
                {
                    author="N/A";
                }
                else {
                    author=user_model.getAuthor();
                }

                if (user_model.getEdition() == "null")
                {
                    edition="N/A";
                }
                else {
                    edition=user_model.getEdition();
                }

                holder.decription_usdeItem.setText(Html.fromHtml("<b>Description:</b> " + description));
                holder.size_usdeItem.setText(Html.fromHtml("<b>Size:</b> " + size));
                holder.brand_usdeItem.setText(Html.fromHtml("<b>Brand:</b> " + brand));
                holder.author_usdeItem.setText(Html.fromHtml("<b>Author:</b> " + author));
                holder.edition_usdeItem.setText(Html.fromHtml("<b>Edition:</b> " + edition));
            }


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

                        }
                        else {

                        }
                    } else {


                    }
                }
            });


            String finalDonor = donor;
            holder.accept_btn.setOnClickListener(v -> {

                progress.setMessage("Please wait..");
                progress.setCancelable(false);
                progress.show();

                Map<String, Object> blood_obj = new HashMap<>();

                blood_obj.put("status", "inactive");

                String table="";

                if (type.equalsIgnoreCase("blood")) {
                    table="bloodRequest";
                }
                else if (type.equalsIgnoreCase("food")) {
                    table="foodRequest";
                }

                else {
                    table="usedItemRequest";
                }
                db.collection(table).document(user_model.getDonationId())
                        .update(blood_obj)
                        .addOnSuccessListener(aVoid -> {
                            sendNotification(finalDonor,name_new[0],userPreferences.getFname()+" has accepted your donation request",contactNo[0],img[0],email[0]);

                        })
                        .addOnFailureListener(e -> {
                            progress.dismiss();
                            Toast.makeText(GeneralDonationRequests.this, e.toString(), Toast.LENGTH_SHORT).show();
                        });


            });


        }

        @Override
        public int getItemCount() {
            return contactModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            // Food
            TextView donation_name_food, quantity_food, category_food, address_food, province_food, city_food,
                    attendant_name_food, attandant_contact_food, pickdrop_food, date_food;
            // Used
            TextView donation_name_usedItems, quantity_usedItems, category_usedItems, province_usedItems,address_usedItems, city_usedItems, attendant_name_usedItems,
                    attandant_contact_usedItems, pickdrop_usedItems, date_usedItems, decription_usdeItem, size_usdeItem, brand_usdeItem, author_usdeItem, edition_usdeItem;
            // Blood
            TextView donation_name_blood, quantity_blood, patient_name_blood, age_blood, disease_blood, hb_blood, pints_blood,
                    time_limit_blood, hospital_blood, province_blood,date_blood, city_blood, pickDrop_blood,attendant_name_blood,attandant_contact_blood;

            Button accept_btn;
            LinearLayout blood_layout, usedItems_layout, food_layout;

            public MyViewHolder(View itemView) {
                super(itemView);
                donation_name_food = itemView.findViewById(R.id.donation_name_food);
                quantity_food = itemView.findViewById(R.id.quantity_food);
                category_food = itemView.findViewById(R.id.category_food);
                address_food = itemView.findViewById(R.id.address_food);
                province_food = itemView.findViewById(R.id.province_food);
                city_food = itemView.findViewById(R.id.city_food);
                attendant_name_food = itemView.findViewById(R.id.attendant_name_food);
                attandant_contact_food = itemView.findViewById(R.id.attandant_contact_food);
                pickdrop_food = itemView.findViewById(R.id.pickdrop_food);
                date_food = itemView.findViewById(R.id.date_food);

                donation_name_usedItems = itemView.findViewById(R.id.donation_name_usedItems);
                quantity_usedItems = itemView.findViewById(R.id.quantity_usedItems);
                category_usedItems = itemView.findViewById(R.id.category_usedItems);
                province_usedItems = itemView.findViewById(R.id.province_usedItems);
                city_usedItems = itemView.findViewById(R.id.city_usedItems);
                attendant_name_usedItems = itemView.findViewById(R.id.attendant_name_usedItems);
                attandant_contact_usedItems = itemView.findViewById(R.id.attandant_contact_usedItems);
                pickdrop_usedItems = itemView.findViewById(R.id.pickdrop_usedItems);
                date_usedItems = itemView.findViewById(R.id.date_usedItems);
                decription_usdeItem = itemView.findViewById(R.id.decription_usdeItem);
                size_usdeItem = itemView.findViewById(R.id.size_usdeItem);
                brand_usdeItem = itemView.findViewById(R.id.brand_usdeItem);
                author_usdeItem = itemView.findViewById(R.id.author_usdeItem);
                edition_usdeItem = itemView.findViewById(R.id.edition_usdeItem);
                address_usedItems=itemView.findViewById(R.id.address_usedItems);


                donation_name_blood = itemView.findViewById(R.id.donation_name_blood);
                quantity_blood = itemView.findViewById(R.id.quantity_blood);
                patient_name_blood = itemView.findViewById(R.id.patient_name_blood);
                age_blood = itemView.findViewById(R.id.age_blood);
                disease_blood = itemView.findViewById(R.id.disease_blood);
                hb_blood = itemView.findViewById(R.id.hb_blood);
                pints_blood = itemView.findViewById(R.id.pints_blood);
                time_limit_blood = itemView.findViewById(R.id.time_limit_blood);
                hospital_blood = itemView.findViewById(R.id.hospital_blood);
                province_blood = itemView.findViewById(R.id.province_blood);
                city_blood = itemView.findViewById(R.id.city_blood);
                pickDrop_blood = itemView.findViewById(R.id.pickDrop_blood);
                attandant_contact_blood=itemView.findViewById(R.id.attandant_contact_blood);
                attendant_name_blood=itemView.findViewById(R.id.attendant_name_blood);
                date_blood=itemView.findViewById(R.id.date_blood);

                accept_btn = itemView.findViewById(R.id.accept_btn);

                food_layout = itemView.findViewById(R.id.food_layout);
                blood_layout = itemView.findViewById(R.id.blood_layout);
                usedItems_layout = itemView.findViewById(R.id.usedItems_layout);

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
                                        Intent chatIntent=new Intent(GeneralDonationRequests.this, ChatActivity.class);
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


}