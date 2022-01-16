package com.example.ehsaas.UI.Food.Receiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ehsaas.R;
import com.example.ehsaas.UI.Activities.UsedItems.Receiver.UsedItemShowCategory;
import com.example.ehsaas.UI.Models.FoodModel;
import com.example.ehsaas.UI.Models.UsedItemsModel;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.Profile.MyDonations;

import java.util.ArrayList;

public class AllFoodAndUsedItems extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView no_item,category_txt;
    ImageView  back;
    UserPreferences userPreferences;
    String type = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_savory);
        back = findViewById(R.id.back_btn);

        back.setOnClickListener(v -> finish());

        recyclerView=findViewById(R.id.recyclerview);
        no_item=findViewById(R.id.no_item);
        category_txt=findViewById(R.id.category_txt);
        userPreferences=new UserPreferences(AllFoodAndUsedItems.this);


        try {
            type = getIntent().getExtras().getString("type");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String from = null;
        try {
            from = getIntent().getExtras().getString("from");
        } catch (Exception e) {
            e.printStackTrace();
        }
        category_txt.setText(type);

        if (from == null)
        {

            ArrayList<FoodModel> foodModels;
            if (type == null)
            {
                category_txt.setText("My Donations");
                foodModels = MyDonations.foodModels;
            }
            else {
                foodModels = FoodSelectCategory.foodModels;
            }


            ArrayList<FoodModel> foodModels_filtered = new ArrayList<>();

            for (int i=0 ;i<foodModels.size() ; i++)
            {
                if (type == null)
                {
                    if (foodModels.get(i).getDonorId().contains(userPreferences.getUserId()))
                    {
                        foodModels_filtered.add(foodModels.get(i));
                    }
                }
                else {
                    if (foodModels.get(i).getCategory().equalsIgnoreCase(type))
                    {
                        foodModels_filtered.add(foodModels.get(i));
                    }
                }

            }


            if (foodModels_filtered.size() == 0)
            {
                no_item.setVisibility(View.VISIBLE);
            }
            else {
                no_item.setVisibility(View.GONE);
            }


            foodAdapter groupAdapter = new foodAdapter(foodModels_filtered, AllFoodAndUsedItems.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AllFoodAndUsedItems.this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(groupAdapter);
        }

        else {
            ArrayList<UsedItemsModel> foodModels;

            if (type == null)
            {
                category_txt.setText("My Donations");
                foodModels = MyDonations.usedItemsModels;
            }
            else {
                foodModels = UsedItemShowCategory.usedItemsModels;
            }


            ArrayList<UsedItemsModel> foodModels_filtered = new ArrayList<>();

            for (int i=0 ;i<foodModels.size() ; i++)
            {
                if (type == null)
                {
                    if (foodModels.get(i).getDonorId().contains(userPreferences.getUserId()))
                    {
                        foodModels_filtered.add(foodModels.get(i));
                    }
                }
                else {
                    if (type.equalsIgnoreCase("other"))
                    {
                        if (!foodModels.get(i).getCategory().equalsIgnoreCase("books") &&
                                !foodModels.get(i).getCategory().equalsIgnoreCase("clothes") &&
                                !foodModels.get(i).getCategory().equalsIgnoreCase("furniture"))
                        {
                            foodModels_filtered.add(foodModels.get(i));
                        }
                    }
                    else
                    {
                        if (foodModels.get(i).getCategory().equalsIgnoreCase(type))
                        {
                            foodModels_filtered.add(foodModels.get(i));
                        }
                    }
                    }


            }


            if (foodModels_filtered.size() == 0)
            {
                no_item.setVisibility(View.VISIBLE);
            }
            else {
                no_item.setVisibility(View.GONE);
            }


            usedItemsAdapter groupAdapter = new usedItemsAdapter(foodModels_filtered, AllFoodAndUsedItems.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AllFoodAndUsedItems.this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(groupAdapter);
        }




        back.setOnClickListener(v -> {
            finish();
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
                Intent intent = new Intent(AllFoodAndUsedItems.this, FoodDetail.class);
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
                Intent intent = new Intent(AllFoodAndUsedItems.this, FoodDetail.class);
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
                intent.putExtra("description",user_model.getDescription());
                intent.putExtra("size",user_model.getSize());
                intent.putExtra("brand",user_model.getBrand());
                intent.putExtra("author",user_model.getAuthor());
                intent.putExtra("edition",user_model.getEdition());
                intent.putExtra("status",user_model.getStatus());
                intent.putExtra("rating",user_model.getRating());
                intent.putExtra("type",type);
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
}