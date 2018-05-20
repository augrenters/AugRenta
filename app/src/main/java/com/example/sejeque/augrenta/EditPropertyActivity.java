package com.example.sejeque.augrenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private EditText propertyNameHandler, priceHandler, descriptionHandler, typeHandler,
            areaHandler, roomsHandler, bathroomsHandler, petsHandler;

    Button saveEdit, removeProperty;

    Property property, edited_property;

    String distance;

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

        propertyNameHandler = findViewById(R.id.editTextPropertyName);
        priceHandler = findViewById(R.id.editTextPrice);
        descriptionHandler = findViewById(R.id.editTextDescription);
        typeHandler = findViewById(R.id.editTextType);
        areaHandler = findViewById(R.id.editTextArea);
        roomsHandler = findViewById(R.id.editTextRooms);
        bathroomsHandler = findViewById(R.id.editTextBathrooms);
        petsHandler = findViewById(R.id.editTextPets);

        saveEdit = findViewById(R.id.saveEdit);
        removeProperty = findViewById(R.id.removeProperty);

        mDatabase = FirebaseDatabase.getInstance().getReference("Property").child(propertyId);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    property = dataSnapshot.getValue(Property.class);
                    //Toast.makeText(EditPropertyActivity.this, ""+property.propertyName, Toast.LENGTH_SHORT).show();

                    propertyNameHandler.setText(property.propertyName);
                    priceHandler.setText(property.price);
                    descriptionHandler.setText(property.description);
                    areaHandler.setText(property.area);
                    typeHandler.setText(property.type);
                    roomsHandler.setText(property.rooms);
                    bathroomsHandler.setText(property.bathroom);
                    petsHandler.setText(property.pets);
                    distance = property.distance;
                }else{
                    proceed();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditPropertyActivity.this, ""+propertyNameHandler.getText().toString(), Toast.LENGTH_SHORT).show();
                String propDesc, latVal, longVal, propOwner, propPrice, propName,
                        key, propType, propArea, propRooms, propBathrooms, propPets, deviceToken, availability, rating, profImage;

                propName = propertyNameHandler.getText().toString();
                propPrice = priceHandler.getText().toString();
                propDesc = descriptionHandler.getText().toString();
                propArea = areaHandler.getText().toString();
                propType = typeHandler.getText().toString();
                propRooms = roomsHandler.getText().toString();
                propBathrooms = bathroomsHandler.getText().toString();
                propPets = petsHandler.getText().toString();

                latVal = property.latitude;
                longVal = property.longitude;
                propOwner = property.owner;
                key = property.propertyID;
                deviceToken = property.deviceToken;
                availability = property.availability;
                rating = property.rating;
                profImage = property.propertyImage;

                edited_property = new Property(propDesc, latVal, longVal, propOwner, propPrice, propName,
                        key, propType, propArea, propRooms, propBathrooms, propPets, deviceToken, availability, rating, profImage, distance);

                mDatabase.setValue(edited_property).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(EditPropertyActivity.this, "New Property Information Saved", Toast.LENGTH_SHORT).show();
                            Intent goBack = new Intent(EditPropertyActivity.this, Main2Activity.class);
                            goBack.putExtra("propertyId", propertyId);
                            startActivity(goBack);
                        }
                    }
                });
            }
        });

        removeProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(EditPropertyActivity.this, "Removed Property", Toast.LENGTH_SHORT).show();
                            Intent goBack = new Intent(EditPropertyActivity.this, MapsActivity.class);
                            startActivity(goBack);
                        }
                    }
                });
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
