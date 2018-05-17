package com.example.sejeque.augrenta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

/**
 * Created by Faith on 18/03/2018.
 */

public class UserPanelActivity extends AppCompatActivity {

    private ImageView user_image;
    TextView nameHandler;
    TextView emailHandler;

    private FirebaseAuth mAuth;

    private CallbackManager mCallbackManager;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();

        Button signout;
        Button addProperty, userRequestBtn;

        signout = findViewById(R.id.btnSignout);
        addProperty = findViewById(R.id.btnAddProperty);
        userRequestBtn = findViewById(R.id.buttonUserRequest);

        user_image = findViewById(R.id.profImage);
        nameHandler = findViewById(R.id.viewName);
        emailHandler = findViewById(R.id.viewEmail);

        currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            proceed();
        }
        else{
            setCredentialView();
        }

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        addProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAdd();
            }
        });
        userRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoYourRequest = new Intent(UserPanelActivity.this, UserRequestsActivity.class);
                startActivity(gotoYourRequest);
            }
        });
    }

    private void proceed() {
        Intent onReturnView = new Intent(UserPanelActivity.this, MainActivity.class);
        startActivity(onReturnView);
    }

    private void goToAdd() {
        Intent onAddView = new Intent(UserPanelActivity.this, AddPropertyActivity.class);
        startActivity(onAddView);
    }

    public void logout() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        proceed();
    }

    public void setCredentialView() {
        String name = currentUser.getDisplayName();
        String email = currentUser.getEmail();
        Uri photoUrl = currentUser.getPhotoUrl();

        // The user's ID, unique to the Firebase project. Do NOT use this value to
        // authenticate with your backend server, if you have one. Use
        // FirebaseUser.getToken() instead.

        String uid = currentUser.getUid();

        //Toast.makeText(UserPanelActivity.this, uid, Toast.LENGTH_LONG).show();


        nameHandler.setText(name);
        emailHandler.setText(email);
        Picasso.get().load(photoUrl).into(user_image);
    }

}
