package com.example.sejeque.augrenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    String ownerId, propertyId;

    /// Database
    //initiate database reference
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    //array container for data fetched from firebase database
    List<Property> properties;

    TextView property_name, property_price, property_description,
            property_type, property_area, property_bedroom, property_bathroom, property_pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Requests");

        //function for Drawer toggle when clicking menu icon
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout1);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.oper_drawer, R.string.close_drawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //end of Drawer toggle function

        //get value from ProperyActivity to show Information
        //ownerId = getIntent().getExtras().getString("ownerId");
        propertyId = getIntent().getExtras().getString("propertyId");

        //get reference from firebase database with child node Property
        mDatabase = FirebaseDatabase.getInstance().getReference("Property");

        //get user information
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //TextView widgets initialisation
        property_name = findViewById(R.id.property_name);
        property_price = findViewById(R.id.property_price);
        property_description = findViewById(R.id.property_description);
        property_area = findViewById(R.id.property_area);
        property_type = findViewById(R.id.property_type);
        property_bedroom = findViewById(R.id.property_bedroom);
        property_bathroom = findViewById(R.id.property_bathroom);
        property_pet = findViewById(R.id.property_pets);


        Button request_visit = (Button) findViewById(R.id.request_visit);
        request_visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent starIntent = new Intent(Main2Activity.this, SeekerRequestsActivity.class);
                startActivity(starIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.child(propertyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
                //Toast.makeText(Main2Activity.this, ""+dataSnapshot, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showData(DataSnapshot dataSnapshot) {
        //for(DataSnapshot propertySnapshot : dataSnapshot.getChildren()){

            Property property = dataSnapshot.getValue(Property.class);
            //Toast.makeText(this, ""+property.propertyName, Toast.LENGTH_SHORT).show();

            property_name.setText(property.propertyName);
            property_price.setText(property.price + " PHP");
            property_description.setText(property.description);
            property_area.setText(property.area);
            property_type.setText(property.type);
            property_bedroom.setText(property.rooms);
            property_bathroom.setText(property.bathroom);
            property_pet.setText(property.pets);


        //}
    }

    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

}
