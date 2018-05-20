package com.example.sejeque.augrenta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by SejeQue on 5/19/2018.
 */

public class LocationAccess extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    String propertyId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        propertyId = getIntent().getExtras().getString("propertyId");


        AlertDialog levelDialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Access");
        builder.setMessage("Would like to allow house seeker to access your location to view the property?");
        builder.setCancelable(false);

        builder.setPositiveButton("ALLOW ACCESS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //DO YOUR CODE HERE IF PROPERTY OWNER ACCEPTS
            }
        });

        builder.setNegativeButton("DENY ACCESS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //DO YOUR CODE HERE IF PROPERTY OWNER ACCEPTS
            }
        });

        levelDialog = builder.create();
        levelDialog.show();
    }
}
