package com.example.sejeque.augrenta;

import android.app.Fragment;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.sejeque.augrenta.R.mipmap.blue_marker;

/**
 * Created by Faith on 30/03/2018.
 */

public class PropertyActivity extends AppCompatActivity {

    //this activity is for property lists

    //initiate database reference
    private DatabaseReference mDatabase;

    //array container for data fetched from firebase database
    List<Property> properties;

    //used for creating listView ui
    ListView propertyListHandler;
    SimpleAdapter sAdapter;

    //array container used for populating listView
    List<HashMap<String, String>> listItem;

    //instantiate firebase auth and user
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    private TextView userNameHandler, emailHandler;
    private ImageView imgHandler;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_list);

        //get user information
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //instantiate array container for fetched data
        //from firebase database

        // Setting Toolbar and Navigation Drawer

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Properties");


        //function for Drawer toggle when clicking menu icon
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


        //get reference from firebase database with child node Property
        mDatabase = FirebaseDatabase.getInstance().getReference("Property");

        properties = new ArrayList<>();

        Button addPropertyBtn = findViewById(R.id.btnAddProperty);

        propertyListHandler = findViewById(R.id.listProperty);

        //instantiate elements for listView ui
        listItem = new ArrayList<>();
        sAdapter = new SimpleAdapter(this, listItem, R.layout.list_item, new String[]{"Image", "Item", "SubItem"}, new int[]{R.id.imageViewAvail, R.id.textUploadItem, R.id.textSubItem});


        //if Add Property button is pressed
        addPropertyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddProperty();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //fetched data from firebase database
        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //remove old data from array container
                properties.clear();
                if (dataSnapshot.exists()){
                    //loop each property from firebase database
                    for(DataSnapshot propertySnapshot: dataSnapshot.getChildren()){
                        Property property = propertySnapshot.getValue(Property.class);
                        //insert newly fetched data from firebase database
                        //to array container
                        properties.add(property);
                    }
                    //populate listView ui
                    populateList();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    //method for starting AddPropertyActivity
    private void goToAddProperty() {
        finish();
        Intent onReturnView = new Intent(PropertyActivity.this, AddPropertyActivity.class);
        startActivity(onReturnView);
    }

    //method for populating listView ui
    private void populateList() {

        //remove old data
        listItem.clear();

        //instantiate all variables that will be used
        //to get address using latitude && longitude
        Geocoder geocoder;
        List<Address> addresses;
        String fullAddress = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        //loop each property fetched from firebase database
        //that is been saved to array container


        //get unique user id that is automatically generated by firebase
        String ownerID = currentUser.getUid();


            for (int x = 0; x < properties.size(); x++) {

                if ( properties.get(x).owner != null && properties.get(x).owner.equals(ownerID)) {

                    Double latVal, longVal;
                    latVal = Double.valueOf(properties.get(x).latitude);
                    longVal = Double.valueOf(properties.get(x).longitude);

                    //get address using latitude && longitude
                    try {
                        addresses = geocoder.getFromLocation(latVal, longVal, 1);
                        fullAddress = addresses.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //insert data to a temporary array container
                    //with property name as Item
                    //and address as SubItem
                    HashMap<String, String> resultMap = new HashMap<>();
                    resultMap.put("Item", properties.get(x).propertyName);
                    resultMap.put("SubItem", fullAddress);
                    resultMap.put("Property Owner", properties.get(x).owner);
                    resultMap.put("Property ID", properties.get(x).propertyID);

                    if (properties.get(x).availability.equals("Available")) {
                        resultMap.put("Image", Integer.toString(R.mipmap.available));
                    } else {
                        resultMap.put("Image", Integer.toString(R.mipmap.red_marker));
                    }

                    //insert temporary array container to listItem array container
                    //container that will be used to population listView ui
                    listItem.add(resultMap);
                }

            }

            //generate listView ui
            propertyListHandler.setAdapter(sAdapter);

            //Click event of list view to pass data and show Property's information
            propertyListHandler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(PropertyActivity.this, ""+ listItem.get(i).get("Property ID"), Toast.LENGTH_SHORT).show();

                    Intent showInfo = new Intent(PropertyActivity.this, Main2Activity.class);
                    showInfo.putExtra("propertyId", listItem.get(i).get("Property ID"));
                    //showInfo.putExtra("ownerId", listItem.get(i).get("Property Owner"));
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
        Intent onPropertyView = new Intent(PropertyActivity.this, MainActivity.class);
        startActivity(onPropertyView);
    }
    private void  goToProfile(){
        finish();
        Intent onPropertyView = new Intent(PropertyActivity.this, UserPanelActivity.class);
        startActivity(onPropertyView);
    }
    //method for starting PropertyActivity
    private void goToPropertyList() {
        finish();
        startActivity(getIntent());
    }

    private void goToRequests() {
        finish();
        Intent onPropertyView = new Intent(PropertyActivity.this, SeekerRequestsActivity.class);
        startActivity(onPropertyView);
    }
    private void goToMessages() {
        finish();
        Intent onPropertyView = new Intent(PropertyActivity.this, MessengerActivity.class);
        startActivity(onPropertyView);
    }

    private void goToFavorite() {
        finish();
        Intent onPropertyView = new Intent(PropertyActivity.this, FavoritesActivity.class);
        startActivity(onPropertyView);
    }

    //method for signing out current user
    //then going back to login panel
    private void signOutUser() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(PropertyActivity.this, "You have been logout", Toast.LENGTH_SHORT).show();
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
        Intent onReturnView = new Intent(PropertyActivity.this, MainActivity.class);
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
