package com.example.sejeque.augrenta;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.google.firebase.auth.FirebaseAuth.*;
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mUser;
    private StorageReference storageReference;

    private CallbackManager mCallbackManager;

    private TextView userNameHandler, emailHandler;
    private ImageView imgHandler;

    //Array container for fetched data from firebase database
    List<Property> properties;

    String prop_Id;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //instantiate firebase auth
        mAuth = getInstance();
        //retrieve user information and store to currentUser
        currentUser = mAuth.getCurrentUser();
        //get reference for firebase database with child node Property
        mUser = FirebaseDatabase.getInstance().getReference("User");
        mDatabase = FirebaseDatabase.getInstance().getReference("Property");
        storageReference = FirebaseStorage.getInstance().getReference("PropertyImages");

        //instantiate array container for fetched data from firebase database
        properties = new ArrayList<>();

        //if user is not logged in, go back to login panel
        if(currentUser == null){
            proceed();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        NavigationView sideNavBar = findViewById(R.id.sideNav);
        //get reference for header in navigation view
        View headerView = sideNavBar.getHeaderView(0);

        //instantiate textView and imageView in header of navigation view
        userNameHandler = headerView.findViewById(R.id.textUser);
        emailHandler = headerView.findViewById(R.id.textEmail);
        imgHandler = headerView.findViewById(R.id.imageProfPic);

        //change texts for user's name, email, and profile pic in header of navigation view
        setCredentialView();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setCustomView(R.layout.search_view);

        //final EditText search_place = (EditText) getActionBar().getCustomView().findViewById(R.id.searchPlace);

        //function for Drawer toggle when clicking menu icon
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.oper_drawer, R.string.close_drawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //if an item is clicked on navigation view
        sideNavBar.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();

               if(itemID == R.id.navigation_home){
                   goToHome();
               }

               else if(itemID == R.id.properties){
                   goToPropertyList();
               }
               else if(itemID == R.id.requests){
                   goToRequests();
               }

               else if(itemID == R.id.signOut){
                   signOutUser();
               }

               return true;
            }
        });
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
        //if user is not logged in, go back to login panel
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            proceed();
        }

        //needed to retrieve data from firebase database
        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clear array container first to remove old data
                properties.clear();

                //fetching data from every child of Property in firebase database
                for(DataSnapshot propertySnapshot: dataSnapshot.getChildren()){
                    Property property = propertySnapshot.getValue(Property.class);
                    //put fetched data from firebase database to array container
                    properties.add(property);
                }

                //put markers for each property on the map
                addMarkers();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //to enable Navigation Drawer
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    // show filter item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }
    // click event of filter
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        switch (item.getItemId()){
            case R.id.filter_menu:
                filterSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // FILTER SEARCH OPTION ON TOOLBAR
    private void filterSearch() {

        final Animation animatioSlideDown, animationSlideUp;
        final ViewGroup hiddenPanel = (ViewGroup)findViewById(R.id.hiddenFilterSearch);

        animatioSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_slide);
        animationSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filter_hide);

        Toast.makeText(this, "You clicked Filter Search", Toast.LENGTH_SHORT).show();
        hiddenPanel.setVisibility(ViewGroup.VISIBLE);
        hiddenPanel.startAnimation(animatioSlideDown);

        Button btnCancelSearch = (Button) findViewById(R.id.btnCancelSearch);
        btnCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hiddenPanel.startAnimation(animationSlideUp);
                hiddenPanel.setVisibility(ViewGroup.GONE);
            }
        });
    }


    //method for going back to login panel
    private void proceed() {
        finish();
        Intent onReturnView = new Intent(MapsActivity.this, MainActivity.class);
        startActivity(onReturnView);
    }

    //method for refreshing MapsActivity
    private void goToHome() {
        finish();
        startActivity(getIntent());
    }

    //method for starting PropertyActivity
    private void goToPropertyList() {
        finish();
        Intent onPropertyView = new Intent(MapsActivity.this, PropertyActivity.class);
        startActivity(onPropertyView);
    }

    private void goToRequests() {
        finish();
        Intent onPropertyView = new Intent(MapsActivity.this, SeekerRequestsActivity.class);
        startActivity(onPropertyView);
    }


    //method for signing out current user
    //then going back to login panel
    private void signOutUser() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(MapsActivity.this, "You have been logout", Toast.LENGTH_SHORT).show();
        finishAffinity();
        proceed();
    }

    //method for setting texts in header in navigation view
    private void setCredentialView() {
        //get user information
        String name = currentUser.getDisplayName();
        String email = currentUser.getEmail();
        Uri photoUrl = currentUser.getPhotoUrl();
        String userId = currentUser.getUid();

        User user = new User(userId, name, email);

        mUser.child(userId).setValue(user);

        //set text in header in navigation view
        userNameHandler.setText(name);
        emailHandler.setText(email);

        //Picasso turns photoUrl to bitmap
        //then changes the pic in header in navigation view
        Picasso.get().load(photoUrl).into(imgHandler);
    }

    HashMap<Marker, String> resultMap = new HashMap<Marker, String>();
    ArrayList listItem = new ArrayList<>();

    //method for adding markers to map
    private void addMarkers(){
        //if no added property yet
        if(properties.size()==0){
            Toast.makeText(MapsActivity.this, "No Property Added Yet", Toast.LENGTH_SHORT).show();
        }
        else {
            //loop to every property saved to firebase database
            //that has been stored to array container
            for(int x=0; x<properties.size(); x++){
                Double lat, longT;
                //get latitude and longitude value from firebase database
                //that has been stored to array container
                lat = Double.valueOf(properties.get(x).latitude);
                longT = Double.valueOf(properties.get(x).longitude);

                //create marker
                LatLng markerPos = new LatLng(lat, longT);
                Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(markerPos)
                                                .title(properties.get(x).propertyName)
                                                .snippet("Price: " + properties.get(x).price + " Php\n" ));
                //getting property ID
                resultMap.put(marker, properties.get(x).propertyID);



            }
        }
    }

    LatLng latLng;

    // calling map
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //when map is ready, zoom camera to user location
        setToUserLocation();


        FloatingActionButton default_zoom = findViewById(R.id.default_zoom);
        FloatingActionButton user_location = findViewById(R.id.user_location);

        // go to location of the user when gps is turned on
        user_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToUserLocation();
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

        //if a marker is pressed
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){

            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng markerll = marker.getPosition();
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(markerll, 17);
                mMap.animateCamera(update);

                showPropertyInfoDialog(resultMap.get(marker));
                //Toast.makeText(MapsActivity.this, ""+resultMap.get(marker), Toast.LENGTH_SHORT).show();


            }
        });

    }
    //// Dialog Box when clickick marker Info window to show Summary Detail of the Property
    private void showPropertyInfoDialog(String s) {

        final String prop_Id = s;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialoginfo_layout, null);

        final TextView name = (TextView) view.findViewById(R.id.seekerRequestPlace1);
        final TextView price = (TextView) view.findViewById(R.id.prop_price);
        final TextView description = (TextView) view.findViewById(R.id.prop_desc);
        final ImageView propImg =  (ImageView) view.findViewById(R.id.seekerRequestImage);

        mDatabase.child(prop_Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Property property = dataSnapshot.getValue(Property.class);

                name.setText(property.propertyName);
                price.setText(property.price + " PHP");
                description.setText(property.description);

                storageReference.child(property.propertyID + "/" + property.profileImage).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(propImg);
                            }
                        });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        alertDialog.setTitle("Property Detail");
        alertDialog.setView(view);

        alertDialog.setPositiveButton("View All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //passing value and directing to the Propery Information
                Intent showInfo = new Intent(MapsActivity.this , Main2Activity.class);
                showInfo.putExtra("propertyId", prop_Id);
                startActivity(showInfo);

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialogInfo = alertDialog.create();
        alertDialogInfo.show();
    }



    //method for zooming to user location
    private void setToUserLocation() {
        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(MapsActivity.this)
                .addOnConnectionFailedListener(MapsActivity.this)
                .build();
        mGoogleApiClient.connect();
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
        gotoLocationZoom(lat, lng, 17);

        Toast.makeText(this, "Lat: " + lat + "& Long " + lng, Toast.LENGTH_SHORT).show();
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
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
            mMap.animateCamera(update);



            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            List<Address> addresses  = null;
            try {
                addresses = geocoder.getFromLocation(lat,lng, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            String locality = addresses.get(0).getLocality();
//            setMarker(locality, lat, lng);
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
