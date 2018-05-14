package com.example.sejeque.augrenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.google.firebase.storage.StorageReference;

/**
 * Created by SejeQue on 5/14/2018.
 */

public class EditPropertyActivity extends AppCompatActivity{

    private DatabaseReference requestDatabase, notifDatabase;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    String ownerId, propertyId;

    TextView property_name, property_price, property_description,
            property_type, property_area, property_bedroom, property_bathroom, property_pet;

    Button saveEdit, removeProperty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_property);

        //get user information
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Property");

        propertyId = getIntent().getExtras().getString("propertyId");
        ownerId = getIntent().getExtras().getString("ownerId");

        property_name = findViewById(R.id.property_name);
        property_price = findViewById(R.id.property_price);
        property_description = findViewById(R.id.property_description);
        property_area = findViewById(R.id.property_area);
        property_type = findViewById(R.id.property_type);
        property_bedroom = findViewById(R.id.property_bedroom);
        property_bathroom = findViewById(R.id.property_bathroom);
        property_pet = findViewById(R.id.property_pets);

        saveEdit = findViewById(R.id.saveEdit);
        removeProperty = findViewById(R.id.removeProperty);

        mDatabase = FirebaseDatabase.getInstance().getReference("Property").child("propertyId");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(getApplicationContext(), " "+ dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }





     /*
    *  UTILITES
    *
    *
    */

    //method for going back to login panel
    private void proceed() {
        finish();
        Intent onReturnView = new Intent(EditPropertyActivity.this, MainActivity.class);
        startActivity(onReturnView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

}
