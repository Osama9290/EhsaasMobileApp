package com.example.ehsaas.UI.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ehsaas.R;
import com.example.ehsaas.UI.Food.Receiver.FoodDetail;
import com.example.ehsaas.UI.Models.Contacts;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.Volunteer.CompletedDeliver;
import com.example.ehsaas.UI.notification.APIService;
import com.example.ehsaas.UI.notification.Client;
import com.example.ehsaas.UI.notification.Data;
import com.example.ehsaas.UI.notification.Response;
import com.example.ehsaas.UI.notification.Sender;
import com.example.ehsaas.UI.notification.Token;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class InboxActivity extends AppCompatActivity {


    private String currentUserID;
    private RecyclerView chat_list;

    private DatabaseReference ChatRef, UsersRef,SearchRef;
    private FirebaseAuth mAuth;
    public static String message = null;
    UserPreferences userPreferences;
    DocumentReference docRef;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_int);

        chat_list=findViewById(R.id.chats_list);
        progress = new ProgressDialog(InboxActivity.this);


        userPreferences=new UserPreferences(InboxActivity.this);
        mAuth=FirebaseAuth.getInstance();
        currentUserID=userPreferences.getUserId();
        ChatRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);

        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");

        chat_list.setLayoutManager(new LinearLayoutManager(InboxActivity.this));

    }

    @Override
    public void onStart() {
        super.onStart();

        progress.setMessage("Getting Data");
        progress.setCancelable(false);
        progress.show();
        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ChatRef,Contacts.class)
                        .build();


        final FirebaseRecyclerAdapter<Contacts,ChatViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Contacts model)
                    {
                        progress.dismiss();
                        final String[] name_new = new String[1];
                        final String[] contactNo = new String[1];
                        final String[] img = new String[1];
                        final String[] email = new String[1];
                        FirebaseFirestore db;
                        db = FirebaseFirestore.getInstance();


                        docRef = db.collection("donor").document(getRef(position).getKey());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists()) {
                                        Map<String, Object> data = document.getData();

                                        name_new[0] = String.valueOf(data.get("name"));
                                        holder.UserName.setText(name_new[0]);
                                        contactNo[0] = String.valueOf(data.get("contactNo"));
                                        holder.UserBio.setText(contactNo[0]);

                                        img[0] = String.valueOf(data.get("img"));

                                        email[0] = String.valueOf(data.get("email"));

                                        Glide.with(InboxActivity.this)
                                                .load(img[0])
                                                .placeholder(R.drawable.user)
                                                .into(holder.user_profile_picture);


                                    }
                                    else {
                                        docRef = db.collection("receiver").document(getRef(position).getKey());
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document != null && document.exists()) {
                                                        Map<String, Object> data = document.getData();

                                                        name_new[0] = String.valueOf(data.get("name"));
                                                        holder.UserName.setText(name_new[0]);
                                                        contactNo[0] = String.valueOf(data.get("contactNo"));
                                                        holder.UserBio.setText(contactNo[0]);

                                                        img[0] = String.valueOf(data.get("img"));

                                                        email[0] = String.valueOf(data.get("email"));

                                                        try {
                                                            Glide.with(InboxActivity.this)
                                                                    .load(img[0])
                                                                    .placeholder(R.drawable.user)
                                                                    .into(holder.user_profile_picture);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }


                                                    }
                                                    else {
                                                        docRef = db.collection("volunteer").document(getRef(position).getKey());
                                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    DocumentSnapshot document = task.getResult();
                                                                    if (document != null && document.exists()) {
                                                                        Map<String, Object> data = document.getData();

                                                                        name_new[0] = String.valueOf(data.get("name"));
                                                                        holder.UserName.setText(name_new[0]);
                                                                        contactNo[0] = String.valueOf(data.get("contactNo"));
                                                                        holder.UserBio.setText(contactNo[0]);

                                                                        img[0] = String.valueOf(data.get("img"));

                                                                        email[0] = String.valueOf(data.get("email"));

                                                                        Glide.with(InboxActivity.this)
                                                                                .load(img[0])
                                                                                .placeholder(R.drawable.user)
                                                                                .into(holder.user_profile_picture);


                                                                    }
                                                                    else {

                                                                    }
                                                                } else {

                                                                }
                                                            }
                                                        });

                                                    }
                                                } else {

                                                }
                                            }
                                        });

                                    }
                                } else {
                                    progress.dismiss();
                                }
                            }
                        });



                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent=new Intent(InboxActivity.this,ChatActivity.class);
                                chatIntent.putExtra("visit_user_id",getRef(position).getKey());
                                chatIntent.putExtra("visit_user_name",name_new[0]);
                                chatIntent.putExtra("user_contact", contactNo[0]);
                                chatIntent.putExtra("visit_image", img[0]);
                                chatIntent.putExtra("email", email[0]);

                                startActivity(chatIntent);
                                overridePendingTransition(R.anim.animate_card_enter,R.anim.animate_card_exit);

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                        return new ChatViewHolder(view);
                    }
                };

        chat_list.setAdapter(adapter);
        adapter.startListening();
        progress.dismiss();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder
    {

        TextView UserName,UserBio;
        CircleImageView user_profile_picture;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            UserName=itemView.findViewById(R.id.user_profile_name);
            UserBio=itemView.findViewById(R.id.user_bio);
            user_profile_picture=itemView.findViewById(R.id.user_profile_image);

        }
    }


}