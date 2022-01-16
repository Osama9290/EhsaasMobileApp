package com.example.ehsaas.UI.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ehsaas.R;
import com.example.ehsaas.UI.Blood.Receiver.BloodDetail;
import com.example.ehsaas.UI.Food.Receiver.AllFoodAndUsedItems;
import com.example.ehsaas.UI.Food.Receiver.FoodDetail;
import com.example.ehsaas.UI.Models.BloodModel;
import com.example.ehsaas.UI.Models.FoodModel;
import com.example.ehsaas.UI.Models.FoodRequestModel;
import com.example.ehsaas.UI.Models.UsedItemsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class search extends Fragment {


    Button search_btn;
    View view;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    ProgressDialog progress;
    EditText et_search;
    ArrayList<FoodModel> foodModels=new ArrayList<>();
    ArrayList<BloodModel> bloodModels = new ArrayList<>();
    ArrayList<UsedItemsModel> usedModel = new ArrayList<>();

    TextView blood, food, usedItems;
    String type="blood";

    public search() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_search, container, false);
        search_btn=view.findViewById(R.id.search_btn);

        recyclerView=view.findViewById(R.id.recyclerview);
        et_search=view.findViewById(R.id.et_search);
        usedItems = view.findViewById(R.id.usedItems);
        food = view.findViewById(R.id.food);
        blood = view.findViewById(R.id.blood);

        et_search.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);


        blood.setOnClickListener(v -> {

            view.findViewById(R.id.blood_view).setVisibility(View.VISIBLE);
            view.findViewById(R.id.food_view).setVisibility(View.GONE);
            view.findViewById(R.id.usedItems_view).setVisibility(View.GONE);

            blood.setTextColor(Color.parseColor("#2BB8C1"));
            food.setTextColor(Color.parseColor("#BAC1C1"));
            usedItems.setTextColor(Color.parseColor("#BAC1C1"));

            et_search.setHint("Search Blood Group");
            bloodModels = new ArrayList<>();
            type="blood";
            et_search.setText("");
            recyclerView.setAdapter(null);

        });

        food.setOnClickListener(v -> {

            view.findViewById(R.id.blood_view).setVisibility(View.GONE);
            view.findViewById(R.id.food_view).setVisibility(View.VISIBLE);
            view.findViewById(R.id.usedItems_view).setVisibility(View.GONE);

            blood.setTextColor(Color.parseColor("#BAC1C1"));
            food.setTextColor(Color.parseColor("#2BB8C1"));
            usedItems.setTextColor(Color.parseColor("#BAC1C1"));
            foodModels = new ArrayList<>();
            et_search.setText("");
            et_search.setHint("Search Food");
            type="food";
            recyclerView.setAdapter(null);
        });

        usedItems.setOnClickListener(v -> {
            view.findViewById(R.id.blood_view).setVisibility(View.GONE);
            view.findViewById(R.id.food_view).setVisibility(View.GONE);
            view.findViewById(R.id.usedItems_view).setVisibility(View.VISIBLE);

            blood.setTextColor(Color.parseColor("#BAC1C1"));
            food.setTextColor(Color.parseColor("#BAC1C1"));
            usedItems.setTextColor(Color.parseColor("#2BB8C1"));
            usedModel = new ArrayList<>();
            et_search.setHint("Search Used Item");
            type="used";
            et_search.setText("");
            recyclerView.setAdapter(null);
        });

        db = FirebaseFirestore.getInstance();
        progress = new ProgressDialog(getActivity());

        search_btn.setOnClickListener(v ->
        {
            if (et_search.getText().toString().isEmpty())
            {
                Toast.makeText(getActivity(), "Enter text for search...", Toast.LENGTH_SHORT).show();
            }
            else {
                if (type.equalsIgnoreCase("food"))
                {
                    searchFoodData(et_search.getText().toString());
                }
                else if (type.equalsIgnoreCase("blood"))
                {
                    searchBloodData(et_search.getText().toString());
                }
                else {
                    searchUsedData(et_search.getText().toString());
                }

            }

        });

        return view;
    }



    public void searchFoodData(String search)
    {
        foodModels=new ArrayList<>();
        progress.setMessage("Searching Data..");
        progress.setCancelable(false);
        progress.show();
        db.collection("food")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progress.dismiss();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            FoodModel foodModel=new FoodModel();
                            foodModel.setDonation_id(document.getId());
                            foodModel.setCity(String.valueOf(data.get("city")));
                            foodModel.setDate(String.valueOf(data.get("date")));
                            DocumentReference documentReference= (DocumentReference) data.get("donorId");
                            foodModel.setDonorId(String.valueOf(documentReference.getPath()));
                            foodModel.setExpiry(String.valueOf(data.get("expiry")));
                            foodModel.setImg(String.valueOf(data.get("img")));
                            foodModel.setProvince(String.valueOf(data.get("province")));
                            foodModel.setStatus(String.valueOf(data.get("status")));

                            foodModel.setCategory(String.valueOf(data.get("category")));
                            foodModel.setName(String.valueOf(data.get("name")));
                            foodModel.setQuantity(String.valueOf(data.get("quantity")));
                            foodModel.setStreetAddress(String.valueOf(data.get("streetAddress")));

                            if (foodModel.getName().contains(search)  || foodModel.getCategory().equalsIgnoreCase(search))
                            {
                                foodModels.add(foodModel);
                            }


                        }
                        foodAdapter groupAdapter = new foodAdapter(foodModels, getActivity());
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(groupAdapter);
                    } else {
                        progress.dismiss();
//                            Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    public class foodAdapter extends RecyclerView.Adapter<foodAdapter.MyViewHolder> {
        ArrayList<FoodModel> contactModelList;
        Context context;


        public foodAdapter(ArrayList<FoodModel> contactModelList, Context context) {
            this.contactModelList = contactModelList;
            this.context = context;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item_view, parent, false);
            return new MyViewHolder(item);
        }

        @Override
        public void onBindViewHolder(foodAdapter.MyViewHolder holder, int position) {
            FoodModel user_model=contactModelList.get(position);


            holder.item_name.setText(user_model.getName());
            holder.quantity.setText(user_model.getQuantity());
            holder.location.setText(user_model.getStreetAddress());

//            holder.province.setText("Province: "+user_model.getProvince());
//            holder.city.setText("City: "+user_model.getCity());
//            holder.date.setText("Date: "+user_model.getDate());

            Glide.with(context)
                    .load(user_model.getImg())
                    .placeholder(R.drawable.food)
                    .into(holder.food_image);



            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), FoodDetail.class);
                intent.putExtra("name",user_model.getName());
                intent.putExtra("img",user_model.getImg());
                intent.putExtra("location",user_model.getStreetAddress());
                intent.putExtra("Quantity",user_model.getQuantity());
                intent.putExtra("province",user_model.getProvince());
                intent.putExtra("city",user_model.getCity());
                intent.putExtra("date",user_model.getDate());
                intent.putExtra("donorId",user_model.getDonorId());
                intent.putExtra("category",user_model.getCategory());
                intent.putExtra("id",user_model.getDonation_id());
                intent.putExtra("status",user_model.getStatus());
                intent.putExtra("type",type);
                intent.putExtra("from","search");
                startActivity(intent);
//                finish();
            });



        }

        @Override
        public int getItemCount() {
            return contactModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView item_name,location,quantity,province,city,date;
            ImageView food_image;

            public MyViewHolder(View itemView) {
                super(itemView);
                item_name=itemView.findViewById(R.id.item_name);
                food_image=itemView.findViewById(R.id.food_image);
                location=itemView.findViewById(R.id.location);
                quantity=itemView.findViewById(R.id.quantity);
                province=itemView.findViewById(R.id.province);
                city=itemView.findViewById(R.id.city);
                date=itemView.findViewById(R.id.date);

            }

            @Override
            public void onClick(View v) {

            }
        }
    }

    public void searchUsedData(String search)
    {
        usedModel=new ArrayList<>();
        progress.setMessage("Searching Data..");
        progress.setCancelable(false);
        progress.show();
        db.collection("usedItems")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progress.dismiss();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            UsedItemsModel usedItemsModel=new UsedItemsModel();

                            usedItemsModel.setDonation_id(document.getId());
                            usedItemsModel.setAuthor(String.valueOf(data.get("author")));
                            usedItemsModel.setCity(String.valueOf(data.get("city")));
                            usedItemsModel.setDate(String.valueOf(data.get("date")));
                            DocumentReference documentReference= (DocumentReference) data.get("donorId");
                            usedItemsModel.setDonorId(String.valueOf(documentReference.getPath()));
                            usedItemsModel.setExpiry(String.valueOf(data.get("expiry")));
                            usedItemsModel.setImg(String.valueOf(data.get("img")));
                            usedItemsModel.setProvince(String.valueOf(data.get("province")));
                            usedItemsModel.setStatus(String.valueOf(data.get("status")));

                            usedItemsModel.setCategory(String.valueOf(data.get("category")));
                            usedItemsModel.setName(String.valueOf(data.get("name")));
                            usedItemsModel.setQuantity(String.valueOf(data.get("quantity")));
                            usedItemsModel.setRating(String.valueOf(data.get("rating")));
                            usedItemsModel.setStreetAddress(String.valueOf(data.get("streetAddress")));


                            usedItemsModel.setDescription(String.valueOf(data.get("description")));
                            usedItemsModel.setSize(String.valueOf(data.get("size")));
                            usedItemsModel.setBrand(String.valueOf(data.get("brand")));
                            usedItemsModel.setAuthor(String.valueOf(data.get("author")));
                            usedItemsModel.setEdition(String.valueOf(data.get("edition")));

                            if (usedItemsModel.getName().contains(search)  || usedItemsModel.getCategory().equalsIgnoreCase(search))
                            {
                                usedModel.add(usedItemsModel);
                            }


                        }

                        usedItemsAdapter groupAdapter = new usedItemsAdapter(usedModel, getActivity());
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(groupAdapter);
                    } else {
                        progress.dismiss();
//                            Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }
    public class usedItemsAdapter extends RecyclerView.Adapter<usedItemsAdapter.MyViewHolder> {
        ArrayList<UsedItemsModel> contactModelList;
        Context context;


        public usedItemsAdapter(ArrayList<UsedItemsModel> contactModelList, Context context) {
            this.contactModelList = contactModelList;
            this.context = context;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item_view, parent, false);
            return new MyViewHolder(item);
        }

        @Override
        public void onBindViewHolder(usedItemsAdapter.MyViewHolder holder, int position) {
            UsedItemsModel user_model=contactModelList.get(position);


            holder.item_name.setText(user_model.getName());
            holder.location.setText(user_model.getStreetAddress());
            holder.quantity.setText(user_model.getQuantity());


            Glide.with(context)
                    .load(user_model.getImg())
                    .placeholder(R.drawable.food)
                    .into(holder.food_image);



            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), FoodDetail.class);
                intent.putExtra("name",user_model.getName());
                intent.putExtra("img",user_model.getImg());
                intent.putExtra("location",user_model.getStreetAddress());
                intent.putExtra("Quantity",user_model.getQuantity());
                intent.putExtra("province",user_model.getProvince());
                intent.putExtra("city",user_model.getCity());
                intent.putExtra("date",user_model.getDate());
                intent.putExtra("donorId",user_model.getDonorId());
                intent.putExtra("id",user_model.getDonation_id());
                intent.putExtra("description",user_model.getDescription());
                intent.putExtra("size",user_model.getSize());
                intent.putExtra("brand",user_model.getBrand());
                intent.putExtra("author",user_model.getAuthor());
                intent.putExtra("edition",user_model.getEdition());
                intent.putExtra("status",user_model.getStatus());
                intent.putExtra("type",type);
                intent.putExtra("from","search");
                startActivity(intent);
//                finish();
            });



        }

        @Override
        public int getItemCount() {
            return contactModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView item_name,location,quantity;
            ImageView food_image;

            public MyViewHolder(View itemView) {
                super(itemView);
                item_name=itemView.findViewById(R.id.item_name);
                food_image=itemView.findViewById(R.id.food_image);
                location=itemView.findViewById(R.id.location);
                quantity=itemView.findViewById(R.id.quantity);


            }

            @Override
            public void onClick(View v) {

            }
        }
    }


    public void searchBloodData(String search)
    {
        bloodModels=new ArrayList<>();
        progress.setMessage("Searching Data...");
        progress.setCancelable(false);
        progress.show();
        db.collection("blood")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progress.dismiss();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            BloodModel bloodModel=new BloodModel();
                            bloodModel.setDonation_id(document.getId());
                            bloodModel.setAge(String.valueOf(data.get("age")));
                            bloodModel.setBloodGroup(String.valueOf(data.get("bloodGroup")));
                            bloodModel.setBloodTransfusion(String.valueOf(data.get("bloodTransfusion")));
                            bloodModel.setCity(String.valueOf(data.get("city")));
                            bloodModel.setCurrentMedication(String.valueOf(data.get("currentMedication")));
                            bloodModel.setDate(String.valueOf(data.get("date")));
                            DocumentReference documentReference= (DocumentReference) data.get("donorId");
                            bloodModel.setDonorId(String.valueOf(documentReference.getPath()));
                            bloodModel.setExpiry(String.valueOf(data.get("expiry")));
                            bloodModel.setIllness(String.valueOf(data.get("illness")));
                            bloodModel.setImg(String.valueOf(data.get("img")));
                            bloodModel.setLastDonated(String.valueOf(data.get("lastDonated")));
                            bloodModel.setProvince(String.valueOf(data.get("province")));
                            bloodModel.setSmoking(String.valueOf(data.get("smoking")));
                            bloodModel.setStatus(String.valueOf(data.get("status")));
                            bloodModel.setVaccination(String.valueOf(data.get("vaccination")));

                            if(bloodModel.getBloodGroup().contains(search))
                            {
                                bloodModels.add(bloodModel);
                            }

                        }

                        BloodAdapter groupAdapter = new BloodAdapter(bloodModels, getActivity());
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(groupAdapter);
                    } else {
                        progress.dismiss();
//                            Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
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
        public void onBindViewHolder(BloodAdapter.MyViewHolder holder, int position) {
            BloodModel user_model=contactModelList.get(position);


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

            holder.donor_side.setVisibility(View.GONE);
            holder.interest.setVisibility(View.GONE);
            holder.activate_layout.setVisibility(View.GONE);


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
            Button edit,delete,interest;

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