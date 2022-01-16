package com.example.ehsaas.UI.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ehsaas.R;
import com.example.ehsaas.UI.Fragments.Dashboard;
import com.example.ehsaas.UI.Models.MessageAdapter;
import com.example.ehsaas.UI.Models.Messages;
import com.example.ehsaas.UI.Preferences.UserPreferences;
import com.example.ehsaas.UI.notification.APIService;
import com.example.ehsaas.UI.notification.Client;
import com.example.ehsaas.UI.notification.Data;
import com.example.ehsaas.UI.notification.Response;
import com.example.ehsaas.UI.notification.Sender;
import com.example.ehsaas.UI.notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {


    private RecyclerView userMessageList;

    //for checking if user has seen msg or not
    ValueEventListener seenListener;

    private int REQUEST_PERMISSION_WRITE = 1001;
    private boolean permissionGranted;

    private String SaveCurrentTime, SaveCurrentDate;
    private String checker = "", myUrl = "";
    StorageTask uploadTask;
    private Uri FileUri;

    private ProgressDialog loadingBar;

    TextView userName, userLastSeen;
    EditText inputMessage;
    ImageView sendMessageButton, attachment;
    CircleImageView userImage;
    ImageView timer, call, video;

    private String currentUserID;
    UserPreferences userPreferences;


    FirebaseAuth mAuth;
    private DatabaseReference RootRef, userRefForSeen;

    private final List<Messages> messagesList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;


    String messageReceiverID, messageReceiverName, receiverImage,user_contact,receiverEmail;


    APIService apiService;
    boolean notify = false;
    private DatabaseReference ContactsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ImageView back = (ImageView) findViewById(R.id.back_btn);

        userPreferences=new UserPreferences(ChatActivity.this);

        messageReceiverID = getIntent().getStringExtra("visit_user_id");

        messageReceiverName = getIntent().getStringExtra("visit_user_name");

        user_contact = getIntent().getStringExtra("user_contact");

        initViews();
        receiverImage = getIntent().getStringExtra("visit_image");
        receiverEmail = getIntent().getStringExtra("email");



        ContactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts");


        messageAdapter = new MessageAdapter(messagesList, ChatActivity.this, messageReceiverID, messageReceiverName, receiverImage);

        userMessageList = findViewById(R.id.private_message_list_of_users);

        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setLayoutManager(linearLayoutManager);
        messageAdapter.notifyDataSetChanged();
        userMessageList.setAdapter(messageAdapter);

        userLastSeen.setText(user_contact);

        loadingBar = new

                ProgressDialog(this);


        //create api service

        apiService = Client.getRetrofit("https://fcm.googleapis.com/").

                create(APIService.class);


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notify = true;
                SendMessage();
            }
        });

//        DisplayLastSeen();

//
//        attachment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                AttachmentFragment attachmentFragment = new AttachmentFragment();
//                attachmentFragment.setData(messageReceiverID, messageReceiverName, receiverImage, ChatActivity.this);
//                attachmentFragment.show(getSupportFragmentManager(), attachmentFragment.getTag());
//
//            }
//        });


        ReadMessage();

        SeenMessage();

    }

    private void initViews() {
        userName = findViewById(R.id.custom_profile_name);
        userImage = findViewById(R.id.custom_profile_image);
        userLastSeen = findViewById(R.id.last_seen);


        inputMessage = findViewById(R.id.input_message);
        sendMessageButton = findViewById(R.id.send_button);
        attachment = findViewById(R.id.attachment);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        SaveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        SaveCurrentTime = currentTime.format(calendar.getTime());


        userName.setText(messageReceiverName);
        Picasso.get().load(receiverImage).placeholder(R.drawable.dp).into(userImage);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = userPreferences.getUserId();

        RootRef = FirebaseDatabase.getInstance().getReference();


    }

    private void ReadMessage() {
        if (!permissionGranted) {
            checkPermission();
        }
        RootRef.child("Messages").child(currentUserID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        //    messagesList.clear();
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SeenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Messages").child(currentUserID).child(messageReceiverID);
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Messages chat = ds.getValue(Messages.class);
//                    if(chat.getFrom().equals(currentUserID)  && chat.getTo().equals(messageReceiverID))
//                    {
                    HashMap<String, Object> hasSeenMap = new HashMap<>();
                    hasSeenMap.put("seen", true);
                    ds.getRef().updateChildren(hasSeenMap);
                }
            }
//            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void SendMessage() {

        final String messageText = inputMessage.getText().toString();

        if (messageText.equals("")) {
            Toast.makeText(this, "Type a message..", Toast.LENGTH_SHORT).show();
        } else {

            String MessageSenderRef = "Messages/" + currentUserID + "/" + messageReceiverID;
            String MessageReceiverRef = "Messages/" + messageReceiverID + "/" + currentUserID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(currentUserID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Calendar calendar = Calendar.getInstance();
            calendar.get(Calendar.YEAR);
            calendar.get(Calendar.MONTH);
            calendar.get(Calendar.DAY_OF_MONTH);
            calendar.get(Calendar.HOUR_OF_DAY);
            calendar.get(Calendar.MINUTE);

            final String timeStamp = String.valueOf(calendar.getTimeInMillis());

            Map<String, Object> messageTextBody = new HashMap<>();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", currentUserID);
            messageTextBody.put("to", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", SaveCurrentTime);
            messageTextBody.put("date", SaveCurrentDate);
            messageTextBody.put("timeStamp", timeStamp);
            messageTextBody.put("timer", false);
            messageTextBody.put("seen", false);


            Map<String, Object> messageBodyDetails = new HashMap<>();
            messageBodyDetails.put(MessageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(MessageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        // Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                        inputMessage.setText("");
                        createContact();
                    } else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }


                    if (notify) {
                        sendNotification(messageReceiverID, messageReceiverName, messageText);
                        // Toast.makeText(ChatActivity.this, "Notification is sent", Toast.LENGTH_SHORT).show();
                    }
                    notify = false;


                }
            });
        }


    }

    private void sendNotification(final String messageReceiverID, final String messageReceiverName, final String messageText) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(messageReceiverID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(currentUserID, messageReceiverName + ":" + messageText, "New Message", messageReceiverID, R.drawable.dp);

                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    if (response.code() == 200) {
                                        //  Toast.makeText(ChatActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            loadingBar.setTitle("Sending File");
            loadingBar.setMessage("Please wait, file is sending..");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            FileUri = data.getData();

            if (!checker.equals("image")) {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");

                final String MessageSenderRef = "Messages/" + currentUserID + "/" + messageReceiverID;
                final String MessageReceiverRef = "Messages/" + messageReceiverID + "/" + currentUserID;

                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                        .child(currentUserID).child(messageReceiverID).push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + checker);


                Calendar calendar = Calendar.getInstance();
                calendar.get(Calendar.YEAR);
                calendar.get(Calendar.MONTH);
                calendar.get(Calendar.DAY_OF_MONTH);
                calendar.get(Calendar.HOUR_OF_DAY);
                calendar.get(Calendar.MINUTE);

                final String timeStamp = String.valueOf(calendar.getTimeInMillis());

                filePath.putFile(FileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            HashMap<String, Object> messageTextBody = new HashMap<>();
                            messageTextBody.put("message", task.getResult().getStorage().getDownloadUrl().toString());
                            messageTextBody.put("name", FileUri.getLastPathSegment());
                            messageTextBody.put("type", checker);
                            messageTextBody.put("from", currentUserID);
                            messageTextBody.put("to", messageReceiverID);
                            messageTextBody.put("messageID", messagePushID);
                            messageTextBody.put("time", SaveCurrentTime);
                            messageTextBody.put("date", SaveCurrentDate);
                            messageTextBody.put("timeStamp", timeStamp);
                            messageTextBody.put("timer", false);
                            messageTextBody.put("seen", false);


                            HashMap<String, Object> messageBodyDetails = new HashMap<>();
                            messageBodyDetails.put(MessageSenderRef + "/" + messagePushID, messageTextBody);
                            messageBodyDetails.put(MessageReceiverRef + "/" + messagePushID, messageTextBody);

                            RootRef.updateChildren(messageBodyDetails);
                            loadingBar.dismiss();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        loadingBar.dismiss();
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double p = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        loadingBar.setMessage((int) p + " % Uploading..");

                    }
                });
            } else {
                loadingBar.dismiss();
                Toast.makeText(this, "Nothing Selected, Error.", Toast.LENGTH_SHORT).show();
            }
        }

        // This request code is set by startActivityForResult(intent, REQUEST_CODE_1) method.
//        if(requestCode==1 && requestCode==RESULT_OK)
//        {
//
//
//            messageReceiverID = data.getStringExtra("visit_user_id");
//        }


    }

    private void DisplayLastSeen() {
        RootRef.child("Users").child(messageReceiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("userState").hasChild("state")) {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();

//                            if (state.equals("online")) {
//                                userLastSeen.setText("online");
//                            } else if (state.equals("offline")) {
//                                userLastSeen.setText(date + " " + time);
//                            }
                        } else {
                            userLastSeen.setText("offline");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void typeMessage(View view) {
        Drawable img = this.getResources().getDrawable(R.drawable.ic_insert_emoticon_color_24dp);
        inputMessage.invalidateDrawable(img);
    }

    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }

//    @Override
//    public void onAttachmentFragmentFinish(String messageReceiverID, String messageReceiverName, String receiverImage, Context context) {
//
//        this.messageReceiverID = messageReceiverID;
//        this.messageReceiverName = messageReceiverName;
//        this.receiverImage = receiverImage;
//
//    }



    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    //check if external storage is available for read and write
    public boolean isExternalStorageWriteable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    //check if external storage is available for read at least
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    // initiate request for permission

    private boolean checkPermission() {
        if (!isExternalStorageReadable() || !isExternalStorageWriteable()) {
            Toast.makeText(this, "This app only works on devices with usable external storage", Toast.LENGTH_SHORT).show();

            return false;
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
                Toast.makeText(this, "External Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You must grant permission!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void createContact()
    {
        UserPreferences userPreferences=new UserPreferences(ChatActivity.this);

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

                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}