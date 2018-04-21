package com.example.sejeque.augrenta;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_list);

        // Setting Toolbar and Navigation Drawer

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Properties");


        //function for Drawer toggle when clicking menu icon
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout1);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.oper_drawer, R.string.close_drawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        // End of Setting Toolbar and Navigation Drawer


        //get reference from firebase database with child node Property
        mDatabase = FirebaseDatabase.getInstance().getReference("Property");

        //get user information
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //instantiate array container for fetched data
        //from firebase database
        properties = new ArrayList<>();

        Button addPropertyBtn = findViewById(R.id.btnAddProperty);

        propertyListHandler = findViewById(R.id.listProperty);

        //instantiate elements for listView ui
        listItem = new ArrayList<>();
        sAdapter = new SimpleAdapter(this, listItem, R.layout.list_item, new String[]{"Item", "SubItem"}, new int[]{R.id.textItem, R.id.textSubItem});

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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
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


        for(int x = 0; x<properties.size(); x++) {

            if(properties.get(x).owner.equals(ownerID)){

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

                Intent showInfo = new Intent(PropertyActivity.this , Main2Activity.class);
                showInfo.putExtra("propertyId", listItem.get(i).get("Property ID"));
                //showInfo.putExtra("ownerId", listItem.get(i).get("Property Owner"));
                startActivity(showInfo);
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
