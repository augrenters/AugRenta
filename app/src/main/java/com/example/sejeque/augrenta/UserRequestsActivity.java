package com.example.sejeque.augrenta;

import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
 * Created by SejeQue on 5/16/2018.
 */


public class UserRequestsActivity extends AppCompatActivity {

    private DatabaseReference userReqDatabase;

    //instantiate firebase auth and user
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    private TextView userNameHandler, emailHandler;
    private ImageView imgHandler;

    //final List<Property> properties = new ArrayList<>();
    List<HashMap<String, String>> userReqList = new ArrayList<>();

    ListView userReqListHandler;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sender_requests);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Requests");

        //get user information
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        drawerLayout = findViewById(R.id.drawerLayout1);
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
                else if(itemID == R.id.signOut){
                    signOutUser();
                }
                return true;
            }
        });

        userReqDatabase = FirebaseDatabase.getInstance().getReference("Requests");
    }

    @Override
    protected void onStart() {
        super.onStart();

        userReqList.clear();

        userReqDatabase.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot propOwner : dataSnapshot.getChildren()){
                    //propOwner.getKey()
                    for(DataSnapshot propId : propOwner.getChildren()){
                        //propId.getKey()
                        //Toast.makeText(UserRequestsActivity.this, ""+propId.getValue(), Toast.LENGTH_SHORT).show();
                        RequestVisit userReq = propId.getValue(RequestVisit.class);

                        if (userReq.getType() != null && userReq.getType().equals("sender")){
                            HashMap<String, String> resultMap = new HashMap<>();
                            resultMap.put("PropName", userReq.getPropertyName());
//                            resultMap.put("Image", propOwner.getKey());
                            resultMap.put("Property ID", propId.getKey());
                            resultMap.put("OwnerId", propOwner.getKey());
                            resultMap.put("Image", null);
                            if (!userReq.isAccepted()){
                                resultMap.put("isAccepted", "Waiting for Property Owner's response");
                            }else{
                                resultMap.put("isAccepted", "Request has accepted");
                            }
                            userReqList.add(resultMap);
                        }else{
                            Log.d("User Request Data", "No data found");
                        }
                    }
                }
                populateUserRequestList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    public void populateUserRequestList(){
        //Toast.makeText(this, ""+userReqList, Toast.LENGTH_SHORT).show();

            userReqListHandler = findViewById(R.id.listUsersRequest);
            adapter = new SimpleAdapter(this, userReqList, R.layout.list_item, new String[]{"Image", "PropName", "isAccepted"}, new int[]{R.id.imageViewAvail,R.id.textUploadItem, R.id.textSubItem});
            userReqListHandler.setAdapter(adapter);


        userReqListHandler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(UserRequestsActivity.this, "Property ID" + userReqList.get(i).get("Property ID"), Toast.LENGTH_SHORT).show();
                //Toast.makeText(UserRequestsActivity.this, "Proper Owener" + userReqList.get(i).get("OwnerId"), Toast.LENGTH_SHORT).show();
                Intent showInfo = new Intent(UserRequestsActivity.this , Main2Activity.class);
                showInfo.putExtra("propertyId", userReqList.get(i).get("Property ID"));
                showInfo.putExtra("ownerId", userReqList.get(i).get("OwnerId"));
                startActivity(showInfo);
            }
        });
    }



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
        Intent onPropertyView = new Intent(UserRequestsActivity.this, MainActivity.class);
        startActivity(onPropertyView);
    }
    private void  goToProfile(){
        finish();
        Intent onPropertyView = new Intent(UserRequestsActivity.this, UserPanelActivity.class);
        startActivity(onPropertyView);
    }
    //method for starting PropertyActivity
    private void goToPropertyList() {
        finish();
        Intent onPropertyView = new Intent(UserRequestsActivity.this, PropertyActivity.class);
        startActivity(onPropertyView);
    }

    private void goToRequests() {
        finish();
        Intent onPropertyView = new Intent(UserRequestsActivity.this, SeekerRequestsActivity.class);
        startActivity(onPropertyView);
    }
    private void goToMessages() {
        finish();
        Intent onPropertyView = new Intent(UserRequestsActivity.this, MessengerActivity.class);
        startActivity(onPropertyView);
    }

    //method for signing out current user
    //then going back to login panel
    private void signOutUser() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(UserRequestsActivity.this, "You have been logout", Toast.LENGTH_SHORT).show();
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
        Intent onReturnView = new Intent(UserRequestsActivity.this, MainActivity.class);
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
