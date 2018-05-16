package com.example.sejeque.augrenta;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SejeQue on 4/24/2018.
 */

public class MessengerActivity extends AppCompatActivity {

    //initiate database reference
    private DatabaseReference mDatabase, propDatabase, usersDatabase;

    //instantiate firebase auth and user
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    private TextView userNameHandler, emailHandler;
    private ImageView imgHandler;

    final List<Property> properties = new ArrayList<>();

    ListView propertyListHandler;
    //final ArrayList<String> message_user =new ArrayList<>();
    List<HashMap<String, String>> message_user;
    List<HashMap<String, String>> user_name;

    SimpleAdapter adapter;

    String prop_ID, sender = null, prop_name, senderName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messenger_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");

        //get user information
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

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
                else if(itemID == R.id.profile){
                    goToProfile();
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

        mDatabase = FirebaseDatabase.getInstance().getReference("Messages");
        propDatabase = FirebaseDatabase.getInstance().getReference("Property");
        usersDatabase = FirebaseDatabase.getInstance().getReference("User");
        message_user = new ArrayList<>();
        user_name = new ArrayList<>();


    }

    private void populatePropertyList() {

        message_user.clear();



        mDatabase.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userMessages : dataSnapshot.getChildren()) {
                    Log.d("Users Sender Key", userMessages.getKey());
                    sender = userMessages.getKey();
                    mAuth.getUid().equals(sender);
                    for (DataSnapshot prop_id : userMessages.getChildren()) {
                        prop_ID = prop_id.getKey();
                        Log.d("Users Messages/property", prop_ID);

                        final HashMap<String, String> resultMap = new HashMap<>();
                        String nameTemp = "";
                        for (int x = 0; x < properties.size(); x++) {
                            if (properties.get(x).propertyID.equals(prop_ID)) {
                                //Toast.makeText(MessengerActivity.this, "again "+ user_name, Toast.LENGTH_SHORT).show();
                                for(int y = 0; y <user_name.size(); y++){
                                    ///Toast.makeText(MessengerActivity.this, "here "+user_name.get(y).get("SenderId"), Toast.LENGTH_SHORT).show();
                                    if(user_name.get(y).get("SenderId").equals(sender)){
                                        nameTemp = user_name.get(y).get("SenderName");
                                        resultMap.put("Sender", nameTemp);
                                    }
                                }
                                prop_name = properties.get(x).propertyName;

                                resultMap.put("SenderId", sender);
                                resultMap.put("Property ID", prop_ID);
                                resultMap.put("Prop_name", prop_name);
                                resultMap.put("Image", prop_name);
                                message_user.add(resultMap);
                                Log.d("Found", ""+message_user);
                            } else {
                                Log.d("No name found", "");
                            }
                        }
                    }
                }
                populateMessagesList();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(final DataSnapshot userSender: dataSnapshot.getChildren()){

                    usersDatabase.child(userSender.getKey()).child("username").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final HashMap<String, String> usersResult = new HashMap<>();
                            usersResult.put("SenderId", userSender.getKey());
                            usersResult.put("SenderName", dataSnapshot.getValue().toString());
                            user_name.add(usersResult);
                            //Toast.makeText(MessengerActivity.this, ""+user_name, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        propDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                properties.clear();
                for (DataSnapshot propertyNames : dataSnapshot.getChildren()){
                    Property property = propertyNames.getValue(Property.class);
                    properties.add(property);
                }

                populatePropertyList();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });



    }


    private void populateMessagesList() {


        propertyListHandler = findViewById(R.id.listProperty);

        View view = getLayoutInflater().inflate(R.layout.list_item, null);
        ImageView availImg = view.findViewById(R.id.imageViewAvail);
        availImg.setVisibility(View.GONE);

        adapter = new SimpleAdapter(this, message_user, R.layout.list_item, new String[]{"Image", "Sender", "Prop_name"}, new int[]{R.id.imageViewAvail,R.id.textUploadItem, R.id.textSubItem});
        propertyListHandler.setAdapter(adapter);

        Log.d("Message Adapater", message_user.toString());

        propertyListHandler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent showInfo = new Intent(MessengerActivity.this , ChatMessage.class);
                showInfo.putExtra("propertyId", message_user.get(i).get("Property ID"));
                showInfo.putExtra("ownerId", message_user.get(i).get("SenderId"));
                startActivity(showInfo);
            }
        });

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
        Intent onPropertyView = new Intent(MessengerActivity.this, MainActivity.class);
        startActivity(onPropertyView);
    }
    private void  goToProfile(){
        finish();
        Intent onPropertyView = new Intent(MessengerActivity.this, UserPanelActivity.class);
        startActivity(onPropertyView);
    }
    //method for starting PropertyActivity
    private void goToPropertyList() {
        finish();
        Intent onPropertyView = new Intent(MessengerActivity.this, PropertyActivity.class);
        startActivity(onPropertyView);

    }
    private void goToRequests() {
        finish();
        Intent onPropertyView = new Intent(MessengerActivity.this, MessengerActivity.class);
        startActivity(onPropertyView);
    }
    private void goToMessages() {
        finish();
        startActivity(getIntent());
    }

    private void goToFavorite(){
        finish();
        Intent onPropertyView = new Intent(MessengerActivity.this, FavoritesActivity.class);
        startActivity(onPropertyView);
    }

    //method for signing out current user
    //then going back to login panel
    private void signOutUser() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(MessengerActivity.this, "You have been logout", Toast.LENGTH_SHORT).show();
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
        Intent onReturnView = new Intent(MessengerActivity.this, MainActivity.class);
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
