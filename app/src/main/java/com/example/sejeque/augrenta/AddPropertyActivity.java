package com.example.sejeque.augrenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Faith on 20/03/2018.
 */

public class AddPropertyActivity extends AppCompatActivity {

    private EditText propertyNameHandler, priceHandler, descriptionHandler;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    Bundle oldBundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_form);

        Button launchMapbtn, submitBtn, cancelBtn;

        propertyNameHandler = findViewById(R.id.editTextPropertyName);
        priceHandler = findViewById(R.id.editTextPrice);
        descriptionHandler = findViewById(R.id.editTextDescription);

        launchMapbtn = findViewById(R.id.btnGoToMap);
        submitBtn = findViewById(R.id.btnSubmit);
        cancelBtn = findViewById(R.id.btnGoBack);

        //Initiating auth for firebase
        mAuth = FirebaseAuth.getInstance();

        //get info about current user then store in currentUser
        currentUser = mAuth.getCurrentUser();

        //get reference from database with child node Property
        mDatabase = FirebaseDatabase.getInstance().getReference("Property");

        //if no currentUser logged in
        if(currentUser == null){
            proceed();
        }

        //initiating Bundle passed from SelectLocationActivity
        //To prevent values already put in fields to be removed when Launch Map is clicked
        oldBundle = getIntent().getExtras();

        //if bundle is not empty, then user has already set location using Launch Map Button
        if(oldBundle != null){
            setOldFormInput();
        }

        launchMapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMap();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCredentials();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });
    }

    //method for populating fields if user came from SelectLocationActivity
    private void setOldFormInput() {
        //populate fields
        propertyNameHandler.setText(oldBundle.getString("property name"));
        priceHandler.setText(oldBundle.getString("property price"));
        descriptionHandler.setText(oldBundle.getString("property description"));
    }

    //method for going back to MapsActivity without recreating the activity
    private void backToHome() {
        finish();
    }

    //method for going back to login panel
    private void proceed() {
        finish();
        Intent onReturnView = new Intent(AddPropertyActivity.this, MainActivity.class);
        startActivity(onReturnView);
    }

    //method for saving property information to firebase database
    private void submitCredentials() {

        //this bundle is passed by SelectLocation Activity
        Bundle bundle = getIntent().getExtras();
        String REQUIRED = "Required";

        //instantiate property information
        String latVal = bundle.getString("latitudeValue");
        String longVal = bundle.getString("longitudeValue");
        String propName = propertyNameHandler.getText().toString();
        String propPrice = priceHandler.getText().toString();
        String propDesc = descriptionHandler.getText().toString();

        //getUid() method gets unique user id given automatically by firebase auth
        String propOwner = currentUser.getUid();

        //if fields are empty, return warnings to fields
        if(TextUtils.isEmpty(propName)){
            propertyNameHandler.setError(REQUIRED);
            return;
        }
        else if(TextUtils.isEmpty(propPrice)){
            priceHandler.setError(REQUIRED);
            return;
        }
        else if(TextUtils.isEmpty(propDesc)){
            descriptionHandler.setError(REQUIRED);
            return;
        }

        //latVal && longVal came from SelectLocationActivty, so user has not yet chosen
        //location if these variables is empty
        //Toast a warning
        else if( latVal == null && longVal == null){
            Toast.makeText(AddPropertyActivity.this, "Select Location First", Toast.LENGTH_SHORT).show();
            return;
        }

        //if all variables is not empty
        else {
            //get unique id that will be given to a child node of Property
            String key = mDatabase.push().getKey();

            //pass variable to model Property
            Property property = new Property(propDesc, latVal, longVal, propOwner, propPrice, propName);

            //save property object to firebase database
            mDatabase.child(key).setValue(property);
            Toast.makeText(AddPropertyActivity.this, "New Property Added!", Toast.LENGTH_SHORT).show();

            //return to MapsActivity
            finish();
            Intent onMapView = new Intent(AddPropertyActivity.this, MapsActivity.class);
            startActivity(onMapView);
        }
    }

    //method for starting SelectLocationActivity
    private void launchMap() {

        //get values already put to fields before pressing Launch Map Button
        String propName = propertyNameHandler.getText().toString();
        String propPrice = priceHandler.getText().toString();
        String propDesc = descriptionHandler.getText().toString();

        //creating intent and bundle
        //that will be used to start SelectLocationActivity
        //and pass variables to SelectLocationActivity
        Intent i = new Intent(this, SelectLocationActivity.class);
        Bundle bundle = new Bundle();

        //put variable values to bundle
        bundle.putString("property name", propName);
        bundle.putString("property price", propPrice);
        bundle.putString("property description", propDesc);

        //put bundle to intent then starts SelectLocationActivity
        i.putExtras(bundle);
        finish();
        startActivity(i);
    }
}
