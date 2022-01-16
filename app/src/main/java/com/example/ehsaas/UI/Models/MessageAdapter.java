package com.example.ehsaas.UI.Models;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ehsaas.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static List<Messages> userMessagesList;
    private final Context activityContext;
    private final String messageReceiverId;
    private final String messageReceiverName;
    private final String userImage;
    public Context context;
    private boolean seen=true;

    private FirebaseAuth mAuth;
    private StorageReference mRef;


    private Boolean click=false,clickReceiver=false,clicktextSender=false,clickTextReceiver=false, clickPdfSender=false,clickPdfReceiver=false;




    public MessageAdapter(List<Messages> userMessagesList, Context context, String messageReceiverID, String messageReceiverName, String userImage)
    {
        MessageAdapter.userMessagesList =userMessagesList;
        this.activityContext=context;
        this.messageReceiverId=messageReceiverID;
        this.messageReceiverName=messageReceiverName;
        this.userImage=userImage;
    }



    class MessageViewHolder extends RecyclerView.ViewHolder
    {
        TextView receiver_message_text, sender_message_text;
        ImageView messageSenderPicture, messageReceiverPicture;
        CardView cardView,sender_cardView;
        TextView sender_time,receiver_time;


        MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            receiver_message_text=itemView.findViewById(R.id.receiver_text_message);
            sender_message_text=itemView.findViewById(R.id.sender_text_message);
            messageSenderPicture=itemView.findViewById(R.id.message_sender_image_view);
            messageReceiverPicture=itemView.findViewById(R.id.message_reciever_image_view);

            sender_time=itemView.findViewById(R.id.sender_time);
            receiver_time=itemView.findViewById(R.id.receiver_time);

            cardView=itemView.findViewById(R.id.cardView);
            sender_cardView=itemView.findViewById(R.id.sender_cardView);

        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout, parent, false);
        context=parent.getContext();
        mAuth=FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position)
    {

        final String messageSenderID =mAuth.getCurrentUser().getUid();
        final Messages messages=userMessagesList.get(position);


        String fromUserID= messages.getFrom();
        String toUserID=messages.getTo();
        String pushID=messages.getMessageID();
        String fromMessageType=messages.getType();

        FirebaseStorage mStorageReference = FirebaseStorage.getInstance();
        mRef = mStorageReference.getReference("Document Files/");

        final DatabaseReference ReceiverRef=FirebaseDatabase.getInstance().getReference().child("Messages").child(toUserID).child(fromUserID).child(pushID);

        final DatabaseReference SenderRef=FirebaseDatabase.getInstance().getReference().child("Messages").child(fromUserID).child(toUserID).child(pushID);

        ReceiverRef.child("seen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class) == null)
                {
                    seen=true;
                }
                else {
                    seen = dataSnapshot.getValue(Boolean.class);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        holder.receiver_message_text.setVisibility(View.GONE);
        holder.sender_message_text.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);
        holder.cardView.setVisibility(View.GONE);
        holder.sender_cardView.setVisibility(View.GONE);
        holder.sender_time.setVisibility(View.GONE);
        holder.receiver_time.setVisibility(View.GONE);



        if(fromMessageType.equals("text"))
        {
            if(fromUserID.equals(messageSenderID))
            {

                holder.sender_message_text.setVisibility(View.VISIBLE);
                Animation animation= AnimationUtils.loadAnimation(context,R.anim.fadein);
                holder.sender_message_text.startAnimation(animation);
                holder.sender_message_text.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.sender_message_text.setTextColor(Color.BLACK);
                holder.sender_message_text.setText(messages.getMessage());

            }
            else
            {
                if((System.currentTimeMillis()) >= Long.parseLong(messages.getTimeStamp()))
                {
                    holder.receiver_message_text.setVisibility(View.VISIBLE);
                    holder.receiver_message_text.setBackgroundResource(R.drawable.receiver_messages_layout);
                    holder.receiver_message_text.setTextColor(Color.BLACK);
                    holder.receiver_message_text.setText(messages.getMessage());
                }
            }


        }
        else  if(fromMessageType.equals("image"))
        {
            // Sender Side
            if(fromUserID.equals(messageSenderID))
            {

                holder.sender_cardView.setVisibility(View.VISIBLE);
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                Animation animation= AnimationUtils.loadAnimation(context,R.anim.fadein);
                holder.sender_cardView.startAnimation(animation);
                holder.messageSenderPicture.startAnimation(animation);
              //  Picasso.get().load(messages.getMessage()).into(holder.messageSenderPicture);

                // Reference to an image file in Cloud Storage
                StorageReference storageReference  = FirebaseStorage.getInstance().getReference().child("Images").child(messages.getMessageID()+".image");


  //Load the image using Glide
//                Glide.with(activityContext)
//                        .using(new FirebaseImageLoader())
//                        .load(storageReference)
//                        .into(holder.messageSenderPicture);
//

                //save file on storage

                long ONE_MEGABYTE= 1024*1024*1024;

                final File file  = new File(Environment.getExternalStorageDirectory(),messages.getMessageID()+".jpg");

                storageReference.getBytes(ONE_MEGABYTE)
                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {

                                holder.messageSenderPicture.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));

                                try {
                                    FileOutputStream fos=new FileOutputStream(file);
                                    fos.write(bytes);
                                    fos.close();
                                    Toast.makeText(activityContext, "saved..", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activityContext, ""+e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }
            // Receiver Side
            else {
                if((System.currentTimeMillis()) >= Long.parseLong(messages.getTimeStamp()))
                {

                    holder.cardView.setVisibility(View.VISIBLE);
                    holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                   // Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPicture);

                    // Reference to an image file in Cloud Storage
                    StorageReference storageReference  = FirebaseStorage.getInstance().getReference().child("Images").child(messages.getMessageID()+".image");


// Load the image using Glide
//                    Glide.with(activityContext)
//                            .using(new FirebaseImageLoader())
//                            .load(storageReference)
//                            .into(holder.messageReceiverPicture);

                    //save file on storage

                    long ONE_MEGABYTE= 1024*1024*1024;

                    final File file  = new File(Environment.getExternalStorageDirectory(),messages.getMessageID()+".jpg");

                    storageReference.getBytes(ONE_MEGABYTE)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {

                                    holder.messageReceiverPicture.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));

                                    try {
                                        FileOutputStream fos=new FileOutputStream(file);
                                        fos.write(bytes);
                                        fos.close();
                                        Toast.makeText(activityContext, "saved..", Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(activityContext, ""+e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });


                }

            }

        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }
}
