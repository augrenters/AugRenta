package com.example.sejeque.augrenta;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

/**
 * Created by SejeQue on 4/24/2018.
 *
 * Chat app
 */


public class ChatMessage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    DatabaseReference mDatabase,notifDatabase;


    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    DateFormat df;
    EditText editMessage;
    String propertyId, ownerId;
    String sender, senderID, messageValue, date;

    private RecyclerView mMessageList;

    private final List<Message> messageList = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;

    private MessageAdapter messageAdapter;

    private TextView userNameHandler, emailHandler;
    private ImageView imgHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatmessage_activity);

        //instantiate firebase auth
        mAuth = getInstance();
        //retrieve user information and store to currentUser
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        notifDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notifDatabase.keepSynced(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat");

        //function for Drawer toggle when clicking menu icon
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout1);
        NavigationView sideNavBar = findViewById(R.id.sideNav);
        //get reference for header in navigation view
        View headerView = sideNavBar.getHeaderView(0);

        //instantiate textView and imageView in header of navigation view
        userNameHandler = headerView.findViewById(R.id.textUser);
        emailHandler = headerView.findViewById(R.id.textEmail);
        imgHandler = headerView.findViewById(R.id.imageProfPic);

        //change texts for user's name, email, and profile pic in header of navigation view
        setCredentialView();

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.oper_drawer, R.string.close_drawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //if an item is clicked on navigation view
        sideNavBar.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();

                if(itemID == R.id.navigation_home){
                    goToHome();
                }
                else if(itemID == R.id.properties){
                    goToPropertyList();
                }
                else if(itemID == R.id.requests){
                    goToRequests();
                }
                else if(itemID == R.id.messenger){
                    goToMessages();
                }
                else if (itemID == R.id.favorite_properties){
                    goToFavorite();
                }

                else if(itemID == R.id.signOut){
                    signOutUser();
                }

                return true;
            }
        });
        // End of Setting Toolbar and Navigation Drawer

        //getting date
        df = new SimpleDateFormat("MMM d yyyy, HH:mm");

        if(currentUser == null){
            proceed();
        }

        editMessage = findViewById(R.id.editMessage);

        messageAdapter = new MessageAdapter(messageList);

        mMessageList = findViewById(R.id.messageRec);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(linearLayoutManager);
        mMessageList.setAdapter(messageAdapter);

        ownerId = getIntent().getExtras().getString("ownerId");
        propertyId = getIntent().getExtras().getString("propertyId");

        sender = currentUser.getDisplayName();
        senderID = currentUser.getUid();
        date = df.format(Calendar.getInstance().getTime());

        fetchMessages();

    }

    public void sendButtonClicked(View view) {
        sendMessage();
    }

    private void sendMessage() {

        messageValue = editMessage.getText().toString().trim();

        if(!TextUtils.isEmpty(messageValue)){

            String message_sender_ref = "Messages/" + senderID + "/" + ownerId + "/" + propertyId;
            String message_receiver_ref = "Messages/" + ownerId + "/" + senderID  + "/" + propertyId;

            DatabaseReference user_message_key = mDatabase.child("Chat").child(senderID).child(ownerId).child(propertyId).push();

            String message_push_id = user_message_key.getKey();

            Map messageTextBody = new HashMap();

            messageTextBody.put("sender", sender);
            messageTextBody.put("message", messageValue);
            messageTextBody.put("date", date);

            Map messageBodyDetails = new HashMap();

            messageBodyDetails.put(message_sender_ref + "/" + message_push_id, messageTextBody);

            messageBodyDetails.put(message_receiver_ref + "/" + message_push_id, messageTextBody);

            mDatabase.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        Log.d("Chat Log", databaseError.getMessage());
                    }else{
                        HashMap<String, String> notificationData = new HashMap<String, String>();
                        notificationData.put("fromName", currentUser.getDisplayName());
                        notificationData.put("fromID", currentUser.getUid());
                        notificationData.put("type", "receiver");
                        notificationData.put("response", "message");
                        notificationData.put("propertyId", propertyId);

                        notifDatabase.child(ownerId).push().setValue(notificationData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Log.d("Updated Req/Sender End", "Completed");
                                            editMessage.setText("");
                                        }
                                    }
                                });
                        editMessage.setText("");
                    }
                    editMessage.setText("");
                }
            });
        }
        else{
            Toast.makeText(this, "Please enter a message ", Toast.LENGTH_SHORT).show();
        }
        //mMessageList.smoothScrollToPosition(messageAdapter.getItemCount()-1);
        //Toast.makeText(ChatMessage.this, ""+ messageAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
    }

    private void fetchMessages() {

        mDatabase.child("Messages").child(senderID).child(ownerId).child(propertyId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Message message = dataSnapshot.getValue(Message.class);
                        messageList.add(message);
                        messageAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            proceed();
        }

    }

    /*
    *  SIDE NAVBAR METHODS
    *  ONCLICK EVENTS OF ITEMS ON SIDENAVBAR
    *
    */

    //method for setting texts in header in navigation view
    private void setCredentialView() {
        //get user information
        String name = currentUser.getDisplayName();
        String email = currentUser.getEmail();
        Uri photoUrl = currentUser.getPhotoUrl();

        //set text in header in navigation view
        userNameHandler.setText(name);
        emailHandler.setText(email);

        //Picasso turns photoUrl to bitmap
        //then changes the pic in header in navigation view
        Picasso.get().load(photoUrl).into(imgHandler);
    }

    //method for refreshing MapsActivity
    private void goToHome() {
        finish();
        Intent onPropertyView = new Intent(ChatMessage.this, MainActivity.class);
        startActivity(onPropertyView);
    }

    //method for starting PropertyActivity
    private void goToPropertyList() {
        finish();
        Intent onPropertyView = new Intent(ChatMessage.this, PropertyActivity.class);
        startActivity(onPropertyView);

    }
    private void goToRequests() {
        finish();
        Intent onPropertyView = new Intent(ChatMessage.this, SeekerRequestsActivity.class);
        startActivity(onPropertyView);
    }
    private void goToMessages() {
        finish();
        Intent onPropertyView = new Intent(ChatMessage.this, MessengerActivity.class);
        startActivity(onPropertyView);
    }

    private void  goToProfile(){
        finish();
        Intent onPropertyView = new Intent(ChatMessage.this, UserPanelActivity.class);
        startActivity(onPropertyView);
    }

    private void goToFavorite(){
        finish();
        Intent onPropertyView = new Intent(ChatMessage.this, FavoritesActivity.class);
        startActivity(onPropertyView);
    }

    //method for signing out current user
    //then going back to login panel
    private void signOutUser() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(ChatMessage.this, "You have been logout", Toast.LENGTH_SHORT).show();
        finishAffinity();
        proceed();
    }



     /*
    *  UTILITES
    *
    *
    */

    //method for going back to login panel
    private void proceed() {
        finish();
        Intent onReturnView = new Intent(ChatMessage.this, MainActivity.class);
        startActivity(onReturnView);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

}
