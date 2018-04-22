package com.example.sejeque.augrenta;

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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    Bundle oldBundle;

    private List<String> fileNameList;
    private List<Uri> fileUriList;

    private RecyclerView imgUploadList;

    private UploadListAdapter uploadListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_form);

        // Setting Toolbar and Navigation Drawer

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Property");

                //function for Drawer toggle when clicking menu icon
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout1);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.oper_drawer, R.string.close_drawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

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

        imgUploadList = findViewById(R.id.imgRecHolder);

        fileNameList = new ArrayList<>();
        fileUriList = new ArrayList<>();

        uploadListAdapter = new UploadListAdapter(fileNameList);

        imgUploadList.setLayoutManager(new LinearLayoutManager(this));
        imgUploadList.setHasFixedSize(true);
        imgUploadList.setAdapter(uploadListAdapter);

        //Initiating auth for firebase
        mAuth = FirebaseAuth.getInstance();

        //get info about current user then store in currentUser
        currentUser = mAuth.getCurrentUser();

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

    //method for going back to login panel
    private void proceed() {
        finish();
        Intent onReturnView = new Intent(AddPropertyActivity.this, MainActivity.class);
        startActivity(onReturnView);
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

            //get unique id that will be given to a child node of Property
            final String key = mDatabase.push().getKey();

            //pass variable to model Property
            Property property = new Property(propDesc, latVal, longVal, propOwner, propPrice, propName,
                                                key, propType, propArea, propRooms, propBathrooms, propPets);

            //save property object to firebase database
            mDatabase.child(key).setValue(property)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            for (int x = 0; x<fileUriList.size(); x++){
                                Uri imgUpload = fileUriList.get(x);
                                String filenameUpload = fileNameList.get(x);
                                storageReference.child(key).child(filenameUpload).putFile(imgUpload)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddPropertyActivity.this, "Property Saved", Toast.LENGTH_SHORT).show();
                                        finish();
                                        Intent onMapView = new Intent(AddPropertyActivity.this, MapsActivity.class);
                                        startActivity(onMapView);
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

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
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
}
