package com.example.sejeque.augrenta;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by SejeQue on 5/12/2018.
 */

public class FirebaseInstanceIDServiceApp extends FirebaseInstanceIdService {
    private  final static String TAG="FCM Token";
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }
    private void sendRegistrationToServer(String token){
        // TODO: Implement this method to send any registration to app's server.
    }
}
