package com.example.sejeque.augrenta;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Faith on 31/03/2018.
 */

public class SelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng;
    Marker marker;
    LocationRequest mLocationRequest;
    private Boolean requestPermissionGranted = false;

    Double latitudeVal, longitudeVal;

    //initiate variable that will be used
    //to get latitude and longitude of current location of user
    LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_location);

        Button selectLocationBtn, goBackBtn;

        selectLocationBtn = findViewById(R.id.btnSetLocation);
        goBackBtn = findViewById(R.id.btnGoBack);

        //instantiate variable that will be used
        //to get latitude and longitude of current location of user
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //if Select Location button is pressed
        selectLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation();
            }
        });

        //if Cancel button is pressed
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //method for going back to AddPropertyActivity
    //without restarting AddPropertyActivity
    private void goBack() {
        finish();
    }

    //method for passing latitude and longitude value to AddPropertyActivity
    private void setLocation() {

        //if latitude && longitude value is empty
        //which means user has not yet selected a location
        if(latitudeVal == null && longitudeVal == null){
            Toast.makeText(SelectLocationActivity.this, "Please Select Location First", Toast.LENGTH_SHORT).show();
        }
        else {
            //get data passed by AddPropertyActivity
            //data from text fields
            //to prevent removal of values in fields when starting AddPropertyActivity
            Bundle oldBundle = getIntent().getExtras();

            //create intent and bundle
            //to start AddPropertyActivity
            //and pass data to AddPropertyActivity
            Intent i = new Intent(this, AddPropertyActivity.class);
            Bundle bundle = new Bundle();

            //put values to bundle that will passed to AddPropertyActivity
            bundle.putString("latitudeValue", latitudeVal.toString());
            bundle.putString("longitudeValue", longitudeVal.toString());
            bundle.putString("property name", oldBundle.getString("property name"));
            bundle.putString("property price", oldBundle.getString("property price"));
            bundle.putString("property description", oldBundle.getString("property description"));
            bundle.putString("property type", oldBundle.getString("property type"));
            bundle.putString("property area", oldBundle.getString("property area"));
            bundle.putString("property rooms", oldBundle.getString("property rooms"));
            bundle.putString("property bathrooms", oldBundle.getString("property bathrooms"));
            bundle.putString("property pets", oldBundle.getString("property pets"));


            //put bundle to intent
            //then starts AddPropertyActivity
            i.putExtras(bundle);
            finish();
            startActivity(i);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            requestPermissionGranted = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            } else {
                requestPermissionGranted = true;
            }

        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);

        }

        //get latitude && longitude values of current location of user
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        Location lastLocation;

        try {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //if current location has been selected by user
            if(lastLocation != null){
                //set variables to current location's latitude && longitude value
                latitudeVal = lastLocation.getLatitude();
                longitudeVal = lastLocation.getLongitude();
            }
        }catch (SecurityException e){}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "" + connectionResult, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        String locality = null;

        if (location == null) {
            // doesn't work if gps is unabled
            Toast.makeText(this, "Can't get current location", Toast.LENGTH_SHORT).show();
        } else {
            LatLng ll = new LatLng(lat, lng);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            mMap.animateCamera(update);


            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(lat, lng, 1);
                locality = addresses.get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }
            setMarker(locality, lat, lng);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        gotoLocationZoom(12.32, 122.53, (float) 5.80);

        /// if map is clicked
        // if user chooses another location
        if (mMap != null) {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    String locality = null;

                    //Toast.makeText(SelectLocationActivity.this, "You clicked here" + latLng, Toast.LENGTH_SHORT).show();

                    Geocoder gc = new Geocoder(SelectLocationActivity.this);

                    double lat = latLng.latitude;
                    double lng = latLng.longitude;
                    List<Address> list = null;

                    try {
                        list = gc.getFromLocation(lat, lng, 1);
                        locality = list.get(0).getLocality();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    setMarker(locality, lat, lng);

                    //set variables to selected location's latitude && longitude values
                    latitudeVal = lat;
                    longitudeVal = lng;
                }
            });
        }

        FloatingActionButton default_zoom = findViewById(R.id.default_zoom);
        FloatingActionButton user_location = findViewById(R.id.user_location);

        // go to location of the user when gps is turned on
        user_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mGoogleApiClient = new GoogleApiClient.Builder(SelectLocationActivity.this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(SelectLocationActivity.this)
                        .addOnConnectionFailedListener(SelectLocationActivity.this)
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

        //Toast.makeText(this, "Lat: " + lat + "& Long " + lng, Toast.LENGTH_SHORT).show();


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

    private void gotoLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.animateCamera(update);
    }
}
