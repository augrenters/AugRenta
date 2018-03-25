package com.example.sejeque.augrenta;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private BottomNavigationView bottomNav;

    private FirebaseAuth mAuth;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            proceed();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        bottomNav = findViewById(R.id.navigation);
//        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                selectFragment(item);
//                return true;
//            }
//        });


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    // @Override


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            proceed();
        }
    }

    private void proceed() {
        Intent onReturnView = new Intent(MapsActivity.this, MainActivity.class);
        startActivity(onReturnView);
    }

    LatLng latLng;

    // calling map
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        gotoLocationZoom(12.32, 122.53, (float) 5.80);

        /// if map is clicked
        if (mMap != null){
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    Toast.makeText(MapsActivity.this, "You clicked here" + latLng, Toast.LENGTH_SHORT).show();

                    Geocoder gc = new Geocoder(MapsActivity.this);

                    double lat = latLng.latitude;
                    double lng = latLng.longitude;
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address add = list.get(0);
                    String locality = add.getLocality();

                    setMarker(locality, lat, lng);
                }
            });
        }


        FloatingActionButton default_zoom = findViewById(R.id.default_zoom);
        FloatingActionButton user_location = findViewById(R.id.user_location);

        // go to location of the user when gps is turned on
        user_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(MapsActivity.this)
                        .addOnConnectionFailedListener(MapsActivity.this)
                        .build();
                mGoogleApiClient.connect();
            }
        });

        //clicking default zoom of the map
        default_zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Default zoom
                gotoLocationZoom(12.32, 122.53, (float) 5.80);
            }
        });




    }

    // zoom on map
    private void gotoLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.animateCamera(update);
    }

    //using search bar for location
    // creating marker
    Marker marker;

    public void geoLocate(View view) throws IOException {
        final EditText searchPlace = findViewById(R.id.searchPlace);


        String location = searchPlace.getText().toString();

        Geocoder geoLocator = new Geocoder(this);
        List<Address> listPlace = geoLocator
                .getFromLocationName(location, 1);
        Address address = listPlace.get(0);
        String locality = address.getLocality();

        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
        double lat = address.getLatitude();
        double lng = address.getLongitude();
        gotoLocationZoom(lat, lng, 15);

        Toast.makeText(this, "Lat: " + lat + "& Long " + lng, Toast.LENGTH_SHORT).show();


        // adding Marker
        setMarker(locality, lat, lng);

    }

    private void setMarker(String locality, double lat, double lng) {
        if(marker !=null){
            marker.remove();
        }
        MarkerOptions options = new MarkerOptions()
                                .title(locality)
                                .position(new LatLng(lat, lng))
                                .snippet("Here");
        marker = mMap.addMarker(options);
    }

    LocationRequest mLocationRequest;
    private Boolean requestPermissionGranted = false;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // mLocationRequest.setInterval(1000); - can be used when tracking the house

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            requestPermissionGranted =  true;

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            requestPermissionGranted = false;

            switch(requestCode){
                case 1234:{
                    if (grantResults.length > 0){
                        for(int i = 0; i< grantResults.length; i++){
                            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
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
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(this, ""+ connectionResult, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this, "Location" + location, Toast.LENGTH_SHORT).show();

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        if (location == null) {
            // doesn't work if gps is unabled
            Toast.makeText(this, "Can't get current location", Toast.LENGTH_SHORT).show();
        }else{
            LatLng ll = new LatLng(lat, lng);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            mMap.animateCamera(update);



            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            List<Address> addresses  = null;
            try {
                addresses = geocoder.getFromLocation(lat,lng, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String locality = addresses.get(0).getLocality();
            setMarker(locality, lat, lng);
        }
    }


    private void selectFragment(MenuItem item) {
        Fragment frag = null;
        // init corresponding fragment
        switch (item.getItemId()) {
            case R.id.navigation_person:
                Intent onUserView = new Intent(MapsActivity.this, UserPanelActivity.class);
                startActivity(onUserView);
        }
    }
}
