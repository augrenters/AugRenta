package com.example.sejeque.augrenta;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

/**
 * Created by Faith on 20/05/2018.
 */

public class userTrackingActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Boolean requestPermissionGranted = false;
    private DatabaseReference userTrackingDB;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    Double userLat;
    Double userLng;
    String username;
    private Marker userMarker;
    private Marker ownerMarker;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_tracking);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        userTrackingDB = FirebaseDatabase.getInstance().getReference("UserTracking");

        //instantiate firebase auth
        mAuth = getInstance();
        //retrieve user information and store to currentUser
        currentUser = mAuth.getCurrentUser();

        userTrackingDB.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserTracking userTracking = dataSnapshot.getValue(UserTracking.class);
                userLat = Double.valueOf(userTracking.latitude);
                userLng = Double.valueOf(userTracking.longitude);
                username = userTracking.username;

                createUserMarker();
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createUserMarker() {
        LatLng userLatLng = new LatLng(userLat, userLng);

        if(userMarker != null) {
            userMarker.remove();
        }

        if(userLatLng != null){
            userMarker = mMap.addMarker(new MarkerOptions()
                    .position(userLatLng)
                    .title(username)
                    .zIndex(5));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        if (location == null) {
            // doesn't work if gps is unabled
            Toast.makeText(this, "Can't get current location", Toast.LENGTH_SHORT).show();
        }else{
            LatLng ll = new LatLng(lat, lng);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
            mMap.animateCamera(update);
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses  = null;
            try {
                addresses = geocoder.getFromLocation(lat,lng, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            createOwnerMarker(lat, lng);
        }
    }

    private void createOwnerMarker(Double lat, Double lng) {
        LatLng ownerLatLng = new LatLng(lat, lng);

        if(ownerMarker != null) {
            ownerMarker.remove();
        }

        if(ownerLatLng != null){
            ownerMarker = mMap.addMarker(new MarkerOptions()
                    .position(ownerLatLng)
                    .title("You Are Here")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.man))
                    .zIndex(5));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        requestPermissionGranted = false;

        switch (requestCode) {
            case 1234: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            requestPermissionGranted = false;
                            return;
                        }
                    }
                    requestPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // mLocationRequest.setInterval(1000); - can be used when tracking the house

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            requestPermissionGranted =  true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1234 );
            }else{
                requestPermissionGranted = true;
            }
        }else{
            ActivityCompat.requestPermissions( this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //when map is ready, zoom camera to user location
        setToUserLocation();

        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    private void setToUserLocation() {
        mGoogleApiClient = new GoogleApiClient.Builder(userTrackingActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(userTrackingActivity.this)
                .addOnConnectionFailedListener(userTrackingActivity.this)
                .build();
        mGoogleApiClient.connect();
    }
}
