package com.example.sejeque.augrenta;

import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Faith on 20/03/2018.
 */

public class AddPropertyActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1 ;
    private Uri imageUri;
    private EditText propertyNameHandler, priceHandler, descriptionHandler, typeHandler,
                        areaHandler, roomsHandler, bathroomsHandler, petsHandler;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    private TextView userNameHandler, emailHandler;
    private ImageView imgHandler;

    Bundle oldBundle;

    private List<String> fileNameList;
    private List<Uri> fileUriList;

    private RecyclerView imgUploadList;

    private UploadListAdapter uploadListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_form);

        //Initiating auth for firebase
        mAuth = FirebaseAuth.getInstance();
        //get info about current user then store in currentUser
        currentUser = mAuth.getCurrentUser();
        // Setting Toolbar and Navigation Drawer

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Property");

        //function for Drawer toggle when clicking menu icon
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout1);
        NavigationView sideNavBar = findViewById(R.id.sideNav);
        //get reference for header in navigation view
        View headerView = sideNavBar.getHeaderView(0);

        //instantiate textView and imageView in header of navigation view
        userNameHandler = headerView.findViewById(R.id.textUser);
        emailHandler = headerView.findViewById(R.id.textEmail);
        imgHandler = headerView.findViewById(R.id.imageProfPic);

        //change texts for user's name, email, and profile pic in header of navigation view
        setCredentialView();

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
                }
                else if (itemID == R.id.favorite_properties){
                    goToFavorite();
                }
                else if(itemID == R.id.signOut){
                    signOutUser();
                }

                return true;
            }
        });
        // End of Setting Toolbar and Navigation Drawer

        Button launchMapbtn, submitBtn, cancelBtn, upload_imgBtn;

        propertyNameHandler = findViewById(R.id.editTextPropertyName);
        priceHandler = findViewById(R.id.editTextPrice);
        descriptionHandler = findViewById(R.id.editTextDescription);
        typeHandler = findViewById(R.id.editTextType);
        areaHandler = findViewById(R.id.editTextArea);
        roomsHandler = findViewById(R.id.editTextRooms);
        bathroomsHandler = findViewById(R.id.editTextBathrooms);
        petsHandler = findViewById(R.id.editTextPets);

        launchMapbtn = findViewById(R.id.btnGoToMap);
        submitBtn = findViewById(R.id.btnSubmit);
        cancelBtn = findViewById(R.id.btnGoBack);
        upload_imgBtn = findViewById(R.id.upload_imgbtn);

        imgUploadList = (RecyclerView) findViewById(R.id.imgRecHolder);

        fileNameList = new ArrayList<>();
        fileUriList = new ArrayList<>();

        uploadListAdapter = new UploadListAdapter(fileNameList);

        imgUploadList.setLayoutManager(new LinearLayoutManager(this));
        imgUploadList.setHasFixedSize(true);
        imgUploadList.setAdapter(uploadListAdapter);





        //get reference from database with child node Property
        mDatabase = FirebaseDatabase.getInstance().getReference("Property");

        storageReference = FirebaseStorage.getInstance().getReference("PropertyImages");

        //if no currentUser logged in
        if(currentUser == null){
            proceed();
        }

        //initiating Bundle passed from SelectLocationActivity
        //To prevent values already put in fields to be removed when Launch Map is clicked
        oldBundle = getIntent().getExtras();

        //if bundle is not empty, then user has already set location using Launch Map Button
        if(oldBundle != null){
            setOldFormInput();
        }



        launchMapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMap();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCredentials();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });


        //refer to onActivityResultMethod
        upload_imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent upload = new Intent();
                upload.setType("image/*");
                upload.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(upload, "Select Picture"), RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK ){

            if(data.getClipData() != null){
                int totalFileSelected = data.getClipData().getItemCount();

                for(int x = 0; x < totalFileSelected; x++){
                    Uri fileUri = data.getClipData().getItemAt(x).getUri();
                    String filename = getFilename(fileUri);

                    fileNameList.add(filename);
                    fileUriList.add(fileUri);
                }

            }else if(data.getData() != null){
                Uri fileUri = data.getData();
                String filename = getFilename(fileUri);

                fileNameList.add(filename);
                fileUriList.add(fileUri);
            }
        }
    }

    //method for populating fields if user came from SelectLocationActivity
    private void setOldFormInput() {
        //populate fields
        propertyNameHandler.setText(oldBundle.getString("property name"));
        priceHandler.setText(oldBundle.getString("property price"));
        descriptionHandler.setText(oldBundle.getString("property description"));
        typeHandler.setText(oldBundle.getString("property type"));
        areaHandler.setText(oldBundle.getString("property area"));
        roomsHandler.setText(oldBundle.getString("property rooms"));
        bathroomsHandler.setText(oldBundle.getString("property bathrooms"));
        petsHandler.setText(oldBundle.getString("property pets"));
    }

    //method for going back to MapsActivity without recreating the activity
    private void backToHome() {
        finish();
    }

    //method for saving property information to firebase database
    private void submitCredentials() {

        //this bundle is passed by SelectLocation Activity
        Bundle bundle = getIntent().getExtras();
        String REQUIRED = "Required";

        //instantiate property information
        String latVal = bundle.getString("latitudeValue");
        String longVal = bundle.getString("longitudeValue");
        final String propName = propertyNameHandler.getText().toString();
        String propPrice = priceHandler.getText().toString();
        String propDesc = descriptionHandler.getText().toString();
        String propType = typeHandler.getText().toString();
        String propArea = areaHandler.getText().toString();
        String propRooms = roomsHandler.getText().toString();
        String propBathrooms = bathroomsHandler.getText().toString();
        String propPets = petsHandler.getText().toString();

        //getUid() method gets unique user id given automatically by firebase auth
        String propOwner = currentUser.getUid();

        //if fields are empty, return warnings to fields
        if(TextUtils.isEmpty(propName)){
            propertyNameHandler.setError(REQUIRED);
            return;
        }
        else if(TextUtils.isEmpty(propPrice)){
            priceHandler.setError(REQUIRED);
            return;
        }
        else if(TextUtils.isEmpty(propDesc)){
            descriptionHandler.setError(REQUIRED);
            return;
        }
        else if(TextUtils.isEmpty(propType)){
            typeHandler.setError(REQUIRED);
            return;
        }
        else if(TextUtils.isEmpty(propArea)){
            areaHandler.setError(REQUIRED);
            return;
        }
        else if(TextUtils.isEmpty(propRooms)){
            roomsHandler.setError(REQUIRED);
            return;
        }
        else if(TextUtils.isEmpty(propBathrooms)){
            bathroomsHandler.setError(REQUIRED);
            return;
        }
        else if(TextUtils.isEmpty(propPets)){
            petsHandler.setError(REQUIRED);
            return;
        }

        //latVal && longVal came from SelectLocationActivty, so user has not yet chosen
        //location if these variables is empty
        //Toast a warning
        else if( latVal == null && longVal == null){
            Toast.makeText(AddPropertyActivity.this, "Select Location First", Toast.LENGTH_SHORT).show();
            return;
        }

        else if(fileUriList.size() == 0){
            Toast.makeText(AddPropertyActivity.this, "Select Image For Property First", Toast.LENGTH_SHORT).show();
            return;
        }

        //if all variables is not empty
        else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Saving Property..");
            progressDialog.show();
            progressDialog.setCancelable(false);

            //get unique id that will be given to a child node of Property
            final String key = mDatabase.push().getKey();
            final String deviceToken = FirebaseInstanceId.getInstance().getToken();
            String availability = "Available";
            String rating = "0";
            String profImage = fileNameList.get(0);

            final Map<String, Object> childUpdates = new HashMap<>();

            for(int x= 0; x<fileUriList.size(); x++){
                childUpdates.put("image" + x, fileNameList.get(x));
            }

            //Toast.makeText(this, "file "+ childUpdates, Toast.LENGTH_SHORT).show();
            //pass variable to model Property
            Log.d("Images", "file "+ childUpdates);
            Property property = new Property(propDesc, latVal, longVal, propOwner, propPrice, propName,
                                                key, propType, propArea, propRooms, propBathrooms, propPets, deviceToken, availability, rating, profImage);

            //save property object to firebase database
            mDatabase.child(key).setValue(property)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabase.child(key).child("images").setValue(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isComplete()){
                                        progressDialog.dismiss();
                                        Toast.makeText(AddPropertyActivity.this, "Property Saved", Toast.LENGTH_SHORT).show();
                                        finish();
                                        Intent onMapView = new Intent(AddPropertyActivity.this, MapsActivity.class);
                                        startActivity(onMapView);

                                    }

                                }
                            });

                            for (int x = 0; x<fileUriList.size(); x++) {
                                Uri imgUpload = fileUriList.get(x);
                                String filenameUpload = fileNameList.get(x);
                                storageReference.child(key).child(filenameUpload).putFile(imgUpload)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                progressDialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddPropertyActivity.this, "Property Image not Saved", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddPropertyActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //method for starting SelectLocationActivity
    private void launchMap() {

        //get values already put to fields before pressing Launch Map Button
        String propName = propertyNameHandler.getText().toString();
        String propPrice = priceHandler.getText().toString();
        String propDesc = descriptionHandler.getText().toString();
        String propType = typeHandler.getText().toString();
        String propArea = areaHandler.getText().toString();
        String propRooms = roomsHandler.getText().toString();
        String propBathrooms = bathroomsHandler.getText().toString();
        String propPets = petsHandler.getText().toString();

        //creating intent and bundle
        //that will be used to start SelectLocationActivity
        //and pass variables to SelectLocationActivity
        Intent i = new Intent(this, SelectLocationActivity.class);
        Bundle bundle = new Bundle();

        //put variable values to bundle
        bundle.putString("property name", propName);
        bundle.putString("property price", propPrice);
        bundle.putString("property description", propDesc);
        bundle.putString("property type", propType);
        bundle.putString("property area", propArea);
        bundle.putString("property rooms", propRooms);
        bundle.putString("property bathrooms", propBathrooms);
        bundle.putString("property pets", propPets);

        //put bundle to intent then starts SelectLocationActivity
        i.putExtras(bundle);
        finish();
        startActivity(i);
    }


    public String getFilename(Uri uri){
        String result = null;

        if(uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try{
                if(cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }
        if(result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1){
                result = result.substring(cut + 1);
            }
        }
        return result;
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
        Intent onPropertyView = new Intent(AddPropertyActivity.this, MainActivity.class);
        startActivity(onPropertyView);
    }
    private void  goToProfile(){
        finish();
        Intent onPropertyView = new Intent(AddPropertyActivity.this, UserPanelActivity.class);
        startActivity(onPropertyView);
    }

    //method for starting PropertyActivity
    private void goToPropertyList() {
        finish();
        Intent onPropertyView = new Intent(AddPropertyActivity.this, PropertyActivity.class);
        startActivity(onPropertyView);

    }
    private void goToRequests() {
        finish();
        Intent onPropertyView = new Intent(AddPropertyActivity.this, SeekerRequestsActivity.class);
        startActivity(onPropertyView);
    }
    private void goToMessages() {
        finish();
        Intent onPropertyView = new Intent(AddPropertyActivity.this, MessengerActivity.class);
        startActivity(onPropertyView);
    }

    private void goToFavorite(){
        finish();
        Intent onPropertyView = new Intent(AddPropertyActivity.this, FavoritesActivity.class);
        startActivity(onPropertyView);
    }

    //method for signing out current user
    //then going back to login panel
    private void signOutUser() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(AddPropertyActivity.this, "You have been logout", Toast.LENGTH_SHORT).show();
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
        Intent onReturnView = new Intent(AddPropertyActivity.this, MainActivity.class);
        startActivity(onReturnView);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
