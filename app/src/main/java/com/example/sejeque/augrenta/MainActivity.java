package com.example.sejeque.augrenta;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnMap = (Button) findViewById(R.id.goToMapBtn);


        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleServicesAvailable();
                if (googleServicesAvailable()) {
                    //Toast.makeText(MainActivity.this, "Map is Ready", Toast.LENGTH_SHORT).show();
                    Intent onMapView = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(onMapView);
                }
            }
        });

    }

    //check if mobile device has Google Play Services
    public boolean googleServicesAvailable(){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if(api.isUserResolvableError(isAvailable)){
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        }else{
            Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;

    }


}
