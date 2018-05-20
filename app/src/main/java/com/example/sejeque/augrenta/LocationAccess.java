package com.example.sejeque.augrenta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by SejeQue on 5/19/2018.
 */

public class LocationAccess extends AppCompatActivity {

    private DatabaseReference mDatabase, rateDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    String propertyId, ownerId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        propertyId = getIntent().getExtras().getString("propertyId");
        ownerId = getIntent().getExtras().getString("ownerId");

        mDatabase = FirebaseDatabase.getInstance().getReference("Location");
        rateDatabase = FirebaseDatabase.getInstance().getReference("RatingTimer");

        AlertDialog levelDialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Access");
        builder.setMessage("Would like to allow house seeker to access your location to view the property?");
        builder.setCancelable(false);

        builder.setPositiveButton("ALLOW ACCESS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //DO YOUR CODE HERE IF PROPERTY OWNER ACCEPTS
                mDatabase.child(ownerId).child(currentUser.getUid()).child("accepted").setValue("true")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){


                                    HashMap<String, String> notificationData = new HashMap<>();

                                    notificationData.put("rate", "false");

                                    int timestamp = (Calendar.getInstance().getTime().getHours() * 60) + Calendar.getInstance().getTime().getMinutes();

                                    notificationData.put("DateTime", String.valueOf(timestamp));

                                    rateDatabase.child(ownerId).child(propertyId).setValue(notificationData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(LocationAccess.this, "You accepted to view your Location", Toast.LENGTH_SHORT).show();
                                                Intent newIntent = new Intent(LocationAccess.this, userTrackingActivity.class);
                                                startActivity(newIntent);
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        });

        builder.setNegativeButton("DENY ACCESS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //DO YOUR CODE HERE IF PROPERTY OWNER Declines
                mDatabase.child(propertyId).child(currentUser.getUid()).child("accepted").setValue("false")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LocationAccess.this, "You denied your Location", Toast.LENGTH_SHORT).show();
                                    Intent newIntent = new Intent(LocationAccess.this, Main2Activity.class);
                                    startActivity(newIntent);
                                }
                            }
                        });
            }
        });

        levelDialog = builder.create();
        levelDialog.show();
    }
}
