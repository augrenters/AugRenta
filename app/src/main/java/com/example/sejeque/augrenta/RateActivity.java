package com.example.sejeque.augrenta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by SejeQue on 5/19/2018.
 */

public class RateActivity extends AppCompatActivity{

    private DatabaseReference ratingDatabse;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ratingDatabse = FirebaseDatabase.getInstance().getReference().child("Ratings");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        final String user_id = currentUser.getUid();
        final String propertyId = getIntent().getExtras().getString("propertyId");


        AlertDialog levelDialog;
        View v = getLayoutInflater().inflate(R.layout.ratingpropertybar, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate the Property");
        builder.setCancelable(false);
        builder.setView(v);

        final RatingBar ratingBar = v.findViewById(R.id.ratingBar);

        builder.setPositiveButton("Submit Rate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(RateActivity.this, ""+ String.valueOf(ratingBar.getRating()), Toast.LENGTH_SHORT).show();
                String rating = String.valueOf(ratingBar.getRating());

                ratingDatabse.child(propertyId).child(user_id).child("rate").setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RateActivity.this, "Rating successfully submitted", Toast.LENGTH_SHORT).show();
                            Log.d("Rating", "Rating successfully submitted");

                            Intent rateIntent = new Intent(RateActivity.this, Main2Activity.class);
                            rateIntent.putExtra("propertyId", propertyId);
                            startActivity(rateIntent);
                        }
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent rateIntent = new Intent(RateActivity.this, Main2Activity.class);
                rateIntent.putExtra("propertyId", propertyId);
                startActivity(rateIntent);
            }
        });

        levelDialog = builder.create();
        levelDialog.show();
    }
}
