package com.example.sejeque.augrenta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.sejeque.augrenta.R.mipmap.available;
import static com.example.sejeque.augrenta.R.mipmap.blue_marker;
import static com.example.sejeque.augrenta.R.mipmap.man;
import static com.example.sejeque.augrenta.R.mipmap.red_marker;
import static com.example.sejeque.augrenta.R.mipmap.yellow_marker;
import static com.google.firebase.auth.FirebaseAuth.*;
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GeoDataClient mGeoDataClient;
    AutocompleteFilter typeFilter;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference statusDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference locationDatabase;
    private DatabaseReference mUser;

    private TextView userNameHandler, emailHandler;
    private ImageView imgHandler;

    //Array container for fetched data from firebase database
    List<Property> properties;
    List<Property> filteredProperties;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;

    AutoCompleteTextView searchPlace;

    Marker marker1;

    String[] filterType, filterPets;
    String filterRooms, filterCR;
    String shit = "";
    EditText etRooms, etbathroom;

    String filterShowProp;
    int[] priceValueProgress = {0};

    RadioGroup filterPropBy;

    private boolean isDown = false;
    private Marker userMarker;
    private boolean isUserLocation;
    private LatLng userPosition;

    String propertyId;
    private DatabaseReference notificationRef;
    Double userLocation_lat, userLocation_long;

    String owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //instantiate firebase auth
        mAuth = getInstance();
        //retrieve user information and store to currentUser
        currentUser = mAuth.getCurrentUser();
        //get reference for firebase database with child node Property
        mDatabase = FirebaseDatabase.getInstance().getReference("Property");
        locationDatabase = FirebaseDatabase.getInstance().getReference("Location");
        statusDatabase = FirebaseDatabase.getInstance().getReference("UserStatus");
        notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");
        notificationRef.keepSynced(true);
        mUser = FirebaseDatabase.getInstance().getReference("User");

        //instantiate array container for fetched data from firebase database
        properties = new ArrayList<>();
        filteredProperties = new ArrayList<>();

        //if user is not logged in, go back to login panel
        if (currentUser == null) {
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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = findViewById(R.id.drawerLayout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.oper_drawer, R.string.close_drawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        filterPropBy = findViewById(R.id.radioGrpFilterProp);

        //if an item is clicked on navigation view
        sideNavBar.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();

                if (itemID == R.id.navigation_home) {
                    goToHome();
                } else if (itemID == R.id.profile) {
                    goToProfile();
                } else if (itemID == R.id.properties) {
                    goToPropertyList();
                } else if (itemID == R.id.requests) {
                    goToRequests();
                } else if (itemID == R.id.messenger) {
                    goToMessages();
                } else if (itemID == R.id.favorite_properties) {
                    goToFavorite();
                } else if (itemID == R.id.signOut) {
                    signOutUser();
                }

                return true;
            }
        });


//        AUTOCOMPLETE SEARCH VIEW
        mGeoDataClient = Places.getGeoDataClient(this, null);

        typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setCountry("PH")
                .build();
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, LAT_LNG_BOUNDS, typeFilter);
        searchPlace = findViewById(R.id.searchPlace);
        searchPlace.setOnItemClickListener(mAutoCompleteListener);
        searchPlace.setAdapter(placeAutocompleteAdapter);

        searchPlace.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN ||
                        keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }
                return false;
            }
        });

//        int selectedId= filterPropBy.getCheckedRadioButtonId();
//        final RadioButton radioSexButton = findViewById(selectedId);
//        final String filterShowProp = radioSexButton.getText().toString();

        final SeekBar seekbarFilterPrice = findViewById(R.id.seekBarPrice);
        final TextView seekbarPriceTextView = findViewById(R.id.textView2);
        final int[] priceValueProgress = {0};
        seekbarFilterPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                priceValueProgress[0] = i;
                seekbarPriceTextView.setText("Price - PHP" + priceValueProgress[0]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Spinner spinnerType = findViewById(R.id.spinnerType);
        ArrayAdapter spinnerTyperAdapter = ArrayAdapter.createFromResource(this, R.array.filterTypeArray, android.R.layout.simple_spinner_item);
        spinnerTyperAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterType = new String[]{"All"};

        spinnerType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filterType[0] = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner spinnerPets = findViewById(R.id.spinnerPets);
        ArrayAdapter spinnerPetsAdapter = ArrayAdapter.createFromResource(this, R.array.filter_pets, android.R.layout.simple_spinner_item);
        spinnerPetsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterPets = new String[]{"All"};


        spinnerPets.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filterPets[0] = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        etRooms = findViewById(R.id.etFilterRoom);
        etbathroom = findViewById(R.id.etFilterCr);

        setUpGClient();
    }


    private void checkUserStatus(){
        locationDatabase.child(propertyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(dataSnapshot.getValue().toString().equals("true")){
                        startTimer();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startTimer(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> notificationData = new HashMap<>();

                notificationData.put("fromName", currentUser.getDisplayName());
                notificationData.put("fromID", currentUser.getUid());
                notificationData.put("type", "receiver");
                notificationData.put("response", "rate");
                notificationData.put("propertyId", propertyId);

                notificationRef.child(currentUser.getUid()).push().setValue(notificationData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        {
                            if (task.isSuccessful()) {
                                Log.d("House Seeker", "is here");
                            }
                        }
                    }
                });
            }
        }, 30000);
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
        if (currentUser == null) {
            proceed();
        }
        //needed to retrieve data from firebase database
        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clear array container first to remove old data
                properties.clear();

                //fetching data from every child of Property in firebase database
                if (dataSnapshot.exists()) {
                    for (DataSnapshot propertySnapshot : dataSnapshot.getChildren()) {
                        Property property = propertySnapshot.getValue(Property.class);
                        //put fetched data from firebase database to array container
                        properties.add(property);
                    }

//                    put markers for each property on the map
                    addMarkers();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        checkUserStatus();
    }

    HashMap<Marker, String> resultMap = new HashMap<Marker, String>();
    Marker marker;
    //method for adding markers to map
    private void addMarkers() {
        //if no added property yet
        if (properties.size() == 0) {
            Toast.makeText(MapsActivity.this, "No Property Added Yet", Toast.LENGTH_SHORT).show();

        } else {
            mMap.clear();
            createUserMarker();
            //loop to every property saved to firebase database
            //that has been stored to array container
            for (int x = 0; x < properties.size(); x++) {
                Double lat = 0.0, longT = 0.0;
                //get latitude and longitude value from firebase database
                //that has been stored to array container
                String longS = properties.get(x).longitude, latS = properties.get(x).latitude;

                if (longS != null && latS != null) {
                    lat = Double.valueOf(latS);
                    longT = Double.valueOf(longS);
                }

                String avail = properties.get(x).availability;
                owner = properties.get(x).owner;
                String currentId = currentUser.getUid();

                BitmapDescriptor markerIcon = null;

                if (owner != null && owner.equals(currentId)) {
                    markerIcon = BitmapDescriptorFactory.fromResource(blue_marker);
                } else if (avail != null && avail.equals("Available")) {
                    markerIcon = BitmapDescriptorFactory.fromResource(available);
                } else if (avail != null && avail.equals("Not Available")) {
                    markerIcon = BitmapDescriptorFactory.fromResource(red_marker);
                }
//
//                BitmapDescriptor markerYellowImg = BitmapDescriptorFactory.fromResource(yellow_marker);

                //create marker
                LatLng markerPos = new LatLng(lat, longT);
                marker = mMap.addMarker(new MarkerOptions()
                        .position(markerPos)
                        .title(properties.get(x).propertyName)
                        .snippet("Price: "+properties.get(x).price + " PHP")
                        .icon(markerIcon));
                //getting property ID
                resultMap.put(marker, properties.get(x).propertyID);
                mMap.getUiSettings().setMapToolbarEnabled(false);

            }
        }
    }

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
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng markerll = marker.getPosition();
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(markerll, 17);
                mMap.animateCamera(update);

                if (marker == userMarker) {
                    showPropertyInfoDialog(null);
                } else {
                    showPropertyInfoDialog(resultMap.get(marker));
                }

                //Toast.makeText(MapsActivity.this, ""+resultMap.get(marker), Toast.LENGTH_SHORT).show();
            }
        });

        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.isMyLocationEnabled();
    }

    //method for zooming to user location
    private void setToUserLocation() {
        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(MapsActivity.this)
                .addOnConnectionFailedListener(MapsActivity.this)
                .build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        isUserLocation = true;
    }

    // zoom on map
    private void gotoLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.animateCamera(update);
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


            if(isUserLocation){
               userPosition = new LatLng(lat, lng);
               createUserMarker();
            }
        }
    }

    private void createUserMarker(){

        if(userMarker != null) {
            userMarker.remove();
        }

        if(userPosition != null){
            userMarker = mMap.addMarker(new MarkerOptions()
                    .position(userPosition)
                    .title("You Are Here")
                    .icon(BitmapDescriptorFactory.fromResource(man))
                    .zIndex(5)
                    );
            //userMarker.showInfoWindow();
            userMarker.hideInfoWindow();
            isUserLocation = false;

        }
            isUserLocation = false;
    }

    /*
    *  PROPERTY INFO DIALOG
    *
    */

    //// Dialog Box when clickick marker Info window to show Summary Detail of the Property
    private void showPropertyInfoDialog(String s) {

        final String prop_Id = s;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialoginfo_layout, null);

        final TextView name = view.findViewById(R.id.seekerRequestPlace1);
        final TextView price = view.findViewById(R.id.prop_price);
        final TextView description = view.findViewById(R.id.prop_desc);
        final TextView distanceToProp = view.findViewById(R.id.prop_distance);

        mDatabase.child(prop_Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Property property = dataSnapshot.getValue(Property.class);

                name.setText(property.propertyName);
                price.setText(property.price + " PHP");
                description.setText(property.description);

                Double lat = 0.0, longT = 0.0;
                String longS = property.longitude, latS = property.latitude;

                if (longS != null && latS != null) {
                    lat = Double.valueOf(latS);
                    longT = Double.valueOf(longS);
                }
                float distance = 0;
                if (userPosition != null){
//
//                    Location selected_location=new Location("locationA");
//                    selected_location.setLatitude((float) userPosition.latitude);
//                    selected_location.setLongitude((float) userPosition.longitude);
//                    Location near_locations=new Location("locationB");
//                    near_locations.setLatitude(lat);
//                    near_locations.setLongitude(longT);
//                    distance = selected_location.distanceTo(near_locations);
                    float [] dist = new float[1];
                    Location.distanceBetween(userPosition.latitude, userPosition.longitude, lat, longT, dist);
                    distanceToProp.append(": " +Math.round(dist[0]) + " m");
                }else{
                    distanceToProp.append(" Distance unknown");
                }

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



    /*
    *  SEARCH BAR
    *  SEARCH PLACE
    *  AUTOCOMPLETE SEARCH
    */

    //using search bar for location
    // creating marker
    public void geoLocate()  {
        String location = searchPlace.getText().toString() + " PH";
        Geocoder geoLocator = new Geocoder(this);
        List<Address> listPlace =  new ArrayList<>();

        try {
            listPlace = geoLocator.getFromLocationName(location, 1);
        }catch (IOException e){
            Log.d("Message", e.getMessage());
        }
        if (listPlace.size() > 0){
            Address address = listPlace.get(0);
            //String locality = address.getLocality();
            if(address.getCountryCode().equals("PH")){
                double lat = address.getLatitude();
                double lng = address.getLongitude();
                gotoLocationZoom(lat, lng, 17);
                //Toast.makeText(this, address.getCountryName(), Toast.LENGTH_LONG).show();
                addMarker(new LatLng(lat, lng));
            }else{
                Toast.makeText(this, "No location found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //ADAPTERS FOR AUTOMPLETE SEARCH
    private AdapterView.OnItemClickListener mAutoCompleteListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();
            //final CharSequence primaryText = item.getPrimaryText(null);

            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);
            Log.i("Place ID", "Called getPlaceById to get Place details for " + placeId);
        }
    };

    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
            try{
                PlaceBufferResponse places = task.getResult();
                // Get the Place object from the buffer.
                final Place place = places.get(0);
                addMarker(place.getLatLng());
                Log.i("Place Query", "Place details received: " + place.getName());
                places.release();
            }catch (RuntimeRemoteException e){
                Log.d("Place Query", "Place query did not complete.", e);
//                return;
            }
        }
    };

    private void addMarker(LatLng latlng){
        if(marker1!=null){marker1.remove();}
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, 17);
        mMap.animateCamera(update);
        marker1 = mMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title("Here!")
                .snippet("Here!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.setOnInfoWindowClickListener(null);
    }


    /*
    *  FILTER SEARCH
    *     FOR PROPERTIES
    *
    */


    // FILTER SEARCH OPTION ON TOOLBAR
    private void filterSearch() {

        final Animation animatioSlideDown, animationSlideUp;
        final ViewGroup hiddenPanel = findViewById(R.id.hiddenFilterSearch);
        animatioSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_slide);
        animationSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filter_hide);
        hiddenPanel.setVisibility(ViewGroup.VISIBLE);
        hiddenPanel.startAnimation(animatioSlideDown);


        Button btnCancelSearch = findViewById(R.id.btnCancelSearch);
        btnCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hiddenPanel.startAnimation(animationSlideUp);
                hiddenPanel.setVisibility(ViewGroup.GONE);
                isDown = false;
            }
        });


        Button btnFilterSearch = findViewById(R.id.filter_searchbtn);
      //  final String finalShit = shit;
        btnFilterSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                properties.clear();
//                resultMap.clear();
//                marker.remove();
                isDown = false;
                hiddenPanel.startAnimation(animationSlideUp);
                hiddenPanel.setVisibility(ViewGroup.GONE);

                filteredProperties.clear();

                String cr;

                int selectedId= filterPropBy.getCheckedRadioButtonId();
                RadioButton radioSexButton = findViewById(selectedId);
                filterShowProp = radioSexButton.getText().toString();

                filterCR = etbathroom.getText().toString();
                filterRooms = etRooms.getText().toString();

                for(int x = 0; x < properties.size(); x++){
                    filteredProperties.add(properties.get(x));
                }




                //if show all is chosen
                if(filterShowProp.equals("Show All")) {
                    //Toast.makeText(MapsActivity.this, "Showing All Properties", Toast.LENGTH_SHORT).show();
                    addMarkers();
                }else {
                   // Toast.makeText(MapsActivity.this, "Removing " + filterShowProp + " Properties", Toast.LENGTH_SHORT).show();
                    //filter for availability
                    for(int x = 0; x < filteredProperties.size(); x++){
                        if(!filteredProperties.get(x).availability.equals(filterShowProp)){
                           // Toast.makeText(MapsActivity.this, "Removing " + filteredProperties.get(x).propertyName, Toast.LENGTH_SHORT).show();
                            filteredProperties.remove(x);
                            x-=1;
                        }

                        //filter for price
                        if(priceValueProgress[0] != 0){
                          //  Toast.makeText(MapsActivity.this, "Removing Properties With Price Higher Than" + priceValueProgress[0], Toast.LENGTH_SHORT).show();
                            for(int y = 0; y < filteredProperties.size(); y++){
                                if(Integer.valueOf(filteredProperties.get(y).price) > priceValueProgress[0]){
                               //     Toast.makeText(MapsActivity.this, "Removing " + filteredProperties.get(y).propertyName, Toast.LENGTH_SHORT).show();
                                    filteredProperties.remove(y);
                                    y-=1;
                                }
                            }
                        }

                        //filter for type
                        if(!filterType[0].equals("All")){
                            for(int z = 0; z < filteredProperties.size(); z++){
                                if(!filteredProperties.get(z).type.equals(filterType[0])){
                                    filteredProperties.remove(z);
                                    z-=1;
                                }
                            }
                        }

                        //filter for pets
                        if(!filterPets[0].equals("All")){
                            for(int a = 0; a < filteredProperties.size(); a++){
                                if(!filteredProperties.get(a).pets.equals(filterPets[0])){
                                    filteredProperties.remove(a);
                                    a-=1;
                                }
                            }
                        }

                        //filter for rooms
                        if(!filterRooms.isEmpty()){
                            for(int b = 0; b < filteredProperties.size(); b++){
                                if(Integer.valueOf(filteredProperties.get(b).rooms) > Integer.valueOf(filterRooms)){
                                    filteredProperties.remove(b);
                                    b-=1;
                                }
                            }
                        }

                        //filter for cr
                        if(!filterCR.isEmpty()){
                            for(int c = 0; c < filteredProperties.size(); c++){
                                if(Integer.valueOf(filteredProperties.get(c).bathroom) > Integer.valueOf(filterCR)){
                                    filteredProperties.remove(c);
                                    c-=1;
                                }
                            }
                        }
                    }
                    addFilteredMarkers();
                }
            }
        });

    }

    private void addFilteredMarkers(){
        mMap.clear();
        createUserMarker();
        for(int x=0; x<filteredProperties.size(); x++){
            Double lat, longT;
            //get latitude and longitude value from firebase database
            //that has been stored to array container
            lat = Double.valueOf(filteredProperties.get(x).latitude);
            longT = Double.valueOf(filteredProperties.get(x).longitude);
            String avail = filteredProperties.get(x).availability;
            String owner = filteredProperties.get(x).owner;
            String currentId = currentUser.getUid();

            BitmapDescriptor markerIcon = null;

            if(owner.equals(currentId)){
                markerIcon = BitmapDescriptorFactory.fromResource(blue_marker);
            }
            else if(avail.equals(null)){
                markerIcon = BitmapDescriptorFactory.fromResource(available);
            }
            else if(avail.equals("Available")){
                markerIcon = BitmapDescriptorFactory.fromResource(available);
            }
            else if(avail.equals("Not Available")){
                markerIcon = BitmapDescriptorFactory.fromResource(red_marker);
            }
//
//                BitmapDescriptor markerYellowImg = BitmapDescriptorFactory.fromResource(yellow_marker);

            //create marker
            LatLng markerPos = new LatLng(lat, longT);
            marker = mMap.addMarker(new MarkerOptions()
                    .position(markerPos)
                    .title(filteredProperties.get(x).propertyName)
                    .snippet("Price: " + filteredProperties.get(x).price + " Php\n" )
                    .icon(markerIcon));
            //getting property ID
            resultMap.put(marker, filteredProperties.get(x).propertyID);
            mMap.getUiSettings().setMapToolbarEnabled(false);

        }
    }






    /*
    *  SIDE NAVBAR METHODS
    *  ONCLICK EVENTS OF ITEMS ON SIDENAVBAR
    *
    */

    //method for setting texts in header in navigation view
    private void setCredentialView() {
        //get user information
        String name = currentUser.getDisplayName();
        String email = currentUser.getEmail();
        Uri photoUrl = currentUser.getPhotoUrl();
        String userId = currentUser.getUid();
        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        User user = new User(userId, name, email, deviceToken);

        mUser.child(userId).setValue(user);

        //set text in header in navigation view
        userNameHandler.setText(name);
        emailHandler.setText(email);

        //Picasso turns photoUrl to bitmap
        //then changes the pic in header in navigation view
        Picasso.get().load(photoUrl).into(imgHandler);
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
    private void goToMessages() {
        finish();
        Intent onPropertyView = new Intent(MapsActivity.this, MessengerActivity.class);
        startActivity(onPropertyView);
    }

    private void  goToProfile(){
        finish();
        Intent onPropertyView = new Intent(MapsActivity.this, UserPanelActivity.class);
        startActivity(onPropertyView);
    }
    private void goToFavorite(){
        finish();
        Intent onPropertyView = new Intent(MapsActivity.this, FavoritesActivity.class);
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

    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Do whatever you need
        //You can display a message here
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MapsActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
    }



    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(MapsActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            getMyLocation();
        }

    }

    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MapsActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
//                    locationRequest.setInterval(3000);
//                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback() {


                        @Override
                        public void onResult(@NonNull Result result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(MapsActivity.this,
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(MapsActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the
                                    // settings so we won't show the dialog.
                                    //finish();
                                    break;

                            }
                        }

//                        @Override
//                        public void onResult(NonNull Result result) {
//
//                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        gotoLocationZoom(12.32, 122.53, (float) 5.80);
                        break;
                }
                break;
        }
    }


    /*
    *  UTILITES
    *
    *
    */

    //method for going back to login panel
    private void proceed() {
        finish();
        Intent onReturnView = new Intent(MapsActivity.this, MainActivity.class);
        startActivity(onReturnView);
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                if(isDown){
                    //don't do anything
                }else{
                    filterSearch();
                    isDown = true;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
