package com.example.sejeque.augrenta;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.sejeque.augrenta.R.mipmap.available;
import static com.example.sejeque.augrenta.R.mipmap.blue_marker;
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
    private DatabaseReference mDatabase;
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
        mUser = FirebaseDatabase.getInstance().getReference("User");

        //instantiate array container for fetched data from firebase database
        properties = new ArrayList<>();
        filteredProperties = new ArrayList<>();

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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = findViewById(R.id.drawerLayout);

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
               else if(itemID == R.id.profile){
                   goToProfile();
               }
               else if(itemID == R.id.properties){
                   goToPropertyList();
               }
               else if(itemID == R.id.requests){
                   goToRequests();
               }
               else if(itemID == R.id.messenger){
                   goToMessages();
               }else if (itemID == R.id.favorite_properties){
                   goToFavorite();
               }
               else if(itemID == R.id.signOut){
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
                if(i == EditorInfo.IME_ACTION_SEARCH ||
                   i == EditorInfo.IME_ACTION_DONE ||
                   keyEvent.getAction() == KeyEvent.ACTION_DOWN ||
                   keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    geoLocate();
                }
                return false;
            }
        });

        filterPropBy = findViewById(R.id.radioGrpFilterProp);

        final SeekBar seekbarFilterPrice = findViewById(R.id.seekBarPrice);
        final TextView seekbarPriceTextView = findViewById(R.id.textView2);
        seekbarFilterPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                priceValueProgress[0] = i;
                seekbarPriceTextView.setText("Price - PHP" +priceValueProgress[0]);
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
                if (dataSnapshot.exists()){
                    for(DataSnapshot propertySnapshot: dataSnapshot.getChildren()){
                        Property property = propertySnapshot.getValue(Property.class);
                        //put fetched data from firebase database to array container
                        properties.add(property);
                    }

                    //put markers for each property on the map
                    addMarkers();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    HashMap<Marker, String> resultMap = new HashMap<Marker, String>();
    Marker marker;
    //method for adding markers to map
    private void addMarkers(){
        //if no added property yet
        if(properties.size()==0){
            Toast.makeText(MapsActivity.this, "No Property Added Yet", Toast.LENGTH_SHORT).show();

        }
        else {
            mMap.clear();
            //loop to every property saved to firebase database
            //that has been stored to array container
            for(int x=0; x<properties.size(); x++){
                Double lat = 0.0, longT = 0.0;
                //get latitude and longitude value from firebase database
                //that has been stored to array container
                String longS = properties.get(x).longitude, latS=properties.get(x).latitude;

                if(longS !=null && latS!=null){
                    lat = Double.valueOf(latS);
                    longT = Double.valueOf(longS);
                }

                String avail = properties.get(x).availability;
                String owner = properties.get(x).owner;
                String currentId = currentUser.getUid();

                BitmapDescriptor markerIcon = null;

                if(owner!= null && owner.equals(currentId)){
                    markerIcon = BitmapDescriptorFactory.fromResource(blue_marker);
                }
                else if(avail != null && avail.equals("Available")){
                    markerIcon = BitmapDescriptorFactory.fromResource(available);
                }
                else if(avail != null && avail.equals("Not Available")){
                    markerIcon = BitmapDescriptorFactory.fromResource(red_marker);
                }
//
//                BitmapDescriptor markerYellowImg = BitmapDescriptorFactory.fromResource(yellow_marker);

                //create marker
                LatLng markerPos = new LatLng(lat, longT);
                marker = mMap.addMarker(new MarkerOptions()
                                                .position(markerPos)
                                                .title(properties.get(x).propertyName)
                                                .snippet("Price: " + properties.get(x).price + " Php\n" )
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

        mMap.getUiSettings().setMapToolbarEnabled(false);
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
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){}

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
        }
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

        mDatabase.child(prop_Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Property property = dataSnapshot.getValue(Property.class);

                name.setText(property.propertyName);
                price.setText(property.price + " PHP");
                description.setText(property.description);
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
                    Toast.makeText(MapsActivity.this, "Showing All Properties", Toast.LENGTH_SHORT).show();
                    addMarkers();
                }else {
                    Toast.makeText(MapsActivity.this, "Removing " + filterShowProp + " Properties", Toast.LENGTH_SHORT).show();
                    //filter for availability
                    for(int x = 0; x < filteredProperties.size(); x++){
                        if(!filteredProperties.get(x).availability.equals(filterShowProp)){
                            Toast.makeText(MapsActivity.this, "Removing " + filteredProperties.get(x).propertyName, Toast.LENGTH_SHORT).show();
                            filteredProperties.remove(x);
                            x-=1;
                        }

                        //filter for price
                        if(priceValueProgress[0] != 0){
                            Toast.makeText(MapsActivity.this, "Removing Properties With Price Higher Than" + priceValueProgress[0], Toast.LENGTH_SHORT).show();
                            for(int y = 0; y < filteredProperties.size(); y++){
                                if(Integer.valueOf(filteredProperties.get(y).price) > priceValueProgress[0]){
                                    Toast.makeText(MapsActivity.this, "Removing " + filteredProperties.get(y).propertyName, Toast.LENGTH_SHORT).show();
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

//                Toast.makeText(MapsActivity.this, "Status: " + filterShowProp  + "\nPrice: " + priceValueProgress[0] + "\nType: " + filterType[0] + "\nPets? " + filterPets[0] + "\nNo. Rooms: " + filterRooms + "\nNo. CRs" + filterCR, Toast.LENGTH_LONG).show();


//                shit = "";
//                filterCR = etbathroom.getText().toString();
//                filterRooms = etRooms.getText().toString();
//
//                if(!filterType[0].equals("All")){
//                    shit = shit + "type_";
//                }
//                if(!filterPets[0].equals("All")){
//                    shit = shit + "pets_";
//                }
//                if(!filterRooms.isEmpty()){
//                    shit = shit + "rooms_";
//                }
//                if(!filterCR.isEmpty()){
//                    shit = shit + "bathroom";
//                }
//
//                if (shit != null && shit.length() > 0 && shit.charAt(shit.length() - 1) == '_') {
//                    shit = shit.substring(0, shit.length() - 1);
//                }
//
//                Log.d("Data Input for Filter", filterType[0] +", " + filterPets[0] + "," + filterRooms +", " + filterCR);
//                Log.d("Data Filter for Filter", shit);
//
//                mDatabase.orderByChild("bathroom").equalTo("1")
//                        .addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                for(DataSnapshot propertySnapshot: dataSnapshot.getChildren()){
//                                    Property property = dataSnapshot.getValue(Property.class);
//
//                                    Toast.makeText(getApplicationContext(), "" + propertySnapshot.getKey(), Toast.LENGTH_SHORT).show();
//                                    Log.d("Data Filter for Filter", ""+property);
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
            }
        });

    }

    private void addFilteredMarkers(){
        mMap.clear();
        for(int x=0; x<filteredProperties.size(); x++){
            Double lat, longT;
            //get latitude and longitude value from firebase database
            //that has been stored to array container
            lat = Double.valueOf(filteredProperties.get(x).latitude);
            longT = Double.valueOf(filteredProperties.get(x).longitude);
            String avail = properties.get(x).availability;
            String owner = properties.get(x).owner;
            String currentId = currentUser.getUid();

            BitmapDescriptor markerIcon = null;

            if(owner.equals(currentId)){
                markerIcon = BitmapDescriptorFactory.fromResource(blue_marker);
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
